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
public class PaymentResponseModel {

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
    private Date date;
    private Date lastUpdatedDate;
}
