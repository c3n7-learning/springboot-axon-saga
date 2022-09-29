package tech.c3n7.estore.ProductsService.command.interceptors;

import org.axonframework.commandhandling.CommandMessage;
import org.axonframework.messaging.MessageDispatchInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import tech.c3n7.estore.ProductsService.command.CreateProductCommand;
import tech.c3n7.estore.ProductsService.core.data.ProductLookupEntity;
import tech.c3n7.estore.ProductsService.core.data.ProductLookupRepositry;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.function.BiFunction;

@Component
public class CreateProductCommandInterceptor implements MessageDispatchInterceptor<CommandMessage<?>> {
    private static final Logger LOGGER = LoggerFactory.getLogger(CreateProductCommandInterceptor.class);

    private final ProductLookupRepositry productLookupRepositry;

    public CreateProductCommandInterceptor(ProductLookupRepositry productLookupRepositry) {
        this.productLookupRepositry = productLookupRepositry;
    }

    @Nonnull
    @Override
    public BiFunction<Integer, CommandMessage<?>, CommandMessage<?>>
        handle(@Nonnull List<? extends CommandMessage<?>> list) {

        return (index, command) -> {

            LOGGER.info("Intercepted command: " + command.getPayloadType());

            if(CreateProductCommand.class.equals(command.getPayloadType())) {
                CreateProductCommand createProductCommand = (CreateProductCommand) command.getPayload();
                // Validate Create Product Command
                ProductLookupEntity productLookupEntity = productLookupRepositry.findByProductIdOrTitle(createProductCommand.getProductId(), createProductCommand.getTitle());

                if(productLookupEntity  != null) {
                    throw new IllegalStateException(String.format("Product with productId %s or title %s already exists",
                        createProductCommand.getProductId(), createProductCommand.getTitle()));
                }
            }
          return command;
        };
    }
}
