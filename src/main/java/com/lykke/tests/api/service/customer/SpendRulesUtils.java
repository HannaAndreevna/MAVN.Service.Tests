package com.lykke.tests.api.service.customer;

import static com.lykke.api.testing.api.base.RequestHeader.getHeader;
import static com.lykke.api.testing.api.common.QueryParamsUtils.getQueryParams;
import static com.lykke.tests.api.base.Paths.Customer.SPEND_RULES_API_PATH;
import static com.lykke.tests.api.base.Paths.Customer.SPEND_RULES_SEARCH_BY_ID_API_PATH;
import static com.lykke.tests.api.base.Paths.Customer.SPEND_RULE_BY_ID_API_PATH;
import static org.apache.http.HttpStatus.SC_OK;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy.LowerCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.lykke.tests.api.service.customer.model.spendrule.SpendRuleDetailsModel;
import com.lykke.tests.api.service.customer.model.spendrule.SpendRuleListDetailsModel;
import io.restassured.response.Response;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.UtilityClass;

@UtilityClass
public class SpendRulesUtils {

    public SpendRuleListDetailsModel[] getSpendRules(String token) {
        return getHeader(token)
                .get(SPEND_RULES_API_PATH)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(SpendRuleListDetailsModel[].class);
    }

    @Deprecated
    public SpendRuleDetailsModel getSpendRuleById_Deprecated(String spendRuleId, String token) {
        return getHeader(token)
                .get(SPEND_RULE_BY_ID_API_PATH.apply(spendRuleId))
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(SpendRuleDetailsModel.class);
    }

    public SpendRuleDetailsModel getSpendRuleById(String spendRuleId, String token) {
        return getHeader(token)
                .queryParams(getQueryParams(BySpendRuleIdRequestModel
                        .builder()
                        .spendRuleId(spendRuleId)
                        .build()))
                .get(SPEND_RULES_SEARCH_BY_ID_API_PATH)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(SpendRuleDetailsModel.class);
    }

    @Deprecated
    public Response getSpendRuleById_Response_Deprecated(String spendRuleId, String token) {
        return getHeader(token)
                .get(SPEND_RULE_BY_ID_API_PATH.apply(spendRuleId));
    }

    public Response getSpendRuleById_Response(String spendRuleId, String token) {
        return getHeader(token)
                .queryParams(getQueryParams(BySpendRuleIdRequestModel
                        .builder()
                        .spendRuleId(spendRuleId)
                        .build()))
                .get(SPEND_RULES_SEARCH_BY_ID_API_PATH);
    }

    @AllArgsConstructor
    @Builder
    @Data
    @NoArgsConstructor
    @JsonNaming(LowerCaseStrategy.class)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class ValidationErrorResponse {

        private String error;
        private String message;
    }

    @AllArgsConstructor
    @Builder
    @Data
    public static final class BySpendRuleIdRequestModel {

        private String spendRuleId;
    }
}
