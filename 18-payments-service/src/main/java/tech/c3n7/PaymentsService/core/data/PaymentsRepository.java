package tech.c3n7.PaymentsService.core.data;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentsRepository extends JpaRepository<PaymentEntity, String> {
    PaymentEntity findByPaymentId(String paymentId);
}
