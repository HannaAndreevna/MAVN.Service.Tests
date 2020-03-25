package com.lykke.tests.api.service.privateblockchainfacade.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Builder
@Data
public class GenericTransferRequestModel {

    private String senderCustomerId;
    private String recipientAddress;
    private long amount;
    private String transferId;
    private String additionalData;
}
