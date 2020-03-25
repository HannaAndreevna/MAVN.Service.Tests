package com.lykke.tests.api.service.referral.model.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Builder
@Data
public class CommonReferralByReferralIdsRequest {

    private String[] referralIds;
}
