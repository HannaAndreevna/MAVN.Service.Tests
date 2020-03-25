package com.lykke.tests.api.service.referral.model.referralhotel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Builder
@Data
public class ReferralHotelUseRequest {

    private String buyerEmail;
    private String partnerId;
    private String location;
    private float amount;
    private String currencyCode;
}
