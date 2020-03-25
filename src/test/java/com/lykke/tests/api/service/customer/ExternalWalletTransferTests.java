package com.lykke.tests.api.service.customer;

import static com.lykke.tests.api.common.CommonConsts.AWAITILITY_POLL_INTERVAL_MID_SEC;
import static com.lykke.tests.api.common.CommonConsts.AwaitilityConsts.TIME_TO_MINE_30_BLOCKS_MINS;
import static com.lykke.tests.api.service.crosschainwalletlinker.CrossChainWalletLinkerUtils.getCustomerPublicAddress;
import static com.lykke.tests.api.service.customer.CustomerWalletUtils.approveLinkRequest;
import static com.lykke.tests.api.service.customer.CustomerWalletUtils.getCustomerWallets;
import static com.lykke.tests.api.service.customer.CustomerWalletUtils.postExternalTransfer;
import static com.lykke.tests.api.service.customer.CustomerWalletUtils.postLinkRequest;
import static com.lykke.tests.api.service.customer.LoginLogoutCustomerUtils.getUserToken;
import static com.lykke.tests.api.service.operationshistory.OperationsUtils.getTransactionsByCustomerId;
import static com.lykke.tests.api.service.privateblockchainfacade.PrivateBlockchainFacadeUtils.createCustomerFundedViaBonusReward;
import static com.lykke.tests.api.service.privateblockchainfacade.PrivateBlockchainFacadeUtils.getCustomerBalance;
import static org.apache.http.HttpStatus.SC_NO_CONTENT;
import static org.apache.http.HttpStatus.SC_OK;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.lykke.api.testing.annotations.UserStoryId;
import com.lykke.tests.api.base.BaseApiTest;
import com.lykke.tests.api.common.CommonConsts.Ethereum;
import com.lykke.tests.api.common.EthereumUtils;
import com.lykke.tests.api.service.crosschainwalletlinker.model.PublicAddressError;
import com.lykke.tests.api.service.crosschainwalletlinker.model.PublicAddressResponseModel;
import com.lykke.tests.api.service.crosschainwalletlinker.model.PublicAddressStatus;
import com.lykke.tests.api.service.customer.model.wallets.ApproveExternalWalletLinkRequest;
import com.lykke.tests.api.service.customer.model.wallets.LinkWalletResponse;
import com.lykke.tests.api.service.customer.model.wallets.TransferToExternalWalletRequest;
import com.lykke.tests.api.service.operationshistory.model.LinkedWalletTransferDirection;
import com.lykke.tests.api.service.operationshistory.model.PaginatedCustomerOperationsResponse;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.awaitility.Awaitility;
import org.awaitility.Duration;
import org.junit.jupiter.api.Test;

@Slf4j
public class ExternalWalletTransferTests extends BaseApiTest {

    private static final Double INITIAL_AMOUNT = 100.0;
    private static final Double OUTGOING_TRANSFER_AMOUNT = 37.0;

    @Test
    @UserStoryId(2859)
    void shouldTransferToExternalAddress() {
        val customerData = createCustomerFundedViaBonusReward(INITIAL_AMOUNT, true);
        val customerToken = getUserToken(customerData);

        val linkRequestResult = postLinkRequest(customerToken)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(LinkWalletResponse.class);

        assertNotNull(linkRequestResult.getLinkCode());

        val walletData = getCustomerWallets(getUserToken(customerData));

        approveLinkRequest(ApproveExternalWalletLinkRequest
                .builder()
                .signature(EthereumUtils.signLinkingCode(linkRequestResult.getLinkCode(), Ethereum.WALLET_PRIVATE_KEY))
                .publicAddress(Ethereum.WALLET_PUBLIC_ADDRESS)
                .privateAddress(walletData[0].getPrivateWalletAddress())
                .build())
                .then()
                .assertThat()
                .statusCode(SC_NO_CONTENT);

        val expectedLinkRequestResult = PublicAddressResponseModel
                .builder()
                .status(PublicAddressStatus.LINKED)
                .publicAddress(Ethereum.WALLET_PUBLIC_ADDRESS)
                .error(PublicAddressError.NONE)
                .build();

        Awaitility.await()
                .atMost(TIME_TO_MINE_30_BLOCKS_MINS, TimeUnit.MINUTES)
                .pollInterval(Duration.FIVE_SECONDS)
                .until(() -> PublicAddressStatus.LINKED == getCustomerPublicAddress(customerData.getCustomerId())
                        .then()
                        .assertThat()
                        .statusCode(SC_OK)
                        .extract()
                        .as(PublicAddressResponseModel.class)
                        .getStatus());

        val actualLinkRequestResult = getCustomerPublicAddress(customerData.getCustomerId())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(PublicAddressResponseModel.class);

        assertEquals(expectedLinkRequestResult, actualLinkRequestResult);

        log.info("-------------------------before the transfer-----------------------------");
        log.info(getCustomerBalance(customerData.getCustomerId()));
        log.info("-------------------------------------------------------------------------");

        val actualResult = postExternalTransfer(TransferToExternalWalletRequest
                .builder()
                .amount(OUTGOING_TRANSFER_AMOUNT.toString())
                .build(), customerToken)
                .then()
                .assertThat()
                .statusCode(SC_NO_CONTENT);

        // waiting for operations history to have the transfer in the output
        Awaitility.await()
                .atMost(TIME_TO_MINE_30_BLOCKS_MINS, TimeUnit.MINUTES)
                .pollInterval(AWAITILITY_POLL_INTERVAL_MID_SEC, TimeUnit.SECONDS)
                .until(() -> {
                    val transactions = getTransactionsByCustomerId(customerData.getCustomerId())
                            .then()
                            .assertThat()
                            .statusCode(SC_OK)
                            .extract()
                            .as(PaginatedCustomerOperationsResponse.class);
                    return 0 < transactions.getLinkedWalletTransfers().length;
                });

        // checking operations history
        val linkedWalletTransferTransaction = getTransactionsByCustomerId(customerData.getCustomerId())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(PaginatedCustomerOperationsResponse.class)
                .getLinkedWalletTransfers()[0];

        log.info("-------------------------after the transfer-----------------------------");
        log.info(getCustomerBalance(customerData.getCustomerId()));
        log.info("------------------------------------------------------------------------");

        assertAll(
                () -> assertEquals("MVN", linkedWalletTransferTransaction.getAssetSymbol()),
                () -> assertEquals(OUTGOING_TRANSFER_AMOUNT,
                        Double.valueOf(linkedWalletTransferTransaction.getAmount())),
                () -> assertEquals(walletData[0].getPrivateWalletAddress(),
                        linkedWalletTransferTransaction.getWalletAddress()),
                () -> assertEquals(Ethereum.WALLET_PUBLIC_ADDRESS.toLowerCase(),
                        linkedWalletTransferTransaction.getLinkedWalletAddress()),
                () -> assertEquals(LinkedWalletTransferDirection.OUTGOING,
                        linkedWalletTransferTransaction.getDirection())//,
                //// ??
                //    () -> assertEquals(customerData.getNewAmount() + customerData.getExtraAmount(),
                //             Double.valueOf(getCustomerBalance(customerData.getCustomerId())))
        );
    }
}
