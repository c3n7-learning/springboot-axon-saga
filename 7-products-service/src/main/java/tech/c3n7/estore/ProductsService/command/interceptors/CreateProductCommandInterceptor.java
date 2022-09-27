package tech.c3n7.estore.ProductsService.command.interceptors;

import org.axonframework.commandhandling.CommandMessage;
import org.axonframework.messaging.MessageDispatchInterceptor;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import tech.c3n7.estore.ProductsService.command.CreateProductCommand;

import javax.annotation.Nonnull;
import java.math.BigDecimal;
import java.util.List;
import java.util.function.BiFunction;
import java.util.logging.Logger;

@Component
public class CreateProductCommandInterceptor implements MessageDispatchInterceptor<CommandMessage<?>> {
    private static final Logger LOGGER = (Logger) LoggerFactory.getLogger(CreateProductCommandInterceptor.class);

    @Nonnull
    @Override
    public BiFunction<Integer, CommandMessage<?>, CommandMessage<?>>
        handle(@Nonnull List<? extends CommandMessage<?>> list) {

        return (index, command) -> {

            LOGGER.info("Intercepted command: " + command.getPayloadType());

            if(CreateProductCommand.class.equals(command.getPayloadType())) {
                CreateProductCommand createProductCommand = (CreateProductCommand) command.getPayload();
                // Validate Create Product Command
                if (createProductCommand.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
                    throw new IllegalArgumentException("Price cannot be less or equal than zero");
                }

                if (createProductCommand.getTitle() == null
                        || createProductCommand.getTitle().isBlank()) {
                    throw new IllegalArgumentException("Price cannot be less or equal than zero");
                }
            }
          return command;
        };
    }
}
