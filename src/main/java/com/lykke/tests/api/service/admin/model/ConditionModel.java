package com.lykke.tests.api.service.admin.model;

import com.lykke.api.testing.annotations.PublicApi;
import com.lykke.tests.api.service.admin.model.earnrules.ConditionBaseModel;
import lombok.Builder;
import lombok.Data;

@Data
@PublicApi
public class ConditionModel extends ConditionBaseModel {

    private String id;
    private String displayName;

    @Builder(builderMethodName = "conditionBuilder")
    public ConditionModel(String type, int completionCount, String immediateReward, String partnerId,
            boolean hasStaking, String stakeAmount, int stakingPeriod, int stakeWarningPeriod, Double stakingRule,
            Double burningRule, RewardType rewardType, String amountInTokens, Double amountInCurrency,
            boolean usePartnerCurrencyRate, boolean rewardHasRatio, String approximateAward, String id,
            String displayName) {
        super(type, completionCount, immediateReward, partnerId, hasStaking, stakeAmount, stakingPeriod,
                stakeWarningPeriod, stakingRule, burningRule, rewardType, amountInTokens, amountInCurrency,
                usePartnerCurrencyRate, rewardHasRatio, approximateAward);
        this.id = id;
        this.displayName = displayName;
    }
}
