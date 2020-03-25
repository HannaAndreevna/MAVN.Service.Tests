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
public enum PaymentCreateStatus {
    OK("OK"),
    CUSTOMER_NOT_FOUND("CustomerNotFound"),
    CUSTOMER_IS_BLOCKED("CustomerIsBlocked"),
    PARTNER_NOT_FOUND("PartnerNotFound"),
    LOCATION_NOT_FOUND("LocationNotFound"),
    INVALID_CURRENCY("InvalidCurrency"),
    CANNOT_PASS_BOTH_FIAT_AND_TOKENS_AMOUNT("CannotPassBothFiatAndTokensAmount"),
    EITHER_FIAT_OR_TOKENS_AMOUNT_SHOULD_BE_PASSED("EitherFiatOrTokensAmountShouldBePassed"),
    INVALID_TOKENS_AMOUNT("InvalidTokensAmount"),
    INVALID_FIAT_AMOUNT("InvalidFiatAmount"),
    INVALID_TOTAL_BILL_AMOUNT("InvalidTotalBillAmount"),
    INTERNAL_TECHNICAL_ERROR("InternalTechnicalError");

    private static Map<String, PaymentCreateStatus> FORMAT_MAP =
            Stream.of(PaymentCreateStatus.values())
                    .collect(toMap(c -> c.getCode(), Function.identity()));
    @Getter
    private String code;

    @JsonCreator
    public static PaymentCreateStatus fromString(String value) {
        return Optional
                .ofNullable(FORMAT_MAP.get(value))
                .orElseThrow(() -> new IllegalArgumentException(value));
    }
}
