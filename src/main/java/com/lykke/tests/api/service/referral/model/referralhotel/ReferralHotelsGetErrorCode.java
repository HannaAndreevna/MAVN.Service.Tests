package com.lykke.tests.api.service.referral.model.referralhotel;

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
public enum ReferralHotelsGetErrorCode {
    NONE("None"),
    CUSTOMER_DOES_NOT_EXIST("CustomerDoesNotExist");

    private static Map<String, ReferralHotelsGetErrorCode> FORMAT_MAP =
            Stream.of(ReferralHotelsGetErrorCode.values())
                    .collect(toMap(c -> c.getCode(), Function.identity()));
    @Getter
    private String code;

    @JsonCreator
    public static ReferralHotelsGetErrorCode fromString(String value) {
        return Optional
                .ofNullable(FORMAT_MAP.get(value))
                .orElseThrow(() -> new IllegalArgumentException(value));
    }
}
