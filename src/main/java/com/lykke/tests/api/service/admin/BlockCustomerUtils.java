package com.lykke.tests.api.service.admin;

import static com.lykke.api.testing.api.base.RequestHeader.getHeader;
import static com.lykke.tests.api.base.Paths.AdminApi.ADMIN_API_CUSTOMER_DETAILS_PATH;
import static com.lykke.tests.api.base.Paths.AdminApi.BLOCK_CUSTOMER_BY_ID_PATH;
import static com.lykke.tests.api.base.Paths.AdminApi.BLOCK_CUSTOMER_WALLET_BY_ID_PATH;
import static com.lykke.tests.api.base.Paths.AdminApi.UNBLOCK_CUSTOMER_BY_ID_PATH;
import static com.lykke.tests.api.base.Paths.AdminApi.UNBLOCK_CUSTOMER_WALLET_BY_ID_PATH;
import static com.lykke.tests.api.service.admin.LoginAdminUtils.getAdminToken;
import static org.apache.http.HttpStatus.SC_OK;

import com.lykke.tests.api.service.admin.model.CustomerDetailsModel;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import lombok.experimental.UtilityClass;

@UtilityClass
public class BlockCustomerUtils {

    @Deprecated
    @Step
    public Response blockCustomer(String customerId) {
        return getHeader(getAdminToken())
                .post(BLOCK_CUSTOMER_BY_ID_PATH.apply(customerId))
                .thenReturn();
    }

    @Deprecated
    @Step
    public Response unblockCustomer(String customerId) {
        return getHeader(getAdminToken())
                .post(UNBLOCK_CUSTOMER_BY_ID_PATH.apply(customerId))
                .thenReturn();
    }

    @Deprecated
    @Step
    public Response blockCustomerWallet(String customerId) {
        return getHeader(getAdminToken())
                .post(BLOCK_CUSTOMER_WALLET_BY_ID_PATH.apply(customerId))
                .thenReturn();
    }

    @Deprecated
    @Step
    public Response unblockCustomerWallet(String customerId) {
        return getHeader(getAdminToken())
                .post(UNBLOCK_CUSTOMER_WALLET_BY_ID_PATH.apply(customerId))
                .thenReturn();
    }

    @Deprecated
    @Step
    public CustomerDetailsModel getCustomerDetails(String customerId) {
        return getHeader(getAdminToken())
                .get(ADMIN_API_CUSTOMER_DETAILS_PATH.apply(customerId))
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(CustomerDetailsModel.class);
    }
}
