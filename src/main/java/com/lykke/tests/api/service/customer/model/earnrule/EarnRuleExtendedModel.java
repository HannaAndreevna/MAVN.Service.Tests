package com.lykke.tests.api.service.customer.model.earnrule;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy.UpperCamelCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.lykke.api.testing.annotations.PublicApi;
import lombok.Data;

@Data
@JsonNaming(UpperCamelCaseStrategy.class)
@JsonIgnoreProperties(ignoreUnknown = true)
@PublicApi
public class EarnRuleExtendedModel extends EarnRuleBaseModel {

    private String amountInTokens;
    private float amountInCurrency;
    private int customerCompletionCount;
    private ConditionExtendedModel[] conditions;
}
