package com.lykke.tests.api.service.campaigns.model.burnrules;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
public class BurnRuleBase {

    private String title;
    private String description;
    private String amountInTokens;
    private float amountInCurrency;
    private boolean usePartnerCurrencyRate;
    private String[] partnerIds;
    private Vertical vertical;
    private Double price;
    private int order;

    public String getAmountInTokens() {
        return Double.valueOf(amountInTokens).toString();
    }
}
