package com.lykke.tests.api.service.mavnpropertyintegration.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Builder
@Data
public class PaymentDueManualEntryModel {

    private String orderNumber;
    private PaymentDueLocationInvoiceDetailManualEntryModel[] invoiceDetail;
    private Double netPropertyPrice;
    private Double totalAmountPaidAsOf;
    private Double discountAmount;
    private Double vatAmount;
    private String opportunityId;
    private String leadEmail;
    private String referrerEmail;
    private String accountId;
    private String mvnReferralId;
    private int totalInstallments;
}
