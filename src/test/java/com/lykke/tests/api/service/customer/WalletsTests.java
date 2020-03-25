package com.lykke.tests.api.service.customer;

import static com.lykke.api.testing.api.common.GenerateUtils.generateRandomString;
import static com.lykke.tests.api.common.CommonConsts.AWAITILITY_INITIAL_BALANCE_WAIT_SECONDS;
import static com.lykke.tests.api.common.CommonConsts.SMOKE_TEST;
import static com.lykke.tests.api.common.prerequisites.EarnRules.AMOUNT_IN_TOKENS_100_000;
import static com.lykke.tests.api.common.prerequisites.EarnRules.getExpectedAmountAfterCompaignCompletion;
import static com.lykke.tests.api.service.campaigns.CampaignUtils.deleteCampaign;
import static com.lykke.tests.api.service.campaigns.CampaignUtils.getCampaigns;
import static com.lykke.tests.api.service.customer.CustomerWalletUtils.getCustomerWallets;
import static com.lykke.tests.api.service.customer.LoginLogoutCustomerUtils.getUserToken;
import static com.lykke.tests.api.service.customermanagement.RegisterCustomerUtils.registerDefaultVerifiedCustomer;
import static com.lykke.tests.api.service.privateblockchainfacade.PrivateBlockchainFacadeUtils.postTransfer;
import static org.apache.http.HttpStatus.SC_OK;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import com.lykke.api.testing.annotations.UserStoryId;
import com.lykke.tests.api.base.BaseApiTest;
import com.lykke.tests.api.common.CommonConsts.Currency;
import com.lykke.tests.api.common.prerequisites.EarnRules;
import com.lykke.tests.api.service.privateblockchainfacade.model.TransferRequestModel;
import com.lykke.tests.api.service.privateblockchainfacade.model.TransferResponseModel;
import java.util.concurrent.TimeUnit;
import lombok.val;
import org.awaitility.Awaitility;
import org.awaitility.Duration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

public class WalletsTests extends BaseApiTest {

    private static final String CAMPAIGNS_FIELD = "Campaigns";
    private static final String ZERO_DOUBLE = "0.00";
    private static final String AMOUNT_120_000 = "120000";
    private static final String AMOUNT_20 = "20";

    @BeforeEach
    void deleteAllCampaigns() {
        // TODO: deletion of campaigns
        /*
        while (!getCampaigns().jsonPath().getList(CAMPAIGNS_FIELD).isEmpty()) {
            String campaign = getCampaigns()
                    .then()
                    .assertThat()
                    .statusCode(SC_OK)
                    .extract()
                    .path(CAMPAIGNS_FIELD + "[0].Id");

            deleteCampaign(campaign)
                    .then()
                    .assertThat()
                    .statusCode(SC_OK);
        }
        */
    }

    @Test
    @UserStoryId(storyId = 2031)
    @Tag(SMOKE_TEST)
    void shouldGetCustomerWalletsWhenBalanceIsZero() {
        val customerData = registerDefaultVerifiedCustomer();
        val customerToken = getUserToken(customerData.getEmail(), customerData.getPassword());

        val actualResult = getCustomerWallets(customerToken);

        assertAll(
                () -> assertEquals(ZERO_DOUBLE, actualResult[0].getBalance()),
                () -> assertEquals(Currency.MVN_CURRENCY, actualResult[0].getAssetSymbol()),
                () -> assertFalse(actualResult[0].getIsWalletBlocked()),
                () -> assertEquals(ZERO_DOUBLE, actualResult[0].getTotalEarned()),
                () -> assertEquals(ZERO_DOUBLE, actualResult[0].getTotalSpent())
        );
    }

    @Disabled("TODO: needs investigation")
    @Test
    @UserStoryId(storyId = 2031)
    void shouldGetCustomerWallets() {
        EarnRules.createEarnRuleWithENContent(false);
        val senderData = registerDefaultVerifiedCustomer();
        val recipientData = registerDefaultVerifiedCustomer();

        val customerToken = getUserToken(senderData.getEmail(), senderData.getPassword());

        Awaitility.await()
                .atMost(AWAITILITY_INITIAL_BALANCE_WAIT_SECONDS, TimeUnit.SECONDS)
                .pollInterval(Duration.TWO_SECONDS)
                .until(() -> Double.valueOf(getExpectedAmountAfterCompaignCompletion()) <= Double
                                .valueOf(getCustomerWallets(customerToken)[0].getBalance())
                        // TODO: probably, we need to wait for totalEarned here
                        // && 0.0 < Double.valueOf(getCustomerWallets(customerToken)[0].getTotalEarned())
                );

        val actualResult = getCustomerWallets(customerToken);

        assertAll(
                () -> assertEquals(Double.valueOf(getExpectedAmountAfterCompaignCompletion()),
                        Double.valueOf(actualResult[0].getBalance())),
                () -> assertEquals(Currency.MVN_CURRENCY, actualResult[0].getAssetSymbol()),
                () -> assertFalse(actualResult[0].getIsWalletBlocked()),
                // TODO: it doesn't work for mow
                // () -> assertEquals(Double.valueOf(getExpectedAmountAfterCompaignCompletion()),
                //     Double.valueOf(actualResult[0].getTotalEarned())),
                () -> assertEquals(0.0, Double.valueOf(actualResult[0].getTotalSpent()))
        );

        postTransfer(TransferRequestModel
                .builder()
                .senderCustomerId(senderData.getCustomerId())
                .recipientCustomerId(recipientData.getCustomerId())
                .amount(AMOUNT_20)
                .transferId(generateRandomString(100))
                .build())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(TransferResponseModel.class);

        Awaitility.await()
                .atMost(AWAITILITY_INITIAL_BALANCE_WAIT_SECONDS, TimeUnit.SECONDS)
                .pollInterval(Duration.TWO_SECONDS)
                .until(() -> getCustomerWallets(customerToken)[0].getBalance()
                        .equals(AMOUNT_IN_TOKENS_100_000));

        val actualResultAfterTransfer = getCustomerWallets(customerToken);

        assertAll(
                () -> assertEquals(Double.valueOf(AMOUNT_IN_TOKENS_100_000),
                        Double.valueOf(actualResultAfterTransfer[0].getBalance())),
                () -> assertEquals(Currency.MVN_CURRENCY, actualResultAfterTransfer[0].getAssetSymbol()),
                () -> assertFalse(actualResultAfterTransfer[0].getIsWalletBlocked()),
                // TODO: () -> assertEquals(Double.valueOf(getExpectedAmountAfterCompaignCompletion()), Double.valueOf(actualResult[0].getTotalEarned())),
                () -> assertEquals(AMOUNT_20, actualResultAfterTransfer[0].getTotalSpent())
        );
    }
}
