package tech.c3n7.OrdersService.saga;

import org.axonframework.commandhandling.CommandCallback;
import org.axonframework.commandhandling.CommandMessage;
import org.axonframework.commandhandling.CommandResultMessage;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.deadline.DeadlineManager;
import org.axonframework.deadline.annotation.DeadlineHandler;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.modelling.saga.EndSaga;
import org.axonframework.modelling.saga.SagaEventHandler;
import org.axonframework.modelling.saga.StartSaga;
import org.axonframework.queryhandling.QueryGateway;
import org.axonframework.spring.stereotype.Saga;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import tech.c3n7.OrdersService.command.commands.ApproveOrderCommand;
import tech.c3n7.OrdersService.command.commands.RejectOrderCommand;
import tech.c3n7.OrdersService.core.events.OrderApprovedEvent;
import tech.c3n7.OrdersService.core.events.OrderCreatedEvent;
import tech.c3n7.OrdersService.core.events.OrderRejectedEvent;
import tech.c3n7.estore.core.commands.CancelProductReservationCommand;
import tech.c3n7.estore.core.commands.ProcessPaymentCommand;
import tech.c3n7.estore.core.commands.ReserveProductCommand;
import tech.c3n7.estore.core.commands.events.PaymentProcessedEvent;
import tech.c3n7.estore.core.commands.events.ProductReservationCancelledEvent;
import tech.c3n7.estore.core.commands.events.ProductReservedEvent;
import tech.c3n7.estore.core.model.User;
import tech.c3n7.estore.core.query.FetchUserPaymentDetailsQuery;

import javax.annotation.Nonnull;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Saga
public class OrderSaga {
    private static final Logger LOGGER = LoggerFactory.getLogger(OrderSaga.class);
    private final String PAYMENT_PROCESSING_TIMEOUT_DEADLINE = "payment-processing-deadline";
    @Autowired
    private transient QueryGateway queryGateway;
    @Autowired
    private transient CommandGateway commandGateway;
    @Autowired
    private transient DeadlineManager deadlineManager;
    private String scheduleId;


    @StartSaga
    @SagaEventHandler(associationProperty = "orderId")
    public void handle(OrderCreatedEvent orderCreatedEvent) {
        ReserveProductCommand reserveProductCommand = ReserveProductCommand.builder()
                .orderId(orderCreatedEvent.getOrderId())
                .productId(orderCreatedEvent.getProductId())
                .quantity(orderCreatedEvent.getQuantity())
                .userId(orderCreatedEvent.getUserId())
                .build();

        LOGGER.info("OrderCreatedEvent handled for orderId: " + reserveProductCommand.getOrderId() +
                " and productId: " + reserveProductCommand.getProductId());

        commandGateway.send(reserveProductCommand, new CommandCallback<ReserveProductCommand, Object>() {
            @Override
            public void onResult(@Nonnull CommandMessage<? extends ReserveProductCommand> commandMessage, @Nonnull CommandResultMessage<?> commandResultMessage) {
                if (commandResultMessage.isExceptional()) {
                    // TODO:: Start compensating transaction
                    LOGGER.error(commandResultMessage.toString());
                    LOGGER.warn(commandMessage.toString());
                }
            }
        });
    }

    @SagaEventHandler(associationProperty = "orderId")
    public void handle(ProductReservedEvent productReservedEvent) {
        LOGGER.info("ProductReservedEvent handled for productId: " + productReservedEvent.getProductId() +
                " and orderId: " + productReservedEvent.getOrderId());

        FetchUserPaymentDetailsQuery fetchUserPaymentDetailsQuery = new FetchUserPaymentDetailsQuery(productReservedEvent.getUserId());

        User userPaymentDetails = null;
        try {
            userPaymentDetails = queryGateway.query(fetchUserPaymentDetailsQuery,
                    ResponseTypes.instanceOf(User.class)).join();
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage());
            // Start compensating transaction
            cancelProductReservation(productReservedEvent, ex.getMessage());
            return;
        }

        if (userPaymentDetails == null) {
            // Start compensating transaction
            cancelProductReservation(productReservedEvent, "Could not fetch user payment details");
            return;
        }

        scheduleId = deadlineManager.schedule(
                Duration.of(120, ChronoUnit.SECONDS),
                PAYMENT_PROCESSING_TIMEOUT_DEADLINE, productReservedEvent);

        // Trigger the deadline schedule
        // if (true) return;

        LOGGER.info("Successfully fetched user payment details for user " + userPaymentDetails.getFirstName());
        ProcessPaymentCommand processPaymentCommand = ProcessPaymentCommand.builder()
                .orderId(productReservedEvent.getOrderId())
                .paymentDetails(userPaymentDetails.getPaymentDetails())
                .paymentId(UUID.randomUUID().toString())
                .build();

        String result = null;
        try {
//            result = commandGateway.sendAndWait(processPaymentCommand, 10, TimeUnit.SECONDS);
            result = commandGateway.sendAndWait(processPaymentCommand, 10, TimeUnit.SECONDS);
        } catch (Exception ex) {
            // Start compensating transaction
            LOGGER.info("The ProcessPaymentCommand Failed. Initiating a compensating transaction:: " + ex.getMessage());
            cancelProductReservation(productReservedEvent, ex.getMessage());
            return;
        }

        if (result == null) {
            // The command timed out (the 10 seconds ran out)
            LOGGER.info("The ProcessPaymentCommand resulted in NULL. Initiating a compensating transaction");
            // Start compensating transaction
            cancelProductReservation(productReservedEvent, "Could not process user payment with provided details");
        }
    }

    private void cancelProductReservation(ProductReservedEvent productReservedEvent, String reason) {
        cancelDeadline();
        CancelProductReservationCommand cancelProductReservationCommand = CancelProductReservationCommand.builder()
                .orderId(productReservedEvent.getOrderId())
                .productId(productReservedEvent.getProductId())
                .quantity(productReservedEvent.getQuantity())
                .userId(productReservedEvent.getUserId())
                .reason(reason)
                .build();

        commandGateway.send(cancelProductReservationCommand);
    }

    @SagaEventHandler(associationProperty = "orderId")
    public void handle(PaymentProcessedEvent paymentProcessedEvent) {
        cancelDeadline();

        // Send an ApprovedOrder Command
        ApproveOrderCommand approveOrderCommand = new ApproveOrderCommand(paymentProcessedEvent.getOrderId());

        commandGateway.send(approveOrderCommand);
    }

    private void cancelDeadline() {
        if (scheduleId != null) {
            deadlineManager.cancelSchedule(PAYMENT_PROCESSING_TIMEOUT_DEADLINE, scheduleId);
            scheduleId = null;
        }
//        deadlineManager.cancelAll(PAYMENT_PROCESSING_TIMEOUT_DEADLINE);
    }

    @EndSaga
    @SagaEventHandler(associationProperty = "orderId")
    public void handle(OrderApprovedEvent orderApprovedEvent) {
        LOGGER.info("Order is approved, Order Saga is complete for orderId: " + orderApprovedEvent.getOrderId());
        // use @EndSaga annotation or the below
        // SagaLifecycle.end();
    }

    @SagaEventHandler(associationProperty = "orderId")
    public void handle(ProductReservationCancelledEvent productReservationCancelledEvent) {
        // Create and send a reject order command
        RejectOrderCommand rejectOrderCommand = new RejectOrderCommand(
                productReservationCancelledEvent.getOrderId(), productReservationCancelledEvent.getReason());

        commandGateway.send(rejectOrderCommand);
    }

    @EndSaga
    @SagaEventHandler(associationProperty = "orderId")
    public void handle(OrderRejectedEvent orderRejectedEvent) {
        LOGGER.info("Successfully rejected order with id " + orderRejectedEvent.getOrderId());
    }

    @DeadlineHandler(deadlineName = PAYMENT_PROCESSING_TIMEOUT_DEADLINE)
    public void handlePaymentDeadline(ProductReservedEvent productReservedEvent) {
        LOGGER.info("Payment processing deadline took place. Sending a compensating command to cancel the transaction");
        cancelProductReservation(productReservedEvent, "Payment Timeout");
    }
}
