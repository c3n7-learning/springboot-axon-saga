package tech.c3n7.OrdersService;

import org.axonframework.config.Configuration;
import org.axonframework.config.ConfigurationScopeAwareProvider;
import org.axonframework.deadline.DeadlineManager;
import org.axonframework.deadline.SimpleDeadlineManager;
import org.axonframework.spring.messaging.unitofwork.SpringTransactionManager;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import({ AxonConfig.class })
public class OrdersServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(OrdersServiceApplication.class, args);
	}

	@Bean
	public DeadlineManager deadlineManager(Configuration configuration, SpringTransactionManager springTransactionManager) {
		return SimpleDeadlineManager.builder()
				.scopeAwareProvider(new ConfigurationScopeAwareProvider(configuration))
				.transactionManager(springTransactionManager)
				.build();
	}
}
