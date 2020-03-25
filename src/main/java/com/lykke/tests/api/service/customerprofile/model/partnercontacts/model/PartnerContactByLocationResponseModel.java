package com.lykke.tests.api.service.customerprofile.model.partnercontacts.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy.UpperCamelCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(UpperCamelCaseStrategy.class)
public class PartnerContactByLocationResponseModel {
    private PartnerContactModel partnerContact;
    private String errorCode;
}
