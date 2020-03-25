package com.lykke.tests.api.service.mavnubeintegration;

import static com.lykke.tests.api.service.mavnubeintegration.UPointsBalanceUtils.executeUPointsBalance;
import static org.apache.http.HttpStatus.SC_NO_CONTENT;

import com.lykke.api.testing.annotations.UserStoryId;
import com.lykke.tests.api.base.BaseApiTest;
import java.util.UUID;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public class UPointsBalanceTests extends BaseApiTest {

    private static UUID operationId = UUID.randomUUID();
    private static UUID customerId = UUID.randomUUID();
    private static Double amount = 100.20;
    private static String paymentDate = "2019-04-25T07:23:41.947Z";

    private static UPointsBalance balance;

    @BeforeAll
    static void setupUPoint() {
        balance = UPointsBalance
                .builder()
                .operationId(operationId)
                .customerId(customerId)
                .amount(amount)
                .changeDate(paymentDate)
                .build();
    }

    @Disabled("Endpoint currently not active")
    @Test
    @UserStoryId(storyId = 391)
    void shouldExecuteUPointsBalance() {
        executeUPointsBalance(balance)
                .then()
                .assertThat()
                .statusCode(SC_NO_CONTENT);
    }
}
