package tech.c3n7.OrdersService.core.events;

import lombok.Value;
import tech.c3n7.OrdersService.core.model.OrderStatus;

@Value
public class OrderRejectedEvent {
    private final String orderId;
    private final String reason;
    private final OrderStatus orderStatus = OrderStatus.REJECTED;
}
