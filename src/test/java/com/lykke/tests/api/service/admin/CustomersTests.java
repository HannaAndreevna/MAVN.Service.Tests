package com.lykke.tests.api.service.admin;

import static com.lykke.tests.api.common.CommonConsts.AwaitilityConsts.TIME_TO_MINE_30_BLOCKS_MINS;
import static com.lykke.tests.api.common.CommonConsts.SMOKE_TEST;
import static com.lykke.tests.api.service.admin.CustomersUtils.getCustomerPublicWalletAddress;
import static com.lykke.tests.api.service.admin.CustomersUtils.getCustomerPublicWalletAddress_Deprecated;
import static com.lykke.tests.api.service.admin.CustomersUtils.getCustomerWalletAddress;
import static com.lykke.tests.api.service.admin.LoginAdminUtils.getAdminToken;
import static com.lykke.tests.api.service.crosschainwalletlinker.CrossChainWalletLinkerUtils.getCustomerPublicAddress;
import static com.lykke.tests.api.service.customer.CustomerWalletUtils.approveLinkRequest;
import static com.lykke.tests.api.service.customer.CustomerWalletUtils.getCustomerWallets;
import static com.lykke.tests.api.service.customer.CustomerWalletUtils.postLinkRequest;
import static com.lykke.tests.api.service.customer.LoginLogoutCustomerUtils.getUserToken;
import static com.lykke.tests.api.service.customermanagement.RegisterCustomerUtils.registerDefaultVerifiedCustomer;
import static com.lykke.tests.api.service.privateblockchainfacade.PrivateBlockchainFacadeUtils.createCustomerFundedViaBonusReward;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.http.HttpStatus.SC_NO_CONTENT;
import static org.apache.http.HttpStatus.SC_OK;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.lykke.api.testing.annotations.UserStoryId;
import com.lykke.tests.api.base.BaseApiTest;
import com.lykke.tests.api.common.CommonConsts.Ethereum;
import com.lykke.tests.api.common.EthereumUtils;
import com.lykke.tests.api.common.model.CustomerInfo;
import com.lykke.tests.api.service.admin.model.CustomerPrivateWalletAddressResponse;
import com.lykke.tests.api.service.admin.model.CustomerPublicWalletAddressResponse;
import com.lykke.tests.api.service.crosschainwalletlinker.model.LinkingRequestResponseModel;
import com.lykke.tests.api.service.crosschainwalletlinker.model.PublicAddressError;
import com.lykke.tests.api.service.crosschainwalletlinker.model.PublicAddressResponseModel;
import com.lykke.tests.api.service.admin.model.extrawallet.PublicAddressStatus;
import com.lykke.tests.api.service.customer.model.wallets.ApproveExternalWalletLinkRequest;
import com.lykke.tests.api.service.privateblockchainfacade.PrivateBlockchainFacadeUtils;
import java.util.concurrent.TimeUnit;
import lombok.val;
import org.awaitility.Awaitility;
import org.awaitility.Duration;
import org.junit.Assert;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

public class CustomersTests extends BaseApiTest {

    private static final String SAMPLE_WALLET_ADDRESS = "0x936a8aedb40fc93cd766f4640b4a76bf428643af";
    private static final String SAMPLE_PUBLIC_ADDRESS = Ethereum.WALLET_PUBLIC_ADDRESS;

    @Test
    @Tag(SMOKE_TEST)
    @UserStoryId(2652)
    void shouldGetCustomerWalletAddress() {
        val customerData = registerDefaultVerifiedCustomer();

        val actualResult = getCustomerWalletAddress(customerData.getCustomerId(), getAdminToken())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(CustomerPrivateWalletAddressResponse.class);

        assertAll(
                () -> assertNotNull(actualResult.getWalletAddress()),
                () -> assertEquals(SAMPLE_WALLET_ADDRESS.length(), actualResult.getWalletAddress().length())
        );
    }

    @Test
    @UserStoryId(3522)
    void shouldGetCustomerPublicWalletAddress_Deprecated() {
        val customerData = PrivateBlockchainFacadeUtils.createCustomerFundedViaBonusReward(100.0, false);
        linkPublicAddress(customerData);

        val actualResult = getCustomerPublicWalletAddress_Deprecated(customerData.getCustomerId(), getAdminToken())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(CustomerPublicWalletAddressResponse.class);

        assertAll(
                () -> assertNotNull(actualResult.getPublicAddress()),
                () -> assertEquals(SAMPLE_PUBLIC_ADDRESS.length(), actualResult.getPublicAddress().length())
        );
    }

    @Test
    @UserStoryId(3886)
    void shouldGetCustomerPublicWalletAddress() {
        val customerData = PrivateBlockchainFacadeUtils.createCustomerFundedViaBonusReward(100.0, false);
        linkPublicAddress(customerData);

        val actualResult = getCustomerPublicWalletAddress(customerData.getCustomerId(), getAdminToken())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(CustomerPublicWalletAddressResponse.class);

        assertAll(
                () -> assertNotNull(actualResult.getPublicAddress()),
                () -> assertEquals(SAMPLE_PUBLIC_ADDRESS.length(), actualResult.getPublicAddress().length())
        );
    }

    @Test
    @UserStoryId(3522)
    void shouldNotGetCustomerPublicWalletAddressIfMissing_Deprecated() {
        val customerData = registerDefaultVerifiedCustomer();

        val actualResult = getCustomerPublicWalletAddress_Deprecated(customerData.getCustomerId(), getAdminToken())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(CustomerPublicWalletAddressResponse.class);

        assertAll(
                () -> assertEquals(EMPTY, actualResult.getPublicAddress()),
                () -> assertEquals(PublicAddressStatus.NOT_LINKED, actualResult.getStatus())
        );
    }

    @Test
    @UserStoryId(3886)
    void shouldNotGetCustomerPublicWalletAddressIfMissing() {
        val customerData = registerDefaultVerifiedCustomer();

        val actualResult = getCustomerPublicWalletAddress(customerData.getCustomerId(), getAdminToken())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(CustomerPublicWalletAddressResponse.class);

        assertAll(
                () -> assertEquals(EMPTY, actualResult.getPublicAddress()),
                () -> assertEquals(PublicAddressStatus.NOT_LINKED, actualResult.getStatus())
        );
    }

    private void linkPublicAddress(CustomerInfo customerData) {
        val token = getUserToken(customerData);
        val linkRequestResult = postLinkRequest(token)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(LinkingRequestResponseModel.class);

        Assert.assertNotNull(linkRequestResult.getLinkCode());

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

        // TODO: use the public api
        val expectedLinkRequestResult = PublicAddressResponseModel
                .builder()
                .status(com.lykke.tests.api.service.crosschainwalletlinker.model.PublicAddressStatus.LINKED)
                .publicAddress(Ethereum.WALLET_PUBLIC_ADDRESS)
                .error(PublicAddressError.NONE)
                .build();

        Awaitility.await()
                .atMost(TIME_TO_MINE_30_BLOCKS_MINS, TimeUnit.MINUTES)
                .pollInterval(Duration.FIVE_SECONDS)
                .until(() -> com.lykke.tests.api.service.crosschainwalletlinker.model.PublicAddressStatus.LINKED
                        == getCustomerPublicAddress(customerData.getCustomerId())
                        .then()
                        .assertThat()
                        .statusCode(SC_OK)
                        .extract()
                        .as(PublicAddressResponseModel.class)
                        .getStatus());
    }
}
