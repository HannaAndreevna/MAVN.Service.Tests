package com.lykke.tests.api.service.tiers.model;

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
public enum CustomerTierResponseErrorCode {
    NONE("None"),
    CUSTOMER_NOT_FOUND("CustomerNotFound");

    private static Map<String, CustomerTierResponseErrorCode> FORMAT_MAP =
            Stream.of(CustomerTierResponseErrorCode.values())
                    .collect(toMap(c -> c.getCode(), Function.identity()));
    @Getter
    private String code;

    @JsonCreator
    public static CustomerTierResponseErrorCode fromString(String value) {
        return Optional
                .ofNullable(FORMAT_MAP.get(value))
                .orElseThrow(() -> new IllegalArgumentException(value));
    }
}
