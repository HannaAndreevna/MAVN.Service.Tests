package com.lykke.tests.api.service.walletmanagement.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@AllArgsConstructor
public class TransferBalanceRequestModel {

    private String operationId;
    private String senderCustomerId;
    private String receiverCustomerId;
    private String amount;
}
