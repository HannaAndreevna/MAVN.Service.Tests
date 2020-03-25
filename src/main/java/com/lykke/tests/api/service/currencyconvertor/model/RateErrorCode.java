package com.lykke.tests.api.service.currencyconvertor.model;

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
public enum RateErrorCode {
    NONE("None", "Unspecified error."),
    RATE_ALREADY_EXISTS("RateAlreadyExists", "Indicates that the currency rate already exists."),
    RATE_DOES_NOT_EXIST("RateDoesNotExist", "Indicates that the currency rate does not exist.");

    private static Map<String, RateErrorCode> FORMAT_MAP =
            Stream.of(RateErrorCode.values())
                    .collect(toMap(c -> c.getCode(), Function.identity()));
    @Getter
    private String code;

    @Getter
    private String description;

    @JsonCreator
    public static RateErrorCode fromString(String value) {
        return Optional
                .ofNullable(FORMAT_MAP.get(value))
                .orElseThrow(() -> new IllegalArgumentException(value));
    }
}
