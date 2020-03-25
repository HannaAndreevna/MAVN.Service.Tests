package com.lykke.tests.api.service.customer;

import static com.lykke.tests.api.common.CommonConsts.AWAITILITY_DEFAULT_SEC;
import static com.lykke.tests.api.common.CommonConsts.AWAITILITY_INITIAL_BALANCE_WAIT_SECONDS;
import static com.lykke.tests.api.common.CommonConsts.AWAITILITY_POLL_INTERVAL_MID_SEC;
import static com.lykke.tests.api.common.CommonConsts.SMOKE_TEST;
import static com.lykke.tests.api.common.CommonMethods.getCustomerBalanceForDefaultAsset;
import static com.lykke.tests.api.common.PaginationConts.CURRENT_PAGE_LOWER_BOUNDARY;
import static com.lykke.tests.api.common.prerequisites.EarnRules.createBasicSignUpEarnRule;
import static com.lykke.tests.api.service.customer.CustomerWalletUtils.transferAsset;
import static com.lykke.tests.api.service.customer.HistoryUtils.getOperationHistoryErrorResponse;
import static com.lykke.tests.api.service.customer.HistoryUtils.getOperationsHistoryResponse;
import static com.lykke.tests.api.service.customer.LoginLogoutCustomerUtils.getUserToken;
import static com.lykke.tests.api.service.customermanagement.RegisterCustomerUtils.registerDefaultVerifiedCustomer;
import static com.lykke.tests.api.service.privateblockchainfacade.PrivateBlockchainFacadeUtils.createCustomerFundedViaBonusReward;
import static org.apache.http.HttpStatus.SC_OK;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.lykke.api.testing.annotations.UserStoryId;
import com.lykke.tests.api.base.BaseApiTest;
import com.lykke.tests.api.service.admin.TestDataForPaginatedTests;
import com.lykke.tests.api.service.customer.model.TransferOperationResponse;
import com.lykke.tests.api.service.customer.model.history.HistoryOperationType;
import com.lykke.tests.api.service.customer.model.history.OperationsHistoryRequest;
import com.lykke.tests.api.service.privateblockchainfacade.PrivateBlockchainFacadeUtils;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;
import lombok.val;
import lombok.var;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class HistoryTests extends BaseApiTest {

    protected static final String CAMPAIGNS_FIELD = "Campaigns";
    private static final String EXPECTED_REWARD_TYPE = "BonusReward";
    private static final HistoryOperationType EXPECTED_SEND_TRANSFER = HistoryOperationType.SEND_TRANSFER;
    private static final HistoryOperationType EXPECTED_RECEIVE_TRANSFER = HistoryOperationType.RECEIVE_TRANSFER;
    private static final String TEST_ASSET = "MVN";
    private static final Double TEST_AMOUNT = 12.0;
    private static final String EXPECTED_TOTAL_COUNT = "2";

    @BeforeAll
    static void setup() {
        deleteAllCampaigns();
        createBasicSignUpEarnRule();
    }

    static Stream<Arguments> getWrongPaginationParameters() {
        return TestDataForPaginatedTests.getWrongPaginationParameters();
    }

    private static void deleteAllCampaigns() {
        // TODO: deletion of campaigns
        /*
        while (!getCampaigns().jsonPath().getList(CAMPAIGNS_FIELD).isEmpty()) {
            String campaign = getCampaigns()
                    .then()
                    .assertThat()
                    .statusCode(SC_OK)
                    .extract()
                    .path(CAMPAIGNS_FIELD + "[0].Id");

            deleteCampaign(campaign)
                    .then()
                    .assertThat()
                    .statusCode(SC_OK);
        }
        */
    }

    @Disabled("TODO: needs investigation")
    @Test
    @Tag(SMOKE_TEST)
    @UserStoryId(storyId = 1408)
    void shouldGetOperationsHistory_BonusReward() {

        val customerData = PrivateBlockchainFacadeUtils.createCustomerFundedViaBonusReward(100.0, true);
        val customerId = customerData.getCustomerId();

        Awaitility
                .await()
                .atMost(AWAITILITY_INITIAL_BALANCE_WAIT_SECONDS, TimeUnit.SECONDS)
                .pollInterval(AWAITILITY_POLL_INTERVAL_MID_SEC, TimeUnit.SECONDS)
                .until(() -> {
                    val balance = getCustomerBalanceForDefaultAsset(customerId);
                    return balance > 0;
                });

        val customerToken = getUserToken(customerData);

        val requestObject = OperationsHistoryRequest
                .builder()
                .currentPage(CURRENT_PAGE_LOWER_BOUNDARY)
                .pageSize(2)
                .token(customerToken)
                .build();

        val operationsHistoryResponse = getOperationsHistoryResponse(requestObject);

        assertAll(
                () -> assertEquals(EXPECTED_REWARD_TYPE, operationsHistoryResponse.getOperations()[0].getType()),
                () -> assertEquals(EXPECTED_TOTAL_COUNT, operationsHistoryResponse.getTotalCount())
        );
    }

    @Test
    @Tag(SMOKE_TEST)
    @UserStoryId(storyId = 1408)
    void shouldGetOperationsHistory_SendTransfer() {
        val senderData = PrivateBlockchainFacadeUtils.createCustomerFundedViaBonusReward(100.0, true);
        val recipientData = registerDefaultVerifiedCustomer();
        val senderCustomerId = senderData.getCustomerId();

        Awaitility
                .await()
                .atMost(AWAITILITY_INITIAL_BALANCE_WAIT_SECONDS, TimeUnit.SECONDS)
                .pollInterval(AWAITILITY_POLL_INTERVAL_MID_SEC, TimeUnit.SECONDS)
                .until(() -> {
                    val balance = getCustomerBalanceForDefaultAsset(senderCustomerId);
                    return balance > 0;
                });

        val initialBalance = getCustomerBalanceForDefaultAsset(senderCustomerId);

        val senderToken = getUserToken(senderData);
        val transferResult = transferAsset(senderToken, recipientData.getEmail(), TEST_AMOUNT, TEST_ASSET)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(TransferOperationResponse.class);
        assertNotNull(transferResult.getTransactionId());

        // Wait until customer balance is updated after asset transfer
        Awaitility
                .await()
                .atMost(AWAITILITY_INITIAL_BALANCE_WAIT_SECONDS, TimeUnit.SECONDS)
                .pollInterval(AWAITILITY_POLL_INTERVAL_MID_SEC, TimeUnit.SECONDS)
                .until(() -> {
                    val balance = getCustomerBalanceForDefaultAsset(senderCustomerId);
                    return balance < initialBalance;
                });

        val requestObject = OperationsHistoryRequest
                .builder()
                .currentPage(CURRENT_PAGE_LOWER_BOUNDARY)
                .pageSize(2)
                .token(senderToken)
                .build();

        val operationsHistoryResponse = getOperationsHistoryResponse(requestObject);

        assertAll(
                () -> assertEquals(EXPECTED_SEND_TRANSFER, operationsHistoryResponse.getOperations()[0].getType()),
                () -> assertEquals(TEST_AMOUNT,
                        Double.valueOf(operationsHistoryResponse.getOperations()[0].getAmount()))
        );
    }

    @Test
    @Tag(SMOKE_TEST)
    @UserStoryId(storyId = 1408)
    void shouldGetOperationsHistory_ReceiveTransfer() {
        val senderData = PrivateBlockchainFacadeUtils.createCustomerFundedViaBonusReward(100.0, true);
        val recipientData = registerDefaultVerifiedCustomer();
        var customerId = senderData.getCustomerId();

        val senderToken = getUserToken(senderData.getEmail(), senderData.getPassword());
        val receiverToken = getUserToken(recipientData.getEmail(), recipientData.getPassword());
        // Wait until customer balance is updated
        Awaitility
                .await()
                .atMost(AWAITILITY_INITIAL_BALANCE_WAIT_SECONDS, TimeUnit.SECONDS)
                .until(() -> {
                    val balance = getCustomerBalanceForDefaultAsset(customerId);
                    return balance > 0;
                });

        val initialBalance = getCustomerBalanceForDefaultAsset(customerId);

        transferAsset(senderToken, recipientData.getEmail(), TEST_AMOUNT, TEST_ASSET);
        Awaitility
                .await()
                .atMost(AWAITILITY_INITIAL_BALANCE_WAIT_SECONDS, TimeUnit.SECONDS)
                .pollInterval(AWAITILITY_POLL_INTERVAL_MID_SEC, TimeUnit.SECONDS)
                .until(() -> {
                    val balance = getCustomerBalanceForDefaultAsset(customerId);
                    return balance < initialBalance;
                });

        val requestObject = OperationsHistoryRequest
                .builder()
                .currentPage(CURRENT_PAGE_LOWER_BOUNDARY)
                .pageSize(2)
                .token(receiverToken)
                .build();

        val operationsHistoryResponse = getOperationsHistoryResponse(requestObject);

        assertAll(
                () -> assertEquals(EXPECTED_RECEIVE_TRANSFER, operationsHistoryResponse.getOperations()[0].getType()),
                () -> assertEquals(TEST_AMOUNT,
                        Double.valueOf(operationsHistoryResponse.getOperations()[0].getAmount()))
        );
    }

    @ParameterizedTest(name = "Run {index}: currentPage={0}, pageSize={1}")
    @MethodSource("getWrongPaginationParameters")
    @UserStoryId(storyId = 1408)
    void shouldNotGetOperationsHistory_InvalidPagination(int currentPage, int pageSize) {
        val customerData = registerDefaultVerifiedCustomer();
        val customerToken = getUserToken(customerData.getEmail(), customerData.getPassword());

        val requestObject = OperationsHistoryRequest
                .builder()
                .currentPage(currentPage)
                .pageSize(pageSize)
                .token(customerToken)
                .build();

        val operationsHistoryErrorResponse = getOperationHistoryErrorResponse(requestObject);

        assertAll(
                () -> assertEquals(requestObject.getValidationResponse(), operationsHistoryErrorResponse)
        );
    }

}
