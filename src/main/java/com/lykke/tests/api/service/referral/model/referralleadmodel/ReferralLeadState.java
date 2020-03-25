package com.lykke.tests.api.service.referral.model.referralleadmodel;

import static java.util.stream.Collectors.toMap;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.lykke.api.testing.annotations.NetClassName;
import com.lykke.api.testing.annotations.PublicApi;
import com.lykke.tests.api.service.referral.model.ReferralErrorCode;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@PublicApi
public enum ReferralLeadState {
    PENDING("Pending", "Indicates that the referral lead waiting for approval. "),
    CONFIRMED("Confirmed", "Indicates that the referral lead confirmed."),
    APPROVED("Approved", "Indicates that the referral lead approved."),
    REJECTED("Rejected", "Indicates that the referral lead rejected.");

    private static Map<String, ReferralLeadState> FORMAT_MAP =
            Stream.of(ReferralLeadState.values())
                    .collect(toMap(c -> c.getCode(), Function.identity()));
    @Getter
    private String code;

    @Getter
    private String comment;

    @JsonCreator
    public static ReferralLeadState fromString(String value) {
        return Optional
                .ofNullable(FORMAT_MAP.get(value))
                .orElseThrow(() -> new IllegalArgumentException(value));
    }
}