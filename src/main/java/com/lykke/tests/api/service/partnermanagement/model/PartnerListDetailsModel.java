package com.lykke.tests.api.service.partnermanagement.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy.UpperCamelCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.util.Date;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonNaming(UpperCamelCaseStrategy.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PartnerListDetailsModel extends PartnerBaseModel {

    private String id;
    private String clientId;
    private String createdBy;
    private Date createdAt;

    @Builder(builderMethodName = "partnerBuilder")
    public PartnerListDetailsModel(String name, String description, String amountInTokens, float amountInCurrency,
            boolean useGlobalCurrencyRate, Vertical businessVertical, String id, String clientId, String createdBy,
            Date createdAt) {
        super(name, description, amountInTokens, amountInCurrency, useGlobalCurrencyRate, businessVertical);
        this.id = id;
        this.clientId = clientId;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
    }
}
