package com.lykke.tests.api.service.admin;

import static com.lykke.api.testing.api.base.RequestHeader.getHeader;
import static com.lykke.tests.api.base.PathConsts.AdminApiService.CURRENT_TOKENS_PATH;
import static com.lykke.tests.api.base.PathConsts.AdminApiService.CUSTOMERS_PATH;
import static com.lykke.tests.api.base.Paths.AdminApi.ADMIN_STATISTICS_API_PATH;
import static org.apache.http.HttpStatus.SC_OK;

import com.lykke.tests.api.service.admin.model.statistics.CustomerStatisticsResponse;
import com.lykke.tests.api.service.admin.model.statistics.TokensStatisticsResponse;
import lombok.experimental.UtilityClass;

@UtilityClass
public class StatisticsUtils {

    CustomerStatisticsResponse getCustomerStatistics(String token) {
        return getHeader(token)
                .get(ADMIN_STATISTICS_API_PATH + CUSTOMERS_PATH.getPath())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(CustomerStatisticsResponse.class);
    }

    TokensStatisticsResponse getCurrentTokens(String token) {
        return getHeader(token)
                .get(ADMIN_STATISTICS_API_PATH + CURRENT_TOKENS_PATH.getPath())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(TokensStatisticsResponse.class);
    }
}
