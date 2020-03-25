package com.lykke.tests.api.service.referral.model.referralhotel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Builder
@Data
public class ReferralHotelCreateRequest {

    private String email;
    private String referrerId;
    private String campaignId;
    private String phoneNumber;
    private int phoneCountryCodeId;
    private String fullName;
}
