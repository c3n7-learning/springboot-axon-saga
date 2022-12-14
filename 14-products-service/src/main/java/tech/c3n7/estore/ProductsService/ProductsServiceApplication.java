package tech.c3n7.estore.ProductsService;

import org.axonframework.commandhandling.CommandBus;
import org.axonframework.config.EventProcessingConfigurer;
import org.axonframework.eventsourcing.EventCountSnapshotTriggerDefinition;
import org.axonframework.eventsourcing.SnapshotTriggerDefinition;
import org.axonframework.eventsourcing.Snapshotter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import tech.c3n7.estore.ProductsService.command.interceptors.CreateProductCommandInterceptor;
import tech.c3n7.estore.ProductsService.core.errorHandling.ProductsServiceEventsErrorHandler;

@SpringBootApplication
@EnableDiscoveryClient
@Import({AxonConfig.class})
public class ProductsServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProductsServiceApplication.class, args);
    }

    @Autowired
    public void registerCreateProductCommandInterceptor(ApplicationContext context, CommandBus commandBus) {
        commandBus.registerDispatchInterceptor(context.getBean(CreateProductCommandInterceptor.class));

    }

    @Autowired
    public void configure(EventProcessingConfigurer config) {
        config.registerListenerInvocationErrorHandler("product-group",
                conf -> new ProductsServiceEventsErrorHandler());
        // This pushes the error further up
//		config.registerListenerInvocationErrorHandler("product-group",
//				conf -> PropagatingErrorHandler.instance());
    }

    @Bean(name="productSnapshotTriggerDefinition")
    public SnapshotTriggerDefinition snapshotTriggerDefinition(Snapshotter snapshotter) {
        // Snapshot every 3 events
        return new EventCountSnapshotTriggerDefinition(snapshotter, 3);
    }
}

