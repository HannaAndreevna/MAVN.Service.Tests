package com.lykke.tests.api.service.admin.model;

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
public enum CustomerWalletActivityStatus {
    ACTIVE("Active", 0),
    BLOCKED("Blocked", 1);

    private static Map<Integer, CustomerWalletActivityStatus> FORMAT_MAP =
            Stream.of(CustomerWalletActivityStatus.values())
                    .collect(toMap(r -> r.getCode(), Function.identity()));
    @Getter
    private String type;

    @Getter
    private int code;

    @JsonCreator
    public static CustomerWalletActivityStatus fromString(Integer value) {
        return Optional
                .ofNullable(FORMAT_MAP.get(value))
                .orElseThrow(() -> new IllegalArgumentException(String.valueOf(value)));
    }
}
