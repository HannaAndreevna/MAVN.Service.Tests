package com.lykke.tests.api.service.customer;

import static com.lykke.api.testing.api.common.GenerateUtils.generateRandomEmail;
import static com.lykke.api.testing.api.common.GenerateUtils.generateRandomString;
import static com.lykke.api.testing.api.common.GenerateUtils.getRandomUuid;
import static com.lykke.tests.api.base.BasicFunctionalities.BASE_ASSET;
import static com.lykke.tests.api.base.BasicFunctionalities.DATE_FORMAT_YYYY_MM_DD;
import static com.lykke.tests.api.base.BasicFunctionalities.TEST_ASSET;
import static com.lykke.tests.api.base.BasicFunctionalities.getCurrentDate;
import static com.lykke.tests.api.common.CommonConsts.AWAITILITY_DEFAULT_SEC;
import static com.lykke.tests.api.common.CommonConsts.ERROR_FIELD;
import static com.lykke.tests.api.common.CommonConsts.MESSAGE_FIELD;
import static com.lykke.tests.api.common.CommonConsts.AWAITILITY_INITIAL_BALANCE_WAIT_SECONDS;
import static com.lykke.tests.api.common.CommonConsts.SMOKE_TEST;
import static com.lykke.tests.api.common.CommonMethods.TOTAL_REWARD;
import static com.lykke.tests.api.common.CommonMethods.createDefaultSignUpCampaign;
import static com.lykke.tests.api.common.CommonMethods.getCustomerBalanceForDefaultAsset;
import static com.lykke.tests.api.service.campaigns.BaseCampaignTest.deleteAllCampaigns;
import static com.lykke.tests.api.service.customer.CustomerWalletUtils.getTransfers;
import static com.lykke.tests.api.service.customer.CustomerWalletUtils.transferAsset;
import static com.lykke.tests.api.service.customer.LoginLogoutCustomerUtils.getUserToken;
import static com.lykke.tests.api.service.customermanagement.RegisterCustomerUtils.registerDefaultVerifiedCustomer;
import static com.lykke.tests.api.service.walletmanagement.WalletManagementUtils.balanceTransfer;
import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.of;

import com.lykke.api.testing.annotations.UserStoryId;
import com.lykke.tests.api.base.BaseApiTest;
import com.lykke.tests.api.service.customer.model.PaginatedTransfersResponseModel;
import com.lykke.tests.api.service.customer.model.PaginationRequestModel;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;
import lombok.val;
import org.awaitility.Awaitility;
import org.awaitility.Duration;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

// TODO: balance is not updated
public class TransferBalanceTests extends BaseApiTest {

    private static final String TRANSACTIONS_FIELD = "TransactionId";
    private static final String TRANSFERS_FIELD = "Transfers";
    private static final String TRANSFERS_FIELD_0 = TRANSFERS_FIELD + "[0]";
    private static final String TRANSFERS_TIMESTAMP_FIELD = TRANSFERS_FIELD_0 + ".Timestamp";
    private static final String TRANSFERS_IS_SENDER_FIELD = TRANSFERS_FIELD_0 + ".IsSender";
    private static final String ERROR_MESSAGE_FIELD = "message";

    private static final String SAME_TARGET_AND_SOURCE_ERR_CODE = "TransferSourceAndTargetMustBeDifferent";
    private static final String RECEIVER_NOT_FOUND_ERR_CODE = "TargetCustomerNotFound";
    private static final String SENDER_ASSET_NOT_FOUND_ERR_CODE = "SenderCustomerAssetNotFound";
    private static final String NOT_ENOUGH_BALANCE_ERR_CODE = "SenderCustomerNotEnoughBalance";

    private static final String MODEL_VALIDATION_FAIL = "ModelValidationFailed";

    private static final String ASSET_SYMBOL_REQUIRED_ERR_MSG = "The AssetSymbol field is required.";
    private static final String RECEIVER_NOT_EXIST_ERR_MSG = "The Receiver does not exist";
    private static final String NOT_ENOUGH_BALANCE_ERR_MSG = "The Sender doesn't have enough balance of the asset.";
    private static final String SENDER_ASSET_NOT_EXIST_ERR_MSG = "The Sender doesn't have any amount of the asset.";
    private static final String SENDER_RECEIVER_SAME_ERR_MSG = "The Sender and the receiver cannot be the same Customers";
    private static final String PAGINATION_INVALID_CURRENT_PAGE_ERR_MSG = "The field CurrentPage must be between 1 and 10000.";
    private static final String PAGINATION_INVALID_PAGE_SIZE_ERR_MSG = "The field PageSize must be between 1 and 500.";
    private static final String PAGINATION_INVALID_PAGE_SIZE_AND_CURRENT_PAGE_ERR_MSG = "The field PageSize must be between 1 and 500. The field CurrentPage must be between 1 and 10000.";

    private static final String TEST_OPERATION_ID = getRandomUuid();
    private static final int TOO_LONG_INT = 2_147_483_647 + 1;

    private static String currentDate = DATE_FORMAT_YYYY_MM_DD.format(getCurrentDate().getTime());
    private static String senderEmail;
    private static String recipientEmail;
    private static String password;
    private static String senderToken;
    private static String randomToken;
    private static String senderId;
    private static String recipientId;

    @BeforeAll
    static void createEarnRule() {
        deleteAllCampaigns();
        createDefaultSignUpCampaign();
    }

    @AfterAll
    static void deleteEarnRule() {
        deleteAllCampaigns();
    }

    private static Stream<Arguments> shouldNotGetTransfers_Invalid() {
        return Stream.of(
                of(-1, 2, PAGINATION_INVALID_CURRENT_PAGE_ERR_MSG),
                of(2, -1, PAGINATION_INVALID_PAGE_SIZE_ERR_MSG),
                of(-2, -1, PAGINATION_INVALID_PAGE_SIZE_AND_CURRENT_PAGE_ERR_MSG),
                of(TOO_LONG_INT, 1, PAGINATION_INVALID_CURRENT_PAGE_ERR_MSG),
                of(1, 1001, PAGINATION_INVALID_PAGE_SIZE_ERR_MSG)
        );
    }

    private static Stream<Arguments> transferBalanceCustomerApi_InvalidParameters() {
        return Stream.of(
                of(randomToken, recipientEmail, 200.0, BASE_ASSET, null, null, SC_UNAUTHORIZED),
                of(null, null, 200.0, null, MODEL_VALIDATION_FAIL, ASSET_SYMBOL_REQUIRED_ERR_MSG,
                        SC_BAD_REQUEST),
                of(senderToken, generateRandomEmail(), 200.0, BASE_ASSET, RECEIVER_NOT_FOUND_ERR_CODE,
                        RECEIVER_NOT_EXIST_ERR_MSG, SC_BAD_REQUEST),
                of(senderToken, recipientEmail,
                        Math.round(TOTAL_REWARD + 2), BASE_ASSET,
                        NOT_ENOUGH_BALANCE_ERR_CODE,
                        NOT_ENOUGH_BALANCE_ERR_MSG, SC_BAD_REQUEST),
                of(senderToken, recipientEmail, 200.0, generateRandomString(10), SENDER_ASSET_NOT_FOUND_ERR_CODE,
                        SENDER_ASSET_NOT_EXIST_ERR_MSG, SC_BAD_REQUEST),
                of(senderToken, senderEmail, 200.0, BASE_ASSET, SAME_TARGET_AND_SOURCE_ERR_CODE,
                        SENDER_RECEIVER_SAME_ERR_MSG, SC_BAD_REQUEST)
                /*of(senderToken, recipientEmail, 100.0, MVN_ASSET, SAME_TARGET_AND_SOURCE_ERR_CODE,
                        SENDER_RECEIVER_SAME_ERR_MSG, SC_BAD_REQUEST)*/ // TODO: log defect
        );
    }

    @BeforeEach
    void methodSetup() {
        val senderData = registerDefaultVerifiedCustomer();
        senderId = senderData.getCustomerId();
        recipientId = registerDefaultVerifiedCustomer().getCustomerId();
        senderToken = getUserToken(senderData);

        // TODO: balance is not updated
        Awaitility
                .await()
                .atMost(AWAITILITY_INITIAL_BALANCE_WAIT_SECONDS, TimeUnit.SECONDS)
                .pollInterval(Duration.TWO_SECONDS)
                .until(() -> {
                    val balance = getCustomerBalanceForDefaultAsset(recipientId);
                    return balance >= TOTAL_REWARD;
                });

        Awaitility
                .await()
                .atMost(AWAITILITY_DEFAULT_SEC, TimeUnit.SECONDS)
                .pollInterval(Duration.TWO_SECONDS)
                .until(() -> {
                    val balance = getCustomerBalanceForDefaultAsset(senderId);
                    return balance >= TOTAL_REWARD;
                });
    }

    //TODO Need to write test after Bug: FAL-780 is fixed
    @Disabled("Need to write test after Bug: FAL-780 is fixed")
    @Test
    @UserStoryId(storyId = 625)
    void shouldNotTransferBalance_ZeroBalance() {

    }

    @ParameterizedTest(name = "Run {index}: senderToken={0}, recipientEmail={1}, amount={2}, assetSymbol={3}, error={4}, message={5}, statusCode={6}")
    @MethodSource("transferBalanceCustomerApi_InvalidParameters")
    @UserStoryId(storyId = 625)
    void shouldNotTransferBalanceBetweenCustomers_Invalid(String senderTokenValue, String receiverEmail, Double amount,
            String assetSymbol, String error, String message, int statusCode) {

        if (senderTokenValue != randomToken) {
            transferAsset(senderTokenValue, receiverEmail, amount, assetSymbol)
                    .then()
                    .assertThat()
                    .statusCode(statusCode)
                    .body(ERROR_FIELD, equalTo(error))
                    .body(MESSAGE_FIELD, equalTo(message));
        } else {
            transferAsset(senderTokenValue, receiverEmail, amount, assetSymbol)
                    .then()
                    .assertThat()
                    .statusCode(statusCode);
        }
    }

    @Test
    @UserStoryId(storyId = 625)
    void shouldTransferBalanceBetweenTwoCustomers() {
        val amount = 10.0;

        transferAsset(senderToken, recipientEmail, amount, TEST_ASSET)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .body(TRANSACTIONS_FIELD, notNullValue());

        val senderBalance = TOTAL_REWARD - amount;
        val receiverBalance = TOTAL_REWARD + amount;

        Awaitility
                .await()
                .atMost(AWAITILITY_DEFAULT_SEC, TimeUnit.SECONDS)
                .until(() -> {
                    val balance = getCustomerBalanceForDefaultAsset(senderId);
                    return balance == senderBalance;
                });

        Awaitility
                .await()
                .atMost(AWAITILITY_DEFAULT_SEC, TimeUnit.SECONDS)
                .until(() -> {
                    val balance = getCustomerBalanceForDefaultAsset(recipientId);
                    return balance == receiverBalance;
                });
    }

    @Test
    @UserStoryId(storyId = 625)
    void shouldTransferBalanceBetweenTwoCustomers_ReceiverAssetDoesNotExist() {
        val amount = 10.0;

        transferAsset(senderToken, recipientEmail, amount, BASE_ASSET)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .body(TRANSACTIONS_FIELD, notNullValue());

        Awaitility
                .await()
                .atMost(AWAITILITY_DEFAULT_SEC, TimeUnit.SECONDS)
                .until(() -> {
                    val balance = getCustomerBalanceForDefaultAsset(recipientId);
                    return balance == TOTAL_REWARD;
                });

        Awaitility
                .await()
                .atMost(AWAITILITY_DEFAULT_SEC, TimeUnit.SECONDS)
                .until(() -> {
                    val balance = getCustomerBalanceForDefaultAsset(senderId);
                    return balance == TOTAL_REWARD;
                });
    }

    @Test
    @UserStoryId(storyId = 861)
    @Tag(SMOKE_TEST)
    void shouldGetTransfersById_Valid() {
        val amount = 20.0;

        balanceTransfer(senderId, recipientId, TEST_ASSET, amount, TEST_OPERATION_ID);

        val receiverBalance = TOTAL_REWARD + amount;

        Awaitility
                .await()
                .atMost(AWAITILITY_INITIAL_BALANCE_WAIT_SECONDS, TimeUnit.SECONDS)
                .pollInterval(Duration.TWO_SECONDS)
                .until(() -> {
                    val balance = getCustomerBalanceForDefaultAsset(recipientId);
                    return balance >= receiverBalance;
                });

        val actualResult = getTransfers(PaginationRequestModel
                .builder()
                .currentPage(1)
                .pageSize(100)
                .build(), senderToken)
                .then()

                ////xx
                ////55
                .log().all()

                .assertThat()
                .statusCode(SC_OK)
                .body(TRANSFERS_FIELD, hasSize(1))
                .body(TRANSFERS_TIMESTAMP_FIELD, containsString(currentDate))
                .body(TRANSFERS_IS_SENDER_FIELD, equalTo(true))
                .extract()
                .as(PaginatedTransfersResponseModel.class);

        assertAll(
                () -> assertEquals(1, actualResult.getTransfers().length),
                () -> assertEquals(1, actualResult.getTotalCount())
        );
    }

    @ParameterizedTest(name = "Run {index}: currentPage={0}, pageSize={1}, errMsg={2}")
    @MethodSource("shouldNotGetTransfers_Invalid")
    @UserStoryId(storyId = 861)
    void shouldNotGetTransfers_Invalid(int currentPage, int pageSize, String errMsg) {
        getTransfers(PaginationRequestModel
                .builder()
                .currentPage(currentPage)
                .pageSize(pageSize)
                .build(), senderToken)
                .then()
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .body(ERROR_MESSAGE_FIELD, equalTo(errMsg));
    }
}
