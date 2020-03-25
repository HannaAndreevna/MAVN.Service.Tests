package com.lykke.tests.api.service.campaigns.model;

import com.lykke.api.testing.annotations.PublicApi;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@PublicApi
public class ConditionEditModel extends ConditionBaseModel {

    private String id;

    @Builder(builderMethodName = "conditionEditBuilder")
    public ConditionEditModel(String type, String immediateReward, int completionCount, String[] partnerIds,
            boolean hasStaking, String stakeAmount, int stakingPeriod, int stakeWarningPeriod, Double stakingRule,
            Double burningRule, RewardType rewardType, String approximateAward, String amountInTokens,
            Double amountInCurrency, boolean usePartnerCurrencyRate, boolean rewardHasRatio, String id) {
        super(type, immediateReward, completionCount, partnerIds, hasStaking, stakeAmount, stakingPeriod,
                stakeWarningPeriod, stakingRule, burningRule, rewardType, approximateAward, amountInTokens,
                amountInCurrency, usePartnerCurrencyRate, rewardHasRatio);
        this.id = id;
    }
}
