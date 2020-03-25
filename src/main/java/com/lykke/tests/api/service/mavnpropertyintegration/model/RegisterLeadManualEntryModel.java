package com.lykke.tests.api.service.mavnpropertyintegration.model;

import com.fasterxml.jackson.databind.PropertyNamingStrategy.UpperCamelCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
@JsonNaming(UpperCamelCaseStrategy.class)
public class RegisterLeadManualEntryModel {

    private String id;
    private String referId;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String phoneCountryCode;
    private String phoneCountryName;
    private String agentSalesforceId;
    private String leadNote;
    private String responseStatus;
    private String responseSalesforceId;
    private String responseMVNReferralID;
    private String responseErrorCode;
}
