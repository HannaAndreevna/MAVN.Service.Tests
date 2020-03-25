package com.lykke.tests.api.service.campaigns.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy.UpperCamelCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.lykke.api.testing.annotations.PublicApi;
import com.lykke.tests.api.service.campaigns.CampaignServiceErrorResponseModel;
import java.util.Date;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonNaming(UpperCamelCaseStrategy.class)
@JsonIgnoreProperties(ignoreUnknown = true)
@PublicApi
public class CampaignDetailResponseModel extends CampaignServiceErrorResponseModel {

    private String id;
    private String name;
    private String description;
    private String reward;
    private RewardType rewardType;
    private String amountInTokens;
    private String approximateAward;
    private Double amountInCurrency;
    private boolean usePartnerCurrencyRate;
    private String fromDate;
    private String toDate;
    private int completionCount;
    private boolean isEnabled;
    private Date creationDate;
    private String createdBy;
    private CampaignStatus campaignStatus;
    private int order;
    private ConditionModel[] conditions;
    private EarnRuleContentResponse[] contents;

    @Builder(builderMethodName = "campaignDetailResponseBuilder")
    public CampaignDetailResponseModel(CampaignServiceErrorCode errorCode, String errorMessage, String name,
            String reward, String amountInTokens, String approximateAward, Double amountInCurrency,
            RewardType rewardType, String fromDate, String toDate, String description, String id, String createdBy,
            Date creationDate, int completionCount, boolean isEnabled, CampaignStatus campaignStatus,
            ConditionModel[] conditions, EarnRuleContentResponse[] contents) {
        super(errorCode, errorMessage);
        this.name = name;
        this.reward = reward;
        this.amountInTokens = amountInTokens;
        this.approximateAward = approximateAward;
        this.amountInCurrency = amountInCurrency;
        this.rewardType = rewardType;
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.description = description;
        this.id = id;
        this.createdBy = createdBy;
        this.creationDate = creationDate;
        this.completionCount = completionCount;
        this.isEnabled = isEnabled;
        this.campaignStatus = campaignStatus;
        this.conditions = conditions;
        this.contents = contents;
    }

    public boolean getIsEnabled() {
        return isEnabled;
    }

    public void setIsEnabled(boolean isEnabled) {
        this.isEnabled = isEnabled;
    }
}
