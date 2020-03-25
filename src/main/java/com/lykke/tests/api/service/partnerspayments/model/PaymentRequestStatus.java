package com.lykke.tests.api.service.partnerspayments.model;

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
public enum PaymentRequestStatus {
    CREATED("Created", "Payment is created by receptionist"),
    REJECTED_BY_CUSTOMER("RejectedByCustomer", "Payment is rejected by the customer"),
    TOKENS_TRANSFER_STARTED("TokensTransferStarted", "Tokens are being transferred"),
    TOKENS_TRANSFER_SUCCEEDED("TokensTransferSucceeded", "Tokens transfer is successful"),
    TOKENS_TRANSFER_FAILED("TokensTransferFailed", "Tokens transfer failed"),
    TOKENS_BURN_STARTED("TokensBurnStarted", "Tokens are being burned"),
    TOKENS_REFUND_STARTED("TokensRefundStarted", "Tokens are being refunded"),
    TOKENS_BURN_SUCCEEDED("TokensBurnSucceeded", "Tokens were successfully burned"),
    TOKENS_BURN_FAILED("TokensBurnFailed", "Tokens burn failed"),
    TOKENS_REFUND_SUCCEEDED("TokensRefundSucceeded", "Tokens were successfully refunded"),
    TOKENS_REFUND_FAILED("TokensRefundFailed", "Tokens refund failed"),
    REQUEST_EXPIRED("RequestExpired", "The request has expired"),
    EXPIRATION_TOKENS_REFUND_STARTED("ExpirationTokensRefundStarted",
            "Tokens refund started because the request has expired"),
    EXPIRATION_TOKENS_REFUND_SUCCEEDED("ExpirationTokensRefundSucceeded", "Expiration tokens refund succeeded"),
    EXPIRATION_TOKENS_REFUND_FAILED("ExpirationTokensRefundFailed", "Expiration tokens refund failed"),
    CANCELLED_BY_PARTNER("CancelledByPartner", "Cancelled by partner");

    private static Map<String, PaymentRequestStatus> FORMAT_MAP =
            Stream.of(PaymentRequestStatus.values())
                    .collect(toMap(c -> c.getCode(), Function.identity()));
    @Getter
    private String code;

    @Getter
    private String comment;

    @JsonCreator
    public static PaymentRequestStatus fromString(String value) {
        return Optional
                .ofNullable(FORMAT_MAP.get(value))
                .orElseThrow(() -> new IllegalArgumentException(value));
    }
}
