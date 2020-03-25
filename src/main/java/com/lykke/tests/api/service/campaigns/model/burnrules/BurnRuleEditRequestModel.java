package com.lykke.tests.api.service.campaigns.model.burnrules;

import com.lykke.api.testing.annotations.NetClassName;
import com.lykke.api.testing.annotations.PublicApi;
import lombok.Builder;
import lombok.Data;

@Data
@PublicApi
@NetClassName("BurnRuleEditRequest")
public class BurnRuleEditRequestModel extends BurnRuleBase {

    private String id;
    private BurnRuleContentEditRequestModel[] burnRuleContents;

    @Builder(builderMethodName = "burnRuleEditRequestBuilder")
    public BurnRuleEditRequestModel(String title, String description, String amountInTokens, float amountInCurrency,
            boolean usePartnerCurrencyRate, String[] partnerIds, Vertical vertical, Double price, int order, String id,
            BurnRuleContentEditRequestModel[] burnRuleContents) {
        super(title, description, amountInTokens, amountInCurrency, usePartnerCurrencyRate, partnerIds, vertical,
                price, order);
        this.id = id;
        this.burnRuleContents = burnRuleContents;
    }
}
