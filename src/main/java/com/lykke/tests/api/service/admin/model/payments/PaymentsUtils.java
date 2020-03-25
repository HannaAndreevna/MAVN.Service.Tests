package com.lykke.tests.api.service.admin.model.payments;

import static com.lykke.tests.api.base.Paths.AdminApi.ACCEPT_UNPROCESSED_PAYMENTS_PATH;
import static com.lykke.tests.api.base.Paths.AdminApi.UNPROCESSED_PAYMENTS_PATH;
import static com.lykke.api.testing.api.base.RequestHeader.getHeader;
import static com.lykke.api.testing.api.common.QueryParamsUtils.getQueryParams;
import static com.lykke.tests.api.service.admin.LoginAdminUtils.getAdminToken;

import com.lykke.tests.api.service.admin.model.PagedRequestModel;
import com.lykke.tests.api.service.admin.model.payments.model.PagedPaymentsResponseModel;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import lombok.experimental.UtilityClass;

@UtilityClass
public class PaymentsUtils {

    @Step
    public PagedPaymentsResponseModel getPagedUnprocessedPayments(PagedRequestModel requestObject) {
        return getHeader(getAdminToken())
                .queryParams(getQueryParams(requestObject))
                .get(UNPROCESSED_PAYMENTS_PATH)
                .then()
                .extract()
                .as(PagedPaymentsResponseModel.class);
    }

    @Step
    public Response getPagedUnprocessedPaymentsResponse(PagedRequestModel requestObject) {
        return getHeader(getAdminToken())
                .queryParams(getQueryParams(requestObject))
                .get(UNPROCESSED_PAYMENTS_PATH);
    }

    @Deprecated
    @Step
    public Response acceptUnprocessedPayment(String paymentId) {
        return getHeader(getAdminToken())
                .post(ACCEPT_UNPROCESSED_PAYMENTS_PATH.apply(paymentId))
                .thenReturn();
    }
}
