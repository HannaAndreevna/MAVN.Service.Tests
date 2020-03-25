package com.lykke.tests.api.service.admin.model.partners;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy.UpperCamelCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.lykke.tests.api.common.enums.BusinessVertical;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonNaming(UpperCamelCaseStrategy.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PartnerDetailsResponse extends PartnerBaseModel {

    private String id;
    private LocationResponse[] locations;

    @Builder(builderMethodName = "partnerDetailsResponseBuilder")
    public PartnerDetailsResponse(String name, String amountInTokens, Double amountInCurrency,
            boolean useGlobalCurrencyRate, String description, String clientId, String clientSecret,
            BusinessVertical businessVertical, String id, LocationResponse[] locations) {
        super(name, amountInTokens, amountInCurrency, useGlobalCurrencyRate, description, clientId, clientSecret,
                businessVertical);
        this.id = id;
        this.locations = locations;
    }
}
