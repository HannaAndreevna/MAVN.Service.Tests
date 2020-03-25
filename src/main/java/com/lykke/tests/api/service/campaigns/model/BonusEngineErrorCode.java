package com.lykke.tests.api.service.campaigns.model;

import static java.util.stream.Collectors.toMap;

import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public enum BonusEngineErrorCode {
    NONE("None"),
    ENTITY_NOT_FOUND("EntityNotFound"),
    GUID_CANNOT_BE_PARSED("GuidCanNotBeParsed"),
    ENTITY_NOT_VALID("EntityNotValid");

    @Getter
    private String code;

    private static Map<String, BonusEngineErrorCode> FORMAT_MAP =
            Stream.of(BonusEngineErrorCode.values())
                    .collect(toMap(c -> c.getCode(), Function.identity()));

    @JsonCreator
    public static BonusEngineErrorCode fromString(String value) {
        return Optional
                .ofNullable(FORMAT_MAP.get(value))
                .orElseThrow(() -> new IllegalArgumentException(value));
    }
}
