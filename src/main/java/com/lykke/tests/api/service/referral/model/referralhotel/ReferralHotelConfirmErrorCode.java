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
public enum ReferralHotelConfirmErrorCode {
    NONE("None"),
    REFERRAL_DOES_NOT_EXIST("ReferralDoesNotExist"),
    REFERRAL_ALREADY_CONFIRMED("ReferralAlreadyConfirmed"),
    REFERRAL_EXPIRED("ReferralExpired");

    private static Map<String, ReferralHotelConfirmErrorCode> FORMAT_MAP =
            Stream.of(ReferralHotelConfirmErrorCode.values())
                    .collect(toMap(c -> c.getCode(), Function.identity()));
    @Getter
    private String code;

    @JsonCreator
    public static ReferralHotelConfirmErrorCode fromString(String value) {
        return Optional
                .ofNullable(FORMAT_MAP.get(value))
                .orElseThrow(() -> new IllegalArgumentException(value));
    }
}
