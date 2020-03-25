package com.lykke.tests.api.service.campaigns;

import static com.lykke.api.testing.api.common.GenerateUtils.generateRandomString;
import static com.lykke.tests.api.common.CommonConsts.ERROR_CODE_FIELD;
import static com.lykke.tests.api.service.campaigns.CampaignUtils.createCampaign;
import static com.lykke.tests.api.service.campaigns.CampaignUtils.createConditionArray;
import static com.lykke.tests.api.service.campaigns.CampaignUtils.createConditionEditArray;
import static com.lykke.tests.api.service.campaigns.CampaignUtils.getCampaignById;
import static com.lykke.tests.api.service.campaigns.CampaignUtils.getCampaignId;
import static com.lykke.tests.api.service.campaigns.CampaignUtils.getEarnRuleId;
import static com.lykke.tests.api.service.campaigns.CampaignUtils.updateCampaignById;
import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.text.CharSequenceLength.hasLength;
import static org.junit.jupiter.params.provider.Arguments.of;

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
import java.util.stream.Stream;
import lombok.val;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

public class PendingCampaignsCrudTests extends BaseApiTest {

    private static final String CONDITION_TYPE_SIGNUP = "signup";
    private static final Integer CONDITION_REWARD = 5;
    private static final int CONDITION_COMPLETION_COUNT = 4;
    private static final String CAMPAIGN_NAME = generateRandomString();
    private static final String CAMPAIGN_CREATED_BY = generateRandomString();
    private static final String CAMPAIGN_FROM = "2022-05-23T06:59:26.627Z";
    private static final String CAMPAIGN_TO = "2025-05-23T06:59:26.627Z";
    private static final String CAMPAIGN_DESC = generateRandomString();
    private static final String CAMPAIGN_REWARD = "500";
    private static final int CAMPAIGN_COMPLETION_COUNT = 2;
    private static final RewardType REWARD_TYPE = RewardType.FIXED;
    private static final String CONDITION_TYPE_IS_NOT_VALID_MESSAGE = "Condition Type  is not a valid Type";
    private static final String NAME_MUST_BE_AT_LEAST_3_CHAR_MESSAGE = "The length of 'Name' must be at least 3 characters. You entered 0 characters.";
    private static final String NAME_MUST_NOT_BE_EMPTY_MESSAGE = "'Name' must not be empty.";
    private static final String CAMPAIGN_REWARD_MUST_BE_GREATER_THAN_0_MESSAGE = "Campaign Reward must be greater than or equal to 0";
    private static final String FROM_DATE_VALIDATION_MESSAGE_01 = "Error converting value {null} to type 'System.DateTime'.";
    private static final String FROM_DATE_VALIDATION_MESSAGE_02 = "Could not convert string to DateTime: invalid_date.";
    private static final String TO_DATE_VALIDATION_MESSAGE = "Could not convert string to DateTime: invalid_date.";
    private static final String REWARD_TYPE_VALIDATION_MESSAGE_01 =
            "Cannot convert null value to Lykke.Service.Campaign.Client.Models.Enums.RewardType.";
    private static final String REWARD_TYPE_VALIDATION_MESSAGE_02 =
            "Error converting value \"\" to type 'Lykke.Service.Campaign.Client.Models.Enums.RewardType'.";
    private static final String REWARD_TYPE_VALIDATION_MESSAGE_03 =
            "Error converting value \"NoFixed\" to type 'Lykke.Service.Campaign.Client.Models.Enums.RewardType'.";
    private static final String DESCRIPTION_MUST_NOT_BE_EMPTY_MESSAGE = "The length of 'Description' must be at least 3 characters. You entered 0 characters.";
    private static final String CAMPAIGN_COMPLETION_COUNT_MUST_BE_EITHER_NULL_OR_GREATER_THAN_0 = "Campaign completion must be either null or greater than 0.";
    private static final String CONDITION_TYPE_S_IS_NOT_VALID_MESSAGE = "Condition Type %s is not a valid Type";
    private static final String DESCRIPTION_MUST_BE_AT_LEAST_3_CHAR_2_MESSAGE = "The length of 'Description' must be at least 3 characters. You entered 2 characters.";
    private static final String DESCRIPTION_MUST_BE_AT_LEAST_3_CHAR_MESSAGE = "The length of 'Description' must be at least 3 characters. You entered 0 characters.";
    private static final String ERROR_CODE = "ErrorCode";
    private static final String ERROR_MESSAGE = "ErrorMessage";
    private static final String MODEL_ERRORS_FIELD = "ModelErrors";
    private static final String TYPE_0_FIELD = "Type[0]";
    private static final String NAME_0_FIELD = "Name[0]";
    private static final String NAME_1_FIELD = "Name[1]";
    private static final String REWARD_0_FIELD = "Reward[0]";
    private static final String TO_DATE_0_FIELD = "ToDate[0]";
    private static final String FROM_DATE_0_FIELD = "FromDate[0]";
    private static final String DESCRIPTION_0_FIELD = "Description[0]";
    private static final String DESCRIPTION_1_FIELD = "Description[1]";
    private static final String REWARD_TYPE_0_FIELD = "RewardType[0]";
    private static final String COMLETION_COUNT_O_FIELD = "CompletionCount[0]";
    private static Campaign campaign;
    private static ConditionCreateModel bonusType;
    private static EarnRule earnRule;
    private static ConditionCreateModel.ConditionCreateModelBuilder baseCondition;
    private static Campaign.CampaignBuilder baseCampaign;
    private static CampaignEditModel.CampaignEditModelBuilder baseEditCampaign;

    @BeforeAll
    static void dataSetup() {
        baseCondition = ConditionCreateModel
                .conditionCreateBuilder()
                .type(CONDITION_TYPE_SIGNUP)
                .immediateReward(CONDITION_REWARD.toString())
                .completionCount(CONDITION_COMPLETION_COUNT);

        bonusType = baseCondition
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
                .rewardType(REWARD_TYPE)
                .completionCount(CAMPAIGN_COMPLETION_COUNT);

        baseEditCampaign = CampaignEditModel
                .campaignBuilder()
                .name(CAMPAIGN_NAME)
                .reward(String.valueOf(CAMPAIGN_REWARD))
                .fromDate(CAMPAIGN_FROM)
                .toDate(CAMPAIGN_TO)
                .description(CAMPAIGN_DESC)
                .conditions(createConditionEditArray(bonusType))
                .rewardType(REWARD_TYPE)
                .completionCount(CAMPAIGN_COMPLETION_COUNT);

        campaign = baseCampaign
                .build();

        earnRule = EarnRule
                .builder()
                .ruleContentType(RuleContentType.TITLE)
                .localization(Localization.EN)
                .value(FakerUtils.title)
                .build();
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
                        CAMPAIGN_FROM, CAMPAIGN_TO, CAMPAIGN_DESC, REWARD_TYPE, CAMPAIGN_COMPLETION_COUNT,
                        NAME_0_FIELD, NAME_MUST_NOT_BE_EMPTY_MESSAGE, NAME_MUST_NOT_BE_EMPTY_MESSAGE),
                of(CONDITION_TYPE_SIGNUP, CONDITION_REWARD, CONDITION_COMPLETION_COUNT, "", CAMPAIGN_REWARD,
                        CAMPAIGN_FROM, CAMPAIGN_TO, CAMPAIGN_DESC, REWARD_TYPE, CAMPAIGN_COMPLETION_COUNT,
                        NAME_0_FIELD, NAME_MUST_NOT_BE_EMPTY_MESSAGE, NAME_MUST_NOT_BE_EMPTY_MESSAGE),
                of(CONDITION_TYPE_SIGNUP, CONDITION_REWARD, CONDITION_COMPLETION_COUNT, "", CAMPAIGN_REWARD,
                        CAMPAIGN_FROM, CAMPAIGN_TO, CAMPAIGN_DESC, REWARD_TYPE, CAMPAIGN_COMPLETION_COUNT,
                        NAME_1_FIELD, NAME_MUST_NOT_BE_EMPTY_MESSAGE, NAME_MUST_BE_AT_LEAST_3_CHAR_MESSAGE),
                of(CONDITION_TYPE_SIGNUP, CONDITION_REWARD, CONDITION_COMPLETION_COUNT, CAMPAIGN_NAME, -1f,
                        CAMPAIGN_FROM, CAMPAIGN_TO, CAMPAIGN_DESC, REWARD_TYPE, CAMPAIGN_COMPLETION_COUNT,
                        REWARD_0_FIELD, CAMPAIGN_REWARD_MUST_BE_GREATER_THAN_0_MESSAGE,
                        CAMPAIGN_REWARD_MUST_BE_GREATER_THAN_0_MESSAGE),
                of(CONDITION_TYPE_SIGNUP, CONDITION_REWARD, CONDITION_COMPLETION_COUNT, CAMPAIGN_NAME, CAMPAIGN_REWARD,
                        CAMPAIGN_FROM, CAMPAIGN_TO, "", REWARD_TYPE, CAMPAIGN_COMPLETION_COUNT,
                        DESCRIPTION_0_FIELD, DESCRIPTION_MUST_NOT_BE_EMPTY_MESSAGE,
                        DESCRIPTION_MUST_NOT_BE_EMPTY_MESSAGE),
                of(CONDITION_TYPE_SIGNUP, CONDITION_REWARD, CONDITION_COMPLETION_COUNT, CAMPAIGN_NAME, CAMPAIGN_REWARD,
                        CAMPAIGN_FROM, CAMPAIGN_TO, "12", REWARD_TYPE, CAMPAIGN_COMPLETION_COUNT,
                        DESCRIPTION_0_FIELD, DESCRIPTION_MUST_BE_AT_LEAST_3_CHAR_2_MESSAGE,
                        DESCRIPTION_MUST_BE_AT_LEAST_3_CHAR_2_MESSAGE),
                of(CONDITION_TYPE_SIGNUP, CONDITION_REWARD, CONDITION_COMPLETION_COUNT, CAMPAIGN_NAME, CAMPAIGN_REWARD,
                        CAMPAIGN_FROM, CAMPAIGN_TO, CAMPAIGN_DESC, REWARD_TYPE, 0, COMLETION_COUNT_O_FIELD,
                        CAMPAIGN_COMPLETION_COUNT_MUST_BE_EITHER_NULL_OR_GREATER_THAN_0,
                        CAMPAIGN_COMPLETION_COUNT_MUST_BE_EITHER_NULL_OR_GREATER_THAN_0),
                of(CONDITION_TYPE_SIGNUP, CONDITION_REWARD, CONDITION_COMPLETION_COUNT, CAMPAIGN_NAME, CAMPAIGN_REWARD,
                        CAMPAIGN_FROM, CAMPAIGN_TO, CAMPAIGN_DESC, REWARD_TYPE, -1, COMLETION_COUNT_O_FIELD,
                        CAMPAIGN_COMPLETION_COUNT_MUST_BE_EITHER_NULL_OR_GREATER_THAN_0,
                        CAMPAIGN_COMPLETION_COUNT_MUST_BE_EITHER_NULL_OR_GREATER_THAN_0)
        );
    }

    private static Stream campaign_InvalidValues() {
        return Stream.of(
                of(CONDITION_TYPE_SIGNUP, CONDITION_REWARD, CONDITION_COMPLETION_COUNT, CAMPAIGN_NAME, CAMPAIGN_REWARD,
                        null, CAMPAIGN_TO, CAMPAIGN_DESC, REWARD_TYPE, CAMPAIGN_COMPLETION_COUNT,
                        FROM_DATE_0_FIELD, FROM_DATE_VALIDATION_MESSAGE_01),
                of(CONDITION_TYPE_SIGNUP, CONDITION_REWARD, CONDITION_COMPLETION_COUNT, CAMPAIGN_NAME, CAMPAIGN_REWARD,
                        "", CAMPAIGN_TO, CAMPAIGN_DESC, REWARD_TYPE, CAMPAIGN_COMPLETION_COUNT,
                        FROM_DATE_0_FIELD, FROM_DATE_VALIDATION_MESSAGE_01),
                of(CONDITION_TYPE_SIGNUP, CONDITION_REWARD, CONDITION_COMPLETION_COUNT, CAMPAIGN_NAME, CAMPAIGN_REWARD,
                        "invalid_date", CAMPAIGN_TO, CAMPAIGN_DESC, REWARD_TYPE, CAMPAIGN_COMPLETION_COUNT,
                        FROM_DATE_0_FIELD, FROM_DATE_VALIDATION_MESSAGE_02),
                of(CONDITION_TYPE_SIGNUP, CONDITION_REWARD, CONDITION_COMPLETION_COUNT, CAMPAIGN_NAME, CAMPAIGN_REWARD,
                        CAMPAIGN_FROM, "invalid_date", CAMPAIGN_DESC, REWARD_TYPE, CAMPAIGN_COMPLETION_COUNT,
                        TO_DATE_0_FIELD, TO_DATE_VALIDATION_MESSAGE),
                of(CONDITION_TYPE_SIGNUP, CONDITION_REWARD, CONDITION_COMPLETION_COUNT, CAMPAIGN_NAME, CAMPAIGN_REWARD,
                        CAMPAIGN_FROM, CAMPAIGN_TO, CAMPAIGN_DESC, null, CAMPAIGN_COMPLETION_COUNT,
                        REWARD_TYPE_0_FIELD, REWARD_TYPE_VALIDATION_MESSAGE_01),
                of(CONDITION_TYPE_SIGNUP, CONDITION_REWARD, CONDITION_COMPLETION_COUNT, CAMPAIGN_NAME, CAMPAIGN_REWARD,
                        CAMPAIGN_FROM, CAMPAIGN_TO, CAMPAIGN_DESC, "", CAMPAIGN_COMPLETION_COUNT,
                        REWARD_TYPE_0_FIELD, REWARD_TYPE_VALIDATION_MESSAGE_02),
                of(CONDITION_TYPE_SIGNUP, CONDITION_REWARD, CONDITION_COMPLETION_COUNT, CAMPAIGN_NAME, CAMPAIGN_REWARD,
                        CAMPAIGN_FROM, CAMPAIGN_TO, CAMPAIGN_DESC, "NoFixed", CAMPAIGN_COMPLETION_COUNT,
                        REWARD_TYPE_0_FIELD, REWARD_TYPE_VALIDATION_MESSAGE_03)
        );
    }

    private static Stream campaign_ValidParameters() {
        return Stream.of(
                of(generateRandomString(), CAMPAIGN_FROM, CAMPAIGN_TO, CAMPAIGN_DESC, CAMPAIGN_REWARD),
                of(CAMPAIGN_NAME, CAMPAIGN_FROM, "2026-02-23T06:59:26.627Z", CAMPAIGN_DESC, CAMPAIGN_REWARD),
                of(CAMPAIGN_NAME, "2020-02-23T06:59:26.627Z", CAMPAIGN_TO, CAMPAIGN_DESC, CAMPAIGN_REWARD),
                of(CAMPAIGN_NAME, CAMPAIGN_FROM, CAMPAIGN_TO, generateRandomString(), CAMPAIGN_REWARD),
                of(CAMPAIGN_NAME, CAMPAIGN_FROM, CAMPAIGN_TO, CAMPAIGN_DESC, 120f)
        );
    }

    @Test
    void shouldCreatePendingCampaignSuccessfully() {
        val campaignId = createCampaign(campaign, bonusType, earnRule)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .body("CampaignId", hasLength(36))
                .extract()
                .jsonPath()
                .getString("CampaignId");

        getCampaignById(campaignId)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .body("Id", equalTo(campaignId))
                .body("IsEnabled", equalTo(true))
                .body("CampaignStatus", equalTo("Pending"))
                .body("Conditions[0].Type", equalTo(CONDITION_TYPE_SIGNUP))
                .body("Conditions[0].ImmediateReward", equalTo(String.valueOf(CONDITION_REWARD)))
                .body("Conditions[0].CompletionCount", equalTo(CONDITION_COMPLETION_COUNT))
                .body("Name", equalTo(CAMPAIGN_NAME))
                .body("Reward", equalTo(CAMPAIGN_REWARD))
                .body("FromDate", equalTo(CAMPAIGN_FROM.split("Z")[0]))
                .body("ToDate", equalTo(CAMPAIGN_TO.split("Z")[0]))
                .body("Description", equalTo(CAMPAIGN_DESC))
                .body("RewardType", equalTo(REWARD_TYPE.getCode()))
                .body("CompletionCount", equalTo(CAMPAIGN_COMPLETION_COUNT));
    }

    @Test
    @UserStoryId(3871)
    void shouldUpdateCampaign() {
        final Integer UPD_CONDITION_REWARD = 10;
        val UPD_CONDITION_COMPLETION_COUNT = 8;

        val UPD_CAMPAIGN_NAME = generateRandomString();
        val UPD_CAMPAIGN_FROM = "2020-10-23T06:59:26.627Z";
        val UPD_CAMPAIGN_TO = "2027-05-23T06:59:26.627Z";
        val UPD_CAMPAIGN_DESC = generateRandomString();
        final Integer UPD_CAMPAIGN_REWARD = 600;
        val UPD_CAMPAIGN_COMPLETION_COUNT = 2;

        val campaignId = createDefaultCampaign();
        val conditionId = getCampaignConditionId(campaignId);

        getCampaignById(campaignId)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .body("IsEnabled", equalTo(true))
                .body("CampaignStatus", equalTo("Pending"));

        val newBonusType = ConditionCreateModel
                .conditionCreateBuilder()
                .completionCount(UPD_CONDITION_COMPLETION_COUNT)
                .immediateReward(UPD_CONDITION_REWARD.toString())
                .type(CONDITION_TYPE_SIGNUP)
                .build();

        val newCampaign = CampaignEditModel
                .campaignBuilder()
                .id(campaignId)
                .name(UPD_CAMPAIGN_NAME)
                .fromDate(UPD_CAMPAIGN_FROM)
                .toDate(UPD_CAMPAIGN_TO)
                .description(UPD_CAMPAIGN_DESC)
                .reward(UPD_CAMPAIGN_REWARD.toString())
                .completionCount(UPD_CAMPAIGN_COMPLETION_COUNT)
                .rewardType(REWARD_TYPE)
                .build();

        updateCampaignById(newCampaign, newBonusType, campaignId, conditionId, earnRule, getEarnRuleId(campaignId))
                .then()
                .assertThat()
                .statusCode(SC_OK);

        getCampaignById(campaignId)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .body("Id", equalTo(campaignId))
                .body("IsEnabled", equalTo(true))
                .body("CampaignStatus", equalTo("Pending"))
                .body("Conditions[0].Type", equalTo(CONDITION_TYPE_SIGNUP))
                .body("Conditions[0].ImmediateReward", equalTo((float) UPD_CONDITION_REWARD))
                .body("Conditions[0].CompletionCount", equalTo(UPD_CONDITION_COMPLETION_COUNT))
                .body("Name", equalTo(UPD_CAMPAIGN_NAME))
                .body("Reward", equalTo((float) UPD_CAMPAIGN_REWARD))
                .body("FromDate", equalTo(UPD_CAMPAIGN_FROM.split("Z")[0]))
                .body("ToDate", equalTo(UPD_CAMPAIGN_TO.split("Z")[0]))
                .body("Description", equalTo(UPD_CAMPAIGN_DESC))
                .body("RewardType", equalTo(REWARD_TYPE))
                .body("CompletionCount", equalTo(UPD_CAMPAIGN_COMPLETION_COUNT));
    }

    @ParameterizedTest(name =
            "Run {index}: conditionType={0}, conditionReward={1}, completionCount={2}, campaignName={3}," +
                    "campaignReward={4}, campaignFrom={5}, campaignTo={6}, campaignDesc={7}, rewardType={8}, campaignCompletionCount={9},"
                    +
                    " field={10}, errorMessage={11}, message={12}")
    @MethodSource("campaign_InvalidParameters")
    @UserStoryId(3871)
    void shouldNotUpdateCampaignWithEmptyOrInvalidData(String conditionType, Integer conditionReward,
            Integer completionCount,
            String campaignName, Float campaignReward, String campaignFrom,
            String campaignTo, String campaignDesc, RewardType rewardType, Integer campaignCompletionCount,
            String field, String errorMessage, String message) {
        val campaignId = createDefaultCampaign();
        val conditionId = getCampaignConditionId(campaignId);

        val newBonusType = ConditionCreateModel
                .conditionCreateBuilder()
                .completionCount(completionCount)
                .immediateReward(conditionReward.toString())
                .type(conditionType)
                .build();

        val newCampaign = CampaignEditModel
                .campaignBuilder()
                .name(campaignName)
                .fromDate(campaignFrom)
                .toDate(campaignTo)
                .description(campaignDesc)
                .reward(String.valueOf(campaignReward))
                .completionCount(campaignCompletionCount)
                .rewardType(rewardType)
                .build();

        updateCampaignById(newCampaign, newBonusType, campaignId, conditionId, earnRule, getEarnRuleId(campaignId))
                .then()
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .body(ERROR_MESSAGE, equalTo(errorMessage))
                .body(MODEL_ERRORS_FIELD + "." + field, equalTo(message));
    }

    @ParameterizedTest(name =
            "Run {index}: conditionType={0}, conditionReward={1}, completionCount={2}, campaignName={3}," +
                    "campaignReward={4}, campaignFrom={5}, campaignTo={6}, campaignDesc={7}, rewardType={8}, campaignCompletionCount={9}, message={10}")
    @MethodSource("campaign_InvalidValues")
    @UserStoryId(3871)
    void shouldNotUpdateCampaignWithInvalidValueTypes(String conditionType, Integer conditionReward,
            Integer completionCount,
            String campaignName, Float campaignReward, String campaignFrom,
            String campaignTo, String campaignDesc, RewardType rewardType, Integer campaignCompletionCount,
            String field, String message) {
        val campaignId = createDefaultCampaign();
        val conditionId = getCampaignConditionId(campaignId);

        val newBonusType = ConditionCreateModel
                .conditionCreateBuilder()
                .completionCount(completionCount)
                .immediateReward(conditionReward.toString())
                .type(conditionType)
                .build();

        val newCampaign = CampaignEditModel
                .campaignBuilder()
                .name(campaignName)
                .fromDate(campaignFrom)
                .toDate(campaignTo)
                .description(campaignDesc)
                .reward(String.valueOf(campaignReward))
                .completionCount(campaignCompletionCount)
                .rewardType(rewardType)
                .build();

        updateCampaignById(newCampaign, newBonusType, campaignId, conditionId, earnRule, getEarnRuleId(campaignId))
                .then()
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .body(ERROR_MESSAGE, containsString(message))
                .body(MODEL_ERRORS_FIELD + "." + field, containsString(message));
    }

    @Test
    void disablePendingCampaign_campaignStatusIsInactiveAndIsEnabledIsFalse() {
        val campaignId = getCampaignId(campaign, bonusType, earnRule);
        val conditionId = getCampaignConditionId(campaignId);

        // Disable campaigns by changing isEnabled to false
        val newCampaign = baseEditCampaign.build();

        updateCampaignById(newCampaign, bonusType, campaignId, conditionId, earnRule, getEarnRuleId(campaignId))
                .then()
                .assertThat()
                .statusCode(SC_OK);

        getCampaignById(campaignId)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .body("Id", equalTo(campaignId))
                .body("IsEnabled", equalTo(false))
                .body("CampaignStatus", equalTo("Inactive"))
                .body("Conditions[0].Type", equalTo(CONDITION_TYPE_SIGNUP))
                .body("Conditions[0].ImmediateReward", equalTo((float) CONDITION_REWARD))
                .body("Conditions[0].CompletionCount", equalTo(CONDITION_COMPLETION_COUNT))
                // .body("Name", equalTo(campaign.getName()))
                .body("Name", equalTo(newCampaign.getName()))
                .body("Reward", equalTo(CAMPAIGN_REWARD))
                .body("FromDate", equalTo(CAMPAIGN_FROM.split("Z")[0]))
                .body("ToDate", equalTo(CAMPAIGN_TO.split("Z")[0]))
                .body("Description", equalTo(CAMPAIGN_DESC))
                .body("RewardType", equalTo(REWARD_TYPE))
                .body("CompletionCount", equalTo(CAMPAIGN_COMPLETION_COUNT));
    }

    @Test
    void shouldUpdateCampaignNameWithNameContainingAllTypesOfCharacters() {
        val campaignId = getCampaignId(campaign, bonusType, earnRule);
        val conditionId = getCampaignConditionId(campaignId);

        val campaignName = "C@mP@1gn_N#m3%&"; //TODO: generate the name

        val newCampaign = baseEditCampaign.name(campaignName).build();

        updateCampaignById(newCampaign, bonusType, campaignId, conditionId, earnRule, getEarnRuleId(campaignId))
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .body(ERROR_CODE, equalTo("None"))
                .body(ERROR_MESSAGE, nullValue());
    }

    @ParameterizedTest
    @CsvSource({
            "C@mP@1gn_N#m3%&",
            "https://www.mavn.com"
    })
    void shouldUpdateCampaignDescriptionWithDescriptionContainingAllTypesOfCharacters(String campaignDescription) {
        val campaignId = getCampaignId(campaign, bonusType, earnRule);
        val conditionId = getCampaignConditionId(campaignId);

        val newCampaign = baseEditCampaign.description(campaignDescription).build();

        updateCampaignById(newCampaign, bonusType, campaignId, conditionId, earnRule, getEarnRuleId(campaignId))
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .body(ERROR_CODE, equalTo("None"))
                .body(ERROR_MESSAGE, nullValue());
    }

    @ParameterizedTest(name = "Run {index}: campaignName={0}, campaignFrom={1}, campaignTo={2}, campaignDesc={3}, campaignReward={4}")
    @MethodSource("campaign_ValidParameters")
    @UserStoryId(3871)
    void shouldUpdateNameDescriptionStartEndDateAndRewardOfActiveCampaign(String campaignName, String campaignFrom,
            String campaignTo, String campaignDesc, Float campaignReward) {
        val campaignId = getCampaignId(campaign, bonusType, earnRule);
        val conditionId = getCampaignConditionId(campaignId);

        val newCampaign = CampaignEditModel
                .campaignBuilder()
                .name(campaignName)
                .fromDate(campaignFrom)
                .toDate(campaignTo)
                .description(campaignDesc)
                .reward(String.valueOf(campaignReward))
                .rewardType(REWARD_TYPE)
                .completionCount(CAMPAIGN_COMPLETION_COUNT)
                .build();

        updateCampaignById(newCampaign, bonusType, campaignId, conditionId, earnRule, getEarnRuleId(campaignId))
                .then()
                .assertThat()
                .statusCode(SC_OK);

        getCampaignById(campaignId)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .body("Id", equalTo(campaignId))
                .body("IsEnabled", equalTo(true))
                .body("CreatedBy", equalTo(CAMPAIGN_CREATED_BY))
                .body("CampaignStatus", equalTo("Pending"))
                .body("Conditions[0].Type", equalTo(CONDITION_TYPE_SIGNUP))
                .body("Conditions[0].ImmediateReward", equalTo((float) CONDITION_REWARD))
                .body("Conditions[0].CompletionCount", equalTo(CONDITION_COMPLETION_COUNT))
                .body("Name", equalTo(campaignName))
                .body("Reward", equalTo(campaignReward))
                .body("FromDate", equalTo(campaignFrom.split("Z")[0]))
                .body("ToDate", equalTo(campaignTo.split("Z")[0]))
                .body("Description", equalTo(campaignDesc))
                .body("RewardType", equalTo(REWARD_TYPE))
                .body("CompletionCount", equalTo(CAMPAIGN_COMPLETION_COUNT));
    }

    @Test
    void shouldNotUpdateCampaignWithInvalidConditionType() {
        val invalidConditionType = generateRandomString();

        val bonusType = baseCondition.type(invalidConditionType).build();

        createCampaign(campaign, bonusType, earnRule)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .body(ERROR_CODE_FIELD, equalTo("EntityNotValid"))
                .body(ERROR_MESSAGE,
                        equalTo(String.format(CONDITION_TYPE_S_IS_NOT_VALID_MESSAGE, invalidConditionType)));
    }

    @Test
    void shouldNotUpdateCampaignWithEmptyConditionType() {
        String invalidConditionType = "";

        val bonusType = baseCondition.type(invalidConditionType).build();

        createCampaign(campaign, bonusType, earnRule)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .body(ERROR_CODE_FIELD, equalTo("EntityNotValid"))
                .body(ERROR_MESSAGE,
                        equalTo(String.format(CONDITION_TYPE_S_IS_NOT_VALID_MESSAGE, invalidConditionType)));
    }

    @Test
    void shouldNotUpdateCampaignWithConditionTypeNull() {
        String invalidConditionType = null;

        val bonusType = baseCondition.type(invalidConditionType).build();

        createCampaign(campaign, bonusType, earnRule)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .body(ERROR_CODE_FIELD, equalTo("EntityNotValid"))
                .body(ERROR_MESSAGE, equalTo(CONDITION_TYPE_IS_NOT_VALID_MESSAGE));
    }
}
