package com.lykke.tests.api.service.mavnpropertyintegration.model;

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
public enum ProcessedAgentStatus {
    KYA_APPROVED("KyaApproved"),
    KYA_REJECTED("KyaRejected"),
    KYA_BLOCKED("KyaBlocked");

    private static Map<String, ProcessedAgentStatus> FORMAT_MAP =
            Stream.of(ProcessedAgentStatus
                    .values())
                    .collect(toMap(c -> c.getCode(), Function.identity()));
    @Getter
    private String code;

    @JsonCreator
    public static ProcessedAgentStatus fromString(
            String value) {
        return Optional
                .ofNullable(FORMAT_MAP.get(value))
                .orElseThrow(() -> new IllegalArgumentException(value));
    }
}
