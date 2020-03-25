package com.lykke.tests.api.service.admin.model.earnrules;

import com.lykke.tests.api.service.admin.model.RewardType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Builder
@Data
public class ConditionBaseModel {

    private String type;
    private int completionCount;
    private String immediateReward;
    private String partnerId;
    private boolean hasStaking;
    private String stakeAmount;
    private int stakingPeriod;
    private int stakeWarningPeriod;
    private Double stakingRule;
    private Double burningRule;
    public RewardType rewardType;
    private String amountInTokens;
    private Double amountInCurrency;
    public boolean usePartnerCurrencyRate;
    public boolean rewardHasRatio;
    private String approximateAward;
}
