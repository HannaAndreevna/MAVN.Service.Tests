package com.lykke.tests.api.service.customer;

import static com.lykke.api.testing.api.base.RequestHeader.getHeader;
import static com.lykke.tests.api.base.Paths.Customer.MOBILE_SETTINGS_API_PATH;

import io.restassured.response.Response;
import lombok.experimental.UtilityClass;

@UtilityClass
public class MobileUtils {

    public Response getMobileSettings() {
        return getHeader()
                .get(MOBILE_SETTINGS_API_PATH)
                .thenReturn();
    }
}
