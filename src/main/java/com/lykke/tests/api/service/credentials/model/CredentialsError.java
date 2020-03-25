package com.lykke.tests.api.service.credentials.model;

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
public enum CredentialsError {
    NONE("None", 0),
    LOGIN_NOT_FOUND("LoginNotFound", 1),
    PASSWORD_MISMATCH("PasswordMismatch", 2),
    LOGIN_ALREADY_EXISTS("LoginAlreadyExists", 3);

    private static Map<String, CredentialsError> FORMAT_MAP =
            Stream.of(CredentialsError.values())
                    .collect(toMap(c -> c.getCode(), Function.identity()));
    @Getter
    private String code;
    @Getter
    private int numericCode;

    @JsonCreator
    public static CredentialsError fromString(String value) {
        return Optional
                .ofNullable(FORMAT_MAP.get(value))
                .orElseThrow(() -> new IllegalArgumentException(value));
    }
}
