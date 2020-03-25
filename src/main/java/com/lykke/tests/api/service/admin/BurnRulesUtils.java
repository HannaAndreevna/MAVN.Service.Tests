package com.lykke.tests.api.service.admin;

import static com.lykke.api.testing.api.base.RequestHeader.getHeader;
import static com.lykke.tests.api.base.Paths.AdminApi.ADMIN_API_BURN_RULES_API_PATH;
import static com.lykke.tests.api.base.Paths.AdminApi.VOUCHERS_API_PATH;
import static java.util.stream.Collectors.toMap;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.http.HttpStatus.SC_OK;

import com.lykke.api.testing.api.common.QueryParamsUtils;
import com.lykke.tests.api.base.PathConsts.AdminApiService;
import com.lykke.tests.api.service.admin.model.burnrules.BurnRuleCreateRequest;
import com.lykke.tests.api.service.admin.model.burnrules.BurnRuleCreateRequestModel;
import com.lykke.tests.api.service.admin.model.burnrules.BurnRuleCreatedResponse;
import com.lykke.tests.api.service.admin.model.burnrules.BurnRuleListRequest;
import com.lykke.tests.api.service.admin.model.burnrules.BurnRuleModel;
import com.lykke.tests.api.service.admin.model.burnrules.BurnRuleUpdateRequest;
import com.lykke.tests.api.service.admin.model.burnrules.BurnRulesListResponse;
import com.lykke.tests.api.service.admin.model.burnrules.ValidationErrorResponse;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import java.io.File;
import java.net.URLConnection;
import java.util.Map;
import java.util.stream.Stream;
import lombok.experimental.UtilityClass;

@UtilityClass
public class BurnRulesUtils {

    public static final String BURN_RULE_ID_FIELD = "Id";
    public static final String TITLE_FIELD = "Title";
    private static final String CURRENT_PAGE_FIELD = "CurrentPage";
    private static final String PAGE_SIZE_FIELD = "PageSize";
    private static final String RULE_CONTENT_ID_FIELD = "RuleContentId";
    private static final String ID_FIELD = "Id";

    @Step("Get Burn rules")
    public BurnRulesListResponse getBurnRules(String title, int currentPage, int pageSize, String token) {
        return getHeader(token)
                .queryParams(QueryParamsUtils.getQueryParams(BurnRuleListRequest
                        .burnRuleListRequestBuilder()
                        .title(title)
                        .currentPage(currentPage)
                        .pageSize(pageSize)
                        .build()))
                .get(ADMIN_API_BURN_RULES_API_PATH)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(BurnRulesListResponse.class);
    }

    @Step("Get Burn rule by id {burnRuleId}")
    public BurnRuleModel getBurnRuleById(String burnRuleId, String token) {
        return getBurnRuleById_Response(burnRuleId, token)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(BurnRuleModel.class);
    }

    @Deprecated
    public Response getBurnRuleById_Response(String burnRuleId, String token) {
        return getHeader(token)
                .get(ADMIN_API_BURN_RULES_API_PATH
                        + AdminApiService.BY_BURN_RULE_ID_PATH.getFilledInPath(burnRuleId));
    }

    @Step("Create Burn rule")
    public BurnRuleCreatedResponse createBurnRule(BurnRuleCreateRequestModel burnRuleCreateRequestModel, String token) {
        return getHeader(token)
                .body(burnRuleCreateRequestModel)
                .post(ADMIN_API_BURN_RULES_API_PATH)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(BurnRuleCreatedResponse.class);
    }

    @Step("Get Burn rule id")
    public String getBurnRuleId(BurnRuleCreateRequestModel burnRuleCreateRequestModel, String token) {
        return getHeader(token)
                .body(burnRuleCreateRequestModel)
                .post(ADMIN_API_BURN_RULES_API_PATH)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .path(BURN_RULE_ID_FIELD);
    }

    public ValidationErrorResponse createBurnRule(BurnRuleCreateRequest burnRuleCreateRequest, String token) {
        return createBurnRule_response(burnRuleCreateRequest, token)
                .then()
                .assertThat()
                .statusCode(burnRuleCreateRequest.getHttpStatus())
                .extract()
                .as(ValidationErrorResponse.class);
    }

    public Response createBurnRule_response(BurnRuleCreateRequest burnRuleCreateRequest, String token) {
        return getHeader(token)
                .body(burnRuleCreateRequest)
                .post(ADMIN_API_BURN_RULES_API_PATH);
    }

    @Step("Update Burn rule")
    public Response updateBurnRule(BurnRuleUpdateRequest burnRuleUpdateRequest, String token) {
        return getHeader(token)
                .body(burnRuleUpdateRequest)
                .put(ADMIN_API_BURN_RULES_API_PATH);

    }

    @Step("Add Image to Burn Rule")
    public Response addAnImage(String ruleContentId, String path, String token) {
        return getHeader(token)
                .contentType("multipart/form-data")
                .multiPart("formFile", new File(path),
                        URLConnection.guessContentTypeFromName(new File(path).getName()))
                .queryParams(addRuleContentQueryParams(ruleContentId))
                .post(ADMIN_API_BURN_RULES_API_PATH + AdminApiService.IMAGE_PATH.getPath());
    }

    @Step("Update Burn rule image")
    public Response updateImage(String id, String ruleContentId, String path, String token) {
        return getHeader(token)
                .contentType("multipart/form-data")
                .multiPart("formFile", new File(path),
                        URLConnection.guessContentTypeFromName(new File(path).getName()))
                .queryParams(updateRuleContentQueryParams(id, ruleContentId))
                .put(ADMIN_API_BURN_RULES_API_PATH + AdminApiService.IMAGE_PATH.getPath());
    }

    @Deprecated
    @Step("Delete Burn Rule")
    public Response deleteBurnRule(String burnRuleId, String token) {
        return getHeader(token)
                .delete(ADMIN_API_BURN_RULES_API_PATH
                        + AdminApiService.BY_BURN_RULE_ID_PATH.getFilledInPath(burnRuleId));
    }

    public Response postVouchers(String spendRuleId, String fileName, String filePath, String token) {
        return getHeader(token)
                .queryParam("spendRuleId", spendRuleId)
                .contentType("multipart/form-data")
                .multiPart("formFile", new File(filePath), "type=text/csv")
                .post(VOUCHERS_API_PATH)
                .thenReturn();
    }

    private Map<String, String> addRuleContentQueryParams(String ruleContentId) {
        return Stream.of(new String[][]{
                {RULE_CONTENT_ID_FIELD, ruleContentId},
        })
                .collect(toMap(item -> item[0], item -> null == item[1] ? EMPTY : item[1]));
    }

    private Map<String, String> updateRuleContentQueryParams(String id, String ruleContentId) {
        return Stream.of(new String[][]{
                {ID_FIELD, id},
                {RULE_CONTENT_ID_FIELD, ruleContentId},
        })
                .collect(toMap(item -> item[0], item -> null == item[1] ? EMPTY : item[1]));
    }
}
