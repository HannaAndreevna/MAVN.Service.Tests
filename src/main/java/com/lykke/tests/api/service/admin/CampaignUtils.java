package com.lykke.tests.api.service.admin;

import static com.lykke.api.testing.api.base.RequestHeader.getHeader;
import static com.lykke.api.testing.api.common.GenerateUtils.generateRandomString;
import static com.lykke.api.testing.api.common.QueryParamsUtils.getQueryParams;
import static com.lykke.tests.api.base.PathConsts.AdminApiService.BY_CAMPAIGN_ID;
import static com.lykke.tests.api.base.Paths.AdminApi.ADMIN_API_CAMPAIGN_BY_ID_PATH;
import static com.lykke.tests.api.base.Paths.AdminApi.ADMIN_API_CAMPAIGN_PATH;
import static com.lykke.tests.api.common.PaginationConts.CURRENT_PAGE_LOWER_BOUNDARY;
import static com.lykke.tests.api.common.PaginationConts.PAGE_SIZE_UPPER_BOUNDARY;
import static com.lykke.tests.api.service.admin.LoginAdminUtils.getAdminToken;
import static com.lykke.tests.api.service.admin.model.RewardType.FIXED;
import static com.lykke.tests.api.service.campaigns.CampaignUtils.createCampaign;
import static com.lykke.tests.api.service.campaigns.CampaignUtils.createConditionArray;
import static com.lykke.tests.api.service.customermanagement.RegisterCustomerUtils.registerDefaultVerifiedCustomer;
import static com.lykke.tests.api.service.partnermanagement.PartnerManagementUtils.createPartner;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.text.CharSequenceLength.hasLength;

import com.lykke.api.testing.api.common.FakerUtils;
import com.lykke.tests.api.common.enums.Localization;
import com.lykke.tests.api.service.admin.model.CampaignListRequest;
import com.lykke.tests.api.service.admin.model.EarnRuleListResponse;
import com.lykke.tests.api.service.admin.model.EarnRuleCreateModel;
import com.lykke.tests.api.service.admin.model.EarnRuleUpdateModel;
import com.lykke.tests.api.service.admin.model.MobileContentCreateRequest;
import com.lykke.tests.api.service.admin.model.RewardType;
import com.lykke.tests.api.service.admin.model.ValidationErrorResponse;
import com.lykke.tests.api.service.admin.model.burnrules.MobileContentEditRequest;
import com.lykke.tests.api.service.campaigns.model.BonusType;
import com.lykke.tests.api.service.campaigns.model.ConditionCreateModel;
import com.lykke.tests.api.service.campaigns.model.ConditionEditModel;
import com.lykke.tests.api.service.campaigns.model.MobileContents;
import com.lykke.tests.api.service.campaigns.model.burnrules.RuleContentType;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import lombok.experimental.UtilityClass;
import lombok.val;

@UtilityClass
public class CampaignUtils {

    public static final String CAMPAIGN_NAME = generateRandomString();
    public static final String ID = "Id";
    public static final int CAMPAIGN_COMPLETION_COUNT = 2;
    private static final int CONDITION_COMPLETION_COUNT = 4;
    private static final String CAMPAIGN_DESC = generateRandomString();
    private static final String CAMPAIGN_FROM = Instant.now().toString();
    private static final String CAMPAIGN_TO = "2025-05-23T06:59:26.627Z";
    private static final Double CAMPAIGN_REWARD = 500.0;
    private static final String CONDITION_TYPE_SIGNUP = "signup";
    private static final Integer IMMEDIATE_REWARD = 150;
    private static final Boolean IS_ENABLED = true;
/*
    //   private static final String CONDITION_TYPE_SIGNUP = "signup";
 //   private static final String CONDITION_TYPE_FRIEND_REFERRAL = "friend-referral";
 //   private static final String CONDITION_TYPE_PURCHASE = "mvn-purchase";
 //   private static final String CONDITION_TYPE_PURCHASE_REFERRAL = "purchase-referral";
    private static final Integer CONDITION_REWARD = 100000;
    //   private static final int CONDITION_COMPLETION_COUNT = 1;
    //   private static final String CAMPAIGN_NAME = generateRandomString();
    private static final String CAMPAIGN_CREATED_BY = generateRandomString();
    //   private static final String CAMPAIGN_FROM = Instant.now().toString();
    //  private static final String CAMPAIGN_TO = "2025-05-23T06:59:26.627Z";
    //  private static final String CAMPAIGN_DESC = generateRandomString();
    //  private static final Integer CAMPAIGN_REWARD = 200000;
    //  private static final int CAMPAIGN_COMPLETION_COUNT = 1;
    private static final Integer CAMPAIGN_REWARD_PERCENTAGE = 10;
    //   private static final RewardType REWARD_TYPE_PERCENTAGE = RewardType.PERCENTAGE;
    private static final RewardType REWARD_TYPE_FIXED = RewardType.FIXED;
    private static final String CAMPAIGN_ID_FIELD = "CampaignId";
    //   private static final String CAMPAIGNS_FIELD = "Campaigns";
    //  private static final int TOTAL_REWARD = CONDITION_REWARD + CAMPAIGN_REWARD;

    private static Campaign campaign;
    private static ConditionCreateModel bonusType;
    private static EarnRule earnRule;
    private static String campaignId;
    private static ConditionCreateModel.ConditionCreateModelBuilder baseCondition;
    private static Campaign.CampaignBuilder baseCampaign;
    private static EarnRule.EarnRuleBuilder baseEarnRule;
    */

    public Response deleteCampaign(String campaignId) {
        return getHeader(getAdminToken())
                .delete(ADMIN_API_CAMPAIGN_BY_ID_PATH.apply(campaignId));
    }

    public ValidationErrorResponse getCampaignsPaginatedValidationResponse(CampaignListRequest requestObject) {
        return getHeader(getAdminToken())
                .queryParams(getQueryParams(requestObject))
                .get(ADMIN_API_CAMPAIGN_PATH)
                .then()
                .assertThat()
                .statusCode(requestObject.getHttpStatus())
                .extract()
                .as(ValidationErrorResponse.class);
    }

    public EarnRuleListResponse getCampaignsPaginatedResponse(CampaignListRequest requestObject) {
        return getHeader(getAdminToken())
                .queryParams(getQueryParams(requestObject))
                .get(ADMIN_API_CAMPAIGN_PATH)
                .then()

                ////55
                .log().all()

                .assertThat()
                .statusCode(requestObject.getHttpStatus())
                .extract()
                .as(EarnRuleListResponse.class);
    }

    public com.lykke.tests.api.service.campaigns.model.ConditionCreateModel createConditionObject() {
        val baseAdminCondition = com.lykke.tests.api.service.campaigns.model.ConditionCreateModel
                .conditionCreateBuilder()
                .type(CONDITION_TYPE_SIGNUP)
                .completionCount(CONDITION_COMPLETION_COUNT)
                .immediateReward(IMMEDIATE_REWARD.toString());
        return baseAdminCondition
                .build();
    }

    public com.lykke.tests.api.service.admin.model.earnrules.ConditionCreateModel createConditionObject2() {
        val baseAdminCondition = com.lykke.tests.api.service.admin.model.earnrules.ConditionCreateModel
                .conditionCreateBuilder()
                .type(CONDITION_TYPE_SIGNUP)
                .completionCount(CONDITION_COMPLETION_COUNT)
                .immediateReward(IMMEDIATE_REWARD.toString())
                .stakingPeriod(com.lykke.tests.api.common.CommonConsts.Staking.DEFAULT_STAKING_PERIOD)
                .stakeWarningPeriod(com.lykke.tests.api.common.CommonConsts.Staking.DEFAULT_STAKE_WARNING_PERIOD)
                .rewardType(FIXED);
        return baseAdminCondition
                .build();
    }

    ////55 estate-lead-referral
    public com.lykke.tests.api.service.admin.model.earnrules.ConditionCreateModel createConditionObjectWithVertical() {
        val baseAdminCondition = com.lykke.tests.api.service.admin.model.earnrules.ConditionCreateModel
                .conditionCreateBuilder()
                .type("estate-lead-referral")
                .completionCount(CONDITION_COMPLETION_COUNT)
                .immediateReward(IMMEDIATE_REWARD.toString())
                .stakingPeriod(com.lykke.tests.api.common.CommonConsts.Staking.DEFAULT_STAKING_PERIOD)
                .stakeWarningPeriod(com.lykke.tests.api.common.CommonConsts.Staking.DEFAULT_STAKE_WARNING_PERIOD)
                .rewardType(FIXED);
        return baseAdminCondition
                .build();
    }

    public BonusType createConditionObject1() {
        val baseAdminCondition = BonusType
                .builder()
                .type(CONDITION_TYPE_SIGNUP)
                .completionCount(CONDITION_COMPLETION_COUNT)
                .immediateReward(IMMEDIATE_REWARD.toString());
        return baseAdminCondition
                .build();
    }

    public EarnRuleCreateModel createCampaignObject() {
        val baseEarnRule = EarnRuleCreateModel
                .earnRuleCreateModelBuilder()
                .isEnabled(true)
                .name(CAMPAIGN_NAME)
                .rewardType(FIXED)
                .reward(CAMPAIGN_REWARD.toString())
                .fromDate(CAMPAIGN_FROM)
                .toDate(CAMPAIGN_TO)
                .completionCount(CAMPAIGN_COMPLETION_COUNT)
                .description(CAMPAIGN_DESC)
                .isEnabled(IS_ENABLED)
                .mobileContents(createMobileContentArray(createMobileContentObject()))
                .conditions(createConditionArray(createConditionObject2()));
        return baseEarnRule
                .build();
    }

    public EarnRuleCreateModel createUpdateCampaignObject() {
        val baseEarnRule = EarnRuleCreateModel
                .earnRuleCreateModelBuilder()
                .isEnabled(true)
                .name(CAMPAIGN_NAME)
                .rewardType(FIXED)
                .reward(CAMPAIGN_REWARD.toString())
                .fromDate(CAMPAIGN_FROM)
                .toDate(CAMPAIGN_TO)
                .completionCount(CAMPAIGN_COMPLETION_COUNT)
                .description(CAMPAIGN_DESC)
                .isEnabled(IS_ENABLED)
                .mobileContents(createMobileContentArray(createMobileContentObject()))
                .conditions(createConditionArray(createConditionObject2()));
        return baseEarnRule
                .build();
    }

    public MobileContents createMobileContentObject() {
        val baseMobileContent = MobileContents
                .builder()
                .mobileLanguage(Localization.EN)
                .title(RuleContentType.TITLE)
                .description(FakerUtils.title);
        return baseMobileContent
                .build();
    }

    public String createCampaignAndReturnId() {
        val adminCondition = createConditionObject2();
        val adminCampaign = createCampaignObject();
        val mobileContent = createMobileContentObject();
        val campaignId = createCampaignAsAdmin(adminCampaign, adminCondition, mobileContent, getAdminToken())
                .then()

                ////55
                .log().all()

                .assertThat()
                .statusCode(SC_OK)
                .body(ID, hasLength(36))
                .extract()
                .path(ID);
        return campaignId.toString();
    }

    public String createCampaignWithVerticalAndReturnId() {
        val adminCondition = createConditionObjectWithVertical();
        val adminCampaign = createCampaignObject();
        val mobileContent = createMobileContentObject();
        val campaignId = createCampaignAsAdmin(adminCampaign, adminCondition, mobileContent, getAdminToken())
                .then()

                ////55
                .log().all()

                .assertThat()
                .statusCode(SC_OK)
                .body(ID, hasLength(36))
                .extract()
                .path(ID);
        return campaignId.toString();
    }

    public String createCampaignWithPercentageReward() {
        val adminCondition = createConditionObject2();
        adminCondition
                .setRewardType(RewardType.PERCENTAGE)
                .setApproximateAward(Double.valueOf(99).toString())
                .setImmediateReward(Double.valueOf(75).toString());
        val adminCampaign = createCampaignObject();
        adminCampaign
                .setReward(Double.valueOf(70).toString())
                .setRewardType(RewardType.PERCENTAGE)
                .setApproximateAward(Double.valueOf(80).toString())
                .setOrder(111);
        val mobileContent = createMobileContentObject();
        val campaignId = createCampaignAsAdmin(adminCampaign, adminCondition, mobileContent, getAdminToken())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .body(ID, hasLength(36))
                .extract()
                .path(ID);
        return campaignId.toString();
    }

    /*
    // TODO: finish the method
    public static String createCampaignWithStakingAndReturnId(PartnerDto partnerData) {

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
                .ruleContentType(com.lykke.tests.api.common.enums.RuleContentType.TITLE)
                .localization(Localization.EN)
                .value(FakerUtils.title);

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
                .ruleContentType(com.lykke.tests.api.common.enums.RuleContentType.TITLE)
                .localization(Localization.EN)
                .value(FakerUtils.title)
                .build();

        //    val partnerData = createPartner(generateRandomString(10));
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
                .build();
        val condition2 = baseCondition
                .type(ConditionType.HOTEL_STAY_REFERRAL.getValue())
                .immediateReward(immediateReward_condition_2.toString())
                .completionCount(1)
                .hasStaking(true)
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

        return campaignId;
    }
    */

    public String createCampaignAndReturnId(EarnRuleCreateModel earnRulesModel,
            com.lykke.tests.api.service.admin.model.earnrules.ConditionCreateModel bonusType,
            MobileContents mobileContents) {
        val campaignId = createCampaignAsAdmin(earnRulesModel, bonusType, mobileContents, getAdminToken())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .body(ID, hasLength(36))
                .extract()
                .path(ID);
        return campaignId.toString();
    }

    public String createPendingCampaignAndReturnId() {
        val adminCondition = createConditionObject2();
        val adminCampaign = createCampaignObject();
        val mobileContent = createMobileContentObject();
        adminCampaign.setFromDate(Instant.now().plus(5, ChronoUnit.DAYS).toString());
        val campaignId = createCampaignAsAdmin(adminCampaign, adminCondition, mobileContent, getAdminToken())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .body(ID, hasLength(36))
                .extract()
                .path(ID);
        return campaignId.toString();
    }

    public static Response createCampaignAsAdmin(EarnRuleCreateModel campaign,
            com.lykke.tests.api.service.admin.model.earnrules.ConditionCreateModel condition,
            MobileContents mobileContent, String token) {
        return getHeader(token)
                .body(createCampaignObject(campaign, condition, mobileContent))
                .post(ADMIN_API_CAMPAIGN_PATH);
    }

    public Response updateCampaign(EarnRuleCreateModel earnRulesModel, String token) {
        return getHeader(token)
                .body(earnRulesModel)
                .put(ADMIN_API_CAMPAIGN_PATH);
    }

    public Response updateCampaignAsAdmin(EarnRuleUpdateModel earnRulesModel, String campaignId,
            com.lykke.tests.api.service.admin.model.earnrules.ConditionUpdateModel condition,
            String conditionId, MobileContents mobileContents, String titleId, String descriptionId, String imageId,
            String token) {
        return getHeader(token)
                .body(campaignAdminUpdate(earnRulesModel, campaignId, condition, conditionId, mobileContents,
                        titleId, descriptionId, imageId))
                .put(ADMIN_API_CAMPAIGN_PATH);
    }

    public Response getCampaignByIdAsAdmin(String campaignId, String token) {
        return getHeader(token)
                .get(ADMIN_API_CAMPAIGN_PATH + String.format(BY_CAMPAIGN_ID.getPath(), campaignId));
    }

    public String getTitleId(String campaignId, String token) {
        return getCampaignByIdAsAdmin(campaignId, token)
                .then()
                .extract()
                .path("MobileContents[0].TitleId");
    }

    public String getDescriptionId(String campaignId, String token) {
        return getCampaignByIdAsAdmin(campaignId, token)
                .then()
                .extract()
                .path("MobileContents[0].DescriptionId");
    }

    public String getImageId(String campaignId, String token) {
        return getCampaignByIdAsAdmin(campaignId, token)
                .then()
                .extract()
                .path("MobileContents[0].ImageId");
    }

    @Step("Get campaigns by admin")
    public Response getCampaignsAsAdmin(String token) {
        return getHeader(token)
                .queryParams(getQueryParams(
                        CampaignListRequest
                                .campaignBuilder()
                                .pageSize(PAGE_SIZE_UPPER_BOUNDARY)
                                .currentPage(CURRENT_PAGE_LOWER_BOUNDARY)
                                .build()))
                .get(ADMIN_API_CAMPAIGN_PATH);
    }

    public static com.lykke.tests.api.service.campaigns.model.ConditionCreateModel[] createConditionArray(
            com.lykke.tests.api.service.campaigns.model.ConditionCreateModel condition) {
        return new com.lykke.tests.api.service.campaigns.model.ConditionCreateModel[]{
                createConditionModelFromBonusType(condition)
        };
    }

    public static com.lykke.tests.api.service.admin.model.earnrules.ConditionCreateModel[] createConditionArray(
            com.lykke.tests.api.service.admin.model.earnrules.ConditionCreateModel condition) {
        return new com.lykke.tests.api.service.admin.model.earnrules.ConditionCreateModel[]{
                condition
        };
    }

    public static com.lykke.tests.api.service.admin.model.earnrules.ConditionUpdateModel[] createConditionArray(
            com.lykke.tests.api.service.admin.model.earnrules.ConditionUpdateModel condition) {
        return new com.lykke.tests.api.service.admin.model.earnrules.ConditionUpdateModel[]{
                condition
        };
    }

    public static ConditionCreateModel[] createConditionArray1(BonusType condition) {
        return new ConditionCreateModel[]{
                createConditionModelFromBonusType1(condition)
        };
    }

    public static com.lykke.tests.api.service.admin.model.earnrules.ConditionCreateModel[] createConditionArray2(
            com.lykke.tests.api.service.admin.model.earnrules.ConditionCreateModel condition) {
        return new com.lykke.tests.api.service.admin.model.earnrules.ConditionCreateModel[]{
                condition
        };
    }

    public static MobileContentCreateRequest[] createMobileContentArray(MobileContents mobileContent) {
        return new MobileContentCreateRequest[]{
                createMobileContentObject(mobileContent)
        };
    }

    public static MobileContentEditRequest[] createUpdatedMobileContentArray(MobileContents mobileContent) {
        return new MobileContentEditRequest[]{
                createUpdatedMobileContentObject(mobileContent)
        };
    }

    private static EarnRuleUpdateModel campaignAdminUpdate(EarnRuleUpdateModel earnRulesModel, String campaignId,
            com.lykke.tests.api.service.admin.model.earnrules.ConditionUpdateModel condition, String conditionId,
            MobileContents mobileContents, String titleId,
            String descriptionId, String imageId) {
        ////55     earnRulesModel.setIsEnabled(true); // To be updated with feature update
        earnRulesModel.setConditions(createAdminConditionUpdateArray1(condition, conditionId));
        earnRulesModel.setMobileContents(createMobileContentsUpdatearray(mobileContents, titleId, descriptionId,
                imageId));
        return earnRulesModel;
    }

    MobileContentEditRequest[] createMobileContentsUpdatearray(MobileContents mobileContents, String titleId,
            String descriptionId, String imageId) {
        return new MobileContentEditRequest[]{
                MobileContentEditRequest
                        .mobileContentEditRequestBuilder()
                        .mobileLanguage(mobileContents.getMobileLanguage())
                        .title(mobileContents.getTitle().getCode())
                        .description(mobileContents.getDescription())
                        .titleId(titleId)
                        .descriptionId(descriptionId)
                        .build()
        };
    }

    com.lykke.tests.api.service.campaigns.model.ConditionCreateModel[] createAdminConditionUpdateArray(
            com.lykke.tests.api.service.campaigns.model.ConditionCreateModel condition, String conditionId) {
        return new com.lykke.tests.api.service.campaigns.model.ConditionCreateModel[]{
                com.lykke.tests.api.service.campaigns.model.ConditionCreateModel
                        .conditionCreateBuilder()
                        .type(condition.getType())
                        .completionCount(condition.getCompletionCount())
                        .immediateReward(condition.getImmediateReward())
                        .build()
        };
    }

    com.lykke.tests.api.service.admin.model.earnrules.ConditionCreateModel[] createAdminConditionUpdateArray1(
            com.lykke.tests.api.service.admin.model.earnrules.ConditionCreateModel condition, String conditionId) {
        return new com.lykke.tests.api.service.admin.model.earnrules.ConditionCreateModel[]{
                condition
        };
    }

    com.lykke.tests.api.service.admin.model.earnrules.ConditionUpdateModel[] createAdminConditionUpdateArray1(
            com.lykke.tests.api.service.admin.model.earnrules.ConditionUpdateModel condition, String conditionId) {
        return new com.lykke.tests.api.service.admin.model.earnrules.ConditionUpdateModel[]{
                condition
        };
    }

    public static com.lykke.tests.api.service.campaigns.model.ConditionCreateModel createConditionModelFromBonusType(
            com.lykke.tests.api.service.campaigns.model.ConditionCreateModel condition) {
        condition.setPartnerIds(new String[]{});
        return condition;
    }

    public static com.lykke.tests.api.service.admin.model.earnrules.ConditionCreateModel createConditionModelFromBonusType(
            com.lykke.tests.api.service.admin.model.earnrules.ConditionCreateModel condition) {
        return condition;
    }

    public static ConditionCreateModel createConditionModelFromBonusType1(BonusType condition) {
        return ConditionCreateModel
                .conditionCreateBuilder()
                .type(condition.getType())
                .immediateReward(condition.getImmediateReward())
                .completionCount(condition.getCompletionCount())
                // TODO: pass here a real Guid
                .partnerIds(new String[]{})
                .build();
    }

    public static ConditionEditModel createConditionEditModelFromBonusType(BonusType condition) {
        return ConditionEditModel
                .conditionEditBuilder()
                .type(condition.getType())
                .immediateReward(condition.getImmediateReward())
                .completionCount(condition.getCompletionCount())
                // TODO: pass here a real Guid
                .partnerIds(new String[]{})
                .build();
    }

    public static ConditionEditModel createConditionEditModelFromBonusType(ConditionCreateModel condition) {
        return ConditionEditModel
                .conditionEditBuilder()
                .type(condition.getType())
                .immediateReward(condition.getImmediateReward())
                .completionCount(condition.getCompletionCount())
                // TODO: pass here a real Guid
                .partnerIds(new String[]{})
                .build();
    }

    public static MobileContentCreateRequest createMobileContentObject(MobileContents mobileContent) {
        return MobileContentCreateRequest
                .builder()
                .mobileLanguage(mobileContent.getMobileLanguage())
                .title(mobileContent.getTitle().getCode())
                .description(mobileContent.getDescription())
                .build();
    }

    public static MobileContentEditRequest createUpdatedMobileContentObject(MobileContents mobileContent) {
        return MobileContentEditRequest
                .mobileContentEditRequestBuilder()
                .mobileLanguage(mobileContent.getMobileLanguage())
                .title(mobileContent.getTitle().getCode())
                .description(mobileContent.getDescription())
                .build();
    }

    private EarnRuleCreateModel createCampaignObject(EarnRuleCreateModel campaign,
            com.lykke.tests.api.service.admin.model.earnrules.ConditionCreateModel condition,
            MobileContents mobileContent) {
        campaign.setRewardType(campaign.getRewardType());
        campaign.setCompletionCount(campaign.getCompletionCount());
        campaign.setIsEnabled(campaign.getIsEnabled());
        campaign.setConditions(createConditionArray(condition));
        campaign.setMobileContents(createMobileContentArray(mobileContent));
        return campaign;
    }
}
