package tech.c3n7.PaymentsService.events;

import org.axonframework.eventhandling.EventHandler;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import tech.c3n7.PaymentsService.core.data.PaymentEntity;
import tech.c3n7.PaymentsService.core.data.PaymentsRepository;
import tech.c3n7.estore.core.commands.events.PaymentProcessedEvent;

@Component
public class PaymentEventsHandler {

    private final PaymentsRepository paymentsRepository;

    public PaymentEventsHandler(PaymentsRepository paymentsRepository) {
        this.paymentsRepository = paymentsRepository;
    }

    @EventHandler
    public void on(PaymentProcessedEvent paymentProcessedEvent) {
        PaymentEntity paymentEntity = new PaymentEntity();

        BeanUtils.copyProperties(paymentProcessedEvent, paymentEntity);

        paymentsRepository.save(paymentEntity);
    }
}
