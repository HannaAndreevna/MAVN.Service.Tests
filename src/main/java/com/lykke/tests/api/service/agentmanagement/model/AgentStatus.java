package com.lykke.tests.api.service.agentmanagement.model;

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
public enum AgentStatus {
    NONE("None"),
    NOT_AGENT("NotAgent"),
    PENDING_KYA("PendingKya"),
    APPROVED_AGENT("ApprovedAgent");

    private static Map<String, AgentStatus> FORMAT_MAP =
            Stream.of(AgentStatus.values())
                    .collect(toMap(c -> c.getStatus(), Function.identity()));
    @Getter
    private String status;

    @JsonCreator
    public static AgentStatus fromString(String value) {
        return Optional
                .ofNullable(FORMAT_MAP.get(value))
                .orElseThrow(() -> new IllegalArgumentException(value));
    }
}
