package tech.c3n7.OrdersService.query;

import lombok.Value;

@Value
public class FindOrderQuery {
    private final String orderId;
}
