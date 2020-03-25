package com.lykke.tests.api.service.customer.model.history;

import static com.lykke.tests.api.common.PaginationConts.CURRENT_PAGE_ERROR_MESSAGE;
import static com.lykke.tests.api.common.CommonConsts.MODEL_VALIDATION_FAILURE;
import static com.lykke.tests.api.common.PaginationConts.PAGE_SIZE_ERROR_MESSAGE;
import static com.lykke.tests.api.common.PaginationConts.CURRENT_PAGE_LOWER_BOUNDARY;
import static com.lykke.tests.api.common.PaginationConts.CURRENT_PAGE_UPPER_BOUNDARY;
import static com.lykke.tests.api.common.PaginationConts.PAGE_SIZE_LOWER_BOUNDARY;
import static com.lykke.tests.api.common.PaginationConts.PAGE_SIZE_UPPER_BOUNDARY;
import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;

import com.lykke.api.testing.annotations.PublicApi;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.val;


@Data
@PublicApi
@Builder
@AllArgsConstructor
public class OperationsHistoryRequest {

    private String token;
    private int pageSize;
    private int currentPage;

    public String getPageSize() {
        return String.valueOf(pageSize);
    }

    public String getCurrentPage() {
        return String.valueOf(currentPage);
    }

    public int getHttpStatus() {
        if (!isTokenValid()) {
            return SC_UNAUTHORIZED;
        } else if (isPageSizeInvalid() || isCurrentPageInvalid()) {
            return SC_BAD_REQUEST;
        } else {
            return SC_OK;
        }
    }

    public OperationHistoryErrorResponseModel getValidationResponse() {
        val response = new OperationHistoryErrorResponseModel();
        response.setError(isPageSizeInvalid() || isCurrentPageInvalid() ? MODEL_VALIDATION_FAILURE : null);
        response.setMessage(
                isCurrentPageInvalid() && isPageSizeInvalid()
                        ? PAGE_SIZE_ERROR_MESSAGE + " " + CURRENT_PAGE_ERROR_MESSAGE
                        : isCurrentPageInvalid()
                                ? CURRENT_PAGE_ERROR_MESSAGE
                                : isPageSizeInvalid()
                                        ? PAGE_SIZE_ERROR_MESSAGE
                                        : null);
        return response;
    }

    private boolean isPageSizeInvalid() {
        return pageSize < PAGE_SIZE_LOWER_BOUNDARY
                || pageSize > PAGE_SIZE_UPPER_BOUNDARY;
    }

    private boolean isCurrentPageInvalid() {
        return currentPage < CURRENT_PAGE_LOWER_BOUNDARY || currentPage > CURRENT_PAGE_UPPER_BOUNDARY;
    }

    private boolean isTokenValid() {
        return getToken().length() < 71;
    }
}
