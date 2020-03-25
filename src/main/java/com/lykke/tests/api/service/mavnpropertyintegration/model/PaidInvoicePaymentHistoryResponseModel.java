package com.lykke.tests.api.service.mavnpropertyintegration.model;

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
public class PaidInvoicePaymentHistoryResponseModel {

    private String unitLocationCode;
    private String customerTransactionId;
    private int installmentNumber;
    private int totalInstallments;
    private Date timestamp;
    private Double paidAmount;
    private Double vatAmount;
    private Double netPropertyPrice;
    private Double discountAmount;
    private String propertySalesforceId;
    private String orderNumber;
    private String opportunityId;
    private String mvnReferralId;
    private String accountNumber;
    private String salesforceAccountId;
    private String agentCustomerId;
    private String buyerCustomerId;
    private String transactionNumber;
    private String oracleCustomerAccountId;
}
