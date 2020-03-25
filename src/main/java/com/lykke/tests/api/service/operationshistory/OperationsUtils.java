package com.lykke.tests.api.service.operationshistory;

import static com.lykke.api.testing.api.base.RequestHeader.getHeader;
import static com.lykke.api.testing.api.common.QueryParamsUtils.getQueryParams;
import static com.lykke.tests.api.base.PathConsts.OperationsHistoryApiEndpoint.TRANSACTIONS_BY_DATE;
import static com.lykke.tests.api.base.PathConsts.OperationsHistoryApiEndpoint.TRANSACTIONS_BY_ID_PATH;
import static com.lykke.tests.api.base.PathConsts.getFullPath;
import static com.lykke.tests.api.base.Paths.OperationsHistory.OPERATIONS_BY_CUSTOMER_ID_API_PATH;
import static com.lykke.tests.api.base.Paths.OperationsHistory.TRANSACTIONS_API_PATH;
import static com.lykke.tests.api.base.Paths.OperationsHistory.VOUCHER_PURCHASES_API_PATH;

import com.lykke.tests.api.service.operationshistory.model.PaginationModelWithDatesRange;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.Map;
import lombok.experimental.UtilityClass;

@UtilityClass
public class OperationsUtils {

    private static final String CURRENT_PAGE_PARAMETER = "CurrentPage";
    private static final String PAGE_SIZE_PARAMETER = "PageSize";

    @Step("Get transactions by customer id {customerId}")
    public Response getTransactionsById(String customerId, int currentPage, int pageSize) {
        return getHeader()
                .given()
                .queryParams(createTransactionsObject(currentPage, pageSize))
                .get(TRANSACTIONS_API_PATH + TRANSACTIONS_BY_ID_PATH.getFilledInPath(customerId));
    }

    public Response getTransactionsByDate(String fromDate, String toDate, int currentPage, int pageSize) {
        return getHeader()
                .given()
                .queryParams(createTransactionsObject(currentPage, pageSize))
                .get(TRANSACTIONS_API_PATH + getFullPath(TRANSACTIONS_BY_DATE.getPath(), fromDate,
                        toDate));
    }

    @Step("Get transactions by customer id {customerId}")
    public static Response getTransactionsByCustomerId(String customerId) {
        return getHeader()
                .queryParams(createTransactionsObject(1, 100))
                .get(OPERATIONS_BY_CUSTOMER_ID_API_PATH.apply(customerId))
                .thenReturn();
    }

    public Response getVoucherPurchases(PaginationModelWithDatesRange requestModel) {
        return getHeader()
                .queryParams(getQueryParams(requestModel))
                .get(VOUCHER_PURCHASES_API_PATH)
                .thenReturn();
    }

    private static Map<String, Integer> createTransactionsObject(int currentPage, int pageSize) {
        Map createTransactionsByIdObject = new HashMap();
        createTransactionsByIdObject.put(CURRENT_PAGE_PARAMETER, currentPage);
        createTransactionsByIdObject.put(PAGE_SIZE_PARAMETER, pageSize);
        return createTransactionsByIdObject;
    }
}
