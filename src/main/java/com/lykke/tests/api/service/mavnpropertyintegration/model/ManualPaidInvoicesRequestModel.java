package com.lykke.tests.api.service.mavnpropertyintegration.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Builder
@Data
public class ManualPaidInvoicesRequestModel {

    private String customerId;
    private String invoiceId;
    private int amount;
    private String currency;
    private String responseStatus;
    private String responseErrorCode;
    private String responsePaymentId;
}
