package com.lykke.tests.api.service.mavnubeintegration;

import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Builder
@Getter
public class PaymentTransaction {
    private UUID operationId;
    private UUID customerId;
    private UUID venueId;
    private Double amount;
    private String paymentDate;
}
