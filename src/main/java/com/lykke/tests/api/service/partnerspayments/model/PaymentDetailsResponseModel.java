package com.lykke.tests.api.service.partnerspayments.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy.UpperCamelCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.util.Date;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonNaming(UpperCamelCaseStrategy.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PaymentDetailsResponseModel {

    private String paymentRequestId;
    private String customerId;
    private String partnerId;
    private PaymentRequestStatus status;
    private String locationId;
    private String tokensAmount;
    private String tokensSendingAmount;
    private float fiatSendingAmount;
    private float fiatAmount;
    private float totalBillAmount;
    private String currency;
    private String partnerMessageId;
    private Date tokensReserveTimestamp;
    private Date tokensBurnTimestamp;
    private Date lastUpdatedTimestamp;
    private Date timestamp;
    private float tokensToFiatConversionRate;
    private int noCustomerActionExpirationTimeLeftInSeconds;
    private Date customerActionExpirationTimestamp;
}
