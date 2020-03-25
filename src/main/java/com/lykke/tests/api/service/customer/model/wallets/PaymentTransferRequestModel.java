package com.lykke.tests.api.service.customer.model.wallets;

import com.fasterxml.jackson.databind.PropertyNamingStrategy.UpperCamelCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Builder
@Data
public class PaymentTransferRequestModel {

    private String campaignId;
    private String invoiceId;
    private String amount;

}
