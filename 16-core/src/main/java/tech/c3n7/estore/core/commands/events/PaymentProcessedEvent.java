package tech.c3n7.estore.core.commands.events;

import lombok.Builder;
import lombok.Data;

@Data
public class PaymentProcessedEvent {
    private String orderId;
    private String paymentId;
}
