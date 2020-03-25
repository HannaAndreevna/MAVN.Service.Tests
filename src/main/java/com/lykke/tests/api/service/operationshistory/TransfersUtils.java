package com.lykke.tests.api.service.operationshistory;

import static com.lykke.tests.api.base.PathConsts.OperationsHistoryApiEndpoint.TRANSFERS_BY_ID_PATH;
import static com.lykke.tests.api.base.Paths.OperationsHistory.TRANSFERS_API_PATH;
import static com.lykke.api.testing.api.base.RequestHeader.getHeader;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.Map;
import lombok.experimental.UtilityClass;

@UtilityClass
public class TransfersUtils {

    private static final String CURRENT_PAGE_PARAMETER = "CurrentPage";
    private static final String PAGE_SIZE_PARAMETER = "PageSize";
    private static final String TRANSFERS_FIELD = "Transfers[0]";
    private static final String OTHER_SIDE_WALLET_ADDRESS_FIELD = ".OtherSideWalletAddress";

    @Step("Get Transfer by customer id {customerId}")
    public static Response getTransferById(String customerId, int currentPage, int pageSize) {
        return getHeader()
                .given()
                .queryParams(createTransferObject(currentPage, pageSize))
                .get(TRANSFERS_API_PATH + TRANSFERS_BY_ID_PATH.getFilledInPath(customerId));
    }

    public static String getOtherSideWalletAddress_LastTransfer(String customerId, int currentPage, int pageSize) {
        return getTransferById(customerId, currentPage, pageSize)
                .then()
                .extract()
                .path(TRANSFERS_FIELD + OTHER_SIDE_WALLET_ADDRESS_FIELD);
    }

    private static Map<String, Integer> createTransferObject(int currentPage, int pageSize) {
        Map createTransferObject = new HashMap();
        createTransferObject.put(CURRENT_PAGE_PARAMETER, currentPage);
        createTransferObject.put(PAGE_SIZE_PARAMETER, pageSize);
        return createTransferObject;
    }
}
