package com.lykke.tests.api.service.partnersintegration.model;

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
public class CustomerBalanceResponseModel {

    private CustomerBalanceStatus status;
    private String tokens;
    private float fiatBalance;
    private String fiatCurrency;

    public String getTokens() {
        return Double.valueOf(tokens).toString();
    }
}
