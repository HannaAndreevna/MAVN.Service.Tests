package com.lykke.tests.api.service.customer;

import static com.lykke.api.testing.api.common.QueryParamsUtils.getQueryParams;
import static com.lykke.tests.api.base.Paths.CUSTOMER_API_OPERATIONS_HISTORY_PATH;
import static com.lykke.api.testing.api.base.RequestHeader.getHeader;

import com.lykke.tests.api.service.customer.model.history.OperationsHistoryRequest;
import com.lykke.tests.api.service.customer.model.history.PaginatedOperationsHistoryResponseModel;
import lombok.experimental.UtilityClass;

@UtilityClass
public class HistoryUtils {

    public PaginatedOperationsHistoryResponseModel getOperationsHistoryResponse(
            OperationsHistoryRequest requestObject) {
        return getHeader(requestObject.getToken())
                .queryParams(getQueryParams(requestObject))
                .get(CUSTOMER_API_OPERATIONS_HISTORY_PATH)
                .then()
                .assertThat()
                .statusCode(requestObject.getHttpStatus())
                .extract()
                .as(PaginatedOperationsHistoryResponseModel.class);
    }

    public PaginatedOperationsHistoryResponseModel getOperationHistoryErrorResponse(
            OperationsHistoryRequest requestObject) {
        return getHeader(requestObject.getToken())
                .queryParams(getQueryParams(requestObject))
                .get(CUSTOMER_API_OPERATIONS_HISTORY_PATH)
                .then()
                .assertThat()
                .statusCode(requestObject.getHttpStatus())
                .extract()
                .as(PaginatedOperationsHistoryResponseModel.class);
    }
}
