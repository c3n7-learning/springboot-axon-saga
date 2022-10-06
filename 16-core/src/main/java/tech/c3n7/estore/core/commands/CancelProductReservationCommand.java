package tech.c3n7.estore.core.commands;

import lombok.Builder;
import lombok.Data;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Data
@Builder
public class CancelProductReservationCommand {
    @TargetAggregateIdentifier
    private final String productId;
    private final int quantity;
    private final String orderId;
    private final String reason;
    private final String userId;
}
