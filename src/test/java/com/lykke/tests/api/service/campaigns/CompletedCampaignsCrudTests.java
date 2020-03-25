package com.lykke.tests.api.service.campaigns;

import static com.lykke.api.testing.api.common.GenerateUtils.generateRandomString;
import static com.lykke.tests.api.service.campaigns.CampaignUtils.createConditionArray;
import static com.lykke.tests.api.service.campaigns.CampaignUtils.getCampaignById;
import static com.lykke.tests.api.service.campaigns.CampaignUtils.getCampaignId;
import static com.lykke.tests.api.service.campaigns.CampaignUtils.getEarnRuleId;
import static com.lykke.tests.api.service.campaigns.CampaignUtils.updateCampaignById;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.Matchers.equalTo;

import com.lykke.api.testing.annotations.UserStoryId;
import com.lykke.api.testing.api.common.FakerUtils;
import com.lykke.tests.api.base.BaseApiTest;
import com.lykke.tests.api.common.enums.Localization;
import com.lykke.tests.api.common.enums.RuleContentType;
import com.lykke.tests.api.service.campaigns.model.Campaign;
import com.lykke.tests.api.service.campaigns.model.CampaignEditModel;
import com.lykke.tests.api.service.campaigns.model.ConditionCreateModel;
import com.lykke.tests.api.service.campaigns.model.EarnRule;
import com.lykke.tests.api.service.campaigns.model.RewardType;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import lombok.val;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class CompletedCampaignsCrudTests extends BaseApiTest {

    private static final String CONDITION_TYPE_SIGNUP = "signup";
    private static final Integer CONDITION_REWARD = 5;
    private static final int CONDITION_COMPLETION_COUNT = 4;
    private static final String CAMPAIGN_NAME = generateRandomString();
    private static final String CAMPAIGN_CREATED_BY = generateRandomString();
    private static final String CAMPAIGN_FROM = Instant.now().toString();
    private static final String CAMPAIGN_TO = Instant.now().plus(1, ChronoUnit.DAYS).toString();
    private static final String CAMPAIGN_DESC = generateRandomString();
    private static final float CAMPAIGN_REWARD = 500;
    private static final int CAMPAIGN_COMPLETION_COUNT = 2;
    private static final String ERROR_MESSAGE_FIELD = "ErrorMessage";
    private static final String ERROR_CODE = "ErrorCode";
    private static final RewardType REWARD_TYPE = RewardType.FIXED;
    private static Campaign campaign;
    private static ConditionCreateModel bonusType;
    private static EarnRule earnRule;

    @BeforeAll
    static void dataSetup() {
        bonusType = ConditionCreateModel
                .conditionCreateBuilder()
                .type(CONDITION_TYPE_SIGNUP)
                .immediateReward(CONDITION_REWARD.toString())
                .completionCount(CONDITION_COMPLETION_COUNT)
                .build();

        campaign = Campaign
                .campaignBuilder()
                .name(CAMPAIGN_NAME)
                .reward(String.valueOf(CAMPAIGN_REWARD))
                .fromDate(CAMPAIGN_FROM)
                .toDate(CAMPAIGN_TO)
                .description(CAMPAIGN_DESC)
                .conditions(createConditionArray(bonusType))
                .createdBy(CAMPAIGN_CREATED_BY)
                .rewardType(REWARD_TYPE)
                .completionCount(CAMPAIGN_COMPLETION_COUNT)
                .build();

        earnRule = EarnRule
                .builder()
                .ruleContentType(RuleContentType.TITLE)
                .localization(Localization.EN)
                .value(FakerUtils.title)
                .build();
    }

    private static String getCampaignConditionId(String campaignId) {
        return getCampaignById(campaignId)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .path("Conditions[0].Id");
    }

    @Test
    @UserStoryId(3871)
    void shouldNotUpdateCompletedCampaign() {
        final Integer UPD_CONDITION_REWARD = 10;
        val UPD_CONDITION_COMPLETION_COUNT = 3;

        val UPD_CAMPAIGN_NAME = generateRandomString();
        val UPD_CAMPAIGN_FROM = "2019-02-23T06:59:26.627Z";
        val UPD_CAMPAIGN_TO = "2027-05-23T06:59:26.627Z";
        val UPD_CAMPAIGN_DESC = generateRandomString();
        final Integer UPD_CAMPAIGN_REWARD = 1000;
        val CONDITION_COMPLETION_COUNT = 4;

        // TODO: 20190604
        // DB?
        String campaignId = getCampaignId(campaign, bonusType, earnRule);
        String conditionId = getCampaignConditionId(campaignId);

        val newBonusType = ConditionCreateModel
                .conditionCreateBuilder()
                .completionCount(UPD_CONDITION_COMPLETION_COUNT)
                .immediateReward(UPD_CONDITION_REWARD.toString())
                .type(CONDITION_TYPE_SIGNUP)
                .build();

        val newCampaign = CampaignEditModel
                .campaignBuilder()
                .name(UPD_CAMPAIGN_NAME)
                .fromDate(UPD_CAMPAIGN_FROM)
                .toDate(UPD_CAMPAIGN_TO)
                .description(UPD_CAMPAIGN_DESC)
                .reward(UPD_CAMPAIGN_REWARD.toString())
                .completionCount(
                        CONDITION_COMPLETION_COUNT) //TODO: Check if the campaigns Condition Count can be updated when the campaigns is completed in FAL-680
                .rewardType(REWARD_TYPE)
                .isEnabled(true)
                .build();

        updateCampaignById(newCampaign, newBonusType, campaignId, conditionId, earnRule, getEarnRuleId(campaignId))
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .body(ERROR_CODE, equalTo("EntityNotValid"))
                .body(ERROR_MESSAGE_FIELD, equalTo("Campaign Reward must not be changed\n"
                        + "Earn rule's Conditions must not be changed"));
    }
}
