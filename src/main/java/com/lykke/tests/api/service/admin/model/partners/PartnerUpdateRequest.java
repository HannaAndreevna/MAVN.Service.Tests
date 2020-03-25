package com.lykke.tests.api.service.admin.model.partners;

import com.lykke.tests.api.common.enums.BusinessVertical;
import lombok.Builder;
import lombok.Data;

@Data
public class PartnerUpdateRequest extends PartnerBaseModel {

    private String id;
    private LocationEditRequest[] locations;

    @Builder(builderMethodName = "partnerUpdateRequestBuilder")
    public PartnerUpdateRequest(String name, String amountInTokens, Double amountInCurrency,
            boolean useGlobalCurrencyRate, String description, String clientId, String clientSecret,
            BusinessVertical businessVertical, String id, LocationEditRequest[] locations) {
        super(name, amountInTokens, amountInCurrency, useGlobalCurrencyRate, description, clientId, clientSecret,
                businessVertical);
        this.id = id;
        this.locations = locations;
    }
}
