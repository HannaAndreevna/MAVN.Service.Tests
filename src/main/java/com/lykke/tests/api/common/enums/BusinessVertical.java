package com.lykke.tests.api.common.enums;

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
public enum BusinessVertical {
    HOSPITALITY("Hospitality"),
    REAL_ESTATE("RealEstate"),
    RETAIL("Retail");

    private static Map<String, BusinessVertical> FORMAT_MAP =
            Stream.of(BusinessVertical.values())
                    .collect(toMap(c -> c.getCode(), Function.identity()));
    @Getter
    private String code;

    @JsonCreator
    public static BusinessVertical fromString(String value) {
        return Optional
                .ofNullable(FORMAT_MAP.get(value))
                .orElseThrow(() -> new IllegalArgumentException(value));
    }
}
