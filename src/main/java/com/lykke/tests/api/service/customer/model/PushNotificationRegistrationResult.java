package com.lykke.tests.api.service.customer.model;

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
public enum PushNotificationRegistrationResult {
    OK("Ok"),
    INFOBIP_PUSH_REGISTRATION_ALREADY_EXISTS("InfobipPushRegistrationAlreadyExists"),
    FIREBASE_TOKEN_ALREADY_EXISTS("FirebaseTokenAlreadyExists"),
    APPLE_TOKEN_ALREADY_EXISTS("AppleTokenAlreadyExists");

    private static Map<String, PushNotificationRegistrationResult> FORMAT_MAP =
            Stream.of(PushNotificationRegistrationResult.values())
                    .collect(toMap(c -> c.getCode(), Function.identity()));
    @Getter
    private String code;

    @JsonCreator
    public static PushNotificationRegistrationResult fromString(String value) {
        return Optional
                .ofNullable(FORMAT_MAP.get(value))
                .orElseThrow(() -> new IllegalArgumentException(value));
    }
}
