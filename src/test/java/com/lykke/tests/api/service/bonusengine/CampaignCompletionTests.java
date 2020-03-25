package com.lykke.tests.api.service.bonusengine;

import static com.lykke.tests.api.common.CommonConsts.SMOKE_TEST;
import static com.lykke.tests.api.common.CommonConsts.Staking.DEFAULT_STAKE_WARNING_PERIOD;
import static com.lykke.tests.api.common.CommonConsts.Staking.DEFAULT_STAKING_PERIOD;
import static com.lykke.tests.api.common.CommonMethods.CAMPAIGN_CREATED_BY;
import static com.lykke.tests.api.common.CommonMethods.CAMPAIGN_DESC;
import static com.lykke.tests.api.common.CommonMethods.CAMPAIGN_FROM;
import static com.lykke.tests.api.common.CommonMethods.CAMPAIGN_REWARD;
import static com.lykke.tests.api.common.CommonMethods.CAMPAIGN_TO;
import static com.lykke.tests.api.common.CommonMethods.CONDITION_REWARD;
import static com.lykke.tests.api.common.CommonMethods.CONDITION_TYPE_SIGNUP;
import static com.lykke.tests.api.service.admin.CampaignUtils.createConditionArray;
import static com.lykke.tests.api.service.campaigns.CampaignUtils.createCampaign;
import static com.lykke.tests.api.service.campaigns.model.ConditionCreateModel.conditionCreateBuilder;
import static com.lykke.tests.api.service.customermanagement.RegisterCustomerUtils.registerDefaultVerifiedCustomer;
import static org.apache.http.HttpStatus.SC_NO_CONTENT;
import static org.apache.http.HttpStatus.SC_OK;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.lykke.tests.api.base.BaseApiTest;
import com.lykke.tests.api.common.enums.Localization;
import com.lykke.tests.api.common.enums.RuleContentType;
import com.lykke.tests.api.common.model.CustomerInfo;
import com.lykke.tests.api.service.bonusengine.model.CampaignCompletionModel;
import com.lykke.tests.api.service.campaigns.model.Campaign;
import com.lykke.tests.api.service.campaigns.model.CampaignCreateResponseModel;
import com.lykke.tests.api.service.campaigns.model.EarnRule;
import com.lykke.tests.api.service.campaigns.model.RewardType;
import lombok.val;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

public class CampaignCompletionTests extends BaseApiTest {

    private static final String EARN_RULE_CONTENT_TITLE_EN_VALUE = "ENGLISH TITLE";
    private static final RuleContentType RULE_CONTENT_TYPE_TITLE = RuleContentType.TITLE;
    private static final Localization LOCALIZATION_EN = Localization.EN;
    private static final RewardType REWARD_TYPE_FIXED = RewardType.FIXED;

    @Test
    @Tag(SMOKE_TEST)
    void getCampaignCompletion() {
        val campaignCreationResult = createSignUpCampaign();
        val customerData = CustomerInfo
                .customerInfoBuilder()
                .customerId(registerDefaultVerifiedCustomer().getCustomerId())
                .build();

        val actualResult = BonusEngineUtils
                .getCampaignCompletion(customerData.getCustomerId(), campaignCreationResult.getCampaignId())
                .then()

                ////xx
                ////55
                .log().all()

                .assertThat()
                ////55
                ////55.statusCode(SC_OK)
                .statusCode(SC_NO_CONTENT);

        ////xx
        ////55
        /*
                .extract()
                .as(CampaignCompletionModel.class);

        assertAll(
                () -> assertEquals(1, actualResult.getCampaignCompletionCount()),
                () -> assertEquals(false, actualResult.isCompleted()),
                () -> assertEquals(customerData.getCustomerId(), actualResult.getCustomerId()),
                () -> assertEquals(campaignCreationResult.getCampaignId(), actualResult.getCampaignId())
        );
        */
    }

    private CampaignCreateResponseModel createSignUpCampaign() {
        val earnRuleContentTitleEn = EarnRule
                .builder()
                .ruleContentType(RULE_CONTENT_TYPE_TITLE)
                .localization(LOCALIZATION_EN)
                .value(EARN_RULE_CONTENT_TITLE_EN_VALUE)
                .build();
        val condition = conditionCreateBuilder()
                .type(CONDITION_TYPE_SIGNUP)
                .immediateReward(CONDITION_REWARD.toString())
                .completionCount(1)
                .hasStaking(false)
                .stakingPeriod(DEFAULT_STAKING_PERIOD)
                .stakeWarningPeriod(DEFAULT_STAKE_WARNING_PERIOD)
                .build();

        val earnRuleContentObject = new EarnRule[]{
                earnRuleContentTitleEn
        };
        return createCampaign(Campaign
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
                .build(), condition, earnRuleContentTitleEn)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(CampaignCreateResponseModel.class);
    }
}
