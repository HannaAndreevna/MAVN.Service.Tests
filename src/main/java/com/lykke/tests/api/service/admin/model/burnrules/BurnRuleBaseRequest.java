package com.lykke.tests.api.service.admin.model.burnrules;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
public class BurnRuleBaseRequest {

    private String title;
    private String description;
    private com.lykke.tests.api.common.enums.BusinessVertical businessVertical;
    private String[] partnerIds;
    private String amountInTokens;
    private float amountInCurrency;
    private boolean usePartnerCurrencyRate;
    private MobileContentCreateRequest[] mobileContents;
    private int order;

    public String getBusinessVertical() {
        return businessVertical.getCode();
    }

    public String getAmountInTokens() {
        return Double.valueOf(amountInTokens).toString();
    }
}
