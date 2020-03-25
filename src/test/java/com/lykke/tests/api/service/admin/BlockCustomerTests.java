package com.lykke.tests.api.service.admin;

import static com.lykke.api.testing.api.common.GenerateUtils.generateRandomString;
import static com.lykke.tests.api.common.CommonConsts.SMOKE_TEST;
import static com.lykke.tests.api.service.admin.BlockCustomerUtils.blockCustomer;
import static com.lykke.tests.api.service.admin.BlockCustomerUtils.blockCustomerWallet;
import static com.lykke.tests.api.service.admin.BlockCustomerUtils.getCustomerDetails;
import static com.lykke.tests.api.service.admin.BlockCustomerUtils.unblockCustomer;
import static com.lykke.tests.api.service.admin.BlockCustomerUtils.unblockCustomerWallet;
import static com.lykke.tests.api.service.admin.GetCustomersUtils.getCustomersPaginatedResponse;
import static com.lykke.tests.api.service.customer.LoginLogoutCustomerUtils.loginUserWithValidEmailAndPassword;
import static com.lykke.tests.api.service.customermanagement.RegisterCustomerUtils.registerDefaultVerifiedCustomer;
import static com.lykke.tests.api.service.walletmanagement.WalletManagementUtils.balanceTransfer;
import static com.lykke.tests.api.service.walletmanagement.model.TransferErrorCode.NONE;
import static com.lykke.tests.api.service.walletmanagement.model.TransferErrorCode.NOT_ENOUGH_FUNDS;
import static com.lykke.tests.api.service.walletmanagement.model.TransferErrorCode.SOURCE_CUSTOMER_WALLET_BLOCKED;
import static org.apache.http.HttpStatus.SC_NO_CONTENT;
import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import com.lykke.api.testing.annotations.UserStoryId;
import com.lykke.tests.api.base.BaseApiTest;
import com.lykke.tests.api.common.model.CustomerInfo;
import com.lykke.tests.api.service.admin.model.CustomerActivityStatus;
import com.lykke.tests.api.service.admin.model.CustomerListRequest;
import com.lykke.tests.api.service.admin.model.CustomerWalletActivityStatus;
import com.lykke.tests.api.service.walletmanagement.model.TransferBalanceRequestModel;
import com.lykke.tests.api.service.walletmanagement.model.TransferBalanceResponse;
import com.lykke.tests.api.service.walletmanagement.model.TransferErrorCode;
import java.util.Arrays;
import java.util.stream.Collectors;
import lombok.val;
import lombok.var;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

public class BlockCustomerTests extends BaseApiTest {

    private static final Double AMOUNT_TO_TRANSFER = 1.0;

    private static final int VALID_PAGE_SIZE = 100;
    private static final int VALID_1ST_CURRENT_PAGE = 1;
    private static final int VALID_2ND_CURRENT_PAGE = 2;

    private CustomerInfo customerData;

    @BeforeEach
    void setUp() {
        customerData = registerDefaultVerifiedCustomer();
    }

    @Test
    @Tag(SMOKE_TEST)
    @UserStoryId(1804)
    void shouldBlockCustomer() {
        blockCustomer(customerData.getCustomerId())
                .then()

                ////55
                .log().all()

                .assertThat()
                .statusCode(SC_NO_CONTENT);

        assertEquals(CustomerActivityStatus.BLOCKED,
                getCustomerDetails(customerData.getCustomerId()).getCustomerStatus());
        loginUserWithValidEmailAndPassword(customerData.getEmail(), customerData.getPassword())
                .then()
                .assertThat()
                .statusCode(SC_UNAUTHORIZED);
    }

    @Test
    @UserStoryId(3055)
    void shouldBlockCustomerStatus() {
        blockCustomer(customerData.getCustomerId())
                .then()
                .assertThat()
                .statusCode(SC_NO_CONTENT);

        assertEquals(CustomerActivityStatus.BLOCKED,
                getCustomerDetails(customerData.getCustomerId()).getCustomerStatus());

        val requestObject = CustomerListRequest
                .builder()
                .currentPage(VALID_1ST_CURRENT_PAGE)
                .pageSize(VALID_PAGE_SIZE)
                .build();

        val actualCustomers = getCustomersPaginatedResponse(requestObject);
        var customerObject = Arrays.stream(actualCustomers.getCustomers())
                .filter(c -> c.getCustomerId().equals(customerData.getCustomerId())).findAny();

        if (customerObject.isPresent()) {
            assertEquals(customerObject.get().getCustomerStatus(), CustomerActivityStatus.BLOCKED);
        } else {
            fail("customer is not present in paginated response");
        }
    }


    @Test
    @UserStoryId(1804)
    void shouldUnblockCustomer() {
        blockCustomer(customerData.getCustomerId())
                .then()
                .assertThat()
                .statusCode(SC_NO_CONTENT);

        assertEquals(CustomerActivityStatus.BLOCKED,
                getCustomerDetails(customerData.getCustomerId()).getCustomerStatus());
        loginUserWithValidEmailAndPassword(customerData.getEmail(), customerData.getPassword())
                .then()
                .assertThat()
                .statusCode(SC_UNAUTHORIZED);

        unblockCustomer(customerData.getCustomerId())
                .then()
                .assertThat()
                .statusCode(SC_NO_CONTENT);

        assertEquals(CustomerActivityStatus.ACTIVE,
                getCustomerDetails(customerData.getCustomerId()).getCustomerStatus());
        loginUserWithValidEmailAndPassword(customerData.getEmail(), customerData.getPassword())
                .then()
                .assertThat()
                .statusCode(SC_OK);
    }

    @Test
    @UserStoryId(1804)
    void shouldBlockCustomerWallet() {
        val recipientData = registerDefaultVerifiedCustomer();
        blockCustomerWallet(customerData.getCustomerId())
                .then()
                .assertThat()
                .statusCode(SC_NO_CONTENT);

        assertEquals(CustomerWalletActivityStatus.BLOCKED,
                getCustomerDetails(customerData.getCustomerId()).getWalletStatus());
        val requestObject = TransferBalanceRequestModel
                .builder()
                .senderCustomerId(customerData.getCustomerId())
                .receiverCustomerId(recipientData.getCustomerId())
                .amount(AMOUNT_TO_TRANSFER.toString())
                .operationId(generateRandomString(100))
                .build();
        val actualResult = balanceTransfer(requestObject)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(TransferBalanceResponse.class);
        assertEquals(SOURCE_CUSTOMER_WALLET_BLOCKED, actualResult.getErrorCode());
    }

    @Test
    @UserStoryId(1804)
    void shouldUnblockCustomerWallet() {
        val recipientData = registerDefaultVerifiedCustomer();
        blockCustomerWallet(customerData.getCustomerId())
                .then()
                .assertThat()
                .statusCode(SC_NO_CONTENT);
        assertEquals(CustomerWalletActivityStatus.BLOCKED,
                getCustomerDetails(customerData.getCustomerId()).getWalletStatus());

        unblockCustomerWallet(customerData.getCustomerId())
                .then()
                .assertThat()
                .statusCode(SC_NO_CONTENT);
        assertEquals(CustomerWalletActivityStatus.ACTIVE,
                getCustomerDetails(customerData.getCustomerId()).getWalletStatus());
        val requestObject = TransferBalanceRequestModel
                .builder()
                .senderCustomerId(customerData.getCustomerId())
                .receiverCustomerId(recipientData.getCustomerId())
                .amount(AMOUNT_TO_TRANSFER.toString())
                .operationId(generateRandomString(100))
                .build();
        val actualResult = balanceTransfer(requestObject)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(TransferBalanceResponse.class);
        assertTrue(Arrays.stream(new TransferErrorCode[]{NONE, NOT_ENOUGH_FUNDS}).collect(Collectors.toList())
                .contains(actualResult.getErrorCode()));
    }
}
