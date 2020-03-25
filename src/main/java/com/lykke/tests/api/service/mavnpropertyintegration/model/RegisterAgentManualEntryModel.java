package com.lykke.tests.api.service.mavnpropertyintegration.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Builder
@Data
public class RegisterAgentManualEntryModel {

    private String customerId;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneCountryName;
    private String phoneCountryCode;
    private String phoneNumber;
    private String note;
    private String countryOfResidence;
    private String beneficiaryName;
    private String bankName;
    private String bankBranch;
    private String accountNumber;
    private String bankAddress;
    private String bankBranchCountry;
    private String iban;
    private String swift;
    private AgentImageModel[] images;
    private String responseStatus;
    private String responseSalesmanSalesforceId;
    private String responseAgentSalesforceId;
    private String responseErrorCode;
    private AgentRegisterStatus responseAgentStatus;

    public String getResponseAgentStatus() {
        return responseAgentStatus.getCode();
    }
}
