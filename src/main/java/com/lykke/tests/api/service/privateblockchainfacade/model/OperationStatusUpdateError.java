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
public enum OperationStatusUpdateError {
    NONE("None", "Operation has  been updated without errors"),
    OPERATION_NOT_FOUND("OperationNotFound", "Operation was not found"),
    INVALID_STATUS("InvalidStatus", "Operation current status doesn't allow update to the desired status"),
    INVALID_TRANSACTION_HASH("InvalidTransactionHash", "Operation transaction hash has invalid value"),
    DUPLICATE_TRANSACTION_HASH("DuplicateTransactionHash", "Transaction hash already used by another operation"),
    OPERATION_ID_IS_NULL("OperationIdIsNull", "The transaction in blockchain has an empty related operation id"),
    UNSUPPORTED_OPERATION_STATUS("UnsupportedOperationStatus",
            "The operation type in blockchain can't be mapped to operation type in domain");

    private static Map<String, OperationStatusUpdateError> FORMAT_MAP =
            Stream.of(OperationStatusUpdateError.values())
                    .collect(toMap(c -> c.getCode(), Function.identity()));
    @Getter
    private String code;
    @Getter
    private String comment;

    @JsonCreator
    public static OperationStatusUpdateError fromString(String value) {
        return Optional
                .ofNullable(FORMAT_MAP.get(value))
                .orElseThrow(() -> new IllegalArgumentException(value));
    }
}
