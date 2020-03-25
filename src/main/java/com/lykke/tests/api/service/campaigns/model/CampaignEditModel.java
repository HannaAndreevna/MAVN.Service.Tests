package com.lykke.tests.api.service.campaigns.model;

import com.lykke.api.testing.annotations.PublicApi;
import lombok.Builder;
import lombok.Data;

@Data
@PublicApi
public class CampaignEditModel extends CampaignBaseModel {

    private String id;
    private boolean isEnabled;
    private ConditionEditModel[] conditions;
    private EarnRuleContentEditRequest[] contents;

    @Builder(builderMethodName = "campaignBuilder")
    public CampaignEditModel(String name, String description, String reward, RewardType rewardType,
            String approximateAward, String amountInTokens, float amountInCurrency, boolean usePartnerCurrencyRate,
            String fromDate, String toDate, int completionCount, int order, String id, boolean isEnabled,
            ConditionEditModel[] conditions, EarnRuleContentEditRequest[] contents) {
        super(name, description, reward, rewardType, approximateAward, amountInTokens, amountInCurrency,
                usePartnerCurrencyRate, fromDate,
                toDate, completionCount, order);
        this.id = id;
        this.isEnabled = isEnabled;
        this.conditions = conditions;
        this.contents = contents;
    }
}
