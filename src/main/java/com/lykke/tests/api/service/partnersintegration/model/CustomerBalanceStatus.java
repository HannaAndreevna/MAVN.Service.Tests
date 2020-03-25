package com.lykke.tests.api.service.partnersintegration.model;

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
public enum CustomerBalanceStatus {
    OK("OK"),
    CUSTOMER_NOT_FOUND("CustomerNotFound"),
    PARTNER_NOT_FOUND("PartnerNotFound"),
    LOCATION_NOT_FOUND("LocationNotFound"),
    INVALID_CURRENCY("InvalidCurrency");

    private static Map<String, CustomerBalanceStatus> FORMAT_MAP =
            Stream.of(CustomerBalanceStatus.values())
                    .collect(toMap(c -> c.getCode(), Function.identity()));
    @Getter
    private String code;

    @JsonCreator
    public static CustomerBalanceStatus fromString(String value) {
        return Optional
                .ofNullable(FORMAT_MAP.get(value))
                .orElseThrow(() -> new IllegalArgumentException(value));
    }
}
