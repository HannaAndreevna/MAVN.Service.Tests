package com.lykke.tests.api.service.partnerspayments.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Builder
@Data
public class ReceptionistProcessPaymentRequest {

    private String paymentRequestId;
}
