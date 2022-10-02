package tech.c3n7.UsersService.query;

import org.springframework.stereotype.Component;
import tech.c3n7.estore.core.model.PaymentDetails;
import tech.c3n7.estore.core.model.User;
import tech.c3n7.estore.core.query.FetchUserPaymentDetailsQuery;

@Component
public class UserEventsHandler {
    public User fetchUser(FetchUserPaymentDetailsQuery fetchUserPaymentDetailsQuery) {
        PaymentDetails paymentDetails = PaymentDetails.builder()
                .cardNumber("123Card")
                .cvv("123")
                .name("TIMOTHY KARANI")
                .validUntilMonth(12)
                .validUntilYear(2030)
                .build();

        User userRest = User.builder()
                .firstName("Timothy")
                .lastName("Karani")
                .userId(fetchUserPaymentDetailsQuery.getUserId())
                .paymentDetails(paymentDetails)
                .build();

        return userRest;
    }
}
