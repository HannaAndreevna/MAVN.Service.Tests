package com.lykke.tests.api.service.campaigns.model.burnrules;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy.UpperCamelCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@JsonNaming(UpperCamelCaseStrategy.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ModelErrorModel {

    private BurnRuleContentErrorModel[] burnRuleContents;
    private String[] createdBy;
    private String[] title;
    private String[] description;
    private String[] amountInTokens;
    private String[] amountInCurrency;
    private String[] vertical;

    public ModelErrorModel() {
        burnRuleContents = new BurnRuleContentErrorModel[]{};
    }
}
