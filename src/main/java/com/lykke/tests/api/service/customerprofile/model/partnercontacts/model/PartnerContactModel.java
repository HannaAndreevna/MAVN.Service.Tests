package com.lykke.tests.api.service.customerprofile.model.partnercontacts.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy.UpperCamelCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Builder
@NoArgsConstructor
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(UpperCamelCaseStrategy.class)
public class PartnerContactModel {
    private String locationId;
    private String email;
    private String firstName;
    private String lastName;
    private String phoneNumber;
}
