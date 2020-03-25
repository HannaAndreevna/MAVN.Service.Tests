package com.lykke.tests.api.service.adminmanagement.model;

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
public enum AdminManagementError {
    NONE("None"),
    LOGIN_NOT_FOUND("LoginNotFound"),
    PASSWORD_MISMATCH("PasswordMismatch"),
    REGISTERED_WITH_ANOTHER_PASSWORD("RegisteredWithAnotherPassword"),
    ALREADY_REGISTERED("AlreadyRegistered"),
    INVALID_EMAIL_OR_PASSWORD_FORMAT("InvalidEmailOrPasswordFormat"),
    ADMIN_NOT_ACTIVE("AdminNotActive");

    private static Map<String, AdminManagementError> FORMAT_MAP =
            Stream.of(AdminManagementError.values())
                    .collect(toMap(c -> c.getCode(), Function.identity()));
    @Getter
    private String code;

    @JsonCreator
    public static AdminManagementError fromString(String value) {
        return Optional
                .ofNullable(FORMAT_MAP.get(value))
                .orElseThrow(() -> new IllegalArgumentException(value));
    }
}
