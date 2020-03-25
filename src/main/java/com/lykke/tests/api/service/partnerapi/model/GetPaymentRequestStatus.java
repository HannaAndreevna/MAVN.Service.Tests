package com.lykke.tests.api.service.partnerapi.model;

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
public enum GetPaymentRequestStatus {
    PAYMENT_REQUEST_NOT_FOUND("PaymentRequestNotFound"),
    PENDING_CUSTOMER_CONFIRMATION("PendingCustomerConfirmation"),
    REJECTED_BY_CUSTOMER("RejectedByCustomer"),
    PENDING_PARTNER_CONFIRMATION("PendingPartnerConfirmation"),
    CANCELLED_BY_PARTNER("CancelledByPartner"),
    PAYMENT_EXECUTED("PaymentExecuted"),
    OPERATION_FAILED("OperationFailed"),
    REQUEST_EXPIRED("RequestExpired"),
    PAYMENT_EXPIRED("PaymentExpired");

    private static Map<String, GetPaymentRequestStatus> FORMAT_MAP =
            Stream.of(GetPaymentRequestStatus.values())
                    .collect(toMap(c -> c.getCode(), Function.identity()));
    @Getter
    private String code;

    @JsonCreator
    public static GetPaymentRequestStatus fromString(String value) {
        return Optional
                .ofNullable(FORMAT_MAP.get(value))
                .orElseThrow(() -> new IllegalArgumentException(value));
    }
}
