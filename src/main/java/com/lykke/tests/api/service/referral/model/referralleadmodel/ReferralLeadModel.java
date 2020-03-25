package com.lykke.tests.api.service.referral.model.referralleadmodel;

import com.fasterxml.jackson.databind.PropertyNamingStrategy.UpperCamelCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.util.Date;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonNaming(UpperCamelCaseStrategy.class)
public class ReferralLeadModel {

    private String id;
    private String firstName;
    private String lastName;
    private int phoneCountryCodeId;
    private String phoneNumber;
    private String email;
    private String note;
    private String agentId;
    private String agentSalesforceId;
    private ReferralLeadState state;
    private String salesforceId;
    private String confirmationToken;
    private String creationDateTime;
    private int purchaseCount;
    private int offersCount;
    private String campaignId;
}
