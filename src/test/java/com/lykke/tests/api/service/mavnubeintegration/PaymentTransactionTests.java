package com.lykke.tests.api.service.mavnubeintegration;

import com.lykke.tests.api.base.BaseApiTest;
import com.lykke.api.testing.annotations.UserStoryId;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.UUID;

public class PaymentTransactionTests extends BaseApiTest {

    private static UUID operationId = UUID.randomUUID();
    private static UUID customerId = UUID.randomUUID();
    private static UUID venueId = UUID.randomUUID();
    private static Double amount = 100.20;
    private static String paymentDate = "2019-04-25T07:23:41.947Z";

    private static PaymentTransaction paymentTransaction;

    @Disabled("Endpoint currently not active")
    @Test
    @UserStoryId(storyId = 391)
    void shouldExecutePaymentTransaction() {
        /*PaymentTransactionUtils.executePaymentTransaction(operationId, customerId, venueId, amount, paymentDate)
                .then()
                .assertThat()
                .statusCode(SC_NO_CONTENT);*/
    }
}
