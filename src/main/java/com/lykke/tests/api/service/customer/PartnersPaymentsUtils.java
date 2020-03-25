package com.lykke.tests.api.service.customer;

import static com.lykke.api.testing.api.base.RequestHeader.getHeader;
import static com.lykke.api.testing.api.common.QueryParamsUtils.getQueryParams;
import static com.lykke.tests.api.base.Paths.Customer.PAYMENTS_API_PATH;
import static com.lykke.tests.api.base.Paths.Customer.PAYMENTS_APPROVAL_API_PATH;
import static com.lykke.tests.api.base.Paths.Customer.PAYMENTS_FAILED_API_PATH;
import static com.lykke.tests.api.base.Paths.Customer.PAYMENTS_PENDING_API_PATH;
import static com.lykke.tests.api.base.Paths.Customer.PAYMENTS_REJECTION_API_PATH;
import static com.lykke.tests.api.base.Paths.Customer.PAYMENTS_SUCCEEDED_API_PATH;
import static com.lykke.tests.api.common.PaginationConts.CURRENT_PAGE_LOWER_BOUNDARY;
import static com.lykke.tests.api.common.PaginationConts.PAGE_SIZE_UPPER_BOUNDARY;

import com.lykke.tests.api.common.PaginationConts;
import com.lykke.tests.api.service.customer.model.partnerspayments.ApprovePartnerPaymentRequest;
import com.lykke.tests.api.service.customer.model.partnerspayments.GetPartnerPaymentRequestDetailsRequest;
import com.lykke.tests.api.service.customer.model.partnerspayments.PaginatedRequestModel;
import com.lykke.tests.api.service.customer.model.partnerspayments.RejectPartnerPaymentRequest;
import io.restassured.response.Response;
import lombok.experimental.UtilityClass;

@UtilityClass
public class PartnersPaymentsUtils {

    public Response getPartnersPayments(GetPartnerPaymentRequestDetailsRequest requestModel, String token) {
        return getHeader(token)
                .queryParams(getQueryParams(requestModel))
                .get(PAYMENTS_API_PATH)
                .thenReturn();
    }

    public Response approvePayment(ApprovePartnerPaymentRequest requestModel, String token) {
        return getHeader(token)
                .body(requestModel)
                .post(PAYMENTS_APPROVAL_API_PATH)
                .thenReturn();
    }

    public Response rejectPayment(RejectPartnerPaymentRequest requestModel, String token) {
        return getHeader(token)
                .body(requestModel)
                .post(PAYMENTS_REJECTION_API_PATH)
                .thenReturn();
    }

    public Response getPendingPayments(String token) {
        return getHeader(token)
                .queryParams(getQueryParams(PaginatedRequestModel
                        .builder()
                        .currentPage(CURRENT_PAGE_LOWER_BOUNDARY)
                        .pageSize(PAGE_SIZE_UPPER_BOUNDARY)
                        .build()))
                .get(PAYMENTS_PENDING_API_PATH)
                .thenReturn();
    }

    public Response getSucceededPayments(PaginatedRequestModel requestModel, String token) {
        return getHeader(token)
                .queryParams(getQueryParams(requestModel))
                .get(PAYMENTS_SUCCEEDED_API_PATH)
                .thenReturn();
    }

    public Response getFailedPayments(PaginatedRequestModel requestModel, String token) {
        return getHeader(token)
                .queryParams(getQueryParams(requestModel))
                .get(PAYMENTS_FAILED_API_PATH)
                .thenReturn();
    }
}
