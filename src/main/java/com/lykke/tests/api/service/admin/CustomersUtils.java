package com.lykke.tests.api.service.admin;

import static com.lykke.api.testing.api.base.RequestHeader.getHeader;
import static com.lykke.tests.api.base.Paths.AdminApi.ADMIN_API_CUSTOMER_PUBLIC_WALLET_ADDRESS_BY_ID_PATH;
import static com.lykke.tests.api.base.Paths.AdminApi.ADMIN_API_CUSTOMER_PUBLIC_WALLET_ADDRESS_PATH;
import static com.lykke.tests.api.base.Paths.AdminApi.ADMIN_API_CUSTOMER_WALLET_ADDRESS_PATH;

import io.restassured.response.Response;
import lombok.experimental.UtilityClass;

@UtilityClass
public class CustomersUtils {

    @Deprecated
    public Response getCustomerWalletAddress(String customerId, String token) {
        return getHeader(token)
                .get(ADMIN_API_CUSTOMER_WALLET_ADDRESS_PATH.apply(customerId))
                .thenReturn();
    }

    // deprecated?
    public Response getCustomerPublicWalletAddress_Deprecated(String customerId, String token) {
        return getHeader(token)
                .get(ADMIN_API_CUSTOMER_PUBLIC_WALLET_ADDRESS_BY_ID_PATH.apply(customerId))
                .thenReturn();
    }

    public Response getCustomerPublicWalletAddress(String customerId, String token) {
        return getHeader(token)
                .queryParam("customerId", customerId)
                .get(ADMIN_API_CUSTOMER_PUBLIC_WALLET_ADDRESS_PATH)
                .thenReturn();
    }
}
