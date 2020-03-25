package com.lykke.tests.api.service.referral.model.referralleadmodel;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy.UpperCamelCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonNaming(UpperCamelCaseStrategy.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ReferralLeadsApprovedResponseModel {

    private ReferralLeadsApprovedModel[] referralLeads;
    private String errorCode;
    private String errorMessage;
}
