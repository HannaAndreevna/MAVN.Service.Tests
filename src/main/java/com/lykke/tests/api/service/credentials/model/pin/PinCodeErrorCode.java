package com.lykke.tests.api.service.credentials.model.pin;

import static java.util.stream.Collectors.toMap;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.PropertyNamingStrategy.UpperCamelCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.lykke.api.testing.annotations.NetClassName;
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
@NetClassName("PinCodeErrorCodes")
public enum PinCodeErrorCode {
    NONE("None", "No error"),
    CUSTOMER_DOES_NOT_EXIST("CustomerDoesNotExist", "The customer does not exist"),
    INVALID_PIN("InvalidPin", "The provided pin is not valid"),
    PIN_ALREADY_SET("PinAlreadySet", "Pin is already set for this customer"),
    PIN_IS_NOT_SET("PinIsNotSet", "Pin is not set for customer"),
    PIN_CODE_MISMATCH("PinCodeMismatch", "Pin code mismatch");

    private static Map<String, PinCodeErrorCode> FORMAT_MAP =
            Stream.of(PinCodeErrorCode.values())
                    .collect(toMap(c -> c.getCode(), Function.identity()));
    @Getter
    private String code;
    @Getter
    private String comment;

    @JsonCreator
    public static PinCodeErrorCode fromString(String value) {
        return Optional
                .ofNullable(FORMAT_MAP.get(value))
                .orElseThrow(() -> new IllegalArgumentException(value));
    }
}
