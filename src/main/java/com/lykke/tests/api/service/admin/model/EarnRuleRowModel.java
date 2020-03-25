package com.lykke.tests.api.service.admin.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy.UpperCamelCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.lykke.api.testing.annotations.PublicApi;
import com.lykke.tests.api.service.admin.model.bonustypes.Vertical;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonNaming(UpperCamelCaseStrategy.class)
@JsonIgnoreProperties(ignoreUnknown = true)
@PublicApi
public class EarnRuleRowModel extends EarnRuleBaseModel {

    private String id;
    private String asset;
    private EarnRuleStatus status;
    private Vertical vertical;

    @Builder(builderMethodName = "earnRuleRowModelBuilder")
    public EarnRuleRowModel(String name, String reward, Double rewardDecimal, String amountInTokens,
            Double amountInCurrency, boolean usePartnerCurrencyRate, RewardType rewardType, String fromDate,
            String toDate, int completionCount, boolean isEnabled, String description, String approximateAward,
            int order, String id, String asset, EarnRuleStatus status, Vertical vertical) {
        super(name, reward, rewardDecimal, amountInTokens, amountInCurrency, usePartnerCurrencyRate, rewardType,
                fromDate, toDate, completionCount, isEnabled, description, approximateAward, order);
        this.id = id;
        this.asset = asset;
        this.status = status;
        this.vertical = vertical;
    }
}
