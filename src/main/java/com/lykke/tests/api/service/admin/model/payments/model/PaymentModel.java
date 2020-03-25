package com.lykke.tests.api.service.admin.model.payments.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy.UpperCamelCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.lykke.api.testing.annotations.PublicApi;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonNaming(UpperCamelCaseStrategy.class)
@JsonIgnoreProperties(ignoreUnknown = true)
@PublicApi
public class PaymentModel {

    private String id;
    private String campaignName;
    private String invoiceId;
    private String customerEmail;
    private String amountInTokens;
    private String amountInCurrency;
    private String timestamp;
}
