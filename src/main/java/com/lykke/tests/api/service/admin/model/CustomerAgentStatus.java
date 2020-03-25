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
public enum CustomerAgentStatus {
    NOT_AGENT("NotAgent", "Indicates that the customer is not an agent."),
    REJECTED("Rejected", "Indicates that the customer registration as an agent was rejected."),
    APPROVED_AGENT("ApprovedAgent",
            "Indicates that the customer registered as an agent and successfully complete KYA.");

    private static Map<String, CustomerAgentStatus> FORMAT_MAP =
            Stream.of(CustomerAgentStatus.values())
                    .collect(toMap(r -> r.getCode(), Function.identity()));
    @Getter
    private String code;

    @Getter
    private String comment;

    @JsonCreator
    public static CustomerAgentStatus fromString(String value) {
        return Optional
                .ofNullable(FORMAT_MAP.get(value))
                .orElseThrow(() -> new IllegalArgumentException(String.valueOf(value)));
    }
}
