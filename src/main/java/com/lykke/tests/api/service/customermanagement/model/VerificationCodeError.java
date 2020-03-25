package com.lykke.tests.api.service.customermanagement.model;

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
public enum VerificationCodeError {
    NONE("None"),
    ALREADY_VERIFIED("AlreadyVerified"),
    VERIFICATION_CODE_DOES_NOT_EXIST("VerificationCodeDoesNotExist"),
    VERIFICATION_CODE_MISMATCH("VerificationCodeMismatch"),
    VERIFICATION_CODE_EXPIRED("VerificationCodeExpired"),
    CUSTOMER_DOES_NOT_EXIST("CustomerDoesNotExist"),
    REACHED_MAXIMUM_REQUEST_FOR_PERIOD("ReachedMaximumRequestForPeriod");

    private static Map<String, VerificationCodeError> FORMAT_MAP =
            Stream.of(VerificationCodeError.values())
                    .collect(toMap(c -> c.getStatus(), Function.identity()));
    @Getter
    private String status;

    @JsonCreator
    public static VerificationCodeError fromString(String value) {
        return Optional
                .ofNullable(FORMAT_MAP.get(value))
                .orElseThrow(() -> new IllegalArgumentException(value));
    }
}
