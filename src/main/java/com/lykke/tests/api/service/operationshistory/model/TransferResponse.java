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
public class TransferResponse {

    private String transactionId;
    private String externalOperationId;
    private String senderCustomerId;
    private String receiverCustomerId;
    private String assetSymbol;
    private String amount;
    private Date timestamp;
    private String walletAddress;
    private String otherSideWalletAddress;
    private String senderCustomerEmail;
    private String receiverCustomerEmail;
}
