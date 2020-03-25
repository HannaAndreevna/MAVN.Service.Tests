package com.lykke.tests.api.service.referral.model.referralleadmodel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Builder
@Data
public class ReferralLeadConfirmRequest {

    private String confirmationToken;
}
