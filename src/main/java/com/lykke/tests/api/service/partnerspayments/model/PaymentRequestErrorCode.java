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
@NetClassName("PaymentRequestErrorCodes")
public enum PaymentRequestErrorCode {
    NONE("None", "No error"),
    CUSTOMER_DOES_NOT_EXIST("CustomerDoesNotExist", "Customer does not exist in the system"),
    CUSTOMER_WALLET_BLOCKED("CustomerWalletBlocked", "Customer's wallet is blocked"),
    CANNOT_PASS_BOTH_FIAT_AND_TOKENS_AMOUNT("CannotPassBothFiatAndTokensAmount",
            "It is allowed to pass either Fiat or Tokens amount, not both"),
    EITHER_FIAT_OR_TOKENS_AMOUNT_SHOULD_BE_PASSED("EitherFiatOrTokensAmountShouldBePassed",
            "Fiat or Tokens amount should be passed"),
    INVALID_TOKENS_AMOUNT("InvalidTokensAmount", "Tokens amount should be a positive number"),
    INVALID_FIAT_AMOUNT("InvalidFiatAmount", "Fiat amount should be a positive number"),
    @Deprecated
    INVALID_CURRENCY("InvalidCurrency", "Provided currency is not a valid one"),
    INVALID_TOTAL_BILL_AMOUNT("InvalidTotalBillAmount", "Total Bill amount should be a positive number"),
    PARTNER_ID_IS_NOT_VALID_GUID("PartnerIdIsNotAValidGuid", "The provided PartnerId is not a valid Guid"),
    PARTNER_DOES_NOT_EXIST("PartnerDoesNotExist", "Partner does not exist"),
    NO_SUCH_LOCATION_FOR_THIS_PARTNER("NoSuchLocationForThisPartner",
            "The provided locationId does not match any of the partner's"),
    INVALID_TOKENS_OR_CURRENCY_RATE_IN_PARTNER("InvalidTokensOrCurrencyRateInPartner",
            "TokensRate or CurrencyRate in the used Partner is not valid");

    private static Map<String, PaymentRequestErrorCode> FORMAT_MAP =
            Stream.of(PaymentRequestErrorCode.values())
                    .collect(toMap(c -> c.getCode(), Function.identity()));
    @Getter
    private String code;

    @Getter
    private String description;

    @JsonCreator
    public static PaymentRequestErrorCode fromString(String value) {
        return Optional
                .ofNullable(FORMAT_MAP.get(value))
                .orElseThrow(() -> new IllegalArgumentException(value));
    }
}