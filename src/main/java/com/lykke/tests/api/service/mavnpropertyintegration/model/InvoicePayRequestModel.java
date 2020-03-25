package com.lykke.tests.api.service.mavnpropertyintegration.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Builder
@Data
public class InvoicePayRequestModel {

    private String customerId;
    private String invoiceId;
    private float amount;
    private String currency;
}
