package com.lykke.tests.api.service.customer;

import static com.lykke.api.testing.api.base.RequestHeader.getHeader;
import static com.lykke.tests.api.base.Paths.CUSTOMER_API_COMMON_INFORMATION;

import io.restassured.response.Response;
import lombok.experimental.UtilityClass;

@UtilityClass
public class CustomerCommonInformation {

    public Response getCustomerCommonInfo() {
        return getHeader()
                .get(CUSTOMER_API_COMMON_INFORMATION);
    }
}
