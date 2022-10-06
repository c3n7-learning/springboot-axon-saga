package tech.c3n7.OrdersService.query;

import org.axonframework.queryhandling.QueryHandler;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Component;
import tech.c3n7.OrdersService.core.data.OrderEntity;
import tech.c3n7.OrdersService.core.data.OrdersRepository;
import tech.c3n7.OrdersService.core.model.OrderSummary;

@Component
public class OrderQueriesHandler {
    OrdersRepository ordersRepository;

    public OrderQueriesHandler(OrdersRepository ordersRepository) {
        this.ordersRepository = ordersRepository;
    }

    @QueryHandler
    public OrderSummary findOrder(FindOrderQuery findOrderQuery) {
        OrderEntity orderEntity = ordersRepository.findByOrderId(findOrderQuery.getOrderId());

        return new OrderSummary(orderEntity.getOrderId(), orderEntity.getOrderStatus(), "");
    }
}
