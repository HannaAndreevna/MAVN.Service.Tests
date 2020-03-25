package com.lykke.tests.api.service.agentmanagement.model;

import static java.util.stream.Collectors.toMap;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.PropertyNamingStrategy.UpperCamelCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.lykke.api.testing.annotations.PublicApi;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@JsonNaming(UpperCamelCaseStrategy.class)
@PublicApi
public enum AgentManagementErrorCode {
    NONE("None"),
    CUSTOMER_PROFILE_DOES_NOT_EXIST("CustomerProfileDoesNotExist"),
    EMAIL_NOT_VERIFIED("EmailNotVerified"),
    NOT_ENOUGH_TOKENS("NotEnoughTokens"),
    AGENT_PENDING_KYA("AgentPendingKya"),
    AGENT_ALREADY_APPROVED("AgentAlreadyApproved"),
    COUNTRY_PHONE_CODE_DOES_NOT_EXIST("CountryPhoneCodeDoesNotExist"),
    COUNTRY_OF_RESIDENCE_DOES_NOT_EXIST("CountryOfResidenceDoesNotExist"),
    BANK_BRANCH_COUNTRY_DOES_NOT_EXIST("BankBranchCountryDoesNotExist");

    private static Map<String, AgentManagementErrorCode> FORMAT_MAP =
            Stream.of(AgentManagementErrorCode.values())
                    .collect(toMap(c -> c.getStatus(), Function.identity()));
    @Getter
    private String status;

    @JsonCreator
    public static AgentManagementErrorCode fromString(String value) {
        return Optional
                .ofNullable(FORMAT_MAP.get(value))
                .orElseThrow(() -> new IllegalArgumentException(value));
    }
}
