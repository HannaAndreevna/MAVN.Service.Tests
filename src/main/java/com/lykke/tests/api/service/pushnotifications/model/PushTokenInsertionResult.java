package com.lykke.tests.api.service.pushnotifications.model;

import static java.util.stream.Collectors.toMap;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
@JsonIgnoreProperties(ignoreUnknown = true)
public enum PushTokenInsertionResult {
    OK("Ok"),
    INFOBIP_TOKEN_ALREADY_EXISTS("InfobipTokenAlreadyExists"),
    FIREBASE_TOKEN_ALREADY_EXISTS("FirebaseTokenAlreadyExists"),
    APPLE_TOKEN_ALREADY_EXISTS("AppleTokenAlreadyExists");

    private static Map<String, PushTokenInsertionResult> FORMAT_MAP =
            Stream.of(PushTokenInsertionResult.values())
                    .collect(toMap(c -> c.getCode(), Function.identity()));
    @Getter
    private String code;

    @JsonCreator
    public static PushTokenInsertionResult fromString(String value) {
        return Optional
                .ofNullable(FORMAT_MAP.get(value))
                .orElseThrow(() -> new IllegalArgumentException(value));
    }
}
