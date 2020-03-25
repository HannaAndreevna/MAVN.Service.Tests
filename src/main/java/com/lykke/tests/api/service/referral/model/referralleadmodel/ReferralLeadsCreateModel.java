package com.lykke.tests.api.service.referral.model.referralleadmodel;

import com.lykke.api.testing.annotations.NetClassName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Builder
@Data
@NetClassName("ReferralLeadCreateRequest")
public class ReferralLeadsCreateModel {

    private String firstName;
    private String lastName;
    private int phoneCountryCodeId;
    private String phoneNumber;
    private String email;
    private String note;
    private String customerId;
    private String campaignId;
}
