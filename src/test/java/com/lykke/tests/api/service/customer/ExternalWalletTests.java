package com.lykke.tests.api.service.customer;

import static com.lykke.tests.api.common.CommonConsts.AwaitilityConsts.TIME_TO_MINE_30_BLOCKS_MINS;
import static com.lykke.tests.api.common.CommonConsts.SMOKE_TEST;
import static com.lykke.tests.api.service.admin.CustomersUtils.getCustomerPublicWalletAddress;
import static com.lykke.tests.api.service.admin.LoginAdminUtils.getAdminToken;
import static com.lykke.tests.api.service.crosschainwalletlinker.CrossChainWalletLinkerUtils.getCustomerPublicAddress;
import static com.lykke.tests.api.service.customer.CustomerWalletUtils.approveLinkRequest;
import static com.lykke.tests.api.service.customer.CustomerWalletUtils.deleteLinkRequest;
import static com.lykke.tests.api.service.customer.CustomerWalletUtils.getCustomerWallets;
import static com.lykke.tests.api.service.customer.CustomerWalletUtils.postLinkRequest;
import static com.lykke.tests.api.service.customer.LoginLogoutCustomerUtils.getUserToken;
import static com.lykke.tests.api.service.notificationsystemaudit.NotificationSystemAuditUtils.getAuditMessageFromService;
import static com.lykke.tests.api.service.notificationsystemaudit.model.MessageType.PUSH_NOTIFICATION;
import static com.lykke.tests.api.service.pushnotifications.PushNotificationsUtils.postPushRegistrations;
import static com.lykke.tests.api.service.privateblockchainfacade.PrivateBlockchainFacadeUtils.createCustomerFundedViaBonusReward;
import static com.lykke.tests.api.service.walletmanagement.WalletManagementUtils.blockCustomerWallet;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.apache.http.HttpStatus.SC_NO_CONTENT;
import static org.apache.http.HttpStatus.SC_OK;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.lykke.api.testing.annotations.UserStoryId;
import com.lykke.tests.api.base.BaseApiTest;
import com.lykke.tests.api.common.CommonConsts.Ethereum;
import com.lykke.tests.api.common.EthereumUtils;
import com.lykke.tests.api.common.model.CustomerBalanceInfo;
import com.lykke.tests.api.service.admin.model.CustomerPublicWalletAddressResponse;
import com.lykke.tests.api.service.crosschainwalletlinker.model.LinkingRequestResponseModel;
import com.lykke.tests.api.service.crosschainwalletlinker.model.PublicAddressError;
import com.lykke.tests.api.service.crosschainwalletlinker.model.PublicAddressResponseModel;
import com.lykke.tests.api.service.crosschainwalletlinker.model.PublicAddressStatus;
import com.lykke.tests.api.service.customer.CustomerWalletUtils.ValidationErrorResponse;
import com.lykke.tests.api.service.customer.model.wallets.ApproveExternalWalletLinkRequest;
import com.lykke.tests.api.service.customer.model.wallets.LinkWalletResponse;
import com.lykke.tests.api.service.customer.model.wallets.LinkingError;
import com.lykke.tests.api.service.notificationsystemaudit.model.DeliveryStatus;
import com.lykke.tests.api.service.pushnotifications.model.CreatePushRegistrationRequestModel;
import com.lykke.tests.api.service.pushnotifications.model.PushTokenInsertionResult;
import com.lykke.tests.api.service.walletmanagement.model.BlockUnblockRequestModel;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.awaitility.Awaitility;
import org.awaitility.Duration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Slf4j
public class ExternalWalletTests extends BaseApiTest {

    private static final String WALLET_LINKING_SUCCESSFUL_PUSH_NOTIFICATION = "wallet-linking-successful-push-notification";
    private static final String WALLET_LINKING_UNSUCCESSFUL_PUSH_NOTIFICATION = "wallet-linking-unsuccessful-push-notification";
    private static final String WALLET_UNLINKING_SUCCESSFUL_PUSH_NOTIFICATION = "wallet-unlinking-successful-push-notification";
    private static final String WALLET_UNLINKING_UNSUCCESSFUL_PUSH_NOTIFICATION = "wallet-unlinking-unsuccessful-push-notification";
    private static final String CUSTOMER_WALLET_BLOCKED_ERROR_MESSAGE = "Customer Wallet blocked";

    private CustomerBalanceInfo customerData;
    private String customerId;
    private String customerToken;

    @BeforeEach
    void setUp() {
        customerData = createCustomerFundedViaBonusReward(100.0, false); // ?? not to register with a random phone????
        customerId = customerData.getCustomerId();
        customerToken = getUserToken(customerData.getEmail(), customerData.getPassword());

        ////55
        customerId = "21745c28-4f31-4fac-8ad5-fd38bdc94e7b";

        val requestObject = CreatePushRegistrationRequestModel
                .builder()
                .customerId(customerId)
                .infobipToken(
                        "bb10d020-6b8f-47f5-ab87-571d60465caf") ////55getRandomUuid()) ////55generateRandomString(10))
                .firebaseToken(
                        "escmbwUbzkM:APA91bEYaQOhNkS46wxEpItN47JpoD6LF1kR5hwjxFJJpxp-bKrmLX75r8jyUlhImYAB7sPhXvKiYbK8FDbusTx1Tt4XsjR4EDFvPcyHOSRgyhBon-kOjLZGhAXiFAPIGwQon2o9ppvy")////55generateRandomString(10))
                ////55    .appleToken(generateRandomString(10))
                .build();
        val actualResult = postPushRegistrations(requestObject)
                .then()

                ////55
                .log().all()

                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(PushTokenInsertionResult.class);
    }

    @Test
    @Tag(SMOKE_TEST)
    @UserStoryId(2859)
    void shouldPostLinkRequest() {
        val linkRequestResult = postLinkRequest(customerToken)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(LinkWalletResponse.class);

        assertNotNull(linkRequestResult.getLinkCode());

        // TODO: use the public api
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
////xx
        assertEquals(expectedResult, actualResult);
    }

    @Test
    ////55   @UserStoryId(2859)
    @UserStoryId(storyId = {2859, 3886})
    void shouldApproveLinkRequest() {
        val linkRequestResult = postLinkRequest(customerToken)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(LinkingRequestResponseModel.class);

        assertNotNull(linkRequestResult.getLinkCode());

        val walletData = getCustomerWallets(getUserToken(customerData));

        approveLinkRequest(ApproveExternalWalletLinkRequest
                .builder()
                .signature(EthereumUtils.signLinkingCode(linkRequestResult.getLinkCode(), Ethereum.WALLET_PRIVATE_KEY))
                .publicAddress(Ethereum.WALLET_PUBLIC_ADDRESS)
                .privateAddress(walletData[0].getPrivateWalletAddress())
                .build())
                .then()

                ////55
                .log().all()

                .assertThat()
                .statusCode(SC_NO_CONTENT);

        log.info(String.format(
                "https://customer-website.falcon-dev.open-source.exchange/en/dapp-linking?internal-address=%s&link-code=%s",
                walletData[0].getPrivateWalletAddress(), linkRequestResult.getLinkCode()));

        // checking admin-api (FAL-3886)
        val adminApiResult = getCustomerPublicWalletAddress(customerData.getCustomerId(), getAdminToken())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(CustomerPublicWalletAddressResponse.class);
        assertEquals(com.lykke.tests.api.service.admin.model.extrawallet.PublicAddressStatus.PENDING_CUSTOMER_APPROVAL,
                adminApiResult.getStatus());

        // TODO: use the public api
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
    }

    @Test
    @UserStoryId(2859)
    void shouldDeleteLinkRequest() {
        val linkRequestResult = postLinkRequest(customerToken)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(LinkWalletResponse.class);

        assertNotNull(linkRequestResult.getLinkCode());

        // TODO: use the public api
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

        // TODO: use the public api
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

        deleteLinkRequest(customerToken)
                .then()
                .assertThat()
                .statusCode(SC_NO_CONTENT);

        // TODO: use public api
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
    }

    @Test
    @UserStoryId(storyId = {3836, 3763, 4029})
    void shouldNotApproveLinkRequestIfWalletIsBlocked() {
        val linkRequestResult = postLinkRequest(customerToken)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(LinkingRequestResponseModel.class);

        assertNotNull(linkRequestResult.getLinkCode());

        val walletData = getCustomerWallets(getUserToken(customerData));
        blockCustomerWallet(BlockUnblockRequestModel
                .builder()
                .customerId(customerId)
                .build());

        val expectedResult = ValidationErrorResponse
                .builder()
                .error(LinkingError.CUSTOMER_WALLET_BLOCKED.getCode())
                .message(CUSTOMER_WALLET_BLOCKED_ERROR_MESSAGE)
                .build();

        val actualResult = approveLinkRequest(ApproveExternalWalletLinkRequest
                .builder()
                .signature(EthereumUtils.signLinkingCode(linkRequestResult.getLinkCode(), Ethereum.WALLET_PRIVATE_KEY))
                .publicAddress(Ethereum.WALLET_PUBLIC_ADDRESS)
                .privateAddress(walletData[0].getPrivateWalletAddress())
                .build())
                .then()
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .extract()
                .as(ValidationErrorResponse.class);

        assertEquals(expectedResult, actualResult);

        Awaitility.await()
                ////55   .atMost(5, TimeUnit.MINUTES)
                .atMost(10, TimeUnit.MINUTES)
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
    @UserStoryId(storyId = {3836, 3845, 3763, 4029})
    void shouldNotUnlinkPublicAddressIfWalletIsBlocked() {
        val linkRequestResult = postLinkRequest(customerToken)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(LinkWalletResponse.class);

        assertNotNull(linkRequestResult.getLinkCode());

        // TODO: use the public api
        val expectedLinkRequestResult = PublicAddressResponseModel
                .builder()
                ////55     .status(PublicAddressStatus.PENDING_CUSTOMER_APPROVAL)
                .status(PublicAddressStatus.NOT_LINKED)
                .publicAddress(EMPTY)
                .error(PublicAddressError.NONE)
                .build();

        val actualLinkRequestResult = getCustomerPublicAddress(customerId)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(PublicAddressResponseModel.class);

        assertEquals(expectedLinkRequestResult, actualLinkRequestResult);

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

        // TODO: use the public api
        val expectedLinkRequestApprovalResult = PublicAddressResponseModel
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

        val actualLinkRequestApprovalResult = getCustomerPublicAddress(customerData.getCustomerId())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(PublicAddressResponseModel.class);

        assertEquals(expectedLinkRequestApprovalResult, actualLinkRequestApprovalResult);

        blockCustomerWallet(BlockUnblockRequestModel
                .builder()
                .customerId(customerId)
                .build());

        val expectedResult = ValidationErrorResponse
                .builder()
                .error(LinkingError.CUSTOMER_WALLET_BLOCKED.getCode())
                .message(CUSTOMER_WALLET_BLOCKED_ERROR_MESSAGE)
                .build();

        val actualResult = deleteLinkRequest(customerToken)
                .then()
                .assertThat()
                .statusCode(SC_NO_CONTENT);
        ////55     .statusCode(SC_BAD_REQUEST)
        ////55      .extract()
        ////55      .as(ValidationErrorResponse.class);

        ////55 assertEquals(expectedResult, actualResult);

        Awaitility.await()
                .atMost(5, TimeUnit.MINUTES)
                .pollInterval(Duration.TWO_SECONDS)
                .until(() -> {
                    val message = getAuditMessageFromService(customerId, PUSH_NOTIFICATION, DeliveryStatus.SUCCESS,
                            WALLET_UNLINKING_UNSUCCESSFUL_PUSH_NOTIFICATION);
                    return null != message.getCustomerId() && message.getCustomerId().equalsIgnoreCase(customerId);
                });

        val actualMessage = getAuditMessageFromService(customerId, PUSH_NOTIFICATION, DeliveryStatus.SUCCESS,
                WALLET_UNLINKING_UNSUCCESSFUL_PUSH_NOTIFICATION);
    }
}
