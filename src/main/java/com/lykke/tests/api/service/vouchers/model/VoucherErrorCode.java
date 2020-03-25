package com.lykke.tests.api.service.vouchers.model;

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
public enum VoucherErrorCode {
    NONE("None", "Unspecified error."),
    CUSTOMER_NOT_FOUND("CustomerNotFound", "Indicates that the customer not found."),
    SPEND_RULE_NOT_FOUND("SpendRuleNotFound", "Indicates that the spend rule not found."),
    INVALID_SPEND_RULE_PRICE("InvalidSpendRulePrice", "Indicates that the spend rule price invalid."),
    INVALID_SPEND_RULE_VERTICAL("InvalidSpendRuleVertical", "Indicates that the spend rule vertical invalid."),
    NO_ENOUGH_TOKENS("NoEnoughTokens", "Indicates that the customer has no enough tokens."),
    CODE_ALREADY_EXIST("CodeAlreadyExist", "Indicates that the voucher code already exist."),
    INVALID_CONVERSION("InvalidConversion", "Indicates that the an error occured during converting voucher price."),
    CUSTOMER_WALLET_BLOCKED("CustomerWalletBlocked", "Indicates that the customer wallet blocked."),
    CUSTOMER_WALLET_DOES_NOT_EXIST("CustomerWalletDoesNotExist",
            "Indicates that the customer wallet does not exist in blockchain."),
    NO_VOUCHERS_IN_STOCK("NoVouchersInStock", "Indicates that there are no vouchers in stock.");

    private static Map<String, VoucherErrorCode> FORMAT_MAP =
            Stream.of(VoucherErrorCode.values())
                    .collect(toMap(c -> c.getCode(), Function.identity()));
    @Getter
    private String code;

    @Getter
    private String comment;

    @JsonCreator
    public static VoucherErrorCode fromString(String value) {
        return Optional
                .ofNullable(FORMAT_MAP.get(value))
                .orElseThrow(() -> new IllegalArgumentException(value));
    }
}
