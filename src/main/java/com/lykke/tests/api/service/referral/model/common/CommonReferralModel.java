package com.lykke.tests.api.service.referral.model.common;

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
public class CommonReferralModel {

    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private Date timeStamp;
    private CommonReferralStatus status;
    private ReferralType referralType;
    private String campaignId;
    private String partnerId;
}
