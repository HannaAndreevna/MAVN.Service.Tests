package com.lykke.tests.api.service.referral.model.referralhotel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Builder
@Data
public class ReferralHotelConfirmRequest {

    private String confirmationToken;
}
