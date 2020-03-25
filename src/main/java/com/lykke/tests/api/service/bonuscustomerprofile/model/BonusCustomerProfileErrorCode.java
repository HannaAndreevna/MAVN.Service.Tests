package com.lykke.tests.api.service.bonuscustomerprofile.model;

import static java.util.stream.Collectors.toMap;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.PropertyNamingStrategy.UpperCamelCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.lykke.api.testing.annotations.NetClassName;
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
@NetClassName("BonusCustomerProfileErrorCodes")
public enum BonusCustomerProfileErrorCode {
    NONE("None"),
    ENTITY_NOT_FOUND("EntityNotFound"),
    GUID_CAN_NOT_BE_PARSED("GuidCanNotBeParsed"),
    ENTITY_NOT_VALID("EntityNotValid");

    private static Map<String, BonusCustomerProfileErrorCode> FORMAT_MAP =
            Stream.of(BonusCustomerProfileErrorCode.values())
                    .collect(toMap(c -> c.getCode(), Function.identity()));
    @Getter
    private String code;

    @JsonCreator
    public static BonusCustomerProfileErrorCode fromString(String value) {
        return Optional
                .ofNullable(FORMAT_MAP.get(value))
                .orElseThrow(() -> new IllegalArgumentException(value));
    }
}
