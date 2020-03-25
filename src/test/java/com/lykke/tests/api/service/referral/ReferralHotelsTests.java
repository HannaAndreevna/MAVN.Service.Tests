package com.lykke.tests.api.service.referral;

import static com.lykke.api.testing.api.common.GenerateUtils.generateRandomString;
import static com.lykke.tests.api.common.CommonConsts.AWAITILITY_INITIAL_BALANCE_WAIT_SECONDS;
import static com.lykke.tests.api.common.CommonConsts.AWAITILITY_OPERATIONS_HISTORY_SEC;
import static com.lykke.tests.api.common.CommonConsts.AWAITILITY_POLL_INTERVAL_MID_SEC;
import static com.lykke.tests.api.common.CommonConsts.ERROR_CODE_FIELD;
import static com.lykke.tests.api.common.CommonConsts.ERROR_CODE_NONE;
import static com.lykke.tests.api.common.CommonConsts.REFERRAL_CODE_FIELD;
import static com.lykke.tests.api.common.CommonConsts.SMOKE_TEST;
import static com.lykke.tests.api.common.CommonConsts.Staking.DEFAULT_STAKE_WARNING_PERIOD;
import static com.lykke.tests.api.common.CommonConsts.Staking.DEFAULT_STAKING_PERIOD;
import static com.lykke.tests.api.common.CommonMethods.getCustomerBalanceForDefaultAsset;
import static com.lykke.tests.api.common.HelperUtils.simulateTrigger;
import static com.lykke.tests.api.common.PaginationConts.CURRENT_PAGE_LOWER_BOUNDARY;
import static com.lykke.tests.api.common.PaginationConts.PAGE_SIZE_UPPER_BOUNDARY;
import static com.lykke.tests.api.service.campaigns.CampaignUtils.createCampaign;
import static com.lykke.tests.api.service.campaigns.CampaignUtils.createConditionArray;
import static com.lykke.tests.api.service.customer.CustomerReferralsUtils.getAllReferrals;
import static com.lykke.tests.api.service.customer.LoginLogoutCustomerUtils.getUserToken;
import static com.lykke.tests.api.service.customermanagement.RegisterCustomerUtils.registerDefaultVerifiedCustomer;
import static com.lykke.tests.api.service.operationshistory.OperationsUtils.getTransactionsByCustomerId;
import static com.lykke.tests.api.service.partnermanagement.PartnerManagementUtils.createPartner;
import static com.lykke.tests.api.service.referral.CommonReferralUtils.getCommonReferralByCustomerId;
import static com.lykke.tests.api.service.referral.CommonReferralUtils.getListOfCommonReferrals;
import static com.lykke.tests.api.service.referral.ReferralCodeUtils.setReferralCodeByCustomerId;
import static com.lykke.tests.api.service.referral.ReferralHotelsUtils.createReferralHotel;
import static java.util.Collections.singletonMap;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.lykke.api.testing.annotations.UserStoryId;
import com.lykke.api.testing.api.common.FakerUtils;
import com.lykke.tests.api.base.BaseApiTest;
import com.lykke.tests.api.common.HelperUtils.SimulateTriggerRequest;
import com.lykke.tests.api.common.enums.Localization;
import com.lykke.tests.api.common.enums.RuleContentType;
import com.lykke.tests.api.common.enums.campaign.ConditionType;
import com.lykke.tests.api.service.campaigns.model.Campaign;
import com.lykke.tests.api.service.campaigns.model.ConditionCreateModel;
import com.lykke.tests.api.service.campaigns.model.EarnRule;
import com.lykke.tests.api.service.campaigns.model.RatioAttribute;
import com.lykke.tests.api.service.campaigns.model.RewardRatioAttribute;
import com.lykke.tests.api.service.campaigns.model.RewardType;
import com.lykke.tests.api.service.customer.model.referral.CustomerCommonReferralResponseModel;
import com.lykke.tests.api.service.customer.model.referral.RatioAttributeModel;
import com.lykke.tests.api.service.customer.model.referral.RatioCompletion;
import com.lykke.tests.api.service.customer.model.referral.ReferralPaginationRequestModel;
import com.lykke.tests.api.service.customer.model.referral.ReferralsListResponseModel;
import com.lykke.tests.api.service.customer.model.referral.RewardRatioAttributeModel;
import com.lykke.tests.api.service.operationshistory.model.PaginatedCustomerOperationsResponse;
import com.lykke.tests.api.service.referral.model.common.CommonReferralByCustomerIdRequest;
import com.lykke.tests.api.service.referral.model.common.CommonReferralByCustomerIdResponse;
import com.lykke.tests.api.service.referral.model.common.CommonReferralByReferralIdsRequest;
import com.lykke.tests.api.service.referral.model.common.CommonReferralByReferralIdsResponse;
import com.lykke.tests.api.service.referral.model.common.CommonReferralStatus;
import com.lykke.tests.api.service.referral.model.common.ReferralType;
import com.lykke.tests.api.service.referral.model.referralhotel.ReferralHotelCreateRequest;
import com.lykke.tests.api.service.referral.model.referralhotel.ReferralHotelCreateResponse;
import java.time.Instant;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import lombok.val;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

public class ReferralHotelsTests extends BaseApiTest {

    // TODO: check the access level
    protected static final Integer CONDITION_REWARD = 100_000;
    protected static final int CONDITION_COMPLETION_COUNT = 1;
    protected static final String CAMPAIGN_NAME = generateRandomString();
    protected static final String CAMPAIGN_CREATED_BY = generateRandomString();
    protected static final String CAMPAIGN_FROM = Instant.now().toString();
    protected static final String CAMPAIGN_TO = "2025-05-23T06:59:26.627Z";
    protected static final String CAMPAIGN_DESC = generateRandomString();
    protected static final Integer CAMPAIGN_REWARD = 200_000;
    protected static final int CAMPAIGN_COMPLETION_COUNT = 1;
    protected static final RewardType REWARD_TYPE_FIXED = RewardType.FIXED;
    protected static final String CAMPAIGN_ID_FIELD = "CampaignId";
    protected static Campaign campaign;
    protected static ConditionCreateModel bonusType;
    protected static EarnRule earnRule;
    protected static String campaignId;
    protected static ConditionCreateModel.ConditionCreateModelBuilder baseCondition;
    protected static Campaign.CampaignBuilder baseCampaign;
    protected static EarnRule.EarnRuleBuilder baseEarnRule;
    private static final double REWARD = 100_000.0;

    private static final String STAKED_CAMPAIGN_ID_KEY = "StakedCampaignId";
    private static final String EXPECTED_BONUS_TYPES = "commission-one-referral, commission-two-referral, estate-otp-referral";

    @BeforeEach
    void dataSetup() {
        // TODO: deletion of campaigns
        // deleteAllCampaigns();

        baseCondition = ConditionCreateModel
                .conditionCreateBuilder()
                .immediateReward(CONDITION_REWARD.toString())
                .completionCount(CONDITION_COMPLETION_COUNT);

        baseCampaign = Campaign
                .campaignBuilder()
                .name(CAMPAIGN_NAME)
                .fromDate(CAMPAIGN_FROM)
                .toDate(CAMPAIGN_TO)
                .description(CAMPAIGN_DESC)
                .createdBy(CAMPAIGN_CREATED_BY)
                .completionCount(CAMPAIGN_COMPLETION_COUNT);

        baseEarnRule = EarnRule
                .builder()
                .ruleContentType(RuleContentType.TITLE)
                .localization(Localization.EN)
                .value(FakerUtils.title);

        bonusType = baseCondition
                .type(ConditionType.SIGNUP.getValue())
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
    @Tag(SMOKE_TEST)
    @UserStoryId(storyId = {3352, 3733, 3993, 3974, 4061, 4112})
    void shouldProcessCampaignWithStaking() {
        val partnerData = createPartner(generateRandomString(10));
        final Integer immediateReward_condition_1 = 100;
        final Integer immediateReward_condition_2 = CONDITION_REWARD;
        final Integer campaignReward = CAMPAIGN_REWARD;
        final Double rewardRatio01 = 3.0;
        final Double paymentRatio01 = 7.0;
        final Integer ratioOrder01 = 11;
        final Double rewardRatio02 = 2.0;
        final Double paymentRatio02 = 8.0;
        final Integer ratioOrder02 = 2;
        val condition1 = baseCondition
                .type(ConditionType.SIGNUP.getValue())
                .immediateReward(immediateReward_condition_1.toString())
                .completionCount(1)
                .hasStaking(false)
                .partnerIds(new String[]{partnerData.getId()})
                .build();
        val condition2 = baseCondition
                .type(ConditionType.HOTEL_STAY_REFERRAL.getValue())
                .immediateReward(immediateReward_condition_2.toString())
                .completionCount(10)
                .hasStaking(true)
                .partnerIds(new String[]{partnerData.getId()})
                .stakingPeriod(DEFAULT_STAKING_PERIOD + DEFAULT_STAKING_PERIOD)
                .stakeWarningPeriod(DEFAULT_STAKE_WARNING_PERIOD)
                .burningRule(19.9)
                .stakingRule(20.0)
                .rewardRatio(RewardRatioAttribute
                        .builder()
                        .ratios(new RatioAttribute[]{
                                RatioAttribute
                                        .builder()
                                        .order(ratioOrder01)
                                        .paymentRatio(paymentRatio01)
                                        .rewardRatio(rewardRatio01)
                                        .build(),
                                RatioAttribute
                                        .builder()
                                        .order(ratioOrder02)
                                        .paymentRatio(paymentRatio02)
                                        .rewardRatio(rewardRatio02)
                                        .build()
                        })
                        .build())
                .build();
        val campaign = baseCampaign.name("Test Campaign")
                .reward(String.valueOf(campaignReward))
                .completionCount(1)
                .amountInTokens(Double.valueOf(CAMPAIGN_REWARD * 5).toString())
                .amountInCurrency(CAMPAIGN_REWARD)
                .usePartnerCurrencyRate(false)
                .rewardType(RewardType.FIXED)
                .build();

        val campaignId = // createCampaign(campaign, condition1, condition2, earnRule)
                createCampaign(campaign, condition2, earnRule)
                        .then()
                        .assertThat()
                        .statusCode(SC_OK)
                        .extract()
                        .jsonPath()
                        .getString(CAMPAIGN_ID_FIELD);

        val customerData = registerDefaultVerifiedCustomer();
        val customerId = customerData.getCustomerId();

        val initialBalance = getCustomerBalanceForDefaultAsset(customerId);

        val referralData = registerDefaultVerifiedCustomer();

        val result = createReferralHotel(ReferralHotelCreateRequest
                .builder()
                .campaignId(campaignId)
                .email(referralData.getEmail())
                .referrerId(customerData.getCustomerId())
                .phoneNumber(referralData.getPhoneNumber())
                .phoneCountryCodeId(referralData.getCountryPhoneCodeId())
                .fullName(referralData.getFullName())
                .build())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(ReferralHotelCreateResponse.class);

        // this also works
        /*
        postReferralHotel(HotelReferralRequestModel
                .builder()
                .campaignId(campaignId)
                .countryPhoneCodeId(referralData.getCountryPhoneCodeId())
                .email(referralData.getEmail())
                .fullName(referralData.getFullName())
                .phoneNumber(referralData.getPhoneNumber())
                .build(), getUserToken(customerData))
                .then()
                .assertThat()
                .statusCode(SC_NO_CONTENT);
        */

        simulateTrigger(SimulateTriggerRequest
                .builder()
                .customerId(customerId)
                .partnerId(partnerData.getId())
                .conditionType(ConditionType.HOTEL_STAY_REFERRAL.getValue())
                .build(), singletonMap(STAKED_CAMPAIGN_ID_KEY, campaignId));

        // waiting for operations history to have the transfer in the output
        Awaitility.await()
                .atMost(AWAITILITY_OPERATIONS_HISTORY_SEC, TimeUnit.SECONDS)
                .pollInterval(AWAITILITY_POLL_INTERVAL_MID_SEC, TimeUnit.SECONDS)
                .until(() -> {
                    val bonusCachInCollection = getTransactionsByCustomerId(customerId)
                            .then()
                            .assertThat()
                            .statusCode(SC_OK)
                            .extract()
                            .as(PaginatedCustomerOperationsResponse.class)
                            .getBonusCashIns();
                    return 0 < bonusCachInCollection.length && getCustomerBalanceForDefaultAsset(customerId) == Arrays
                            .stream(bonusCachInCollection)
                            .map(tran -> Double.valueOf(tran.getAmount())).reduce(0.0, (a, b) -> a + b);
                });

        // checking operations history
        val transactions = getTransactionsByCustomerId(customerId)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(PaginatedCustomerOperationsResponse.class);

        assertEquals(getCustomerBalanceForDefaultAsset(customerId), Arrays.stream(transactions.getBonusCashIns())
                .map(tran -> Double.valueOf(tran.getAmount())).reduce(0.0, (a, b) -> a + b));

        // FAL-3733, FAL-3794
        val commonReferralResult = getCommonReferralByCustomerId(
                CommonReferralByCustomerIdRequest
                        .builder()
                        .customerId(customerId)
                        .campaignId(campaignId)
                        .statuses(new CommonReferralStatus[]{CommonReferralStatus.PENDING})
                        .build())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(CommonReferralByCustomerIdResponse.class)
                .getReferrals()[0];

        assertAll(
                () -> assertEquals(campaignId, commonReferralResult.getCampaignId()),
                () -> assertEquals(partnerData.getId(), commonReferralResult.getPartnerId())
        );

        getListOfCommonReferrals(CommonReferralByReferralIdsRequest
                .builder()
                .referralIds(new String[]{referralData.getCustomerId()})
                .build())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(CommonReferralByReferralIdsResponse.class);

        // FAL-3974, FAL-3993, FAL-4061, FAL-4112
        val expectedResult = ReferralsListResponseModel
                .builder()
                .currentPage(CURRENT_PAGE_LOWER_BOUNDARY)
                .pageSize(PAGE_SIZE_UPPER_BOUNDARY)
                .totalCount(1)
                .referrals(new CustomerCommonReferralResponseModel[]{
                        CustomerCommonReferralResponseModel
                                .builder()
                                .referralType(ReferralType.HOSPITALITY)
                                .status(com.lykke.tests.api.service.customer.model.referral.CommonReferralStatus.ONGOING)
                                .firstName(customerData.getFirstName())
                                .lastName(customerData.getLastName())
                                .partnerName(partnerData.getName())
                                .hasStaking(false)
                                .totalReward(CAMPAIGN_REWARD.toString())
                                .currentRewardedAmount(Double.valueOf(0).toString())
                                .rewardHasRatio(true)
                                .rewardRatio(RewardRatioAttributeModel
                                        .builder()
                                        .ratios(new RatioAttributeModel[]{
                                                RatioAttributeModel
                                                        .builder()
                                                        .order(ratioOrder01)
                                                        .rewardRatio(rewardRatio01)
                                                        .paymentRatio(paymentRatio01)
                                                        .threshold(paymentRatio01 + paymentRatio02)
                                                        .build(),
                                                RatioAttributeModel
                                                        .builder()
                                                        .order(ratioOrder02)
                                                        .rewardRatio(rewardRatio02)
                                                        .paymentRatio(paymentRatio02)
                                                        .threshold(paymentRatio02)
                                                        .build()})
                                        .ratioCompletion(new RatioCompletion[]{})
                                        .build())
                                .build()
                })
                .build();

        val actualResult = getAllReferrals(ReferralPaginationRequestModel
                .referralPaginationRequestModelBuilder()
                .status(com.lykke.tests.api.service.customer.model.referral.CommonReferralStatus.ONGOING)
                .currentPage(CURRENT_PAGE_LOWER_BOUNDARY)
                .pageSize(PAGE_SIZE_UPPER_BOUNDARY)
                .build(), getUserToken(customerData))
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(ReferralsListResponseModel.class);

        assertEquals(expectedResult, actualResult);
    }

    @Test
    @Tag(SMOKE_TEST)
    @UserStoryId(3352)
    void shouldProcessCampaignWithNoStaking() {
        val partnerData = createPartner(generateRandomString(10));
        final Integer immediateReward_condition_1 = 100;
        final Integer immediateReward_condition_2 = 200;
        final Integer campaignReward = 1024;
        val condition1 = baseCondition
                .type(ConditionType.ESTATE_LEAD_REFERRAL.getValue())
                .immediateReward(immediateReward_condition_1.toString())
                .completionCount(1)
                .hasStaking(false)
                .partnerIds(new String[]{partnerData.getId()})
                .stakingPeriod(DEFAULT_STAKING_PERIOD)
                .stakeWarningPeriod(DEFAULT_STAKE_WARNING_PERIOD)
                .build();
        val condition2 = baseCondition
                .type(ConditionType.HOTEL_STAY_REFERRAL.getValue())
                .immediateReward(immediateReward_condition_2.toString())
                .completionCount(1)
                .hasStaking(false)
                .partnerIds(new String[]{partnerData.getId()})
                .stakingPeriod(DEFAULT_STAKING_PERIOD)
                .stakeWarningPeriod(DEFAULT_STAKE_WARNING_PERIOD)
                .build();
        val campaign = baseCampaign.name("Test Campaign")
                .reward(String.valueOf(campaignReward))
                .completionCount(1)
                .amountInTokens(Double.valueOf(REWARD).toString())
                .amountInCurrency(20000)
                .usePartnerCurrencyRate(false)
                .rewardType(RewardType.FIXED)
                .build();

        val campaignId = createCampaign(campaign, condition1, condition2, earnRule)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .jsonPath()
                .getString(CAMPAIGN_ID_FIELD);

        val customerData = registerDefaultVerifiedCustomer();
        val customerId = customerData.getCustomerId();

        simulateTrigger(SimulateTriggerRequest
                .builder()
                .customerId(customerId)
                .partnerId(partnerData.getId())
                .conditionType(ConditionType.ESTATE_LEAD_REFERRAL.getValue())
                .build(), singletonMap(STAKED_CAMPAIGN_ID_KEY, campaignId));

        Awaitility.await().atMost(AWAITILITY_INITIAL_BALANCE_WAIT_SECONDS, TimeUnit.SECONDS)
                .until(() -> getCustomerBalanceForDefaultAsset(customerId) >= immediateReward_condition_1);

        val initialBalance = getCustomerBalanceForDefaultAsset(customerId);

        val referralData = registerDefaultVerifiedCustomer();

        val result = createReferralHotel(ReferralHotelCreateRequest
                .builder()
                .campaignId(campaignId)
                .email(customerData.getEmail())
                .referrerId(referralData.getCustomerId())
                .fullName(customerData.getFirstName() + " " + customerData.getLastName())
                .phoneCountryCodeId(customerData.getCountryPhoneCodeId())
                .phoneNumber(customerData.getPhoneNumber())
                .build())
                .then()

                ////xx
                ////55
                .log().all()

                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(ReferralHotelCreateResponse.class);

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

    @Test
    @Tag(SMOKE_TEST)
    @UserStoryId(3352)
    void shouldProcessNoCampaignIdProvided() {
        val partnerData = createPartner(generateRandomString(10));
        final Integer immediateReward_condition_1 = 100;
        final Integer immediateReward_condition_2 = 200;
        final Integer campaignReward = 1024;
        val condition1 = baseCondition
                .type(ConditionType.ESTATE_LEAD_REFERRAL.getValue())
                .immediateReward(immediateReward_condition_1.toString())
                .completionCount(1)
                .hasStaking(false)
                .partnerIds(new String[]{partnerData.getId()})
                .stakingPeriod(DEFAULT_STAKING_PERIOD)
                .stakeWarningPeriod(DEFAULT_STAKE_WARNING_PERIOD)
                .build();
        val condition2 = baseCondition
                .type(ConditionType.HOTEL_STAY_REFERRAL.getValue())
                .immediateReward(immediateReward_condition_2.toString())
                .completionCount(1)
                .hasStaking(false)
                .partnerIds(new String[]{partnerData.getId()})
                .stakingPeriod(DEFAULT_STAKING_PERIOD)
                .stakeWarningPeriod(DEFAULT_STAKE_WARNING_PERIOD)
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
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .jsonPath()
                .getString(CAMPAIGN_ID_FIELD);

        val customerData = registerDefaultVerifiedCustomer();
        val customerId = customerData.getCustomerId();

        simulateTrigger(SimulateTriggerRequest
                .builder()
                .customerId(customerId)
                .partnerId(partnerData.getId())
                .conditionType(ConditionType.ESTATE_LEAD_REFERRAL.getValue())
                .build(), singletonMap(STAKED_CAMPAIGN_ID_KEY, campaignId));

        Awaitility.await().atMost(AWAITILITY_INITIAL_BALANCE_WAIT_SECONDS, TimeUnit.SECONDS)
                .until(() -> getCustomerBalanceForDefaultAsset(customerId) >= immediateReward_condition_1);

        val initialBalance = getCustomerBalanceForDefaultAsset(customerId);

        val referralData = registerDefaultVerifiedCustomer();

        val result = createReferralHotel(ReferralHotelCreateRequest
                .builder()
                .email(customerData.getEmail())
                .referrerId(referralData.getCustomerId())
                .fullName(customerData.getFirstName() + " " + customerData.getLastName())
                .phoneCountryCodeId(customerData.getCountryPhoneCodeId())
                .phoneNumber(customerData.getPhoneNumber())
                .build())
                .then()

                ////xx
                ////55
                .log().all()

                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(ReferralHotelCreateResponse.class);

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

    @Test
    @Tag(SMOKE_TEST)
    @UserStoryId(3352)
    void shouldNotProcessCampaignIfCustomerHasNotEnoughTokens() {
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
        ////xx

        val campaignId = createCampaign(campaign, condition1, condition2, earnRule)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .jsonPath()
                .getString(CAMPAIGN_ID_FIELD);

        val customerData = registerDefaultVerifiedCustomer();
        val customerId = customerData.getCustomerId();

        // TODO: decide are these lines needed or not
        /*
        simulateTrigger(SimulateTriggerRequest
                .builder()
                .customerId(customerId)
                .partnerId(partnerData.getId())
                .conditionType(ConditionType.ESTATE_LEAD_REFERRAL.getValue())
                .build(), singletonMap(STAKED_CAMPAIGN_ID_KEY, campaignId));

        Awaitility.await().atMost(AWAITILITY_INITIAL_BALANCE_WAIT_SECONDS, TimeUnit.SECONDS)
                .until(() -> getCustomerBalanceForDefaultAsset(customerId) >= immediateReward_condition_1);
*/
        val initialBalance = getCustomerBalanceForDefaultAsset(customerId);

        val referralData = registerDefaultVerifiedCustomer();

        val result = createReferralHotel(ReferralHotelCreateRequest
                .builder()
                .campaignId(campaignId)
                .email(customerData.getEmail())
                .referrerId(referralData.getCustomerId())
                .build())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(ReferralHotelCreateResponse.class);

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

    // TODO: deletion of campaigns
    /*
    public static void deleteAllCampaigns() {
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
    }*/

    protected static String createCampaignAndGetId() {
        return createCampaign(campaign, bonusType, earnRule)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .body(ERROR_CODE_FIELD, equalTo(ERROR_CODE_NONE))
                .extract()
                .path(CAMPAIGN_ID_FIELD);
    }

    protected static String getReferralCode(String customerId) {
        return setReferralCodeByCustomerId(customerId)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .path(REFERRAL_CODE_FIELD);
    }
}
