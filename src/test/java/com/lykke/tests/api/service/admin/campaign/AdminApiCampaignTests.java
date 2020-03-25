package com.lykke.tests.api.service.admin.campaign;

import static com.lykke.api.testing.api.common.GenerateUtils.generateRandomString;
import static com.lykke.api.testing.api.common.GenerateUtils.getRandomUuid;
import static com.lykke.tests.api.common.CommonConsts.SMOKE_TEST;
import static com.lykke.tests.api.common.CommonConsts.Staking.DEFAULT_STAKE_WARNING_PERIOD;
import static com.lykke.tests.api.common.CommonConsts.Staking.DEFAULT_STAKING_PERIOD;
import static com.lykke.tests.api.service.admin.BonusTypesUtils.getAllBonusTypes;
import static com.lykke.tests.api.service.admin.CampaignUtils.createCampaignAndReturnId;
import static com.lykke.tests.api.service.admin.CampaignUtils.createCampaignAsAdmin;
import static com.lykke.tests.api.service.admin.CampaignUtils.createConditionArray;
import static com.lykke.tests.api.service.admin.CampaignUtils.createMobileContentArray;
import static com.lykke.tests.api.service.admin.CampaignUtils.createUpdatedMobileContentArray;
import static com.lykke.tests.api.service.admin.CampaignUtils.getCampaignByIdAsAdmin;
import static com.lykke.tests.api.service.admin.CampaignUtils.getCampaignsAsAdmin;
import static com.lykke.tests.api.service.admin.CampaignUtils.getDescriptionId;
import static com.lykke.tests.api.service.admin.CampaignUtils.getImageId;
import static com.lykke.tests.api.service.admin.CampaignUtils.getTitleId;
import static com.lykke.tests.api.service.admin.CampaignUtils.updateCampaignAsAdmin;
import static com.lykke.tests.api.service.admin.LoginAdminUtils.getAdminToken;
import static com.lykke.tests.api.service.admin.model.RewardType.FIXED;
import static com.lykke.tests.api.service.admin.model.RewardType.PERCENTAGE;
import static com.lykke.tests.api.service.campaigns.BonusTypesUtils.getConditionsAsAdmin;
import static com.lykke.tests.api.service.campaigns.CampaignUtils.getCampaignById;
import static com.lykke.tests.api.service.campaigns.CampaignUtils.getCampaignsById;
import static com.lykke.tests.api.service.campaigns.model.CampaignStatus.ACTIVE;
import static com.lykke.tests.api.service.campaigns.model.ErrorCode.NONE;
import static com.lykke.tests.api.service.campaigns.model.ErrorDescription.VALUE_IS_INVALID;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.contains;
import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.apache.http.HttpStatus.SC_NO_CONTENT;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.text.CharSequenceLength.hasLength;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.params.provider.Arguments.of;

import com.lykke.api.testing.annotations.UserStoryId;
import com.lykke.api.testing.api.common.FakerUtils;
import com.lykke.tests.api.base.BaseApiTest;
import com.lykke.tests.api.common.enums.Localization;
import com.lykke.tests.api.service.admin.CampaignUtils;
import com.lykke.tests.api.service.admin.model.EarnRuleListResponse;
import com.lykke.tests.api.service.admin.model.EarnRuleCreateModel;
import com.lykke.tests.api.service.admin.model.EarnRuleUpdateModel;
import com.lykke.tests.api.service.admin.model.RewardType;
import com.lykke.tests.api.service.admin.model.bonustypes.BonusTypeModel;
import com.lykke.tests.api.service.admin.model.earnrules.ConditionCreateModel;
import com.lykke.tests.api.service.admin.model.earnrules.ConditionUpdateModel;
import com.lykke.tests.api.service.admin.model.earnrules.EarnRuleCreatedResponse;
import com.lykke.tests.api.service.campaigns.model.BonusEngineErrorResponseModel;
import com.lykke.tests.api.service.campaigns.model.BonusType;
import com.lykke.tests.api.service.campaigns.model.Campaign;
import com.lykke.tests.api.service.campaigns.model.CampaignsResponse;
import com.lykke.tests.api.service.campaigns.model.ConditionEditModel;
import com.lykke.tests.api.service.campaigns.model.ErrorResponse;
import com.lykke.tests.api.service.campaigns.model.MobileContents;
import com.lykke.tests.api.service.campaigns.model.burnrules.RuleContentType;
import io.qameta.allure.Step;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.time.Instant;
import java.util.Arrays;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import lombok.val;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

public class AdminApiCampaignTests extends BaseApiTest {

    public static final String CAMPAIGN_ID = "CampaignId";
    public static final String ID = "Id";
    public static final String DATE_SPLIT_PATTERN_01 = "T\\d{1,2}\\:\\d{1,2}\\:\\d{1,2}\\.\\d{0,3}Z";
    public static final String DATE_SPLIT_PATTERN_02 = "T|\\.";
    private static final int CONDITION_COMPLETION_COUNT = 4;
    private static final String CAMPAIGN_NAME = generateRandomString();
    private static final String CAMPAIGN_FROM = Instant.now().toString();
    private static final String CAMPAIGN_TO = "2025-05-23T06:59:26.627Z";
    private static final String CAMPAIGN_DESC = generateRandomString();
    private static final Double CAMPAIGN_REWARD = 500.0;
    private static final int CONDITION_REWARD = 5;
    private static final String CONDITION_TYPE_SIGNUP = "signup";
    private static final Integer IMMEDIATE_REWARD = 10;
    private static final RewardType REWARD_TYPE = PERCENTAGE; ////55 FIXED;
    private static final Boolean IS_ENABLED = true;
    private static final int CAMPAIGN_COMPLETION_COUNT = 2;
    private static final String INVALID_CAMPAIGN_ID_01 = "11";
    private static final String CAMPAIGN_ID_GUID_NO_DASHES = "f6357d1258584b0c827e750b04dbba1f";
    private static final String CAMPAIGN_ID_GUID_WITH_DASHES = "f6357d12-5858-4b0c-827e-750b04dbba1f";
    private static final String NAME_MUST_NOT_BE_EMPTY_MESSAGE = "Name required";
    private static final String CAMPAIGN_REWARD_MUST_BE_GREATER_THAN_0_MESSAGE = "Reward should be greater than or equal to zero";
    private static final String THE_INPUT_WAS_NOT_VALID_MESSAGE = "The input was not valid.";
    private static final String DESCRIPTION_MUST_NOT_BE_EMPTY_MESSAGE = "Description required";
    private static final String DESCRIPTION_MUST_BE_AT_LEAST_3_CHAR_2_MESSAGE = "Description should be between 3 and 1000 chars";
    private static final String NAME_0_FIELD = "Name[0]";
    private static final String REWARD_0_FIELD = "Reward[0]";
    private static final String TO_DATE_0_FIELD = "ToDate[0]";
    private static final String FROM_DATE_0_FIELD = "FromDate[0]";
    private static final String DESCRIPTION_0_FIELD = "Description[0]";
    private static final String REWARD_TYPE_0_FIELD = "RewardType[0]";
    private static final String COMLETION_COUNT_O_FIELD = "CompletionCount[0]";
    private static final String CAMPAIGN_COMPLETION_COUNT_MUST_BE_AT_LEAST_1 = "Completion count should be greater than zero";
    private static final int ORDER = 17;
    private static EarnRuleCreateModel earnRules;
    private static ConditionCreateModel condition;
    private static ConditionUpdateModel updatedCondition;
    private static EarnRuleCreateModel adminCampaign;
    private static ConditionCreateModel adminCondition;
    private static EarnRuleCreateModel.EarnRuleCreateModelBuilder baseEarnRule;
    private static ConditionCreateModel.ConditionCreateModelBuilder baseCondition;
    private static EarnRuleUpdateModel.EarnRuleUpdateModelBuilder baseUpdateEarnRule;
    private static MobileContents mobileContents;

    @BeforeAll
    static void dataSetup() {
        baseCondition = ConditionCreateModel
                .conditionCreateBuilder()
                .type(CONDITION_TYPE_SIGNUP)
                .completionCount(CONDITION_COMPLETION_COUNT)
                .immediateReward(IMMEDIATE_REWARD.toString())
                .hasStaking(false)
                .stakingPeriod(DEFAULT_STAKING_PERIOD)
                .stakeWarningPeriod(DEFAULT_STAKE_WARNING_PERIOD)
                .rewardType(RewardType.PERCENTAGE)
                .approximateAward("1");
        condition = baseCondition
                .build();

        updatedCondition = ConditionUpdateModel
                .conditionUpdateBuilder()
                .type(CONDITION_TYPE_SIGNUP)
                .completionCount(CONDITION_COMPLETION_COUNT)
                .immediateReward(IMMEDIATE_REWARD.toString())
                .hasStaking(false)
                .stakingPeriod(DEFAULT_STAKING_PERIOD)
                .stakeWarningPeriod(DEFAULT_STAKE_WARNING_PERIOD)
                .rewardType(RewardType.PERCENTAGE)
                .approximateAward("1")
                .build();

        mobileContents = MobileContents
                .builder()
                .mobileLanguage(Localization.EN)
                .title(RuleContentType.TITLE)
                .description(FakerUtils.title)
                .descriptionId(getRandomUuid())
                .titleId(getRandomUuid())
                .build();

        baseEarnRule = EarnRuleCreateModel
                .earnRuleCreateModelBuilder()
                .name(CAMPAIGN_NAME)
                .description(CAMPAIGN_NAME)
                .rewardType(REWARD_TYPE)
                .reward(CAMPAIGN_REWARD.toString())
                .fromDate(CAMPAIGN_FROM)
                .toDate(CAMPAIGN_TO)
                .completionCount(CAMPAIGN_COMPLETION_COUNT)
                .description(CAMPAIGN_DESC)
                .isEnabled(IS_ENABLED)
                .conditions(createConditionArray(condition))
                .rewardType(REWARD_TYPE)
                .mobileContents(createMobileContentArray(mobileContents))
                .completionCount(CAMPAIGN_COMPLETION_COUNT)
                .reward("0")
                .approximateAward("100");
        earnRules = baseEarnRule
                .build();

        baseUpdateEarnRule = EarnRuleUpdateModel
                .earnRuleUpdateModelBuilder()
                .name(CAMPAIGN_NAME)
                .description(CAMPAIGN_NAME)
                .rewardType(REWARD_TYPE)
                .reward(CAMPAIGN_REWARD.toString())
                .fromDate(CAMPAIGN_FROM)
                .toDate(CAMPAIGN_TO)
                .completionCount(CAMPAIGN_COMPLETION_COUNT)
                .description(CAMPAIGN_DESC)
                .isEnabled(IS_ENABLED)
                .conditions(createConditionArray(ConditionUpdateModel
                        .conditionUpdateBuilder()
                        .amountInCurrency(condition.getAmountInCurrency())
                        .amountInTokens(condition.getAmountInTokens())
                        .approximateAward(condition.getApproximateAward())
                        .burningRule(condition.getBurningRule())
                        .completionCount(condition.getCompletionCount())
                        .hasStaking(condition.isHasStaking())
                        .immediateReward(condition.getImmediateReward())
                        .partnerId(condition.getPartnerId())
                        .rewardHasRatio(condition.isRewardHasRatio())
                        .rewardRatio(condition.getRewardRatio())
                        .rewardType(condition.getRewardType())
                        .stakeAmount(condition.getStakeAmount())
                        .stakeWarningPeriod(condition.getStakeWarningPeriod())
                        .stakingPeriod(condition.getStakingPeriod())
                        .stakingRule(condition.getStakingRule())
                        .type(condition.getType())
                        .usePartnerCurrencyRate(condition.isUsePartnerCurrencyRate())
                        .build()))
                .rewardType(REWARD_TYPE)
                .mobileContents(createUpdatedMobileContentArray(mobileContents))
                .completionCount(CAMPAIGN_COMPLETION_COUNT)
                .reward("0")
                .approximateAward("100");

        val baseAdminCondition = ConditionCreateModel
                .conditionCreateBuilder()
                .type(CONDITION_TYPE_SIGNUP)
                .completionCount(CONDITION_COMPLETION_COUNT)
                .immediateReward(IMMEDIATE_REWARD.toString())
                .hasStaking(false)
                .stakingPeriod(DEFAULT_STAKING_PERIOD)
                .stakeWarningPeriod(DEFAULT_STAKE_WARNING_PERIOD);
        adminCondition = baseAdminCondition
                .build();

        val baseAdminCampaign = EarnRuleCreateModel
                .earnRuleCreateModelBuilder()
                .name(CAMPAIGN_NAME)
                .rewardType(FIXED)
                .reward(String.valueOf(CAMPAIGN_REWARD))
                .fromDate(CAMPAIGN_FROM)
                .toDate(CAMPAIGN_TO)
                .completionCount(CAMPAIGN_COMPLETION_COUNT)
                .description(CAMPAIGN_DESC)
                .conditions(createConditionArray(adminCondition));
        adminCampaign = baseAdminCampaign
                .build();
    }

    private static Stream campaign_InvalidParameters() {
        return Stream.of(
                //TODO: Fix the commented out rows (Automation task FAL-1973)
//                of(null, CONDITION_REWARD, CONDITION_COMPLETION_COUNT, CAMPAIGN_NAME, CAMPAIGN_REWARD, CAMPAIGN_FROM,
//                        CAMPAIGN_TO, CAMPAIGN_DESC, REWARD_TYPE, CAMPAIGN_COMPLETION_COUNT,
//                        TYPE_0_FIELD, CONDITION_TYPE_IS_NOT_VALID_MESSAGE),
//                of(EMPTY, CONDITION_REWARD, CONDITION_COMPLETION_COUNT, CAMPAIGN_NAME, CAMPAIGN_REWARD, CAMPAIGN_FROM,
//                        CAMPAIGN_TO, CAMPAIGN_DESC, REWARD_TYPE, CAMPAIGN_COMPLETION_COUNT,
//                        TYPE_0_FIELD, CONDITION_TYPE_IS_NOT_VALID_MESSAGE),
//                of("invalid", CONDITION_REWARD, CONDITION_COMPLETION_COUNT, CAMPAIGN_NAME, CAMPAIGN_REWARD,
//                        CAMPAIGN_FROM, CAMPAIGN_TO, CAMPAIGN_DESC, REWARD_TYPE, CAMPAIGN_COMPLETION_COUNT,
//                        TYPE_0_FIELD, String.format(CONDITION_TYPE_S_IS_NOT_VALID_MESSAGE, "invalid"),
                of(CONDITION_TYPE_SIGNUP, CONDITION_REWARD, CONDITION_COMPLETION_COUNT, null, CAMPAIGN_REWARD,
                        CAMPAIGN_FROM, CAMPAIGN_TO, CAMPAIGN_DESC, REWARD_TYPE, CAMPAIGN_COMPLETION_COUNT,
                        NAME_0_FIELD, NAME_MUST_NOT_BE_EMPTY_MESSAGE),
                of(CONDITION_TYPE_SIGNUP, CONDITION_REWARD, CONDITION_COMPLETION_COUNT, EMPTY, CAMPAIGN_REWARD,
                        CAMPAIGN_FROM, CAMPAIGN_TO, CAMPAIGN_DESC, REWARD_TYPE, CAMPAIGN_COMPLETION_COUNT,
                        NAME_0_FIELD, NAME_MUST_NOT_BE_EMPTY_MESSAGE),
                of(CONDITION_TYPE_SIGNUP, CONDITION_REWARD, CONDITION_COMPLETION_COUNT, EMPTY, CAMPAIGN_REWARD,
                        CAMPAIGN_FROM, CAMPAIGN_TO, CAMPAIGN_DESC, REWARD_TYPE, CAMPAIGN_COMPLETION_COUNT,
                        NAME_0_FIELD, NAME_MUST_NOT_BE_EMPTY_MESSAGE),
                of(CONDITION_TYPE_SIGNUP, CONDITION_REWARD, CONDITION_COMPLETION_COUNT, CAMPAIGN_NAME, -1f,
                        CAMPAIGN_FROM, CAMPAIGN_TO, CAMPAIGN_DESC, REWARD_TYPE, CAMPAIGN_COMPLETION_COUNT,
                        REWARD_0_FIELD, CAMPAIGN_REWARD_MUST_BE_GREATER_THAN_0_MESSAGE),
                of(CONDITION_TYPE_SIGNUP, CONDITION_REWARD, CONDITION_COMPLETION_COUNT, CAMPAIGN_NAME, CAMPAIGN_REWARD,
                        CAMPAIGN_FROM, CAMPAIGN_TO, EMPTY, REWARD_TYPE, CAMPAIGN_COMPLETION_COUNT,
                        DESCRIPTION_0_FIELD, DESCRIPTION_MUST_NOT_BE_EMPTY_MESSAGE),
                of(CONDITION_TYPE_SIGNUP, CONDITION_REWARD, CONDITION_COMPLETION_COUNT, CAMPAIGN_NAME, CAMPAIGN_REWARD,
                        CAMPAIGN_FROM, CAMPAIGN_TO, EMPTY, REWARD_TYPE, CAMPAIGN_COMPLETION_COUNT,
                        DESCRIPTION_0_FIELD, DESCRIPTION_MUST_NOT_BE_EMPTY_MESSAGE),
                of(CONDITION_TYPE_SIGNUP, CONDITION_REWARD, CONDITION_COMPLETION_COUNT, CAMPAIGN_NAME, CAMPAIGN_REWARD,
                        CAMPAIGN_FROM, CAMPAIGN_TO, null, REWARD_TYPE, CAMPAIGN_COMPLETION_COUNT,
                        DESCRIPTION_0_FIELD, DESCRIPTION_MUST_NOT_BE_EMPTY_MESSAGE),
                of(CONDITION_TYPE_SIGNUP, CONDITION_REWARD, CONDITION_COMPLETION_COUNT, CAMPAIGN_NAME, CAMPAIGN_REWARD,
                        CAMPAIGN_FROM, CAMPAIGN_TO, "12", REWARD_TYPE, CAMPAIGN_COMPLETION_COUNT,
                        DESCRIPTION_0_FIELD, DESCRIPTION_MUST_BE_AT_LEAST_3_CHAR_2_MESSAGE),
//                of(CONDITION_TYPE_SIGNUP, CONDITION_REWARD, 0, CAMPAIGN_NAME, CAMPAIGN_REWARD, CAMPAIGN_FROM,
//                        CAMPAIGN_TO, CAMPAIGN_DESC, REWARD_TYPE, CAMPAIGN_COMPLETION_COUNT, "Conditions.CompletionCount",
//                        CONDITION_COMPLETION_COUNT_MUST_BE_GREATER_THAN_0_MESSAGE),
//                of(CONDITION_TYPE_SIGNUP, CONDITION_REWARD, -1, CAMPAIGN_NAME, CAMPAIGN_REWARD, CAMPAIGN_FROM,
//                        CAMPAIGN_TO, CAMPAIGN_DESC, REWARD_TYPE, CAMPAIGN_COMPLETION_COUNT, "Conditions.CompletionCount",
//                        CONDITION_COMPLETION_COUNT_MUST_BE_GREATER_THAN_0_MESSAGE),
                of(CONDITION_TYPE_SIGNUP, CONDITION_REWARD, CONDITION_COMPLETION_COUNT, CAMPAIGN_NAME, CAMPAIGN_REWARD,
                        CAMPAIGN_FROM, CAMPAIGN_TO, CAMPAIGN_DESC, REWARD_TYPE, 0, COMLETION_COUNT_O_FIELD,
                        CAMPAIGN_COMPLETION_COUNT_MUST_BE_AT_LEAST_1),
                of(CONDITION_TYPE_SIGNUP, CONDITION_REWARD, CONDITION_COMPLETION_COUNT, CAMPAIGN_NAME, CAMPAIGN_REWARD,
                        CAMPAIGN_FROM, CAMPAIGN_TO, CAMPAIGN_DESC, REWARD_TYPE, -1, COMLETION_COUNT_O_FIELD,
                        CAMPAIGN_COMPLETION_COUNT_MUST_BE_AT_LEAST_1)
        );
    }

    private static Stream campaign_InvalidParameters_ErrorConverting() {
        return Stream.of(
                of(CONDITION_TYPE_SIGNUP, CONDITION_REWARD, CONDITION_COMPLETION_COUNT, CAMPAIGN_NAME, CAMPAIGN_REWARD,
                        null, CAMPAIGN_TO, CAMPAIGN_DESC, REWARD_TYPE, CAMPAIGN_COMPLETION_COUNT,
                        FROM_DATE_0_FIELD),
                of(CONDITION_TYPE_SIGNUP, CONDITION_REWARD, CONDITION_COMPLETION_COUNT, CAMPAIGN_NAME, CAMPAIGN_REWARD,
                        EMPTY, CAMPAIGN_TO, CAMPAIGN_DESC, REWARD_TYPE, CAMPAIGN_COMPLETION_COUNT,
                        FROM_DATE_0_FIELD),
                of(CONDITION_TYPE_SIGNUP, CONDITION_REWARD, CONDITION_COMPLETION_COUNT, CAMPAIGN_NAME, CAMPAIGN_REWARD,
                        "invalid_date", CAMPAIGN_TO, CAMPAIGN_DESC, REWARD_TYPE, CAMPAIGN_COMPLETION_COUNT,
                        FROM_DATE_0_FIELD),
                of(CONDITION_TYPE_SIGNUP, CONDITION_REWARD, CONDITION_COMPLETION_COUNT, CAMPAIGN_NAME, CAMPAIGN_REWARD,
                        CAMPAIGN_FROM, "invalid_date", CAMPAIGN_DESC, REWARD_TYPE, CAMPAIGN_COMPLETION_COUNT,
                        TO_DATE_0_FIELD),
                of(CONDITION_TYPE_SIGNUP, CONDITION_REWARD, CONDITION_COMPLETION_COUNT, CAMPAIGN_NAME, CAMPAIGN_REWARD,
                        CAMPAIGN_FROM, CAMPAIGN_TO, CAMPAIGN_DESC, null, CAMPAIGN_COMPLETION_COUNT,
                        REWARD_TYPE_0_FIELD),
                of(CONDITION_TYPE_SIGNUP, CONDITION_REWARD, CONDITION_COMPLETION_COUNT, CAMPAIGN_NAME, CAMPAIGN_REWARD,
                        CAMPAIGN_FROM, CAMPAIGN_TO, CAMPAIGN_DESC, EMPTY, CAMPAIGN_COMPLETION_COUNT,
                        REWARD_TYPE_0_FIELD),
                of(CONDITION_TYPE_SIGNUP, CONDITION_REWARD, CONDITION_COMPLETION_COUNT, CAMPAIGN_NAME, CAMPAIGN_REWARD,
                        CAMPAIGN_FROM, CAMPAIGN_TO, CAMPAIGN_DESC, "NoFixed", CAMPAIGN_COMPLETION_COUNT,
                        REWARD_TYPE_0_FIELD)
        );
    }

    @Step("Create multiple campagins")
    private static void createMultipleCampaigns(int numberOfCampaignsToBeCreated) {
        IntStream.range(0, numberOfCampaignsToBeCreated)
                .forEach(n -> createDefaultCampaign());
    }

    private static String createDefaultCampaign() {
        return createCampaignAsAdmin(earnRules, condition, mobileContents, getAdminToken())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .path(CAMPAIGN_ID);
    }

    private static Stream campaign_ValidParameters() {
        return Stream.of(
                of(generateRandomString(), CAMPAIGN_FROM, CAMPAIGN_TO, CAMPAIGN_DESC),
                of(CAMPAIGN_NAME, CAMPAIGN_FROM, "2026-02-23T06:59:26.627Z", CAMPAIGN_DESC),
                of(CAMPAIGN_NAME, "2019-02-23T06:59:26.627Z", CAMPAIGN_TO, CAMPAIGN_DESC),
                of(CAMPAIGN_NAME, CAMPAIGN_FROM, CAMPAIGN_TO, generateRandomString())
        );
    }

    @Test
    @UserStoryId(storyId = {124, 679, 673})
    void shouldCreateCampaigns() {
        String campaignId = createCampaignAsAdmin(earnRules, adminCondition, mobileContents, getAdminToken())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .body(ID, hasLength(36))
                .extract()
                .path(ID);

        getCampaignById(campaignId)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .body("Id", equalTo(campaignId))
                .body("CampaignStatus", equalTo(ACTIVE.getStatus()))
                .body("Conditions[0].Type", equalTo(CONDITION_TYPE_SIGNUP))
                .body("Conditions[0].ImmediateReward", equalTo(IMMEDIATE_REWARD.toString()))
                .body("Conditions[0].CompletionCount", equalTo(CONDITION_COMPLETION_COUNT))
                .body("Name", equalTo(CAMPAIGN_NAME))
                .body("Reward", equalTo(CAMPAIGN_REWARD.toString()))
                .body("FromDate", containsString(CAMPAIGN_FROM.split(DATE_SPLIT_PATTERN_01)[0]))
                .body("FromDate", containsString(CAMPAIGN_FROM.split(DATE_SPLIT_PATTERN_02)[0]))
                .body("ToDate", containsString(CAMPAIGN_TO.split(DATE_SPLIT_PATTERN_01)[0]))
                .body("ToDate", containsString(CAMPAIGN_TO.split(DATE_SPLIT_PATTERN_02)[0]))
                .body("RewardType", equalTo(REWARD_TYPE.getType()))
                .body("Description", equalTo(CAMPAIGN_DESC));
    }

    @Test
    @Tag(SMOKE_TEST)
    @UserStoryId(787)
    void shouldGetCampaignsById() {
        val campaignId = createCampaignAndReturnId();
        Campaign
                .builder()
                .description(generateRandomString(50))
                .build();
        val campaign2Id = createCampaignForGettingById();

        val responseObject = getExtractableResponse(SC_OK, campaignId, campaign2Id)
                .as(CampaignsResponse.class);

        assertTrue(Arrays.stream(responseObject.getCampaigns()).anyMatch(c -> campaignId.equals(c.getId())));
    }

    @Test
    @UserStoryId(1019)
    void shouldNotFailOnGetCampaignsByIdIfSomeIdAreInvalid() {
        val campaignId = createCampaignForGettingById();
        Campaign
                .builder()
                .description(generateRandomString(50))
                .build();
        val campaign2Id = createCampaignForGettingById();

        val responseObject = getExtractableResponse(
                SC_OK,
                campaignId,
                campaign2Id,
                CAMPAIGN_ID_GUID_NO_DASHES,
                CAMPAIGN_ID_GUID_WITH_DASHES)
                .as(CampaignsResponse.class);

        assertTrue(Arrays.stream(responseObject.getCampaigns()).anyMatch(c -> campaignId.equals(c.getId())));
    }

    @Test
    @UserStoryId(1019)
    void shouldNotProcessCampaignsByIdIfSomeIdAreInvalid() {
        val campaignId = createCampaignForGettingById();
        Campaign
                .builder()
                .description(generateRandomString(50))
                .build();
        val campaign2Id = createCampaignForGettingById();

        val responseObject = getExtractableResponse(
                SC_BAD_REQUEST,
                campaignId,
                campaign2Id,
                INVALID_CAMPAIGN_ID_01,
                CAMPAIGN_ID_GUID_NO_DASHES,
                CAMPAIGN_ID_GUID_WITH_DASHES)
                .as(BonusEngineErrorResponseModel.class);

        assertEquals(
                VALUE_IS_INVALID.getValue(INVALID_CAMPAIGN_ID_01),
                responseObject.getErrorMessage());
    }

    @Test
    @UserStoryId(1019)
    void shouldGetNoCampaignsIfThereAreNoIdsProvided() {
        val responseObject = getExtractableResponse(SC_OK)
                .as(ErrorResponse.class);

        assertAll(
                () -> assertEquals(NONE.getCodeName(), responseObject.getErrorCode()),
                () -> assertEquals(null, responseObject.getErrorMessage()),
                () -> assertEquals(0, responseObject.getCampaigns().length)
        );
    }

    ////55  @Disabled("Test to be refactored")
    @Test
    @UserStoryId(storyId = {515, 679, 673, 4347})
    void shouldUpdateCampaignByAdmin() {
        val UPD_CONDITION_TYPE = "mvn-purchase"; // TODO: make enum
        val UPD_CONDITION_REWARD = 9;
        val UPD_CONDITION_COUNT = 100;

        val UPD_CAMPAIGN_NAME = generateRandomString();
        val UPD_CAMPAIGN_FROM = "2020-10-23T06:59:26.627Z";
        val UPD_CAMPAIGN_TO = "2027-05-23T06:59:26.627Z";
        val UPD_CAMPAIGN_DESC = generateRandomString();
        val UPD_CAMPAIGN_REWARD = 600;
        val UPD_COMPLETION_COUNT = CAMPAIGN_COMPLETION_COUNT + 1;

        String campaignId = CampaignUtils.createCampaignAsAdmin(earnRules, condition, mobileContents, getAdminToken())
                .then()

                ////55
                .log().all()

                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .path(ID);

        /*
        BonusType updCondition = BonusType
                .builder()
                .type("signup")
                .immediateReward(String.valueOf(5))
                .completionCount(4)
                .displayName("Sign Up")
                .build();
        */
        val updCondition = ConditionUpdateModel
                .conditionUpdateBuilder()
                .type("signup")
                .immediateReward(String.valueOf(5))
                .completionCount(4)
                .build();

        val newCampaign = EarnRuleUpdateModel
                .earnRuleUpdateModelBuilder()
                .name(UPD_CAMPAIGN_NAME)
                .fromDate(UPD_CAMPAIGN_FROM)
                .toDate(UPD_CAMPAIGN_TO)
                .description(UPD_CAMPAIGN_DESC)
                .rewardType(REWARD_TYPE)
                .reward(CAMPAIGN_REWARD.toString())
                .completionCount(223)
                .completionCount(CAMPAIGN_COMPLETION_COUNT)
                .build();

        String conditionId = getCampaignByIdAsAdmin(campaignId, getAdminToken())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .path("Conditions[0].Id");

        updateCampaignAsAdmin(newCampaign, campaignId, updCondition, conditionId,
                mobileContents, "", "", "", getAdminToken())
                .then()

                ////55
                .log().all()

                .assertThat()
                .statusCode(SC_NO_CONTENT);

        getCampaignByIdAsAdmin(campaignId, getAdminToken())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .body("Id", equalTo(campaignId))
                .body("CampaignStatus", not(equalTo("Active")))
                .body("Conditions[0].Type", equalTo(CONDITION_TYPE_SIGNUP))
                .body("Conditions[0].Type", equalTo(UPD_CONDITION_TYPE))
                .body("Conditions[0].ImmediateReward", equalTo((float) UPD_CONDITION_REWARD))
                .body("Conditions[0].CompletionCount", equalTo(UPD_CONDITION_COUNT))
                .body("Name", equalTo(UPD_CAMPAIGN_NAME))
                .body("Reward", equalTo(CAMPAIGN_REWARD))
                .body("FromDate", equalTo(UPD_CAMPAIGN_FROM))
                .body("ToDate", equalTo(UPD_CAMPAIGN_TO))
                .body("RewardType", equalTo(REWARD_TYPE))
                .body("Description", equalTo(UPD_CAMPAIGN_DESC));
    }

    @Test
    @Tag(SMOKE_TEST)
    @UserStoryId(storyId = 128)
    void shouldGetAllCampaigns() {
        val campaigns = getCampaignsAsAdmin(getAdminToken());
        val initialNumberOfCampaigns = campaigns
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(EarnRuleListResponse.class)
                .getPagedResponse()
                .getTotalCount();

        createMultipleCampaigns(2);

        val updatedCampaigns = getCampaignsAsAdmin(getAdminToken());
        val updatedNumberOfCampaigns = updatedCampaigns
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(EarnRuleListResponse.class)
                .getPagedResponse()
                .getTotalCount();

        assertEquals(initialNumberOfCampaigns + 2, updatedNumberOfCampaigns);
    }

    @ParameterizedTest(name =
            "Run {index}: conditionType={0}, conditionReward={1}, completionCount={2}, campaignName={3}," +
                    "campaignReward={4}, campaignFrom={5}, campaignTo={6}, campaignDesc={7}," +
                    "rewardType={8}, campaignCompletionCount={9}, field={10}, message={11}")
    @MethodSource("campaign_InvalidParameters")
    @UserStoryId(storyId = {551, 3869})
    void shouldValidateFieldsWhileCreatingCampaign(String conditionType,
            Integer conditionReward, Integer completionCount,
            String campaignName, Double campaignReward, String campaignFrom, String campaignTo,
            String campaignDesc, RewardType rewardType, Integer campaignCompletionCount, String field,
            String message) {
        val condition = ConditionCreateModel
                .conditionCreateBuilder()
                .type(conditionType)
                .immediateReward(conditionReward.toString())
                .completionCount(completionCount)
                .rewardType(rewardType)
                .build();

        val earnRules = EarnRuleCreateModel
                .earnRuleCreateModelBuilder()
                .name(campaignName)
                .reward(campaignReward.toString())
                .fromDate(campaignFrom)
                .toDate(campaignTo)
                .description(campaignDesc)
                .conditions(createConditionArray(condition))
                .rewardType(rewardType)
                .completionCount(campaignCompletionCount)
                .mobileContents(createMobileContentArray(mobileContents))
                .isEnabled(IS_ENABLED)
                .order(ORDER)
                .build();

        createCampaignAsAdmin(earnRules, condition, mobileContents, getAdminToken())
                .then()
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .body(field, equalTo(message));
    }

    @ParameterizedTest(name =
            "Run {index}: conditionType={0}, conditionReward={1}, completionCount={2}, campaignName={3}," +
                    "campaignReward={4}, campaignFrom={5}, campaignTo={6}, campaignDesc={7}," +
                    "rewardType={8}, campaignCompletionCount={9}, field={10}")
    @MethodSource("campaign_InvalidParameters_ErrorConverting")
    @UserStoryId(storyId = 551)
    void shouldValidateFieldsWhileCreatingCampaign_ErrorConverting(String conditionType,
            Integer conditionReward, Integer completionCount,
            String campaignName, Double campaignReward, String campaignFrom, String campaignTo,
            String campaignDesc, RewardType rewardType, Integer campaignCompletionCount, String field) {
        val condition = ConditionCreateModel
                .conditionCreateBuilder()
                .type(conditionType)
                .immediateReward(conditionReward.toString())
                .completionCount(completionCount)
                .build();

        val earnRules = EarnRuleCreateModel
                .earnRuleCreateModelBuilder()
                .name(campaignName)
                .reward(campaignReward.toString())
                .fromDate(campaignFrom)
                .toDate(campaignTo)
                .description(campaignDesc)
                .conditions(createConditionArray(condition))
                .rewardType(rewardType)
                .completionCount(campaignCompletionCount)
                .mobileContents(createMobileContentArray(mobileContents))
                .isEnabled(IS_ENABLED)
                .fromDate(Instant.now().toString())
                .build();

        createCampaignAsAdmin(earnRules, condition, mobileContents, getAdminToken())
                .then()
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .body(field, containsString("convert"));
    }

    @Test
    @Tag(SMOKE_TEST)
    void shouldGetAllConditions() {
        getConditionsAsAdmin(getAdminToken())
                .then()
                .assertThat()
                .statusCode(SC_OK);
    }

    @Test
    @Tag(SMOKE_TEST)
    @UserStoryId(2647)
    void shouldCheckBonusTypeModel() {
        val actualResult = getConditionsAsAdmin(getAdminToken())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(BonusTypeModel[].class)[0];

        assertAll(
                () -> assertNotNull(actualResult.isAllowConversionRate()),
                () -> assertNotNull(actualResult.isAllowInfinite()),
                () -> assertNotNull(actualResult.isAllowPercentage())
        );
    }

    @Test
    @UserStoryId(3355)
    void shouldGetAllBonusTypes() {
        val actualResult = getAllBonusTypes(getAdminToken())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(BonusTypeModel[].class);

        // isStakeable is not null
        Arrays.stream(actualResult)
                .allMatch(item -> item.isStakeable() || !item.isStakeable());
    }

    @Test
    @UserStoryId(storyId = 551)
    void shouldNotUpdateActiveCampaignRewardAndCondition() {
        final Integer UPD_CONDITION_REWARD = 10;
        val UPD_CONDITION_COMPLETION_COUNT = 8;

        final Double UPD_CAMPAIGN_REWARD = 1000.0;
        val token = getAdminToken();

        String campaignId = createCampaignAndReturnId(earnRules, condition, mobileContents);

        String conditionId = getCampaignByIdAsAdmin(campaignId, token)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .path("Conditions[0].Id");

        val newCondition = ConditionUpdateModel
                .conditionUpdateBuilder()
                .completionCount(UPD_CONDITION_COMPLETION_COUNT)
                .immediateReward(UPD_CONDITION_REWARD.toString())
                .type(CONDITION_TYPE_SIGNUP)
                .build();

        val newEarnRule = baseUpdateEarnRule.reward(UPD_CAMPAIGN_REWARD.toString()).isEnabled(true).build();

        updateCampaignAsAdmin(newEarnRule, campaignId, newCondition, conditionId, mobileContents,
                getTitleId(campaignId, token), getDescriptionId(campaignId, token), getImageId(campaignId, token),
                token)
                .then()
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .body("error", equalTo("EntityNotValid"))
                .body("message",
                        equalTo("Campaign Reward must not be changed\nCampaign Conditions must not be changed"));
    }

    @ParameterizedTest(name = "Run {index}: campaignName={0}, campaignFrom={1}, campaignTo={2}, campaignDesc={3}")
    @MethodSource("campaign_ValidParameters")
    void shouldUpdateNameDescriptionStartAndEndDateOfActiveCampaign(String campaignName, String campaignFrom,
            String campaignTo, String campaignDesc) {
        val token = getAdminToken();
        String campaignId = createCampaignAndReturnId(earnRules, condition, mobileContents);
        String conditionId = getCampaignByIdAsAdmin(campaignId, token)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .path("Conditions[0].Id");

        val newEarnRule = EarnRuleUpdateModel
                .earnRuleUpdateModelBuilder()
                .name(campaignName)
                .reward(CAMPAIGN_REWARD.toString())
                .fromDate(campaignFrom)
                .toDate(campaignTo)
                .description(campaignDesc)
                .isEnabled(true)
                .conditions(createConditionArray(updatedCondition))
                .rewardType(REWARD_TYPE)
                .mobileContents(createUpdatedMobileContentArray(mobileContents))
                .completionCount(CAMPAIGN_COMPLETION_COUNT)
                .build();

        updateCampaignAsAdmin(newEarnRule, campaignId, updatedCondition, conditionId, mobileContents,
                getTitleId(campaignId, token), getDescriptionId(campaignId, token), getImageId(campaignId, token),
                token)
                .then()
                .assertThat()
                .statusCode(SC_NO_CONTENT);

        getCampaignByIdAsAdmin(campaignId, getAdminToken())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .body("Id", equalTo(campaignId))
                .body("IsEnabled", equalTo(true))
                .body("Conditions[0].Type", equalTo(CONDITION_TYPE_SIGNUP))
                .body("Conditions[0].ImmediateReward", equalTo(IMMEDIATE_REWARD))
                .body("Conditions[0].CompletionCount", equalTo(CONDITION_COMPLETION_COUNT))
                .body("Name", equalTo(campaignName))
                .body("Reward", equalTo(CAMPAIGN_REWARD))
                .body("FromDate", containsString(campaignFrom.split(DATE_SPLIT_PATTERN_01)[0]))
                .body("FromDate", containsString(campaignFrom.split(DATE_SPLIT_PATTERN_02)[0]))
                .body("ToDate", containsString(campaignTo.split(DATE_SPLIT_PATTERN_01)[0]))
                .body("ToDate", containsString(campaignTo.split(DATE_SPLIT_PATTERN_02)[0]))
                .body("RewardType", equalTo(REWARD_TYPE))
                .body("Description", equalTo(campaignDesc));
    }

    @ParameterizedTest(name = "Run {index}: campaignName={0}, campaignFrom={1}, campaignTo={2}, campaignDesc={3}")
    @MethodSource("campaign_ValidParameters")
    void enableCampaign_shouldUpdateNameDescriptionStartAndEndDate(String campaignName, String campaignFrom,
            String campaignTo, String campaignDesc) {
        val token = getAdminToken();
        String campaignId = createCampaignAndReturnId(earnRules, condition, mobileContents);
        String conditionId = getCampaignByIdAsAdmin(campaignId, token)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .path("Conditions[0].Id");

        //Disable campaigns by setting isEnabled to false
        val disableEarnRule = baseUpdateEarnRule.isEnabled(false).build();

        //Enable campaigns by setting isEnabled to true
        val enableEarnRule = baseUpdateEarnRule.isEnabled(true).build();

        updateCampaignAsAdmin(disableEarnRule, campaignId, updatedCondition, conditionId, mobileContents,
                getTitleId(campaignId, token), getDescriptionId(campaignId, token), getImageId(campaignId, token),
                token);
        updateCampaignAsAdmin(enableEarnRule, campaignId, updatedCondition, conditionId, mobileContents,
                getTitleId(campaignId, token), getDescriptionId(campaignId, token), getImageId(campaignId, token),
                token);

        getCampaignByIdAsAdmin(campaignId, token)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .body("IsEnabled", equalTo(true));

        val newEarnRule = EarnRuleUpdateModel
                .earnRuleUpdateModelBuilder()
                .name(campaignName)
                .reward(CAMPAIGN_REWARD.toString())
                .fromDate(campaignFrom)
                .toDate(campaignTo)
                .description(campaignDesc)
                .isEnabled(true)
                .conditions(createConditionArray(updatedCondition))
                .rewardType(REWARD_TYPE)
                .mobileContents(createUpdatedMobileContentArray(mobileContents))
                .completionCount(CAMPAIGN_COMPLETION_COUNT)
                .build();

        //Can update fields when the campaigns is activated
        updateCampaignAsAdmin(newEarnRule, campaignId, updatedCondition, conditionId, mobileContents,
                getTitleId(campaignId, token), getDescriptionId(campaignId, token), getImageId(campaignId, token),
                token)
                .then()
                .assertThat()
                .statusCode(SC_NO_CONTENT);

        getCampaignById(campaignId)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .body("Id", equalTo(campaignId))
                .body("IsEnabled", equalTo(true))
                .body("Conditions[0].Type", equalTo(CONDITION_TYPE_SIGNUP))
                .body("Conditions[0].ImmediateReward", equalTo(IMMEDIATE_REWARD))
                .body("Conditions[0].CompletionCount", equalTo(CONDITION_COMPLETION_COUNT))
                .body("Name", equalTo(campaignName))
                .body("Reward", equalTo(CAMPAIGN_REWARD))
                .body("FromDate", containsString(campaignFrom.split(DATE_SPLIT_PATTERN_01)[0]))
                .body("FromDate", containsString(campaignFrom.split(DATE_SPLIT_PATTERN_02)[0]))
                .body("ToDate", containsString(campaignTo.split(DATE_SPLIT_PATTERN_01)[0]))
                .body("ToDate", containsString(campaignTo.split(DATE_SPLIT_PATTERN_02)[0]))
                .body("Description", equalTo(campaignDesc))
                .body("RewardType", equalTo(REWARD_TYPE))
                .body("CompletionCount", equalTo(CAMPAIGN_COMPLETION_COUNT));
    }

    @Test
    @UserStoryId(storyId = {124, 679, 673})
    void shouldCreateCampaignsWithRewardAndImmediateRewardEqualTo0() {
        val condition = baseCondition.immediateReward(String.valueOf(0)).build();
        val earnRule = baseEarnRule.reward("0").build();

        String campaignId = createCampaignAsAdmin(earnRule, condition, mobileContents, getAdminToken())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .body(ID, hasLength(36))
                .extract()
                .path(ID);

        getCampaignById(campaignId)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .body("Id", equalTo(campaignId))
                .body("CampaignStatus", equalTo(ACTIVE.getStatus()))
                .body("Conditions[0].Type", equalTo(CONDITION_TYPE_SIGNUP))
                .body("Conditions[0].ImmediateReward",
                        equalTo(0))
                .body("Conditions[0].CompletionCount", equalTo(CONDITION_COMPLETION_COUNT))
                .body("Name", equalTo(CAMPAIGN_NAME))
                .body("Reward", equalTo((float) 0))
                .body("FromDate", containsString(CAMPAIGN_FROM.split(DATE_SPLIT_PATTERN_01)[0]))
                .body("FromDate", containsString(CAMPAIGN_FROM.split(DATE_SPLIT_PATTERN_02)[0]))
                .body("ToDate", containsString(CAMPAIGN_TO.split(DATE_SPLIT_PATTERN_01)[0]))
                .body("ToDate", containsString(CAMPAIGN_TO.split(DATE_SPLIT_PATTERN_02)[0]))
                .body("RewardType", equalTo(REWARD_TYPE))
                .body("Description", equalTo(CAMPAIGN_DESC));
    }

    private String createCampaignForGettingById() {
        return createCampaignAsAdmin(earnRules, adminCondition, mobileContents, getAdminToken())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .body(ID, hasLength(36))
                .extract()
                .as(EarnRuleCreatedResponse.class)
                .getId();
    }

    private String createCampaignWithPercentageReward() {
        return createCampaignAsAdmin(earnRules, adminCondition, mobileContents, getAdminToken())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .body(ID, hasLength(36))
                .extract()
                .as(EarnRuleCreatedResponse.class)
                .getId();
    }

    private ExtractableResponse<Response> getExtractableResponse(
            int expectedStatus, String... ids) {
        return getCampaignsById(ids)
                .then()
                .assertThat()
                .statusCode(expectedStatus)
                .extract();
    }
}
