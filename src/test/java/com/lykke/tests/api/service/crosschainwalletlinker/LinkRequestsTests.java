package com.lykke.tests.api.service.crosschainwalletlinker;

import static com.lykke.api.testing.api.common.GenerateUtils.generateRandomString;
import static com.lykke.tests.api.common.CommonConsts.AwaitilityConsts.TIME_TO_MINE_30_BLOCKS_MINS;
import static com.lykke.tests.api.common.CommonConsts.SMOKE_TEST;
import static com.lykke.tests.api.common.EthereumUtils.signLinkingCode;
import static com.lykke.tests.api.service.crosschainwalletlinker.CrossChainWalletLinkerUtils.deleteLinkRequest;
import static com.lykke.tests.api.service.crosschainwalletlinker.CrossChainWalletLinkerUtils.getCustomerPublicAddress;
import static com.lykke.tests.api.service.crosschainwalletlinker.CrossChainWalletLinkerUtils.postLinkRequest;
import static com.lykke.tests.api.service.crosschainwalletlinker.CrossChainWalletLinkerUtils.postLinkRequestApproval;
import static com.lykke.tests.api.service.customer.CustomerWalletUtils.getCustomerWallets;
import static com.lykke.tests.api.service.customer.LoginLogoutCustomerUtils.getUserToken;
import static com.lykke.tests.api.service.notificationsystem.NotificationMessageUtils.registerPushUserAndSendPushMessage;
import static com.lykke.tests.api.service.notificationsystemaudit.NotificationSystemAuditUtils.getAuditMessageFromService;
import static com.lykke.tests.api.service.notificationsystemaudit.model.MessageType.PUSH_NOTIFICATION;
import static com.lykke.tests.api.service.privateblockchainfacade.PrivateBlockchainFacadeUtils.createCustomerFundedViaBonusReward;
import static com.lykke.tests.api.service.walletmanagement.WalletManagementUtils.blockCustomerWallet;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.http.HttpStatus.SC_INTERNAL_SERVER_ERROR;
import static org.apache.http.HttpStatus.SC_OK;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.lykke.api.testing.annotations.UserStoryId;
import com.lykke.tests.api.base.LongApiTest;
import com.lykke.tests.api.common.CommonConsts.Ethereum;
import com.lykke.tests.api.common.model.CustomerBalanceInfo;
import com.lykke.tests.api.common.model.CustomerInfo;
import com.lykke.tests.api.service.crosschainwalletlinker.CrossChainWalletLinkerUtils.ByCustomerIdRequest;
import com.lykke.tests.api.service.crosschainwalletlinker.model.LinkApprovalRequestModel;
import com.lykke.tests.api.service.crosschainwalletlinker.model.LinkingApprovalResponseModel;
import com.lykke.tests.api.service.crosschainwalletlinker.model.LinkingError;
import com.lykke.tests.api.service.crosschainwalletlinker.model.LinkingRequestResponseModel;
import com.lykke.tests.api.service.crosschainwalletlinker.model.PublicAddressError;
import com.lykke.tests.api.service.crosschainwalletlinker.model.PublicAddressResponseModel;
import com.lykke.tests.api.service.crosschainwalletlinker.model.PublicAddressStatus;
import com.lykke.tests.api.service.crosschainwalletlinker.model.UnlinkResponseModel;
import com.lykke.tests.api.service.notificationsystemaudit.model.DeliveryStatus;
import com.lykke.tests.api.service.walletmanagement.model.BlockUnblockRequestModel;
import java.nio.ByteBuffer;
import java.util.concurrent.TimeUnit;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.awaitility.Awaitility;
import org.awaitility.Duration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Slf4j
public class LinkRequestsTests extends LongApiTest {

    private static final String WALLET_LINKING_SUCCESSFUL_PUSH_NOTIFICATION = "wallet-linking-successful-push-notification";
    private static final String WALLET_LINKING_UNSUCCESSFUL_PUSH_NOTIFICATION = "wallet-linking-unsuccessful-push-notification";
    private static final String WALLET_UNLINKING_SUCCESSFUL_PUSH_NOTIFICATION = "wallet-unlinking-successful-push-notification";
    private static final String WALLET_UNLINKING_UNSUCCESSFUL_PUSH_NOTIFICATION = "wallet-unlinking-unsuccessful-push-notification";

    private CustomerBalanceInfo customerData;
    private String customerId;

    @BeforeEach
    void setUp() {
        customerData = createCustomerFundedViaBonusReward(100.0, true);
        customerId = customerData.getCustomerId();
        registerPushUserAndSendPushMessage(customerId);
    }

    @Test
    @Tag(SMOKE_TEST)
    @UserStoryId(2845)
    void shouldCreateLinkRequest() {
        val linkRequestResult = postLinkRequest(ByCustomerIdRequest
                .builder()
                .customerId(customerId)
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

        val expectedResult = PublicAddressResponseModel
                .builder()
                .status(PublicAddressStatus.PENDING_CUSTOMER_APPROVAL)
                .error(PublicAddressError.NONE)
                .publicAddress(EMPTY)
                .build();

        val actualResult = getCustomerPublicAddress(customerId)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(PublicAddressResponseModel.class);

        assertEquals(expectedResult, actualResult);
    }

    @Test
    @UserStoryId(storyId = {2845, 3763, 4029})
    void shouldApproveLinkRequest() {
        val linkRequestResult = postLinkRequest(ByCustomerIdRequest
                .builder()
                .customerId(customerId)
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
                .signature(signLinkingCode(linkRequestResult.getLinkCode(), Ethereum.WALLET_PRIVATE_KEY))
                .publicAddress(Ethereum.WALLET_PUBLIC_ADDRESS)
                .privateAddress(walletData[0].getPrivateWalletAddress())
                .build())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(LinkingApprovalResponseModel.class);

        log.info(String.format(
                "https://customer-website.falcon-dev.open-source.exchange/en/dapp-linking?internal-address=%s&link-code=%s",
                walletData[0].getPrivateWalletAddress(), linkRequestResult.getLinkCode()));

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

        Awaitility.await()
                .atMost(5, TimeUnit.MINUTES)
                .pollInterval(Duration.TWO_SECONDS)
                .until(() -> {
                    val message = getAuditMessageFromService(customerId, PUSH_NOTIFICATION, DeliveryStatus.SUCCESS,
                            WALLET_LINKING_SUCCESSFUL_PUSH_NOTIFICATION);
                    return null != message.getCustomerId() && message.getCustomerId().equalsIgnoreCase(customerId);
                });

        val actualMessage = getAuditMessageFromService(customerId, PUSH_NOTIFICATION, DeliveryStatus.SUCCESS,
                WALLET_LINKING_SUCCESSFUL_PUSH_NOTIFICATION);
    }

    @SneakyThrows
    @Test
    @UserStoryId(2845)
    void shouldNotApproveLinkRequestOnMissingPublicAddress() {
        val linkRequestResult = postLinkRequest(ByCustomerIdRequest
                .builder()
                .customerId(customerId)
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

        val byteBuffer = ByteBuffer.allocate(linkRequestResult.getLinkCode().length());
        byteBuffer.putInt(Integer.valueOf(linkRequestResult.getLinkCode()));
        val byteArray = byteBuffer.array();
        val approvalResult = postLinkRequestApproval(LinkApprovalRequestModel
                .builder()
                .signature(Ethereum.WALLET_PRIVATE_KEY)
                .publicAddress(generateRandomString(10))
                .privateAddress(walletData[0].getPrivateWalletAddress())
                .build())
                .then()
                .assertThat()
                .statusCode(SC_INTERNAL_SERVER_ERROR);
    }

    @Test
    @UserStoryId(storyId = {2845, 3763, 4029})
    void shouldNotApproveLinkRequestWithInvalidSignature() {
        val linkRequestResult = postLinkRequest(ByCustomerIdRequest
                .builder()
                .customerId(customerId)
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
                .signature(generateRandomString(10))
                .publicAddress(generateRandomString(10))
                .privateAddress(walletData[0].getPrivateWalletAddress())
                .build())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(LinkingApprovalResponseModel.class);

        assertEquals(LinkingError.INVALID_SIGNATURE, approvalResult.getError());

        Awaitility.await()
                .atMost(5, TimeUnit.MINUTES)
                .pollInterval(Duration.TWO_SECONDS)
                .until(() -> {
                    val message = getAuditMessageFromService(customerId, PUSH_NOTIFICATION, DeliveryStatus.SUCCESS,
                            WALLET_LINKING_UNSUCCESSFUL_PUSH_NOTIFICATION);
                    return null != message.getCustomerId() && message.getCustomerId().equalsIgnoreCase(customerId);
                });

        val actualMessage = getAuditMessageFromService(customerId, PUSH_NOTIFICATION, DeliveryStatus.SUCCESS,
                WALLET_LINKING_UNSUCCESSFUL_PUSH_NOTIFICATION);
    }

    @Test
    @UserStoryId(2845)
    void shouldNotApproveLinkRequestOnInvalidInput() {
        val linkRequestResult = postLinkRequest(ByCustomerIdRequest
                .builder()
                .customerId(customerId)
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

        val approvalResult = postLinkRequestApproval(LinkApprovalRequestModel
                .builder()
                .signature(generateRandomString(10))
                .publicAddress(generateRandomString(20))
                .privateAddress(generateRandomString(20))
                .build())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(LinkingApprovalResponseModel.class);

        assertEquals(LinkingError.LINKING_REQUEST_DOES_NOT_EXIST, approvalResult.getError());

        val expectedResult = PublicAddressResponseModel
                .builder()
                .status(PublicAddressStatus.PENDING_CUSTOMER_APPROVAL)
                .error(PublicAddressError.NONE)
                .build();

        val actualResult = getCustomerPublicAddress(customerId)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(PublicAddressResponseModel.class);

        assertEquals(expectedResult, actualResult);
    }

    @Test
    @UserStoryId(storyId = {3835, 3763, 4029})
    void shouldNotApproveLinkRequestIfWalletIsBlocked() {
        val linkRequestResult = postLinkRequest(ByCustomerIdRequest
                .builder()
                .customerId(customerId)
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
        blockCustomerWallet(BlockUnblockRequestModel
                .builder()
                .customerId(customerId)
                .build());

        val approvalResult = postLinkRequestApproval(LinkApprovalRequestModel
                .builder()
                .signature(signLinkingCode(linkRequestResult.getLinkCode(), Ethereum.WALLET_PRIVATE_KEY))
                .publicAddress(Ethereum.WALLET_PUBLIC_ADDRESS)
                .privateAddress(walletData[0].getPrivateWalletAddress())
                .build())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(LinkingApprovalResponseModel.class);

        assertEquals(LinkingError.CUSTOMER_WALLET_BLOCKED, approvalResult.getError());

        Awaitility.await()
                .atMost(5, TimeUnit.MINUTES)
                .pollInterval(Duration.TWO_SECONDS)
                .until(() -> {
                    val message = getAuditMessageFromService(customerId, PUSH_NOTIFICATION, DeliveryStatus.SUCCESS,
                            WALLET_LINKING_UNSUCCESSFUL_PUSH_NOTIFICATION);
                    return null != message.getCustomerId() && message.getCustomerId().equalsIgnoreCase(customerId);
                });

        val actualMessage = getAuditMessageFromService(customerId, PUSH_NOTIFICATION, DeliveryStatus.SUCCESS,
                WALLET_LINKING_UNSUCCESSFUL_PUSH_NOTIFICATION);
    }

    @Test
    @UserStoryId(storyId = {2845, 2914, 3844, 3763, 4029})
    void shouldDeleteLinkRequest() {
        val linkRequestResult = postLinkRequest(ByCustomerIdRequest
                .builder()
                .customerId(customerId)
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

        val expectedResult = PublicAddressResponseModel
                .builder()
                .status(PublicAddressStatus.PENDING_CUSTOMER_APPROVAL)
                .error(PublicAddressError.NONE)
                .publicAddress(EMPTY)
                .build();

        val actualResult = getCustomerPublicAddress(customerId)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(PublicAddressResponseModel.class);

        assertEquals(expectedResult, actualResult);

        val walletData = getCustomerWallets(getUserToken(customerData));

        val approvalResult = postLinkRequestApproval(LinkApprovalRequestModel
                .builder()
                .signature(signLinkingCode(linkRequestResult.getLinkCode(), Ethereum.WALLET_PRIVATE_KEY))
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

        ////55 new lines
        val actualLinkRequestResult = getCustomerPublicAddress(customerData.getCustomerId())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(PublicAddressResponseModel.class);

        assertEquals(expectedLinkRequestResult, actualLinkRequestResult);
        ////55

        val deletionResult = deleteLinkRequest(ByCustomerIdRequest
                .builder()
                .customerId(customerId)
                .build())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(UnlinkResponseModel.class);

        assertEquals(LinkingError.NONE, deletionResult.getError());

        val expectedLinkRequestStatusResult = PublicAddressResponseModel
                .builder()
                .status(PublicAddressStatus.NOT_LINKED)
                .publicAddress(EMPTY)
                .error(PublicAddressError.NONE)
                .build();

        Awaitility.await()
                .atMost(TIME_TO_MINE_30_BLOCKS_MINS, TimeUnit.MINUTES)
                .pollInterval(Duration.FIVE_SECONDS)
                .until(() -> PublicAddressStatus.LINKED != getCustomerPublicAddress(customerData.getCustomerId())
                        .then()
                        .assertThat()
                        .statusCode(SC_OK)
                        .extract()
                        .as(PublicAddressResponseModel.class)
                        .getStatus());

        val actualLinkRequestStatusResult = getCustomerPublicAddress(customerData.getCustomerId())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(PublicAddressResponseModel.class);

        assertEquals(expectedLinkRequestStatusResult, actualLinkRequestStatusResult);

        Awaitility.await()
                .atMost(5, TimeUnit.MINUTES)
                .pollInterval(Duration.TWO_SECONDS)
                .until(() -> {
                    val message = getAuditMessageFromService(customerId, PUSH_NOTIFICATION, DeliveryStatus.SUCCESS,
                            WALLET_UNLINKING_SUCCESSFUL_PUSH_NOTIFICATION);
                    return null != message.getCustomerId() && message.getCustomerId().equalsIgnoreCase(customerId);
                });

        val actualMessage = getAuditMessageFromService(customerId, PUSH_NOTIFICATION, DeliveryStatus.SUCCESS,
                WALLET_UNLINKING_SUCCESSFUL_PUSH_NOTIFICATION);
    }

    @Test
    @UserStoryId(storyId = {3835, 3844})
    void shouldNotDeleteLinkedPublicAddressIfWalletIsBlocked() {
        val linkRequestResult = postLinkRequest(ByCustomerIdRequest
                .builder()
                .customerId(customerId)
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

        val expectedResult = PublicAddressResponseModel
                .builder()
                .status(PublicAddressStatus.PENDING_CUSTOMER_APPROVAL)
                .error(PublicAddressError.NONE)
                .publicAddress(EMPTY)
                .build();

        val actualResult = getCustomerPublicAddress(customerId)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(PublicAddressResponseModel.class);

        assertEquals(expectedResult, actualResult);

        val walletData = getCustomerWallets(getUserToken(customerData));

        val approvalResult = postLinkRequestApproval(LinkApprovalRequestModel
                .builder()
                .signature(signLinkingCode(linkRequestResult.getLinkCode(), Ethereum.WALLET_PRIVATE_KEY))
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

        blockCustomerWallet(BlockUnblockRequestModel
                .builder()
                .customerId(customerId)
                .build());
        val deletionResult = deleteLinkRequest(ByCustomerIdRequest
                .builder()
                .customerId(customerId)
                .build())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(UnlinkResponseModel.class);

        assertEquals(LinkingError.CUSTOMER_WALLET_BLOCKED, deletionResult.getError());
    }
}
