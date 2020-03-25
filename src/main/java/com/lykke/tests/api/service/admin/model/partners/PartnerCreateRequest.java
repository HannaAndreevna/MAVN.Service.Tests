package com.lykke.tests.api.service.admin.model.partners;

import com.lykke.tests.api.common.enums.BusinessVertical;
import lombok.Builder;
import lombok.Data;

@Data
public class PartnerCreateRequest extends PartnerBaseModel {

    private LocationCreateRequest[] locations;

    @Builder(builderMethodName = "partnerCreateRequestBuilder")
    public PartnerCreateRequest(String name, String amountInTokens, Double amountInCurrency,
            boolean useGlobalCurrencyRate, String description, String clientId, String clientSecret,
            BusinessVertical businessVertical, LocationCreateRequest[] locations) {
        super(name, amountInTokens, amountInCurrency, useGlobalCurrencyRate, description, clientId, clientSecret,
                businessVertical);
        this.locations = locations;
    }
}
