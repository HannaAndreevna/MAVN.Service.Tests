package com.lykke.tests.api.service.mavnubeintegration;

import static org.apache.http.HttpStatus.SC_NO_CONTENT;

import com.lykke.api.testing.annotations.UserStoryId;
import com.lykke.tests.api.base.BaseApiTest;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public class DecreaseUPointBalanceTests extends BaseApiTest {

    private static final String CUSTOMER_ID = "8c13fb4e-6837-4150-afbf-666a5a119acf"; //TODO: Change when we can get the CustomerId
    private static Double changeBalance = 30.50;

    @Test
    @Disabled("Endpoint currently not active")
    @UserStoryId(storyId = 391)
    void shouldDecreaseUPointBalance() {
        DecreaseUPointsBalanceUtils.decreaseUPointsBalance(CUSTOMER_ID, changeBalance)
                .then()
                .assertThat()
                .statusCode(SC_NO_CONTENT);
        // TODO: add balance change proof*/
    }
}
