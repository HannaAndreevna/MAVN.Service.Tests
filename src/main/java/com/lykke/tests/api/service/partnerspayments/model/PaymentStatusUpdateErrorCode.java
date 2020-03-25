package com.lykke.tests.api.service.partnerspayments.model;

import static java.util.stream.Collectors.toMap;

import com.fasterxml.jackson.annotation.JsonCreator;
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
@NetClassName("PaymentStatusUpdateErrorCodes")
public enum PaymentStatusUpdateErrorCode {
    NONE("None"),
    INVALID_SENDER_ID("InvalidSenderId"),
    INVALID_RECIPIENT_ID("InvalidRecipientId"),
    SENDER_WALLET_MISSING("SenderWalletMissing"),
    RECIPIENT_WALLET_MISSING("RecipientWalletMissing"),
    INVALID_AMOUNT("InvalidAmount"),
    NOT_ENOUGH_FUNDS("NotEnoughFunds"),
    DUPLICATE_REQUEST("DuplicateRequest"),
    INVALID_ADDITIONAL_DATA_FORMAT("InvalidAdditionalDataFormat"),
    PAYMENT_DOES_NOT_EXIST("PaymentDoesNotExist"),
    CUSTOMER_ID_DOES_NOT_MATCH("CustomerIdDoesNotMatch"),
    PAYMENT_IS_IN_INVALID_STATUS("PaymentIsInInvalidStatus"),
    CUSTOMER_WALLET_IS_BLOCKED("CustomerWalletIsBlocked");

    private static Map<String, PaymentStatusUpdateErrorCode> FORMAT_MAP =
            Stream.of(PaymentStatusUpdateErrorCode.values())
                    .collect(toMap(c -> c.getCode(), Function.identity()));
    @Getter
    private String code;

    @JsonCreator
    public static PaymentStatusUpdateErrorCode fromString(String value) {
        return Optional
                .ofNullable(FORMAT_MAP.get(value))
                .orElseThrow(() -> new IllegalArgumentException(value));
    }
}
