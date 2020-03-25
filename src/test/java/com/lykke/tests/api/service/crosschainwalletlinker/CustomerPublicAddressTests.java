package com.lykke.tests.api.service.crosschainwalletlinker;

import static com.lykke.tests.api.common.CommonConsts.AwaitilityConsts.TIME_TO_MINE_30_BLOCKS_MINS;
import static com.lykke.tests.api.common.CommonConsts.SMOKE_TEST;
import static com.lykke.tests.api.common.EthereumUtils.signLinkingCode;
import static com.lykke.tests.api.service.crosschainwalletlinker.CrossChainWalletLinkerUtils.deleteLinkRequest;
import static com.lykke.tests.api.service.crosschainwalletlinker.CrossChainWalletLinkerUtils.getConfigurationByType;
import static com.lykke.tests.api.service.crosschainwalletlinker.CrossChainWalletLinkerUtils.getCustomerNextFee;
import static com.lykke.tests.api.service.crosschainwalletlinker.CrossChainWalletLinkerUtils.getCustomerPublicAddress;
import static com.lykke.tests.api.service.crosschainwalletlinker.CrossChainWalletLinkerUtils.postLinkRequest;
import static com.lykke.tests.api.service.crosschainwalletlinker.CrossChainWalletLinkerUtils.postLinkRequestApproval;
import static com.lykke.tests.api.service.crosschainwalletlinker.CrossChainWalletLinkerUtils.postOrUpdateConfiguration;
import static com.lykke.tests.api.service.crosschainwalletlinker.model.ConfigurationItemType.FIRST_TIME_LINKING_FEE;
import static com.lykke.tests.api.service.crosschainwalletlinker.model.ConfigurationItemType.SUBSEQUENT_LINKING_FEE;
import static com.lykke.tests.api.service.customer.CustomerWalletUtils.getCustomerWallets;
import static com.lykke.tests.api.service.customer.LoginLogoutCustomerUtils.getUserToken;
import static com.lykke.tests.api.service.customermanagement.RegisterCustomerUtils.registerDefaultVerifiedCustomer;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.http.HttpStatus.SC_OK;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.lykke.api.testing.annotations.UserStoryId;
import com.lykke.tests.api.base.BaseApiTest;
import com.lykke.tests.api.common.CommonConsts.Ethereum;
import com.lykke.tests.api.common.model.CustomerInfo;
import com.lykke.tests.api.service.crosschainwalletlinker.CrossChainWalletLinkerUtils.ByCustomerIdRequest;
import com.lykke.tests.api.service.crosschainwalletlinker.model.ConfigurationItemRequestModel;
import com.lykke.tests.api.service.crosschainwalletlinker.model.ConfigurationItemResponseModel;
import com.lykke.tests.api.service.crosschainwalletlinker.model.ConfigurationItemUpdateResponseModel;
import com.lykke.tests.api.service.crosschainwalletlinker.model.LinkApprovalRequestModel;
import com.lykke.tests.api.service.crosschainwalletlinker.model.LinkingApprovalResponseModel;
import com.lykke.tests.api.service.crosschainwalletlinker.model.LinkingError;
import com.lykke.tests.api.service.crosschainwalletlinker.model.LinkingRequestResponseModel;
import com.lykke.tests.api.service.crosschainwalletlinker.model.NextFeeResponseModel;
import com.lykke.tests.api.service.crosschainwalletlinker.model.PublicAddressError;
import com.lykke.tests.api.service.crosschainwalletlinker.model.PublicAddressResponseModel;
import com.lykke.tests.api.service.crosschainwalletlinker.model.PublicAddressStatus;
import com.lykke.tests.api.service.crosschainwalletlinker.model.UnlinkResponseModel;
import java.util.concurrent.TimeUnit;
import lombok.val;
import org.awaitility.Awaitility;
import org.awaitility.Duration;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

public class CustomerPublicAddressTests extends BaseApiTest {

    private static final int UNREAL_FIRST_TIME_LINKING_FEE = 100_000;
    private static final int UNREAL_SUBSEQUENT_LINKING_FEE = 100_000;

    @Test
    @Tag(SMOKE_TEST)
    @UserStoryId(2845)
    void shouldNotGetCustomerWalletPublicAddressIfEmpty() {
        val customerData = registerDefaultVerifiedCustomer();
        val expectedResult = PublicAddressResponseModel
                .builder()
                .publicAddress(EMPTY)
                .status(PublicAddressStatus.NOT_LINKED)
                .error(PublicAddressError.NONE)
                .build();
        val actualResult = getCustomerPublicAddress(customerData.getCustomerId())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(PublicAddressResponseModel.class);

        assertEquals(expectedResult, actualResult);
    }

    @Test
    @UserStoryId(3527)
    void shouldGetCustomerNextFee() {
        val firstTimeLinkingFee = getFirstTimeLinkingFee();
        val nextFee = getSubsequentLinkingFee();
        val customerData = registerDefaultVerifiedCustomer();
        assertEquals(
                Double.valueOf(firstTimeLinkingFee),
                Double.valueOf(getCustomerNextFee(customerData.getCustomerId())
                        .then()
                        .assertThat()
                        .statusCode(SC_OK)
                        .extract()
                        .as(NextFeeResponseModel.class)
                        .getFee()));

        prepareCustomerWithLinkedPublicAddress(customerData);
        unlinkPublicAddress(customerData);

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

        assertEquals(
                Double.valueOf(nextFee),
                Double.valueOf(getCustomerNextFee(customerData.getCustomerId())
                        .then()
                        .assertThat()
                        .statusCode(SC_OK)
                        .extract()
                        .as(NextFeeResponseModel.class)
                        .getFee()));
    }

    @Test
    @UserStoryId(3527)
    void shouldRefuseFirstTimeLinkingPublicAddressOnLackOfTokens() {
        val originalFirstTimeLinkingFee = getFirstTimeLinkingFee();
        val customerData = registerDefaultVerifiedCustomer();

        postOrUpdateConfiguration(ConfigurationItemRequestModel
                .builder()
                .type(FIRST_TIME_LINKING_FEE)
                .value(Double.valueOf(UNREAL_FIRST_TIME_LINKING_FEE).toString())
                .build())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(ConfigurationItemUpdateResponseModel.class);

        val temporaryFirstTimeLinkingFee = getFirstTimeLinkingFee();

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
                .signature(signLinkingCode(linkRequestResult.getLinkCode(), Ethereum.WALLET_PRIVATE_KEY))
                .publicAddress(Ethereum.WALLET_PUBLIC_ADDRESS)
                .privateAddress(walletData[0].getPrivateWalletAddress())
                .build())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(LinkingApprovalResponseModel.class);

        assertEquals(LinkingError.NOT_ENOUGH_FUNDS, approvalResult.getError());

        // restore the original setting
        postOrUpdateConfiguration(ConfigurationItemRequestModel
                .builder()
                .type(FIRST_TIME_LINKING_FEE)
                .value(originalFirstTimeLinkingFee)
                .build())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(ConfigurationItemUpdateResponseModel.class);
    }

    @Test
    @UserStoryId(3527)
    void shouldRefuseSubsequentLinkingPublicAddressOnLackOfTokens() {
        val originalSubsequentLinkingFee = getSubsequentLinkingFee();
        val customerData = registerDefaultVerifiedCustomer();
        prepareCustomerWithLinkedPublicAddress(customerData);
        unlinkPublicAddress(customerData);

        postOrUpdateConfiguration(ConfigurationItemRequestModel
                .builder()
                .type(SUBSEQUENT_LINKING_FEE)
                .value(Double.valueOf(UNREAL_SUBSEQUENT_LINKING_FEE).toString())
                .build())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(ConfigurationItemUpdateResponseModel.class);

        val temporarySubsequentLinkingFee = getSubsequentLinkingFee();

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
                .signature(signLinkingCode(linkRequestResult.getLinkCode(), Ethereum.WALLET_PRIVATE_KEY))
                .publicAddress(Ethereum.WALLET_PUBLIC_ADDRESS)
                .privateAddress(walletData[0].getPrivateWalletAddress())
                .build())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(LinkingApprovalResponseModel.class);

        assertEquals(LinkingError.NOT_ENOUGH_FUNDS, approvalResult.getError());

        // restore the original setting
        postOrUpdateConfiguration(ConfigurationItemRequestModel
                .builder()
                .type(SUBSEQUENT_LINKING_FEE)
                .value(originalSubsequentLinkingFee)
                .build())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(ConfigurationItemUpdateResponseModel.class);
    }

    private void prepareCustomerWithLinkedPublicAddress(CustomerInfo customerData) {
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

        val actualLinkRequestResult = getCustomerPublicAddress(customerData.getCustomerId())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(PublicAddressResponseModel.class);

        assertEquals(expectedLinkRequestResult, actualLinkRequestResult);
    }

    private void unlinkPublicAddress(CustomerInfo customerData) {
        val deletionResult = deleteLinkRequest(ByCustomerIdRequest
                .builder()
                .customerId(customerData.getCustomerId())
                .build())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(UnlinkResponseModel.class);

        assertEquals(LinkingError.NONE, deletionResult.getError());

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
    }

    private String getFirstTimeLinkingFee() {
        return getConfigurationByType(FIRST_TIME_LINKING_FEE)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(ConfigurationItemResponseModel.class)
                .getValue();
    }

    private String getSubsequentLinkingFee() {
        return getConfigurationByType(SUBSEQUENT_LINKING_FEE)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(ConfigurationItemResponseModel.class)
                .getValue();
    }
}
