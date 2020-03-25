package com.lykke.tests.api.service.campaigns;

import static com.lykke.api.testing.api.common.GenerateUtils.generateRandomString;
import static com.lykke.tests.api.common.CommonConsts.AWAITILITY_INITIAL_BALANCE_WAIT_SECONDS;
import static com.lykke.tests.api.common.CommonConsts.AWAITILITY_OPERATIONS_HISTORY_SEC;
import static com.lykke.tests.api.common.CommonConsts.AWAITILITY_POLL_INTERVAL_MID_SEC;
import static com.lykke.tests.api.common.CommonConsts.SMOKE_TEST;
import static com.lykke.tests.api.common.CommonConsts.Staking.DEFAULT_STAKE_WARNING_PERIOD;
import static com.lykke.tests.api.common.CommonConsts.Staking.DEFAULT_STAKING_PERIOD;
import static com.lykke.tests.api.common.CommonMethods.getCustomerBalanceForDefaultAsset;
import static com.lykke.tests.api.common.HelperUtils.simulateTrigger;
import static com.lykke.tests.api.service.campaigns.CampaignUtils.createCampaign;
import static com.lykke.tests.api.service.campaigns.CampaignUtils.createCampaignWithInfiniteCompletionCount;
import static com.lykke.tests.api.service.campaigns.CampaignUtils.createConditionArray;
import static com.lykke.tests.api.service.customermanagement.RegisterCustomerUtils.registerDefaultVerifiedCustomer;
import static com.lykke.tests.api.service.operationshistory.OperationsUtils.getTransactionsByCustomerId;
import static com.lykke.tests.api.service.partnermanagement.PartnerManagementUtils.createPartner;
import static java.util.Collections.singletonMap;
import static org.apache.http.HttpStatus.SC_OK;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.lykke.api.testing.annotations.UserStoryId;
import com.lykke.api.testing.api.common.FakerUtils;
import com.lykke.tests.api.common.HelperUtils.SimulateTriggerRequest;
import com.lykke.tests.api.common.enums.Localization;
import com.lykke.tests.api.common.enums.RuleContentType;
import com.lykke.tests.api.common.enums.campaign.ConditionType;
import com.lykke.tests.api.service.campaigns.model.BonusTypeResponseModel;
import com.lykke.tests.api.service.campaigns.model.Campaign;
import com.lykke.tests.api.service.campaigns.model.EarnRule;
import com.lykke.tests.api.service.campaigns.model.RewardType;
import com.lykke.tests.api.service.operationshistory.model.PaginatedCustomerOperationsResponse;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import lombok.val;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

public class CampaignTests extends BaseCampaignTest {

    public static final String STAKED_CAMPAIGN_ID_KEY = "StakedCampaignId";
    private static final String CAMPAIGN_ID_FIELD = "CampaignId";
    private static final String EXPECTED_BONUS_TYPES = "commission-one-referral, commission-two-referral, estate-otp-referral";

    @BeforeEach
    void campaignSetup() {
        deleteAllCampaigns();

        bonusType = baseCondition
                .type(CONDITION_TYPE_SIGNUP)
                .stakingPeriod(DEFAULT_STAKING_PERIOD)
                .stakeWarningPeriod(DEFAULT_STAKE_WARNING_PERIOD)
                .stakeAmount(Double.valueOf(CAMPAIGN_REWARD / 2).toString())
                .build();

        campaign = baseCampaign
                .rewardType(REWARD_TYPE_FIXED)
                .reward(CAMPAIGN_REWARD.toString())
                .conditions(createConditionArray(bonusType))
                .amountInTokens(CAMPAIGN_REWARD.toString())
                .build();

        earnRule = EarnRule
                .builder()
                .ruleContentType(RuleContentType.TITLE)
                .localization(Localization.EN)
                .value(FakerUtils.title)
                .build();
    }

    @Test
    @UserStoryId(storyId = 3460)
    void shouldGetBonusTypes_IsHiddenField() {
        val actualTypes = BonusTypesUtils.getBonusTypes();
        for (BonusTypeResponseModel typeResult : actualTypes.getBonusTypes()) {
            if (EXPECTED_BONUS_TYPES.trim().contains(typeResult.getType())) {
                assertTrue(typeResult.getIsHidden());
            }
        }
    }

    @Test
    void shouldReceiveRewardsForSignUpIfCampaignExistsWithWait() {
        createCampaign(campaign, bonusType, earnRule)
                .then()
                .assertThat()
                .statusCode(SC_OK);

        val customerId = registerDefaultVerifiedCustomer().getCustomerId();

        Awaitility.await().atMost(AWAITILITY_INITIAL_BALANCE_WAIT_SECONDS, TimeUnit.SECONDS)
                .until(() -> getCustomerBalanceForDefaultAsset(customerId) == (CAMPAIGN_REWARD + CONDITION_REWARD));
    }

    @Test
    @UserStoryId(storyId = 860)
    void shouldBeAbleToCreateCampaignWithInfiniteConditionCount() {
        Campaign campaignWithInfiniteConditionCount =
                baseCampaign
                        .reward(CAMPAIGN_REWARD.toString())
                        .toDate(CAMPAIGN_TO)
                        .conditions(createConditionArray(bonusType))
                        .build();

        createCampaignWithInfiniteCompletionCount(campaignWithInfiniteConditionCount, bonusType, earnRule)
                .then()
                .assertThat()
                .statusCode(SC_OK);
    }

    @Test
    @Tag(SMOKE_TEST)
    @UserStoryId(storyId = 1147)
    void shouldGetImmediateRewardEveryTimeConditionIsMet() {
        val partnerData = createPartner(generateRandomString(10));
        final Integer immediateReward = 145;
        final Integer reward = 622;
        val condition = baseCondition
                .type(ConditionType.HOTEL_STAY.getValue())
                .immediateReward(immediateReward.toString())
                .completionCount(2)
                .hasStaking(false)
                .partnerIds(new String[]{partnerData.getId()})
                .stakingPeriod(DEFAULT_STAKING_PERIOD)
                .stakeWarningPeriod(DEFAULT_STAKE_WARNING_PERIOD)
                .build();
        val campaign = baseCampaign
                .name("Hotel Stay Campaign")
                .reward(reward.toString())
                .completionCount(2)
                .amountInTokens(Double.valueOf(100_000.0).toString())
                .amountInCurrency(20000)
                .usePartnerCurrencyRate(false)
                .rewardType(RewardType.FIXED)
                .build();

        createCampaign(campaign, condition, earnRule)
                .then()
                .assertThat()
                .statusCode(SC_OK);

        val customerId = registerDefaultVerifiedCustomer().getCustomerId();

        val initialBalance = getCustomerBalanceForDefaultAsset(customerId);

        simulateTrigger(SimulateTriggerRequest
                .builder()
                .customerId(customerId)
                .partnerId(partnerData.getId())
                .conditionType(ConditionType.HOTEL_STAY.getValue())
                .build(), new HashMap<String, String>());

        ////xx
        Awaitility.await().atMost(AWAITILITY_INITIAL_BALANCE_WAIT_SECONDS, TimeUnit.SECONDS)
                .until(() -> getCustomerBalanceForDefaultAsset(customerId) == initialBalance + immediateReward);

        simulateTrigger(SimulateTriggerRequest
                .builder()
                .customerId(customerId)
                .partnerId(partnerData.getId())
                .conditionType(ConditionType.HOTEL_STAY.getValue())
                .build(), new HashMap<String, String>());
        Awaitility.await().atMost(AWAITILITY_INITIAL_BALANCE_WAIT_SECONDS, TimeUnit.SECONDS)
                .until(() -> getCustomerBalanceForDefaultAsset(customerId)
                        == initialBalance + 2 * immediateReward + reward);

        simulateTrigger(SimulateTriggerRequest
                .builder()
                .customerId(customerId)
                .partnerId(partnerData.getId())
                .conditionType(ConditionType.HOTEL_STAY.getValue())
                .build(), new HashMap<String, String>());
        Awaitility.await().atMost(AWAITILITY_INITIAL_BALANCE_WAIT_SECONDS, TimeUnit.SECONDS)
                .until(() -> getCustomerBalanceForDefaultAsset(customerId)
                        == initialBalance + 3 * immediateReward + reward);

        simulateTrigger(SimulateTriggerRequest
                .builder()
                .customerId(customerId)
                .partnerId(partnerData.getId())
                .conditionType(ConditionType.HOTEL_STAY.getValue())
                .build(), new HashMap<String, String>());
        Awaitility.await().atMost(AWAITILITY_INITIAL_BALANCE_WAIT_SECONDS, TimeUnit.SECONDS)
                .until(() -> getCustomerBalanceForDefaultAsset(customerId)
                        == initialBalance + 4 * immediateReward + 2 * reward);

        assertEquals(initialBalance + 4 * immediateReward + 2 * reward, getCustomerBalanceForDefaultAsset(customerId));
    }

    @Test
    @Tag(SMOKE_TEST)
    @UserStoryId(storyId = {1300, 3353, 3354, 3257})
    void shouldGetImmediateRewardEveryTimeConditionIsMet_MultipleConditions() {
        val partnerData = createPartner(generateRandomString(10));
        final Integer immediateReward_condition_1 = 100;
        final Integer immediateReward_condition_2 = 200;
        final Integer campaignReward = 1024;
        val condition1 = baseCondition
                .type(ConditionType.ESTATE_LEAD_REFERRAL.getValue())
                .immediateReward(immediateReward_condition_1.toString())
                .completionCount(1)
                .hasStaking(true)
                .partnerIds(new String[]{partnerData.getId()})
                .stakingPeriod(DEFAULT_STAKING_PERIOD)
                .stakeWarningPeriod(DEFAULT_STAKE_WARNING_PERIOD)
                .burningRule(10.0)
                .stakingRule(2.0)
                .build();
        val condition2 = baseCondition
                .type(ConditionType.HOTEL_STAY_REFERRAL.getValue())
                .immediateReward(immediateReward_condition_2.toString())
                .completionCount(1)
                .hasStaking(true)
                .partnerIds(new String[]{partnerData.getId()})
                .stakingPeriod(DEFAULT_STAKING_PERIOD)
                .stakeWarningPeriod(DEFAULT_STAKE_WARNING_PERIOD)
                .burningRule(10.0)
                .stakingRule(2.0)
                .build();
        val campaign = baseCampaign.name("Test Campaign")
                .reward(String.valueOf(campaignReward))
                .completionCount(1)
                .amountInTokens(Double.valueOf(100_000.0).toString())
                .amountInCurrency(20000)
                .usePartnerCurrencyRate(false)
                .rewardType(RewardType.FIXED)
                .build();

        val campaignId = createCampaign(campaign, condition1, condition2, earnRule)
                .then()

                ////xx
                ////55
                .log().all()

                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .jsonPath()
                .getString(CAMPAIGN_ID_FIELD);

        val customerId = registerDefaultVerifiedCustomer().getCustomerId();

        simulateTrigger(SimulateTriggerRequest
                .builder()
                .customerId(customerId)
                .partnerId(partnerData.getId())
                .conditionType(ConditionType.ESTATE_LEAD_REFERRAL.getValue())
                .build(), singletonMap(STAKED_CAMPAIGN_ID_KEY, campaignId));

        Awaitility.await().atMost(AWAITILITY_INITIAL_BALANCE_WAIT_SECONDS, TimeUnit.SECONDS)
                .until(() -> getCustomerBalanceForDefaultAsset(customerId) >= immediateReward_condition_1);

        val initialBalance = getCustomerBalanceForDefaultAsset(customerId);

        simulateTrigger(SimulateTriggerRequest
                .builder()
                .customerId(customerId)
                .partnerId(partnerData.getId())
                .conditionType(ConditionType.HOTEL_STAY_REFERRAL.getValue())
                .build(), singletonMap(STAKED_CAMPAIGN_ID_KEY, campaignId));

        Awaitility.await().atMost(AWAITILITY_INITIAL_BALANCE_WAIT_SECONDS, TimeUnit.SECONDS)
                .until(() -> initialBalance + immediateReward_condition_2 + campaignReward
                        == getCustomerBalanceForDefaultAsset(customerId));

        assertEquals(initialBalance + immediateReward_condition_2 + campaignReward,
                getCustomerBalanceForDefaultAsset(customerId));

        // waiting for operations history to have the transfer in the output
        Awaitility.await()
                .atMost(AWAITILITY_OPERATIONS_HISTORY_SEC, TimeUnit.SECONDS)
                .pollInterval(AWAITILITY_POLL_INTERVAL_MID_SEC, TimeUnit.SECONDS)
                .until(() -> 0 < getTransactionsByCustomerId(customerId)
                        .then()
                        .assertThat()
                        .statusCode(SC_OK)
                        .extract()
                        .as(PaginatedCustomerOperationsResponse.class)
                        .getBonusCashIns().length);

        // checking operations history
        val transactions = getTransactionsByCustomerId(customerId)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(PaginatedCustomerOperationsResponse.class);

        assertEquals(getCustomerBalanceForDefaultAsset(customerId), Arrays.stream(transactions.getBonusCashIns())
                .map(tran -> Double.valueOf(tran.getAmount())).reduce(0.0, (a, b) -> a + b));
    }
}
