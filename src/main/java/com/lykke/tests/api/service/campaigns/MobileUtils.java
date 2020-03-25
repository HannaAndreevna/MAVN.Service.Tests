package com.lykke.tests.api.service.campaigns;

import static com.lykke.api.testing.api.base.RequestHeader.getHeader;
import static com.lykke.tests.api.base.PathConsts.CampaignsEndpoint.BY_ID;
import static com.lykke.tests.api.base.Paths.Campaigns.MOBILE_BURN_RULES_API_PATH;
import static com.lykke.tests.api.base.Paths.Campaigns.MOBILE_BURN_RULES_BY_ID_API_PATH;
import static com.lykke.tests.api.base.Paths.Campaigns.MOBILE_EARN_RULES_API_PATH;
import static org.apache.http.HttpStatus.SC_OK;

import com.lykke.tests.api.common.enums.Localization;
import com.lykke.tests.api.service.campaigns.model.CampaignStatus;
import com.lykke.tests.api.service.campaigns.model.mobile.BurnRuleLocalizedResponse;
import com.lykke.tests.api.service.campaigns.model.mobile.EarnRuleLocalizedResponse;
import com.lykke.tests.api.service.campaigns.model.mobile.EarnRulePaginatedResponseModel;
import com.lykke.tests.api.service.campaigns.model.mobile.MobileGetByIdRequest;
import com.lykke.tests.api.service.campaigns.model.mobile.ValidationResponse;
import com.lykke.tests.exceptions.RuleNotFoundException;
import io.restassured.response.Response;
import java.util.Arrays;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import lombok.val;

@UtilityClass
public class MobileUtils {

    public static final String MOBILE_BURN_RULE_WITH_ID_S_IS_NOT_FOUND_ERROR_MESSAGE = "Mobile burn rule with id=%s is not found";

    public BurnRuleLocalizedResponse[] getMobileBurnRules(Localization language) {
        return getHeader()
                .queryParams("language", language)
                .get(MOBILE_BURN_RULES_API_PATH)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(BurnRuleLocalizedResponse[].class);
    }

    @SneakyThrows
    public BurnRuleLocalizedResponse getMobileBurnRuleWithId(Localization language, String ruleId) {
        val rules = getHeader()
                .queryParams("language", language)
                .get(MOBILE_BURN_RULES_API_PATH)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(BurnRuleLocalizedResponse[].class);
        return Arrays.stream(rules)
                .filter(rule -> ruleId.equalsIgnoreCase(rule.getId()))
                .findFirst()
                .orElseThrow(
                        () -> new RuleNotFoundException(MOBILE_BURN_RULE_WITH_ID_S_IS_NOT_FOUND_ERROR_MESSAGE, ruleId));
    }

    public Response getMobileBurnRulesResp(String language) {
        return getHeader()
                .queryParams("language", language)
                .get(MOBILE_BURN_RULES_API_PATH);
    }

    public BurnRuleLocalizedResponse[] getMobileBurnRules() {
        return getHeader()
                .get(MOBILE_BURN_RULES_API_PATH)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(BurnRuleLocalizedResponse[].class);
    }

    @SneakyThrows
    public BurnRuleLocalizedResponse getMobileBurnRuleWithId(String ruleId, boolean includeDeleted) {
        return getHeader()
                .queryParam("includeDeleted", includeDeleted)
                .get(MOBILE_BURN_RULES_BY_ID_API_PATH.apply(ruleId))
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(BurnRuleLocalizedResponse.class);
    }

    @SneakyThrows
    public BurnRuleLocalizedResponse getMobileBurnRuleWithId(String ruleId) {
        val rules = getHeader()
                .get(MOBILE_BURN_RULES_API_PATH)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(BurnRuleLocalizedResponse[].class);
        return Arrays.stream(rules)
                .filter(rule -> ruleId.equalsIgnoreCase(rule.getId()))
                .findFirst()
                .orElseThrow(
                        () -> new RuleNotFoundException(MOBILE_BURN_RULE_WITH_ID_S_IS_NOT_FOUND_ERROR_MESSAGE, ruleId));
    }

    public BurnRuleLocalizedResponse getMobileBurnRuleById(String burnRuleId) {
        return getHeader()
                .get(MOBILE_BURN_RULES_API_PATH + BY_ID.getFilledInPath(burnRuleId))
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(BurnRuleLocalizedResponse.class);
    }

    public BurnRuleLocalizedResponse getMobileBurnRuleById(String burnRuleId, Localization language) {
        return getHeader()
                .queryParams("language", language)
                .get(MOBILE_BURN_RULES_API_PATH + BY_ID.getFilledInPath(burnRuleId))
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(BurnRuleLocalizedResponse.class);
    }

    public Response getMobileBurnRulesByIdResp(String burnRuleId, String language) {
        return getHeader()
                .queryParams("language", language)
                .get(MOBILE_BURN_RULES_API_PATH + BY_ID.getFilledInPath(burnRuleId));
    }

    public ValidationResponse getMobileBurnRuleByIdValidationResponse(MobileGetByIdRequest request) {
        return getHeader()
                .get(MOBILE_BURN_RULES_API_PATH + BY_ID.getFilledInPath(request.getBurnRuleId()))
                .then()
                .assertThat()
                .statusCode(request.getHttpStatus())
                .extract()
                .as(ValidationResponse.class);
    }

    public EarnRulePaginatedResponseModel getMobileEarnRules(Localization language, CampaignStatus statuses) {
        return getHeader()
                .queryParams("language", language)
                .queryParams("statuses", statuses)
                .get(MOBILE_EARN_RULES_API_PATH)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(EarnRulePaginatedResponseModel.class);
    }

    public EarnRulePaginatedResponseModel getMobileEarnRules(CampaignStatus statuses) {
        return getHeader()
                .queryParams("statuses", statuses)
                .get(MOBILE_EARN_RULES_API_PATH)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(EarnRulePaginatedResponseModel.class);
    }

    public EarnRuleLocalizedResponse getMobilEarnRuleById(String earnRuleId) {
        return getHeader()
                .get(MOBILE_EARN_RULES_API_PATH + BY_ID.getFilledInPath(earnRuleId))
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(EarnRuleLocalizedResponse.class);
    }


    public EarnRuleLocalizedResponse getMobilEarnRuleById(String earnRuleId, Localization language) {
        return getHeader()
                .queryParams("language", language)
                .get(MOBILE_EARN_RULES_API_PATH + BY_ID.getFilledInPath(earnRuleId))
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(EarnRuleLocalizedResponse.class);
    }

    public Response getMobileEarnRulesResp(String language, CampaignStatus statuses) {
        return getHeader()
                .queryParams("language", language)
                .queryParams("statuses", statuses)
                .get(MOBILE_EARN_RULES_API_PATH);
    }

    public Response getMobileEarnRulesByIdResp(String earnRuleId, String language) {
        return getHeader()
                .queryParams("language", language)
                .get(MOBILE_EARN_RULES_API_PATH + BY_ID.getFilledInPath(earnRuleId));
    }

    public ValidationResponse getMobileEarnRuleByIdValidationResponse(MobileGetByIdRequest request) {
        return getHeader()
                .get(MOBILE_EARN_RULES_API_PATH + BY_ID.getFilledInPath(request.getBurnRuleId()))
                .then()
                .assertThat()
                .statusCode(request.getHttpStatus())
                .extract()
                .as(ValidationResponse.class);
    }
}
