package com.lykke.tests.api.service.customermanagement;

import static com.lykke.api.testing.api.common.GenerateUtils.getRandomUuid;
import static com.lykke.tests.api.common.CommonConsts.ERROR_FIELD_UCFL;
import static com.lykke.tests.api.common.CommonConsts.SMOKE_TEST;
import static com.lykke.tests.api.service.customermanagement.BlockCustomerUtils.blockCustomer;
import static com.lykke.tests.api.service.customermanagement.BlockCustomerUtils.getBlockStatus;
import static com.lykke.tests.api.service.customermanagement.BlockCustomerUtils.getBlockStatusList;
import static com.lykke.tests.api.service.customermanagement.BlockCustomerUtils.unblockCustomer;
import static com.lykke.tests.api.service.customermanagement.LoginCustomerUtils.loginCustomerWithValidEmailAndPassword;
import static com.lykke.tests.api.service.customermanagement.RegisterCustomerUtils.CUSTOMER_ID_FIELD;
import static com.lykke.tests.api.service.customermanagement.RegisterCustomerUtils.TOKEN_FIELD;
import static com.lykke.tests.api.service.customermanagement.RegisterCustomerUtils.registerDefaultVerifiedCustomer;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.lykke.api.testing.annotations.UserStoryId;
import com.lykke.tests.api.base.BaseApiTest;
import com.lykke.tests.api.common.model.CustomerInfo;
import com.lykke.tests.api.service.customermanagement.model.blockeduser.CustomerBlockRequest;
import java.util.Map;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

public class BlockUnblockCustomerTests extends BaseApiTest {

    private static final String ACTIVE_STATUS = "Active";
    private static final String BLOCKED_STATUS = "Blocked";
    private static final String CUSTOMER_BLOCKED_STATUS = "CustomerBlocked";
    private static final String ERROR_STATUS_NONE = "None";
    private static final String CUSTOMER_NOT_BLOCKED_ERR = "CustomerNotBlocked";
    private static final String CUSTOMER_ALREADY_BLOCKED_ERR = "CustomerAlreadyBlocked";
    private static final String CUSTOMER_NOT_FOUND_ERR = "CustomerNotFound";
    private static final String CUSTOMER_BLOCK_STATUSES_FIELD = "CustomersBlockStatuses";
    private static CustomerBlockRequest customerIdObj;
    private CustomerInfo customerData;

    @BeforeEach
    void setup() {
        customerData = registerDefaultVerifiedCustomer();

        customerIdObj = CustomerBlockRequest
                .builder()
                .customerId(customerData.getCustomerId())
                .build();
    }

    @Test
    @UserStoryId(storyId = 1800)
    @Tag(SMOKE_TEST)
    void shouldBlockCustomer() {
        val actualBlockStatus = getBlockStatus(customerData.getCustomerId());

        assertAll(
                () -> assertEquals(ERROR_STATUS_NONE, actualBlockStatus.getError()),
                () -> assertEquals(ACTIVE_STATUS, actualBlockStatus.getStatus())
        );

        assertEquals(blockCustomer(customerIdObj).getError(), ERROR_STATUS_NONE);

        val newBlockStatus = getBlockStatus(customerData.getCustomerId());

        assertAll(
                () -> assertEquals(ERROR_STATUS_NONE, newBlockStatus.getError()),
                () -> assertEquals(BLOCKED_STATUS, newBlockStatus.getStatus())
        );

        loginCustomerWithValidEmailAndPassword(customerData.getEmail(), customerData.getPassword())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .body(CUSTOMER_ID_FIELD, nullValue())
                .body(TOKEN_FIELD, nullValue())
                .body(ERROR_FIELD_UCFL, equalTo(CUSTOMER_BLOCKED_STATUS));
    }

    @Test
    @UserStoryId(storyId = 1800)
    @Tag(SMOKE_TEST)
    void shouldUnblockCustomer() {
        blockCustomer(customerIdObj);

        val blockStatus = getBlockStatus(customerData.getCustomerId());

        assertAll(
                () -> assertEquals(ERROR_STATUS_NONE, blockStatus.getError()),
                () -> assertEquals(BLOCKED_STATUS, blockStatus.getStatus())
        );

        assertEquals(ERROR_STATUS_NONE, unblockCustomer(customerIdObj).getError());

        val newBlockStatus = getBlockStatus(customerData.getCustomerId());

        assertAll(
                () -> assertEquals(ERROR_STATUS_NONE, newBlockStatus.getError()),
                () -> assertEquals(ACTIVE_STATUS, newBlockStatus.getStatus())
        );

        loginCustomerWithValidEmailAndPassword(customerData.getEmail(), customerData.getPassword())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .body(CUSTOMER_ID_FIELD, equalTo(customerData.getCustomerId()))
                .body(TOKEN_FIELD, notNullValue())
                .body(ERROR_FIELD_UCFL, equalTo(ERROR_STATUS_NONE));
    }

    @Test
    @UserStoryId(storyId = 1800)
    void shouldNotUnblockActiveCustomer() {
        assertEquals(CUSTOMER_NOT_BLOCKED_ERR, unblockCustomer(customerIdObj).getError());
    }

    @Test
    @UserStoryId(storyId = 1800)
    void shouldNotBlockBlockedCustomer() {
        blockCustomer(customerIdObj);

        assertEquals(CUSTOMER_ALREADY_BLOCKED_ERR, blockCustomer(customerIdObj).getError());
    }

    @Test
    @UserStoryId(storyId = 1800)
    void shouldNotBlockNotExistingCustomer() {
        customerIdObj = CustomerBlockRequest
                .builder()
                .customerId(getRandomUuid())
                .build();

        assertEquals(CUSTOMER_NOT_FOUND_ERR, blockCustomer(customerIdObj).getError());
    }

    @Test
    @UserStoryId(storyId = 1800)
    void shouldNotUnblockNotExistingCustomer() {
        customerIdObj = CustomerBlockRequest
                .builder()
                .customerId(getRandomUuid())
                .build();

        assertEquals(CUSTOMER_NOT_FOUND_ERR, unblockCustomer(customerIdObj).getError());
    }

    @Test
    @UserStoryId(storyId = 1800)
    void shouldNotGetBlockStatusOfNonExistingCustomer() {
        val blockStatus = getBlockStatus(getRandomUuid());

        assertAll(
                () -> assertEquals(CUSTOMER_NOT_FOUND_ERR, blockStatus.getError()),
                () -> assertNull(blockStatus.getStatus())
        );
    }

    @Test
    @Tag(SMOKE_TEST)
    @UserStoryId(storyId = 2066)
    void shouldGetBlockStatusesByIds() {
        val firstCustomerId = registerDefaultVerifiedCustomer().getCustomerId();
        val secondCustomerId = registerDefaultVerifiedCustomer().getCustomerId();
        val customerList = new String[]{firstCustomerId, secondCustomerId};

        customerIdObj = CustomerBlockRequest
                .builder()
                .customerId(secondCustomerId)
                .build();

        blockCustomer(customerIdObj);

        Map<String, String> responseBlockList = getBlockStatusList(customerList)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .path(CUSTOMER_BLOCK_STATUSES_FIELD);

        assertAll(
                () -> assertEquals(ACTIVE_STATUS, responseBlockList.get(firstCustomerId)),
                () -> assertEquals(BLOCKED_STATUS, (responseBlockList.get(secondCustomerId)))
        );
    }

    @Test
    @UserStoryId(storyId = 2066)
    void shouldNotGetBlockStatusById_NonExistingCustomer() {
        val firstCustomerId = registerDefaultVerifiedCustomer().getCustomerId();
        val secondCustomerId = getRandomUuid();
        val customerList = new String[]{firstCustomerId, secondCustomerId};

        customerIdObj = CustomerBlockRequest
                .builder()
                .customerId(secondCustomerId)
                .build();

        blockCustomer(customerIdObj);

        Map<String, String> responseBlockList = getBlockStatusList(customerList)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .path(CUSTOMER_BLOCK_STATUSES_FIELD);

        assertEquals(1, responseBlockList.size());
    }
}
