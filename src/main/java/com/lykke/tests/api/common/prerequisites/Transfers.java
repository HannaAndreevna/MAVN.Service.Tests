package com.lykke.tests.api.common.prerequisites;

import static com.lykke.api.testing.api.common.GenerateUtils.generateRandomString;
import static com.lykke.tests.api.common.CommonConsts.AWAITILITY_INITIAL_BALANCE_WAIT_SECONDS;
import static com.lykke.tests.api.common.CommonConsts.AWAITILITY_OPERATIONS_HISTORY_SEC;
import static com.lykke.tests.api.common.CommonConsts.AWAITILITY_PBF_TRANSFER_BALANCE_SEC;
import static com.lykke.tests.api.common.CommonConsts.AWAITILITY_POLL_INTERVAL_MID_SEC;
import static com.lykke.tests.api.service.customer.LoginLogoutCustomerUtils.getUserToken;
import static com.lykke.tests.api.service.operationshistory.OperationsUtils.getTransactionsByCustomerId;
import static com.lykke.tests.api.service.privateblockchainfacade.PrivateBlockchainFacadeUtils.createCustomerFundedViaBonusReward;
import static com.lykke.tests.api.service.privateblockchainfacade.PrivateBlockchainFacadeUtils.getCustomerBalance;
import static com.lykke.tests.api.service.quorumexplorer.QuorumExplorerUtils.postTransationsRequest;
import static com.lykke.tests.api.service.walletmanagement.WalletManagementUtils.balanceTransfer;
import static org.apache.http.HttpStatus.SC_OK;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.lykke.tests.api.common.model.TransferPair;
import com.lykke.tests.api.service.operationshistory.model.PaginatedCustomerOperationsResponse;
import com.lykke.tests.api.service.quorumexplorer.model.FilteredTransactionsRequest;
import com.lykke.tests.api.service.quorumexplorer.model.PaginatedTransactionsResponse;
import com.lykke.tests.api.service.quorumexplorer.model.PaginationModel;
import com.lykke.tests.api.service.walletmanagement.model.TransferBalanceRequestModel;
import com.lykke.tests.api.service.walletmanagement.model.TransferBalanceResponse;
import com.lykke.tests.api.service.walletmanagement.model.TransferErrorCode;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import lombok.var;
import org.awaitility.Awaitility;
import org.awaitility.Duration;

@Slf4j
@UtilityClass
public class Transfers {

    public TransferPair performTransfer(Double senderAmount, Double recipientAmount, Double transferAmount) {
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

                    ////55
                    System.out
                            .println("===============================================================================");
                    System.out.println("Sender's expected initial balance: " + senderData.getNewAmount());
                    System.out.println("Recipient's expected initial balance: " + recipientData.getNewAmount());
                    System.out.println(
                            "Sender's expected initial balance with extra: " + (senderData.getNewAmount() + senderData
                                    .getNewAmount() + senderData
                                    .getExtraAmount()));
                    System.out.println(
                            "Recipient's expected initial balance with extra: " + (recipientData.getNewAmount()
                                    + recipientData.getNewAmount()
                                    + recipientData.getExtraAmount()));
                    System.out.println("Sender's actual initial balance: " + Double
                            .valueOf(getCustomerBalance(senderData.getCustomerId())));
                    System.out.println(
                            "Recipient's actual initial balance: " + Double
                                    .valueOf(getCustomerBalance(recipientData.getCustomerId())));
                    System.out
                            .println("===============================================================================");

                    return senderData.getNewAmount() + senderData.getNewAmount() + senderData.getExtraAmount() == Double
                            .valueOf(getCustomerBalance(senderData.getCustomerId()))
                            &&
                            recipientData.getNewAmount() + recipientData.getNewAmount() + recipientData.getExtraAmount()
                                    == Double
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

                    return senderData.getNewAmount() + senderData.getNewAmount() - transferAmount + senderData
                            .getExtraAmount() == Double
                            .valueOf(getCustomerBalance(senderData.getCustomerId()))
                            &&
                            recipientData.getNewAmount() + recipientData.getNewAmount() + transferAmount + recipientData
                                    .getExtraAmount() == Double
                                    .valueOf(getCustomerBalance(recipientData.getCustomerId()));
                });

        assertAll(
                () -> assertEquals(TransferErrorCode.NONE, actualResult.getErrorCode()),
                () -> assertEquals(senderData.getNewAmount() + senderData.getNewAmount() - transferAmount + senderData
                                .getExtraAmount(),
                        Double.valueOf(getCustomerBalance(senderData.getCustomerId()))),
                () -> assertEquals(
                        recipientData.getNewAmount() + recipientData.getNewAmount() + transferAmount + recipientData
                                .getExtraAmount(),
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

        return TransferPair
                .builder()
                .sender(senderData)
                .recipient(recipientData)
                .transferAmount(transferAmount)
                .build();
    }
}
