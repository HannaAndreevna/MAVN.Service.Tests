package com.lykke.tests.api.service.customer.model.spendrule;

import com.fasterxml.jackson.databind.PropertyNamingStrategy.UpperCamelCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonNaming(UpperCamelCaseStrategy.class)
public class SpendRuleResponseModel {

    private String id;
    private String title;
    private int amountInTokens;
    private int amountInCurrency;
    private String currencyName;
    private String description;
    private String imageUrl;
    private String businessVertical;
}
