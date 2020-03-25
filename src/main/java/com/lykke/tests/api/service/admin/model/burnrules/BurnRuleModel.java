package com.lykke.tests.api.service.admin.model.burnrules;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy.UpperCamelCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.lykke.tests.api.common.enums.BusinessVertical;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonNaming(UpperCamelCaseStrategy.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class BurnRuleModel {

    private String id;
    private String title;
    private String description;
    private BusinessVertical businessVertical;
    private String[] partnerIds;
    private String amountInTokens;
    private float amountInCurrency;
    private boolean usePartnerCurrencyRate;
    private MobileContentResponse[] mobileContents;
    private int order;

    public String getAmountInTokens() {
        return Double.valueOf(amountInTokens).toString();
    }
}
