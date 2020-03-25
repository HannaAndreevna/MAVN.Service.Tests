package com.lykke.tests.api.service.referral.model.referralleadmodel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Builder
@Data
public class ReferralLeadCreateRequest {

    private String firstName;
    private String lastName;
    private int phoneCountryCodeId;
    private String phoneNumber;
    private String email;
    private String note;
    private String customerId;
    private String campaignId;
}
