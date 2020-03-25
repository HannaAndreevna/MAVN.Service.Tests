package com.lykke.tests.api.service.reporting.model;

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
public class TransactionReport {

    private String id;
    private String senderCustomerName;
    private String senderCustomerEmail;
    private String senderCustomerWallet;
    private String inboundWalletAddress;
    private Date timestamp;
    private String transactionType;
    private String actionRuleName;
    private String receiverCustomerName;
    private String receiverCustomerEmail;
    private String receiverCustomerWallet;
    private String outboundWalletAddress;
    private String amount;
}
