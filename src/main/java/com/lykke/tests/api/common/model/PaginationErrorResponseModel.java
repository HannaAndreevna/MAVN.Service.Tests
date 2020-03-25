package com.lykke.tests.api.common.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy.UpperCamelCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Builder
@NoArgsConstructor
@JsonNaming(UpperCamelCaseStrategy.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PaginationErrorResponseModel {

    private static final String INVALID_CURRENT_PAGE_MESSAGE = "The field CurrentPage must be between 1 and 2147483647.";
    private static final String INVALID_PAGE_SIZE_MESSAGE = "The field PageSize must be between 1 and 2147483647.";

    private int currentPage;
    private int pageSize;
    private String[] currentPageData;
    private String[] pageSizeData;

    public String[] getCurrentPage() {
        return null != currentPageData
                ? currentPageData
                : 0 >= currentPage && currentPage >= Integer.MAX_VALUE
                        ? new String[]{INVALID_CURRENT_PAGE_MESSAGE}
                        : new String[]{};
    }

    public void setCurrentPage(String[] currentPageMessage) {
        currentPageData = currentPageMessage;
    }

    public String[] getPageSize() {
        return null != pageSizeData
                ? pageSizeData
                : 0 >= pageSize && pageSize >= Integer.MAX_VALUE
                        ? new String[]{INVALID_PAGE_SIZE_MESSAGE}
                        : new String[]{};
    }

    public void setPageSize(String[] pageSizeMessage) {
        pageSizeData = pageSizeMessage;
    }
}
