package com.lykke.tests.api.service.partnerspayments;

import static com.lykke.api.testing.api.base.RequestHeader.getHeader;
import static com.lykke.api.testing.api.common.QueryParamsUtils.getQueryParams;
import static com.lykke.tests.api.base.Paths.PartnersPayments.CUSTOMER_APPROVAL_API_PATH;
import static com.lykke.tests.api.base.Paths.PartnersPayments.CUSTOMER_FAILED_API_PATH;
import static com.lykke.tests.api.base.Paths.PartnersPayments.CUSTOMER_PENDING_API_PATH;
import static com.lykke.tests.api.base.Paths.PartnersPayments.CUSTOMER_REJECTION_API_PATH;
import static com.lykke.tests.api.base.Paths.PartnersPayments.CUSTOMER_SUCCEEDED_API_PATH;
import static com.lykke.tests.api.base.Paths.PartnersPayments.PARTNER_APPROVAL_API_PATH;
import static com.lykke.tests.api.base.Paths.PartnersPayments.PARTNER_REJECTION_API_PATH;
import static com.lykke.tests.api.base.Paths.PartnersPayments.PAYMENTS_API_PATH;
import static com.lykke.tests.api.base.Paths.PartnersPayments.PAYMENT_REQUEST_ID_API_PATH;

import com.lykke.tests.api.service.partnerspayments.model.CustomerApprovePaymentRequest;
import com.lykke.tests.api.service.partnerspayments.model.CustomerRejectPaymentRequest;
import com.lykke.tests.api.service.partnerspayments.model.PaginatedRequestForCustomer;
import com.lykke.tests.api.service.partnerspayments.model.PaymentRequestModel;
import com.lykke.tests.api.service.partnerspayments.model.ReceptionistProcessPaymentRequest;
import com.lykke.tests.api.service.partnerspayments.model.RequestByCustomerId;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import lombok.experimental.UtilityClass;
import lombok.val;

@UtilityClass
public class PartnersPaymentsUtils {

    @Step("Create partners payment")
    public <E extends PaymentRequestModel> Response postPayment(E requestModel, String token) {
        val body = getQueryParams(requestModel);
        //         x -> !EMPTY.equalsIgnoreCase(x) && !"0.0".equalsIgnoreCase(x) && !"0".equalsIgnoreCase(x));

        return getHeader(token)
                .body(body)
                .post(PAYMENTS_API_PATH)
                .thenReturn();
    }

    @Step("Get customer pending")
    public Response getCustomerPending(PaginatedRequestForCustomer
            requestModel, String token) {
        return getHeader(token)
                .queryParams(getQueryParams(requestModel))
                .get(CUSTOMER_PENDING_API_PATH)
                .thenReturn();
    }

    @Step("Get customer succeeded")
    public Response getCustomerSucceeded(PaginatedRequestForCustomer requestModel, String token) {
        return getHeader(token)
                .queryParams(getQueryParams(requestModel))
                .get(CUSTOMER_SUCCEEDED_API_PATH)
                .thenReturn();
    }

    @Step("Get customer failed")
    public Response getCustomerFailed(PaginatedRequestForCustomer requestModel, String token) {
        return getHeader(token)
                .queryParams(getQueryParams(requestModel))
                .get(CUSTOMER_FAILED_API_PATH)
                .thenReturn();
    }

    @Step("Post customer approval")
    public Response postCustomerApproval(CustomerApprovePaymentRequest requestModel, String token) {
        return getHeader(token)
                .body(requestModel)
                .post(CUSTOMER_APPROVAL_API_PATH)
                .thenReturn();
    }

    @Step("Post customer rejection")
    public Response postCustomerRejection(CustomerRejectPaymentRequest requestModel, String token) {
        return getHeader(token)
                .body(requestModel)
                .post(CUSTOMER_REJECTION_API_PATH)
                .thenReturn();
    }

    public Response postPartnerApproval(ReceptionistProcessPaymentRequest requestModel, String token) {
        return getHeader(token)
                .body(requestModel)
                .post(PARTNER_APPROVAL_API_PATH)
                .thenReturn();
    }

    public Response postPartnerCancellation(ReceptionistProcessPaymentRequest requestModel, String token) {
        return getHeader(token)
                .body(requestModel)
                .post(PARTNER_REJECTION_API_PATH)
                .thenReturn();
    }

    public Response getPaymentByRequestId(String paymentRequestId, String token) {
        return getHeader(token)
                .get(PAYMENT_REQUEST_ID_API_PATH.apply(paymentRequestId))
                .thenReturn();
    }
}
