package com.lykke.tests.api.common;

import static com.lykke.api.testing.api.base.RequestHeader.getHeader;
import static com.lykke.api.testing.api.common.GenerateUtils.generateRandomString;
import static com.lykke.tests.api.base.PathConsts.getFullPath;
import static com.lykke.tests.api.base.Paths.AdminApi.ADMIN_API_CUSTOMER_BALANCE_PATH;
import static com.lykke.tests.api.common.CommonConsts.Expiration.DEFAULT_EXPIRATION_INTERVAL_MSEC;
import static com.lykke.tests.api.service.admin.LoginAdminUtils.getAdminToken;
import static com.lykke.tests.api.service.campaigns.CampaignUtils.createCampaign;
import static com.lykke.tests.api.service.campaigns.CampaignUtils.createConditionArray;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.Matchers.equalTo;

import com.lykke.api.testing.api.common.FakerUtils;
import com.lykke.tests.api.common.enums.Localization;
import com.lykke.tests.api.common.enums.RuleContentType;
import com.lykke.tests.api.common.enums.campaign.ConditionType;
import com.lykke.tests.api.service.campaigns.model.BonusType;
import com.lykke.tests.api.service.campaigns.model.Campaign;
import com.lykke.tests.api.service.campaigns.model.EarnRule;
import com.lykke.tests.api.service.campaigns.model.RewardType;
import java.time.Instant;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import lombok.val;

@UtilityClass
public class CommonMethods {

    public static final String CAMPAIGN_NAME = generateRandomString();
    public static final Double CONDITION_REWARD = 105.0;
    public static final Double CAMPAIGN_REWARD = 500.0;
    public static final String CONDITION_TYPE_SIGNUP = ConditionType.SIGNUP.getValue();
    public static final int CONDITION_COMPLETION_COUNT = 1;
    public static final String CAMPAIGN_CREATED_BY = generateRandomString();
    public static final String CAMPAIGN_FROM = Instant.now().toString();
    public static final String CAMPAIGN_TO = "2025-05-23T06:59:26.627Z";
    public static final String CAMPAIGN_DESC = generateRandomString();
    public static final int CAMPAIGN_COMPLETION_COUNT = 1;
    public static final RewardType REWARD_TYPE = RewardType.FIXED;

    public static final Double TOTAL_REWARD = CONDITION_REWARD + CAMPAIGN_REWARD;

    public static BonusType getDefaultConditionObject() {
        return BonusType
                .builder()
                .type(CONDITION_TYPE_SIGNUP)
                .immediateReward(CONDITION_REWARD.toString())
                .completionCount(CONDITION_COMPLETION_COUNT)
                .build();
    }

    public static Campaign getDefaultCampaignObject() {
        return Campaign
                .campaignBuilder()
                .name(CAMPAIGN_NAME)
                .reward(CAMPAIGN_REWARD.toString())
                .fromDate(CAMPAIGN_FROM)
                .toDate(CAMPAIGN_TO)
                .description(CAMPAIGN_DESC)
                .conditions(createConditionArray(
                        getDefaultConditionObject()))
                // TODO: check there is sth wrong here, mentioning condition twice
                .createdBy(CAMPAIGN_CREATED_BY)
                .rewardType(REWARD_TYPE)
                .completionCount(CAMPAIGN_COMPLETION_COUNT)
                .build();
    }

    public static EarnRule getDefaultEarnRuleObject() {
        return EarnRule
                .builder()
                .ruleContentType(RuleContentType.TITLE)
                .localization(Localization.EN)
                .value(FakerUtils.title)
                .build();
    }

    public static void createDefaultSignUpCampaign() {
        val condition = getDefaultConditionObject();
        val campaign = getDefaultCampaignObject();
        val earnRule = getDefaultEarnRuleObject();

        createCampaign(campaign, condition, earnRule);
    }

    @Deprecated
    public static int getCustomerBalanceForDefaultAsset(String customerId) {
        return getHeader(getAdminToken())
                .when()
                .get(getFullPath(ADMIN_API_CUSTOMER_BALANCE_PATH, customerId))
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .body("Asset", equalTo("MVN"))
                .extract()
                .jsonPath()
                .getInt("Amount");
    }

    @SneakyThrows
    public void waitForExpiration(int seconds) {
        Thread.sleep(seconds * 1000 + DEFAULT_EXPIRATION_INTERVAL_MSEC);
    }
}
