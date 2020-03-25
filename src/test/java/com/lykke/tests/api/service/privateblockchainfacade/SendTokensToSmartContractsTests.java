package com.lykke.tests.api.service.privateblockchainfacade;

import static com.lykke.api.testing.api.common.BuilderUtils.getObjectWithData;
import static com.lykke.api.testing.api.common.GenerateUtils.getRandomUuid;
import static com.lykke.tests.api.service.privateblockchainfacade.PrivateBlockchainFacadeUtils.postGenericTransfer;
import static com.lykke.tests.api.service.privateblockchainfacade.model.TransferError.SENDER_WALLET_MISSING;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.params.provider.Arguments.of;

import com.lykke.api.testing.annotations.UserStoryId;
import com.lykke.tests.api.base.BaseApiTest;
import com.lykke.tests.api.service.privateblockchainfacade.model.GenericTransferRequestModel;
import io.restassured.response.ValidatableResponse;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;
import lombok.val;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class SendTokensToSmartContractsTests extends BaseApiTest {

    private static final String FAKE_DATA_0X12345 = "0x12345";
    private static final String FAKE_ADDRESS_0X123456 = "0x123456";
    private static final int SOME_AMOUNT = 10;
    private static final int ZERO_AMOUNT = 0;
    private static final String THE_FIELD_AMOUNT_MUST_BE_BETWEEN_1_AND_2147483647_ERROR_MESSAGE = "The field Amount must be between 1 and 2147483647.";
    private static final String THE_SENDER_CUSTOMER_ID_FIELD_IS_REQUIRED_ERROR_MESSAGE = "The SenderCustomerId field is required.";
    private static final String THE_RECIPIENT_ADDRESS_FIELD_IS_REQUIRED_ERROR_MESSAGE = "The RecipientAddress field is required.";
    private static final String AMOUNT_FIELD_PATH = "Amount[0]";
    private static final String RECIPIENT_ADDRESS_FIELD_PATH = "RecipientAddress[0]";
    private static final String SENDER_CUSTOMER_ID_FIELD_PATH = "SenderCustomerId[0]";
    private static final String ERROR_FIELD_PATH = "Error";
    private static final String OPERATION_ID_FIELD_PATH = "OperationId";
    private static final String EMPTY_OPERATION_ID_ERROR_MESSAGE = "00000000-0000-0000-0000-000000000000";

    static Stream<Arguments> getGenericTransferInvalidInputData() {
        return Stream.of(
                of("RecipientAddress",
                        Arrays.asList((Consumer<GenericTransferRequestModel>) x -> x.setAdditionalData(
                                FAKE_DATA_0X12345),
                                (Consumer<GenericTransferRequestModel>) x -> x.setAmount(SOME_AMOUNT),
                                (Consumer<GenericTransferRequestModel>) x -> x.setRecipientAddress(EMPTY),
                                (Consumer<GenericTransferRequestModel>) x -> x.setSenderCustomerId(getRandomUuid())),
                        (Consumer<ValidatableResponse>) (response -> response
                                .body(RECIPIENT_ADDRESS_FIELD_PATH,
                                        containsString(THE_RECIPIENT_ADDRESS_FIELD_IS_REQUIRED_ERROR_MESSAGE))),
                        SC_BAD_REQUEST),
                of("SenderCustomerId",
                        Arrays.asList(
                                (Consumer<GenericTransferRequestModel>) x -> x.setAdditionalData(FAKE_DATA_0X12345),
                                (Consumer<GenericTransferRequestModel>) x -> x.setAmount(SOME_AMOUNT),
                                (Consumer<GenericTransferRequestModel>) x -> x.setRecipientAddress(
                                        FAKE_ADDRESS_0X123456),
                                (Consumer<GenericTransferRequestModel>) x -> x.setSenderCustomerId(EMPTY)),
                        (Consumer<ValidatableResponse>) (response -> response
                                .body(SENDER_CUSTOMER_ID_FIELD_PATH,
                                        containsString(THE_SENDER_CUSTOMER_ID_FIELD_IS_REQUIRED_ERROR_MESSAGE))),
                        SC_BAD_REQUEST),
                of("Amount",
                        Arrays.asList(
                                (Consumer<GenericTransferRequestModel>) x -> x.setAdditionalData(FAKE_DATA_0X12345),
                                (Consumer<GenericTransferRequestModel>) x -> x.setAmount(ZERO_AMOUNT),
                                (Consumer<GenericTransferRequestModel>) x -> x
                                        .setRecipientAddress(FAKE_ADDRESS_0X123456),
                                (Consumer<GenericTransferRequestModel>) x -> x.setSenderCustomerId(getRandomUuid())),
                        (Consumer<ValidatableResponse>) (response -> response
                                .body(AMOUNT_FIELD_PATH, containsString(
                                        THE_FIELD_AMOUNT_MUST_BE_BETWEEN_1_AND_2147483647_ERROR_MESSAGE))),
                        SC_BAD_REQUEST),
                of("Amount, RecipientAddress, SenderCustomerId",
                        Arrays.asList((Consumer<GenericTransferRequestModel>) x -> x.setAmount(ZERO_AMOUNT)),
                        (Consumer<ValidatableResponse>) (response -> response
                                .body(AMOUNT_FIELD_PATH, containsString(
                                        THE_FIELD_AMOUNT_MUST_BE_BETWEEN_1_AND_2147483647_ERROR_MESSAGE))
                                .body(RECIPIENT_ADDRESS_FIELD_PATH, containsString(
                                        THE_RECIPIENT_ADDRESS_FIELD_IS_REQUIRED_ERROR_MESSAGE))
                                .body(SENDER_CUSTOMER_ID_FIELD_PATH,
                                        containsString(THE_SENDER_CUSTOMER_ID_FIELD_IS_REQUIRED_ERROR_MESSAGE))),
                        SC_BAD_REQUEST),
                of("RecipientAddress, SenderCustomerId",
                        Arrays.asList((Consumer<GenericTransferRequestModel>) x -> x.setAdditionalData(EMPTY),
                                (Consumer<GenericTransferRequestModel>) x -> x.setAmount(SOME_AMOUNT)),
                        (Consumer<ValidatableResponse>) (response -> response
                                .body(RECIPIENT_ADDRESS_FIELD_PATH,
                                        containsString(THE_RECIPIENT_ADDRESS_FIELD_IS_REQUIRED_ERROR_MESSAGE))
                                .body(SENDER_CUSTOMER_ID_FIELD_PATH,
                                        containsString(THE_SENDER_CUSTOMER_ID_FIELD_IS_REQUIRED_ERROR_MESSAGE))),
                        SC_BAD_REQUEST),
                of("Amount, RecipientAddress, SenderCustomerId",
                        Arrays.asList((Consumer<GenericTransferRequestModel>) x -> x.setRecipientAddress(EMPTY)),
                        (Consumer<ValidatableResponse>) (response -> response
                                .body(AMOUNT_FIELD_PATH, containsString(
                                        THE_FIELD_AMOUNT_MUST_BE_BETWEEN_1_AND_2147483647_ERROR_MESSAGE))
                                .body(RECIPIENT_ADDRESS_FIELD_PATH,
                                        containsString(THE_RECIPIENT_ADDRESS_FIELD_IS_REQUIRED_ERROR_MESSAGE))
                                .body(SENDER_CUSTOMER_ID_FIELD_PATH,
                                        containsString(THE_SENDER_CUSTOMER_ID_FIELD_IS_REQUIRED_ERROR_MESSAGE))),
                        SC_BAD_REQUEST),
                of("RecipientAddress, SenderCustomerId",
                        Arrays.asList(
                                (Consumer<GenericTransferRequestModel>) x -> x.setAdditionalData(FAKE_DATA_0X12345),
                                (Consumer<GenericTransferRequestModel>) x -> x.setAmount(SOME_AMOUNT),
                                (Consumer<GenericTransferRequestModel>) x -> x.setRecipientAddress(EMPTY)),
                        (Consumer<ValidatableResponse>) (response -> response
                                .body(RECIPIENT_ADDRESS_FIELD_PATH,
                                        containsString(THE_RECIPIENT_ADDRESS_FIELD_IS_REQUIRED_ERROR_MESSAGE))
                                .body(SENDER_CUSTOMER_ID_FIELD_PATH,
                                        containsString(THE_SENDER_CUSTOMER_ID_FIELD_IS_REQUIRED_ERROR_MESSAGE))),
                        SC_BAD_REQUEST),
                of("SenderWalletMissing",
                        Arrays.asList((Consumer<GenericTransferRequestModel>) x -> x.setAdditionalData(EMPTY),
                                (Consumer<GenericTransferRequestModel>) x -> x.setAmount(10),
                                (Consumer<GenericTransferRequestModel>) x -> x
                                        .setRecipientAddress(FAKE_ADDRESS_0X123456),
                                (Consumer<GenericTransferRequestModel>) x -> x.setSenderCustomerId(getRandomUuid())),
                        (Consumer<ValidatableResponse>) (response -> response
                                .body(ERROR_FIELD_PATH,
                                        containsString(SENDER_WALLET_MISSING.getCode()))
                                .body(OPERATION_ID_FIELD_PATH,
                                        containsString(EMPTY_OPERATION_ID_ERROR_MESSAGE))), SC_OK)
        );
    }

    @ParameterizedTest(name = "Run {index}: {0}")
    @MethodSource("getGenericTransferInvalidInputData")
    @UserStoryId(1759)
    void shouldNotSendTokensOnInvalidInput(String testDescription,
            List<Consumer<GenericTransferRequestModel>> requestModelData, Consumer<ValidatableResponse> assertAction,
            int errorCode) {
        val requestModel = getObjectWithData(GenericTransferRequestModel
                .builder()
                .build(), requestModelData);

        val actualResponse = postGenericTransfer(requestModel)
                .thenReturn();
        actualResponse
                .then()
                .assertThat()
                .statusCode(errorCode);

        assertAction.accept(actualResponse.then().assertThat());
    }
}
