package com.lykke.tests.api.service.customer.model.partnerspayments;

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
public class PartnerPaymentRequestDetailsResponse {

    private String paymentRequestId;
    private String status;
    private String totalInToken;
    private float totalInCurrency;
    private String sendingAmountInToken;
    private String currencyCode;
    private String partnerId;
    private String partnerName;
    private String locationId;
    private String locationName;
    private String paymentInfo;
    private String walletBalance;
    @Exclude
    private String date;
    @Exclude
    private String lastUpdatedDate;
    private float tokensToFiatConversionRate;
    @Exclude
    private int customerActionExpirationTimeLeftInSeconds;
    @Exclude
    private Date customerActionExpirationTimestamp;
    private String requestedAmountInTokens;

    public String getTotalInToken() {
        return Double.valueOf(null == totalInToken ? "0.0" : totalInToken).toString();
    }

    public String getRequestedAmountInTokens() {
        return Double.valueOf(null == requestedAmountInTokens ? "0.0" : requestedAmountInTokens).toString();
    }
}
