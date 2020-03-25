package com.lykke.tests.api.service.referral.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy.UpperCamelCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonNaming(UpperCamelCaseStrategy.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ReferralCreateResponse extends ReferralErrorResponseModel {

    private String referralCode;

    @Builder(builderMethodName = "referralCreateResponseBuilder")
    public ReferralCreateResponse(ReferralErrorCode errorCode, String errorMessage, String referralCode) {
        super(errorCode, errorMessage);
        this.referralCode = referralCode;
    }
}
