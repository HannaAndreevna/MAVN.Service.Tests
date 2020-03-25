package com.lykke.tests.api.service.partnerspayments.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy.UpperCamelCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
@JsonNaming(UpperCamelCaseStrategy.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PendingPaymentResponseModel {

    private String paymentRequestId;
    private String customerId;
    private String partnerId;
    private PaymentRequestStatus status;
    private String locationId;
    private long tokensAmount;
    private long tokensSendingAmount;
    private float fiatAmount;
    private String currency;
    private String paymentInfo;
    private Date date;
}
