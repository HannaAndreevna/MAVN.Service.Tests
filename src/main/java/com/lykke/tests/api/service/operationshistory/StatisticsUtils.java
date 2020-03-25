package com.lykke.tests.api.service.operationshistory;

import static com.lykke.tests.api.base.PathConsts.OperationsHistoryApiEndpoint.TOKENS_PATH;
import static com.lykke.tests.api.base.Paths.OperationsHistory.STATISTICS_ACTIVE_CUSTOMERS_PATH;
import static com.lykke.tests.api.base.Paths.OperationsHistory.STATISTICS_CUSTOMERS_BY_DATE_PATH;
import static com.lykke.tests.api.base.Paths.OperationsHistory.STATISTICS_PATH;
import static com.lykke.api.testing.api.base.RequestHeader.getHeader;
import static com.lykke.tests.api.service.admin.LoginAdminUtils.getAdminToken;
import static java.util.stream.Collectors.toMap;
import static org.apache.commons.lang3.StringUtils.EMPTY;

import com.lykke.tests.api.service.operationshistory.model.ActiveCustomersRequest;
import com.lykke.tests.api.service.operationshistory.model.ActiveCustomersResponse;
import com.lykke.tests.api.service.operationshistory.model.Statistics.CustomersByDateResponseModel;
import com.lykke.tests.api.service.operationshistory.model.TokensStatistics.TokensStatisticsRequest;
import com.lykke.tests.api.service.operationshistory.model.TokensStatistics.TokensStatisticsResponse;
import com.lykke.tests.api.service.operationshistory.model.TokensStatistics.ValidationErrorTokensResponse;
import com.lykke.tests.api.service.operationshistory.model.ValidationErrorResponse;
import java.util.Map;
import java.util.stream.Stream;

public class StatisticsUtils {

    public static final String TO_DATE_FIELD = "toDate";
    public static final String FROM_DATE_FIELD = "fromDate";
    public static final String DATE_TO_FIELD = "dateTo";
    public static final String DATE_FROM_FIELD = "dateFrom";

    public static ActiveCustomersResponse getActiveCustomersResponse(ActiveCustomersRequest requestObject) {
        return getHeader(getAdminToken())
                .queryParams(getActiveCustomersParametersObject(requestObject))
                .get(STATISTICS_ACTIVE_CUSTOMERS_PATH)
                .then()
                .assertThat()
                .statusCode(requestObject.getHttpStatus())
                .extract()
                .as(ActiveCustomersResponse.class);
    }

    public static ValidationErrorResponse getActiveCustomersValidationResponse(ActiveCustomersRequest requestObject) {
        return getHeader(getAdminToken())
                .queryParams(getActiveCustomersParametersObject(requestObject))
                .get(STATISTICS_ACTIVE_CUSTOMERS_PATH)
                .then()
                .assertThat()
                .statusCode(requestObject.getHttpStatus())
                .extract()
                .as(ValidationErrorResponse.class);
    }

    public static TokensStatisticsResponse[] getTokensStatisticsResponse(TokensStatisticsRequest requestObject) {
        return getHeader()
                .queryParams(getTokenStatisticsObject(requestObject))
                .get(STATISTICS_PATH + TOKENS_PATH.getPath())
                .then()
                .assertThat()
                .statusCode(requestObject.getHttpStatus())
                .extract()
                .as(TokensStatisticsResponse[].class);
    }

    public static ValidationErrorTokensResponse getTokensStatisticsValidationsResponse(
            TokensStatisticsRequest requestObject) {
        return getHeader()
                .queryParams(getTokenStatisticsObject(requestObject))
                .get(STATISTICS_PATH + TOKENS_PATH.getPath())
                .then()
                .assertThat()
                .statusCode(requestObject.getHttpStatus())
                .extract()
                .as(ValidationErrorTokensResponse.class);
    }

    public static CustomersByDateResponseModel getCustomersByDate(ActiveCustomersRequest requestObject) {
        return getHeader()
                .queryParams(getActiveCustomersParametersObject(requestObject))
                .get(STATISTICS_CUSTOMERS_BY_DATE_PATH)
                .then()
                .assertThat()
                .statusCode(requestObject.getHttpStatus())
                .extract()
                .as(CustomersByDateResponseModel.class);
    }

    private static Map<String, String> getActiveCustomersParametersObject(ActiveCustomersRequest requestParameters) {

        return Stream.of(new String[][]{
                {FROM_DATE_FIELD, requestParameters.getFromDate()},
                {TO_DATE_FIELD, requestParameters.getToDate()}
        })
                .collect(toMap(item -> item[0], item -> null == item[1] ? EMPTY : item[1]));
    }

    private static Map<String, String> getTokenStatisticsObject(TokensStatisticsRequest requestParameters) {

        return Stream.of(new String[][]{
                {DATE_FROM_FIELD, requestParameters.getFormDate()},
                {DATE_TO_FIELD, requestParameters.getToDate()}
        })
                .collect(toMap(item -> item[0], item -> null == item[1] ? EMPTY : item[1]));
    }
}
