package tech.c3n7.OrdersService.core.data;

import lombok.Data;
import tech.c3n7.OrdersService.core.model.OrderStatus;

import javax.persistence.*;

@Data
@Entity
@Table(name="orders")
public class OrderEntity {
    @Id
    @Column(unique = true)
    public String orderId;

    private String productId;

    private String userId;

    private int quantity;

    private String addressId;

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;
}
