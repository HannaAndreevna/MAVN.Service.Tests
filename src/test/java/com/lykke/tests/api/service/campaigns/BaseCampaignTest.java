package com.lykke.tests.api.service.campaigns;

import static com.lykke.api.testing.api.common.GenerateUtils.generateRandomString;
import static com.lykke.tests.api.common.CommonConsts.ERROR_CODE_FIELD;
import static com.lykke.tests.api.common.CommonConsts.ERROR_CODE_NONE;
import static com.lykke.tests.api.common.CommonConsts.REFERRAL_CODE_FIELD;
import static com.lykke.tests.api.service.campaigns.CampaignUtils.createCampaign;
import static com.lykke.tests.api.service.campaigns.CampaignUtils.deleteCampaign;
import static com.lykke.tests.api.service.campaigns.CampaignUtils.getCampaigns;
import static com.lykke.tests.api.service.referral.ReferralCodeUtils.setReferralCodeByCustomerId;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.Matchers.equalTo;

import com.lykke.api.testing.api.common.FakerUtils;
import com.lykke.tests.api.base.BaseApiTest;
import com.lykke.tests.api.common.enums.Localization;
import com.lykke.tests.api.common.enums.RuleContentType;
import com.lykke.tests.api.service.campaigns.model.BonusType;
import com.lykke.tests.api.service.campaigns.model.Campaign;
import com.lykke.tests.api.service.campaigns.model.ConditionCreateModel;
import com.lykke.tests.api.service.campaigns.model.EarnRule;
import com.lykke.tests.api.service.campaigns.model.RewardType;
import java.time.Instant;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

public class BaseCampaignTest extends BaseApiTest {

    protected static final String CONDITION_TYPE_SIGNUP = "signup";
    protected static final String CONDITION_TYPE_FRIEND_REFERRAL = "friend-referral";
    protected static final String CONDITION_TYPE_PURCHASE = "mvn-purchase";
    protected static final String CONDITION_TYPE_PURCHASE_REFERRAL = "purchase-referral";
    protected static final Integer CONDITION_REWARD = 100000;
    protected static final int CONDITION_COMPLETION_COUNT = 1;
    protected static final String CAMPAIGN_NAME = generateRandomString();
    protected static final String CAMPAIGN_CREATED_BY = generateRandomString();
    protected static final String CAMPAIGN_FROM = Instant.now().toString();
    protected static final String CAMPAIGN_TO = "2025-05-23T06:59:26.627Z";
    protected static final String CAMPAIGN_DESC = generateRandomString();
    protected static final Integer CAMPAIGN_REWARD = 200000;
    protected static final int CAMPAIGN_COMPLETION_COUNT = 1;
    protected static final Integer CAMPAIGN_REWARD_PERCENTAGE = 10;
    protected static final RewardType REWARD_TYPE_PERCENTAGE = RewardType.PERCENTAGE;
    protected static final RewardType REWARD_TYPE_FIXED = RewardType.FIXED;
    protected static final String CAMPAIGN_ID_FIELD = "CampaignId";
    protected static final String CAMPAIGNS_FIELD = "Campaigns";
    protected static final int TOTAL_REWARD = CONDITION_REWARD + CAMPAIGN_REWARD;
    protected static Campaign campaign;
    protected static ConditionCreateModel bonusType;
    protected static EarnRule earnRule;
    protected static String campaignId;
    protected static ConditionCreateModel.ConditionCreateModelBuilder baseCondition;
    protected static Campaign.CampaignBuilder baseCampaign;
    protected static EarnRule.EarnRuleBuilder baseEarnRule;

    public static void deleteAllCampaigns() {
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

    @BeforeEach
    void dataSetup() {
        deleteAllCampaigns();

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
    }

    @AfterEach
    void campaignCleanup() {
        deleteCampaign(campaignId)
                .then()
                .assertThat()
                .statusCode(SC_OK);
    }
}
