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
@JsonNaming(UpperCamelCaseStrategy.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ConditionBaseModel {

    private String type;
    private String immediateReward;
    private int completionCount;
    private String[] partnerIds;
    private boolean hasStaking;
    private String stakeAmount;
    private int stakingPeriod;
    private int stakeWarningPeriod;
    private Double stakingRule;
    private Double burningRule;
    private RewardType rewardType;
    private String approximateAward;
    private String amountInTokens;
    private Double amountInCurrency;
    private boolean usePartnerCurrencyRate;
    private boolean rewardHasRatio;

    public ConditionBaseModel() {
        stakingPeriod = 10;
        stakeWarningPeriod = 2;
        stakingRule = 0.0;
    }

    public RewardType getRewardType() {
        return null == rewardType ? RewardType.FIXED : rewardType;
    }
}
