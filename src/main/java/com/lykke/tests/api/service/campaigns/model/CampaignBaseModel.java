package com.lykke.tests.api.service.campaigns.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy.UpperCamelCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Builder
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(UpperCamelCaseStrategy.class)
public class CampaignBaseModel {

    private String name;
    private String description;
    private String reward;
    private RewardType rewardType;
    private String approximateAward;
    private String amountInTokens;
    private float amountInCurrency;
    private boolean usePartnerCurrencyRate;
    private String fromDate;
    private String toDate;
    private int completionCount;
    private int order;

    public String getRewardType() {
        return rewardType.getCode();
    }

    public RewardType getRewardTypeValue() {
        return rewardType;
    }
}
