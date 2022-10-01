package tech.c3n7.OrdersService.command.commands;

import lombok.Builder;
import lombok.Data;
import org.axonframework.modelling.command.TargetAggregateIdentifier;
import tech.c3n7.OrdersService.core.model.OrderStatus;

@Builder
@Data
public class CreateOrderCommand {
    @TargetAggregateIdentifier
    public final String orderId;
    private final String userId;
    private final String productId;
    private final int quantity;
    private final String addressId;
    private final OrderStatus orderStatus;

}
