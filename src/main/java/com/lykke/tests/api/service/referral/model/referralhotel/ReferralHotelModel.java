package com.lykke.tests.api.service.referral.model.referralhotel;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy.UpperCamelCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.util.Date;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonNaming(UpperCamelCaseStrategy.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ReferralHotelModel {

    private String id;
    private String email;
    private String referrerId;
    private String confirmationToken;
    private String location;
    private String partnerId;
    private ReferralHotelState state;
    private Date creationDateTime;
    private Date expirationDateTime;
    private String customerId;
    private String campaignId;
    private String phoneNumber;
    public int phoneCountryCodeId;
    private String firstName;
    private String lastName;
    private String fullName;
}
