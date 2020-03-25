package com.lykke.tests.api.service.campaigns.model.mobile;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy.UpperCamelCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.lykke.tests.api.service.campaigns.model.RewardRatioAttributeDetailsResponseModel;
import com.lykke.tests.api.service.campaigns.model.RewardType;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonNaming(UpperCamelCaseStrategy.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ConditionLocalizedResponse {

    private String id;
    private String type;
    private boolean isHidden;
    private String displayName;
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
    private boolean isApproximate;
    private String approximateAward;
    private String amountInTokens;
    private Double amountInCurrency;
    private boolean usePartnerCurrencyRate;
    private RewardRatioAttributeDetailsResponseModel rewardRatio;
}
