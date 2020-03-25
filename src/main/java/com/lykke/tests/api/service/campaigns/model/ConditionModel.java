package com.lykke.tests.api.service.campaigns.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy.UpperCamelCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.lykke.api.testing.annotations.PublicApi;
import com.lykke.tests.api.service.campaigns.model.burnrules.Vertical;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonNaming(UpperCamelCaseStrategy.class)
@JsonIgnoreProperties(ignoreUnknown = true)
@PublicApi
public class ConditionModel extends ConditionBaseModel {

    private String id;
    private String typeDisplayName;
    private Vertical vertical;
    private boolean isHiddenType;
    private String campaignId;
    private RewardRatioAttributeDetailsResponseModel rewardRatio;

    @Builder(builderMethodName = "conditionModelBuilder")
    public ConditionModel(String type, String immediateReward, int completionCount, String[] partnerIds,
            boolean hasStaking, String stakeAmount, int stakingPeriod, int stakeWarningPeriod, Double stakingRule,
            Double burningRule, RewardType rewardType, String approximateAward, String amountInTokens,
            Double amountInCurrency, boolean usePartnerCurrencyRate, boolean rewardHasRatio, String id,
            String typeDisplayName, Vertical vertical, boolean isHiddenType, String campaignId,
            RewardRatioAttributeDetailsResponseModel rewardRatio) {
        super(type, immediateReward, completionCount, partnerIds, hasStaking, stakeAmount, stakingPeriod,
                stakeWarningPeriod, stakingRule, burningRule, rewardType, approximateAward, amountInTokens,
                amountInCurrency, usePartnerCurrencyRate, rewardHasRatio);
        this.id = id;
        this.typeDisplayName = typeDisplayName;
        this.vertical = vertical;
        this.isHiddenType = isHiddenType;
        this.campaignId = campaignId;
        this.rewardRatio = rewardRatio;
    }
}
