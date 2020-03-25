package com.lykke.tests.api.service.admin.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy.UpperCamelCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
@JsonNaming(UpperCamelCaseStrategy.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PersonalDataResponse {

    private String customerId;
    private String email;
    private String firstName;
    private String lastName;
    @EqualsAndHashCode.Exclude
    private String registeredDate;
    @EqualsAndHashCode.Exclude
    private String referralCode;
    private String agentStatus;
    private int walletStatus;
    private int customerStatus;
}
