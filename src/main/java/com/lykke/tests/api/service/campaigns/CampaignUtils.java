package com.lykke.tests.api.service.campaigns;

import static com.lykke.api.testing.api.base.RequestHeader.getHeader;
import static com.lykke.api.testing.api.common.QueryParamsUtils.getQueryParams;
import static com.lykke.tests.api.base.PathConsts.CampaignsEndpoint.ACTIVE_CAMPAIGN_PATH;
import static com.lykke.tests.api.base.PathConsts.CampaignsEndpoint.CAMPAIGN_ALL_PATH;
import static com.lykke.tests.api.base.PathConsts.CampaignsEndpoint.CAMPAIGN_BY_ID_PATH;
import static com.lykke.tests.api.base.Paths.CAMPAIGNS_API_PATH;
import static com.lykke.tests.api.service.admin.CampaignUtils.createConditionEditModelFromBonusType;
import static com.lykke.tests.api.service.admin.CampaignUtils.createConditionModelFromBonusType;
import static com.lykke.tests.api.service.admin.CampaignUtils.createConditionModelFromBonusType1;
import static java.util.stream.Collectors.toMap;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.http.HttpStatus.SC_OK;

import com.lykke.api.testing.api.common.QueryParamsUtils;
import com.lykke.tests.api.base.Paths.Campaigns;
import com.lykke.tests.api.common.enums.Localization;
import com.lykke.tests.api.common.enums.RuleContentType;
import com.lykke.tests.api.service.campaigns.model.BonusType;
import com.lykke.tests.api.service.campaigns.model.Campaign;
import com.lykke.tests.api.service.campaigns.model.CampaignCreateResponseModel;
import com.lykke.tests.api.service.campaigns.model.CampaignDetailResponseModel;
import com.lykke.tests.api.service.campaigns.model.CampaignEditModel;
import com.lykke.tests.api.service.campaigns.model.CampaignSortBy;
import com.lykke.tests.api.service.campaigns.model.CampaignStatus;
import com.lykke.tests.api.service.campaigns.model.CampaignsInfoListResponseModel;
import com.lykke.tests.api.service.campaigns.model.CampaignsPaginationRequestModel;
import com.lykke.tests.api.service.campaigns.model.ConditionCreateModel;
import com.lykke.tests.api.service.campaigns.model.ConditionEditModel;
import com.lykke.tests.api.service.campaigns.model.EarnRule;
import com.lykke.tests.api.service.campaigns.model.EarnRuleContentEditRequest;
import com.lykke.tests.api.service.campaigns.model.EarnRuleContentResponse;
import com.lykke.tests.api.service.campaigns.model.ListSortDirection;
import com.lykke.tests.api.service.campaigns.model.earnrules.FileCreateRequestModel;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Stream;
import lombok.experimental.UtilityClass;
import lombok.val;
import org.json.simple.JSONObject;

@UtilityClass
public class CampaignUtils {

    public static final String TOTAL_COUNT_FIELD = "TotalCount";
    public static final String CAMPAIGN_NAME_FIELD = "CampaignName";
    public static final String CONDITION_TYPE_FIELD = "ConditionType";
    public static final String CAMPAIGN_STATUS_FIELD = "CampaignStatus";
    public static final String SORT_BY_FIELD = "SortBy";
    public static final String SORT_DIRECTION = "SortDirection";
    public static final String CURRENT_PAGE_FIELD = "CurrentPage";
    public static final String PAGE_SIZE_FIELD = "PageSize";

    public static final String CAMPAIGNS_IDS_QUERY_PARAM = "campaignsIds";

    public String getCampaignId(Campaign campaign, ConditionCreateModel bonusType,
            EarnRule earnRule) {
        return createCampaign(campaign, bonusType, earnRule)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(CampaignCreateResponseModel.class)
                .getCampaignId();
    }

    @Step("Create campaign")
    public static Response createCampaign(Campaign campaign, BonusType bonusType,
            EarnRule earnRule) {
        return getHeader()
                .body(createCampaignObject(campaign, bonusType, earnRule))
                .post(CAMPAIGNS_API_PATH);
    }

    @Step("Create campaign")
    public static Response createCampaign(Campaign campaign, ConditionCreateModel condition,
            EarnRule earnRule) {
        return getHeader()
                .body(createCampaignObject(campaign, condition, earnRule))
                .post(CAMPAIGNS_API_PATH);
    }

    @Step("Create Earn Rule")
    public static Response createEarnRule(Campaign campaign) {
        return getHeader()
                .body(campaign)
                .post(CAMPAIGNS_API_PATH);
    }

    public static Response addEarnRuleImage(FileCreateRequestModel fileCreateRequest) {
        return getHeader()
                .body(QueryParamsUtils.getQueryParams(fileCreateRequest))
                .post(Campaigns.EARN_RULE_IMAGE_API_PATH);
    }

    public static String getEarnRuleContentIdByContentType(String earnRuleId, RuleContentType contentType,
            Localization localization) {

        val earnRule = getEarnRuleById(earnRuleId);

        return Arrays.stream(earnRule.getContents())
                .filter(content -> contentType == content.getRuleContentType()
                        && localization == content.getLocalization())
                .reduce((first, second) -> second)
                .orElse(new EarnRuleContentResponse()).getId();
    }


    public Response createCampaignWithInfiniteCompletionCount(Campaign campaign, BonusType bonusType,
            EarnRule earnRule) {
        return getHeader()
                .body(createCampaignObjectWithNullCompletionCount(campaign, bonusType, earnRule))
                .post(CAMPAIGNS_API_PATH);
    }

    public Response createCampaignWithInfiniteCompletionCount(Campaign campaign, ConditionCreateModel condition,
            EarnRule earnRule) {
        return getHeader()
                .body(createCampaignObjectWithNullCompletionCount(campaign, condition, earnRule))
                .post(CAMPAIGNS_API_PATH);
    }

    Response createCampaign(Campaign campaign, BonusType bonusType1, BonusType bonusType2,
            EarnRule earnRule) {
        return getHeader()
                .body(createCampaignObject(campaign, bonusType1, bonusType2, earnRule))
                .post(CAMPAIGNS_API_PATH);
    }

    public Response createCampaign(Campaign campaign, ConditionCreateModel bonusType1, ConditionCreateModel bonusType2,
            EarnRule earnRule) {
        return getHeader()
                .body(createCampaignObject(campaign, bonusType1, bonusType2, earnRule))
                .post(CAMPAIGNS_API_PATH);
    }

    Response createCampaign(Campaign campaign, BonusType bonusType1, BonusType bonusType2,
            BonusType bonusType3, EarnRule earnRule) {
        return getHeader()
                .body(createCampaignObject(campaign, bonusType1, bonusType2, bonusType3, earnRule))
                .post(CAMPAIGNS_API_PATH);
    }

    Response createCampaign(Campaign campaign, ConditionCreateModel bonusType1, ConditionCreateModel bonusType2,
            ConditionCreateModel bonusType3, EarnRule earnRule) {
        return getHeader()
                .body(createCampaignObject(campaign, bonusType1, bonusType2, bonusType3, earnRule))
                .post(CAMPAIGNS_API_PATH);
    }

    public int getTotalCount() {
        return getCampaigns()
                .then()
                .extract()
                .path(TOTAL_COUNT_FIELD);
    }

    public static Response getCampaigns() {
        return getHeader()
                .get(CAMPAIGNS_API_PATH);
    }

    Response getFilteredCampaigns(String name, String condtionType, CampaignStatus status, CampaignSortBy sortBy,
            ListSortDirection sortDirection,
            int currentPage, int pageSize) {
        return getHeader()
                .queryParams(getQueryParams(
                        CampaignsPaginationRequestModel
                                .requestModelBuilder()
                                .campaignName(name)
                                .conditionType(condtionType)
                                .campaignStatus(status)
                                .sortBy(sortBy)
                                .sortDirection(sortDirection)
                                .currentPage(currentPage)
                                .pageSize(pageSize)
                                .build()))
                .get(CAMPAIGNS_API_PATH);
    }

    @Deprecated
    public Response getActiveCampaigns() {
        return getHeader()
                .get(CAMPAIGNS_API_PATH + ACTIVE_CAMPAIGN_PATH.getPath());
    }

    public static Response getCampaignById(String campaignId) {
        return getHeader()
                .get(CAMPAIGNS_API_PATH + CAMPAIGN_BY_ID_PATH.getFilledInPath(campaignId));
    }

    public CampaignDetailResponseModel getEarnRuleById(String campaignId) {
        return getHeader()
                .get(CAMPAIGNS_API_PATH + CAMPAIGN_BY_ID_PATH.getFilledInPath(campaignId))
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(CampaignDetailResponseModel.class);
    }

    public Response getCampaignsById(String... campaignIds) {
        val header = getHeader();
        if (null != campaignIds && 0 < campaignIds.length) {
            Arrays.stream(campaignIds)
                    .forEach(id -> header.queryParam(CAMPAIGNS_IDS_QUERY_PARAM, id));
        }
        return header
                .get(CAMPAIGNS_API_PATH + CAMPAIGN_ALL_PATH.getPath());
    }

    public CampaignsInfoListResponseModel getCampaignsByIds(String... campaignIds) {
        val header = getHeader();
        if (null != campaignIds && 0 < campaignIds.length) {
            Arrays.stream(campaignIds)
                    .forEach(id -> header.queryParam(CAMPAIGNS_IDS_QUERY_PARAM, id));
        }
        return header
                .get(CAMPAIGNS_API_PATH + CAMPAIGN_ALL_PATH.getPath())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(CampaignsInfoListResponseModel.class);
    }

    Response updateCampaignById(JSONObject updateObject) {
        return getHeader()
                .body(updateObject)
                .put(CAMPAIGNS_API_PATH);
    }

    public Response updateCampaignById(CampaignEditModel newCampaign, ConditionCreateModel newBonusType,
            String campaignId,
            String conditionId, EarnRule earnRule, String earnRuleId) {
        return getHeader()
                .body(createCampaignUpdateObject(
                        newCampaign, newBonusType, campaignId, conditionId, earnRule, earnRuleId))
                .put(CAMPAIGNS_API_PATH);
    }

    @Step("Delete campagin {campaignId}")
    public static Response deleteCampaign(String campaignId) {
        return getHeader()
                .delete(CAMPAIGNS_API_PATH + String.format(CAMPAIGN_BY_ID_PATH.getPath(), campaignId));
    }

    CampaignEditModel createCampaignUpdateObject(CampaignEditModel campaign, BonusType bonusType,
            String campaignIdToBeUpdated,
            String conditionId, EarnRule earnRule, String earnRuleId) {
        campaign.setConditions(createConditionEditModelUpdateArray(bonusType, conditionId));
        campaign.setContents(createEarnRuleContentEditArray(earnRule, earnRuleId));
        return campaign;
    }

    CampaignEditModel createCampaignUpdateObject(CampaignEditModel campaign, ConditionCreateModel bonusType,
            String campaignIdToBeUpdated,
            String conditionId, EarnRule earnRule, String earnRuleId) {
        campaign.setId(campaignIdToBeUpdated);
        campaign.setConditions(createConditionEditModelUpdateArray(bonusType, conditionId));
        campaign.setContents(createEarnRuleContentEditArray(earnRule, earnRuleId));
        return campaign;
    }

    public static com.lykke.tests.api.service.admin.model.earnrules.ConditionCreateModel[] createConditionArray(
            com.lykke.tests.api.service.admin.model.earnrules.ConditionCreateModel bonusType) {
        return new com.lykke.tests.api.service.admin.model.earnrules.ConditionCreateModel[]{
                createConditionModelFromBonusType(bonusType)
        };
    }

    public static ConditionCreateModel[] createConditionArray(BonusType bonusType) {
        return new ConditionCreateModel[]{
                createConditionModelFromBonusType1(bonusType)
        };
    }

    public static ConditionCreateModel[] createConditionArray(
            ConditionCreateModel condition) {
        // TODO: ??
        condition.setHasStaking(false);
        // TODO: remove it after FAL-3357 is done
        condition.setStakingPeriod(1);
        return new ConditionCreateModel[]{
                condition
        };
    }

    public static ConditionEditModel[] createConditionEditArray(BonusType bonusType) {
        return new ConditionEditModel[]{
                createConditionEditModelFromBonusType(bonusType)
        };
    }

    public static ConditionEditModel[] createConditionEditArray(ConditionCreateModel bonusType) {
        return new ConditionEditModel[]{
                createConditionEditModelFromBonusType(bonusType)
        };
    }

    public EarnRule[] createEarnRuleArray(
            EarnRule earnRule) {
        return new EarnRule[]{
                EarnRule
                        .builder()
                        .ruleContentType(earnRule.getRuleContentTypeValue())
                        .localization(earnRule.getLocalization())
                        .value(earnRule.getValue())
                        .build()
        };
    }

    public EarnRule[] createEarnRuleEditArray(
            EarnRule earnRule, String earnRuleId) {
        return new EarnRule[]{
                EarnRule
                        .builder()
                        .ruleContentType(earnRule.getRuleContentTypeValue())
                        .localization(earnRule.getLocalization())
                        .value(earnRule.getValue())
                        .build()
        };
    }

    public EarnRuleContentEditRequest[] createEarnRuleContentEditArray(
            EarnRule earnRule, String earnRuleId) {
        return new EarnRuleContentEditRequest[]{
                EarnRuleContentEditRequest
                        .builder()
                        .ruleContentType(earnRule.getRuleContentTypeValue())
                        .localization(earnRule.getLocalization())
                        .value(earnRule.getValue())
                        .id(earnRuleId)
                        .build()
        };
    }

    ConditionCreateModel[] createConditionArray(BonusType bonusType1, BonusType bonusType2) {
        return new ConditionCreateModel[]{
                ConditionCreateModel
                        .conditionCreateBuilder()
                        .type(bonusType1.getType())
                        .immediateReward(bonusType1.getImmediateReward())
                        .completionCount(bonusType1.getCompletionCount())
                        .build(),
                ConditionCreateModel
                        .conditionCreateBuilder()
                        .type(bonusType2.getType())
                        .immediateReward(bonusType2.getImmediateReward())
                        .completionCount(bonusType2.getCompletionCount())
                        .build()
        };
    }

    ConditionCreateModel[] createConditionArray(ConditionCreateModel bonusType1, ConditionCreateModel bonusType2) {
        return new ConditionCreateModel[]{
                bonusType1,
                bonusType2
        };
    }

    ConditionCreateModel[] createConditionArray(BonusType bonusType1, BonusType bonusType2, BonusType bonusType3) {
        return new ConditionCreateModel[]{
                ConditionCreateModel
                        .conditionCreateBuilder()
                        .type(bonusType1.getType())
                        .immediateReward(bonusType1.getImmediateReward())
                        .completionCount(bonusType1.getCompletionCount())
                        .build(),
                ConditionCreateModel
                        .conditionCreateBuilder()
                        .type(bonusType2.getType())
                        .immediateReward(bonusType2.getImmediateReward())
                        .completionCount(bonusType2.getCompletionCount())
                        .build(),
                ConditionCreateModel
                        .conditionCreateBuilder()
                        .type(bonusType3.getType())
                        .immediateReward(bonusType3.getImmediateReward())
                        .completionCount(bonusType3.getCompletionCount())
                        .build()
        };
    }

    ConditionCreateModel[] createConditionUpdateArray(BonusType bonusType, String conditionId) {
        return new ConditionCreateModel[]{
                ConditionCreateModel
                        .conditionCreateBuilder()
                        .type(bonusType.getType())
                        .immediateReward(bonusType.getImmediateReward())
                        .completionCount(bonusType.getCompletionCount())
                        .build()
        };
    }

    ConditionEditModel[] createConditionEditModelUpdateArray(BonusType bonusType, String conditionId) {
        return new ConditionEditModel[]{
                ConditionEditModel
                        .conditionEditBuilder()
                        .type(bonusType.getType())
                        .immediateReward(bonusType.getImmediateReward())
                        .completionCount(bonusType.getCompletionCount())
                        .build()
        };
    }

    ConditionEditModel[] createConditionEditModelUpdateArray(ConditionCreateModel bonusType, String conditionId) {
        return new ConditionEditModel[]{
                ConditionEditModel
                        .conditionEditBuilder()
                        .type(bonusType.getType())
                        .immediateReward(bonusType.getImmediateReward())
                        .completionCount(bonusType.getCompletionCount())
                        .build()
        };
    }

    private static JSONObject createCampaignObject(Campaign campaign) {
        JSONObject campaignObject = new JSONObject();
        campaignObject.put("Name", campaign.getName());
        campaignObject.put("Reward", campaign.getReward());
        campaignObject.put("FromDate", campaign.getFromDate());
        campaignObject.put("ToDate", campaign.getToDate());
        campaignObject.put("Description", campaign.getDescription());
        campaignObject.put("CreatedBy", campaign.getCreatedBy());
        campaignObject.put("RewardType", campaign.getRewardType());
        campaignObject.put("CompletionCount", campaign.getCompletionCount());
        campaignObject.put("Contents", campaign.getContents());
        return campaignObject;
    }

    private Campaign createCampaignObject(Campaign campaign, BonusType bonusType, EarnRule earnRule) {
        campaign.setRewardType(campaign.getRewardTypeValue());
        campaign.setCompletionCount(campaign.getCompletionCount());
        campaign.setConditions(createConditionArray(bonusType));
        campaign.setContents(createEarnRuleArray(earnRule));

        return campaign;
    }

    private Campaign createCampaignObject(Campaign campaign, ConditionCreateModel condition,
            EarnRule earnRule) {
        campaign.setCompletionCount(campaign.getCompletionCount());
        campaign.setConditions(createConditionArray(condition));
        campaign.setContents(createEarnRuleArray(earnRule));

        return campaign;
    }

    private static JSONObject createCampaignObjectWithNullCompletionCount(Campaign campaign, BonusType bonusType,
            EarnRule earnRule) {
        JSONObject campaignObject = createCampaignObject(campaign);
        campaignObject.put("Conditions", createConditionArray(bonusType));
        campaignObject.put("CompletionCount", null);
        campaignObject.put("Contents", createEarnRuleArray(earnRule));
        return campaignObject;
    }

    private static JSONObject createCampaignObjectWithNullCompletionCount(Campaign campaign,
            ConditionCreateModel condition,
            EarnRule earnRule) {
        JSONObject campaignObject = createCampaignObject(campaign);
        campaignObject.put("Conditions", createConditionArray(condition));
        campaignObject.put("CompletionCount", null);
        campaignObject.put("Contents", createEarnRuleArray(earnRule));
        return campaignObject;
    }

    private Campaign createCampaignObject(Campaign campaign, BonusType bonusType1, BonusType bonusType2,
            EarnRule earnRule) {
        campaign.setConditions(createConditionArray(bonusType1, bonusType2));
        campaign.setContents(createEarnRuleArray(earnRule));
        return campaign;
    }

    private Campaign createCampaignObject(Campaign campaign, ConditionCreateModel bonusType1,
            ConditionCreateModel bonusType2,
            EarnRule earnRule) {
        campaign.setConditions(createConditionArray(bonusType1, bonusType2));
        campaign.setContents(createEarnRuleArray(earnRule));
        return campaign;
    }

    private Campaign createCampaignObject(Campaign campaign, BonusType bonusType1, BonusType bonusType2,
            BonusType bonusType3, EarnRule earnRule) {
        campaign.setConditions(createConditionArray(bonusType1, bonusType2, bonusType3));
        campaign.setContents(createEarnRuleArray(earnRule));
        return campaign;
    }

    private Campaign createCampaignObject(Campaign campaign, ConditionCreateModel bonusType1,
            ConditionCreateModel bonusType2,
            ConditionCreateModel bonusType3, EarnRule earnRule) {
        campaign.setConditions(new ConditionCreateModel[]{bonusType1, bonusType2, bonusType3});
        campaign.setContents(createEarnRuleArray(earnRule));
        return campaign;
    }

    private Map<String, String> getPaginationQueryParams(String name, String conditionType, String status,
            String sortBy, String sortDirection, int currentPage, int pageSize) {
        return Stream.of(new String[][]{
                {CAMPAIGN_NAME_FIELD, name},
                {CONDITION_TYPE_FIELD, conditionType},
                {CAMPAIGN_STATUS_FIELD, status},
                {SORT_BY_FIELD, sortBy},
                {SORT_DIRECTION, sortDirection},
                {CURRENT_PAGE_FIELD, String.valueOf(currentPage)},
                {PAGE_SIZE_FIELD, String.valueOf(pageSize)}
        })
                .collect(toMap(item -> item[0], item -> null == item[1] ? EMPTY : item[1]));
    }

    public String getEarnRuleId(String campaignId) {
        return getCampaignById(campaignId)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .path("Contents[0].Id");
    }
}
