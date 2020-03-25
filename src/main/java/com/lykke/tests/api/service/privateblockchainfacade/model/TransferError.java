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
public enum TransferError {
    NONE("None"),
    INVALID_SENDER_ID("InvalidSenderId"),
    INVALID_RECEIVER_ID("InvalidRecipientId"),
    SENDER_WALLET_MISSING("SenderWalletMissing"),
    RECIPIENT_WALLET_MISSING("RecipientWalletMissing"),
    INVALID_AMOUNT("InvalidAmount"),
    NOT_ENOUGH_FUNDS("NotEnoughFunds"),
    DUPLICATE_REQUEST("DuplicateRequest"),
    INVALID_ADDITIONAL_DATA_FORMAT("InvalidAdditionalDataFormat");

    private static Map<String, TransferError> FORMAT_MAP =
            Stream.of(TransferError.values())
                    .collect(toMap(c -> c.getCode(), Function.identity()));
    @Getter
    private String code;

    @JsonCreator
    public static TransferError fromString(String value) {
        return Optional
                .ofNullable(FORMAT_MAP.get(value))
                .orElseThrow(() -> new IllegalArgumentException(value));
    }
}
