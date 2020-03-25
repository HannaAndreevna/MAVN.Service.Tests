package com.lykke.tests.api.service.customerprofile.model.partnercontacts.model;

import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.apache.http.HttpStatus.SC_OK;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.val;

@AllArgsConstructor
@Builder
@Data
public class PartnerContactsPaginatedRequest {

    private static final String PAGE_SIZE_LESS_THAN_1 = "Page Size can't be less than 1";
    private static final String PAGE_SIZE_MORE_THAN_1000 = "Page Size cannot exceed more then 1000";
    private static final String CURRENT_PAGE_LESS_THAN_1 = "Current page can't be less than 1 or greater than 2147483647";
    private static final String CURRENT_PAGE_INT_MAX = "The requested combination of (CurrentPage - 1) * PageSize can't be more than 2147483647";

    private int pageSize;
    private int currentPage;

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

    public PartnerContactPaginatedValidationErrorResponse getValidationResponse() {
        val response = new PartnerContactPaginatedValidationErrorResponse();
        response.getModelErrors()
                .setPageSize(isPageSizeInvalid() ? new String[]{getPageSizeValidationErrorMessage()} : null);
        response.getModelErrors()
                .setCurrentPage(isCurrentPageInvalid() ? new String[]{getCurrentPageValidationErrorMessage()} : null);
        return response;
    }

    private boolean isPageSizeInvalid() {
        return pageSize < 1 || pageSize > 1000;
    }

    private boolean isCurrentPageInvalid() {
        return currentPage < 1 || currentPage == Integer.MAX_VALUE;
    }

    private String getPageSizeValidationErrorMessage() {
        String pageSizeErrorMessage = "";
        if (pageSize < 1) {
            pageSizeErrorMessage = PAGE_SIZE_LESS_THAN_1;
        } else if (pageSize > 1000) {
            pageSizeErrorMessage = PAGE_SIZE_MORE_THAN_1000;
        }
        return pageSizeErrorMessage;
    }

    private String getCurrentPageValidationErrorMessage() {
        String currentPageErrorMessage = "";
        if (currentPage < 1) {
            currentPageErrorMessage = CURRENT_PAGE_LESS_THAN_1;
        } else if (currentPage == Integer.MAX_VALUE) {
            currentPageErrorMessage = CURRENT_PAGE_INT_MAX;
        }
        return currentPageErrorMessage;
    }
}
