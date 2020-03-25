package com.lykke.tests.api.service.privateblockchainfacade.model;

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
public enum CustomerWalletCreationError {
    NONE("None"),
    ALREADY_CREATED("AlreadyCreated"),
    INVALID_CUSTOMER_ID("InvalidCustomerId");

    @Getter
    private String code;

    private static Map<String, CustomerWalletCreationError> FORMAT_MAP =
            Stream.of(CustomerWalletCreationError.values())
                    .collect(toMap(c -> c.getCode(), Function.identity()));

    @JsonCreator
    public static CustomerWalletCreationError fromString(String value) {
        return Optional
                .ofNullable(FORMAT_MAP.get(value))
                .orElseThrow(() -> new IllegalArgumentException(value));
    }
}
