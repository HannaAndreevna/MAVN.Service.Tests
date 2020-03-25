package com.lykke.tests.api.service.crosschainwalletlinker.model;

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
public enum ConfigurationItemUpdateError {
    NONE("None", "No errors"),
    TYPE_NOT_RECOGNIZED("TypeNotRecognized", "The configuration item type provided is not expected by server");

    private static Map<String, ConfigurationItemUpdateError> FORMAT_MAP =
            Stream.of(ConfigurationItemUpdateError.values())
                    .collect(toMap(c -> c.getCode(), Function.identity()));
    @Getter
    private String code;

    @Getter
    private String comment;

    @JsonCreator
    public static ConfigurationItemUpdateError fromString(String value) {
        return Optional
                .ofNullable(FORMAT_MAP.get(value))
                .orElseThrow(() -> new IllegalArgumentException(value));
    }
}
