package com.lykke.tests.api.service.admin.model;

import static com.lykke.tests.api.common.PaginationConts.CURRENT_PAGE_LOWER_BOUNDARY;
import static com.lykke.tests.api.common.PaginationConts.CURRENT_PAGE_UPPER_BOUNDARY;
import static com.lykke.tests.api.common.PaginationConts.PAGE_SIZE_LOWER_BOUNDARY;
import static com.lykke.tests.api.common.PaginationConts.PAGE_SIZE_UPPER_BOUNDARY;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.apache.http.HttpStatus.SC_OK;

import com.lykke.api.testing.annotations.QueryParameters;
import lombok.Builder;
import lombok.Data;
import lombok.val;

@Data
@QueryParameters
public class CampaignListRequest extends PagedRequestModel {

    private static final String PAGE_SIZE_ERROR_MESSAGE = "The field PageSize must be between 1 and 500.";
    private static final String CURRENT_PAGE_ERROR_MESSAGE = "The field CurrentPage must be between 1 and 10000.";

    private String campaignName;

    @Builder(builderMethodName = "campaignBuilder")
    public CampaignListRequest(int pageSize, int currentPage, String campaignName) {
        super(pageSize, currentPage);
        this.campaignName = campaignName;
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
        return getPageSize() < PAGE_SIZE_LOWER_BOUNDARY || getPageSize() > PAGE_SIZE_UPPER_BOUNDARY;
    }

    private boolean isCurrentPageInvalid() {
        return getCurrentPage() < CURRENT_PAGE_LOWER_BOUNDARY || getCurrentPage() > CURRENT_PAGE_UPPER_BOUNDARY;
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
