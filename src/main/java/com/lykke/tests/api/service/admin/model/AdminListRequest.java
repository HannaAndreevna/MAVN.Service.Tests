package com.lykke.tests.api.service.admin.model;

import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.apache.http.HttpStatus.SC_OK;

import com.lykke.api.testing.annotations.PublicApi;
import com.lykke.api.testing.annotations.QueryParameters;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.val;

@AllArgsConstructor
@Builder
@Data
@PublicApi
@QueryParameters
public class AdminListRequest {

    private static final String CUSTOMER_ID_FORMAT = "\\b[a-z0-9]{8}\\-\\b[a-z0-9]{4}\\-\\b[a-z0-9]{4}\\-\\b[a-z0-9]{4}\\-\\b[a-z0-9]{12}";
    private static final String PAGE_SIZE_ERROR_MESSAGE = "The field PageSize must be between 1 and 1000.";
    private static final String CURRENT_PAGE_ERROR_MESSAGE = "The field CurrentPage must be between 1 and 2147483647.";
    private static final String INVALID_CUSTOMER_ID_FORMAT_ERR_MSG = "CustomerId has invalid format";
    private static final String CUSTOMER_ID_REQUIRED_ERR_MSG = "CustomerId required";
    private static final String ERROR_FIELD_ERR_MSG = "CustomerNotFound";
    private static final String MESSAGE_FIELD_ERR_MSG = "Customer not found.";

    private int pageSize;
    private int currentPage;
    private String searchValue;

    public String getPageSize() {
        return String.valueOf(pageSize);
    }

    public String getCurrentPage() {
        return String.valueOf(currentPage);
    }

    public int getHttpStatus() {
        return isPageSizeInvalid() || isCurrentPageInvalid()
                ? SC_BAD_REQUEST
                : SC_OK;
    }

    public ValidationErrorResponse getValidationResponse() {
        val response = new ValidationErrorResponse();
        response.setPageSize(isPageSizeInvalid() ? new String[]{getPageSizeValidationErrorMessage()} : null);
        response.setCurrentPage(isCurrentPageInvalid() ? new String[]{getCurrentPageValidationErrorMessage()} : null);
        return response;
    }

    private boolean isPageSizeInvalid() {
        return pageSize < 1 || pageSize > 1000;
    }

    private boolean isCurrentPageInvalid() {
        return currentPage < 1 || currentPage > Integer.MAX_VALUE;
    }

    private String getPageSizeValidationErrorMessage() {
        return isPageSizeInvalid()
                ? PAGE_SIZE_ERROR_MESSAGE
                : EMPTY;
    }

    private String getCurrentPageValidationErrorMessage() {
        return isCurrentPageInvalid()
                ? CURRENT_PAGE_ERROR_MESSAGE
                : EMPTY;
    }
}
