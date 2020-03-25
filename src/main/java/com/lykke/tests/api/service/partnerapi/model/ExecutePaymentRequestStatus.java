package com.lykke.tests.api.service.partnerapi.model;

import static java.util.stream.Collectors.toMap;

import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public enum ExecutePaymentRequestStatus {
    OK("OK"),
    PAYMENT_REQUEST_NOT_FOUND("PaymentRequestNotFound"),
    PAYMENT_REQUEST_NOT_VALID("PaymentRequestNotValid");

    private static Map<String, ExecutePaymentRequestStatus> FORMAT_MAP =
            Stream.of(ExecutePaymentRequestStatus.values())
                    .collect(toMap(c -> c.getCode(), Function.identity()));
    @Getter
    private String code;

    @JsonCreator
    public static ExecutePaymentRequestStatus fromString(String value) {
        return Optional
                .ofNullable(FORMAT_MAP.get(value))
                .orElseThrow(() -> new IllegalArgumentException(value));
    }
}
