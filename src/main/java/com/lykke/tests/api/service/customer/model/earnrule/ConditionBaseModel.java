package com.lykke.tests.api.service.customer.model.earnrule;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy.UpperCamelCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.lykke.tests.api.service.customer.model.referral.RewardRatioAttributeModel;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonNaming(UpperCamelCaseStrategy.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ConditionBaseModel {

    private String id;
    private String type;
    private String displayName;
    private String immediateReward;
    private int completionCount;
    private boolean hasStaking;
    private String stakeAmount;
    private int stakingPeriod;
    private int stakeWarningPeriod;
    private Double stakingRule;
    private Double burningRule;
    private RewardType rewardType;
    private String amountInTokens;
    private Double amountInCurrency;
    private boolean usePartnerCurrencyRate;
    private RewardRatioAttributeModel rewardRatio;
    private String approximateAward;
    private boolean isApproximate;
}
