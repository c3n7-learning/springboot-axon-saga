package tech.c3n7.OrdersService.command.rest;

import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.axonframework.queryhandling.SubscriptionQueryResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tech.c3n7.OrdersService.command.commands.CreateOrderCommand;
import tech.c3n7.OrdersService.core.model.OrderStatus;
import tech.c3n7.OrdersService.core.model.OrderSummary;
import tech.c3n7.OrdersService.query.FindOrderQuery;

import javax.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping("/orders")
public class OrdersCommandController {

    private final CommandGateway commandGateway;

    private final QueryGateway queryGateway;

    @Autowired
    public OrdersCommandController(CommandGateway commandGateway, QueryGateway queryGateway) {
        this.commandGateway = commandGateway;
        this.queryGateway = queryGateway;
    }

    @PostMapping
    public OrderSummary createOrder(@Valid @RequestBody CreateOrderRestModel createOrderRestModel) {

        String orderId = UUID.randomUUID().toString();

        CreateOrderCommand createOrderCommand = CreateOrderCommand.builder()
                .orderId(orderId)
                .userId("27b95829-4f3f-4ddf-8983-151ba010e35b")
                .productId(createOrderRestModel.getProductId())
                .quantity(createOrderRestModel.getQuantity())
                .addressId(createOrderRestModel.getAddressId())
                .orderStatus(OrderStatus.CREATED)
                .build();

        SubscriptionQueryResult<OrderSummary, OrderSummary> queryResult = queryGateway.subscriptionQuery(
                new FindOrderQuery(orderId),
                ResponseTypes.instanceOf(OrderSummary.class),
                ResponseTypes.instanceOf(OrderSummary.class));
//        String returnValue = commandGateway.sendAndWait(createOrderCommand);

//        return returnValue;

        try {
            commandGateway.sendAndWait(createOrderCommand);
            return queryResult.updates().blockFirst();
        } finally {
            queryResult.close();
        }
    }
}
