package com.lykke.tests.api.service.customer;

import static com.lykke.api.testing.api.base.RequestHeader.getHeader;
import static com.lykke.api.testing.api.common.QueryParamsUtils.getQueryParams;
import static com.lykke.tests.api.base.Paths.Customer.EARN_RULES_API_PATH;
import static com.lykke.tests.api.base.Paths.Customer.EARN_RULE_BY_ID_API_PATH;
import static com.lykke.tests.api.base.Paths.Customer.EARN_RULE_BY_ID_STAKING_API_PATH;
import static com.lykke.tests.api.base.Paths.Customer.EARN_RULE_SEARCH_API_PATH;
import static com.lykke.tests.api.base.Paths.Customer.EARN_RULE_STAKING_API_PATH;
import static org.apache.http.HttpStatus.SC_OK;

import com.lykke.tests.api.service.customer.model.earnrule.EarnRuleExtendedModel;
import com.lykke.tests.api.service.customer.model.earnrule.EarnRuleStakingListModel;
import com.lykke.tests.api.service.customer.model.earnrule.EarnRulesListResponseModel;
import io.restassured.response.Response;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.UtilityClass;

@UtilityClass
public class EarnRulesUtils {

    public EarnRulesListResponseModel getEarnRules(String statuses, String token) {
        return getHeader(token)
                .queryParams("statuses", statuses)
                .queryParams("pageSize", 500)
                .queryParams("currentPage", 1)
                .get(EARN_RULES_API_PATH)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(EarnRulesListResponseModel.class);
    }

    @Deprecated
    public EarnRuleExtendedModel getEarnRulesById_Deprecated(String earnRuleId, String token) {
        return getHeader(token)
                .get(EARN_RULE_BY_ID_API_PATH.apply(earnRuleId))
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(EarnRuleExtendedModel.class);
    }

    public EarnRuleExtendedModel getEarnRulesById(String earnRuleId, String token) {
        return getHeader(token)
                .queryParams(getQueryParams(ByEarnRuleId
                        .builder()
                        .earnRuleId(earnRuleId)
                        .build()))
                .get(EARN_RULE_SEARCH_API_PATH)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(EarnRuleExtendedModel.class);
    }

    @Deprecated
    public EarnRuleStakingListModel getEarnRulesStakingById_Deprecated(String earnRuleId, String token) {
        return getHeader(token)
                .get(EARN_RULE_BY_ID_STAKING_API_PATH.apply(earnRuleId))
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(EarnRuleStakingListModel.class);
    }

    public EarnRuleStakingListModel getEarnRulesStakingById(String earnRuleId, String token) {
        return getHeader(token)
                .queryParams(getQueryParams(ByEarnRuleId
                        .builder()
                        .earnRuleId(earnRuleId)
                        .build()))
                .get(EARN_RULE_STAKING_API_PATH)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(EarnRuleStakingListModel.class);
    }

    @Deprecated
    public Response getEarnRuleById_Response_Deprecated(String earnRuleId, String token) {
        return getHeader(token)
                .get(EARN_RULE_BY_ID_API_PATH.apply(earnRuleId));
    }

    public Response getEarnRuleById_Response(String earnRuleId, String token) {
        return getHeader(token)
                .get(EARN_RULE_BY_ID_API_PATH.apply(earnRuleId));
    }

    @AllArgsConstructor
    @Builder
    @Data
    public static final class ByEarnRuleId {

        private String earnRuleId;
    }
}
