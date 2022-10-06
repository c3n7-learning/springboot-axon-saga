package tech.c3n7.OrdersService.core.events;

import lombok.Value;
import tech.c3n7.OrdersService.core.model.OrderStatus;

@Value
public class OrderApprovedEvent {
    private final String orderId;
    private final OrderStatus orderStatus = OrderStatus.APPROVED;
}
