package com.lykke.tests.api.service.referral.model.common;

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
public enum CommonReferralStatus {
    PENDING("Pending", "The referral is in pending"),
    CONFIRMED("Confirmed", "The referral is confirmed and waiting acceptance"),
    ACCEPTED("Accepted", "The referral is accepted"),
    EXPIRED("Expired", "The referral is expired or rejected");

    private static Map<String, CommonReferralStatus> FORMAT_MAP =
            Stream.of(CommonReferralStatus.values())
                    .collect(toMap(c -> c.getCode(), Function.identity()));
    @Getter
    private String code;
    @Getter
    private String comment;

    @JsonCreator
    public static CommonReferralStatus fromString(String value) {
        return Optional
                .ofNullable(FORMAT_MAP.get(value))
                .orElseThrow(() -> new IllegalArgumentException(value));
    }
}
