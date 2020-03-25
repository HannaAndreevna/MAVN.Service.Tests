package com.lykke.tests.api.service.credentials.model;

import static java.util.stream.Collectors.toMap;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.PropertyNamingStrategy.UpperCamelCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
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
public enum PasswordResetError {
    NONE("None", "There is no Error with the Generation of Reset Identifier"),
    THERE_IS_NO_IDENTIFIER_FOR_THIS_CUSTOMER("ThereIsNoIdentifierForThisCustomer",
            "There given Customer doesn't have identifier"),
    REACHED_MAXIMUM_REQUEST_FOR_PERIOD("ReachedMaximumRequestForPeriod",
            "Reaching the Maximum amount of Calls per Given Period and Blocking further such actions for the same Period."),
    IDENTIFIER_MISMATCH("IdentifierMismatch", "The Provided identifier doesn't much the Customer's one"),
    PROVIDED_IDENTIFIER_HAS_EXPIRED("ProvidedIdentifierHasExpired", "The Provided identifier has expired"),
    CUSTOMER_DOES_NOT_EXIST("CustomerDoesNotExist", "The Customer Doesn't exist"),
    LOGIN_DOES_NOT_EXIST("LoginDoesNotExist", "Indicates that the login doesn't exist."),
    IDENTIFIER_DOES_NOT_EXIST("IdentifierDoesNotExist", "Indicates that the reset password identifier doesn't exist.");

    private static Map<String, PasswordResetError> FORMAT_MAP =
            Stream.of(PasswordResetError.values())
                    .collect(toMap(c -> c.getCode(), Function.identity()));
    @Getter
    private String code;
    @Getter
    private String description;

    @JsonCreator
    public static PasswordResetError fromString(String value) {
        return Optional
                .ofNullable(FORMAT_MAP.get(value))
                .orElseThrow(() -> new IllegalArgumentException(value));
    }
}
