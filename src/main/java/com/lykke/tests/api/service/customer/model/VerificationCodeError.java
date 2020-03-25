package com.lykke.tests.api.service.customer.model;

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
public enum VerificationCodeError {
    NONE("None", null),
    ALREADY_VERIFIED("AlreadyVerified", ""),
    VERIFICATION_CODE_DOES_NOT_EXIST("VerificationCodeDoesNotExist", ""),
    VERIFICATION_CODE_MISMATCH("VerificationCodeMismatch", ""),
    VERIFICATION_CODE_EXPIRED("VerificationCodeExpired", ""),
    CUSTOMER_DOES_NOT_EXIST("NoCustomerWithSuchEmail", "Customer with such email does not exist"),
    REACHED_MAXIMUM_REQUEST_FOR_PERIOD("ReachedMaximumRequestForPeriod",
            "Reached maximum requests for period for this Customer");

    private static Map<String, VerificationCodeError> FORMAT_MAP =
            Stream.of(VerificationCodeError.values())
                    .collect(toMap(verificationCodeError -> verificationCodeError.getCode(), Function.identity()));
    @Getter
    private String code;
    @Getter
    private String message;

    @JsonCreator
    public static VerificationCodeError fromString(String value) {
        return Optional
                .ofNullable(FORMAT_MAP.get(value))
                .orElseThrow(() -> new IllegalArgumentException(value));
    }
}
