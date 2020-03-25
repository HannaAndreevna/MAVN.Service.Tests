package com.lykke.tests.api.service.referral;

import static com.lykke.api.testing.api.base.RequestHeader.getHeader;
import static com.lykke.tests.api.base.Paths.Referral.COMMON_REFERRAL_BY_CUSTOMER_ID_API_PATH;
import static com.lykke.tests.api.base.Paths.Referral.COMMON_REFERRAL_LIST_API_PATH;

import com.lykke.tests.api.service.referral.model.common.CommonReferralByCustomerIdRequest;
import com.lykke.tests.api.service.referral.model.common.CommonReferralByReferralIdsRequest;
import io.restassured.response.Response;
import lombok.experimental.UtilityClass;

@UtilityClass
public class CommonReferralUtils {

    public Response getCommonReferralByCustomerId(
            CommonReferralByCustomerIdRequest requestModel) {
        return getHeader()
                .body(requestModel)
                .post(COMMON_REFERRAL_BY_CUSTOMER_ID_API_PATH)
                .thenReturn();
    }

    public Response getListOfCommonReferrals(CommonReferralByReferralIdsRequest requestModel) {
        return getHeader()
                .body(requestModel)
                .post(COMMON_REFERRAL_LIST_API_PATH)
                .thenReturn();
    }
}
