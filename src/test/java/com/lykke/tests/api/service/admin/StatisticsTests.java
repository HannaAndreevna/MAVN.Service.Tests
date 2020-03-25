package com.lykke.tests.api.service.admin;

import static com.lykke.tests.api.common.CommonConsts.AWAITILITY_DEFAULT_SEC;
import static com.lykke.tests.api.common.CommonConsts.SMOKE_TEST;
import static com.lykke.tests.api.service.admin.StatisticsUtils.getCurrentTokens;
import static com.lykke.tests.api.service.admin.StatisticsUtils.getCustomerStatistics;
import static com.lykke.tests.api.service.customerprofile.CustomerProfileUtils.getRegistrationCount;
import static com.lykke.tests.api.service.customerprofile.CustomerProfileUtils.getTotalCount;
import static com.lykke.tests.api.service.operationshistory.StatisticsUtils.getActiveCustomersResponse;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.lykke.api.testing.annotations.UserStoryId;
import com.lykke.tests.api.base.BaseApiTest;
import com.lykke.tests.api.service.operationshistory.model.ActiveCustomersRequest;
import com.lykke.tests.api.service.tokenstatisticsjob.TokenStatisticsJobUtils;
import com.lykke.tests.api.service.tokenstatisticsjob.model.general.TokenStatisticsRequest;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.concurrent.TimeUnit;
import lombok.val;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

public class StatisticsTests extends BaseApiTest {

    @Disabled("needs test refactor")
    @Test
    @Tag(SMOKE_TEST)
    @UserStoryId(storyId = 932)
    void shouldGetCustomerStatistics() {
        val startDate = getYesterdaysDateInUTC() + "T00:00";
        val endDate = getYesterdaysDateInUTC() + "T23:59";
        val token = LoginAdminUtils.getAdminToken();
        val customerStatistics = getCustomerStatistics(token);
        val requestObject = ActiveCustomersRequest
                .builder()
                .fromDate("2019-01-01T15:18:47.978Z")
                .toDate(endDate)
                .build();

        Awaitility.await().atMost(AWAITILITY_DEFAULT_SEC, TimeUnit.SECONDS)
                .until(() -> customerStatistics.getActiveCount() + 26 <= getActiveCustomersResponse(requestObject)
                        .getActiveCustomersCount());
        val activeCustomersResponse = getActiveCustomersResponse(requestObject);

        // TODO: magic numbers
        assertAll(
                () -> assertEquals(customerStatistics.getActiveCount() + 26 + 44243,
                        activeCustomersResponse.getActiveCustomersCount()),
                () -> assertEquals(customerStatistics.getRegistrationsCount() + 37,
                        getRegistrationCount(startDate, endDate)),
                () -> assertEquals(customerStatistics.getTotalCount() + 37, getTotalCount(startDate, endDate))
        );
    }

    @Test
    @Tag(SMOKE_TEST)
    @UserStoryId(storyId = {937, 2463})
    void shouldGetTokensStatistics() {
        val token = LoginAdminUtils.getAdminToken();
        val currentTokens = getCurrentTokens(token);
        val requestObject = TokenStatisticsRequest
                .builder()
                .fromDate("2019-01-01T15:18:47.978Z")
                .toDate(getTodayInUTC() + "T23:59")
                .build();

        val tokensStatisticsResponse = TokenStatisticsJobUtils.getTokensStatisticsForPeriodByDays(requestObject);

        assertAll(
                () -> assertEquals(tokensStatisticsResponse.getTotalEarn(), currentTokens.getEarnedCount()),
                () -> assertEquals(tokensStatisticsResponse.getTotalBurn(), currentTokens.getBurnedCount()),
                () -> assertEquals(tokensStatisticsResponse.getTotalWalletBalance(), currentTokens.getTotalCount())
        );
    }

    String getTodayInUTC() {
        String[] todayDate = OffsetDateTime.now(ZoneOffset.UTC).toString().split("T");
        return todayDate[0];
    }

    String getYesterdaysDateInUTC() {
        String[] yesterdaysDate = OffsetDateTime.now(ZoneOffset.UTC).minusDays(1).toString().split("T");
        return yesterdaysDate[0];
    }
}
