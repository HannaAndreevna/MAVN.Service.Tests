package com.lykke.tests.api.service.admin.model;

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
public enum RewardType {
    NONE("None"),
    FIXED("Fixed"),
    PERCENTAGE("Percentage");

    private static Map<String, RewardType> FORMAT_MAP =
            Stream.of(RewardType.values())
                    .collect(toMap(r -> r.getType(), Function.identity()));
    @Getter
    private String type;

    @JsonCreator
    public static RewardType fromString(String value) {
        return Optional
                .ofNullable(FORMAT_MAP.get(value))
                .orElseThrow(() -> new IllegalArgumentException(value));
    }
}
