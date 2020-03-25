package com.lykke.tests.api.service.admin.model;

import com.lykke.api.testing.annotations.PublicApi;
import com.lykke.tests.api.service.admin.model.earnrules.ConditionCreateModel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@PublicApi
public class EarnRuleCreateModel extends EarnRuleBaseModel {

    private ConditionCreateModel[] conditions;
    private MobileContentCreateRequest[] mobileContents;

    @Builder(builderMethodName = "earnRuleCreateModelBuilder")
    public EarnRuleCreateModel(String name, String reward, Double rewardDecimal, String amountInTokens,
            Double amountInCurrency, boolean usePartnerCurrencyRate, RewardType rewardType, String fromDate,
            String toDate, int completionCount, boolean isEnabled, String description, String approximateAward,
            int order, ConditionCreateModel[] conditions, MobileContentCreateRequest[] mobileContents) {
        super(name, reward, rewardDecimal, amountInTokens, amountInCurrency, usePartnerCurrencyRate, rewardType,
                fromDate, toDate, completionCount, isEnabled, description, approximateAward, order);
        this.conditions = conditions;
        this.mobileContents = mobileContents;
    }

    public boolean getIsEnabled() {
        return super.isEnabled();
    }

    public void setIsEnabled(boolean value) {
        super.setEnabled(value);
    }
}
