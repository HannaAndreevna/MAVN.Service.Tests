package com.lykke.tests.api.service.referral;

import static com.lykke.tests.api.base.PathConsts.ReferralService.PURCHASES_BY_CUSTOMER_ID_PATH;
import static com.lykke.tests.api.base.Paths.REFERRAL_API_REFERRAL_PATH;
import static com.lykke.api.testing.api.base.RequestHeader.getHeader;

import io.restassured.response.Response;
import lombok.experimental.UtilityClass;

@UtilityClass
public class PurchaseReferralUtils {

    public Response getPurchaseReferralByCustomerId(String customerId) {
        return getHeader()
                .get(REFERRAL_API_REFERRAL_PATH + PURCHASES_BY_CUSTOMER_ID_PATH.getFilledInPath(customerId));
    }
}
