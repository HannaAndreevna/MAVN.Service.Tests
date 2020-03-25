package com.lykke.tests.api.service.referral.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Builder
@Data
public class ReferralCreateRequest {

    private String customerId;
}
