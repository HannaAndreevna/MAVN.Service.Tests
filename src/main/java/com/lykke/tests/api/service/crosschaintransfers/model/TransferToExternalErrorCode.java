package com.lykke.tests.api.service.crosschaintransfers.model;

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
@NetClassName("TransferToExternalErrorCodes")
public enum TransferToExternalErrorCode {
    NONE("None", "No error"),
    INVALID_AMOUNT("InvalidAmount", "Provided amount is not valid"),
    CUSTOMER_DOES_NOT_EXIST("CustomerDoesNotExist", "Customer does not exist in the system"),
    CUSTOMER_WALLET_BLOCKED("CustomerWalletBlocked",
            "Customer's wallet is blocked so he is not able to transfer tokens"),
    CUSTOMER_ID_IS_NOT_AVALID_GUID("CustomerIdIsNotAValidGuid", "Provided customer id is not a valid guid"),
    CUSTOMER_WALLET_MISSING("CustomerWalletMissing", "Customer does not have a wallet in the system"),
    NOT_ENOUGH_BALANCE("NotEnoughBalance", "Customer does not have enough balance"),
    WALLET_IS_NOT_LINKED("WalletIsNotLinked", "Customer's internal wallet is not linked to a public one");

    private static Map<String, TransferToExternalErrorCode> FORMAT_MAP =
            Stream.of(TransferToExternalErrorCode.values())
                    .collect(toMap(c -> c.getCode(), Function.identity()));
    @Getter
    private String code;

    @Getter
    private String comment;

    @JsonCreator
    public static TransferToExternalErrorCode fromString(String value) {
        return Optional
                .ofNullable(FORMAT_MAP.get(value))
                .orElseThrow(() -> new IllegalArgumentException(value));
    }
}
