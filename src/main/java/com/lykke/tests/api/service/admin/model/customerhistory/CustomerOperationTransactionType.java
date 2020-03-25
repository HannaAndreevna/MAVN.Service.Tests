package com.lykke.tests.api.service.admin.model.customerhistory;

import static java.util.stream.Collectors.toMap;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.PropertyNamingStrategy.UpperCamelCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

import com.lykke.tests.api.service.partnerapi.model.BonusCustomerStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@JsonNaming(UpperCamelCaseStrategy.class)
public enum CustomerOperationTransactionType {
    P_2_P("P2P"),
    EARN("Earn"),
    BURN("Burn"),
    BURN_CANCELLED("BurnCancelled"),
    REFERRAL_STAKE("ReferralStake"),
    RELEASED_REFERRAL_STAKE("ReleasedReferralStake");

    private static Map<String, CustomerOperationTransactionType> FORMAT_MAP =
            Stream.of(CustomerOperationTransactionType.values())
                    .collect(toMap(c -> c.getCode(), Function.identity()));
    @Getter
    private String code;

    @JsonCreator
    public static CustomerOperationTransactionType fromString(String value) {
        return Optional
                .ofNullable(FORMAT_MAP.get(value))
                .orElseThrow(() -> new IllegalArgumentException(value));
    }
}
