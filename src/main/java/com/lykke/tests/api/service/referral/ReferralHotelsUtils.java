package com.lykke.tests.api.service.referral;

import static com.lykke.api.testing.api.base.RequestHeader.getHeader;
import static com.lykke.tests.api.base.Paths.Referral.REFERRAL_HOTELS_API_PATH;

import com.lykke.tests.api.service.referral.model.referralhotel.ReferralHotelCreateRequest;
import io.restassured.response.Response;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ReferralHotelsUtils {

    public Response createReferralHotel(ReferralHotelCreateRequest requestModel) {
        return getHeader()
                .body(requestModel)
                .post(REFERRAL_HOTELS_API_PATH)
                .thenReturn();
    }
}
