package com.lykke.tests.api.service.partnerapi.model;

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
public class GetPaymentRequestRequestModel {

    private String paymentRequestId;
}
