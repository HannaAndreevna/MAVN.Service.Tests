package com.lykke.tests.api.service.customer.model.wallets;

import static java.util.stream.Collectors.toMap;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.PropertyNamingStrategy.UpperCamelCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
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
public enum PublicAddressLinkingStatus {
    NOT_LINKED("NotLinked"),
    PENDING_CUSTOMER_APPROVAL("PendingCustomerApproval"),
    PENDING_CONFIRMATION_IN_BLOCKCHAIN("PendingConfirmationInBlockchain"),
    LINKED("Linked");

    private static Map<String, PublicAddressLinkingStatus> FORMAT_MAP =
            Stream.of(PublicAddressLinkingStatus.values())
                    .collect(toMap(c -> c.getCode(), Function.identity()));
    @Getter
    private String code;

    @JsonCreator
    public static PublicAddressLinkingStatus fromString(String value) {
        return Optional
                .ofNullable(FORMAT_MAP.get(value))
                .orElseThrow(() -> new IllegalArgumentException(value));
    }
}
