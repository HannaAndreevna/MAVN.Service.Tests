package com.lykke.tests.api.service.privateblockchainfacade;

import static com.lykke.tests.api.service.customermanagement.RegisterCustomerUtils.registerCustomer;
import static com.lykke.tests.api.service.privateblockchainfacade.PrivateBlockchainFacadeUtils.getCustomerBalance;
import static com.lykke.tests.api.service.privateblockchainfacade.PrivateBlockchainFacadeUtils.getNewOperations;
import static com.lykke.tests.api.service.privateblockchainfacade.PrivateBlockchainFacadeUtils.postBonuses;
import static com.lykke.tests.api.service.privateblockchainfacade.model.CustomerBalanceError.CUSTOMER_WALLET_MISSING;
import static com.lykke.tests.api.service.privateblockchainfacade.model.CustomerBalanceError.NONE;
import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.apache.http.HttpStatus.SC_NOT_FOUND;
import static org.apache.http.HttpStatus.SC_OK;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.lykke.api.testing.annotations.UserStoryId;
import com.lykke.api.testing.api.common.GenerateUtils;
import com.lykke.tests.api.base.BaseApiTest;
import com.lykke.tests.api.service.privateblockchainfacade.model.BonusRewardError;
import com.lykke.tests.api.service.privateblockchainfacade.model.BonusRewardRequestModel;
import com.lykke.tests.api.service.privateblockchainfacade.model.BonusRewardResponseModel;
import com.lykke.tests.api.service.privateblockchainfacade.model.CustomerBalanceRequestModel;
import com.lykke.tests.api.service.privateblockchainfacade.model.CustomerBalanceResponseModel;
import com.lykke.tests.api.service.privateblockchainfacade.model.PrivateBlockChainFacadeCommonErrorResponseModel;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.awaitility.Awaitility;
import org.awaitility.Duration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class CustomerBalanceTests extends BaseApiTest {

    private static final String INVALID_CUSTOMER_ID_01 = "aaa";
    private static final String INVALID_CUSTOMER_ID_02 = "111";
    private static final Double AMOUNT = 1000.0;
    private static final String SOME_BONUS_REASON = "some bonus reason";

    @Test
    @UserStoryId(975)
    void shouldReturnCustomerBalanceByValidCustomerId() {
        val requestObject = CustomerBalanceRequestModel
                .builder()
                .customerId(registerCustomer())
                .build();
        val actualResult = getCustomerBalance(requestObject)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(CustomerBalanceResponseModel.class);

        assertAll(
                () -> assertEquals(NONE, actualResult.getError()),
                () -> assertEquals(0.0, Double.valueOf(actualResult.getTotal()))
        );
    }

    @Test
    @UserStoryId(975)
    void shouldReturnUpdatedCustomerBalanceByValidCustomerId() {
        val customerId = registerCustomer();

        val postBonusResponse = postBonuses(BonusRewardRequestModel
                .builder()
                .customerId(customerId)
                .rewardRequestId(GenerateUtils.generateRandomString(100))
                .bonusReason(SOME_BONUS_REASON)
                .amount(AMOUNT.toString())
                .build())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(BonusRewardResponseModel.class);

        assertEquals(BonusRewardError.NONE, postBonusResponse.getError());

        Awaitility.await().atMost(30, TimeUnit.SECONDS)
                .with()
                .pollInterval(Duration.FIVE_SECONDS)
                .until(() -> {
                    getNewOperations();
                    val newAmount = getCustomerBalance(CustomerBalanceRequestModel
                            .builder()
                            .customerId(customerId)
                            .build())
                            .then()
                            .assertThat()
                            .statusCode(SC_OK)
                            .extract()
                            .as(CustomerBalanceResponseModel.class)
                            .getTotal();
                    System.out.println(newAmount);
                    return Double.valueOf(AMOUNT) == Double.valueOf(newAmount);
                });

        val senderBalance = getCustomerBalance(CustomerBalanceRequestModel
                .builder()
                .customerId(customerId)
                .build())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(CustomerBalanceResponseModel.class)
                .getTotal();
        assertEquals(AMOUNT, senderBalance);

        val requestObject = CustomerBalanceRequestModel
                .builder()
                .customerId(customerId)
                .build();
        val actualResult = getCustomerBalance(requestObject)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(CustomerBalanceResponseModel.class);

        assertAll(
                () -> assertEquals(NONE, actualResult.getError()),
                () -> assertEquals(AMOUNT, actualResult.getTotal())
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {INVALID_CUSTOMER_ID_01, INVALID_CUSTOMER_ID_02})
    @UserStoryId(975)
    void shouldNotReturnCustomerBalanceByInvalidCustomerId(String customerId) {
        val requestObject = CustomerBalanceRequestModel
                .builder()
                .customerId(customerId)
                .build();
        val actualResult = getCustomerBalance(requestObject)
                .then()
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .extract()
                .as(PrivateBlockChainFacadeCommonErrorResponseModel.class);

        assertEquals(requestObject.getCustomerIdMessage()[0], actualResult.getModelErrors().getCustomerId()[0]);
    }

    @Test
    @UserStoryId(975)
    void shouldNotReturnCustomerBalanceByEmptyCustomerId() {
        val requestObject = CustomerBalanceRequestModel
                .builder()
                .customerId(StringUtils.EMPTY)
                .build();
        getCustomerBalance(requestObject)
                .then()
                .assertThat()
                .statusCode(SC_NOT_FOUND);
    }

    @Test
    @UserStoryId(975)
    void shouldNotReturnCustomerBalanceByNonExistingCustomerId() {
        val requestObject = CustomerBalanceRequestModel
                .builder()
                .customerId(UUID.randomUUID().toString())
                .build();
        val actualResult = getCustomerBalance(requestObject)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(CustomerBalanceResponseModel.class);

        /*
        expected: <0> but was: <0E-18>
Comparison Failure:
Expected :0
Actual   :0E-18
        */
        assertAll(
                () -> assertEquals(CUSTOMER_WALLET_MISSING, actualResult.getError()),
                () -> assertEquals(0.0, Double.valueOf(actualResult.getTotal()))
        );
    }
}
