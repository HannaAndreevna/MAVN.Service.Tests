package com.lykke.tests.api.service.partnermanagement.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy.UpperCamelCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
@JsonNaming(UpperCamelCaseStrategy.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PartnerBaseModel {

    private String name;
    private String description;
    private String amountInTokens;
    private float amountInCurrency;
    private boolean useGlobalCurrencyRate;
    private Vertical businessVertical;

    public String getBusinessVertical() {
        return null != businessVertical ? businessVertical.getCode() : Vertical.HOSPITALITY.getCode();
    }
}
