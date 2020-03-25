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
public enum ReferralHotelUseErrorCode {

    NONE("None"),
    REFERRAL_DOES_NOT_EXIST("ReferralDoesNotExist"),
    CURRENCY_DOES_NOT_EXIST("CurrencyDoesNotExist"),
    REFERRAL_EXPIRED("ReferralExpired"),
    REFERRAL_NOT_CONFIRMED("ReferralNotConfirmed"),
    REFERRAL_ALREADY_USED("ReferralAlreadyUsed");

    private static Map<String, ReferralHotelUseErrorCode> FORMAT_MAP =
            Stream.of(ReferralHotelUseErrorCode.values())
                    .collect(toMap(c -> c.getCode(), Function.identity()));
    @Getter
    private String code;

    @JsonCreator
    public static ReferralHotelUseErrorCode fromString(String value) {
        return Optional
                .ofNullable(FORMAT_MAP.get(value))
                .orElseThrow(() -> new IllegalArgumentException(value));
    }
}
