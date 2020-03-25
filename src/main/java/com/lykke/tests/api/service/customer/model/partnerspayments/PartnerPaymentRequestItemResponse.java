package com.lykke.tests.api.service.customer.model.partnerspayments;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy.UpperCamelCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.lykke.api.testing.annotations.PublicApi;
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
@PublicApi
public class PartnerPaymentRequestItemResponse {

    private String paymentRequestId;
    private String status;
    private String totalInToken;
    private float totalInCurrency;
    private String sendingAmountInToken;
    private String currencyCode;
    private String partnerId;
    private String partnerName;
    private String locationId;
    private String paymentInfo;
    private Date date;
    private Date lastUpdatedDate;
}
