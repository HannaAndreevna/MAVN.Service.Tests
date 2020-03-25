package com.lykke.tests.api.service.walletmanagement;

import static com.lykke.api.testing.api.common.GenerateUtils.getRandomUuid;
import static com.lykke.tests.api.base.BasicFunctionalities.BASE_ASSET;
import static com.lykke.tests.api.common.CommonConsts.AWAITILITY_DEFAULT_MAX_SEC;
import static com.lykke.tests.api.common.CommonConsts.AWAITILITY_DEFAULT_SEC;
import static com.lykke.tests.api.common.CommonMethods.TOTAL_REWARD;
import static com.lykke.tests.api.common.CommonMethods.createDefaultSignUpCampaign;
import static com.lykke.tests.api.common.CommonMethods.getCustomerBalanceForDefaultAsset;
import static com.lykke.tests.api.service.campaigns.BaseCampaignTest.deleteAllCampaigns;
import static com.lykke.tests.api.service.customermanagement.RegisterCustomerUtils.registerCustomer;
import static com.lykke.tests.api.service.walletmanagement.WalletManagementUtils.balanceTransfer;
import static com.lykke.tests.api.service.walletmanagement.WalletManagementUtils.blockCustomerWallet;
import static com.lykke.tests.api.service.walletmanagement.WalletManagementUtils.getWalletBlockStatus;
import static com.lykke.tests.api.service.walletmanagement.WalletManagementUtils.unblockCustomerWallet;
import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.lykke.api.testing.annotations.UserStoryId;
import com.lykke.tests.api.base.BaseApiTest;
import com.lykke.tests.api.service.walletmanagement.model.BlockUnblockRequestModel;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;
import lombok.val;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class WalletManagementTests extends BaseApiTest {

    private static final String TEST_ASSET = "TestAsset";
    private static final String TEST_OPERATION_ID = getRandomUuid();
    private static final String MVN_ASSET = "MAVN";
    private static final String TRANSACTIONS_FIELD = "TransactionId";
    private static final String EXTERNAL_OPERATION_ID_FIELD = "ExternalOperationId";
    private static final String ERROR_CODE_FIELD = "ErrorCode";
    private static final String SAME_TARGET_AND_SOURCE_ERR_MSG = "TransferSourceAndTargetMustBeDifferent";
    private static final String TARGET_CUSTOMER_NOT_FOUND_ERR_MSG = "TargetCustomerNotFound";
    private static final String SOURCE_CUSTOMER_NOT_FOUND_ERR_MSG = "SourceCustomerNotFound";
    private static final String SOURCE_ASSET_NOT_FOUND_ERR_MSG = "SourceCustomerAssetNotFound";
    private static final String NOT_ENOUGH_BALANCE_ERROR = "NotEnoughFunds";
    private static final String NAME_FIELD = "name";
    private static final String ACTIVE_STATUS = "Active";
    private static final String BLOCKED_STATUS = "Blocked";
    private static final String BLOCKED_WALLET_ERR_CODE = "SourceCustomerWalletBlocked";
    private static final String NOT_ENOUGH_FUNDS_ERR_CODE = "NotEnoughFunds";
    private static final String CUSTOMER_NOT_FOUND_ERR_CODE = "CustomerNotFound";
    private static final String ALREADY_BLOCKED_ERR_CODE = "CustomerWalletAlreadyBlocked";
    private static final String NOT_BLOCKED_ERR_CODE = "CustomerWalletNotBlocked";

    private static final Double AMOUNT = 5.0;
    private static final Double SOME_AMOUNT_01 = 10.0;
    private static final Double SOME_AMOUNT_02 = 4000.0;
    private static final Double SOME_AMOUNT_03 = 1.0;

    private static String customerId;

    @BeforeAll
    static void createEarnRule() {
        deleteAllCampaigns();
        createDefaultSignUpCampaign();
    }

    @AfterAll
    static void deleteEarnRule() {
        deleteAllCampaigns();
    }

    private static Stream<Arguments> transferBalance_OperationId() {
        return Stream.of(
                Arguments.of(20, getRandomUuid()),
                Arguments.of(20, null)
        );
    }

    @BeforeEach
    void methodSetup() {
        registerCustomerAndWaitUntilRewardReceived();
    }

    @Test
    @UserStoryId(storyId = 477)
    void shouldTransferBalanceBetweenTwoCustomers() {
        String senderId = customerId;
        String receiverId = registerCustomerAndWaitUntilRewardReceived();
        val amount = 2000;

        balanceTransfer(senderId, receiverId, BASE_ASSET, AMOUNT, TEST_OPERATION_ID)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .body(TRANSACTIONS_FIELD, notNullValue());

        val senderBalance = TOTAL_REWARD - AMOUNT;
        val receiverBalance = TOTAL_REWARD + AMOUNT;

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
                    val balance = getCustomerBalanceForDefaultAsset(receiverId);
                    return balance == receiverBalance;
                });
    }

    @Disabled("Cannot be tested now, as no way to add balance - other than to default wallet")
    @Test
    @UserStoryId(storyId = 477)
    void shouldTransferBalanceBetweenTwoCustomers_ReceiverAssetDoesNotExist() {
        String senderId = customerId;
        String receiverId = registerCustomer();

        balanceTransfer(senderId, receiverId, BASE_ASSET, SOME_AMOUNT_01, TEST_OPERATION_ID)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .body(TRANSACTIONS_FIELD, notNullValue());
    }

    @Test
    @UserStoryId(storyId = 477)
    void shouldNotTransferBalanceBetweenTwoCustomers_SenderAssetDoesNotExist() {
        String senderId = customerId;
        String receiverId = registerCustomer();

        balanceTransfer(senderId, receiverId, "DummyAsset", SOME_AMOUNT_01, TEST_OPERATION_ID)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .body(TRANSACTIONS_FIELD, nullValue())
                .body(ERROR_CODE_FIELD, equalTo(SOURCE_ASSET_NOT_FOUND_ERR_MSG));
    }

    @Test
    @UserStoryId(storyId = 477)
    void shouldNotTransferBalanceBetweenTwoCustomers_SenderDoesNotExist() {
        String senderId = getRandomUuid();
        String receiverId = registerCustomer();

        balanceTransfer(senderId, receiverId, MVN_ASSET, SOME_AMOUNT_01, TEST_OPERATION_ID)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .body(TRANSACTIONS_FIELD, nullValue())
                .body(ERROR_CODE_FIELD, equalTo(SOURCE_CUSTOMER_NOT_FOUND_ERR_MSG));
    }

    @Test
    @UserStoryId(storyId = 477)
    void shouldNotTransferBalanceBetweenTwoCustomers_ReceiverDoesNotExist() {
        String senderId = customerId;
        String receiverId = getRandomUuid();

        balanceTransfer(senderId, receiverId, MVN_ASSET, SOME_AMOUNT_01, TEST_OPERATION_ID)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .body(TRANSACTIONS_FIELD, nullValue())
                .body(ERROR_CODE_FIELD, equalTo(TARGET_CUSTOMER_NOT_FOUND_ERR_MSG));
    }

    @Test
    @UserStoryId(storyId = 477)
    void shouldNotTransferBalanceBetweenTwoCustomers_InsufficientBalance() {
        String senderId = customerId;
        String receiverId = registerCustomerAndWaitUntilRewardReceived();

        balanceTransfer(senderId, receiverId, BASE_ASSET, SOME_AMOUNT_02, TEST_OPERATION_ID)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .body(TRANSACTIONS_FIELD, nullValue())
                .body(ERROR_CODE_FIELD, equalTo(NOT_ENOUGH_BALANCE_ERROR));

        Awaitility
                .await()
                .atMost(AWAITILITY_DEFAULT_SEC, TimeUnit.SECONDS)
                .until(() -> {
                    val balance = getCustomerBalanceForDefaultAsset(senderId);
                    return balance == TOTAL_REWARD;
                });

        Awaitility
                .await()
                .atMost(AWAITILITY_DEFAULT_SEC, TimeUnit.SECONDS)
                .until(() -> {
                    val balance = getCustomerBalanceForDefaultAsset(receiverId);
                    return balance == TOTAL_REWARD;
                });
    }

    @Test
    @UserStoryId(storyId = 477)
    void shouldNotTransferBalanceBetweenTwoCustomers_SendToSameCustomer() {
        String senderId = customerId;

        balanceTransfer(senderId, senderId, MVN_ASSET, SOME_AMOUNT_01, TEST_OPERATION_ID)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .body(TRANSACTIONS_FIELD, nullValue())
                .body(ERROR_CODE_FIELD, equalTo(SAME_TARGET_AND_SOURCE_ERR_MSG));
    }

    @Test
    @UserStoryId(storyId = 477)
    void shouldNotTransferBalanceBetweenTwoCustomers_BadRequest() {
        balanceTransfer("", "", MVN_ASSET, SOME_AMOUNT_01, TEST_OPERATION_ID)
                .then()
                .assertThat()
                .statusCode(SC_BAD_REQUEST);
    }

    @ParameterizedTest(name = "Run {index}: amount={0}, operationId={1}")
    @MethodSource("transferBalance_OperationId")
    @UserStoryId(storyId = 815)
    void shouldTransferBalance_OperationId(Double amount, String operationId) {
        String senderId = customerId;
        String receiverId = registerCustomer();
        ;

        balanceTransfer(senderId, receiverId, BASE_ASSET, amount, operationId)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .body(TRANSACTIONS_FIELD, notNullValue())
                .body(EXTERNAL_OPERATION_ID_FIELD, equalTo(operationId));
    }

    @Test
    @UserStoryId(storyId = 1802)
    void shouldBlockUnblockCustomerWallet() {
        val customerId = registerCustomer();
        val receiverId = registerCustomer();

        val blockUnblockObject = BlockUnblockRequestModel
                .builder()
                .customerId(customerId)
                .build();

        val blockStatusResponse = getWalletBlockStatus(blockUnblockObject);

        assertAll(
                () -> assertEquals(ACTIVE_STATUS, blockStatusResponse.getStatus())
        );

        blockCustomerWallet(blockUnblockObject);

        val disabledBlockStatusResponse = getWalletBlockStatus(blockUnblockObject);

        assertAll(
                () -> assertEquals(BLOCKED_STATUS, disabledBlockStatusResponse.getStatus())
        );

        balanceTransfer(customerId, receiverId, BASE_ASSET, SOME_AMOUNT_03, getRandomUuid())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .body(ERROR_CODE_FIELD, equalTo(BLOCKED_WALLET_ERR_CODE));

        unblockCustomerWallet(blockUnblockObject);

        val reactivatedBlockStatusResponse = getWalletBlockStatus(blockUnblockObject);

        assertAll(
                () -> assertEquals(ACTIVE_STATUS, reactivatedBlockStatusResponse.getStatus())
        );

        balanceTransfer(customerId, receiverId, BASE_ASSET, SOME_AMOUNT_03, getRandomUuid())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .body(ERROR_CODE_FIELD, equalTo(NOT_ENOUGH_FUNDS_ERR_CODE));
    }

    @Test
    @UserStoryId(storyId = 1802)
    void shouldNotBlockCustomerWallet_CustomerNotFound() {
        val customerId = getRandomUuid();

        val blockUnblockObject = BlockUnblockRequestModel
                .builder()
                .customerId(customerId)
                .build();

        val notFoundBlockStatusResponse = blockCustomerWallet(blockUnblockObject);

        assertAll(
                () -> assertEquals(CUSTOMER_NOT_FOUND_ERR_CODE, notFoundBlockStatusResponse.getError())
        );
    }

    @Test
    @UserStoryId(storyId = 1802)
    void shouldNotUnBlockCustomerWallet_CustomerNotFound() {
        val customerId = getRandomUuid();

        val blockUnblockObject = BlockUnblockRequestModel
                .builder()
                .customerId(customerId)
                .build();

        val notFoundBlockStatusResponse = unblockCustomerWallet(blockUnblockObject);

        assertAll(
                () -> assertEquals(CUSTOMER_NOT_FOUND_ERR_CODE, notFoundBlockStatusResponse.getError())
        );
    }

    @Test
    @UserStoryId(storyId = 1802)
    void shouldNotUnBlockCustomerWallet_NotBlocked() {
        val customerId = registerCustomer();

        val blockUnblockObject = BlockUnblockRequestModel
                .builder()
                .customerId(customerId)
                .build();

        val notFoundBlockStatusResponse = unblockCustomerWallet(blockUnblockObject);

        assertAll(
                () -> assertEquals(NOT_BLOCKED_ERR_CODE, notFoundBlockStatusResponse.getError())
        );
    }

    @Test
    @UserStoryId(storyId = 1802)
    void shouldNotBlockCustomerWallet_AlreadyBlocked() {
        val customerId = registerCustomer();

        val blockUnblockObject = BlockUnblockRequestModel
                .builder()
                .customerId(customerId)
                .build();

        blockCustomerWallet(blockUnblockObject);

        val alreadyBlockedResponse = blockCustomerWallet(blockUnblockObject);

        assertAll(
                () -> assertEquals(ALREADY_BLOCKED_ERR_CODE, alreadyBlockedResponse.getError())
        );
    }

    public String registerCustomerAndWaitUntilRewardReceived() {
        customerId = registerCustomer();
        ;

        Awaitility
                .await()
                .atMost(AWAITILITY_DEFAULT_MAX_SEC, TimeUnit.SECONDS)
                .until(() -> {
                    val balance = getCustomerBalanceForDefaultAsset(customerId);
                    return balance == TOTAL_REWARD;
                });

        return customerId;
    }
}
