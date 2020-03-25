package com.lykke.tests.api.service.campaigns.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy.UpperCamelCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.lykke.api.testing.annotations.NetClassName;
import com.lykke.api.testing.annotations.PublicApi;
import lombok.Builder;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(UpperCamelCaseStrategy.class)
@PublicApi
@NetClassName("CampaignCreateModel")
public class Campaign extends CampaignBaseModel {

    private String createdBy;
    private ConditionCreateModel[] conditions;
    private EarnRule[] contents;

    @Builder(builderMethodName = "campaignBuilder")
    public Campaign(String name, String description, String reward, RewardType rewardType, String approximateAward,
            String amountInTokens, float amountInCurrency, boolean usePartnerCurrencyRate, String fromDate,
            String toDate, int completionCount, int order, String createdBy, ConditionCreateModel[] conditions,
            EarnRule[] contents) {
        super(name, description, reward, rewardType, approximateAward, amountInTokens, amountInCurrency,
                usePartnerCurrencyRate, fromDate, toDate, completionCount, order);
        this.createdBy = createdBy;
        this.conditions = conditions;
        this.contents = contents;
    }
}
