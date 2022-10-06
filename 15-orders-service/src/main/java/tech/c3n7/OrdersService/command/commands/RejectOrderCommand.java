package tech.c3n7.OrdersService.command.commands;

import lombok.Value;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Value
public class RejectOrderCommand {
    @TargetAggregateIdentifier
    private final String orderId;
    private final String reason;
}
