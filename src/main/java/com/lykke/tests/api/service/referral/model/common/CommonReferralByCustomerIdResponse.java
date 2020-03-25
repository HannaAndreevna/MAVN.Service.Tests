package com.lykke.tests.api.service.referral.model.common;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy.UpperCamelCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.lykke.tests.api.service.referral.model.ReferralErrorCode;
import com.lykke.tests.api.service.referral.model.ReferralErrorResponseModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonNaming(UpperCamelCaseStrategy.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CommonReferralByCustomerIdResponse extends ReferralErrorResponseModel {

    private CommonReferralModel[] referrals;

    @Builder(builderMethodName = "commonReferralByCustomerIdResponseBuilder")
    public CommonReferralByCustomerIdResponse(ReferralErrorCode errorCode, String errorMessage,
            CommonReferralModel[] referrals) {
        super(errorCode, errorMessage);
        this.referrals = referrals;
    }
}
