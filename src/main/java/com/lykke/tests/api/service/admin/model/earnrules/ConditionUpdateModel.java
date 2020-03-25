package com.lykke.tests.api.service.admin.model.earnrules;

import com.lykke.api.testing.annotations.PublicApi;
import com.lykke.tests.api.service.admin.model.RewardType;
import com.lykke.tests.api.service.campaigns.model.RewardRatioAttribute;
import lombok.Builder;
import lombok.Data;

@Data
@PublicApi
public class ConditionUpdateModel extends ConditionBaseModel {

    private String id;
    // using Lykke.Service.Campaign.Client.Models.Condition;
    private RewardRatioAttribute rewardRatio;

    @Builder(builderMethodName = "conditionUpdateBuilder")
    public ConditionUpdateModel(String type, int completionCount, String immediateReward, String partnerId,
            boolean hasStaking, String stakeAmount, int stakingPeriod, int stakeWarningPeriod, Double stakingRule,
            Double burningRule, RewardType rewardType, String amountInTokens, Double amountInCurrency,
            boolean usePartnerCurrencyRate, boolean rewardHasRatio, String approximateAward, String id,
            RewardRatioAttribute rewardRatio) {
        super(type, completionCount, immediateReward, partnerId, hasStaking, stakeAmount, stakingPeriod,
                stakeWarningPeriod, stakingRule, burningRule, rewardType, amountInTokens, amountInCurrency,
                usePartnerCurrencyRate, rewardHasRatio, approximateAward);
        this.id = id;
        this.rewardRatio = rewardRatio;
    }
}
