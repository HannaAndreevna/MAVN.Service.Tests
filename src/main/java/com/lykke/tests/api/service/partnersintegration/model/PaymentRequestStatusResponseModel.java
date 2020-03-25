package com.lykke.tests.api.service.partnersintegration.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy.UpperCamelCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode.Exclude;
import lombok.NoArgsConstructor;

import java.util.Date;

@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
@JsonNaming(UpperCamelCaseStrategy.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PaymentRequestStatusResponseModel {

    private PaymentRequestStatus status;
    private float totalFiatAmount;
    // TODO: temporarily
    @Exclude
    private float fiatAmount;
    private String fiatCurrency;
    private String tokensAmount;
    @Exclude
    private Date paymentRequestTimestamp;
    @Exclude
    private Date paymentRequestCustomerExpirationTimestamp;
    @Exclude
    private Date paymentRequestApprovedTimestamp;
    @Exclude
    private Date paymentExecutionTimestamp;

    public String getTokensAmount() {
        return Double.valueOf(tokensAmount).toString();
    }
}
