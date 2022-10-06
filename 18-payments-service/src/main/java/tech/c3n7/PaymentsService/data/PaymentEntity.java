package tech.c3n7.PaymentsService.data;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serial;
import java.io.Serializable;

@Entity
@Data
@Table(name = "payments")
public class PaymentEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 5313493413859894403L;

    @Id
    private String paymentId;

    @Column
    public String orderId;
}
