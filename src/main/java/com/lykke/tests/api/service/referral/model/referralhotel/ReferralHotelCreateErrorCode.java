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
public enum ReferralHotelCreateErrorCode {
    NONE("None", "No error"),
    CUSTOMER_DOES_NOT_EXIST("CustomerDoesNotExist", "Customer does not exist"),
    REFERRAL_ALREADY_CONFIRMED("ReferralAlreadyConfirmed", "Referral has already been confirmed"),
    REFERRALS_LIMIT_EXCEEDED("ReferralsLimitExceeded", "Referral limit exceeded"),
    REFERRAL_ALREADY_EXIST("ReferralAlreadyExist", "Referral for given email and referrer already exists"),
    AGENT_CANT_REFER_HIMSELF("AgentCantReferHimself", "Agent can't refer himself"),
    CAMPAIGN_NOT_FOUND("CampaignNotFound", "Campaign not found."),
    REFERRAL_EXPIRED("ReferralExpired", "Referral has expired"),
    INVALID_STAKE("InvalidStake", "The stake for the referral is invalid");

    private static Map<String, ReferralHotelCreateErrorCode> FORMAT_MAP =
            Stream.of(ReferralHotelCreateErrorCode.values())
                    .collect(toMap(c -> c.getCode(), Function.identity()));
    @Getter
    private String code;

    @Getter
    private String comment;

    @JsonCreator
    public static ReferralHotelCreateErrorCode fromString(String value) {
        return Optional
                .ofNullable(FORMAT_MAP.get(value))
                .orElseThrow(() -> new IllegalArgumentException(value));
    }
}