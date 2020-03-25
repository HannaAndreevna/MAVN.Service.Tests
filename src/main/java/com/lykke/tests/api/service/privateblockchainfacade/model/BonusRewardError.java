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
public enum BonusRewardError {
    NONE("None", "No errors"),
    DUPLICATE_REQUEST("DuplicateRequest", "The same reward request has already been received "),
    INVALID_CUSTOMER_ID("InvalidCustomerId", "The customer id value is not valid"),
    INVALID_AMOUNT("InvalidAmount", "Invalid amount"),
    CUSTOMER_WALLET_MISSING("CustomerWalletMissing", "The customer wallet has not been created yet"),
    MISSING_BONUS_REASON("MissingBonusReason", "The bonus reason missed."),
    INVALID_CAMPAIGN_ID("InvalidCampaignId", "The campaign identifier is not valid.");

    private static Map<String, BonusRewardError> FORMAT_MAP =
            Stream.of(BonusRewardError.values())
                    .collect(toMap(c -> c.getCode(), Function.identity()));
    @Getter
    private String code;

    @Getter
    private String comment;

    @JsonCreator
    public static BonusRewardError fromString(String value) {
        return Optional
                .ofNullable(FORMAT_MAP.get(value))
                .orElseThrow(() -> new IllegalArgumentException(value));
    }
}
