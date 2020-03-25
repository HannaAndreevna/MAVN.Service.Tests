package com.lykke.tests.api.service.currencyconvertor.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Builder
@Data
public class CurrencyConvertor {

    private String currencyAssetCode;
    private String currencyAssetLabel;
    private Float currencyRate;
}
