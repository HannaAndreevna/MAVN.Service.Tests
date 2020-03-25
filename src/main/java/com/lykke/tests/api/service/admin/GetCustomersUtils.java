package com.lykke.tests.api.service.admin;

import static com.lykke.api.testing.api.base.RequestHeader.getHeader;
import static com.lykke.api.testing.api.common.QueryParamsUtils.getQueryParams;
import static com.lykke.tests.api.base.Paths.AdminApi;
import static com.lykke.tests.api.base.Paths.CUSTOMER_API_PATH;
import static com.lykke.tests.api.common.CommonConsts.AWAITILITY_GET_CUSTOMER_ID_SEC;
import static com.lykke.tests.api.common.PaginationConts.CURRENT_PAGE_LOWER_BOUNDARY;
import static com.lykke.tests.api.service.admin.LoginAdminUtils.getAdminToken;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.http.HttpStatus.SC_BAD_REQUEST;

import com.lykke.tests.api.base.PathConsts.AdminApiService;
import com.lykke.tests.api.service.admin.model.CustomerListRequest;
import com.lykke.tests.api.service.admin.model.CustomerListResponse;
import com.lykke.tests.api.service.admin.model.ValidationErrorResponse;
import com.lykke.tests.api.service.admin.model.customerhistory.CustomerOperationsHistoryRequest;
import com.lykke.tests.api.service.admin.model.customerhistory.CustomerOperationsHistoryResponse;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import lombok.experimental.UtilityClass;
import lombok.val;
import org.awaitility.Awaitility;
import org.awaitility.Duration;

@UtilityClass
public class GetCustomersUtils {

    public ValidationErrorResponse getCustomersPaginatedValidationResponse(CustomerListRequest requestObject) {
        return getHeader(getAdminToken())
                .queryParams(getQueryParams(requestObject))
                .get(AdminApi.ADMIN_API_CUSTOMERS_PATH + AdminApiService.SEARCH_PATH.getPath())
                .then()
                .assertThat()
                .statusCode(requestObject.getHttpStatus())
                .extract()
                .as(ValidationErrorResponse.class);
    }

    public Response getCustomers() {
        return getHeader(getAdminToken())
                .get(CUSTOMER_API_PATH);
    }

    @Step("Get Customers paginated request")
    public CustomerListResponse getCustomersPaginatedResponse(CustomerListRequest requestObject) {
        return getHeader(getAdminToken())
                .body(requestObject)
                .post(AdminApi.ADMIN_API_CUSTOMERS_PATH + AdminApiService.SEARCH_PATH.getPath())
                .then()
                .assertThat()
                .statusCode(requestObject.getHttpStatus())
                .extract()
                .as(CustomerListResponse.class);
    }

    // TODO: it doesn't return id each time, needs investigation
    @Step
    public static String getCustomerId(String email) {
        val requestObject = CustomerListRequest
                .builder()
                .currentPage(CURRENT_PAGE_LOWER_BOUNDARY)
                .pageSize(1000)
                .build();

        Awaitility.await()
                .atMost(AWAITILITY_GET_CUSTOMER_ID_SEC, TimeUnit.SECONDS)
                .pollInterval(Duration.ONE_HUNDRED_MILLISECONDS)
                .until(() -> {

                    val response = getHeader(getAdminToken())
                            .queryParams(getQueryParams(requestObject))
                            .get(AdminApi.ADMIN_API_CUSTOMERS_PATH)
                            .then()
                            .assertThat()
                            .statusCode(requestObject.getHttpStatus())
                            .extract()
                            .as(CustomerListResponse.class);

                    val candidate = Arrays.stream(response.getCustomers())
                            .filter(cust -> email.equals(cust.getEmail()))
                            .findFirst();
                    return candidate.isPresent();
                });

        val customers = getHeader(getAdminToken())
                .queryParams(getQueryParams(requestObject))
                .get(AdminApi.ADMIN_API_CUSTOMERS_PATH)
                .then()
                .assertThat()
                .statusCode(requestObject.getHttpStatus())
                .extract()
                .as(CustomerListResponse.class);

        val customerCandidate = Arrays.stream(customers.getCustomers())
                .filter(cust -> email.equals(cust.getEmail()))
                .findFirst();
        return customerCandidate.isPresent() ? customerCandidate.get().getCustomerId() : EMPTY;
    }

    @Step
    public CustomerOperationsHistoryResponse getCustomerHistoryResponse(CustomerOperationsHistoryRequest
            requestObject) {
        return getHeader(getAdminToken())
                .queryParams(getQueryParams(requestObject))
                .get(AdminApi.ADMIN_API_CUSTOMERS_HISTORY_PATH)
                .then()
                .assertThat()
                .statusCode(requestObject.getHttpStatus())
                .extract()
                .as(CustomerOperationsHistoryResponse.class);
    }

    @Step
    public ValidationErrorResponse getCustomerHistoryErrorResponse(CustomerListRequest requestObject) {
        return getHeader(getAdminToken())
                .queryParams(getQueryParams(requestObject))
                .get(AdminApi.ADMIN_API_CUSTOMERS_HISTORY_PATH)
                .then()
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .extract()
                .as(ValidationErrorResponse.class);
    }

    @Step
    public ValidationErrorResponse getNonExistingCustomerIdErrorResponse(CustomerListRequest requestObject) {
        return getHeader(getAdminToken())
                .queryParams(getQueryParams(requestObject))
                .get(AdminApi.ADMIN_API_CUSTOMERS_HISTORY_PATH)
                .then()
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .extract()
                .as(ValidationErrorResponse.class);
    }
}
