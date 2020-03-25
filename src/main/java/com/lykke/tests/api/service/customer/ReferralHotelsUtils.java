package com.lykke.tests.api.service.customer;

import static com.lykke.api.testing.api.base.RequestHeader.getHeader;
import static com.lykke.tests.api.base.Paths.Customer.REFERRAL_HOTELS_ALL_API_PATH;

import com.lykke.tests.api.service.customer.model.referral.HotelReferralRequestModel;
import io.restassured.response.Response;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ReferralHotelsUtils {

    public Response postReferralHotel(HotelReferralRequestModel requestModel, String token) {
        return getHeader(token)
                .body(requestModel)
                .post(REFERRAL_HOTELS_ALL_API_PATH)
                .thenReturn();
    }
}
