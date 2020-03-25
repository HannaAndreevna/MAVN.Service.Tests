package com.lykke.tests.api.service.partnermanagement.model;

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
public enum PartnerManagementError {
    NONE("None"),
    PARTNER_NOT_FOUND("PartnerNotFound"),
    LOGIN_NOT_FOUND("LoginNotFound"),
    ALREADY_REGISTERED("AlreadyRegistered"),
    REGISTRATION_FAILED("RegistrationFailed"),
    LOCATION_EXTERNAL_ID_NOT_UNIQUE("LocationExternalIdNotUnique");

    private static Map<String, PartnerManagementError> FORMAT_MAP =
            Stream.of(PartnerManagementError.values())
                    .collect(toMap(c -> c.getCode(), Function.identity()));
    @Getter
    private String code;

    @JsonCreator
    public static PartnerManagementError fromString(String value) {
        return Optional
                .ofNullable(FORMAT_MAP.get(value))
                .orElseThrow(() -> new IllegalArgumentException(value));
    }
}
