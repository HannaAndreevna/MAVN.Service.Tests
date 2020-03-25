package com.lykke.tests.api.service.admin.model.admins;

import static java.util.stream.Collectors.toMap;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.PropertyNamingStrategy.UpperCamelCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.lykke.api.testing.annotations.PublicApi;
import com.lykke.tests.api.service.customermanagement.model.VerificationCodeError;
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
public enum SuggestedValueType {
    DEPARTMENT("Department"),
    COMPANY("Company");

    private static Map<String, SuggestedValueType> FORMAT_MAP =
            Stream.of(SuggestedValueType.values())
                    .collect(toMap(c -> c.getStatus(), Function.identity()));
    @Getter
    private String status;

    @JsonCreator
    public static SuggestedValueType fromString(String value) {
        return Optional
                .ofNullable(FORMAT_MAP.get(value))
                .orElseThrow(() -> new IllegalArgumentException(value));
    }
}
