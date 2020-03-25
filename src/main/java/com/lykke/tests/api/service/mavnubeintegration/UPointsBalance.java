package com.lykke.tests.api.service.mavnubeintegration;

import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Builder
@Getter
public class UPointsBalance {
    private UUID operationId;
    private UUID customerId;
    private Double amount;
    private String changeDate;
}
