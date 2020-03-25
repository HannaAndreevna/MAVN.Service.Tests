package com.lykke.tests.api.service.tokensstatisticsjob;

import static com.lykke.tests.api.base.BasicFunctionalities.DATE_FORMAT_YYYY_MM_DD;
import static com.lykke.tests.api.base.BasicFunctionalities.getTomorrowsDate;
import static com.lykke.tests.api.base.BasicFunctionalities.getYesterdayDateString;
import static com.lykke.tests.api.common.CommonConsts.AWAITILITY_DEFAULT_SEC;
import static com.lykke.tests.api.common.CommonConsts.SMOKE_TEST;
import static com.lykke.tests.api.service.admin.CampaignUtils.createCampaignAndReturnId;
import static com.lykke.tests.api.service.admin.CampaignUtils.deleteCampaign;
import static com.lykke.tests.api.service.customermanagement.RegisterCustomerUtils.registerDefaultVerifiedCustomer;
import static com.lykke.tests.api.service.dashboardstatistics.DashboardStatisticsUtils.getTokensListValidationResponse;
import static com.lykke.tests.api.service.tokenstatisticsjob.TokenStatisticsJobUtils.getTokenCurrent;
import static com.lykke.tests.api.service.tokenstatisticsjob.TokenStatisticsJobUtils.getTokensByDate;
import static com.lykke.tests.api.service.tokenstatisticsjob.TokenStatisticsJobUtils.getTokensStatisticsForPeriodByDays;
import static com.lykke.tests.api.service.tokenstatisticsjob.TokenStatisticsJobUtils.getValidationErrorResponseModel;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.params.provider.Arguments.of;

import com.lykke.api.testing.annotations.UserStoryId;
import com.lykke.tests.api.base.BaseApiTest;
import com.lykke.tests.api.service.dashboardstatistics.models.DashboardStatisticsRequest;
import com.lykke.tests.api.service.tokenstatisticsjob.model.general.TokenStatisticsRequest;
import com.lykke.tests.api.service.tokenstatisticsjob.model.general.TokensErrorCode;
import com.lykke.tests.api.service.tokenstatisticsjob.model.general.TokensRequest;
import java.time.Instant;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;
import lombok.val;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

public class GetTokensTests extends BaseApiTest {

    private static final String INVALID_DATE_1 = "123dasd";
    private static final String INVALID_DATE_2 = "16-06-2019";
    private static final String STATISTICS_NOT_FOUND_ERR_CODE = "StatisticsNotFound";
    private static final TokensErrorCode NONE_ERR_CODE = TokensErrorCode.NONE;
    private static final String ZERO_TOKENS = "0";
    private static final String PAST_DATE = "2019-07-26";
    private static final String FUTURE_DATE = "2025-01-12";
    private static final String INVALID_DATE = "21-dec-12d";
    private static final String EMPTY_DATE = "''";
    private static final String NEWER_DATE = "2019-08-01";

    public static Stream<Arguments> getInvalidDateParameters() {
        return Stream.of(
                of(EMPTY),
                of(INVALID_DATE_1),
                of(INVALID_DATE_2)
        );
    }

    @ParameterizedTest(name = "Run {index}: invalidDate={0}")
    @MethodSource("getInvalidDateParameters")
    @UserStoryId(storyId = 1126)
    public void shouldNotGetTokensByDate(String invalidDate) {
        val requestObject = TokensRequest
                .builder()
                .date(invalidDate)
                .build();

        val validationResponse = getValidationErrorResponseModel(requestObject);
        assertEquals(requestObject.getValidationErrorResponse(), validationResponse);
    }

    @Test
    @UserStoryId(storyId = 1126)
    public void shouldNotGetTokensByDate_NotFound() {
        val currentDate = DATE_FORMAT_YYYY_MM_DD.format(getTomorrowsDate().getTime());

        val requestObject = TokensRequest
                .builder()
                .date(currentDate)
                .build();

        val tokensByDateResponse = getTokensByDate(requestObject);

        assertAll(
                () -> assertEquals(ZERO_TOKENS, tokensByDateResponse.getTotalTokensAmount()),
                () -> assertEquals(STATISTICS_NOT_FOUND_ERR_CODE, tokensByDateResponse.getErrorCode().getCode())
        );
    }

    @Test
    @Tag(SMOKE_TEST)
    @UserStoryId(storyId = 1126)
    public void shouldGetTokensByDate() {
        val yesterdayDate = getYesterdayDateString();

        val requestObject = TokensRequest
                .builder()
                .date(yesterdayDate)
                .build();

        val tokensByDateResponse = getTokensByDate(requestObject);

        assertAll(
                () -> assertNotEquals(ZERO_TOKENS, tokensByDateResponse.getTotalTokensAmount()),
                () -> assertEquals(NONE_ERR_CODE, tokensByDateResponse.getErrorCode())
        );
    }

    @Disabled("Enable when FAL-3038 is fixed")
    @Test
    @Tag(SMOKE_TEST)
    @UserStoryId(storyId = 1161)
    public void shouldGetCurrentTokens() {
        val currentTokensAmount = getTokenCurrent().getTotalTokensAmount();

        val campaignId = createCampaignAndReturnId();
        registerDefaultVerifiedCustomer();
        Awaitility.await().atMost(AWAITILITY_DEFAULT_SEC, TimeUnit.SECONDS)
                .until(() -> currentTokensAmount != getTokenCurrent().getTotalTokensAmount());

        val currentTokensResponse = getTokenCurrent();

        assertAll(
                () -> assertNotEquals(currentTokensAmount, currentTokensResponse.getTotalTokensAmount()),
                () -> assertEquals(NONE_ERR_CODE, currentTokensResponse.getErrorCode())
        );

        deleteCampaign(campaignId);
    }

    @Test
    @UserStoryId(storyId = 2056)
    @Tag(SMOKE_TEST)
    void shouldGetTokens() {
        val fromDate = PAST_DATE;
        val toDate = Instant.now().toString();
        val requestModelObj = TokenStatisticsRequest
                .builder()
                .fromDate(fromDate)
                .toDate(toDate)
                .build();

        val actualResult = getTokensStatisticsForPeriodByDays(requestModelObj);

        //TODO: Compare to DB.
        assertAll(
                () -> assertNotNull(actualResult.getBurn().length),
                () -> assertNotNull(actualResult.getEarn().length),
                () -> assertNotNull(actualResult.getTotalBurn()),
                () -> assertNotNull(actualResult.getTotalEarn()),
                () -> assertEquals(Double.parseDouble(getTokenCurrent().getTotalTokensAmount()),
                        Double.valueOf(actualResult.getTotalWalletBalance())),
                () -> assertEquals(actualResult.getTotalWalletBalance(),
                        actualResult.getWalletBalance()[actualResult.getWalletBalance().length - 1].getAmount())
        );
    }

    @ParameterizedTest
    @CsvSource({INVALID_DATE + ", " + PAST_DATE,
            PAST_DATE + ", " + INVALID_DATE,
            PAST_DATE + ", " + FUTURE_DATE,
            NEWER_DATE + ", " + PAST_DATE,
            EMPTY_DATE + ", " + NEWER_DATE,
            PAST_DATE + ", " + EMPTY_DATE})
    @UserStoryId(storyId = 2056)
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
