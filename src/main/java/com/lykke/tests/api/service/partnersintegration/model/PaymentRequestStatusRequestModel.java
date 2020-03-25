package com.lykke.tests.api.service.partnersintegration.model;

import com.lykke.api.testing.annotations.NetClassName;
import com.lykke.api.testing.annotations.QueryParameters;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Builder
@Data
@NetClassName("none")
@QueryParameters
public class PaymentRequestStatusRequestModel {

    private String paymentRequestId;
    private String partnerId;
}
