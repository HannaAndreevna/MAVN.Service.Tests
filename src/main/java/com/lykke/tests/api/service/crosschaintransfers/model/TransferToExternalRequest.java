package com.lykke.tests.api.service.crosschaintransfers.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Builder
@Data
public class TransferToExternalRequest {

    private String customerId;
    private String amount;
}
