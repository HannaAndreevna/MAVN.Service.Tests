package com.lykke.tests.api.service.campaigns.model.mobile;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy.UpperCamelCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.lykke.tests.api.service.campaigns.model.CampaignStatus;
import com.lykke.tests.api.service.campaigns.model.RewardType;
import lombok.Data;

@Data
@JsonNaming(UpperCamelCaseStrategy.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class EarnRuleLocalizedResponse {

    private String id;
    private String title;
    private CampaignStatus status;
    private String description;
    private String imageUrl;
    private String reward;
    private RewardType rewardType;
    private boolean isApproximate;
    private String approximateAward;
    private String amountInTokens;
    private long amountInCurrency;
    private boolean usePartnerCurrencyRate;
    private String fromDate;
    private String toDate;
    private int completionCount;
    private String creationDate;
    private String createdBy;
    private ConditionLocalizedResponse[] conditions;

    public EarnRuleLocalizedResponse() {
        conditions = new ConditionLocalizedResponse[]{};
    }
}
