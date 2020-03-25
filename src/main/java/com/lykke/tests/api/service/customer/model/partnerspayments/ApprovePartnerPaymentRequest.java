package com.lykke.tests.api.service.customer.model.partnerspayments;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Builder
@Data
public class ApprovePartnerPaymentRequest {

    private String paymentRequestId;
    private String sendingAmount;
}
