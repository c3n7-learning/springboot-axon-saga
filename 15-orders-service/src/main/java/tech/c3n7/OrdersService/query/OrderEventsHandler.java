package tech.c3n7.OrdersService.query;

import org.axonframework.eventhandling.EventHandler;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import tech.c3n7.OrdersService.core.data.OrderEntity;
import tech.c3n7.OrdersService.core.data.OrdersRepository;
import tech.c3n7.OrdersService.core.events.OrderCreatedEvent;

@Component
public class OrderEventsHandler {
    private final OrdersRepository ordersRepository;

    public OrderEventsHandler(OrdersRepository ordersRepository) {
        this.ordersRepository = ordersRepository;
    }

    @EventHandler
    public void on(OrderCreatedEvent orderCreatedEvent) {
        OrderEntity orderEntity = new OrderEntity();
        BeanUtils.copyProperties(orderCreatedEvent, orderEntity);

        ordersRepository.save(orderEntity);
    }
}
