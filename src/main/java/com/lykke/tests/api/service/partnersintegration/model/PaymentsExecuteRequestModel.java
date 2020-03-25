package com.lykke.tests.api.service.partnersintegration.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Builder
@Data
public class PaymentsExecuteRequestModel {

    private String paymentRequestId;
    private String partnerId;
}
