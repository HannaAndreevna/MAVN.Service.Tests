package com.lykke.tests.api.service.campaigns;

import static com.lykke.api.testing.api.common.GenerateUtils.generateRandomString;
import static com.lykke.tests.api.common.CommonConsts.ERROR_CODE_FIELD;
import static com.lykke.tests.api.common.CommonConsts.ERROR_CODE_NONE;
import static com.lykke.tests.api.common.CommonConsts.SMOKE_TEST;
import static com.lykke.tests.api.service.campaigns.CampaignUtils.createCampaign;
import static com.lykke.tests.api.service.campaigns.CampaignUtils.createConditionArray;
import static com.lykke.tests.api.service.campaigns.CampaignUtils.createConditionEditArray;
import static com.lykke.tests.api.service.campaigns.CampaignUtils.createEarnRuleContentEditArray;
import static com.lykke.tests.api.service.campaigns.CampaignUtils.deleteCampaign;
import static com.lykke.tests.api.service.campaigns.CampaignUtils.getCampaignById;
import static com.lykke.tests.api.service.campaigns.CampaignUtils.getCampaignId;
import static com.lykke.tests.api.service.campaigns.CampaignUtils.getCampaigns;
import static com.lykke.tests.api.service.campaigns.CampaignUtils.getEarnRuleId;
import static com.lykke.tests.api.service.campaigns.CampaignUtils.updateCampaignById;
import static com.lykke.tests.api.service.customermanagement.RegisterCustomerUtils.ERROR_MESSAGE;
import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.text.CharSequenceLength.hasLength;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.params.provider.Arguments.of;

import com.lykke.api.testing.annotations.UserStoryId;
import com.lykke.api.testing.api.common.FakerUtils;
import com.lykke.tests.api.base.BaseApiTest;
import com.lykke.tests.api.common.enums.Localization;
import com.lykke.tests.api.common.enums.RuleContentType;
import com.lykke.tests.api.service.campaigns.model.Campaign;
import com.lykke.tests.api.service.campaigns.model.CampaignDetailResponseModel;
import com.lykke.tests.api.service.campaigns.model.CampaignEditModel;
import com.lykke.tests.api.service.campaigns.model.CampaignServiceErrorCode;
import com.lykke.tests.api.service.campaigns.model.CampaignStatus;
import com.lykke.tests.api.service.campaigns.model.ConditionCreateModel;
import com.lykke.tests.api.service.campaigns.model.ConditionModel;
import com.lykke.tests.api.service.campaigns.model.EarnRule;
import com.lykke.tests.api.service.campaigns.model.RewardType;
import com.lykke.tests.api.service.campaigns.model.burnrules.Vertical;
import java.time.Instant;
import java.util.stream.Stream;
import lombok.val;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

public class ActiveCampaignsCrudTests extends BaseApiTest {

    public static final String SPLIT_BY_SECONDS = "\\.\\d{2,6}Z";
    protected static final String CAMPAIGNS_FIELD = "Campaigns";
    private static final String CONDITION_TYPE_SIGNUP = "signup";
    private static final String CONDITION_TYPE_REFERRAL = "referral";
    private static final Integer CONDITION_REWARD = 5;
    private static final int CONDITION_COMPLETION_COUNT = 4;
    private static final String CAMPAIGN_NAME = generateRandomString();
    private static final String CAMPAIGN_CREATED_BY = generateRandomString();
    private static final String CAMPAIGN_FROM = Instant.now().toString();
    private static final String CAMPAIGN_TO = "2025-05-23T06:59:26.627Z";
    private static final String CAMPAIGN_DESC = generateRandomString();
    private static final Double CAMPAIGN_REWARD = 500.0;
    private static final Double CAMPAIGN_REWARD_WITH_PERCENTAGE = 99.0;
    private static final int CAMPAIGN_COMPLETION_COUNT = 100;
    private static final RewardType REWARD_TYPE_FIXED = RewardType.FIXED;
    private static final RewardType REWARD_TYPE_PERCENTAGE = RewardType.PERCENTAGE;
    private static final String CONDITION_TYPE_IS_NOT_VALID_MESSAGE = "Condition Type  is not a valid Type";
    private static final String NAME_MUST_NOT_BE_EMPTY_MESSAGE = "'Name' must not be empty.";
    private static final String NAME_MUST_BE_AT_LEAST_3_CHAR_MESSAGE = "The length of 'Name' must be at least 3 characters. You entered 0 characters.";
    private static final String CAMPAIGN_REWARD_MUST_BE_GREATER_THAN_OR_EQUAL_TO_0_MESSAGE = "Campaign Reward must be greater than or equal to 0";
    ////55private static final String CAMPAIGN_REWARD_MUST_BE_GREATER_THAN_0_MESSAGE = "Campaign Reward must be greater than 0";
    private static final String CAMPAIGN_REWARD_MUST_BE_GREATER_THAN_0_MESSAGE = "Campaign Reward must be greater than or equal to 0";
    private static final String CAMPAIGN_REWARD_MUST_BE_WHOLE_NUMBER_MESSAGE = "Campaign Reward must be a whole number";
    private static final String DESCRIPTION_MUST_NOT_BE_EMPTY_MESSAGE = "The length of 'Description' must be at least 3 characters. You entered 0 characters.";
    private static final String DESCRIPTION_MUST_BE_AT_LEAST_3_CHAR_2_MESSAGE = "The length of 'Description' must be at least 3 characters. You entered 2 characters.";
    private static final String CAMPAIGN_COMPLETION_MUST_BE_EITHER_NULL_OR_GREATER_THAN_0 = "Campaign completion must be either null or greater than 0.";
    private static final String CONDITION_TYPE_MUST_NOT_HAVE_TWO_OR_MORE_CONDITIONS_OF_THE_SAME_TYPE_MESSAGE = "Campaign must not have two or more Conditions of same type";
    private static final String CONDITION_TYPE_S_IS_NOT_VALID_MESSAGE = "Condition Type %s is not a valid Type";
    private static final String FROM_DATE_MESSAGE_01 = "Error converting value {null} to type 'System.DateTime'.";
    private static final String FROM_DATE_MESSAGE_02 = "Could not convert string to DateTime: invalid_date.";
    private static final String TO_DATE_MESSAGE = "Could not convert string to DateTime: invalid_date.";
    private static final String REWARD_TYPE_MESSAGE_01 =
            "Cannot convert null value to Lykke.Service.Campaign.Client.Models.Enums.RewardType.";
    private static final String REWARD_TYPE_MESSAGE_02 =
            "Error converting value \"\" to type 'Lykke.Service.Campaign.Client.Models.Enums.RewardType'.";
    private static final String REWARD_TYPE_MESSAGE_03 =
            "Error converting value \"NoFixed\" to type 'Lykke.Service.Campaign.Client.Models.Enums.RewardType'.";
    private static final String NAME_FIELD = "name";
    private static final String ERROR_MESSAGE_FIELD = "ErrorMessage";
    private static final String MODEL_ERRORS_FIELD = "ModelErrors";
    private static final String NAME_0_FIELD = "Name[0]";
    private static final String NAME_1_FIELD = "Name[1]";
    private static final String REWARD_0_FIELD = "Reward[0]";
    private static final String TO_DATE_0_FIELD = "ToDate[0]";
    private static final String FROM_DATE_0_FIELD = "FromDate[0]";
    private static final String DESCRIPTION_0_FIELD = "Description[0]";
    private static final String REWARD_TYPE_0_FIELD = "RewardType[0]";
    private static final String COMLETION_COUNT_O_FIELD = "CompletionCount[0]";
    private static final String CONDITION_0_FIELD = "Conditions[0]";
    private static final int ORDER = 23;
    private static Campaign campaign;
    private static ConditionCreateModel bonusType;
    private static ConditionCreateModel bonusTypeWithVertical;
    private static EarnRule earnRule;
    private static ConditionCreateModel.ConditionCreateModelBuilder baseCondition;
    private static Campaign.CampaignBuilder baseCampaign;
    private static CampaignEditModel.CampaignEditModelBuilder baseEditCampaign;
    private static EarnRule.EarnRuleBuilder baseEarnRule;

    @BeforeAll
    static void dataSetup() {
        deleteAllCampaigns();
        baseCondition = ConditionCreateModel
                .conditionCreateBuilder()
                .type(CONDITION_TYPE_SIGNUP)
                .immediateReward(CONDITION_REWARD.toString())
                .completionCount(CONDITION_COMPLETION_COUNT)
                .hasStaking(false);

        bonusType = baseCondition
                .build();

        bonusTypeWithVertical = ConditionCreateModel
                .conditionCreateBuilder()
                .type("estate-lead-referral")
                .immediateReward(CONDITION_REWARD.toString())
                .completionCount(CONDITION_COMPLETION_COUNT)
                .hasStaking(false)
                .build();

        baseCampaign = Campaign
                .campaignBuilder()
                .name(CAMPAIGN_NAME)
                .reward(String.valueOf(CAMPAIGN_REWARD))
                .fromDate(CAMPAIGN_FROM)
                .toDate(CAMPAIGN_TO)
                .description(CAMPAIGN_DESC)
                .conditions(createConditionArray(bonusType))
                .createdBy(CAMPAIGN_CREATED_BY)
                .rewardType(REWARD_TYPE_FIXED)
                .completionCount(CAMPAIGN_COMPLETION_COUNT);

        baseEditCampaign = CampaignEditModel
                .campaignBuilder()
                .name(CAMPAIGN_NAME)
                .reward(String.valueOf(CAMPAIGN_REWARD))
                .fromDate(CAMPAIGN_FROM)
                .toDate(CAMPAIGN_TO)
                .description(CAMPAIGN_DESC)
                .conditions(createConditionEditArray(bonusType))
                .rewardType(REWARD_TYPE_FIXED)
                .completionCount(CAMPAIGN_COMPLETION_COUNT);

        campaign = baseCampaign
                .build();

        baseEarnRule = EarnRule
                .builder()
                .ruleContentType(RuleContentType.TITLE)
                .localization(Localization.EN)
                .value(FakerUtils.title);

        earnRule = baseEarnRule
                .build();
    }

    private static void createMultipleCampaigns(int numberOfCampaignsToBeCreated) {
        int i = 0;
        while (i < numberOfCampaignsToBeCreated) { // TODO: convert to Stream
            createDefaultCampaign();
            i++;
        }
    }

    private static String createDefaultCampaign() {
        return createCampaign(campaign, bonusType, earnRule)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .path("CampaignId");
    }

    private static String getCampaignConditionId(String campaignId) {
        return getCampaignById(campaignId)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .path("Conditions[0].Id");
    }

    private static Stream campaign_InvalidParameters() {
        return Stream.of(
                of(CONDITION_TYPE_SIGNUP, CONDITION_REWARD, CONDITION_COMPLETION_COUNT, null, CAMPAIGN_REWARD,
                        CAMPAIGN_FROM, CAMPAIGN_TO, CAMPAIGN_DESC, CAMPAIGN_CREATED_BY, REWARD_TYPE_FIXED,
                        CAMPAIGN_COMPLETION_COUNT,
                        NAME_0_FIELD, NAME_MUST_NOT_BE_EMPTY_MESSAGE, NAME_MUST_NOT_BE_EMPTY_MESSAGE),
                of(CONDITION_TYPE_SIGNUP, CONDITION_REWARD, CONDITION_COMPLETION_COUNT, "", CAMPAIGN_REWARD,
                        CAMPAIGN_FROM, CAMPAIGN_TO, CAMPAIGN_DESC, CAMPAIGN_CREATED_BY, REWARD_TYPE_FIXED,
                        CAMPAIGN_COMPLETION_COUNT,
                        NAME_0_FIELD, NAME_MUST_NOT_BE_EMPTY_MESSAGE, NAME_MUST_NOT_BE_EMPTY_MESSAGE),
                of(CONDITION_TYPE_SIGNUP, CONDITION_REWARD, CONDITION_COMPLETION_COUNT, "", CAMPAIGN_REWARD,
                        CAMPAIGN_FROM, CAMPAIGN_TO, CAMPAIGN_DESC, CAMPAIGN_CREATED_BY, REWARD_TYPE_FIXED,
                        CAMPAIGN_COMPLETION_COUNT,
                        NAME_1_FIELD, NAME_MUST_NOT_BE_EMPTY_MESSAGE, NAME_MUST_BE_AT_LEAST_3_CHAR_MESSAGE),
                of(CONDITION_TYPE_SIGNUP, CONDITION_REWARD, CONDITION_COMPLETION_COUNT, CAMPAIGN_NAME, -1.0,
                        CAMPAIGN_FROM, CAMPAIGN_TO, CAMPAIGN_DESC, CAMPAIGN_CREATED_BY, REWARD_TYPE_FIXED,
                        CAMPAIGN_COMPLETION_COUNT,
                        REWARD_0_FIELD, CAMPAIGN_REWARD_MUST_BE_GREATER_THAN_OR_EQUAL_TO_0_MESSAGE,
                        CAMPAIGN_REWARD_MUST_BE_GREATER_THAN_OR_EQUAL_TO_0_MESSAGE),
                of(CONDITION_TYPE_SIGNUP, CONDITION_REWARD, CONDITION_COMPLETION_COUNT, CAMPAIGN_NAME, CAMPAIGN_REWARD,
                        CAMPAIGN_FROM, CAMPAIGN_TO, "12", CAMPAIGN_CREATED_BY, REWARD_TYPE_FIXED,
                        CAMPAIGN_COMPLETION_COUNT,
                        DESCRIPTION_0_FIELD, DESCRIPTION_MUST_BE_AT_LEAST_3_CHAR_2_MESSAGE,
                        DESCRIPTION_MUST_BE_AT_LEAST_3_CHAR_2_MESSAGE),
//                of(CONDITION_TYPE_SIGNUP, CONDITION_REWARD, 0, CAMPAIGN_NAME, CAMPAIGN_REWARD, CAMPAIGN_FROM,
//                        CAMPAIGN_TO, CAMPAIGN_DESC, CAMPAIGN_CREATED_BY, REWARD_TYPE_FIXED, CAMPAIGN_COMPLETION_COUNT, "Conditions[0].CompletionCount",
//                        CONDITION_COMPLETION_COUNT_MUST_BE_GREATER_THAN_0_MESSAGE, CONDITION_COMPLETION_COUNT_MUST_BE_GREATER_THAN_0_MESSAGE),
//                of(CONDITION_TYPE_SIGNUP, CONDITION_REWARD, -1, CAMPAIGN_NAME, CAMPAIGN_REWARD, CAMPAIGN_FROM,
//                        CAMPAIGN_TO, CAMPAIGN_DESC, CAMPAIGN_CREATED_BY, REWARD_TYPE_FIXED, CAMPAIGN_COMPLETION_COUNT, "Conditions[0].CompletionCount",
//                        CONDITION_COMPLETION_COUNT_MUST_BE_GREATER_THAN_0_MESSAGE, CONDITION_COMPLETION_COUNT_MUST_BE_GREATER_THAN_0_MESSAGE),
                of(CONDITION_TYPE_SIGNUP, CONDITION_REWARD, CONDITION_COMPLETION_COUNT, CAMPAIGN_NAME, CAMPAIGN_REWARD,
                        CAMPAIGN_FROM, CAMPAIGN_TO, CAMPAIGN_DESC, CAMPAIGN_CREATED_BY, REWARD_TYPE_FIXED, 0,
                        COMLETION_COUNT_O_FIELD,
                        CAMPAIGN_COMPLETION_MUST_BE_EITHER_NULL_OR_GREATER_THAN_0,
                        CAMPAIGN_COMPLETION_MUST_BE_EITHER_NULL_OR_GREATER_THAN_0),
                of(CONDITION_TYPE_SIGNUP, CONDITION_REWARD, CONDITION_COMPLETION_COUNT, CAMPAIGN_NAME, CAMPAIGN_REWARD,
                        CAMPAIGN_FROM, CAMPAIGN_TO, CAMPAIGN_DESC, CAMPAIGN_CREATED_BY, REWARD_TYPE_FIXED, -1,
                        COMLETION_COUNT_O_FIELD,
                        CAMPAIGN_COMPLETION_MUST_BE_EITHER_NULL_OR_GREATER_THAN_0,
                        CAMPAIGN_COMPLETION_MUST_BE_EITHER_NULL_OR_GREATER_THAN_0),
                of(CONDITION_TYPE_SIGNUP, CONDITION_REWARD, CONDITION_COMPLETION_COUNT, CAMPAIGN_NAME, 0.0,
                        CAMPAIGN_FROM, CAMPAIGN_TO, CAMPAIGN_DESC, CAMPAIGN_CREATED_BY, REWARD_TYPE_PERCENTAGE,
                        CAMPAIGN_COMPLETION_COUNT,
                        REWARD_0_FIELD, CAMPAIGN_REWARD_MUST_BE_GREATER_THAN_0_MESSAGE,
                        CAMPAIGN_REWARD_MUST_BE_GREATER_THAN_0_MESSAGE),
                of(CONDITION_TYPE_SIGNUP, CONDITION_REWARD, CONDITION_COMPLETION_COUNT, CAMPAIGN_NAME, -1.0,
                        CAMPAIGN_FROM, CAMPAIGN_TO, CAMPAIGN_DESC, CAMPAIGN_CREATED_BY, REWARD_TYPE_PERCENTAGE,
                        CAMPAIGN_COMPLETION_COUNT,
                        REWARD_0_FIELD, CAMPAIGN_REWARD_MUST_BE_GREATER_THAN_0_MESSAGE,
                        CAMPAIGN_REWARD_MUST_BE_GREATER_THAN_0_MESSAGE),
                of(CONDITION_TYPE_SIGNUP, CONDITION_REWARD, CONDITION_COMPLETION_COUNT, CAMPAIGN_NAME, 1.2,
                        CAMPAIGN_FROM, CAMPAIGN_TO, CAMPAIGN_DESC, CAMPAIGN_CREATED_BY, REWARD_TYPE_FIXED,
                        CAMPAIGN_COMPLETION_COUNT,
                        REWARD_0_FIELD, CAMPAIGN_REWARD_MUST_BE_WHOLE_NUMBER_MESSAGE,
                        CAMPAIGN_REWARD_MUST_BE_WHOLE_NUMBER_MESSAGE)
        );
    }

    private static Stream campaign_InvalidValues() {
        return Stream.of(
                of(CONDITION_TYPE_SIGNUP, CONDITION_REWARD, CONDITION_COMPLETION_COUNT, CAMPAIGN_NAME, CAMPAIGN_REWARD,
                        null, CAMPAIGN_TO, CAMPAIGN_DESC, CAMPAIGN_CREATED_BY, REWARD_TYPE_FIXED,
                        CAMPAIGN_COMPLETION_COUNT,
                        FROM_DATE_0_FIELD, FROM_DATE_MESSAGE_01),
                of(CONDITION_TYPE_SIGNUP, CONDITION_REWARD, CONDITION_COMPLETION_COUNT, CAMPAIGN_NAME, CAMPAIGN_REWARD,
                        "", CAMPAIGN_TO, CAMPAIGN_DESC, CAMPAIGN_CREATED_BY, REWARD_TYPE_FIXED,
                        CAMPAIGN_COMPLETION_COUNT,
                        FROM_DATE_0_FIELD, FROM_DATE_MESSAGE_01),
                of(CONDITION_TYPE_SIGNUP, CONDITION_REWARD, CONDITION_COMPLETION_COUNT, CAMPAIGN_NAME, CAMPAIGN_REWARD,
                        "invalid_date", CAMPAIGN_TO, CAMPAIGN_DESC, CAMPAIGN_CREATED_BY, REWARD_TYPE_FIXED,
                        CAMPAIGN_COMPLETION_COUNT,
                        FROM_DATE_0_FIELD, FROM_DATE_MESSAGE_02),
                of(CONDITION_TYPE_SIGNUP, CONDITION_REWARD, CONDITION_COMPLETION_COUNT, CAMPAIGN_NAME, CAMPAIGN_REWARD,
                        CAMPAIGN_FROM, "invalid_date", CAMPAIGN_DESC, CAMPAIGN_CREATED_BY, REWARD_TYPE_FIXED,
                        CAMPAIGN_COMPLETION_COUNT,
                        TO_DATE_0_FIELD, TO_DATE_MESSAGE),
                of(CONDITION_TYPE_SIGNUP, CONDITION_REWARD, CONDITION_COMPLETION_COUNT, CAMPAIGN_NAME, CAMPAIGN_REWARD,
                        CAMPAIGN_FROM, CAMPAIGN_TO, CAMPAIGN_DESC, CAMPAIGN_CREATED_BY, null, CAMPAIGN_COMPLETION_COUNT,
                        REWARD_TYPE_0_FIELD, REWARD_TYPE_MESSAGE_01),
                of(CONDITION_TYPE_SIGNUP, CONDITION_REWARD, CONDITION_COMPLETION_COUNT, CAMPAIGN_NAME, CAMPAIGN_REWARD,
                        CAMPAIGN_FROM, CAMPAIGN_TO, CAMPAIGN_DESC, CAMPAIGN_CREATED_BY, "", CAMPAIGN_COMPLETION_COUNT,
                        REWARD_TYPE_0_FIELD, REWARD_TYPE_MESSAGE_02),
                of(CONDITION_TYPE_SIGNUP, CONDITION_REWARD, CONDITION_COMPLETION_COUNT, CAMPAIGN_NAME, CAMPAIGN_REWARD,
                        CAMPAIGN_FROM, CAMPAIGN_TO, CAMPAIGN_DESC, CAMPAIGN_CREATED_BY, "NoFixed",
                        CAMPAIGN_COMPLETION_COUNT,
                        REWARD_TYPE_0_FIELD, REWARD_TYPE_MESSAGE_03)
        );
    }

    private static Stream campaign_ValidParameters() {
        return Stream.of(
                of(generateRandomString(), CAMPAIGN_FROM, CAMPAIGN_TO, CAMPAIGN_DESC),
                of(CAMPAIGN_NAME, CAMPAIGN_FROM, "2026-02-23T06:59:26.627Z", CAMPAIGN_DESC),
                of(CAMPAIGN_NAME, "2019-02-23T06:59:26.627Z", CAMPAIGN_TO, CAMPAIGN_DESC),
                of(CAMPAIGN_NAME, CAMPAIGN_FROM, CAMPAIGN_TO, generateRandomString())
        );
    }

    private static void deleteAllCampaigns() {
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
    void shouldCreateActiveCampaignSuccessfully() {
        String campaignId = createCampaign(campaign, bonusType, earnRule)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .body("CampaignId", hasLength(36))
                .extract()
                .path("CampaignId");

        getCampaignById(campaignId)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .body("Id", equalTo(campaignId))
                .body("IsEnabled", equalTo(true))
                .body("CampaignStatus", equalTo("Active"))
                .body("Conditions[0].Type", equalTo(CONDITION_TYPE_SIGNUP))
                .body("Conditions[0].ImmediateReward", equalTo(CONDITION_REWARD.toString()))
                .body("Conditions[0].CompletionCount", equalTo(CONDITION_COMPLETION_COUNT))
                .body("Name", equalTo(CAMPAIGN_NAME))
                ////????
                .body("Reward", equalTo(CAMPAIGN_REWARD.toString() + ".0"))
                .body("FromDate", containsString(CAMPAIGN_FROM.split(SPLIT_BY_SECONDS)[0]))
                .body("ToDate", containsString(CAMPAIGN_TO.split(SPLIT_BY_SECONDS)[0]))
                .body("Description", equalTo(CAMPAIGN_DESC))
                .body("RewardType", equalTo(REWARD_TYPE_FIXED.getCode()))
                .body("CompletionCount", equalTo(CAMPAIGN_COMPLETION_COUNT));
    }

    @Test
    @UserStoryId(storyId = {2648, 3871})
    void shouldCreateActiveCampaignSuccessfullyAndReturnModel() {
        campaign.setOrder(ORDER);
        String campaignId = createCampaign(campaign, bonusType, earnRule)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .body("CampaignId", hasLength(36))
                .extract()
                .path("CampaignId");

        val expectedResult = CampaignDetailResponseModel
                .campaignDetailResponseBuilder()
                .id(campaignId)
                .isEnabled(true)
                .amountInCurrency(0.0)
                .campaignStatus(CampaignStatus.ACTIVE)
                .conditions(new ConditionModel[]{ConditionModel
                        .conditionModelBuilder()
                        .type(CONDITION_TYPE_SIGNUP)
                        .immediateReward(CONDITION_REWARD.toString())
                        .completionCount(CONDITION_COMPLETION_COUNT)
                        .vertical(Vertical.REALESTATE)
                        .isHiddenType(false)
                        .build()})
                .createdBy(CAMPAIGN_CREATED_BY)
                .name(CAMPAIGN_NAME)
                .reward(CAMPAIGN_REWARD.toString())
                .fromDate(CAMPAIGN_FROM.split("Z")[0])
                .toDate(CAMPAIGN_TO.split("Z")[0])
                .description(CAMPAIGN_DESC)
                .rewardType(com.lykke.tests.api.service.campaigns.model.RewardType.FIXED)
                .completionCount(CAMPAIGN_COMPLETION_COUNT)
                .errorCode(CampaignServiceErrorCode.NONE)
                .build();

        val actualResult = getCampaignById(campaignId)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(CampaignDetailResponseModel.class);

        assertAll(
                () -> assertEquals(expectedResult.getAmountInCurrency(), actualResult.getAmountInCurrency()),
                () -> assertEquals(expectedResult.getAmountInTokens(), actualResult.getAmountInTokens()),
                () -> assertEquals(expectedResult.getCampaignStatus(), actualResult.getCampaignStatus()),
                () -> assertEquals(expectedResult.getCompletionCount(), actualResult.getCompletionCount()),
                () -> assertEquals(expectedResult.getCreatedBy(), actualResult.getCreatedBy()),
                () -> assertEquals(expectedResult.getDescription(), actualResult.getDescription()),
                () -> assertEquals(expectedResult.getFromDate(), actualResult.getFromDate()),
                () -> assertEquals(expectedResult.getIsEnabled(), actualResult.getIsEnabled()),
                () -> assertEquals(expectedResult.getName(), actualResult.getName()),
                () -> assertEquals(expectedResult.getId(), actualResult.getId()),
                () -> assertEquals(Double.valueOf(expectedResult.getReward()),
                        Double.valueOf(actualResult.getReward())),
                () -> assertEquals(expectedResult.getRewardType(), actualResult.getRewardType()),
                () -> assertEquals(expectedResult.getToDate(), actualResult.getToDate()),
                () -> assertEquals(expectedResult.getErrorCode(), actualResult.getErrorCode()),
                () -> assertEquals(expectedResult.getErrorMessage(), actualResult.getErrorMessage()),
                // FAL-3871
                () -> assertEquals(ORDER, actualResult.getOrder())
        );
    }

    @Test
    @UserStoryId(4345)
    void shouldCreateActiveCampaignWithVerticalSuccessfullyAndReturnModel() {
        campaign.setOrder(ORDER);
        String campaignId = createCampaign(campaign, bonusTypeWithVertical, earnRule)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .body("CampaignId", hasLength(36))
                .extract()
                .path("CampaignId");

        val expectedResult = CampaignDetailResponseModel
                .campaignDetailResponseBuilder()
                .id(campaignId)
                .isEnabled(true)
                .amountInCurrency(0.0)
                .campaignStatus(CampaignStatus.ACTIVE)
                .conditions(new ConditionModel[]{ConditionModel
                        .conditionModelBuilder()
                        .type(CONDITION_TYPE_SIGNUP)
                        .immediateReward(CONDITION_REWARD.toString())
                        .completionCount(CONDITION_COMPLETION_COUNT)
                        .vertical(Vertical.REALESTATE)
                        .isHiddenType(false)
                        .build()})
                .createdBy(CAMPAIGN_CREATED_BY)
                .name(CAMPAIGN_NAME)
                .reward(CAMPAIGN_REWARD.toString())
                .fromDate(CAMPAIGN_FROM.split("Z")[0])
                .toDate(CAMPAIGN_TO.split("Z")[0])
                .description(CAMPAIGN_DESC)
                .rewardType(com.lykke.tests.api.service.campaigns.model.RewardType.FIXED)
                .completionCount(CAMPAIGN_COMPLETION_COUNT)
                .errorCode(CampaignServiceErrorCode.NONE)
                .build();

        val actualResult = getCampaignById(campaignId)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(CampaignDetailResponseModel.class);

        assertAll(
                () -> assertEquals(expectedResult.getAmountInCurrency(), actualResult.getAmountInCurrency()),
                () -> assertEquals(expectedResult.getAmountInTokens(), actualResult.getAmountInTokens()),
                () -> assertEquals(expectedResult.getCampaignStatus(), actualResult.getCampaignStatus()),
                () -> assertEquals(expectedResult.getCompletionCount(), actualResult.getCompletionCount()),
                () -> assertEquals(expectedResult.getCreatedBy(), actualResult.getCreatedBy()),
                () -> assertEquals(expectedResult.getDescription(), actualResult.getDescription()),
                () -> assertEquals(expectedResult.getFromDate(), actualResult.getFromDate()),
                () -> assertEquals(expectedResult.getIsEnabled(), actualResult.getIsEnabled()),
                () -> assertEquals(expectedResult.getName(), actualResult.getName()),
                () -> assertEquals(expectedResult.getId(), actualResult.getId()),
                () -> assertEquals(Double.valueOf(expectedResult.getReward()),
                        Double.valueOf(actualResult.getReward())),
                () -> assertEquals(expectedResult.getRewardType(), actualResult.getRewardType()),
                () -> assertEquals(expectedResult.getToDate(), actualResult.getToDate()),
                () -> assertEquals(expectedResult.getErrorCode(), actualResult.getErrorCode()),
                () -> assertEquals(expectedResult.getErrorMessage(), actualResult.getErrorMessage()),
                // FAL-3871
                () -> assertEquals(ORDER, actualResult.getOrder()),
                // FAL-4345
                () -> assertEquals(expectedResult.getConditions()[0].getVertical(),
                        actualResult.getConditions()[0].getVertical()),
                () -> assertEquals(expectedResult.getConditions()[0].isHiddenType(),
                        actualResult.getConditions()[0].isHiddenType())
        );
    }

    @Test
    @UserStoryId(storyId = {4060, 3871, 4345})
    void shouldCreateCampaignWithPercentageReward() {
        final int reward = 99;
        final int approximateReward = 80;
        campaign
                .setRewardType(RewardType.PERCENTAGE)
                .setReward(Double.valueOf(reward).toString())
                .setApproximateAward(Double.valueOf(approximateReward).toString())
                .setOrder(ORDER);
        val conditions = campaign.getConditions();
        conditions[0]
                .setApproximateAward(Double.valueOf(40).toString())
                .setRewardType(RewardType.PERCENTAGE);
        campaign.setConditions(conditions);
        String campaignId = createCampaign(campaign, bonusType, earnRule)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .body("CampaignId", hasLength(36))
                .extract()
                .path("CampaignId");

        val expectedResult = CampaignDetailResponseModel
                .campaignDetailResponseBuilder()
                .id(campaignId)
                .isEnabled(true)
                .campaignStatus(CampaignStatus.ACTIVE)
                .conditions(new ConditionModel[]{ConditionModel
                        .conditionModelBuilder()
                        .type(CONDITION_TYPE_SIGNUP)
                        .immediateReward(CONDITION_REWARD.toString())
                        .completionCount(CONDITION_COMPLETION_COUNT)
                        .vertical(Vertical.RETAIL)
                        .isHiddenType(true)
                        .build()})
                .createdBy(CAMPAIGN_CREATED_BY)
                .name(CAMPAIGN_NAME)
                .reward(Double.valueOf(reward).toString())
                .approximateAward(Double.valueOf(approximateReward).toString())
                .fromDate(CAMPAIGN_FROM.split("Z")[0])
                .toDate(CAMPAIGN_TO.split("Z")[0])
                .description(CAMPAIGN_DESC)
                .rewardType(RewardType.PERCENTAGE)
                .completionCount(CAMPAIGN_COMPLETION_COUNT)
                .errorCode(CampaignServiceErrorCode.NONE)
                .build();

        val actualResult = getCampaignById(campaignId)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(CampaignDetailResponseModel.class);

        assertAll(
                () -> assertEquals(0.0, actualResult.getAmountInCurrency()),
                () -> assertEquals(expectedResult.getAmountInTokens(), actualResult.getAmountInTokens()),
                () -> assertEquals(expectedResult.getCampaignStatus(), actualResult.getCampaignStatus()),
                () -> assertEquals(expectedResult.getCompletionCount(), actualResult.getCompletionCount()),
                () -> assertEquals(expectedResult.getCreatedBy(), actualResult.getCreatedBy()),
                () -> assertEquals(expectedResult.getDescription(), actualResult.getDescription()),
                () -> assertEquals(expectedResult.getFromDate().substring(0, 20),
                        actualResult.getFromDate().substring(0, 20)),
                () -> assertEquals(expectedResult.getIsEnabled(), actualResult.getIsEnabled()),
                () -> assertEquals(expectedResult.getName(), actualResult.getName()),
                () -> assertEquals(expectedResult.getId(), actualResult.getId()),
                () -> assertEquals(Double.valueOf(expectedResult.getReward()),
                        Double.valueOf(actualResult.getReward())),
                () -> assertEquals(expectedResult.getRewardType(), actualResult.getRewardType()),
                () -> assertEquals(expectedResult.getToDate(), actualResult.getToDate()),
                () -> assertEquals(expectedResult.getErrorCode(), actualResult.getErrorCode()),
                () -> assertEquals(expectedResult.getErrorMessage(), actualResult.getErrorMessage()),
                () -> assertNotNull(actualResult.getApproximateAward()),
                () -> assertNotNull(actualResult.getConditions()[0].getApproximateAward()),
                // FAL-3871
                () -> assertEquals(ORDER, actualResult.getOrder()),
                // FAL-4345
                () -> assertEquals(expectedResult.getConditions()[0].getVertical(),
                        actualResult.getConditions()[0].getVertical()),
                () -> assertEquals(expectedResult.getConditions()[0].isHiddenType(),
                        actualResult.getConditions()[0].isHiddenType())
        );
    }

    @UserStoryId(429)
    @ParameterizedTest(name =
            "Run {index}: conditionType={0}, conditionReward={1}, completionCount={2}, campaignName={3}," +
                    "campaignReward={4}, campaignFrom={5}, campaignTo={6}, campaignDesc={7}, createdBy={8}, " +
                    "rewardType={9}, campaignCompletionCount={10}, field={11}, errorMessage={12}, message={13}")
    @MethodSource("campaign_InvalidParameters")
    void shouldNotCreateCampaignWhenRequiredFieldsAreEmptyOrValueIsNotValid(String conditionType,
            Integer conditionReward, Integer completionCount,
            String campaignName, Double campaignReward, String campaignFrom, String campaignTo,
            String campaignDesc, String createdBy, RewardType rewardType, Integer campaignCompletionCount, String field,
            String errorMessage, String message) {
        val bonusType = ConditionCreateModel
                .conditionCreateBuilder()
                .type(conditionType)
                .immediateReward(conditionReward.toString())
                .completionCount(completionCount)
                .build();

        val campaign = Campaign
                .campaignBuilder()
                .name(campaignName)
                .reward(campaignReward.toString())
                .fromDate(campaignFrom)
                .toDate(campaignTo)
                .description(campaignDesc)
                .conditions(createConditionArray(bonusType))
                .createdBy(createdBy)
                .rewardType(rewardType)
                .completionCount(campaignCompletionCount)
                .order(ORDER)
                .build();

        createCampaign(campaign, bonusType, earnRule)
                .then()
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .body(ERROR_MESSAGE_FIELD, equalTo(errorMessage))
                .body(MODEL_ERRORS_FIELD + "." + field, equalTo(message));
    }

    @UserStoryId(429)
    @ParameterizedTest(name =
            "Run {index}: conditionType={0}, conditionReward={1}, completionCount={2}, campaignName={3}," +
                    "campaignReward={4}, campaignFrom={5}, campaignTo={6}, campaignDesc={7}, createdBy={8}, " +
                    "rewardType={9}, campaignCompletionCount={10}, field={11}, message={13}")
    @MethodSource("campaign_InvalidValues")
    void shouldNotCreateCampaignWhenValueTypeIsNotCorrect(String conditionType,
            Integer conditionReward, Integer completionCount,
            String campaignName, Double campaignReward, String campaignFrom, String campaignTo,
            String campaignDesc, String createdBy, RewardType rewardType, Integer campaignCompletionCount,
            String field, String message) {
        val bonusType = ConditionCreateModel
                .conditionCreateBuilder()
                .type(conditionType)
                .immediateReward(conditionReward.toString())
                .completionCount(completionCount)
                .build();

        Campaign campaign = Campaign
                .campaignBuilder()
                .name(campaignName)
                .reward(campaignReward.toString())
                .fromDate(campaignFrom)
                .toDate(campaignTo)
                .description(campaignDesc)
                .conditions(createConditionArray(bonusType))
                .createdBy(createdBy)
                .rewardType(rewardType)
                .completionCount(campaignCompletionCount)
                .build();

        createCampaign(campaign, bonusType, earnRule)
                .then()
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .body(ERROR_MESSAGE_FIELD, containsString(message))
                .body(MODEL_ERRORS_FIELD + "." + field, containsString(message));
    }

    @Test
    @Tag(SMOKE_TEST)
    void shouldGetCampaignById() {
        val campaignId = createDefaultCampaign();

        getCampaignById(campaignId)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .body("Id", equalTo(campaignId))
                .body("CreatedBy", equalTo(CAMPAIGN_CREATED_BY))
                .body("CampaignStatus", equalTo("Active"))
                .body("Conditions[0].Type", equalTo(CONDITION_TYPE_SIGNUP))
                .body("Conditions[0].ImmediateReward", equalTo(CONDITION_REWARD.toString()))
                .body("Conditions[0].CompletionCount", equalTo(CONDITION_COMPLETION_COUNT))
                .body("Name", equalTo(CAMPAIGN_NAME))
                .body("Reward", equalTo(CAMPAIGN_REWARD_WITH_PERCENTAGE.toString()))
                .body("FromDate", containsString(CAMPAIGN_FROM.split(SPLIT_BY_SECONDS)[0]))
                .body("ToDate", containsString(CAMPAIGN_TO.split(SPLIT_BY_SECONDS)[0]))
                .body("Description", equalTo(CAMPAIGN_DESC))
                .body("RewardType", equalTo(REWARD_TYPE_FIXED.getCode()))
                .body("CompletionCount", equalTo(CAMPAIGN_COMPLETION_COUNT))
                .body("CreationDate", containsString(Instant.now().toString().substring(0, 17)));
    }

    @Test
    @Tag(SMOKE_TEST)
    void shouldGetAllCampaigns() { // TODO: add 1:1 comparison to DB
        val numberOfCampaignsToBeCreated = 3;
        createMultipleCampaigns(numberOfCampaignsToBeCreated);

        getCampaigns()
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .body("Campaigns", hasSize(greaterThanOrEqualTo(numberOfCampaignsToBeCreated)));
    }

    @Test
    void shouldDeleteCampaignById() {
        String campaignId = createDefaultCampaign();

        int initialNumberOfCampaigns = getCampaigns().jsonPath().getList("Campaigns").size();

        deleteCampaign(campaignId)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .body(ERROR_CODE_FIELD, equalTo(ERROR_CODE_NONE))
                .body(ERROR_MESSAGE, nullValue());

        int secondaryNumberOfCampaigns = getCampaigns().jsonPath().getList("Campaigns").size();
        assertEquals(initialNumberOfCampaigns, secondaryNumberOfCampaigns + 1);
    }

    @Test
    void shouldNotUpdateActiveCampaignRewardAndCondition() {
        final Integer UPD_CONDITION_REWARD = 10;
        val UPD_CONDITION_COMPLETION_COUNT = 8;

        final Integer UPD_CAMPAIGN_REWARD = 1000;

        val campaignId = getCampaignId(campaign, bonusType, earnRule);
        val conditionId = getCampaignConditionId(campaignId);

        val newBonusType = ConditionCreateModel
                .conditionCreateBuilder()
                .completionCount(UPD_CONDITION_COMPLETION_COUNT)
                .immediateReward(UPD_CONDITION_REWARD.toString())
                .type(CONDITION_TYPE_SIGNUP)
                .build();

        val newCampaign = baseEditCampaign.reward(UPD_CAMPAIGN_REWARD.toString()).build();

        updateCampaignById(newCampaign, newBonusType, campaignId, conditionId, earnRule, getEarnRuleId(campaignId))
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .body("ErrorMessage",
                        equalTo("Campaign Reward must not be changed\nCampaign Conditions must not be changed"));
    }

    @Disabled("EntityNotValid")
    @ParameterizedTest(name = "Run {index}: campaignName={0}, campaignFrom={1}, campaignTo={2}, campaignDesc={3}")
    @MethodSource("campaign_ValidParameters")
    @UserStoryId(3871)
    void shouldUpdateNameDescriptionStartAndEndDateOfActiveCampaign(String campaignName, String campaignFrom,
            String campaignTo, String campaignDesc) {
        val campaignId = getCampaignId(campaign, bonusType, earnRule);
        val conditionId = getCampaignConditionId(campaignId);

        val newCampaign = CampaignEditModel
                .campaignBuilder()
                .name(campaignName)
                .reward(String.valueOf(CAMPAIGN_REWARD))
                .fromDate(campaignFrom)
                .toDate(campaignTo)
                .description(campaignDesc)
                // originally
                .conditions(createConditionEditArray(bonusType))
                // experimantally
                .conditions(createConditionEditArray(campaign.getConditions()[0]))
                .rewardType(REWARD_TYPE_FIXED)
                .completionCount(CAMPAIGN_COMPLETION_COUNT)
                .order(ORDER + ORDER)
                .build();

        updateCampaignById(newCampaign, bonusType, campaignId, conditionId, earnRule, getEarnRuleId(campaignId))
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .body(ERROR_CODE_FIELD, equalTo(ERROR_CODE_NONE))
                .body(ERROR_MESSAGE, nullValue());

        getCampaignById(campaignId)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .body("Id", equalTo(campaignId))
                .body("CreatedBy", equalTo(CAMPAIGN_CREATED_BY))
                .body("CampaignStatus", equalTo("Active"))
                .body("Conditions[0].Type", equalTo(CONDITION_TYPE_SIGNUP))
                .body("Conditions[0].ImmediateReward", equalTo(CONDITION_REWARD.toString()))
                .body("Conditions[0].CompletionCount", equalTo(CONDITION_COMPLETION_COUNT))
//                .body("Name", equalTo(campaignName))
//                .body("Reward", equalTo(CAMPAIGN_REWARD.toString()))
//                .body("FromDate", containsString(campaignFrom.split(SPLIT_BY_SECONDS)[0]))
//                .body("ToDate", containsString(campaignTo.split(SPLIT_BY_SECONDS)[0]))
                .body("RewardType", equalTo(REWARD_TYPE_FIXED.getCode()))
                .body("Description", equalTo(campaignDesc))
                .body("Order", equalTo(ORDER + ORDER));
    }

    @Test
    void disableActiveCampaign_campaignStatusIsInactiveAndIsEnabledIsFalse() {
        val baseCamp = baseCampaign;
        String campaignId = getCampaignId(baseCamp.build(), bonusType, earnRule);
        String conditionId = getCampaignConditionId(campaignId);

        // Disable campaigns by setting isEnabled to false
        val newCampaign = baseEditCampaign.build();

        updateCampaignById(newCampaign, bonusType, campaignId, conditionId, earnRule, getEarnRuleId(campaignId))
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .body(ERROR_CODE_FIELD, equalTo("None"))
                .body(ERROR_MESSAGE, nullValue());

        getCampaignById(campaignId)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .body("IsEnabled", equalTo(false))
                .body("CampaignStatus", equalTo("Inactive"));
    }

    @ParameterizedTest(name = "Run {index}: campaignName={0}, campaignFrom={1}, campaignTo={2}, campaignDesc={3}")
    @MethodSource("campaign_ValidParameters")
    @UserStoryId(3871)
    void enableCampaign_shouldUpdateNameDescriptionStartAndEndDate(String campaignName, String campaignFrom,
            String campaignTo, String campaignDesc) {

        campaign.setOrder(ORDER);
        String campaignId = getCampaignId(campaign, bonusType, earnRule);
        String conditionId = getCampaignConditionId(campaignId);

        //Disable campaigns by setting isEnabled to false
        val disableCampaign = baseEditCampaign.build();

        //Enable campaigns by setting isEnabled to true
        val enableCampaign = baseEditCampaign.build();

        updateCampaignById(disableCampaign, bonusType, campaignId, conditionId, earnRule, getEarnRuleId(campaignId));

        updateCampaignById(enableCampaign, bonusType, campaignId, conditionId, earnRule, getEarnRuleId(campaignId));

        getCampaignById(campaignId)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .body("IsEnabled", equalTo(true))
                .body("CampaignStatus", equalTo("Active"));

        val newCampaign = CampaignEditModel
                .campaignBuilder()
                .name(campaignName)
                .reward(String.valueOf(CAMPAIGN_REWARD))
                .fromDate(campaignFrom)
                .toDate(campaignTo)
                .description(campaignDesc)
                .conditions(createConditionEditArray(bonusType))
                .rewardType(REWARD_TYPE_FIXED)
                .completionCount(CAMPAIGN_COMPLETION_COUNT)
                .order(ORDER + ORDER)
                .build();

        //Can update fields when the campaigns is activated
        updateCampaignById(newCampaign, bonusType, campaignId, conditionId, earnRule, getEarnRuleId(campaignId))
                .then()
                .assertThat()
                .statusCode(SC_OK);

        getCampaignById(campaignId)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .body("Id", equalTo(campaignId))
                .body("CreatedBy", equalTo(CAMPAIGN_CREATED_BY))
                .body("CampaignStatus", equalTo("Active"))
                .body("Conditions[0].Type", equalTo(CONDITION_TYPE_SIGNUP))
                .body("Conditions[0].ImmediateReward", equalTo(CONDITION_REWARD.toString()))
                .body("Conditions[0].CompletionCount", equalTo(CONDITION_COMPLETION_COUNT))
//                .body("Name", equalTo(campaignName))
                .body("Reward", equalTo(String.valueOf(CAMPAIGN_REWARD.intValue())))
                .body("FromDate", containsString(campaignFrom.split(SPLIT_BY_SECONDS)[0]))
                .body("ToDate", containsString(campaignTo.split(SPLIT_BY_SECONDS)[0]))
                .body("Description", equalTo(campaignDesc))
                .body("RewardType", equalTo(REWARD_TYPE_FIXED.getCode()))
                .body("CompletionCount", equalTo(CAMPAIGN_COMPLETION_COUNT))
                .body("Order", equalTo(ORDER));
    }

    @Test
    void shouldCreateCampaignWithNoEndDate() {
        Campaign campaign = baseCampaign.toDate("").build();

        createCampaign(campaign, bonusType, earnRule)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .body("CampaignId", hasLength(36))
                .body(ERROR_CODE_FIELD, equalTo(ERROR_CODE_NONE))
                .body(ERROR_MESSAGE, nullValue());
    }

    @Test
    void campaignNameCanContainAllTypesOfCharacters() {
        String campaignName = "C@mP@1gn_N#m3%&"; //TODO: generate the name
        Campaign campaign = baseCampaign.name(campaignName).build();

        createCampaign(campaign, bonusType, earnRule)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .body("CampaignId", hasLength(36));
    }

    @Test
    @Disabled("There is not campaigns which can be completed more than once")
    void shouldNotCompleteCampaignMoreThanOnceGivenThatNumberOfCompletionIsOne() {
        //TODO: You cannot complete it more than once (given the number of completions is 1)
    }

    @ParameterizedTest
    @CsvSource({
            "C@mP@1gn_N#m3%&",
            "https://www.mavn.com"
    })
    void campaignDescriptionCanContainAllTypesOfCharacters(String campaignDescription) {
        Campaign campaign = baseCampaign.description(campaignDescription).build();

        createCampaign(campaign, bonusType, earnRule)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .body("CampaignId", hasLength(36));
    }

    @Test
    @Disabled
    void campaignShouldHaveAtLeastOneCondition() {
        //TODO
    }

    @Test
    @Disabled
    void shouldCreateCampaignWithMultipleConditions() {
        Integer immediateReward = 10;
        Integer completionCount = 2;

        val bonusType1 = ConditionCreateModel
                .conditionCreateBuilder()
                .type(CONDITION_TYPE_REFERRAL)
                .immediateReward(immediateReward.toString())
                .completionCount(completionCount)
                .build();

        val campaign = baseCampaign.conditions(createConditionArray(bonusType, bonusType1)).build();

        createCampaign(campaign, bonusType, bonusType1, earnRule)
                .then()
                .assertThat()
                .statusCode(SC_OK);

    }

    ////????
    @Test
    void shouldNotCreateCampaignWithTwoOrMoreConditionsOfSameType() {
        val campaign = baseCampaign.conditions(createConditionArray(bonusType, bonusType)).build();

        createCampaign(campaign, bonusType, bonusType, earnRule)
                .then()
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .body(ERROR_MESSAGE_FIELD,
                        equalTo(CONDITION_TYPE_MUST_NOT_HAVE_TWO_OR_MORE_CONDITIONS_OF_THE_SAME_TYPE_MESSAGE))
                .body(MODEL_ERRORS_FIELD + "." + CONDITION_0_FIELD,
                        equalTo(CONDITION_TYPE_MUST_NOT_HAVE_TWO_OR_MORE_CONDITIONS_OF_THE_SAME_TYPE_MESSAGE));
    }

    @Test
    void shouldNotCreateCampaignWithInvalidConditionType() {
        String invalidConditionType = "invalid_cond_type";

        val bonusType = baseCondition.type(invalidConditionType).build();

        createCampaign(campaign, bonusType, earnRule)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .body(ERROR_CODE_FIELD, equalTo("EntityNotValid"))
                .body(ERROR_MESSAGE_FIELD,
                        equalTo(String.format(CONDITION_TYPE_S_IS_NOT_VALID_MESSAGE, invalidConditionType)));
    }

    @Test
    void shouldNotCreateCampaignWithEmptyConditionType() {
        String invalidConditionType = "";

        val bonusType = baseCondition.type(invalidConditionType).build();

        createCampaign(campaign, bonusType, earnRule)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .body(ERROR_CODE_FIELD, equalTo("EntityNotValid"))
                .body(ERROR_MESSAGE_FIELD,
                        equalTo(String.format(CONDITION_TYPE_S_IS_NOT_VALID_MESSAGE, invalidConditionType)));
    }

    @Test
    void shouldNotCreateCampaignWithConditionTypeNull() {
        String invalidConditionType = null;

        val bonusType = baseCondition.type(invalidConditionType).build();

        createCampaign(campaign, bonusType, earnRule)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .body(ERROR_CODE_FIELD, equalTo("EntityNotValid"))
                .body(ERROR_MESSAGE_FIELD, equalTo(CONDITION_TYPE_IS_NOT_VALID_MESSAGE));
    }

    @Disabled
    @Test
    @UserStoryId(storyId = 1225)
    void conditionAndCampaignCompletionCountCanBeNull() {
        val condition = baseCondition.build();
        val campaign = baseCampaign.build();

        val campaignId = getCampaignId(campaign, condition, earnRule);

        getCampaignById(campaignId)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .body("Conditions[0].CompletionCount", nullValue())
                .body("CompletionCount", nullValue());
    }

    @Test
    @UserStoryId(storyId = 1225)
    void shouldAcceptDecimalPointNumbersWhenRewardTypeIsPercentage() {
        val campaignReward = 123.54f;
        val campaign = baseCampaign.rewardType(REWARD_TYPE_PERCENTAGE).reward(String.valueOf(campaignReward)).build();

        val campaignId = getCampaignId(campaign, bonusType, earnRule);

        getCampaignById(campaignId)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .body("RewardType", equalTo(REWARD_TYPE_PERCENTAGE))
                .body("Reward", equalTo(campaignReward));
    }

    @Test
    @UserStoryId(4060)
    void shouldCreateCampaignWithApproximateAward() {
        val campaign = baseCampaign
                .rewardType(REWARD_TYPE_PERCENTAGE)
                .reward(Double.valueOf(90).toString())
                .approximateAward(Double.valueOf(30).toString())
                .build();

        bonusType
                .setApproximateAward(Double.valueOf(80).toString())
                .setRewardType(RewardType.PERCENTAGE)
                .setImmediateReward(Double.valueOf(95).toString());

        val campaignId = getCampaignId(campaign, bonusType, earnRule);

        val actualResult = getCampaignById(campaignId)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(CampaignDetailResponseModel.class);

        assertAll(
                () -> assertEquals(Double.valueOf(campaign.getApproximateAward()),
                        Double.valueOf(actualResult.getApproximateAward())),
                () -> assertEquals(Double.valueOf(bonusType.getApproximateAward()),
                        Double.valueOf(actualResult.getConditions()[0].getApproximateAward()))
        );
    }
}
