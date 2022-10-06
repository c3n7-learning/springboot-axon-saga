package tech.c3n7.OrdersService.saga;

import org.axonframework.commandhandling.CommandCallback;
import org.axonframework.commandhandling.CommandMessage;
import org.axonframework.commandhandling.CommandResultMessage;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.modelling.saga.EndSaga;
import org.axonframework.modelling.saga.SagaEventHandler;
import org.axonframework.modelling.saga.SagaLifecycle;
import org.axonframework.modelling.saga.StartSaga;
import org.axonframework.queryhandling.QueryGateway;
import org.axonframework.spring.stereotype.Saga;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import tech.c3n7.OrdersService.command.commands.ApproveOrderCommand;
import tech.c3n7.OrdersService.core.events.OrderApprovedEvent;
import tech.c3n7.OrdersService.core.events.OrderCreatedEvent;
import tech.c3n7.estore.core.commands.ProcessPaymentCommand;
import tech.c3n7.estore.core.commands.ReserveProductCommand;
import tech.c3n7.estore.core.commands.events.PaymentProcessedEvent;
import tech.c3n7.estore.core.commands.events.ProductReservedEvent;
import tech.c3n7.estore.core.model.User;
import tech.c3n7.estore.core.query.FetchUserPaymentDetailsQuery;

import javax.annotation.Nonnull;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Saga
public class OrderSaga {
    private static final Logger LOGGER = LoggerFactory.getLogger(OrderSaga.class);
    @Autowired
    private transient QueryGateway queryGateway;
    @Autowired
    private transient CommandGateway commandGateway;


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
            return;
        }

        if(userPaymentDetails == null) {
            // Start compensating transaction
            return;
        }

        LOGGER.info("Successfully fetched user payment details for user " + userPaymentDetails.getFirstName());
        ProcessPaymentCommand processPaymentCommand = ProcessPaymentCommand.builder()
                .orderId(productReservedEvent.getOrderId())
                .paymentDetails(userPaymentDetails.getPaymentDetails())
                .paymentId(UUID.randomUUID().toString())
                .build();

        String result = null;
        try {
            result = commandGateway.sendAndWait(processPaymentCommand, 10, TimeUnit.SECONDS);
        } catch(Exception ex) {
            // Start compensating transaction
            LOGGER.info("The ProcessPaymentCommand Failed. Initiating a compensating transaction:: " + ex.getMessage());
            return;
        }

        if(result == null) {
            // The command timed out (the 10 seconds ran out)
            LOGGER.info("The ProcessPaymentCommand resulted in NULL. Initiating a compensating transaction");
            // Start compensating transaction
        }
    }

    @SagaEventHandler(associationProperty = "orderId")
    public void handle(PaymentProcessedEvent paymentProcessedEvent) {
        // Send an ApprovedOrder Command
        ApproveOrderCommand approveOrderCommand = new ApproveOrderCommand(paymentProcessedEvent.getOrderId());

        commandGateway.send(approveOrderCommand);
    }
    @EndSaga
    @SagaEventHandler(associationProperty = "orderId")
    public void handle(OrderApprovedEvent orderApprovedEvent) {
        LOGGER.info("Order is approved, Order Saga is complete for orderId: " + orderApprovedEvent.getOrderId());
        // use @EndSaga annotation or the below
        // SagaLifecycle.end();
    }
}
