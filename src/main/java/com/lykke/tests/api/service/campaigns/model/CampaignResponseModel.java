package com.lykke.tests.api.service.campaigns.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy.UpperCamelCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.lykke.api.testing.annotations.NetClassName;
import com.lykke.api.testing.annotations.PublicApi;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
@JsonNaming(UpperCamelCaseStrategy.class)
@JsonIgnoreProperties(ignoreUnknown = true)
@PublicApi
@NetClassName("CampaignResponse")
public class CampaignResponseModel extends BonusEngineErrorResponseModel {

    private String id;
    private String name;
    private String description;
    private String reward;
    private RewardType rewardType;
    private String approximateAward;
    private String amountInTokens;
    private float amountInCurrency;
    private boolean usePartnerCurrencyRate;
    @EqualsAndHashCode.Exclude
    private Date fromDate;
    @JsonIgnore
    private Date toDate;
    private int completionCount;
    private boolean isEnabled;
    @EqualsAndHashCode.Exclude
    private Date creationDate;
    private String createdBy;
    private CampaignStatus campaignStatus;
    private int order;
    private ConditionCreateModel[] conditions;

    public void setIsEnabled(boolean value) {
        isEnabled = value;
    }
}
