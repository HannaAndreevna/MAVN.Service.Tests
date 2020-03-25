package com.lykke.tests.api.service.admin.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy.UpperCamelCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.lykke.api.testing.annotations.PublicApi;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
@JsonNaming(UpperCamelCaseStrategy.class)
@JsonIgnoreProperties(ignoreUnknown = true)
@PublicApi
public class EarnRuleBaseModel {

    private String name;
    private String reward;
    private Double rewardDecimal;
    private String amountInTokens;
    private Double amountInCurrency;
    private boolean usePartnerCurrencyRate;
    private RewardType rewardType;
    private String fromDate;
    // TODO: Optional
    private String toDate;
    // TODO: Optional
    private int completionCount;
    @JsonProperty("IsEnabled")
    private boolean isEnabled;
    private String description;
    private String approximateAward;
    private int order;
}
