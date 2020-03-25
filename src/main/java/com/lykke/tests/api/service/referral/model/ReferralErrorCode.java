package com.lykke.tests.api.service.referral.model;

import static java.util.stream.Collectors.toMap;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.lykke.api.testing.annotations.NetClassName;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@NetClassName("ReferralErrorCodes")
public enum ReferralErrorCode {
    NONE("None"),
    REFERRAL_NOT_FOUND("ReferralNotFound"),
    GUID_CAN_NOT_BE_PARSED("GuidCanNotBeParsed"),
    REFERRAL_LEAD_PROCESSING_FAILED("ReferralLeadProcessingFailed"),
    REFERRAL_LEAD_ALREADY_EXIST("ReferralLeadAlreadyExist"),
    REFERRAL_DOES_NOT_EXIST("ReferralDoesNotExist"),
    LEAD_ALREADY_CONFIRMED("LeadAlreadyConfirmed"),
    CUSTOMER_NOT_APPROVED_AGENT("CustomerNotApprovedAgent"),
    CUSTOMER_DOES_NOT_EXIST("CustomerDoesNotExist"),
    COUNTRY_CODE_DOES_NOT_EXIST("CountryCodeDoesNotExist"),
    REFER_YOURSELF("ReferYourself"),
    REFERRAL_LEAD_ALREADY_CONFIRMED("ReferralLeadAlreadyConfirmed"),
    INVALID_PHONE_NUMBER("InvalidPhoneNumber"),
    CAMPAIGN_NOT_FOUND("CampaignNotFound"),
    INVALID_STAKE("InvalidStake");

    private static Map<String, ReferralErrorCode> FORMAT_MAP =
            Stream.of(ReferralErrorCode.values())
                    .collect(toMap(c -> c.getCode(), Function.identity()));
    @Getter
    private String code;

    @JsonCreator
    public static ReferralErrorCode fromString(String value) {
        return Optional
                .ofNullable(FORMAT_MAP.get(value))
                .orElseThrow(() -> new IllegalArgumentException(value));
    }
}
