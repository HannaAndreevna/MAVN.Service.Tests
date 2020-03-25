package com.lykke.tests.api.service.partnerapi.model;

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
public enum SendMessageStatus {
    OK("OK"),
    CUSTOMER_NOT_FOUND("CustomerNotFound"),
    CUSTOMER_IS_BLOCKED("CustomerIsBlocked"),
    PARTNER_NOT_FOUND("PartnerNotFound"),
    LOCATION_NOT_FOUND("LocationNotFound");

    private static Map<String, SendMessageStatus> FORMAT_MAP =
            Stream.of(SendMessageStatus.values())
                    .collect(toMap(c -> c.getCode(), Function.identity()));
    @Getter
    private String code;

    @JsonCreator
    public static SendMessageStatus fromString(String value) {
        return Optional
                .ofNullable(FORMAT_MAP.get(value))
                .orElseThrow(() -> new IllegalArgumentException(value));
    }
}
