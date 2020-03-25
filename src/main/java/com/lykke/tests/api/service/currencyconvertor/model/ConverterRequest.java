package com.lykke.tests.api.service.currencyconvertor.model;

import com.lykke.api.testing.annotations.NetClassName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Builder
@Data
@NetClassName("none")
public class ConverterRequest {

    private String fromAsset;
    private String toAsset;
    private double amount;
}
