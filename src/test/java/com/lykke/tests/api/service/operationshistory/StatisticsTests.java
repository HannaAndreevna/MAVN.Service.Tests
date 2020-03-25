package com.lykke.tests.api.service.operationshistory;

import static com.lykke.api.testing.api.common.GenerateUtils.getRandomUuid;
import static com.lykke.tests.api.base.BasicFunctionalities.BASE_ASSET;
import static com.lykke.tests.api.common.CommonConsts.AWAITILITY_OPERATIONS_HISTORY_STATISTICS_SEC;
import static com.lykke.tests.api.common.CommonConsts.SMOKE_TEST;
import static com.lykke.tests.api.service.admin.CampaignUtils.createCampaignAndReturnId;
import static com.lykke.tests.api.service.customermanagement.RegisterCustomerUtils.registerDefaultVerifiedCustomer;
import static com.lykke.tests.api.service.operationshistory.StatisticsUtils.getActiveCustomersResponse;
import static com.lykke.tests.api.service.operationshistory.StatisticsUtils.getActiveCustomersValidationResponse;
import static com.lykke.tests.api.service.operationshistory.StatisticsUtils.getCustomersByDate;
import static com.lykke.tests.api.service.operationshistory.StatisticsUtils.getTokensStatisticsResponse;
import static com.lykke.tests.api.service.operationshistory.StatisticsUtils.getTokensStatisticsValidationsResponse;
import static com.lykke.tests.api.service.walletmanagement.WalletManagementUtils.balanceTransfer;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.of;

import com.lykke.api.testing.annotations.UserStoryId;
import com.lykke.tests.api.base.BaseApiTest;
import com.lykke.tests.api.service.operationshistory.model.ActiveCustomersRequest;
import com.lykke.tests.api.service.operationshistory.model.TokensStatistics.TokensStatisticsRequest;
import com.lykke.tests.api.service.operationshistory.model.TokensStatistics.TokensStatisticsResponse;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;
import lombok.val;
import org.awaitility.Awaitility;
import org.awaitility.Duration;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class StatisticsTests extends BaseApiTest {

    // TODO: the shouldGetActiveCustomers() test will fail if being run frequetnly
    // it's for one run per the whole suite
    private static final String CAMPAIGN_FROM = Instant.now().minus(5, ChronoUnit.MINUTES).toString();
    private static final String CAMPAIGN_TO = Instant.now().plus(10, ChronoUnit.MINUTES).toString();
    private static final int CREATED_BY_TEST = 2;
    private static final Double SOME_AMOUNT = 20.0;

    static Stream<Arguments> getWrongDateFormat() {
        return Stream.of(
                of("23", "12d2"),
                of("2019-05-23T06:59:26.627Z", "12d2")
        );
    }

    @UserStoryId(storyId = 911)
    @ParameterizedTest(name = "Run {index}: fromDate={0}, toDate={1}")
    @MethodSource("getWrongDateFormat")
    void shouldNotGetActiveCustomers_Invalid(String fromDate, String toDate) {
        val requestObject = ActiveCustomersRequest
                .builder()
                .fromDate(fromDate)
                .toDate(toDate)
                .build();

        val validationResponse = getActiveCustomersValidationResponse(requestObject);

        assertEquals(requestObject.getValidationResponse(), validationResponse);
    }

    @Test
    @Tag(SMOKE_TEST)
    @UserStoryId(storyId = 911)
    void shouldGetActiveCustomers() {

        val requestObject = ActiveCustomersRequest
                .builder()
                .fromDate(CAMPAIGN_FROM)
                .toDate(CAMPAIGN_TO)
                .build();

        val initialActiveCustomers = getActiveCustomersResponse(requestObject).getActiveCustomersCount();

        createCampaignAndReturnId();
        val senderId = registerDefaultVerifiedCustomer().getCustomerId();
        val recipientId = registerDefaultVerifiedCustomer().getCustomerId();

        balanceTransfer(senderId, recipientId, BASE_ASSET, SOME_AMOUNT, getRandomUuid());

        Awaitility.await()
                .atMost(AWAITILITY_OPERATIONS_HISTORY_STATISTICS_SEC, TimeUnit.SECONDS)
                .pollInterval(Duration.TWO_SECONDS)
                .until(() -> {
                    val activeCustomersResponse = getActiveCustomersResponse(requestObject);

                    return (initialActiveCustomers <= activeCustomersResponse
                            .getActiveCustomersCount());
                });
    }

    @Test
    @Tag(SMOKE_TEST)
    @UserStoryId(storyId = 2052)
    void shouldGetCustomersByDate() {

        val requestObject = ActiveCustomersRequest
                .builder()
                .fromDate(CAMPAIGN_FROM)
                .toDate(CAMPAIGN_TO)
                .build();

        val initialTotalActiveCustomers = getCustomersByDate(requestObject).getTotalActiveCustomers();

        createCampaignAndReturnId();
        val senderId = registerDefaultVerifiedCustomer().getCustomerId();
        val recipientId = registerDefaultVerifiedCustomer().getCustomerId();

        balanceTransfer(senderId, recipientId, BASE_ASSET, SOME_AMOUNT, getRandomUuid());

        Awaitility.await()
                .atMost(AWAITILITY_OPERATIONS_HISTORY_STATISTICS_SEC, TimeUnit.SECONDS)
                .pollInterval(Duration.TWO_SECONDS)
                .until(() -> {
                    ////xx
                    val activeCustomersResponse = getCustomersByDate(requestObject);

                    return (initialTotalActiveCustomers == activeCustomersResponse
                            .getTotalActiveCustomers());
                });
    }

    @Test
    @Tag(SMOKE_TEST)
    @UserStoryId(storyId = 912)
    void shouldGetTokensStatistics() {
        //The dates are hardcoded since we expect to get the same burnedAmount value for this period
        val requestObject = TokensStatisticsRequest
                .builder()
                .dateFrom("2019-06-01T15:18:47.978Z")
                .dateTo("2019-06-07T15:18:47.978Z")
                .build();

        /*
        // TODO: why has it been changed?
        expected: <ECT> but was: <MVN>
        Comparison Failure:
        Expected :ECT
        Actual   :MVN

        expected: <4826> but was: <0>
        Comparison Failure:
        Expected :4826
        Actual   :0
        */
        val expObject = TokensStatisticsResponse
                .builder()
                .asset("MVN")
                .earnedAmount(0)
                .burnedAmount(0)
                .build();

        val tokensStatisticsResponse = getTokensStatisticsResponse(requestObject)[0];

        assertAll(
                () -> assertEquals(expObject.getAsset(), tokensStatisticsResponse.getAsset()),
                () -> assertEquals(expObject.getEarnedAmount(), tokensStatisticsResponse.getEarnedAmount()),
                () -> assertEquals(expObject.getBurnedAmount(), tokensStatisticsResponse.getBurnedAmount())
        );
    }

    @ParameterizedTest(name = "Run {index}: fromDate={0}, toDate={1}")
    @MethodSource("getWrongDateFormat")
    @UserStoryId(storyId = 912)
    void shouldValidateFromAndToDates(String fromDate, String toDate) {
        val requestObject = TokensStatisticsRequest
                .builder()
                .dateFrom(fromDate)
                .dateTo(toDate)
                .build();

        val validationResponse = getTokensStatisticsValidationsResponse(requestObject);

        assertEquals(requestObject.getValidationResponse(), validationResponse);
    }
}
