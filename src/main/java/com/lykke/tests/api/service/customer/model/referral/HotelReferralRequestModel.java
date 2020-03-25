package com.lykke.tests.api.service.customer.model.referral;

import com.lykke.api.testing.annotations.PublicApi;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Builder
@Data
@PublicApi
public class HotelReferralRequestModel {

    private String email;
    private int countryPhoneCodeId;
    private String phoneNumber;
    private String fullName;
    private String campaignId;
}
