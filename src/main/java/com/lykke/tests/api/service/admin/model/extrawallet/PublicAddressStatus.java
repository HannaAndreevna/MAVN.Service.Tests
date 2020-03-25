package com.lykke.tests.api.service.admin.model.extrawallet;

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
public enum PublicAddressStatus {
    NOT_LINKED("NotLinked", "The public wallet address is not linked"),
    PENDING_CUSTOMER_APPROVAL("PendingCustomerApproval", "The wallet linking is pending customer approval"),
    PENDING_CONFIRMATION("PendingConfirmation", "The linking process is pending confirmation in blockchain"),
    LINKED("Linked", "The public wallet address is linked");

    private static Map<String, PublicAddressStatus> FORMAT_MAP =
            Stream.of(PublicAddressStatus.values())
                    .collect(toMap(c -> c.getCode(), Function.identity()));
    @Getter
    private String code;

    @Getter
    private String comment;

    @JsonCreator
    public static PublicAddressStatus fromString(
            String value) {
        return Optional
                .ofNullable(FORMAT_MAP.get(value))
                .orElseThrow(() -> new IllegalArgumentException(value));
    }
}
