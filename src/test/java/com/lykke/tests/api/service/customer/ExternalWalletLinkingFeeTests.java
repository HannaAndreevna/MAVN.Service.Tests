package com.lykke.tests.api.service.customer;

import static com.lykke.tests.api.common.CommonConsts.AwaitilityConsts.TIME_TO_MINE_30_BLOCKS_MINS;
import static com.lykke.tests.api.common.EthereumUtils.signLinkingCode;
import static com.lykke.tests.api.service.crosschainwalletlinker.CrossChainWalletLinkerUtils.getConfigurationByType;
import static com.lykke.tests.api.service.crosschainwalletlinker.CrossChainWalletLinkerUtils.getCustomerPublicAddress;
import static com.lykke.tests.api.service.crosschainwalletlinker.CrossChainWalletLinkerUtils.postOrUpdateConfiguration;
import static com.lykke.tests.api.service.crosschainwalletlinker.model.ConfigurationItemType.FIRST_TIME_LINKING_FEE;
import static com.lykke.tests.api.service.crosschainwalletlinker.model.ConfigurationItemType.SUBSEQUENT_LINKING_FEE;
import static com.lykke.tests.api.service.customer.CustomerWalletUtils.approveLinkRequest;
import static com.lykke.tests.api.service.customer.CustomerWalletUtils.deleteLinkRequest;
import static com.lykke.tests.api.service.customer.CustomerWalletUtils.getCustomerWallets;
import static com.lykke.tests.api.service.customer.CustomerWalletUtils.getNextFee;
import static com.lykke.tests.api.service.customer.CustomerWalletUtils.postLinkRequest;
import static com.lykke.tests.api.service.customer.HistoryUtils.getOperationsHistoryResponse;
import static com.lykke.tests.api.service.customer.LoginLogoutCustomerUtils.getUserToken;
import static com.lykke.tests.api.service.customermanagement.RegisterCustomerUtils.registerDefaultVerifiedCustomer;
import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.apache.http.HttpStatus.SC_NO_CONTENT;
import static org.apache.http.HttpStatus.SC_OK;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.lykke.api.testing.annotations.UserStoryId;
import com.lykke.tests.api.base.BaseApiTest;
import com.lykke.tests.api.common.CommonConsts.Ethereum;
import com.lykke.tests.api.common.model.CustomerInfo;
import com.lykke.tests.api.service.crosschainwalletlinker.model.ConfigurationItemRequestModel;
import com.lykke.tests.api.service.crosschainwalletlinker.model.ConfigurationItemResponseModel;
import com.lykke.tests.api.service.crosschainwalletlinker.model.ConfigurationItemUpdateResponseModel;
import com.lykke.tests.api.service.crosschainwalletlinker.model.PublicAddressError;
import com.lykke.tests.api.service.crosschainwalletlinker.model.PublicAddressResponseModel;
import com.lykke.tests.api.service.crosschainwalletlinker.model.PublicAddressStatus;
import com.lykke.tests.api.service.customer.CustomerWalletUtils.ValidationErrorResponse;
import com.lykke.tests.api.service.customer.model.history.HistoryOperationType;
import com.lykke.tests.api.service.customer.model.history.OperationHistoryResponseModel;
import com.lykke.tests.api.service.customer.model.history.OperationsHistoryRequest;
import com.lykke.tests.api.service.customer.model.wallets.ApproveExternalWalletLinkRequest;
import com.lykke.tests.api.service.customer.model.wallets.LinkWalletResponse;
import com.lykke.tests.api.service.customer.model.wallets.NextWalletLinkingFeeResponseModel;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import lombok.val;
import org.awaitility.Awaitility;
import org.awaitility.Duration;
import org.junit.jupiter.api.Test;

public class ExternalWalletLinkingFeeTests extends BaseApiTest {

    private static final int UNREAL_FIRST_TIME_LINKING_FEE = 100_000;
    private static final int UNRREAL_SUBSEQUENT_LINKING_FEE = 100_000;
    private static final String NOT_ENOUGH_TOKENS_ERROR_CODE = "NotEnoughTokens";
    private static final String NOT_ENOUGH_TOKENS_ERROR_MESSAGE = "Not enough tokens";

    @Test
    @UserStoryId(3530)
    void shouldGetCustomerNextFee() {
        val firstTimeLinkingFee = getFirstTimeLinkingFee();
        val nextFee = getSubsequentLinkingFee();
        val customerData = registerDefaultVerifiedCustomer();
        val customerToken = getUserToken(customerData);
        assertEquals(
                Double.valueOf(firstTimeLinkingFee),
                Double.valueOf(getNextFee(customerToken)
                        .then()
                        .assertThat()
                        .statusCode(SC_OK)
                        .extract()
                        .as(NextWalletLinkingFeeResponseModel.class)
                        .getFee()));

        prepareCustomerWithLinkedPublicAddress(customerData);
        unlinkPublicAddress(customerData);

        val linkRequestResult = postLinkRequest(customerToken)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(LinkWalletResponse.class);

        assertNotNull(linkRequestResult.getLinkCode());

        assertEquals(
                Double.valueOf(nextFee),
                Double.valueOf(getNextFee(customerToken)
                        .then()
                        .assertThat()
                        .statusCode(SC_OK)
                        .extract()
                        .as(NextWalletLinkingFeeResponseModel.class)
                        .getFee()));
    }

    @Test
    @UserStoryId(3792)
    void shouldGetLinkHistory() {
        val firstTimeLinkingFee = getFirstTimeLinkingFee();
        val nextFee = getSubsequentLinkingFee();
        val customerData = registerDefaultVerifiedCustomer();
        val customerToken = getUserToken(customerData);
        assertEquals(
                Double.valueOf(firstTimeLinkingFee),
                Double.valueOf(getNextFee(customerToken)
                        .then()
                        .assertThat()
                        .statusCode(SC_OK)
                        .extract()
                        .as(NextWalletLinkingFeeResponseModel.class)
                        .getFee()));

        prepareCustomerWithLinkedPublicAddress(customerData);
        unlinkPublicAddress(customerData);
        prepareCustomerWithLinkedPublicAddress(customerData);

        /*
        GET
        /api/history/operations
        Get operations history for the authorized user

        "LinkWalletOperations": [
           {
             "OperationId": "b5d01089-e134-47ec-9fde-7114825bea4a",
             "CustomerId": "c6e399df-ae3a-42c6-b727-2db57789d32b",
             "Fee": "0.1",
             "PrivateAddress": "0x252237ad6b4c8178a40da791325ebf238aad3ef5",
             "PublicAddress": "0x3ff662AF6C47e92d8F4BB23deeC3cf58AdBB2796",
             "Direction": "Link",
             "Timestamp": "2019-11-13T09:50:14.3201477",
             "AssetSymbol": "MVN"
           }
         ],
        */

        val actualResultCollection = getOperationsHistoryResponse(OperationsHistoryRequest
                .builder()
                .currentPage(1)
                .pageSize(500)
                .build())
                .getOperations();

        val actualResult = Arrays.stream(actualResultCollection)
                .filter(item -> item.getType() == HistoryOperationType.WALLET_LINKING)
                .findFirst()
                .orElse(new OperationHistoryResponseModel());

        assertEquals(nextFee, actualResult.getAmount());
    }

    @Test
    @UserStoryId(3530)
    void shouldRefuseFirstTimeLinkingPublicAddressOnLackOfTokens() {
        val originalFirstTimeLinkingFee = getFirstTimeLinkingFee();
        val customerData = registerDefaultVerifiedCustomer();
        val customerToken = getUserToken(customerData);

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

        val linkRequestResult = postLinkRequest(customerToken)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(LinkWalletResponse.class);

        assertNotNull(linkRequestResult.getLinkCode());

        val walletData = getCustomerWallets(getUserToken(customerData));

        val expectedResult = ValidationErrorResponse
                .builder()
                .error(NOT_ENOUGH_TOKENS_ERROR_CODE)
                .message(NOT_ENOUGH_TOKENS_ERROR_MESSAGE)
                .build();

        val approvalResult = approveLinkRequest(ApproveExternalWalletLinkRequest
                .builder()
                .signature(signLinkingCode(linkRequestResult.getLinkCode(), Ethereum.WALLET_PRIVATE_KEY))
                .publicAddress(Ethereum.WALLET_PUBLIC_ADDRESS)
                .privateAddress(walletData[0].getPrivateWalletAddress())
                .build())
                .then()
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .extract()
                .as(ValidationErrorResponse.class);

        assertEquals(expectedResult, approvalResult);

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
    @UserStoryId(3530)
    void shouldRefuseSubsequentLinkingPublicAddressOnLackOfTokens() {
        val originalSubsequentLinkingFee = getSubsequentLinkingFee();
        val customerData = registerDefaultVerifiedCustomer();
        val customerToken = getUserToken(customerData);
        prepareCustomerWithLinkedPublicAddress(customerData);
        unlinkPublicAddress(customerData);

        postOrUpdateConfiguration(ConfigurationItemRequestModel
                .builder()
                .type(SUBSEQUENT_LINKING_FEE)
                .value(Double.valueOf(UNRREAL_SUBSEQUENT_LINKING_FEE).toString())
                .build())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(ConfigurationItemUpdateResponseModel.class);

        val temporarySubsequentLinkingFee = getSubsequentLinkingFee();

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
                .signature(signLinkingCode(linkRequestResult.getLinkCode(), Ethereum.WALLET_PRIVATE_KEY))
                .publicAddress(Ethereum.WALLET_PUBLIC_ADDRESS)
                .privateAddress(walletData[0].getPrivateWalletAddress())
                .build())
                .then()
                .assertThat()
                .statusCode(SC_BAD_REQUEST);

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
        val linkRequestResult = postLinkRequest(getUserToken(customerData))
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(LinkWalletResponse.class);

        assertNotNull(linkRequestResult.getLinkCode());

        val walletData = getCustomerWallets(getUserToken(customerData));

        approveLinkRequest(ApproveExternalWalletLinkRequest
                .builder()
                .signature(signLinkingCode(linkRequestResult.getLinkCode(), Ethereum.WALLET_PRIVATE_KEY))
                .publicAddress(Ethereum.WALLET_PUBLIC_ADDRESS)
                .privateAddress(walletData[0].getPrivateWalletAddress())
                .build())
                .then()
                .assertThat()
                .statusCode(SC_NO_CONTENT);

        // TODO: use public api
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
        deleteLinkRequest(getUserToken(customerData))
                .then()
                .assertThat()
                .statusCode(SC_NO_CONTENT);

        // TODO: use public api
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
