package com.lykke.tests.api.service.customer.model.partnerspayments;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Builder
@Data
public class GetPartnerPaymentRequestDetailsRequest {

    private String paymentRequestId;
}
