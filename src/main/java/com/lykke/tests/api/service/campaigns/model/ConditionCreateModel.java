package com.lykke.tests.api.service.campaigns.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy.UpperCamelCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.lykke.api.testing.annotations.PublicApi;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonNaming(UpperCamelCaseStrategy.class)
@JsonIgnoreProperties(ignoreUnknown = true)
@PublicApi
public class ConditionCreateModel extends ConditionBaseModel {

    private RewardRatioAttribute rewardRatio;

    @Builder(builderMethodName = "conditionCreateBuilder")
    public ConditionCreateModel(String type, String immediateReward, int completionCount, String[] partnerIds,
            boolean hasStaking, String stakeAmount, int stakingPeriod, int stakeWarningPeriod, Double stakingRule,
            Double burningRule, RewardType rewardType, String approximateAward, String amountInTokens,
            Double amountInCurrency, boolean usePartnerCurrencyRate, boolean rewardHasRatio,
            RewardRatioAttribute rewardRatio) {
        super(type, immediateReward, completionCount, partnerIds, hasStaking, stakeAmount, stakingPeriod,
                stakeWarningPeriod, stakingRule, burningRule, rewardType, approximateAward, amountInTokens,
                amountInCurrency, usePartnerCurrencyRate, rewardHasRatio);
        this.rewardRatio = rewardRatio;
    }
}
