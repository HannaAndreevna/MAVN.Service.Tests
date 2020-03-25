package com.lykke.tests.api.service.currencyconvertor.model;

import com.lykke.api.testing.annotations.PublicApi;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Builder
@Data
@PublicApi
public class GlobalCurrencyRateRequest {

    private String amountInTokens;
    private float amountInCurrency;
}
