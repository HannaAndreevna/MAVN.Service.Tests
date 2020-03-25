package com.lykke.tests.api.service.admin.model;

import com.lykke.api.testing.annotations.PublicApi;
import com.lykke.tests.api.service.admin.model.burnrules.MobileContentEditRequest;
import com.lykke.tests.api.service.admin.model.earnrules.ConditionUpdateModel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@PublicApi
public class EarnRuleUpdateModel extends EarnRuleBaseModel {

    public String id;
    public ConditionUpdateModel[] conditions;
    public MobileContentEditRequest[] mobileContents;

    @Builder(builderMethodName = "earnRuleUpdateModelBuilder")
    public EarnRuleUpdateModel(String name, String reward, Double rewardDecimal, String amountInTokens,
            Double amountInCurrency, boolean usePartnerCurrencyRate, RewardType rewardType, String fromDate,
            String toDate, int completionCount, boolean isEnabled, String description, String approximateAward,
            int order, String id, ConditionUpdateModel[] conditions, MobileContentEditRequest[] mobileContents) {
        super(name, reward, rewardDecimal, amountInTokens, amountInCurrency, usePartnerCurrencyRate, rewardType,
                fromDate, toDate, completionCount, isEnabled, description, approximateAward, order);
        this.id = id;
        this.conditions = conditions;
        this.mobileContents = mobileContents;
    }
}
