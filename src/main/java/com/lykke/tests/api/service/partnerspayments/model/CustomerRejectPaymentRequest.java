package com.lykke.tests.api.service.partnerspayments.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Builder
@Data
public class CustomerRejectPaymentRequest {

    private String paymentRequestId;
    private String customerId;
}
