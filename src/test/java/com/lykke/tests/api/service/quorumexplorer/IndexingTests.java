package com.lykke.tests.api.service.quorumexplorer;

import static com.lykke.api.testing.api.common.GenerateUtils.generateRandomString;
import static com.lykke.tests.api.common.CommonConsts.AWAITILITY_INITIAL_BALANCE_WAIT_SECONDS;
import static com.lykke.tests.api.common.CommonConsts.AWAITILITY_OPERATIONS_HISTORY_SEC;
import static com.lykke.tests.api.common.CommonConsts.AWAITILITY_PBF_TRANSFER_BALANCE_SEC;
import static com.lykke.tests.api.common.CommonConsts.AWAITILITY_POLL_INTERVAL_MID_SEC;
import static com.lykke.tests.api.service.customer.LoginLogoutCustomerUtils.getUserToken;
import static com.lykke.tests.api.service.operationshistory.OperationsUtils.getTransactionsByCustomerId;
import static com.lykke.tests.api.service.privateblockchainfacade.PrivateBlockchainFacadeUtils.getCustomerBalance;
import static com.lykke.tests.api.service.privateblockchainfacade.PrivateBlockchainFacadeUtils.createCustomerFundedViaBonusReward;
import static com.lykke.tests.api.service.quorumexplorer.QuorumExplorerUtils.postTransationsRequest;
import static com.lykke.tests.api.service.walletmanagement.WalletManagementUtils.balanceTransfer;
import static org.apache.http.HttpStatus.SC_OK;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.of;

import com.lykke.api.testing.annotations.UserStoryId;
import com.lykke.tests.api.base.BaseApiTest;
import com.lykke.tests.api.service.operationshistory.model.PaginatedCustomerOperationsResponse;
import com.lykke.tests.api.service.quorumexplorer.model.FilteredTransactionsRequest;
import com.lykke.tests.api.service.quorumexplorer.model.PaginatedTransactionsResponse;
import com.lykke.tests.api.service.quorumexplorer.model.PaginationModel;
import com.lykke.tests.api.service.walletmanagement.model.TransferBalanceRequestModel;
import com.lykke.tests.api.service.walletmanagement.model.TransferBalanceResponse;
import com.lykke.tests.api.service.walletmanagement.model.TransferErrorCode;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import lombok.var;
import org.awaitility.Awaitility;
import org.awaitility.Duration;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@Slf4j
public class IndexingTests extends BaseApiTest {

    private static final Double INITIAL_SENDER_AMOUNT_01 = 1000.3;
    private static final Double INITIAL_RECIPIENT_AMOUNT_01 = 20.4;
    private static final Double VALID_TRANSFER_AMOUNT_01 = 300.5;
    private static final Double INITIAL_SENDER_AMOUNT_02 = 1000.0;
    private static final Double INITIAL_RECIPIENT_AMOUNT_02 = 20.0;
    private static final Double VALID_TRANSFER_AMOUNT_02 = 300.0;
    private static final Double INITIAL_SENDER_AMOUNT_03 = 1000_000_000.0;
    private static final Double INITIAL_RECIPIENT_AMOUNT_03 = 20_000_000.0;
    private static final Double VALID_TRANSFER_AMOUNT_03 = 300_000_000.0;
    private static final Double INITIAL_SENDER_AMOUNT_04 = 1000_000.0;
    private static final Double INITIAL_RECIPIENT_AMOUNT_04 = 20_000.0;
    private static final Double VALID_TRANSFER_AMOUNT_04 = 300_000.0;

    static Stream<Arguments> getValidAmountsToTransfer() {
        return Stream.of(
                of(INITIAL_SENDER_AMOUNT_04, INITIAL_RECIPIENT_AMOUNT_04, VALID_TRANSFER_AMOUNT_04),
                of(INITIAL_SENDER_AMOUNT_03, INITIAL_RECIPIENT_AMOUNT_03, VALID_TRANSFER_AMOUNT_03),

                of(INITIAL_SENDER_AMOUNT_01, INITIAL_RECIPIENT_AMOUNT_01, VALID_TRANSFER_AMOUNT_01),
                of(INITIAL_SENDER_AMOUNT_02, INITIAL_RECIPIENT_AMOUNT_02, VALID_TRANSFER_AMOUNT_02));
    }

    @ParameterizedTest(name = "Run {index}: senderAmount={0}, recipientAmount={1}, transferAmount={2}")
    @MethodSource("getValidAmountsToTransfer")
    @UserStoryId(1626)
    void shouldAddBalanceIfValidAmountViaWalletManagement(Double senderAmount, Double recipientAmount,
            Double transferAmount) {
        final Double initialSenderAmount = senderAmount * 3;
        val initialRecipientAmount = recipientAmount * 3;
        val expectedSenderAmount = initialSenderAmount - transferAmount.longValue();
        val expectedRecipientAmount = initialRecipientAmount + transferAmount.longValue();
        val senderData = createCustomerFundedViaBonusReward(senderAmount);
        val recipientData = createCustomerFundedViaBonusReward(recipientAmount);
        val senderToken = getUserToken(senderData.getEmail(), senderData.getPassword());
        val recipientToken = getUserToken(recipientData.getEmail(), recipientData.getPassword());

        Awaitility.await()
                .atMost(AWAITILITY_INITIAL_BALANCE_WAIT_SECONDS, TimeUnit.SECONDS)
                .pollInterval(Duration.TWO_SECONDS)
                .until(() -> {
                    log.info("===============================================================================");
                    log.info("Sender's expected initial balance: " + senderData.getNewAmount());
                    log.info("Recipient's expected initial balance: " + recipientData.getNewAmount());
                    log.info(
                            "Sender's expected initial balance with extra: " + (senderData.getNewAmount() + senderData
                                    .getExtraAmount()));
                    log.info(
                            "Recipient's expected initial balance with extra: " + (recipientData.getNewAmount()
                                    + recipientData.getExtraAmount()));
                    log.info("Sender's actual initial balance: " + Double
                            .valueOf(getCustomerBalance(senderData.getCustomerId())));
                    log.info(
                            "Recipient's actual initial balance: " + Double
                                    .valueOf(getCustomerBalance(recipientData.getCustomerId())));
                    log.info("===============================================================================");
                    return senderData.getNewAmount() + senderData.getExtraAmount() == Double
                            .valueOf(getCustomerBalance(senderData.getCustomerId()))
                            && recipientData.getNewAmount() + recipientData.getExtraAmount() == Double
                            .valueOf(getCustomerBalance(recipientData.getCustomerId()));
                });

        val requestObject = TransferBalanceRequestModel
                .builder()
                .senderCustomerId(senderData.getCustomerId())
                .receiverCustomerId(recipientData.getCustomerId())
                .amount(transferAmount.toString())
                .operationId(generateRandomString(100))
                .build();
        val actualResult = balanceTransfer(requestObject)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(TransferBalanceResponse.class);

        Awaitility.await()
                .atMost(AWAITILITY_PBF_TRANSFER_BALANCE_SEC, TimeUnit.SECONDS)
                .pollInterval(Duration.TWO_SECONDS)
                .until(() -> {

                    var senderActual = Double.valueOf(getCustomerBalance(senderData.getCustomerId()));
                    var recipientActual = Double.valueOf(getCustomerBalance(recipientData.getCustomerId()));
                    log.info("===============================================================================");
                    log.info(
                            "Sender's expected resulting balance: " + (senderData.getNewAmount() - transferAmount
                                    + senderData.getExtraAmount()));
                    log.info(
                            "Recipient's expected resulting balance: " + (recipientData.getNewAmount() + transferAmount
                                    + recipientData.getExtraAmount()));
                    log.info("Sender's actual resulting balance: " + senderActual);
                    log.info("Recipient's actual resulting balance: " + recipientActual);
                    log.info("===============================================================================");

                    return senderData.getNewAmount() - transferAmount + senderData.getExtraAmount() == Double
                            .valueOf(getCustomerBalance(senderData.getCustomerId()))
                            && recipientData.getNewAmount() + transferAmount + recipientData.getExtraAmount() == Double
                            .valueOf(getCustomerBalance(recipientData.getCustomerId()));
                });

        assertAll(
                () -> assertEquals(TransferErrorCode.NONE, actualResult.getErrorCode()),
                () -> assertEquals(senderData.getNewAmount() - transferAmount + senderData.getExtraAmount(),
                        Double.valueOf(getCustomerBalance(senderData.getCustomerId()))),
                () -> assertEquals(recipientData.getNewAmount() + transferAmount + recipientData.getExtraAmount(),
                        Double.valueOf(getCustomerBalance(recipientData.getCustomerId())))
        );

        // waiting for operations history to have the transfer in the output
        Awaitility.await()
                .atMost(AWAITILITY_OPERATIONS_HISTORY_SEC, TimeUnit.SECONDS)
                .pollInterval(AWAITILITY_POLL_INTERVAL_MID_SEC, TimeUnit.SECONDS)
                .until(() -> 0 < getTransactionsByCustomerId(senderData.getCustomerId())
                        .then()
                        .assertThat()
                        .statusCode(SC_OK)
                        .extract()
                        .as(PaginatedCustomerOperationsResponse.class)
                        .getTransfers().length);

        // checking operations history
        val senderTransactions = getTransactionsByCustomerId(senderData.getCustomerId())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(PaginatedCustomerOperationsResponse.class);

        val recipientTransactions = getTransactionsByCustomerId(recipientData.getCustomerId())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(PaginatedCustomerOperationsResponse.class);

        assertAll(
                () -> assertEquals(transferAmount, Arrays.stream(senderTransactions.getTransfers())
                        .map(tran -> Double.valueOf(tran.getAmount())).reduce(0.0, (a, b) -> a + b)),
                () -> assertEquals(transferAmount, Arrays.stream(recipientTransactions.getTransfers())
                        .map(tran -> Double.valueOf(tran.getAmount())).reduce(0.0, (a, b) -> a + b))
        );

        final String senderWalletAddress = senderTransactions.getTransfers()[0].getWalletAddress();
        final String toWalletAddress = recipientTransactions.getTransfers()[0].getOtherSideWalletAddress();

        val actualTransactions = postTransationsRequest(FilteredTransactionsRequest
                .builder()
                .from(new String[]{senderWalletAddress})
                .to(new String[]{})
                .affectedAddresses(new String[]{})
                .pagingInfo(new PaginationModel(1, 100))
                .build())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(PaginatedTransactionsResponse.class);

        assertAll(
                () -> assertEquals(1, actualTransactions.getTransactions().length),
                () -> assertEquals(senderWalletAddress, actualTransactions.getTransactions()[0].getFrom())
        );
    }
}
