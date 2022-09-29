package tech.c3n7.estore.ProductsService.query;

import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.messaging.interceptors.ExceptionHandler;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import tech.c3n7.estore.ProductsService.core.data.ProductEntity;
import tech.c3n7.estore.ProductsService.core.data.ProductsRepository;
import tech.c3n7.estore.ProductsService.core.events.ProductCreatedEvent;

@Component
@ProcessingGroup("product-group")
public class ProductEventsHandler {

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

        productsRepository.save(productEntity);

        if(true) throw new Exception("Forcing exception in event handler class");
    }
}
