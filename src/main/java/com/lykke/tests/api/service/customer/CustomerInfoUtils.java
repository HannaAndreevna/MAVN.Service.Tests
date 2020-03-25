package com.lykke.tests.api.service.customer;

import static com.lykke.tests.api.base.Paths.CUSTOMER_API_PATH;
import static com.lykke.api.testing.api.base.RequestHeader.getHeader;

import io.restassured.response.Response;
import lombok.experimental.UtilityClass;

@UtilityClass
public class CustomerInfoUtils {

    public Response getCurrentCustomerInfo(String token) {
        return getHeader(token)
                .get(CUSTOMER_API_PATH);
    }
}
