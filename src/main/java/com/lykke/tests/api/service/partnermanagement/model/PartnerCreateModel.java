package com.lykke.tests.api.service.partnermanagement.model;

import lombok.Builder;
import lombok.Data;

@Data
public class PartnerCreateModel extends PartnerBaseModel {

    private LocationCreateModel[] locations;
    private String clientId;
    private String clientSecret;
    private String createdBy;

    @Builder(builderMethodName = "partnerBuilder")
    public PartnerCreateModel(String name, String description, String amountInTokens, float amountInCurrency,
            boolean useGlobalCurrencyRate, Vertical businessVertical,
            String clientId, String clientSecret, LocationCreateModel[] locations,
            String createdBy) {
        super(name, description, amountInTokens, amountInCurrency, useGlobalCurrencyRate, businessVertical);
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.locations = locations;
        this.createdBy = createdBy;
    }
}
