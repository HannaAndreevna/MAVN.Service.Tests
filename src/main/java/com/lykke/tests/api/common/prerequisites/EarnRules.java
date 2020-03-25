package com.lykke.tests.api.common.prerequisites;

import static com.lykke.api.testing.api.common.GenerateUtils.generateRandomString;
import static com.lykke.tests.api.common.CommonConsts.Staking.DEFAULT_STAKE_WARNING_PERIOD;
import static com.lykke.tests.api.common.CommonConsts.Staking.DEFAULT_STAKING_PERIOD;
import static com.lykke.tests.api.service.admin.CampaignUtils.createConditionArray;
import static com.lykke.tests.api.service.campaigns.CampaignUtils.addEarnRuleImage;
import static com.lykke.tests.api.service.campaigns.CampaignUtils.createEarnRule;
import static com.lykke.tests.api.service.campaigns.CampaignUtils.getCampaignById;
import static com.lykke.tests.api.service.campaigns.CampaignUtils.getEarnRuleContentIdByContentType;
import static org.apache.http.HttpStatus.SC_OK;

import com.lykke.api.testing.api.common.Base64Utils;
import com.lykke.api.testing.api.common.FakerUtils;
import com.lykke.tests.api.common.HelperUtils;
import com.lykke.tests.api.common.enums.Localization;
import com.lykke.tests.api.common.enums.PictureContentType;
import com.lykke.tests.api.common.enums.RuleContentType;
import com.lykke.tests.api.common.enums.campaign.ConditionType;
import com.lykke.tests.api.service.campaigns.model.Campaign;
import com.lykke.tests.api.service.campaigns.model.ConditionCreateModel;
import com.lykke.tests.api.service.campaigns.model.EarnRule;
import com.lykke.tests.api.service.campaigns.model.RewardRatioAttribute;
import com.lykke.tests.api.service.campaigns.model.RewardType;
import com.lykke.tests.api.service.campaigns.model.earnrules.FileCreateRequestModel;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import lombok.val;

public class EarnRules {

    public static final String AMOUNT_IN_TOKENS_100_000 = "100000";
    protected static final String CAMPAIGN_ID_FIELD = "CampaignId";
    private static final String CONDITION_TYPE_SIGNUP = ConditionType.SIGNUP.getValue();
    private static final String CONDITION_TYPE_HOTEL_STAY_REFERRAL = ConditionType.HOTEL_STAY_REFERRAL.getValue();
    private static final String CONDITION_TYPE_REFERRAL = "referral";
    private static final Integer CONDITION_REWARD = 120;
    private static final int CONDITION_COMPLETION_COUNT = 1;
    private static final String CAMPAIGN_NAME = generateRandomString();
    private static final String CAMPAIGN_CREATED_BY = generateRandomString();
    private static final String CAMPAIGN_FROM = Instant.now().toString();
    private static final String CAMPAIGN_TO = "2025-05-23T06:59:26.627Z";
    private static final String CAMPAIGN_DESC = generateRandomString();
    private static final Integer CAMPAIGN_REWARD = 500;
    private static final int CAMPAIGN_COMPLETION_COUNT = 100;
    private static final RewardType REWARD_TYPE_FIXED = RewardType.FIXED;
    private static final RewardType REWARD_TYPE_PERCENTAGE = RewardType.PERCENTAGE;
    private static final RuleContentType RULE_CONTENT_TYPE_TITLE = RuleContentType.TITLE;
    private static final RuleContentType RULE_CONTENT_TYPE_DESCRIPTION = RuleContentType.DESCRIPTION;
    private static final RuleContentType RULE_CONTENT_TYPE_URL_FOR_PICTURE = RuleContentType.URL_FOR_PICTURE;
    private static final Localization LOCALIZATION_EN = Localization.EN;
    private static final Localization LOCALIZATION_AR = Localization.AR;
    private static final String EARN_RULE_CONTENT_TITLE_EN_VALUE = "ENGLISH TITLE";
    private static final String EARN_RULE_CONTENT_TITLE_AR_VALUE = "ARABIC TITLE";
    private static final String EARN_RULE_CONTENT_DESCRIPTION_EN_VALUE = "ENGLISH DESCRIPTION";
    private static final String EARN_RULE_CONTENT_DESCRIPTION_AR_VALUE = "ARABIC DESCRIPTION";
    private static final int AMOUNT_IN_CURRENCY_10_000 = 10000;
    static ConditionCreateModel condition = ConditionCreateModel
            .conditionCreateBuilder()
            .type(CONDITION_TYPE_SIGNUP)
            .immediateReward(CONDITION_REWARD.toString())
            .completionCount(CONDITION_COMPLETION_COUNT)
            .stakingPeriod(DEFAULT_STAKING_PERIOD)
            .stakeWarningPeriod(DEFAULT_STAKE_WARNING_PERIOD)
            .rewardRatio(RewardRatioAttribute
                    .builder()
                    .build())
            .build();
    static ConditionCreateModel conditionWithStaking = ConditionCreateModel
            .conditionCreateBuilder()
            .type(CONDITION_TYPE_HOTEL_STAY_REFERRAL)
            .immediateReward(CONDITION_REWARD.toString())
            .completionCount(CONDITION_COMPLETION_COUNT)
            .stakingPeriod(DEFAULT_STAKING_PERIOD)
            .stakeWarningPeriod(DEFAULT_STAKE_WARNING_PERIOD)
            .stakingRule(100.0)
            .burningRule(100.0)
            .stakeAmount(Double.valueOf(100).toString())
            .hasStaking(true)
            .rewardRatio(RewardRatioAttribute
                    .builder()
                    .build())
            .build();
    static EarnRule earnRuleContentTitleEn = EarnRule
            .builder()
            .ruleContentType(RULE_CONTENT_TYPE_TITLE)
            .localization(LOCALIZATION_EN)
            .value(EARN_RULE_CONTENT_TITLE_EN_VALUE)
            .build();
    static EarnRule earnRuleContentTitleAr = EarnRule
            .builder()
            .ruleContentType(RULE_CONTENT_TYPE_TITLE)
            .localization(LOCALIZATION_AR)
            .value(EARN_RULE_CONTENT_TITLE_AR_VALUE)
            .build();
    static EarnRule earnRuleContentDescriptionEn = EarnRule
            .builder()
            .ruleContentType(RULE_CONTENT_TYPE_DESCRIPTION)
            .localization(LOCALIZATION_EN)
            .value(EARN_RULE_CONTENT_DESCRIPTION_EN_VALUE)
            .build();
    static EarnRule earnRuleContentDescriptionAr = EarnRule
            .builder()
            .ruleContentType(RULE_CONTENT_TYPE_DESCRIPTION)
            .localization(LOCALIZATION_AR)
            .value(EARN_RULE_CONTENT_DESCRIPTION_AR_VALUE)
            .build();
    static EarnRule earnRuleContentUrlForPictureEn = EarnRule
            .builder()
            .ruleContentType(RULE_CONTENT_TYPE_URL_FOR_PICTURE)
            .localization(LOCALIZATION_EN)
            .value("")
            .build();
    static EarnRule earnRuleContentUrlForPictureAr = EarnRule
            .builder()
            .ruleContentType(RULE_CONTENT_TYPE_URL_FOR_PICTURE)
            .localization(LOCALIZATION_AR)
            .value("")
            .build();
    private static String content = Base64Utils.encodeToString(HelperUtils.getImagePath("test_image.jpg"));
    static ConditionCreateModel condition2 = ConditionCreateModel
            .conditionCreateBuilder()
            .type(CONDITION_TYPE_SIGNUP)
            .immediateReward(Double.valueOf(90).toString())
            .completionCount(CONDITION_COMPLETION_COUNT)
            .rewardType(RewardType.PERCENTAGE)
            .approximateAward(Double.valueOf(50).toString())
            .stakingPeriod(DEFAULT_STAKING_PERIOD)
            .stakeWarningPeriod(DEFAULT_STAKE_WARNING_PERIOD)
            .rewardRatio(RewardRatioAttribute
                    .builder()
                    .build())
            .build();

    public static String createBasicSignUpEarnRule() {
        String earnRuleId;
        val condition = com.lykke.tests.api.service.campaigns.model.ConditionCreateModel
                .conditionCreateBuilder()
                .type(CONDITION_TYPE_SIGNUP)
                .immediateReward(CONDITION_REWARD.toString())
                .completionCount(1)
                .hasStaking(true)
                .stakeAmount("1.0")
                .stakingPeriod(DEFAULT_STAKING_PERIOD)
                .stakeWarningPeriod(DEFAULT_STAKE_WARNING_PERIOD)
                .burningRule(10.0)
                .stakingRule(2.0)
                .build();

        val earnRuleContentObject = new EarnRule[]{
                earnRuleContentTitleEn
        };

        val earnRuleCreateObj = Campaign
                .campaignBuilder()
                .name("Basic SignUp Earn Rule")
                .reward(CAMPAIGN_REWARD.toString())
                .fromDate(CAMPAIGN_FROM)
                .toDate(CAMPAIGN_TO)
                .description(CAMPAIGN_DESC)
                .conditions(createConditionArray(condition))
                .createdBy(CAMPAIGN_CREATED_BY)
                .rewardType(REWARD_TYPE_FIXED)
                .contents(earnRuleContentObject)
                .completionCount(1)
                .build();

        earnRuleId = createEarnRule(earnRuleCreateObj)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .path(CAMPAIGN_ID_FIELD);

        return earnRuleId;
    }

    public static String createEarnRuleWithAllContentTypes(Boolean image) {
        String earnRuleId;

        val earnRuleContentObject = new EarnRule[]{
                earnRuleContentTitleEn, earnRuleContentTitleAr, earnRuleContentDescriptionEn,
                earnRuleContentDescriptionAr, earnRuleContentUrlForPictureEn, earnRuleContentUrlForPictureAr
        };

        val earnRuleCreateObj = Campaign
                .campaignBuilder()
                .name(CAMPAIGN_NAME)
                .reward(CAMPAIGN_REWARD.toString())
                .fromDate(CAMPAIGN_FROM)
                .toDate(CAMPAIGN_TO)
                .description(CAMPAIGN_DESC)
                .conditions(createConditionArray(condition))
                .createdBy(CAMPAIGN_CREATED_BY)
                .rewardType(REWARD_TYPE_FIXED)
                .contents(earnRuleContentObject)
                .completionCount(CAMPAIGN_COMPLETION_COUNT)
                .amountInTokens(AMOUNT_IN_TOKENS_100_000)
                .amountInCurrency(AMOUNT_IN_CURRENCY_10_000)
                .usePartnerCurrencyRate(false)
                .order(88)
                .build();

        earnRuleId = createEarnRule(earnRuleCreateObj)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .path(CAMPAIGN_ID_FIELD);

        if (image) {
            addImage(earnRuleId, LOCALIZATION_EN);
            addImage(earnRuleId, LOCALIZATION_AR);
        }

        return earnRuleId;
    }

    public static String createEarnRuleWithAllContentTypesAndPercentageReward(Boolean image) {
        String earnRuleId;

        val earnRuleContentObject = new EarnRule[]{
                earnRuleContentTitleEn, earnRuleContentTitleAr, earnRuleContentDescriptionEn,
                earnRuleContentDescriptionAr, earnRuleContentUrlForPictureEn, earnRuleContentUrlForPictureAr
        };

        val earnRuleCreateObj = Campaign
                .campaignBuilder()
                .name(CAMPAIGN_NAME)
                .reward(Double.valueOf(90).toString())
                .fromDate(CAMPAIGN_FROM)
                .toDate(CAMPAIGN_TO)
                .description(CAMPAIGN_DESC)
                .conditions(createConditionArray(condition2))
                .createdBy(CAMPAIGN_CREATED_BY)
                .rewardType(REWARD_TYPE_PERCENTAGE)
                .contents(earnRuleContentObject)
                .completionCount(CAMPAIGN_COMPLETION_COUNT)
                .amountInTokens(AMOUNT_IN_TOKENS_100_000)
                .amountInCurrency(AMOUNT_IN_CURRENCY_10_000)
                .approximateAward(Double.valueOf(50).toString())
                .usePartnerCurrencyRate(false)
                .order(55)
                .build();

        earnRuleId = createEarnRule(earnRuleCreateObj)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .path(CAMPAIGN_ID_FIELD);

        if (image) {
            addImage(earnRuleId, LOCALIZATION_EN);
            addImage(earnRuleId, LOCALIZATION_AR);
        }

        return earnRuleId;
    }

    public static String createEarnRuleWithAllContentTypesAndStaking(Boolean image) {
        String earnRuleId;

        val earnRuleContentObject = new EarnRule[]{
                earnRuleContentTitleEn, earnRuleContentTitleAr, earnRuleContentDescriptionEn,
                earnRuleContentDescriptionAr, earnRuleContentUrlForPictureEn, earnRuleContentUrlForPictureAr
        };

        val earnRuleCreateObj = Campaign
                .campaignBuilder()
                .name(CAMPAIGN_NAME)
                .reward(CAMPAIGN_REWARD.toString())
                .fromDate(CAMPAIGN_FROM)
                .toDate(CAMPAIGN_TO)
                .description(CAMPAIGN_DESC)
                .conditions(createConditionArray(conditionWithStaking))
                .createdBy(CAMPAIGN_CREATED_BY)
                .rewardType(REWARD_TYPE_FIXED)
                .contents(earnRuleContentObject)
                .completionCount(CAMPAIGN_COMPLETION_COUNT)
                .amountInTokens(AMOUNT_IN_TOKENS_100_000)
                .amountInCurrency(AMOUNT_IN_CURRENCY_10_000)
                .usePartnerCurrencyRate(false)
                .build();

        earnRuleId = createEarnRule(earnRuleCreateObj)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .path(CAMPAIGN_ID_FIELD);

        if (image) {
            addImage(earnRuleId, LOCALIZATION_EN);
            addImage(earnRuleId, LOCALIZATION_AR);
        }

        return earnRuleId;
    }

    public static String createEarnRuleWithENContent(Boolean image) {
        String earnRuleId;

        val earnRuleContentObject = new EarnRule[]{
                earnRuleContentTitleEn, earnRuleContentDescriptionEn, earnRuleContentUrlForPictureEn
        };

        val earnRuleCreateObj = Campaign
                .campaignBuilder()
                .name(CAMPAIGN_NAME)
                .reward(CAMPAIGN_REWARD.toString())
                .fromDate(CAMPAIGN_FROM)
                .toDate(CAMPAIGN_TO)
                .description(CAMPAIGN_DESC)
                .conditions(createConditionArray(condition))
                .createdBy(CAMPAIGN_CREATED_BY)
                .rewardType(REWARD_TYPE_FIXED)
                .contents(earnRuleContentObject)
                .completionCount(CAMPAIGN_COMPLETION_COUNT)
                .amountInTokens(AMOUNT_IN_TOKENS_100_000)
                .amountInCurrency(AMOUNT_IN_CURRENCY_10_000)
                .usePartnerCurrencyRate(false)
                .build();

        earnRuleId = createEarnRule(earnRuleCreateObj)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .path(CAMPAIGN_ID_FIELD);

        if (image) {
            addImage(earnRuleId, LOCALIZATION_EN);
        }

        return earnRuleId;
    }

    public static String createPendingEarnRule(Boolean image) {
        String earnRuleId;

        val earnRuleContentObject = new EarnRule[]{
                earnRuleContentTitleEn, earnRuleContentTitleAr, earnRuleContentDescriptionEn,
                earnRuleContentDescriptionAr, earnRuleContentUrlForPictureEn, earnRuleContentUrlForPictureAr
        };

        val earnRuleCreateObj = Campaign
                .campaignBuilder()
                .name(CAMPAIGN_NAME)
                .reward(CAMPAIGN_REWARD.toString())
                .fromDate(Instant.now().plus(1, ChronoUnit.DAYS).toString())
                .toDate(CAMPAIGN_TO)
                .description(CAMPAIGN_DESC)
                .conditions(createConditionArray(condition))
                .createdBy(CAMPAIGN_CREATED_BY)
                .rewardType(REWARD_TYPE_FIXED)
                .contents(earnRuleContentObject)
                .completionCount(CAMPAIGN_COMPLETION_COUNT)
                .amountInTokens(AMOUNT_IN_TOKENS_100_000)
                .amountInCurrency(AMOUNT_IN_CURRENCY_10_000)
                .usePartnerCurrencyRate(false)
                .build();

        earnRuleId = createEarnRule(earnRuleCreateObj)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .path(CAMPAIGN_ID_FIELD);

        if (image) {
            addImage(earnRuleId, LOCALIZATION_EN);
            addImage(earnRuleId, LOCALIZATION_AR);
        }

        return earnRuleId;
    }

    public static String createCompletedEarnRule(Boolean image) {
        String earnRuleId;

        val earnRuleContentObject = new EarnRule[]{
                earnRuleContentTitleEn, earnRuleContentTitleAr, earnRuleContentDescriptionEn,
                earnRuleContentDescriptionAr, earnRuleContentUrlForPictureEn, earnRuleContentUrlForPictureAr
        };

        val earnRuleCreateObj = Campaign
                .campaignBuilder()
                .name(CAMPAIGN_NAME)
                .reward(CAMPAIGN_REWARD.toString())
                .fromDate(CAMPAIGN_FROM)
                .toDate(Instant.now().plus(3, ChronoUnit.SECONDS).toString())
                .description(CAMPAIGN_DESC)
                .conditions(createConditionArray(condition))
                .createdBy(CAMPAIGN_CREATED_BY)
                .rewardType(REWARD_TYPE_FIXED)
                .contents(earnRuleContentObject)
                .completionCount(CAMPAIGN_COMPLETION_COUNT)
                .amountInTokens(AMOUNT_IN_TOKENS_100_000)
                .amountInCurrency(AMOUNT_IN_CURRENCY_10_000)
                .usePartnerCurrencyRate(false)
                .order(77)
                .build();

        earnRuleId = createEarnRule(earnRuleCreateObj)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .path(CAMPAIGN_ID_FIELD);

        if (image) {
            addImage(earnRuleId, LOCALIZATION_EN);
            addImage(earnRuleId, LOCALIZATION_AR);
        }

        return earnRuleId;
    }

    private static void addImage(String burnRuleId, Localization localization) {
        val type = PictureContentType.JPG.getValue();

        val fileCreateRequestObj = FileCreateRequestModel
                .builder()
                .ruleContentId(getEarnRuleContentIdByContentType(
                        burnRuleId, RULE_CONTENT_TYPE_URL_FOR_PICTURE, localization))
                .name(FakerUtils.title)
                .type(type)
                .content(content)
                .build();

        addEarnRuleImage(fileCreateRequestObj)
                .then()
                .statusCode(SC_OK);
    }

    private static String getCampaignConditionId(String campaignId) {
        return getCampaignById(campaignId)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .path("Conditions[0].Id");
    }

    public static String getExpectedAmountAfterCompaignCompletion() {
        return String.valueOf(CONDITION_REWARD + CAMPAIGN_REWARD);
    }

}
