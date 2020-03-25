package com.lykke.tests.api.service.admin;

import static com.lykke.tests.api.base.PathConsts.AdminApiService.CUSTOMER;
import static com.lykke.tests.api.base.Paths.AdminApi.ADMIN_API_CUSTOMER_DETAILS_PATH;
import static com.lykke.tests.api.base.Paths.AdminApi.BASE_URL_PATH;
import static com.lykke.api.testing.api.base.RequestHeader.getHeader;
import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.apache.http.HttpStatus.SC_OK;

import com.lykke.tests.api.common.model.ValidationErrorResponseModel;
import com.lykke.tests.api.service.admin.model.CustomerDetailsResponse;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import lombok.experimental.UtilityClass;

@UtilityClass
public class GetCustomerPersonalDataUtils {

    static final String CUSTOMER_NOT_FOUND_ERROR_CODE = "CustomerNotFound";
    static final String CUSTOMER_NOT_FOUND_ERROR_MESSAGE = "Customer not found.";

    @Step("Get Customer personal data")
    Response getCustomerPersonalData(String token, String customerId) {
        return getHeader(token)
                .get(BASE_URL_PATH + CUSTOMER.getFilledInPath(customerId));
    }

    @Deprecated
    CustomerDetailsResponse getCustomerDetails(String token, String customerId) {
        return getHeader(token)
                .get(ADMIN_API_CUSTOMER_DETAILS_PATH.apply(customerId))
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(CustomerDetailsResponse.class);
    }

    @Deprecated
    ValidationErrorResponseModel getCustomerDetails_Unsuccessful(String token, String customerId) {
        return getHeader(token)
                .get(ADMIN_API_CUSTOMER_DETAILS_PATH.apply(customerId))
                .then()
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .extract()
                .as(ValidationErrorResponseModel.class);
    }
}
