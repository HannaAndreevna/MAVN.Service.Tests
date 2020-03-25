package com.lykke.tests.api.service.admin.model.burnrules;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy.UpperCamelCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.lykke.tests.api.service.admin.model.bonustypes.Vertical;
import java.util.Date;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonNaming(UpperCamelCaseStrategy.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class BurnRuleInfoModel {

    private String id;
    private String title;
    private long amountInTokens;
    private float amountInCurrency;
    private boolean usePartnerCurrencyRate;
    private Date creationDate;
    private int order;
    private Vertical vertical;
}
