package com.lykke.tests.api.service.operationshistory.model;

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
public class VoucherPurchasePaymentResponse {

    private String transferId;
    private String customerId;
    private String spendRuleId;
    private String voucherId;
    private String amount;
    private String assetSymbol;
    private Date timestamp;
}
