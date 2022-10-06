package tech.c3n7.PaymentsService.command;

import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;
import org.springframework.beans.BeanUtils;
import tech.c3n7.estore.core.commands.ProcessPaymentCommand;
import tech.c3n7.estore.core.commands.events.PaymentProcessedEvent;

@Aggregate
public class PaymentAggregate {

    @AggregateIdentifier
    private String paymentId;

    private String orderId;

    @CommandHandler
    public PaymentAggregate(ProcessPaymentCommand processPaymentCommand) {
        PaymentProcessedEvent paymentProcessedEvent =new PaymentProcessedEvent();

        if(processPaymentCommand.getOrderId() == null ||
            processPaymentCommand.getOrderId().isBlank()) {
            throw new IllegalArgumentException("The Order ID can not be left blank");
        }

        if(processPaymentCommand.getPaymentDetails() == null) {
            throw new IllegalArgumentException("Missing payment details");
        }

        if(processPaymentCommand.getPaymentId() == null) {
            throw new IllegalArgumentException("Missing payment Id");
        }

        BeanUtils.copyProperties(processPaymentCommand, paymentProcessedEvent);

        AggregateLifecycle.apply(paymentProcessedEvent);
    }

    @EventSourcingHandler
    public void on(PaymentProcessedEvent paymentProcessedEvent) {
        this.orderId = paymentProcessedEvent.getOrderId();
        this.paymentId = paymentProcessedEvent.getPaymentId();
    }
}
