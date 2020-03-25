package com.lykke.tests.api.service.eligibilityengine;

import static com.lykke.api.testing.api.base.RequestHeader.getHeader;
import static com.lykke.tests.api.base.Paths.EligibilityServices.CONDITION_AMOUNT_API_PATH;
import static com.lykke.tests.api.base.Paths.EligibilityServices.CONDITION_RATE_API_PATH;
import static com.lykke.tests.api.base.Paths.EligibilityServices.EARN_RULE_AMOUNT_API_PATH;
import static com.lykke.tests.api.base.Paths.EligibilityServices.EARN_RULE_RATE_API_PATH;
import static com.lykke.tests.api.base.Paths.EligibilityServices.PARTNER_AMOUNT_API_PATH;
import static com.lykke.tests.api.base.Paths.EligibilityServices.PARTNER_RATE_API_PATH;
import static com.lykke.tests.api.base.Paths.EligibilityServices.SPEND_RULE_AMOUNT_API_PATH;
import static com.lykke.tests.api.base.Paths.EligibilityServices.SPEND_RULE_RATE_API_PATH;

import com.lykke.tests.api.service.eligibilityengine.model.ConvertAmountByConditionRequest;
import com.lykke.tests.api.service.eligibilityengine.model.ConvertAmountByEarnRuleRequest;
import com.lykke.tests.api.service.eligibilityengine.model.ConvertAmountBySpendRuleRequest;
import com.lykke.tests.api.service.eligibilityengine.model.ConvertOptimalByPartnerRequest;
import com.lykke.tests.api.service.eligibilityengine.model.CurrencyRateByConditionRequest;
import com.lykke.tests.api.service.eligibilityengine.model.CurrencyRateByEarnRuleRequest;
import com.lykke.tests.api.service.eligibilityengine.model.CurrencyRateBySpendRuleRequest;
import com.lykke.tests.api.service.eligibilityengine.model.OptimalCurrencyRateByPartnerRequest;
import io.restassured.response.Response;
import lombok.experimental.UtilityClass;

@UtilityClass
public class EligibilityEngineUtils {

    public Response getPartnerRate(OptimalCurrencyRateByPartnerRequest requestModel) {
        return getHeader()
                .body(requestModel)
                .post(PARTNER_RATE_API_PATH)
                .thenReturn();
    }

    public Response getEarnRuleRate(CurrencyRateByEarnRuleRequest requestModel) {
        return getHeader()
                .body(requestModel)
                .post(EARN_RULE_RATE_API_PATH)
                .thenReturn();
    }

    public Response getSpendRuleRate(CurrencyRateBySpendRuleRequest requestModel) {
        return getHeader()
                .body(requestModel)
                .post(SPEND_RULE_RATE_API_PATH)
                .thenReturn();
    }

    public Response getPartnerAmount(ConvertOptimalByPartnerRequest requestModel) {
        return getHeader()
                .body(requestModel)
                .post(PARTNER_AMOUNT_API_PATH)
                .thenReturn();
    }

    public Response getEarnRuleAmount(ConvertAmountByEarnRuleRequest requestModel) {
        return getHeader()
                .body(requestModel)
                .post(EARN_RULE_AMOUNT_API_PATH)
                .thenReturn();
    }

    public Response getSpendRuleAmount(ConvertAmountBySpendRuleRequest requestModel) {
        return getHeader()
                .body(requestModel)
                .post(SPEND_RULE_AMOUNT_API_PATH)
                .thenReturn();
    }

    public Response getConditionAmount(ConvertAmountByConditionRequest requestModel) {
        return getHeader()
                .body(requestModel)
                .post(CONDITION_AMOUNT_API_PATH)
                .thenReturn();
    }

    public Response getConditionRate(CurrencyRateByConditionRequest requestModel) {
        return getHeader()
                .body(requestModel)
                .post(CONDITION_RATE_API_PATH)
                .thenReturn();
    }
}
