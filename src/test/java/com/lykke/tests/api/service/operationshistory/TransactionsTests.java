package com.lykke.tests.api.service.operationshistory;

import static com.lykke.api.testing.api.common.GenerateUtils.getRandomUuid;
import static com.lykke.tests.api.base.BasicFunctionalities.BASE_ASSET;
import static com.lykke.tests.api.common.CommonConsts.SMOKE_TEST;
import static com.lykke.tests.api.common.CommonMethods.createDefaultSignUpCampaign;
import static com.lykke.tests.api.service.campaigns.BaseCampaignTest.deleteAllCampaigns;
import static com.lykke.tests.api.service.customermanagement.RegisterCustomerUtils.registerCustomer;
import static com.lykke.tests.api.service.operationshistory.OperationsUtils.getTransactionsByDate;
import static com.lykke.tests.api.service.operationshistory.OperationsUtils.getTransactionsById;
import static com.lykke.tests.api.service.walletmanagement.WalletManagementUtils.balanceTransfer;
import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.params.provider.Arguments.of;

import com.lykke.api.testing.annotations.UserStoryId;
import com.lykke.tests.api.base.BaseApiTest;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.stream.Stream;
import lombok.val;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class TransactionsTests extends BaseApiTest {

    private static final int TOO_LONG_INT = 2_147_483_647 + 1;
    private static final String ERROR_MESSAGE_FIELD = "ErrorMessage";
    private static final String CURRENT_PAGE_LOWER_BOUND_MESSAGE = "Current page can't be less than 1";
    private static final String PAGE_SIZE_LOWER_BOUND_MESSAGE = "Page Size can't be less than 1";
    private static final String PAGE_SIZE_UPPER_BOUND_MESSAGE = "Page Size cannot exceed more then 1000";
    private static final String INVALID_DATE_ERR_MSG = "The value '21-05-2019' is not valid.";
    private static final String TEST_ASSET = "TestAsset";
    private static final String TRANSFERS_FIELD = "Transfers";
    private static final String BONUS_CASH_INS_FIELD = "BonusCashIns[0]";
    private static final String TRANSACTIONS_HISTORY_FIELD = "TransactionsHistory";
    private static final String TRANSACTIONS_HISTORY_TIMESTAMP_FIELD = TRANSACTIONS_HISTORY_FIELD + ".Timestamp[0]";
    private static final String TRANSFERS_TIMESTAMP_FIELD = TRANSFERS_FIELD + ".Timestamp[0]";
    private static final String FROM_DATE = "fromDate";
    private static final String TO_DATE = "toDate";
    private static final String INVALID_DATE_FORMAT = "21-05-2019";
    private static final String TEST_OPERATION_ID = getRandomUuid();
    private static final String NAME_FIELD = "name";
    private static final String PARTNER_ID_FIELD = "PartnerId";
    private static final String CAMPAIGN_NAME_FIELD = "CampaignName";
    private static final String WALLETS_ADDRESS_FIELD = TRANSACTIONS_HISTORY_FIELD + ".WalletAddress[0]";
    private static final double SOME_AMOUNT = 20.0;

    private static String customerId;
    private static String testCustomer;
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private static String currentDate = dateFormat.format(getCurrentDate().getTime());

    private static Stream<Arguments> shouldNotGetTransactionsById_Invalid() {
        return Stream.of(
                of(-1, 2, CURRENT_PAGE_LOWER_BOUND_MESSAGE),
                of(2, -1, PAGE_SIZE_LOWER_BOUND_MESSAGE),
                of(-2, -1, PAGE_SIZE_LOWER_BOUND_MESSAGE),
                of(TOO_LONG_INT, 1, CURRENT_PAGE_LOWER_BOUND_MESSAGE),
                of(1, 1001, PAGE_SIZE_UPPER_BOUND_MESSAGE)
        );
    }

    private static Stream<Arguments> shouldGetTransactionsById_Valid() {
        return Stream.of(
                of(1, 5),
                of(2, 1)
        );
    }

    private static Stream<Arguments> shouldGetTransactionsByDate_Valid() {
        HashMap<String, String> testDates = getTransfersTestDates();
        return Stream.of(
                of(1, 2, testDates.get(FROM_DATE), testDates.get(TO_DATE)),
                of(2, 1, testDates.get(FROM_DATE), testDates.get(TO_DATE))
        );
    }

    private static Stream<Arguments> shouldNotGetTransactionsByDate_Invalid() {
        HashMap<String, String> testDates = getTransfersTestDates();
        return Stream.of(
                of(-1, 2, testDates.get(FROM_DATE), testDates.get(TO_DATE), CURRENT_PAGE_LOWER_BOUND_MESSAGE),
                of(2, -1, testDates.get(FROM_DATE), testDates.get(TO_DATE), PAGE_SIZE_LOWER_BOUND_MESSAGE),
                of(-2, -1, testDates.get(FROM_DATE), testDates.get(TO_DATE), PAGE_SIZE_LOWER_BOUND_MESSAGE),
                of(TOO_LONG_INT, 1, testDates.get(FROM_DATE), testDates.get(TO_DATE), CURRENT_PAGE_LOWER_BOUND_MESSAGE),
                of(1, 1001, testDates.get(FROM_DATE), testDates.get(TO_DATE), PAGE_SIZE_UPPER_BOUND_MESSAGE),
                of(1, 2, INVALID_DATE_FORMAT, testDates.get(TO_DATE), INVALID_DATE_ERR_MSG)
        );
    }

    private static Calendar getCurrentDate() {
        Calendar date = Calendar.getInstance();
        date.setTime(new Date());
        return date;
    }

    private static HashMap<String, String> getTransfersTestDates() {
        HashMap<String, String> datesMap = new HashMap<>();
        Calendar date = getCurrentDate();
        datesMap.put(FROM_DATE, dateFormat.format(date.getTime()));
        date.add(Calendar.DATE, 1);
        datesMap.put(TO_DATE, dateFormat.format(date.getTime()));
        return datesMap;
    }

    @BeforeAll
    static void createEarnRule() {
        deleteAllCampaigns();
        createDefaultSignUpCampaign();
    }

    @AfterAll
    static void deleteEarnRule() {
        deleteAllCampaigns();
    }

    @BeforeEach
    void methodSetup() {
        customerId = registerCustomer();
    }

    @Test
    @Tag(SMOKE_TEST)
    @UserStoryId(storyId = 1128)
    void shouldGetTransactionsById_AddedFields() {
        testCustomer = registerCustomer();
        getTransactionsById(testCustomer, 1, 1)
                .then()
                .assertThat()
                .statusCode(SC_OK);
    }

    @ParameterizedTest(name = "Run {index}: currentPage={0}, pageSize={1}, errMsg={2}")
    @MethodSource("shouldNotGetTransactionsById_Invalid")
    @UserStoryId(storyId = 790)
    void shouldNotGetTransactionsById_Invalid(int currentPage, final int pageSize, final String errMsg) {
        getTransactionsById(customerId, currentPage, pageSize)
                .then()
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .body(ERROR_MESSAGE_FIELD, equalTo(errMsg));
    }

    @Disabled("by default, the service returns only 100 first transactions. We need to get data from DB find the latest ones")
    @ParameterizedTest(name = "Run {index}: currentPage={0}, pageSize={1}")
    @Tag(SMOKE_TEST)
    @MethodSource("shouldGetTransactionsById_Valid")
    @UserStoryId(storyId = 790)
    void shouldGetTransactionsById_Valid(int currentPage, int pageSize) {
        val receiverId = registerCustomer();

        balanceTransfer(customerId, receiverId, BASE_ASSET, SOME_AMOUNT, TEST_OPERATION_ID);
        balanceTransfer(receiverId, customerId, BASE_ASSET, SOME_AMOUNT, TEST_OPERATION_ID);

        getTransactionsById(customerId, currentPage, pageSize)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .body(TRANSFERS_TIMESTAMP_FIELD, containsString(currentDate))
                .body(WALLETS_ADDRESS_FIELD, nullValue());
    }

    @ParameterizedTest(name = "Run {index}: currentPage={0}, pageSize={1}, fromDate={2}, toDate={3}")
    @Tag(SMOKE_TEST)
    @MethodSource("shouldGetTransactionsByDate_Valid")
    @UserStoryId(storyId = 790)
    void shouldGetTransactionsByDate_Valid(
            final int currentPage, final int pageSize, final String fromDate, final String toDate) {
        String receiverId = registerCustomer();

        balanceTransfer(customerId, receiverId, BASE_ASSET, SOME_AMOUNT, TEST_OPERATION_ID);
        balanceTransfer(receiverId, customerId, BASE_ASSET, SOME_AMOUNT, TEST_OPERATION_ID);

        getTransactionsByDate(fromDate, toDate, currentPage, pageSize)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .body(TRANSACTIONS_HISTORY_FIELD, hasSize(pageSize))
                .body(TRANSACTIONS_HISTORY_TIMESTAMP_FIELD, containsString(currentDate));
    }

    @ParameterizedTest(name = "Run {index}: currentPage={0}, pageSize={1}, fromDate={2}, toDate={3}, errMsg={4}")
    @MethodSource("shouldNotGetTransactionsByDate_Invalid")
    @UserStoryId(storyId = 790)
    void shouldNotGetTransactionsByDate_Invalid(int currentPage, int pageSize, String fromDate, String toDate,
            String errMsg) {
        getTransactionsByDate(fromDate, toDate, currentPage, pageSize)
                .then()
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .body(ERROR_MESSAGE_FIELD, equalTo(errMsg));
    }
}
