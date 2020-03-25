package com.lykke.tests.api.service.customer.model.spendrule;

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
@NetClassName("ConversionRateErrorCodes")
public enum ConversionRateErrorCode {
    NONE("None"),
    INVALID_CUSTOMER_ID("InvalidCustomerId"),
    INVALID_EARN_RULE_ID("InvalidEarnRuleId"),
    INVALID_BURN_RULE_ID("InvalidBurnRuleId"),
    INVALID_PARTNER_ID("InvalidPartnerId"),
    PARTNER_NOT_FOUND("PartnerNotFound"),
    CUSTOMER_NOT_FOUND("CustomerNotFound"),
    EARN_RULE_NOT_FOUND("EarnRuleNotFound"),
    SPEND_RULE_NOT_FOUND("SpendRuleNotFound"),
    CONVERSION_RATE_NOT_FOUND("ConversionRateNotFound");

    private static Map<String, ConversionRateErrorCode> FORMAT_MAP =
            Stream.of(ConversionRateErrorCode.values())
                    .collect(toMap(c -> c.getCode(), Function.identity()));
    @Getter
    private String code;

    @JsonCreator
    public static ConversionRateErrorCode fromString(String value) {
        return Optional
                .ofNullable(FORMAT_MAP.get(value))
                .orElseThrow(() -> new IllegalArgumentException(value));
    }
}
