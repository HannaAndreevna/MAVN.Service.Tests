package com.lykke.tests.api.service.admin.model.burnrules;

import com.lykke.api.testing.annotations.NetClassName;
import com.lykke.api.testing.annotations.PublicApi;
import lombok.Builder;
import lombok.Data;

@Data
@PublicApi
@NetClassName("BurnRuleCreateRequest")
public class BurnRuleCreateRequestModel extends BurnRuleBaseRequest {

    @Builder(builderMethodName = "burnRuleCreateRequestModelBuilder")
    public BurnRuleCreateRequestModel(String title, String description,
            com.lykke.tests.api.common.enums.BusinessVertical businessVertical, String[] partnerIds,
            String amountInTokens, float amountInCurrency, boolean usePartnerCurrencyRate,
            MobileContentCreateRequest[] mobileContents, int order) {
        super(title, description, businessVertical, partnerIds, amountInTokens, amountInCurrency,
                usePartnerCurrencyRate, mobileContents, order);
    }
}
