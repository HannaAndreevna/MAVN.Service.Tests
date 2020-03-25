package com.lykke.tests.api.service.referral;

import static com.lykke.tests.api.base.Paths.Referral.REFERRAL_API_PATH;

import com.lykke.api.testing.api.base.RequestHeader;
import com.lykke.tests.api.base.Paths;
import com.lykke.tests.api.base.Paths.Referral;
import com.lykke.tests.api.service.referral.model.ReferralCreateRequest;
import io.restassured.response.Response;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ReferralUtils {

    public Response postReferral(String customerId) {
        return RequestHeader.getHeader()
                .body(ReferralCreateRequest
                        .builder()
                        .customerId(customerId)
                        .build())
                .post(REFERRAL_API_PATH)
                .thenReturn();

    }
}
