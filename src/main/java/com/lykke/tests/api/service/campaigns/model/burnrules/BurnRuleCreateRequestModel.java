package com.lykke.tests.api.service.campaigns.model.burnrules;

import com.fasterxml.jackson.databind.PropertyNamingStrategy.UpperCamelCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.lykke.api.testing.annotations.NetClassName;
import com.lykke.api.testing.annotations.PublicApi;
import lombok.Builder;
import lombok.Data;

@Data
@JsonNaming(UpperCamelCaseStrategy.class)
@NetClassName("BurnRuleCreateRequest")
@PublicApi
public class BurnRuleCreateRequestModel extends BurnRuleBase {

    private String createdBy;
    private BurnRuleContentCreateRequestModel[] burnRuleContents;

    @Builder(builderMethodName = "burnRuleCreateRequestBuilder")
    public BurnRuleCreateRequestModel(String title, String description, String amountInTokens, float amountInCurrency,
            boolean usePartnerCurrencyRate, String[] partnerIds, Vertical vertical, Double price, int order,
            String createdBy, BurnRuleContentCreateRequestModel[] burnRuleContents) {
        super(title, description, amountInTokens, amountInCurrency, usePartnerCurrencyRate, partnerIds, vertical, price,
                order);
        this.createdBy = createdBy;
        this.burnRuleContents = burnRuleContents;
    }
}
