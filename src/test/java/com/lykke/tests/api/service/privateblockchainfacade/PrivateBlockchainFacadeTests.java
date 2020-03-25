package com.lykke.tests.api.service.privateblockchainfacade;

import static com.lykke.api.testing.api.common.GenerateUtils.generateRandomEmail;
import static com.lykke.api.testing.api.common.GenerateUtils.generateRandomHash;
import static com.lykke.api.testing.api.common.GenerateUtils.getRandomUuid;
import static com.lykke.tests.api.service.customermanagement.RegisterCustomerUtils.registerCustomer;
import static com.lykke.tests.api.service.privateblockchainfacade.PrivateBlockchainFacadeUtils.getAcceptedOperations;
import static com.lykke.tests.api.service.privateblockchainfacade.PrivateBlockchainFacadeUtils.getNewOperations;
import static com.lykke.tests.api.service.privateblockchainfacade.PrivateBlockchainFacadeUtils.getTotalAmount;
import static com.lykke.tests.api.service.privateblockchainfacade.PrivateBlockchainFacadeUtils.postWallets;
import static com.lykke.tests.api.service.privateblockchainfacade.PrivateBlockchainFacadeUtils.putAcceptedOperations;
import static com.lykke.tests.api.service.privateblockchainfacade.PrivateBlockchainFacadeUtils.putFailedOperations;
import static com.lykke.tests.api.service.privateblockchainfacade.PrivateBlockchainFacadeUtils.putSucceededOperations;
import static com.lykke.tests.api.service.privateblockchainfacade.model.CustomerWalletCreationError.ALREADY_CREATED;
import static com.lykke.tests.api.service.privateblockchainfacade.model.CustomerWalletCreationError.NONE;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.params.provider.Arguments.of;

import com.lykke.api.testing.annotations.UserStoryId;
import com.lykke.tests.api.base.BaseApiTest;
import com.lykke.tests.api.service.privateblockchainfacade.model.AcceptedOperationResponseModel;
import com.lykke.tests.api.service.privateblockchainfacade.model.CustomerWalletCreationResponseModel;
import com.lykke.tests.api.service.privateblockchainfacade.model.NewOperationResponseModel;
import com.lykke.tests.api.service.privateblockchainfacade.model.OperationStatusUpdateError;
import com.lykke.tests.api.service.privateblockchainfacade.model.OperationStatusUpdateResponseModel;
import com.lykke.tests.api.service.privateblockchainfacade.model.TotalTokensSupplyResponse;
import java.util.Arrays;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Stream;
import lombok.val;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class PrivateBlockchainFacadeTests extends BaseApiTest {

    public static final int HARD_CODED_RESPONSE = 1024;
    private static final String ERROR_MESSAGE_FIELD_PATH = "ErrorMessage";
    private static final String MODEL_ERRORS_CUSTOMER_ID_0_FIELD_PATH = "ModelErrors.CustomerId[0]";
    private static final Function<String, String> ERROR_CONVERTING_VALUE_TO_TYPE_SYSTEM_GUID_PATH_CUSTOMER_ID_LINE_1_POSITION_ERROR_MESSAGE =
            (value) -> String
                    .format("Error converting value \"%s\" to type 'System.Guid'. Path 'CustomerId', line 1, position",
                            value);
    private static final String INVALID_CUSTOMER_ID_01 = EMPTY;
    private static final String INVALID_CUSTOMER_ID_02 = "aaa";
    private static final String INVALID_CUSTOMER_ID_03 = "111";
    private static final int MAX_NUMBER_OF_ELEMENTS = 100;
    private static final String CUSTOMER_WALLET_CREATION = "CustomerWalletCreation";

    static Stream<Arguments> getInvalidCustomerIds() {
        return Stream.of(
                of(INVALID_CUSTOMER_ID_01),
                of(INVALID_CUSTOMER_ID_02),
                of(INVALID_CUSTOMER_ID_03)
        );
    }

    @Test
    @UserStoryId(storyId = {971, 1223})
    void shouldCreateWalletForNewCustomer() {
        val actualResult = postWallets(UUID.randomUUID().toString())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(CustomerWalletCreationResponseModel.class);

        assertEquals(NONE, actualResult.getError());
    }

    @Test
    @UserStoryId(storyId = {971, 1223})
    void shouldNotCreateWalletIfCustomerAlreadyHasOne() {
        val customerEmail = generateRandomEmail();
        val customerId = registerCustomer();
        postWallets(customerId)
                .then()
                .assertThat()
                .statusCode(SC_OK);

        val actualResult = postWallets(customerId)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(CustomerWalletCreationResponseModel.class);

        assertEquals(ALREADY_CREATED, actualResult.getError());
    }

    @Test
    @UserStoryId(storyId = {971, 1223})
    void shouldNotCreateWalletForNonExistingCustomerId() {
        val actualResult = postWallets(getRandomUuid())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(CustomerWalletCreationResponseModel.class);

        assertEquals(NONE, actualResult.getError());
    }

    @ParameterizedTest
    @MethodSource("getInvalidCustomerIds")
    @UserStoryId(storyId = {971, 1223})
    void shouldNotCreateWalletForInvalidCustomerId(String customerId) {
        postWallets(customerId)
                .then()
                .assertThat()
                .statusCode(SC_BAD_REQUEST);

        val actualResult = postWallets(customerId)
                .then()
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .body(ERROR_MESSAGE_FIELD_PATH, containsString(
                        ERROR_CONVERTING_VALUE_TO_TYPE_SYSTEM_GUID_PATH_CUSTOMER_ID_LINE_1_POSITION_ERROR_MESSAGE
                                .apply(customerId)))
                .body(MODEL_ERRORS_CUSTOMER_ID_0_FIELD_PATH, containsString(
                        ERROR_CONVERTING_VALUE_TO_TYPE_SYSTEM_GUID_PATH_CUSTOMER_ID_LINE_1_POSITION_ERROR_MESSAGE
                                .apply(customerId)));
    }

    @Test
    @UserStoryId(storyId = {971, 1223})
    void shouldReturnNewOperations() {
        val newCustomerId = UUID.randomUUID().toString();
        postWallets(newCustomerId)
                .then()
                .assertThat()
                .statusCode(SC_OK);

        val actualResultCollection = getNewOperations()
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(NewOperationResponseModel[].class);

        val actualTransactionCandidate = Arrays.stream(actualResultCollection)
                .filter(tran -> tran.getPayloadJson().contains(newCustomerId))
                .findFirst();

        val actualTransaction = actualTransactionCandidate.orElseGet(NewOperationResponseModel::new);

        assertAll(
                () -> assertTrue(MAX_NUMBER_OF_ELEMENTS >= actualResultCollection.length),
                () -> assertNotEquals(EMPTY, actualTransaction.getId()),
                () -> assertNotEquals(0, actualTransaction.getNonce()),
                () -> assertEquals(CUSTOMER_WALLET_CREATION, actualTransaction.getType())
        );
    }

    @Test
    @UserStoryId(2779)
    void shouldGetAcceptedOperations() {
        val expectedResult = AcceptedOperationResponseModel
                .builder()
                .build();

        val actualResult = getAcceptedOperations()
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(AcceptedOperationResponseModel[].class);

        // assertEquals(OperationStatusUpdateError.NONE, actualResult.getError());
    }

    @Disabled("this test generates a transaction hash and puts it, but nonce could be equal to real transaction's nonce, and all the blockchain stops")
    @Test
    @UserStoryId(storyId = {971, 1223})
    void shouldChangeAcceptedOperations() {
        val customerId = registerCustomer();
        postWallets(customerId)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(CustomerWalletCreationResponseModel.class);

        val actualTransaction = getActualTransaction(customerId);

        // TODO: get the actual transaction hash
        val actualResult = putAcceptedOperations(
                actualTransaction.getId().toString(),
                generateRandomHash())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(OperationStatusUpdateResponseModel.class);

        assertEquals(OperationStatusUpdateError.NONE, actualResult.getError());
    }

    @Test
    @UserStoryId(2521)
    void shouldNotChangeAcceptedOperationsWithoutApiKey() {
        val customerEmail = generateRandomEmail();
        val customerId = registerCustomer();
        postWallets(customerId)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(CustomerWalletCreationResponseModel.class);

        val actualTransaction = getActualTransaction(customerId);

        // TODO: get the actual transaction hash
        val actualResult = putAcceptedOperations(
                actualTransaction.getId().toString(),
                generateRandomHash())
                .then()
                .assertThat()
                .statusCode(SC_UNAUTHORIZED);
    }

    @Disabled("this test generates a transaction hash and puts it, but nonce could be equal to real transaction's nonce, and all the blockchain stops")
    @Test
    @UserStoryId(storyId = {971, 1223})
    void shouldChangeFailedOperations() {
        val customerId = registerCustomer();
        postWallets(customerId)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(CustomerWalletCreationResponseModel.class);

        // TODO: get the actual transaction hash
        val hash = generateRandomHash();
        val acceptedTransaction = getAcceptedTransaction(customerId, hash);
        putFailedOperations(hash)
                .then()
                .assertThat()
                .statusCode(SC_OK);

        assertEquals(OperationStatusUpdateError.NONE, acceptedTransaction.getError());
    }

    @Test
    @UserStoryId(2521)
    void shouldNotChangeFailedOperationsWithoutApiKey() {
        val customerId = registerCustomer();
        postWallets(customerId)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(CustomerWalletCreationResponseModel.class);

        // TODO: get the actual transaction hash
        val hash = generateRandomHash();
        putFailedOperations(hash)
                .then()
                .assertThat()
                .statusCode(SC_UNAUTHORIZED);
    }

    @Disabled("this test generates a transaction hash and puts it, but nonce could be equal to real transaction's nonce, and all the blockchain stops")
    @Test
    @UserStoryId(storyId = {971, 1223})
    void shouldChangeSucceededOperations() {
        val customerId = registerCustomer();
        postWallets(customerId)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(CustomerWalletCreationResponseModel.class);

        // TODO: get the actual transaction hash
        val hash = generateRandomHash();
        val acceptedTransaction = getAcceptedTransaction(customerId, hash);
        putSucceededOperations(hash)
                .then()
                .assertThat()
                .statusCode(SC_OK);

        // TODO: assertions
    }

    @Test
    @UserStoryId(2521)
    void shouldNotChangeSucceededOperationsWithoutApiKey() {
        val customerEmail = generateRandomEmail();
        val customerId = registerCustomer();
        postWallets(customerId)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(CustomerWalletCreationResponseModel.class);

        // TODO: get the actual transaction hash
        val hash = generateRandomHash();
        putSucceededOperations(hash)
                .then()
                .assertThat()
                .statusCode(SC_UNAUTHORIZED);
    }

    @Test
    @UserStoryId(1029)
        // TODO: now the value is hard-coded to 1024, in the future will be taken from the OperationExecutor service
    void shouldReturnTotalAmountValue() {
        val actualResult = getTotalAmount()
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(TotalTokensSupplyResponse.class);

        // now it's not 1024, it's around 13610533 or greater
        assertTrue(HARD_CODED_RESPONSE < actualResult.getTotalTokensAmount());
    }

    private OperationStatusUpdateResponseModel getAcceptedTransaction(String customerId, String hash) {
        val actualTransaction = getActualTransaction(customerId);

        // TODO: get the actual transaction hash
        return putAcceptedOperations(
                actualTransaction.getId().toString(),
                hash)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(OperationStatusUpdateResponseModel.class);
    }

    private NewOperationResponseModel getActualTransaction(String customerId) {
        val actualResultCollection = getNewOperations()
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(NewOperationResponseModel[].class);

        val actualTransactionCandidate = Arrays.stream(actualResultCollection)
                .filter(tran -> tran.getPayloadJson().contains(customerId))
                .findFirst();

        return actualTransactionCandidate.orElseGet(NewOperationResponseModel::new);
    }
}
