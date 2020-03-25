package com.lykke.tests.api.service.mavnpropertyintegration.model;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Builder
@Data
public class PaymentDueLocationInvoiceDetailManualEntryModel {

    private String customerTrxId;
    private String trxNumber;
    private String trxDate;
    private Double lineAmount;
    private String customerAccountId;
    private String accountNumber;
    private String locationCode;
    private int installmentNumber;
}
