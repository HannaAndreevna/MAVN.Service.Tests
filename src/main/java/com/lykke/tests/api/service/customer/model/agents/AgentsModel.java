package com.lykke.tests.api.service.customer.model.agents;

import com.fasterxml.jackson.databind.PropertyNamingStrategy.UpperCamelCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.lykke.api.testing.annotations.PublicApi;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonNaming(UpperCamelCaseStrategy.class)
@PublicApi
public class AgentsModel {

    private String email;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String status;
    private String isEligible;
    private Boolean hasEnoughTokens;
    private Boolean hasVerifiedEmail;
    private int requiredNumberOfTokens;
}
