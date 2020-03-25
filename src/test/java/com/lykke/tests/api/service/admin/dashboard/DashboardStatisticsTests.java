package com.lykke.tests.api.service.admin.dashboard;

import static com.lykke.tests.api.common.CommonConsts.AWAITILITY_DEFAULT_MAX_SEC;
import static com.lykke.tests.api.common.CommonConsts.SMOKE_TEST;
import static com.lykke.tests.api.service.admin.DashboardUtils.getCustomerListValidationResponse;
import static com.lykke.tests.api.service.admin.DashboardUtils.getCustomersStatistics;
import static com.lykke.tests.api.service.admin.DashboardUtils.getLeadsListValidationResponse;
import static com.lykke.tests.api.service.admin.DashboardUtils.getLeadsStatistics;
import static com.lykke.tests.api.service.admin.DashboardUtils.getTokensListValidationResponse;
import static com.lykke.tests.api.service.admin.DashboardUtils.getTokensStatistics;
import static com.lykke.tests.api.service.admin.LoginAdminUtils.getAdminToken;
import static com.lykke.tests.api.service.tokenstatisticsjob.TokenStatisticsJobUtils.getTokenCurrent;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.lykke.api.testing.annotations.UserStoryId;
import com.lykke.tests.api.base.BaseApiTest;
import com.lykke.tests.api.service.admin.model.dashboard.DashboardStatisticsRequest;
import com.lykke.tests.api.service.admin.model.dashboard.customerstatistics.CustomersStatisticResponse;
import com.lykke.tests.api.service.admin.model.dashboard.tokenstatistics.TokensListResponse;
import com.lykke.tests.api.service.customermanagement.RegisterCustomerUtils;
import java.time.Instant;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.awaitility.Awaitility;
import org.awaitility.Duration;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

@Slf4j
public class DashboardStatisticsTests extends BaseApiTest {

    private static final String PAST_DATE = "2019-07-26";
    private static final String FUTURE_DATE = "2025-01-12";
    private static final String INVALID_DATE = "21-dec-12d";
    private static final String EMPTY_DATE = "''";
    private static final String NEWER_DATE = "2019-08-01";

    private static Double getBurnAmount(TokensListResponse tokensListResponse) {
        Double burnAmount = 0.0;
        for (int i = 0; i < tokensListResponse.getBurn().length; i++) {
            burnAmount += Double.valueOf(tokensListResponse.getBurn()[i].getAmount());
        }

        return burnAmount;
    }

    private static Double getEarnAmount(TokensListResponse tokensListResponse) {
        Double earnAmount = 0.0;
        for (int i = 0; i < tokensListResponse.getEarn().length; i++) {
            earnAmount += Double.valueOf(tokensListResponse.getEarn()[i].getAmount());
        }

        return earnAmount;
    }

    private static int getNewCustomersCount(CustomersStatisticResponse customersStatisticResponse) {
        int newCustomersCount = 0;
        for (int i = 0; i < customersStatisticResponse.getNewCustomers().length; i++) {
            newCustomersCount += customersStatisticResponse.getNewCustomers()[i].getCount();
        }

        return newCustomersCount;
    }

    @Test
    @UserStoryId(storyId = 2043)
    @Tag(SMOKE_TEST)
    void shouldGetLeadsStatistics() {
        val token = getAdminToken();
        val fromDate = PAST_DATE;
        val toDate = Instant.now().toString().split("T")[0];
        val requestModelObj = DashboardStatisticsRequest
                .builder()
                .fromDate(fromDate)
                .toDate(toDate)
                .build();

        val actualResult = getLeadsStatistics(requestModelObj, token);
        val numberOfLeadsRecords = actualResult.getLeads().length - 1;

        assertAll(
                () -> assertEquals(actualResult.getLeads()[numberOfLeadsRecords].getTotal(),
                        actualResult.getTotalNumber()),
                () -> assertEquals(fromDate, actualResult.getLeads()[0].getDay().split("T")[0]),
                () -> assertEquals(toDate,
                        actualResult.getLeads()[numberOfLeadsRecords].getDay().split("T")[0]),
                () -> assertNotNull(actualResult.getTotalNumber()),
                () -> assertEquals(actualResult.getTotalNumber(),
                        Arrays.stream(actualResult.getLeads()[numberOfLeadsRecords].getValue())
                                .mapToInt(lead -> lead.getCount())
                                .sum())
        );
    }

    @ParameterizedTest
    @CsvSource({INVALID_DATE + ", " + PAST_DATE,
            PAST_DATE + ", " + INVALID_DATE,
            PAST_DATE + ", " + FUTURE_DATE,
            NEWER_DATE + ", " + PAST_DATE,
            EMPTY_DATE + ", " + NEWER_DATE,
            PAST_DATE + ", " + EMPTY_DATE})
    @UserStoryId(storyId = 2043)
    void shouldNotGetLeadsWhenDatesAreNotValid(String fromDate, String toDate) {
        val requestModelObj = DashboardStatisticsRequest
                .builder()
                .fromDate(fromDate)
                .toDate(toDate)
                .build();
        if (SC_BAD_REQUEST == requestModelObj.getHttpStatus()) {
            requestModelObj.setHttpStatus(SC_UNAUTHORIZED);
            requestModelObj.setFromDate(EMPTY);
            requestModelObj.setToDate(EMPTY);
        }

        val actualResult = getLeadsListValidationResponse(requestModelObj);

        assertEquals(requestModelObj.getValidationResponse(), actualResult);
    }

    @Test
    @UserStoryId(storyId = 2054)
    @Tag(SMOKE_TEST)
    void shouldGetTokensStatistics() {
        val token = getAdminToken();
        val fromDate = PAST_DATE;
        val toDate = Instant.now().toString().split("T")[0];
        val requestModelObj = DashboardStatisticsRequest
                .builder()
                .fromDate(fromDate)
                .toDate(toDate)
                .build();

        val actualResult = getTokensStatistics(requestModelObj, token);

        //TODO: Compare to DB.
        //TODO: Compare with http://tokens-statistics.jobs get /api/general/byDays when Automated
        assertAll(
                () -> assertNotNull(actualResult.getBurn().length),
                () -> assertNotNull(actualResult.getEarn().length),
                () -> assertNotNull(actualResult.getTotalBurn()),
                () -> assertNotNull(actualResult.getTotalEarn()),
                () -> assertEquals(Double.parseDouble(getTokenCurrent().getTotalTokensAmount()),
                        Double.parseDouble(actualResult.getTotalWalletBalance())),
                () -> assertEquals(actualResult.getTotalWalletBalance(),
                        actualResult.getWalletBalance()[actualResult.getWalletBalance().length - 1].getAmount()),
                () -> assertEquals(getBurnAmount(actualResult), Double.parseDouble(actualResult.getTotalBurn())),
                () -> assertEquals(getEarnAmount(actualResult), Double.parseDouble(actualResult.getTotalEarn()))
        );
    }

    @ParameterizedTest
    @CsvSource({INVALID_DATE + ", " + PAST_DATE,
            PAST_DATE + ", " + INVALID_DATE,
            PAST_DATE + ", " + FUTURE_DATE,
            NEWER_DATE + ", " + PAST_DATE,
            EMPTY_DATE + ", " + NEWER_DATE,
            PAST_DATE + ", " + EMPTY_DATE})
    @UserStoryId(storyId = 2054)
    void shouldNotGetTokensWhenDatesAreNotValid(String fromDate, String toDate) {
        val requestModelObj = DashboardStatisticsRequest
                .builder()
                .fromDate(fromDate)
                .toDate(toDate)
                .build();
        if (SC_BAD_REQUEST == requestModelObj.getHttpStatus()) {
            requestModelObj.setHttpStatus(SC_UNAUTHORIZED);
            requestModelObj.setFromDate(EMPTY);
            requestModelObj.setToDate(EMPTY);
        }

        val actualResult = getTokensListValidationResponse(requestModelObj);

        assertEquals(requestModelObj.getValidationResponse(), actualResult);
    }

    @Test
    @UserStoryId(storyId = 2048)
    @Tag(SMOKE_TEST)
    void shouldGetCustomersStatistics() {
        val token = getAdminToken();
        val fromDate = PAST_DATE;
        val toDate = Instant.now().toString().split("T")[0];
        val requestModelObj = DashboardStatisticsRequest
                .builder()
                .fromDate(fromDate)
                .toDate(toDate)
                .build();

        val actualResult = getCustomersStatistics(requestModelObj, token);

        //TODO: Add more assertions. Compare to DB.
        assertAll(
                () -> assertNotNull(actualResult.getTotalCustomers()),
                () -> assertNotNull(actualResult.getTotalActiveCustomers()),
                () -> assertNotNull(actualResult.getTotalNewCustomers()),
                () -> assertNotNull(actualResult.getNewCustomers()),
                () -> assertEquals(actualResult.getTotalCustomers(),
                        actualResult.getTotalNonActiveCustomers() + actualResult.getTotalActiveCustomers()),
                () -> assertTrue(0 < actualResult.getNewCustomers().length),
                () -> assertEquals(fromDate, actualResult.getNewCustomers()[0].getDay().split("T")[0]),
                () -> assertEquals(toDate, actualResult
                        .getNewCustomers()[actualResult.getNewCustomers().length - 1].getDay().split("T")[0])
                // TODO: these counters don't match, at least since 2019-09-13
                // Expected :49476
                // Actual   :49508
                ,
                () -> assertEquals(actualResult.getTotalNewCustomers(), getNewCustomersCount(actualResult))
        );
    }

    @Test
    @UserStoryId(storyId = 2046)
    void shouldUpdateCustomersList() {
        val token = getAdminToken();
        val fromDate = PAST_DATE;
        val toDate = Instant.now().toString();
        val requestModelObj = DashboardStatisticsRequest
                .builder()
                .fromDate(fromDate)
                .toDate(toDate)
                .build();

        val expectedResult = getCustomersStatistics(requestModelObj, token);
        val expectedNewCustomersResult = expectedResult.getTotalNewCustomers() + 1;
        val expectedActiveCustomersResult = expectedResult.getTotalActiveCustomers() + 1;
        val expectedTotalCustomersResult = expectedResult.getTotalCustomers() + 1;
        val expectedNonActiveCustomersResult = expectedResult.getTotalNonActiveCustomers();

        RegisterCustomerUtils.registerCustomer();

        Awaitility.await()
                .atMost(AWAITILITY_DEFAULT_MAX_SEC, TimeUnit.SECONDS)
                .pollInterval(Duration.TWO_SECONDS)
                .until(() -> {
                    log.info("===============================================================================");
                    log.info("Expected TotalActiveCustomers: " + expectedActiveCustomersResult);
                    log.info("Expected TotalCustomers: " + expectedTotalCustomersResult);
                    log.info("Expected TotalNonActiveCustomers: " + expectedNonActiveCustomersResult);
                    log.info("Expected TotalNewCustomers: " + expectedNewCustomersResult);
                    log.info("===============================================================================");

                    System.out.println("expected: " + expectedActiveCustomersResult);
                    return expectedActiveCustomersResult == getCustomersStatistics(requestModelObj, token)
                            .getTotalActiveCustomers();
                });

        val actualResult = getCustomersStatistics(requestModelObj, token);

        assertAll(
                () -> assertEquals(expectedTotalCustomersResult, actualResult.getTotalCustomers()),
                () -> assertEquals(expectedNewCustomersResult, actualResult.getTotalNewCustomers()),
                () -> assertEquals(expectedActiveCustomersResult, actualResult.getTotalActiveCustomers()),
                () -> assertEquals(expectedNonActiveCustomersResult, actualResult.getTotalNonActiveCustomers()),
                () -> assertEquals(expectedResult
                                .getNewCustomers()[expectedResult.getNewCustomers().length - 1].getCount() + 1,
                        actualResult.getNewCustomers()[actualResult.getNewCustomers().length - 1].getCount())
        );
    }

    @ParameterizedTest
    @CsvSource({INVALID_DATE + ", " + PAST_DATE,
            PAST_DATE + ", " + INVALID_DATE,
            PAST_DATE + ", " + FUTURE_DATE,
            NEWER_DATE + ", " + PAST_DATE,
            EMPTY_DATE + ", " + NEWER_DATE,
            PAST_DATE + ", " + EMPTY_DATE})
    @UserStoryId(storyId = 2048)
    void shouldNotGetCustomersWhenDatesAreNotValid(String fromDate, String toDate) {
        val requestModelObj = DashboardStatisticsRequest
                .builder()
                .fromDate(fromDate)
                .toDate(toDate)
                .build();

        val actualResult = getCustomerListValidationResponse(requestModelObj);

        assertEquals(requestModelObj.getValidationResponse(), actualResult);
    }
}
