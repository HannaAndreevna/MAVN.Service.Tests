package com.lykke.tests.api.service.operationshistory;

import static com.lykke.tests.api.base.BasicFunctionalities.BASE_ASSET;
import static com.lykke.api.testing.api.common.GenerateUtils.getRandomUuid;
import static com.lykke.tests.api.service.customermanagement.RegisterCustomerUtils.registerCustomer;
import static com.lykke.tests.api.service.operationshistory.OperationsUtils.getTransactionsById;
import static com.lykke.tests.api.service.operationshistory.TransfersUtils.getTransferById;
import static com.lykke.tests.api.service.walletmanagement.WalletManagementUtils.balanceTransfer;
import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.params.provider.Arguments.of;
import static com.lykke.tests.api.common.CommonConsts.SMOKE_TEST;

import com.lykke.api.testing.annotations.UserStoryId;
import com.lykke.tests.api.base.BaseApiTest;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class TransfersTests extends BaseApiTest {

    private static final int TOO_LONG_INT = 2147483647 + 1;
    private static final Double SOME_AMOUNT = 20.0;
    private static String customerId;
    private static final String TEST_ASSET = "TestAsset";
    private static final String TEST_OPERATION_ID = getRandomUuid();
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private static String currentDate = dateFormat.format(getCurrentDate().getTime());

    private static final String TRANSFERS_FIELD = "Transfers";
    private static final String TRANSFERS_TIMESTAMP_FIELD = TRANSFERS_FIELD + ".Timestamp[0]";
    private static final String ERROR_MESSAGE_FIELD = "ErrorMessage";

    private static final String CURRENT_PAGE_LOWER_BOUND_MESSAGE = "Current page can't be less than 1";
    private static final String PAGE_SIZE_LOWER_BOUND_MESSAGE = "Page Size can't be less than 1";
    private static final String PAGE_SIZE_UPPER_BOUND_MESSAGE = "Page Size cannot exceed more then 1000";


    @BeforeEach
    void methodSetup() {
        customerId = registerCustomer();
    }

    @Disabled("by default, the service returns only 100 first transactions. We need to get data from DB find the latest ones")
    @ParameterizedTest(name = "Run {index}: currentPage={0}, pageSize={1}")
    @Tag(SMOKE_TEST)
    @MethodSource("shouldGetTransfersById_Valid")
    @UserStoryId(storyId = 861)
    void shouldGetTransfersById_Valid(int currentPage, int pageSize) {
        String receiverId = registerCustomer();

        balanceTransfer(customerId, receiverId, BASE_ASSET, SOME_AMOUNT, TEST_OPERATION_ID);
        balanceTransfer(receiverId, customerId, BASE_ASSET, SOME_AMOUNT, TEST_OPERATION_ID);

        getTransferById(customerId, currentPage, pageSize)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .body(TRANSFERS_FIELD, hasSize(pageSize))
                .body(TRANSFERS_TIMESTAMP_FIELD, containsString(currentDate));
    }

    @ParameterizedTest(name = "Run {index}: currentPage={0}, pageSize={1}, errMsg={2}")
    @MethodSource("shouldNotGetTransfersById_Invalid")
    @UserStoryId(storyId = 861)
    void shouldNotGetTransfersById_Invalid(int currentPage, int pageSize, String errMsg) {
        getTransactionsById(customerId, currentPage, pageSize)
                .then()
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .body(ERROR_MESSAGE_FIELD, equalTo(errMsg));
    }

    private static Stream<Arguments> shouldGetTransfersById_Valid() {
        return Stream.of(
                of(1, 2),
                of(2, 1)
        );
    }

    private static Stream<Arguments> shouldNotGetTransfersById_Invalid() {
        return Stream.of(
                of(-1, 2, CURRENT_PAGE_LOWER_BOUND_MESSAGE),
                of(2, -1, PAGE_SIZE_LOWER_BOUND_MESSAGE),
                of(-2, -1, PAGE_SIZE_LOWER_BOUND_MESSAGE),
                of(TOO_LONG_INT, 1, CURRENT_PAGE_LOWER_BOUND_MESSAGE),
                of(1, 1001, PAGE_SIZE_UPPER_BOUND_MESSAGE)
        );
    }

    private static Calendar getCurrentDate() {
        Calendar date = Calendar.getInstance();
        date.setTime(new Date());
        return date;
    }
}
