package com.lykke.tests.api.service.campaigns;

import static com.lykke.api.testing.api.base.RequestHeader.getHeader;
import static com.lykke.api.testing.api.common.QueryParamsUtils.getQueryParams;
import static com.lykke.tests.api.base.PathConsts.CampaignsEndpoint.IMAGE_PATH;
import static com.lykke.tests.api.base.Paths.Campaigns.BURN_RULES_API_PATH;
import static com.lykke.tests.api.base.Paths.Campaigns.BURN_RULE_BY_ID_API_PATH;
import static java.util.stream.Collectors.toMap;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.http.HttpStatus.SC_OK;

import com.lykke.api.testing.annotations.QueryParameters;
import com.lykke.tests.api.service.campaigns.model.burnrules.BurnRuleContentEditRequestModel;
import com.lykke.tests.api.service.campaigns.model.burnrules.BurnRuleCreateRequestModel;
import com.lykke.tests.api.service.campaigns.model.burnrules.BurnRuleEditRequestModel;
import com.lykke.tests.api.service.campaigns.model.burnrules.BurnRuleResponseModel;
import com.lykke.tests.api.service.campaigns.model.burnrules.BurnRulesCreateRequest;
import com.lykke.tests.api.service.campaigns.model.burnrules.BurnRulesEditRequest;
import com.lykke.tests.api.service.campaigns.model.burnrules.FileCreateRequestModel;
import com.lykke.tests.api.service.campaigns.model.burnrules.FileEditRequestModel;
import com.lykke.tests.api.service.campaigns.model.burnrules.Localization;
import com.lykke.tests.api.service.campaigns.model.burnrules.PaginatedBurnRuleListResponseModel;
import com.lykke.tests.api.service.campaigns.model.burnrules.RuleContentType;
import com.lykke.tests.api.service.campaigns.model.burnrules.ValidationErrorBurnRuleResponse;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import java.util.Map;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.UtilityClass;

@UtilityClass
public class BurnRulesUtils {

    public static final String BURN_RULE_ID_FIELD = "BurnRuleId";
    public static final String TITLE_FIELD = "Title";
    public static final String DESCRIPTION_FIELD = "Description";
    public static final String CREATED_BY_FIELD = "CreatedBy";
    public static final String BURN_RULE_CONTENTS_FIELD = "BurnRuleContents";
    private static final String CURRENT_PAGE_FIELD = "CurrentPage";
    private static final String PAGE_SIZE_FIELD = "PageSize";
    private static final String BURN_RULE_EDIT_ID_FIELD = "Id";
    private static final String BURN_RULE_CONTENT_ID_FIELD = "Id";
    private static final String RULE_CONTENT_ID_FIELD = "RuleContentId";
    private static final String NAME_FIELD = "Name";
    private static final String TYPE_FIELD = "Type";
    private static final String CONTENT_FIELD = "Content";
    private static final String ID_FIELD = "Id";
    private static final String AMOUNT_IN_TOKENS_FIELD = "AmountInTokens";
    private static final String AMOUNT_IN_CURRENCY_FIELD = "AmountInCurrency";

    public static String getBurnRuleId(BurnRuleCreateRequestModel burnRuleCreateRequest) {
        return createBurnRule(burnRuleCreateRequest)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .path(BURN_RULE_ID_FIELD);
    }

    public static Response addBurnRuleImage(FileCreateRequestModel fileCreateRequestModel) {
        return getHeader()
                .body(fileUploadObject(fileCreateRequestModel))
                .post(BURN_RULES_API_PATH + IMAGE_PATH.getPath());

    }

    public static String getBurnRuleContentIdByContentType(String burnRuleId, RuleContentType contentType,
            Localization localization) {
        String contentId = null;
        BurnRuleResponseModel burnRule = getBurnRuleById(burnRuleId);
        for (int i = 0; i < burnRule.getBurnRuleContents().length; i++) {
            if (burnRule.getBurnRuleContents()[i].getRuleContentType().equalsIgnoreCase(contentType.getCode())
                    && burnRule.getBurnRuleContents()[i].getLocalization().equalsIgnoreCase(localization.getCode())) {
                contentId = burnRule.getBurnRuleContents()[i].getId();
            }
        }

        return contentId;
    }

    public BurnRuleResponseModel getBurnRuleById(String burnRuleId) {
        return getHeader()
                .get(BURN_RULE_BY_ID_API_PATH.apply(burnRuleId))
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(BurnRuleResponseModel.class);
    }

    @Step("Create Burn Rule")
    public Response createBurnRule(BurnRuleCreateRequestModel burnRuleCreateRequest) {
        return getHeader()
                .body(burnRuleCreateRequest)
                .post(BURN_RULES_API_PATH);
    }

    public ValidationErrorBurnRuleResponse createBurnRule(BurnRulesCreateRequest burnRulesCreateRequest) {
        return getHeader()
                .body(burnRulesCreateRequest)
                .post(BURN_RULES_API_PATH)
                .then()
                .assertThat()
                .statusCode(burnRulesCreateRequest.getHttpStatus())
                .extract()
                .as(ValidationErrorBurnRuleResponse.class);
    }

    public ValidationErrorBurnRuleResponse editBurnRuleErrorResponse(BurnRulesEditRequest burnRulesEditRequest) {
        return getHeader()
                .body(burnRulesEditRequest)
                .put(BURN_RULES_API_PATH)
                .then()
                .assertThat()
                .statusCode(burnRulesEditRequest.getHttpStatus())
                .extract()
                .as(ValidationErrorBurnRuleResponse.class);
    }

    public Response editBurnRule(BurnRuleEditRequestModel burnRuleEditRequestModel,
            BurnRuleContentEditRequestModel... burnRuleContentEditRequestModel) {
        burnRuleEditRequestModel
                .setBurnRuleContents(burnRuleContentEditRequestModel);
        return getHeader()
                .body(burnRuleEditRequestModel)
                .put(BURN_RULES_API_PATH);
    }

    public PaginatedBurnRuleListResponseModel getPaginatedBurnRulesList(String title, int currentPage, int pageSize) {
        return getHeader()
                .queryParams(getQueryParams(Parameters
                        .builder()
                        .title(title)
                        .currentPage(currentPage)
                        .pageSize(pageSize)
                        .build()))
                .get(BURN_RULES_API_PATH)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(PaginatedBurnRuleListResponseModel.class);
    }

    public Response editBurnRuleImage(FileEditRequestModel fileEditRequestModel) {
        return getHeader()
                .body(fileEditObject(fileEditRequestModel))
                .put(BURN_RULES_API_PATH + IMAGE_PATH.getPath());

    }

    public Response deleteBurnRuleById(String burnRuleId) {
        return getHeader()
                .delete(BURN_RULE_BY_ID_API_PATH.apply(burnRuleId));
    }

    public String getBurnRuleContentId(String burnRuleId) {
        return getBurnRuleById(burnRuleId).getBurnRuleContents()[0].getId();
    }

    public String getBurnRuleContentId(String burnRuleId, int index) {
        return getBurnRuleById(burnRuleId).getBurnRuleContents()[index].getId();
    }

    private BurnRuleContentEditRequestModel[] burnRuleEditContentObject(
            BurnRuleContentEditRequestModel burnRuleContentEditRequestModel) {
        return new BurnRuleContentEditRequestModel[]{
                BurnRuleContentEditRequestModel
                        .builder()
                        .id(burnRuleContentEditRequestModel.getId())
                        .ruleContentType(burnRuleContentEditRequestModel.getRuleContentType())
                        .localization(burnRuleContentEditRequestModel.getLocalization())
                        .value(burnRuleContentEditRequestModel.getValue())
                        .build()
        };
    }

    private Map<String, String> fileUploadObject(FileCreateRequestModel fileCreateRequestModel) {
        return Stream.of(new String[][]{
                {RULE_CONTENT_ID_FIELD, fileCreateRequestModel.getRuleContentId()},
                {NAME_FIELD, fileCreateRequestModel.getName()},
                {TYPE_FIELD, fileCreateRequestModel.getType()},
                {CONTENT_FIELD, fileCreateRequestModel.getContent()}
        })
                .collect(toMap(item -> item[0], item -> null == item[1] ? EMPTY : item[1]));
    }

    private Map<String, String> fileEditObject(FileEditRequestModel fileEditRequestModel) {
        return Stream.of(new String[][]{
                {ID_FIELD, fileEditRequestModel.getId()},
                {RULE_CONTENT_ID_FIELD, fileEditRequestModel.getRuleContentId()},
                {NAME_FIELD, fileEditRequestModel.getName()},
                {TYPE_FIELD, fileEditRequestModel.getType()},
                {CONTENT_FIELD, fileEditRequestModel.getContent()}
        })
                .collect(toMap(item -> item[0], item -> null == item[1] ? EMPTY : item[1]));
    }

    @AllArgsConstructor
    @Builder
    @Data
    @QueryParameters
    private static class Parameters {

        private String title;
        private int currentPage;
        private int pageSize;
    }
}
