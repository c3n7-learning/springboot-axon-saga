package tech.c3n7.estore.ProductsService.query;

import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.eventhandling.ResetHandler;
import org.axonframework.messaging.interceptors.ExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import tech.c3n7.estore.ProductsService.core.data.ProductEntity;
import tech.c3n7.estore.ProductsService.core.data.ProductsRepository;
import tech.c3n7.estore.ProductsService.core.events.ProductCreatedEvent;
import tech.c3n7.estore.core.commands.events.ProductReservationCancelledEvent;
import tech.c3n7.estore.core.commands.events.ProductReservedEvent;

@Component
@ProcessingGroup("product-group")
public class ProductEventsHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductEventsHandler.class);

    private final ProductsRepository productsRepository;

    public ProductEventsHandler(ProductsRepository productsRepository) {
        this.productsRepository = productsRepository;
    }


    @ExceptionHandler
    public void handle(IllegalArgumentException exception) {
        // Log error message
    }

    @ExceptionHandler
    public void handle(Exception exception) throws Exception{
        throw exception;
    }

    @EventHandler
    public void on(ProductCreatedEvent event) throws Exception {
        ProductEntity productEntity = new ProductEntity();
        BeanUtils.copyProperties(event, productEntity);

        try {
            productsRepository.save(productEntity);
        } catch(IllegalArgumentException ex) {
            ex.printStackTrace();
        }

//        productsRepository.save(productEntity);

//        if(true) throw new Exception("Forcing exception in event handler class");
    }

    @EventHandler
    public void on(ProductReservedEvent productReservedEvent) {
        ProductEntity productEntity = productsRepository.findByProductId(productReservedEvent.getProductId());

        LOGGER.info("ProductReservedEvent: Current product quantity " + productEntity.getQuantity());
        productEntity.setQuantity(productEntity.getQuantity() - productReservedEvent.getQuantity());
        productsRepository.save(productEntity);


        LOGGER.info("ProductReservedEvent: New product quantity " + productEntity.getQuantity());
        LOGGER.info("ProductReservedEvent called for productId: " + productReservedEvent.getProductId() +
                " and orderId: " + productReservedEvent.getOrderId());
    }

    @EventHandler
    public void on(ProductReservationCancelledEvent productReservationCancelledEvent) {
        ProductEntity productEntity = productsRepository
                .findByProductId(productReservationCancelledEvent.getProductId());

        LOGGER.info("ProductReservationCancelledEvent: Current product quantity " + productEntity.getQuantity());
        int quantity = productEntity.getQuantity() + productReservationCancelledEvent.getQuantity();

        productEntity.setQuantity(quantity);
        productsRepository.save(productEntity);

        LOGGER.info("ProductReservationCancelledEvent: New product quantity " + productEntity.getQuantity());
    }

    @ResetHandler
    public void reset() {
        productsRepository.deleteAll();
    }
}
