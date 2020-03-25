package com.lykke.tests.api.service.campaigns.model.burnrules;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy.UpperCamelCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.lykke.api.testing.annotations.NetClassName;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonNaming(UpperCamelCaseStrategy.class)
@JsonIgnoreProperties(ignoreUnknown = true)
@NetClassName("BurnRuleResponse")
public class BurnRuleResponseModel extends BurnRuleBase {

    private String id;
    private String errorCode;
    private String errorMessage;
    private BurnRuleContentResponseModel[] burnRuleContents;

    @Builder(builderMethodName = "burnRuleCreateRequestBuilder")
    public BurnRuleResponseModel(String title, String description, String amountInTokens, float amountInCurrency,
            boolean usePartnerCurrencyRate, String[] partnerIds, Vertical vertical, Double price, int order, String id,
            String errorCode, String errorMessage, BurnRuleContentResponseModel[] burnRuleContents) {
        super(title, description, amountInTokens, amountInCurrency, usePartnerCurrencyRate, partnerIds, vertical,
                price, order);
        this.id = id;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.burnRuleContents = burnRuleContents;
    }
}
