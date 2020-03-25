package com.lykke.tests.api.service.partnersintegration.model;

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
public enum BonusCustomerStatus {
    OK("OK"),
    CUSTOMER_NOT_FOUND("CustomerNotFound"),
    PARTNER_NOT_FOUND("PartnerNotFound"),
    LOCATION_NOT_FOUND("LocationNotFound"),
    CUSTOMER_ID_DOES_NOT_MATCH_EMAIL("CustomerIdDoesNotMatchEmail"),
    INVALID_CURRENCY("InvalidCurrency"),
    INVALID_FIAT_AMOUNT("InvalidFiatAmount"),
    INVALID_PAYMENT_TIMESTAMP("InvalidPaymentTimestamp"),
    TECHNICAL_PROBLEM("TechnicalProblem");

    private static Map<String, BonusCustomerStatus> FORMAT_MAP =
            Stream.of(BonusCustomerStatus.values())
                    .collect(toMap(c -> c.getCode(), Function.identity()));
    @Getter
    private String code;

    @JsonCreator
    public static BonusCustomerStatus fromString(String value) {
        return Optional
                .ofNullable(FORMAT_MAP.get(value))
                .orElseThrow(() -> new IllegalArgumentException(value));
    }
}
