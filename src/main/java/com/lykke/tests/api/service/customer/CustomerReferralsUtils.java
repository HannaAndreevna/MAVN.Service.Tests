package com.lykke.tests.api.service.customer;

import static com.lykke.api.testing.api.common.QueryParamsUtils.getQueryParams;
import static com.lykke.tests.api.base.Paths.Customer.REFERRALS_ALL_API_PATH;
import static com.lykke.api.testing.api.base.RequestHeader.getHeader;
import static com.lykke.tests.api.base.Paths.REFERRALS_API_PATH;

import com.lykke.tests.api.service.customer.model.referral.ReferralPaginationRequestModel;
import io.restassured.response.Response;
import lombok.experimental.UtilityClass;

@UtilityClass
public class CustomerReferralsUtils {

    public static Response getCustomerReferrals(String token) {
        return getHeader(token)
                .get(REFERRALS_API_PATH);
    }

    public Response getAllReferrals(ReferralPaginationRequestModel requestModel, String token) {
        return getHeader(token)
                .queryParams(getQueryParams(requestModel))
                .get(REFERRALS_ALL_API_PATH)
                .thenReturn();
    }
}
