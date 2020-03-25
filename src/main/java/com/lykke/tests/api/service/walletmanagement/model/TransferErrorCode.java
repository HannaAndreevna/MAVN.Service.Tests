package com.lykke.tests.api.service.walletmanagement.model;

import static java.util.stream.Collectors.toMap;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.PropertyNamingStrategy.UpperCamelCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.lykke.api.testing.annotations.NetClassName;
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
@NetClassName("TransferErrorCodes")
@PublicApi
public enum TransferErrorCode {
    NONE("None", "no error"),
    INVALID_SENDER_ID("InvalidSenderId", "The source customer id is not valid"),
    INVALID_RECIPIENT_ID("InvalidRecipientId", "The recipient customer id is not valid"),
    SENDER_WALLET_MISSING("SenderWalletMissing", "The source customer does not have a wallet"),
    RECIPIENT_WALLET_MISSING("RecipientWalletMissing", "The recipient customer does not have a wallet"),
    INVALID_AMOUNT("InvalidAmount", "The value for amount is not valid"),
    NOT_ENOUGH_FUNDS("NotEnoughFunds", "The source customer does not have enough tokens"),
    DUPLICATE_REQUEST("DuplicateRequest", "There is a request duplication"),
    INVALID_ADDITIONAL_DATA_FORMAT("InvalidAdditionalDataFormat",
            "The additional data passed in the request was not in a valid format"),
    TARGET_CUSTOMER_NOT_FOUND("TargetCustomerNotFound", "The target customer is not found"),
    SOURCE_CUSTOMER_NOT_FOUND("SourceCustomerNotFound", "The source customer is not found"),
    TRANSFER_SOURCE_AND_TARGET_MUST_BE_DIFFERENT("TransferSourceAndTargetMustBeDifferent",
            "Transfer source and target must be different"),
    SOURCE_CUSTOMER_WALLET_BLOCKED("SourceCustomerWalletBlocked", "The source Customer's Wallet is blocked"),
    TARGET_CUSTOMER_WALLET_BLOCKED("TargetCustomerWalletBlocked", "The recipient Customer's Wallet is blocked");

    private static Map<String, TransferErrorCode> FORMAT_MAP =
            Stream.of(TransferErrorCode.values())
                    .collect(toMap(c -> c.getCode(), Function.identity()));
    @Getter
    private String code;

    @Getter
    private String description;

    @JsonCreator
    public static TransferErrorCode fromString(String value) {
        return Optional
                .ofNullable(FORMAT_MAP.get(value))
                .orElseThrow(() -> new IllegalArgumentException(value));
    }
}
