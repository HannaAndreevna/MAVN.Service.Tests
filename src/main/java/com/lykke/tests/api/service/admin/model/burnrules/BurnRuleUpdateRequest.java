package com.lykke.tests.api.service.admin.model.burnrules;

import lombok.Builder;
import lombok.Data;

@Data
public class BurnRuleUpdateRequest extends BurnRuleBaseRequest {

    private String id;

    @Builder(builderMethodName = "burnRuleUpdateRequestBuilder")
    public BurnRuleUpdateRequest(String title, String description,
            com.lykke.tests.api.common.enums.BusinessVertical businessVertical, String[] partnerIds,
            String amountInTokens, float amountInCurrency, boolean usePartnerCurrencyRate,
            MobileContentCreateRequest[] mobileContents, int order, String id) {
        super(title, description, businessVertical, partnerIds, amountInTokens, amountInCurrency,
                usePartnerCurrencyRate, mobileContents, order);
        this.id = id;
    }
}
