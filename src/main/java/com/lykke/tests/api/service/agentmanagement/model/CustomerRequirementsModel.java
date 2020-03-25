package com.lykke.tests.api.service.agentmanagement.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy.UpperCamelCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.lykke.api.testing.annotations.PublicApi;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonNaming(UpperCamelCaseStrategy.class)
@JsonIgnoreProperties(ignoreUnknown = true)
@PublicApi
public class CustomerRequirementsModel {

    private String customerId;
    private boolean isEligible;
    private boolean hasEnoughTokens;
    private boolean hasVerifiedEmail;

    public boolean isEligible() {
        return isEligible;
    }

    public boolean hasEnoughTokens() {
        return hasEnoughTokens;
    }

    public boolean hasVerifiedEmail() {
        return hasVerifiedEmail;
    }
}
