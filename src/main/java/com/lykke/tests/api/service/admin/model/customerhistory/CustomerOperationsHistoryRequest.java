package com.lykke.tests.api.service.admin.model.customerhistory;

import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.apache.http.HttpStatus.SC_OK;

import com.lykke.api.testing.annotations.PublicApi;
import com.lykke.api.testing.annotations.QueryParameters;
import com.lykke.tests.api.common.enums.LoginProviders;
import com.lykke.tests.api.service.admin.model.PagedRequestModel;
import lombok.Builder;
import lombok.Data;

@Data
@PublicApi
@QueryParameters
public class CustomerOperationsHistoryRequest extends PagedRequestModel {

    private String customerId;

    @Builder(builderMethodName = "customerOperationsHistoryRequestBuilder")
    public CustomerOperationsHistoryRequest(int pageSize, int currentPage, String customerId) {
        super(pageSize, currentPage);
        this.customerId = customerId;
    }

    public int getHttpStatus() {
        return isPageSizeInvalid() || isCurrentPageInvalid()
                ? SC_BAD_REQUEST
                : SC_OK;
    }

    private boolean isPageSizeInvalid() {
        return getPageSize() < 1 || getPageSize() > 1000;
    }

    private boolean isCurrentPageInvalid() {
        return getCurrentPage() < 1 || getCurrentPage() > Integer.MAX_VALUE;
    }
}
