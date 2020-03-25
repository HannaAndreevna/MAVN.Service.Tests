package com.lykke.tests.api.service.eligibilityengine.model;

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
public enum ConversionSource {
    BURN_RULE("BurnRule", "The conversion rate is taken from a burn rule."),
    EARN_RULE("EarnRule", "The conversion rate is taken from a earn rule.\n"),
    PARTNER("Partner", "The conversion rate is taken from a partner profile."),
    GLOBAL("Global", "The conversion rate is taken from the global conversion rate."),
    CONDITION("Condition", "The conversion rate is taken from a condition.");

    private static Map<String, ConversionSource> FORMAT_MAP =
            Stream.of(ConversionSource.values())
                    .collect(toMap(c -> c.getCode(), Function.identity()));
    @Getter
    private String code;

    @Getter
    private String comment;

    @JsonCreator
    public static ConversionSource fromString(String value) {
        return Optional
                .ofNullable(FORMAT_MAP.get(value))
                .orElseThrow(() -> new IllegalArgumentException(value));
    }
}
