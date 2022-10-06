package tech.c3n7.estore.core.commands.events;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductReservationCancelledEvent {
    private final String productId;
    private final int quantity;
    private final String orderId;
    private final String reason;
    private final String userId;
}
