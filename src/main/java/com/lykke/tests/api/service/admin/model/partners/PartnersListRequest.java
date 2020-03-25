package com.lykke.tests.api.service.admin.model.partners;

import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.apache.http.HttpStatus.SC_OK;

import com.lykke.api.testing.annotations.PublicApi;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.val;

@AllArgsConstructor
@Builder
@Data
@PublicApi
public class PartnersListRequest {

    private static final String PAGE_SIZE_ERROR_MESSAGE = "The field PageSize must be between 1 and 500.";
    private static final String CURRENT_PAGE_ERROR_MESSAGE = "The field CurrentPage must be between 1 and 10000.";

    private int pageSize;
    private int currentPage;
    private String name;
    private String businessVertical;

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
        return pageSize < 1 || pageSize > 500;
    }

    private boolean isCurrentPageInvalid() {
        return currentPage < 1 || currentPage > 1000;
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
