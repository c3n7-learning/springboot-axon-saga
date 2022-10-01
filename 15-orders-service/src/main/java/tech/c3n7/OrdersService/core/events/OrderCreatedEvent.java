package tech.c3n7.OrdersService.core.events;

import lombok.Data;
import tech.c3n7.OrdersService.core.model.OrderStatus;

@Data
public class OrderCreatedEvent {
    private String orderId;
    private String productId;
    private String userId;
    private int quantity;
    private String addressId;
    private OrderStatus orderStatus;
}
