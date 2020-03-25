package com.lykke.tests.api.service.campaigns.purchaseproducttriggers;

import static com.lykke.tests.api.base.BasicFunctionalities.BASE_ASSET;
import static com.lykke.tests.api.common.CommonConsts.AWAITILITY_DEFAULT_SEC;
import static com.lykke.tests.api.common.CommonMethods.getCustomerBalanceForDefaultAsset;
import static com.lykke.tests.api.service.bonusengine.purchaseproducttriggers.MVNIntegrationUtils.makePurchaseWithReferral;
import static com.lykke.tests.api.service.bonusengine.purchaseproducttriggers.MVNIntegrationUtils.makePurchaseWithoutReferral;
import static com.lykke.tests.api.service.campaigns.CampaignUtils.createConditionArray;
import static com.lykke.tests.api.service.customermanagement.RegisterCustomerUtils.registerDefaultVerifiedCustomer;
import static org.apache.http.HttpStatus.SC_OK;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.lykke.api.testing.annotations.UserStoryId;
import com.lykke.tests.api.service.campaigns.BaseCampaignTest;
import java.util.concurrent.TimeUnit;
import lombok.val;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public class PurchaseProductTriggersTests extends BaseCampaignTest {

    @BeforeEach
    void campaignCleanup() {
        deleteAllCampaigns();
    }

    @AfterEach
    void deleteEarnRule() {
        deleteAllCampaigns();
    }

    @Test
    @UserStoryId(storyId = 606)
    void shouldReceiveRewardForMVNPurchase() {
        val condition = baseCondition
                .type(CONDITION_TYPE_PURCHASE)
                .build();

        campaign = baseCampaign
                .reward(CAMPAIGN_REWARD.toString())
                .rewardType(REWARD_TYPE_FIXED)
                .conditions(createConditionArray(condition))
                .build();

        campaignId = createCampaignAndGetId();

        val customerData = registerDefaultVerifiedCustomer();

        makePurchaseWithoutReferral(customerData.getCustomerId(), 5f, BASE_ASSET)
                .then()
                .assertThat()
                .statusCode(SC_OK);

        Awaitility.await().atMost(AWAITILITY_DEFAULT_SEC, TimeUnit.SECONDS)
                .until(() -> getCustomerBalanceForDefaultAsset(customerData.getCustomerId()) == (CAMPAIGN_REWARD
                        + CONDITION_REWARD));
    }

    @Test
    @UserStoryId(storyId = 609)
    void shouldReceiveCorrectRewardForMVNPurchaseWithPercentageRewardType() {
        val condition = baseCondition
                .type(CONDITION_TYPE_PURCHASE)
                .build();

        campaign = baseCampaign
                .reward(CAMPAIGN_REWARD_PERCENTAGE.toString())
                .rewardType(REWARD_TYPE_PERCENTAGE)
                .conditions(createConditionArray(condition))
                .build();

        campaignId = createCampaignAndGetId();

        val customerData = registerDefaultVerifiedCustomer();
        float purchaseAmount = 5;

        makePurchaseWithoutReferral(customerData.getCustomerId(), purchaseAmount, BASE_ASSET)
                .then()
                .assertThat()
                .statusCode(SC_OK);

        Awaitility.await().atMost(AWAITILITY_DEFAULT_SEC, TimeUnit.SECONDS)
                .until(() -> getCustomerBalanceForDefaultAsset(customerData.getCustomerId()) == (
                        CAMPAIGN_REWARD_PERCENTAGE * purchaseAmount
                                + CONDITION_REWARD));
    }

    @Disabled("Disabled because makes purchase which causes errors, "
            + " purchase product and purchase product referral are deprecated and not in the MVP")
    @Test
    @UserStoryId(storyId = 608)
    void shouldReceiveRewardForMVNPurchaseWithReferralForReferrerAndReferred() {
        val condition = baseCondition
                .type(CONDITION_TYPE_PURCHASE_REFERRAL)
                .build();

        campaign = baseCampaign
                .rewardType(REWARD_TYPE_FIXED)
                .reward(CAMPAIGN_REWARD.toString())
                .conditions(createConditionArray(condition))
                .build();

        campaignId = createCampaignAndGetId();

        val referrerCustomer = registerDefaultVerifiedCustomer().getCustomerId();
        val referredCustomer = registerDefaultVerifiedCustomer().getCustomerId();
        String referralCode = getReferralCode(referrerCustomer);

        makePurchaseWithReferral(referredCustomer, 5f, BASE_ASSET, referralCode)
                .then()
                .assertThat()
                .statusCode(SC_OK);

        Awaitility.await().atMost(AWAITILITY_DEFAULT_SEC, TimeUnit.SECONDS)
                .until(() -> getCustomerBalanceForDefaultAsset(referrerCustomer) == (CAMPAIGN_REWARD
                        + CONDITION_REWARD));
        assertEquals(0, getCustomerBalanceForDefaultAsset(referredCustomer));
    }
}
