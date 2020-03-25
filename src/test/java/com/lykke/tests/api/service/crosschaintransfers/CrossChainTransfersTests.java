package com.lykke.tests.api.service.crosschaintransfers;

import static com.lykke.tests.api.common.CommonConsts.AWAITILITY_POLL_INTERVAL_MID_SEC;
import static com.lykke.tests.api.common.CommonConsts.AwaitilityConsts.TIME_TO_MINE_30_BLOCKS_MINS;
import static com.lykke.tests.api.common.EthereumUtils.signLinkingCode;
import static com.lykke.tests.api.service.crosschaintransfers.CrossChainTransfersUtils.postTransferToExternal;
import static com.lykke.tests.api.service.crosschainwalletlinker.CrossChainWalletLinkerUtils.getCustomerPublicAddress;
import static com.lykke.tests.api.service.crosschainwalletlinker.CrossChainWalletLinkerUtils.postLinkRequest;
import static com.lykke.tests.api.service.crosschainwalletlinker.CrossChainWalletLinkerUtils.postLinkRequestApproval;
import static com.lykke.tests.api.service.customer.CustomerWalletUtils.getCustomerWallets;
import static com.lykke.tests.api.service.customer.LoginLogoutCustomerUtils.getUserToken;
import static com.lykke.tests.api.service.ethereumbridge.ExternalWalletUtils.getExternalBalance;
import static com.lykke.tests.api.service.notificationsystem.NotificationMessageUtils.registerPushUserAndSendPushMessage;
import static com.lykke.tests.api.service.operationshistory.OperationsUtils.getTransactionsByCustomerId;
import static com.lykke.tests.api.service.privateblockchainfacade.PrivateBlockchainFacadeUtils.getCustomerBalance;
import static com.lykke.tests.api.service.privateblockchainfacade.PrivateBlockchainFacadeUtils.createCustomerFundedViaBonusReward;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.http.HttpStatus.SC_OK;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.web3j.tx.Contract.GAS_LIMIT;
import static org.web3j.tx.ManagedTransaction.GAS_PRICE;

import com.lykke.api.testing.annotations.UserStoryId;
import com.lykke.tests.api.base.LongApiTest;
import com.lykke.tests.api.common.CommonConsts.Ethereum;
import com.lykke.tests.api.common.EthereumUtils;
import com.lykke.tests.api.service.crosschaintransfers.model.TransferToExternalErrorCode;
import com.lykke.tests.api.service.crosschaintransfers.model.TransferToExternalRequest;
import com.lykke.tests.api.service.crosschaintransfers.model.TransferToExternalResponse;
import com.lykke.tests.api.service.crosschainwalletlinker.CrossChainWalletLinkerUtils.ByCustomerIdRequest;
import com.lykke.tests.api.service.crosschainwalletlinker.model.LinkApprovalRequestModel;
import com.lykke.tests.api.service.crosschainwalletlinker.model.LinkingApprovalResponseModel;
import com.lykke.tests.api.service.crosschainwalletlinker.model.LinkingError;
import com.lykke.tests.api.service.crosschainwalletlinker.model.LinkingRequestResponseModel;
import com.lykke.tests.api.service.crosschainwalletlinker.model.PublicAddressError;
import com.lykke.tests.api.service.crosschainwalletlinker.model.PublicAddressResponseModel;
import com.lykke.tests.api.service.crosschainwalletlinker.model.PublicAddressStatus;
import com.lykke.tests.api.service.operationshistory.model.LinkedWalletTransferDirection;
import com.lykke.tests.api.service.operationshistory.model.LinkedWalletTransferResponse;
import com.lykke.tests.api.service.operationshistory.model.PaginatedCustomerOperationsResponse;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.awaitility.Awaitility;
import org.awaitility.Duration;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Numeric;

@Slf4j
public class CrossChainTransfersTests extends LongApiTest {

    private static final Double INITIAL_AMOUNT = 100.0;
    private static final Double OUTGOING_TRANSFER_AMOUNT = 37.0;
    private static final Double INCOMING_TRANSFER_AMOUNT = 1.0;

    @Test
    @UserStoryId(storyId = {2977, 2981, 3828, 3825})
    void shouldPostTransferToExternal() {
        val customerData = createCustomerFundedViaBonusReward(INITIAL_AMOUNT, true);
        registerPushUserAndSendPushMessage(customerData.getCustomerId());

        val linkRequestResult = postLinkRequest(ByCustomerIdRequest
                .builder()
                .customerId(customerData.getCustomerId())
                .build())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(LinkingRequestResponseModel.class);

        assertAll(
                () -> assertEquals(LinkingError.NONE, linkRequestResult.getError()),
                () -> assertNotNull(linkRequestResult.getLinkCode())
        );

        val expectedGetPUblicAddressResult = PublicAddressResponseModel
                .builder()
                .status(PublicAddressStatus.PENDING_CUSTOMER_APPROVAL)
                .error(PublicAddressError.NONE)
                .publicAddress(EMPTY)
                .build();

        val actualGetPublicAddressResult = getCustomerPublicAddress(customerData.getCustomerId())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(PublicAddressResponseModel.class);

        assertEquals(expectedGetPUblicAddressResult, actualGetPublicAddressResult);

        val initialWalletData = getCustomerWallets(getUserToken(customerData));

        val approvalResult = postLinkRequestApproval(LinkApprovalRequestModel
                .builder()
                .signature(signLinkingCode(linkRequestResult.getLinkCode(), Ethereum.WALLET_PRIVATE_KEY))
                .publicAddress(Ethereum.WALLET_PUBLIC_ADDRESS)
                .privateAddress(initialWalletData[0].getPrivateWalletAddress())
                .build())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(LinkingApprovalResponseModel.class);

        assertEquals(LinkingError.NONE, approvalResult.getError());

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
        val b1 = getCustomerBalance(customerData.getCustomerId());
        log.info(b1);
        log.info("expected balance: " + (
                Double.valueOf(customerData.getNewAmount()) + Double.valueOf(customerData.getExtraAmount())));
        log.info("new " + customerData.getNewAmount());
        log.info("extra " + customerData.getExtraAmount());
        log.info("-------------------------------------------------------------------------");

        val walletDataBeforeTransfer = getCustomerWallets(getUserToken(customerData));

        val actualResult = postTransferToExternal(TransferToExternalRequest
                .builder()
                .amount(OUTGOING_TRANSFER_AMOUNT.toString())
                .customerId(customerData.getCustomerId())
                .build())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(TransferToExternalResponse.class);

        assertEquals(TransferToExternalErrorCode.NONE, actualResult.getError());

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
        val b2 = getCustomerBalance(customerData.getCustomerId());
        log.info(b2);
        log.info("expected balance: " + (
                Double.valueOf(customerData.getNewAmount()) + Double.valueOf(customerData.getExtraAmount())
                        - OUTGOING_TRANSFER_AMOUNT));
        log.info("new " + customerData.getNewAmount());
        log.info("extra " + customerData.getExtraAmount());
        log.info("------------------------------------------------------------------------");

        val walletDataAfterTransfer = getCustomerWallets(getUserToken(customerData));

        log.info("=============================================================================");
        val currentBalance1 = getCustomerBalance(customerData.getCustomerId());
        log.info("new amount: " + customerData.getNewAmount());
        log.info("extra amount: " + customerData.getExtraAmount());
        log.info("initial amount: " + INITIAL_AMOUNT);
        log.info("outgoing amount: " + OUTGOING_TRANSFER_AMOUNT);
        log.info("actual amount: " + currentBalance1);
        log.info("=============================================================================");

        assertAll(
                () -> assertEquals("MVN", linkedWalletTransferTransaction.getAssetSymbol()),
                () -> assertEquals(OUTGOING_TRANSFER_AMOUNT,
                        Double.valueOf(linkedWalletTransferTransaction.getAmount())),
                () -> assertEquals(initialWalletData[0].getPrivateWalletAddress(),
                        linkedWalletTransferTransaction.getWalletAddress()),
                () -> assertEquals(Ethereum.WALLET_PUBLIC_ADDRESS.toLowerCase(),
                        linkedWalletTransferTransaction.getLinkedWalletAddress()),
                () -> assertEquals(LinkedWalletTransferDirection.OUTGOING,
                        linkedWalletTransferTransaction.getDirection())
        );

        // waiting for updated external balance
        Awaitility.await()
                .atMost(TIME_TO_MINE_30_BLOCKS_MINS, TimeUnit.MINUTES)
                .pollInterval(AWAITILITY_POLL_INTERVAL_MID_SEC, TimeUnit.SECONDS)
                .until(() -> {
                    val walletDataInProgress = getCustomerWallets(getUserToken(customerData));
                    return Double.valueOf(walletDataAfterTransfer[0].getExternalBalance()) < Double
                            .valueOf(walletDataInProgress[0].getExternalBalance());
                });

        val walletDataUpdated = getCustomerWallets(getUserToken(customerData));
        val externalBalance = getExternalBalance(walletDataUpdated[0].getPublicWalletAddress());

        log.info("=============================================================================");
        val currentBalance2 = getCustomerBalance(customerData.getCustomerId());
        log.info("new amount: " + customerData.getNewAmount());
        log.info("extra amount: " + customerData.getExtraAmount());
        log.info("initial amount: " + INITIAL_AMOUNT);
        log.info("outgoing amount: " + OUTGOING_TRANSFER_AMOUNT);
        log.info("actual amount: " + currentBalance2);
        log.info("=============================================================================");

        assertAll(
                () -> assertEquals(OUTGOING_TRANSFER_AMOUNT,
                        Double.valueOf(walletDataUpdated[0].getExternalBalance())
                                - Double.valueOf(walletDataBeforeTransfer[0].getExternalBalance())),
                () -> assertEquals(Double.valueOf(walletDataUpdated[0].getExternalBalance()),
                        Double.valueOf(externalBalance.getAmount()))
                /*,
                () -> assertEquals(
                customerData.getNewAmount() + customerData.getExtraAmount() + INITIAL_AMOUNT + INITIAL_AMOUNT
                        - OUTGOING_TRANSFER_AMOUNT,
                Double.valueOf(getCustomerBalance(customerData.getCustomerId())))
                 */
        );

        log.info("external amount before transfer: " + walletDataBeforeTransfer[0].getExternalBalance());
        log.info("external amount after transfer: " + walletDataUpdated[0].getExternalBalance());
    }

    @Disabled("TODO: transfer from external network")
    @SneakyThrows
    @Test
    @UserStoryId(storyId = {2977, 2981})
    void shouldReceivePaymentFromExternalAddress() {
        val customerData = createCustomerFundedViaBonusReward(INITIAL_AMOUNT, true);
        registerPushUserAndSendPushMessage(customerData.getCustomerId());

        val linkRequestResult = postLinkRequest(ByCustomerIdRequest
                .builder()
                .customerId(customerData.getCustomerId())
                .build())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(LinkingRequestResponseModel.class);

        assertAll(
                () -> assertEquals(LinkingError.NONE, linkRequestResult.getError()),
                () -> assertNotNull(linkRequestResult.getLinkCode())
        );

        val walletData = getCustomerWallets(getUserToken(customerData));

        val approvalResult = postLinkRequestApproval(LinkApprovalRequestModel
                .builder()
                .signature(EthereumUtils.signLinkingCode(linkRequestResult.getLinkCode(), Ethereum.WALLET_PRIVATE_KEY))
                .publicAddress(Ethereum.WALLET_PUBLIC_ADDRESS)
                .privateAddress(walletData[0].getPrivateWalletAddress())
                .build())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(LinkingApprovalResponseModel.class);

        assertEquals(LinkingError.NONE, approvalResult.getError());

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

        log.info("-------------------------before the outgoing transfer-----------------------------");
        log.info(getCustomerBalance(customerData.getCustomerId()));
        log.info("----------------------------------------------------------------------------------");

        val actualResult = postTransferToExternal(TransferToExternalRequest
                .builder()
                .amount(OUTGOING_TRANSFER_AMOUNT.toString())
                .customerId(customerData.getCustomerId())
                .build())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(TransferToExternalResponse.class);

        assertEquals(TransferToExternalErrorCode.NONE, actualResult.getError());

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
                    return 0 < transactions.getLinkedWalletTransfers().length
                            && LinkedWalletTransferDirection.OUTGOING == transactions.getLinkedWalletTransfers()[0]
                            .getDirection();
                });

        // checking operations history
        val outgoingLinkedWalletTransferTransaction = getTransactionsByCustomerId(customerData.getCustomerId())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(PaginatedCustomerOperationsResponse.class)
                .getLinkedWalletTransfers()[0];

        assertAll(
                () -> assertEquals("MVN", outgoingLinkedWalletTransferTransaction.getAssetSymbol()),
                () -> assertEquals(OUTGOING_TRANSFER_AMOUNT,
                        Double.valueOf(outgoingLinkedWalletTransferTransaction.getAmount())),
                () -> assertEquals(walletData[0].getPrivateWalletAddress(),
                        outgoingLinkedWalletTransferTransaction.getWalletAddress()),
                () -> assertEquals(Ethereum.WALLET_PUBLIC_ADDRESS.toLowerCase(),
                        outgoingLinkedWalletTransferTransaction.getLinkedWalletAddress()),
                () -> assertEquals(LinkedWalletTransferDirection.OUTGOING,
                        outgoingLinkedWalletTransferTransaction.getDirection())//,
                //  () -> assertEquals(customerData.getNewAmount() + customerData.getExtraAmount(),
                //          getCustomerBalance(customerData.getCustomerId()))
        );

        log.info("-------------------------after the outgoing transfer-----------------------------");
        log.info(getCustomerBalance(customerData.getCustomerId()));
        log.info("---------------------------------------------------------------------------------");

        Web3j web3j = Web3j.build(new HttpService(Ethereum.ETH_NODE_ADDRESS));
        log.info("--------------Enter into transferEtherAccountToAccount function");

        val credentials = Credentials.create(Ethereum.WALLET_PRIVATE_KEY);

        //   final BigInteger GAS_PRICE = BigInteger.valueOf(20_000_000_000L);
        //   final BigInteger GAS_LIMIT = BigInteger.valueOf(4_300_000);

        EthGetTransactionCount ethGetTransactionCount = web3j.ethGetTransactionCount(
                Ethereum.WALLET_PUBLIC_ADDRESS, DefaultBlockParameterName.LATEST).sendAsync().get();

        BigInteger nonce = ethGetTransactionCount.getTransactionCount();
        log.info(nonce.toString());

        /*
        RawTransaction rawTransaction = RawTransaction.createEtherTransaction (
                nonce, Convert.toWei("22", Convert.Unit.MWEI).toBigInteger(), Convert.toWei("44", Convert.Unit.GWEI).toBigInteger(), to, amount);
        */
        RawTransaction rawTransaction = RawTransaction.createEtherTransaction(
                nonce, GAS_PRICE, GAS_LIMIT, Ethereum.MVN_TRANSIT_ACCOUNT,
                BigInteger.valueOf(INCOMING_TRANSFER_AMOUNT.longValue()));
        byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, credentials);
        String hexValue = Numeric.toHexString(signedMessage);

        EthSendTransaction ethSendTransaction = web3j.ethSendRawTransaction(hexValue).sendAsync().get();
        String transactionHash = ethSendTransaction.getTransactionHash();

        //Load the deployed contract:
        //    MyContract contract = MyContract.load(contractAddress, web3j, credentials,     GAS_PRICE, GAS_LIMIT);

//Call methods on the contract:
        //   Future<> result = contract.transfer(_to, _value);

        /*
        // Find the wallet from database which is used as sender address
        UserWallet wallet = userWalletRepository.findByAddress(Ethereum.ETH_PUBLIC_ADDRESS);
        BigInteger amount = web3j.ethGetBalance(wallet.getAddress(), DefaultBlockParameterName.fromString("latest")).send()
                .getBalance();


        System.out.println("Creating  Parity...");

        System.out.println("Getting wallet ..." + wallet.getAddress() + "  and balance is "
                + web3j.ethGetBalance(wallet.getAddress(), DefaultBlockParameterName.fromString("latest")).send()
                .getBalance());
        // Minimum gas is 21000 used for transaction
        BigInteger gas = BigInteger.valueOf(21000);
        System.out.println("gas:::" + gas);

        BigInteger xth = new BigInteger("1000000000000000000");
        System.out.println(":::Transferred amount is::::" + amount);
        */

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
                    return 1 < transactions.getLinkedWalletTransfers().length
                            && null != Arrays.stream(transactions.getLinkedWalletTransfers())
                            .filter(tran -> LinkedWalletTransferDirection.INCOMING == tran.getDirection())
                            .findAny().orElse(new LinkedWalletTransferResponse());
                });

        log.info("-------------------------after the incoming transfer-----------------------------");
        log.info(getCustomerBalance(customerData.getCustomerId()));
        log.info("---------------------------------------------------------------------------------");

        // checking operations history
        val incomingLinkedWalletTransferTransaction = getTransactionsByCustomerId(customerData.getCustomerId())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(PaginatedCustomerOperationsResponse.class)
                .getLinkedWalletTransfers()[0];

        assertAll(
                () -> assertEquals("MVN", incomingLinkedWalletTransferTransaction.getAssetSymbol()),
                () -> assertEquals(OUTGOING_TRANSFER_AMOUNT,
                        Double.valueOf(incomingLinkedWalletTransferTransaction.getAmount())),
                () -> assertEquals(walletData[0].getPrivateWalletAddress(),
                        incomingLinkedWalletTransferTransaction.getWalletAddress()),
                () -> assertEquals(Ethereum.WALLET_PUBLIC_ADDRESS.toLowerCase(),
                        incomingLinkedWalletTransferTransaction.getLinkedWalletAddress()),
                () -> assertEquals(LinkedWalletTransferDirection.INCOMING,
                        incomingLinkedWalletTransferTransaction.getDirection())//,
                //  () -> assertEquals(customerData.getNewAmount() + customerData.getExtraAmount() - ,
                //          getCustomerBalance(customerData.getCustomerId()))
        );
    }
}
