package tech.c3n7.estore.ProductsService.command;

import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.stereotype.Component;
import tech.c3n7.estore.ProductsService.core.data.ProductLookupEntity;
import tech.c3n7.estore.ProductsService.core.data.ProductLookupRepositry;
import tech.c3n7.estore.ProductsService.core.events.ProductCreatedEvent;

@Component
@ProcessingGroup("product-group")
public class ProductLookupEventsHandler {

    private final ProductLookupRepositry productLookupRepositry;

    public ProductLookupEventsHandler(ProductLookupRepositry productLookupRepositry) {
        this.productLookupRepositry = productLookupRepositry;
    }

    @EventHandler
    public void on(ProductCreatedEvent event) {
        ProductLookupEntity productLookupEntity = new ProductLookupEntity(event.getProductId(), event.getTitle());

        productLookupRepositry.save(productLookupEntity);
    }
}
