package com.lykke.tests.api.service.partnermanagement.model;

import lombok.Builder;
import lombok.Data;

@Data
public class PartnerUpdateModel extends PartnerBaseModel {

    private String id;
    private String clientId;
    private String clientSecret;
    private LocationUpdateModel[] locations;

    @Builder(builderMethodName = "partnerBuilder")
    public PartnerUpdateModel(String name, String description, String amountInTokens, float amountInCurrency,
            boolean useGlobalCurrencyRate, Vertical businessVertical, String id, String clientId, String clientSecret,
            LocationUpdateModel[] locations) {
        super(name, description, amountInTokens, amountInCurrency, useGlobalCurrencyRate, businessVertical);
        this.id = id;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.locations = locations;
    }
}
