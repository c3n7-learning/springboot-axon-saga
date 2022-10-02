package tech.c3n7.estore.core.commands;

import lombok.Builder;
import lombok.Data;
import org.axonframework.modelling.command.TargetAggregateIdentifier;
import tech.c3n7.estore.core.model.PaymentDetails;

@Builder
@Data
public class ProcessPaymentCommand {
    @TargetAggregateIdentifier
    private final String paymentId;
    private final String orderId;
    private final PaymentDetails paymentDetails;
}
