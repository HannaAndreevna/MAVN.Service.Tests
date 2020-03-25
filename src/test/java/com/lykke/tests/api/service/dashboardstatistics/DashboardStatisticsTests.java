package com.lykke.tests.api.service.dashboardstatistics;

import static com.lykke.tests.api.common.CommonConsts.AWAITILITY_DEFAULT_SEC;
import static com.lykke.tests.api.common.CommonConsts.SMOKE_TEST;
import static com.lykke.tests.api.service.dashboardstatistics.DashboardStatisticsUtils.getCustomersList;
import static com.lykke.tests.api.service.dashboardstatistics.DashboardStatisticsUtils.getCustomersListValidationResponse;
import static com.lykke.tests.api.service.dashboardstatistics.DashboardStatisticsUtils.getLeadsList;
import static com.lykke.tests.api.service.dashboardstatistics.DashboardStatisticsUtils.getLeadsListValidationResponse;
import static com.lykke.tests.api.service.dashboardstatistics.DashboardStatisticsUtils.getTokensList;
import static com.lykke.tests.api.service.dashboardstatistics.DashboardStatisticsUtils.getTokensListValidationResponse;
import static com.lykke.tests.api.service.tokenstatisticsjob.TokenStatisticsJobUtils.getTokenCurrent;
import static com.lykke.tests.api.service.tokenstatisticsjob.TokenStatisticsJobUtils.getTokensStatisticsForPeriodByDays;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.lykke.api.testing.annotations.UserStoryId;
import com.lykke.tests.api.base.BaseApiTest;
import com.lykke.tests.api.service.customermanagement.RegisterCustomerUtils;
import com.lykke.tests.api.service.dashboardstatistics.models.DashboardStatisticsRequest;
import com.lykke.tests.api.service.dashboardstatistics.models.customers.CustomersListResponseModel;
import com.lykke.tests.api.service.dashboardstatistics.models.leads.LeadsStatisticsModel;
import com.lykke.tests.api.service.dashboardstatistics.models.tokens.TokensListResponseModel;
import com.lykke.tests.api.service.tokenstatisticsjob.model.general.TokenStatisticsRequest;
import java.time.Instant;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.awaitility.Awaitility;
import org.awaitility.Duration;
import org.junit.jupiter.api.Disabled;
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
    private static final String NAME_FIELD = "name";

    private static Double getBurnAmount(TokensListResponseModel tokensListResponse) {
        Double burnAmount = 0.0;
        for (int i = 0; i < tokensListResponse.getBurn().length; i++) {
            burnAmount += Double.valueOf(tokensListResponse.getBurn()[i].getAmount());
        }

        return burnAmount;
    }

    private static Double getEarnAmount(TokensListResponseModel tokensListResponse) {
        return IntStream.range(0, tokensListResponse.getEarn().length)
                .mapToDouble(index -> Double.valueOf(tokensListResponse.getEarn()[index].getAmount()))
                .sum();
    }

    private static int getNewCustomersCount(CustomersListResponseModel customersListResponseModel) {
        return IntStream.range(0, customersListResponseModel.getNewCustomers().length)
                .map(index -> customersListResponseModel.getNewCustomers()[index].getCount())
                .sum();
    }

    @Test
    @UserStoryId(storyId = 2042)
    @Tag(SMOKE_TEST)
    void shouldGetCustomers() {
        val fromDate = PAST_DATE;
        val toDate = Instant.now().toString().split("T")[0];
        val requestModelObj = DashboardStatisticsRequest
                .builder()
                .fromDate(fromDate)
                .toDate(toDate)
                .build();

        val actualResult = getCustomersList(requestModelObj);

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
                        .getNewCustomers()[actualResult.getNewCustomers().length - 1].getDay().split("T")[0]),
                () -> assertEquals(actualResult.getTotalNewCustomers(), getNewCustomersCount(actualResult))
        );
    }

    @Test
    @UserStoryId(storyId = 2042)
    void shouldUpdateCustomersList() {
        val fromDate = PAST_DATE;
        val toDate = Instant.now().toString();
        val requestModelObj = DashboardStatisticsRequest
                .builder()
                .fromDate(fromDate)
                .toDate(toDate)
                .build();

        val expectedResult = getCustomersList(requestModelObj);
        val expectedNewCustomersResult = expectedResult.getTotalNewCustomers() + 1;
        val expectedActiveCustomersResult = expectedResult.getTotalActiveCustomers() + 1;
        val expectedTotalCustomersResult = expectedResult.getTotalCustomers() + 1;
        val expectedNonActiveCustomersResult = expectedResult.getTotalNonActiveCustomers();

        RegisterCustomerUtils.registerCustomer();

        Awaitility.await()
                .atMost(AWAITILITY_DEFAULT_SEC, TimeUnit.SECONDS)
                .pollInterval(Duration.TWO_SECONDS)
                .until(() -> {
                    log.info("===============================================================================");
                    log.info("Expected TotalActiveCustomers: " + expectedActiveCustomersResult);
                    log.info("Expected TotalCustomers: " + expectedTotalCustomersResult);
                    log.info("Expected TotalNonActiveCustomers: " + expectedNonActiveCustomersResult);
                    log.info("Expected TotalNewCustomers: " + expectedNewCustomersResult);
                    log.info("===============================================================================");

                    return expectedActiveCustomersResult == getCustomersList(requestModelObj).getTotalActiveCustomers();
                });

        val actualResult = getCustomersList(requestModelObj);

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

    @Test
    @UserStoryId(storyId = 2042)
    @Tag(SMOKE_TEST)
    void shouldGetLeads() {
        val fromDate = PAST_DATE;
        val toDate = Instant.now().toString().split("T")[0];
        val requestModelObj = DashboardStatisticsRequest
                .builder()
                .fromDate(fromDate)
                .toDate(toDate)
                .build();

        val actualResult = getLeadsList(requestModelObj);

        val numberOfLeadsRecords = actualResult.getLeads().length - 1;

        //TODO: Compere to DB.
        assertAll(
                () -> assertEquals(actualResult.getLeads()[numberOfLeadsRecords].getTotal(),
                        actualResult.getTotalNumber()),
                () -> assertEquals(fromDate, actualResult.getLeads()[0].getDay().split("T")[0]),
                // TODO: investigate into it        () -> assertEquals(toDate,
                // TODO: investigate into it                actualResult.getLeads()[numberOfLeadsRecords].getDay().split("T")[0]),
                () -> assertNotNull(actualResult.getTotalNumber()),
                () -> assertEquals(actualResult.getTotalNumber(),
                        Arrays.stream(actualResult.getLeads()[numberOfLeadsRecords].getValue())
                                .mapToInt(LeadsStatisticsModel::getCount)
                                .sum())
        );
    }

    @Test
    @UserStoryId(storyId = 2042)
    @Tag(SMOKE_TEST)
    void shouldGetTokens() {
        val fromDate = PAST_DATE;
        val toDate = Instant.now().toString();
        val requestModelObj = DashboardStatisticsRequest
                .builder()
                .fromDate(fromDate)
                .toDate(toDate)
                .build();

        val actualResult = getTokensList(requestModelObj);

        val expectedResult = getTokensStatisticsForPeriodByDays(TokenStatisticsRequest
                .builder()
                .fromDate(fromDate)
                .toDate(toDate)
                .build());
////xx
        assertAll(
                () -> assertEquals(expectedResult.getTotalBurn(), actualResult.getTotalBurn()),
                () -> assertEquals(expectedResult.getTotalEarn(), actualResult.getTotalEarn()),
                () -> assertEquals(expectedResult.getTotalWalletBalance(), actualResult.getTotalWalletBalance()),
                () -> assertNotNull(actualResult.getBurn().length),
                () -> assertNotNull(actualResult.getEarn().length),
                () -> assertNotNull(actualResult.getTotalBurn()),
                () -> assertNotNull(actualResult.getTotalEarn()),
                () -> assertEquals(Double.parseDouble(getTokenCurrent().getTotalTokensAmount()),
                        Double.valueOf(actualResult.getTotalWalletBalance())),
                () -> assertEquals(actualResult.getTotalWalletBalance(),
                        actualResult.getWalletBalance()[actualResult.getWalletBalance().length - 1].getAmount()),
                () -> assertEquals(getBurnAmount(actualResult), Double.valueOf(actualResult.getTotalBurn())),
                () -> assertEquals(getEarnAmount(actualResult), Double.valueOf(actualResult.getTotalEarn()))
        );
    }

    @ParameterizedTest
    @CsvSource({INVALID_DATE + ", " + PAST_DATE,
            PAST_DATE + ", " + INVALID_DATE,
            PAST_DATE + ", " + FUTURE_DATE,
            NEWER_DATE + ", " + PAST_DATE,
            EMPTY_DATE + ", " + NEWER_DATE,
            PAST_DATE + ", " + EMPTY_DATE})
    @UserStoryId(storyId = 2042)
    void shouldNotGetCustomersWhenDatesAreNotValid(String fromDate, String toDate) {
        val requestModelObj = DashboardStatisticsRequest
                .builder()
                .fromDate(fromDate)
                .toDate(toDate)
                .build();

        val actualResult = getCustomersListValidationResponse(requestModelObj);

        assertEquals(requestModelObj.getValidationResponse(), actualResult);
    }

    @ParameterizedTest
    @CsvSource({INVALID_DATE + ", " + PAST_DATE,
            PAST_DATE + ", " + INVALID_DATE,
            PAST_DATE + ", " + FUTURE_DATE,
            NEWER_DATE + ", " + PAST_DATE,
            EMPTY_DATE + ", " + NEWER_DATE,
            PAST_DATE + ", " + EMPTY_DATE})
    @UserStoryId(storyId = 2042)
    void shouldNotGetLeadsWhenDatesAreNotValid(String fromDate, String toDate) {
        val requestModelObj = DashboardStatisticsRequest
                .builder()
                .fromDate(fromDate)
                .toDate(toDate)
                .build();

        val actualResult = getLeadsListValidationResponse(requestModelObj);

        assertEquals(requestModelObj.getValidationResponse(), actualResult);
    }

    @ParameterizedTest
    @CsvSource({INVALID_DATE + ", " + PAST_DATE,
            PAST_DATE + ", " + INVALID_DATE,
            PAST_DATE + ", " + FUTURE_DATE,
            NEWER_DATE + ", " + PAST_DATE,
            EMPTY_DATE + ", " + NEWER_DATE,
            PAST_DATE + ", " + EMPTY_DATE})
    @UserStoryId(storyId = 2042)
    void shouldNotGetTokensWhenDatesAreNotValid(String fromDate, String toDate) {
        val requestModelObj = DashboardStatisticsRequest
                .builder()
                .fromDate(fromDate)
                .toDate(toDate)
                .build();

        val actualResult = getTokensListValidationResponse(requestModelObj);

        assertEquals(requestModelObj.getValidationResponse(), actualResult);
    }
}
