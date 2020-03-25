package com.lykke.tests.api.service.partnerapi.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy.UpperCamelCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.lykke.api.testing.annotations.PublicApi;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode.Exclude;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
@JsonNaming(UpperCamelCaseStrategy.class)
@JsonIgnoreProperties(ignoreUnknown = true)
@PublicApi
public class GetPaymentRequestStatusResponseModel {

    private GetPaymentRequestStatus status;
    private String totalFiatAmount;
    // TODO: temporarily
    @Exclude
    private String fiatAmount;
    private String fiatCurrency;
    private String tokensAmount;
    @Exclude
    private Date paymentRequestTimestamp;
    @Exclude
    private Date paymentRequestCustomerExpirationTimestamp;
    @Exclude
    private Date paymentExecutionTimestamp;
    @Exclude
    private Date paymentRequestApprovedTimestamp;

    public Double getTokensAmount() {
        return Double.valueOf(tokensAmount);
    }
}
