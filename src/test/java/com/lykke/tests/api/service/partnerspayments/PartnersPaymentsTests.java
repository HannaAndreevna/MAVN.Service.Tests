package com.lykke.tests.api.service.partnerspayments;

import static com.lykke.api.testing.api.common.GenerateUtils.generateRandomEmail;
import static com.lykke.api.testing.api.common.GenerateUtils.generateRandomPhone;
import static com.lykke.api.testing.api.common.GenerateUtils.generateRandomString;
import static com.lykke.api.testing.api.common.GenerateUtils.getRandomUuid;
import static com.lykke.api.testing.api.common.PasswordGen.generateValidPassword;
import static com.lykke.tests.api.common.CommonConsts.Currency.AED_CURRENCY;
import static com.lykke.tests.api.common.CommonConsts.Currency.MVN_CURRENCY;
import static com.lykke.tests.api.common.CommonConsts.Currency.USD_CURRENCY;
import static com.lykke.api.testing.api.common.GenerateUtils.generateRandomEmail;
import static com.lykke.api.testing.api.common.GenerateUtils.generateRandomString;
import static com.lykke.api.testing.api.common.GenerateUtils.getRandomUuid;
import static com.lykke.tests.api.common.CommonConsts.SMOKE_TEST;
import static com.lykke.tests.api.common.CommonMethods.waitForExpiration;
import static com.lykke.tests.api.common.PaginationConts.CURRENT_PAGE_LOWER_BOUNDARY;
import static com.lykke.tests.api.common.PaginationConts.CURRENT_PAGE_UPPER_BOUNDARY;
import static com.lykke.tests.api.common.PaginationConts.INVALID_CURRENT_PAGE_LOWER_BOUNDARY;
import static com.lykke.tests.api.common.PaginationConts.INVALID_CURRENT_PAGE_UPPER_BOUNDARY;
import static com.lykke.tests.api.common.PaginationConts.INVALID_PAGE_SIZE_LOWER_BOUNDARY;
import static com.lykke.tests.api.common.PaginationConts.INVALID_PAGE_SIZE_UPPER_BOUNDARY;
import static com.lykke.tests.api.common.PaginationConts.PAGE_SIZE_LOWER_BOUNDARY;
import static com.lykke.tests.api.common.PaginationConts.PAGE_SIZE_UPPER_BOUNDARY;
import static com.lykke.tests.api.service.customer.LoginLogoutCustomerUtils.getUserToken;
import static com.lykke.tests.api.service.partnerapi.PartnerApiLogInLogOutTests.USER_INFO;
import static com.lykke.tests.api.service.partnerapi.PartnerApiUtils.getPartnerToken;
import static com.lykke.tests.api.service.partnermanagement.PartnerManagementUtils.createDefaultPartner;
import static com.lykke.tests.api.service.partnermanagement.PartnerManagementUtils.getLocationId;
import static com.lykke.tests.api.service.partnerspayments.PartnersPaymentsUtils.getCustomerFailed;
import static com.lykke.tests.api.service.partnerspayments.PartnersPaymentsUtils.getCustomerPending;
import static com.lykke.tests.api.service.partnerspayments.PartnersPaymentsUtils.getCustomerSucceeded;
import static com.lykke.tests.api.service.partnerspayments.PartnersPaymentsUtils.getPaymentByRequestId;
import static com.lykke.tests.api.service.partnerspayments.PartnersPaymentsUtils.postCustomerApproval;
import static com.lykke.tests.api.service.partnerspayments.PartnersPaymentsUtils.postCustomerRejection;
import static com.lykke.tests.api.service.partnerspayments.PartnersPaymentsUtils.postPartnerApproval;
import static com.lykke.tests.api.service.partnerspayments.PartnersPaymentsUtils.postPartnerCancellation;
import static com.lykke.tests.api.service.partnerspayments.PartnersPaymentsUtils.postPayment;
import static com.lykke.tests.api.service.partnerspayments.model.PaymentRequestErrorCode.CANNOT_PASS_BOTH_FIAT_AND_TOKENS_AMOUNT;
import static com.lykke.tests.api.service.partnerspayments.model.PaymentRequestErrorCode.EITHER_FIAT_OR_TOKENS_AMOUNT_SHOULD_BE_PASSED;
import static com.lykke.tests.api.service.partnerspayments.model.PaymentRequestErrorCode.INVALID_CURRENCY;
import static com.lykke.tests.api.service.partnerspayments.model.PaymentRequestErrorCode.INVALID_FIAT_AMOUNT;
import static com.lykke.tests.api.service.partnerspayments.model.PaymentRequestErrorCode.INVALID_TOKENS_AMOUNT;
import static com.lykke.tests.api.service.partnerspayments.model.PaymentStatusUpdateErrorCode.CUSTOMER_WALLET_IS_BLOCKED;
import static com.lykke.tests.api.service.partnerspayments.model.PaymentStatusUpdateErrorCode.INVALID_AMOUNT;
import static com.lykke.tests.api.service.partnerspayments.model.PaymentStatusUpdateErrorCode.NONE;
import static com.lykke.tests.api.service.partnerspayments.model.PaymentStatusUpdateErrorCode.PAYMENT_DOES_NOT_EXIST;
import static com.lykke.tests.api.service.partnerspayments.model.PaymentStatusUpdateErrorCode.PAYMENT_IS_IN_INVALID_STATUS;
import static com.lykke.tests.api.service.privateblockchainfacade.PrivateBlockchainFacadeUtils.createCustomerFundedViaBonusReward;
import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.apache.http.HttpStatus.SC_NO_CONTENT;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.params.provider.Arguments.of;

import com.lykke.api.testing.annotations.UserStoryId;
import com.lykke.tests.api.base.BaseApiTest;
import com.lykke.tests.api.service.partnermanagement.model.PartnerCreateResponse;
import com.lykke.tests.api.service.partnersintegration.PartnersIntegrationUtils;
import com.lykke.tests.api.service.partnersintegration.model.PaymentRequestStatusRequestModel;
import com.lykke.tests.api.service.partnersintegration.model.PaymentRequestStatusResponseModel;
import com.lykke.tests.api.service.partnerspayments.model.CustomerApprovePaymentRequest;
import com.lykke.tests.api.service.partnerspayments.model.CustomerRejectPaymentRequest;
import com.lykke.tests.api.service.partnerspayments.model.PaginatedPaymentRequestsResponse;
import com.lykke.tests.api.service.partnerspayments.model.PaginatedRequestForCustomer;
import com.lykke.tests.api.service.partnerspayments.model.PaymentDetailsResponseModel;
import com.lykke.tests.api.service.partnerspayments.model.PaymentRequestErrorCode;
import com.lykke.tests.api.service.partnerspayments.model.PaymentRequestModel;
import com.lykke.tests.api.service.partnerspayments.model.PaymentRequestResponseModel;
import com.lykke.tests.api.service.partnerspayments.model.PaymentRequestStatus;
import com.lykke.tests.api.service.partnerspayments.model.PaymentResponseModel;
import com.lykke.tests.api.service.partnerspayments.model.PaymentStatusUpdateErrorCode;
import com.lykke.tests.api.service.partnerspayments.model.PaymentStatusUpdateResponse;
import com.lykke.tests.api.service.partnerspayments.model.ReceptionistProcessPaymentRequest;
import com.lykke.tests.api.service.privateblockchainfacade.PrivateBlockchainFacadeUtils;
import io.restassured.response.ValidatableResponse;
import java.time.Instant;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Stream;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

public class PartnersPaymentsTests extends BaseApiTest {

    private static final String ERR_FIELD = "Error";
    private static final String PARTNER_ID_FIELD_PATH = "PartnerId[0]";
    private static final String CUSTOMER_ID_FIELD_PATH = "CustomerId[0]";
    private static final String PAYMENT_REQUEST_ID_FIELD_PATH = "PaymentRequestId[0]";
    private static final String CURRENCY_FIELD_PATH = "Currency[0]";
    private static final String PAGE_SIZE_FIELD_PATH = "PageSize[0]";
    private static final String CURRENT_PAGE_FIELD_PATH = "CurrentPage[0]";
    private static final String THE_PARTNER_ID_FIELD_IS_REQUIRED_ERROR_MESSAGE = "The PartnerId field is required.";
    private static final String THE_CUSTOMER_ID_FIELD_IS_REQUIRED_ERROR_MESSAGE = "The CustomerId field is required.";
    private static final String THE_PAYMENT_REQUEST_ID_FIELD_IS_REQUIRED_ERROR_MESSAGE = "The PaymentRequestId field is required.";
    private static final String THE_CURRENCY_FIELD_IS_REQUIRED_ERROR_MESSAGE = "The Currency field is required.";
    private static final String THE_FIELD_PAGE_SIZE_MUST_BE_BETWEEN_1_AND_500_ERROR_MESSAGE = "The field PageSize must be between 1 and 500.";
    private static final String THE_FIELD_CURRENT_PAGE_MUST_BE_BETWEEN_1_AND_10000_ERROR_MESSAGE = "The field CurrentPage must be between 1 and 10000.";
    private static final Double SOME_FIAT_AMOUNT = 10.0;
    private static final Double SOME_TOTAL_FIAT_AMOUNT = 20.0;
    private static final Double SOME_INITIAL_FIAT_AMOUNT = SOME_FIAT_AMOUNT * 5;
    private static final String SOME_PAYMENT_INFO = "my payment";
    private static final Double SOME_INITIAL_TOKENS_AMOUNT = 5.0;
    private static final Double SOME_TOKENS_AMOUNT = 5.0;
    private static final Double ZERO_FIAT_AMOUNT = 0.0;
    private static final Double ZERO_TOKENS_AMOUNT = 0.0;
    private static final int ZERO_TOTAL_COUNT = 0;
    private static final Double NEGATIVE_FIAT_AMOUNT = -1.0;
    private static final Double NEGATIVE_TOKENS_AMOUNT = -1.0;
    private static final int CURRENT_PAGE = 1;
    private static final int PAGE_SIZE = 500;
    private static final Double ANY_TOKENS_AMOUNT = 100.0;
    private static final Double ANY_FIAT_AMOUNT = 100.0;
    private static final Double ANY_TOTAL_BILL_AMOUNT = 100.0;
    private static final Double ANY_SENDING_AMOUNT = 1000.0;
    private static final Double SOME_SENDING_AMOUNT = 10.0;
    private static String partnerId;
    private static String partnerPassword;
    private static String partnerToken;
    private static String customerId;
    private static String customerPassword;
    private static String customerToken;
    private static String email;
    private static String phone;
    private static String locationId;
    private PartnerCreateResponse partnerData;

    static Stream<Arguments> getPostPaymentRequestMissingInputData() {
        return Stream.of(
                of(PaymentRequestModel
                                .builder()
                                .currency(MVN_CURRENCY)
                                .build(),
                        (Consumer<ValidatableResponse>) (response -> response.body(PARTNER_ID_FIELD_PATH,
                                containsString(THE_PARTNER_ID_FIELD_IS_REQUIRED_ERROR_MESSAGE))
                                .body(CUSTOMER_ID_FIELD_PATH,
                                        containsString(THE_CUSTOMER_ID_FIELD_IS_REQUIRED_ERROR_MESSAGE)))),
                of(PaymentRequestModel
                                .builder()
                                .currency(USD_CURRENCY)
                                .partnerId(getRandomUuid())
                                .customerId(customerId)
                                .fiatAmount(SOME_FIAT_AMOUNT)
                                .tokensAmount(ZERO_TOKENS_AMOUNT.toString())
                                .totalBillAmount(SOME_TOTAL_FIAT_AMOUNT)
                                .build(),
                        (Consumer<ValidatableResponse>) (response -> response.body(CUSTOMER_ID_FIELD_PATH,
                                containsString(THE_CUSTOMER_ID_FIELD_IS_REQUIRED_ERROR_MESSAGE)))),
                of(PaymentRequestModel
                                .builder()
                                .currency(USD_CURRENCY)
                                .customerId(customerId)
                                .fiatAmount(SOME_FIAT_AMOUNT)
                                .tokensAmount(ZERO_TOKENS_AMOUNT.toString())
                                .totalBillAmount(SOME_TOTAL_FIAT_AMOUNT)
                                .build(),
                        (Consumer<ValidatableResponse>) (response -> response.body(PARTNER_ID_FIELD_PATH,
                                containsString(THE_PARTNER_ID_FIELD_IS_REQUIRED_ERROR_MESSAGE))
                                .body(CUSTOMER_ID_FIELD_PATH,
                                        containsString(THE_CUSTOMER_ID_FIELD_IS_REQUIRED_ERROR_MESSAGE)))),
                of(PaymentRequestModel
                                .builder()
                                .partnerId(getRandomUuid())
                                .customerId(getRandomUuid())
                                .fiatAmount(SOME_FIAT_AMOUNT)
                                .tokensAmount(ZERO_TOKENS_AMOUNT.toString())
                                .totalBillAmount(SOME_TOTAL_FIAT_AMOUNT)
                                .build(),
                        (Consumer<ValidatableResponse>) (response -> response.body(CURRENCY_FIELD_PATH,
                                containsString(THE_CURRENCY_FIELD_IS_REQUIRED_ERROR_MESSAGE))))
        );
    }

    static Stream<Arguments> getPostPaymentRequestInvalidInputData() {
        return Stream.of(
                of(PaymentRequestModel
                        .builder()
                        .currency(MVN_CURRENCY)
                        .partnerId(getRandomUuid())
                        .customerId(getRandomUuid())
                        .build(), SC_OK, PaymentRequestResponseModel
                        .builder()
                        .error(INVALID_CURRENCY)
                        .build()),
                of(PaymentRequestModel
                        .builder()
                        .currency(AED_CURRENCY)
                        .partnerId(getRandomUuid())
                        .customerId(getRandomUuid())
                        .tokensAmount(NEGATIVE_TOKENS_AMOUNT.toString())
                        .build(), SC_OK, PaymentRequestResponseModel
                        .builder()
                        .error(INVALID_TOKENS_AMOUNT)
                        .build()),
                of(PaymentRequestModel
                        .builder()
                        .currency(AED_CURRENCY)
                        .partnerId(getRandomUuid())
                        .currency(getRandomUuid())
                        .build(), SC_BAD_REQUEST, PaymentRequestResponseModel
                        .builder()
                        .build()),
                of(PaymentRequestModel
                        .builder()
                        .currency(AED_CURRENCY)
                        .partnerId(getRandomUuid())
                        .customerId(getRandomUuid())
                        .tokensAmount(ZERO_TOKENS_AMOUNT.toString())
                        .totalBillAmount(SOME_TOTAL_FIAT_AMOUNT)
                        .build(), SC_OK, PaymentRequestResponseModel
                        .builder()
                        .error(EITHER_FIAT_OR_TOKENS_AMOUNT_SHOULD_BE_PASSED)
                        .build()),
                of(PaymentRequestModel
                        .builder()
                        .currency(AED_CURRENCY)
                        .partnerId(getRandomUuid())
                        .customerId(getRandomUuid())
                        .fiatAmount(ZERO_FIAT_AMOUNT)
                        .totalBillAmount(SOME_TOTAL_FIAT_AMOUNT)
                        .build(), SC_OK, PaymentRequestResponseModel
                        .builder()
                        .error(EITHER_FIAT_OR_TOKENS_AMOUNT_SHOULD_BE_PASSED)
                        .build()),
                of(PaymentRequestModel
                        .builder()
                        .currency(AED_CURRENCY)
                        .partnerId(getRandomUuid())
                        .customerId(getRandomUuid())
                        .fiatAmount(NEGATIVE_FIAT_AMOUNT)
                        .tokensAmount(ZERO_TOKENS_AMOUNT.toString())
                        .totalBillAmount(SOME_TOTAL_FIAT_AMOUNT)
                        .build(), SC_OK, PaymentRequestResponseModel
                        .builder()
                        .error(INVALID_FIAT_AMOUNT)
                        .build()),
                of(PaymentRequestModel
                        .builder()
                        .currency(AED_CURRENCY)
                        .partnerId(getRandomUuid())
                        .customerId(getRandomUuid())
                        .fiatAmount(NEGATIVE_FIAT_AMOUNT)
                        .tokensAmount(SOME_TOKENS_AMOUNT.toString())
                        .totalBillAmount(SOME_TOTAL_FIAT_AMOUNT)
                        .build(), SC_OK, PaymentRequestResponseModel
                        .builder()
                        .error(CANNOT_PASS_BOTH_FIAT_AND_TOKENS_AMOUNT)
                        .build()),
                of(PaymentRequestModel
                        .builder()
                        .currency(AED_CURRENCY)
                        .partnerId(getRandomUuid())
                        .customerId(getRandomUuid())
                        .fiatAmount(ZERO_FIAT_AMOUNT)
                        .tokensAmount(NEGATIVE_TOKENS_AMOUNT.toString())
                        .totalBillAmount(SOME_TOTAL_FIAT_AMOUNT)
                        .build(), SC_OK, PaymentRequestResponseModel
                        .builder()
                        .error(INVALID_TOKENS_AMOUNT)
                        .build()),
                of(PaymentRequestModel
                        .builder()
                        .currency(AED_CURRENCY)
                        .partnerId(getRandomUuid())
                        .customerId(getRandomUuid())
                        .fiatAmount(SOME_FIAT_AMOUNT)
                        .tokensAmount(NEGATIVE_TOKENS_AMOUNT.toString())
                        .totalBillAmount(SOME_TOTAL_FIAT_AMOUNT)
                        .build(), SC_OK, PaymentRequestResponseModel
                        .builder()
                        .error(CANNOT_PASS_BOTH_FIAT_AND_TOKENS_AMOUNT)
                        .build())
        );
    }

    static Stream<Arguments> getCustomerApprovalMissingInputData() {
        return Stream.of(
                of(CustomerApprovePaymentRequest
                        .builder()
                        .customerId(getRandomUuid())
                        .build(), (Consumer<ValidatableResponse>) (response -> response.body(
                        PAYMENT_REQUEST_ID_FIELD_PATH,
                        containsString(THE_PAYMENT_REQUEST_ID_FIELD_IS_REQUIRED_ERROR_MESSAGE)))
                ));
    }

    static Stream<Arguments> getCustomerApprovalInvalidInputData() {
        return Stream.of(
                of(CustomerApprovePaymentRequest
                        .builder()
                        .customerId(getRandomUuid())
                        .paymentRequestId(getRandomUuid())
                        .build(), PaymentStatusUpdateResponse.builder().error(INVALID_AMOUNT).build()
                ),
                of(CustomerApprovePaymentRequest
                        .builder()
                        .sendingAmount(SOME_SENDING_AMOUNT.toString())
                        .paymentRequestId(getRandomUuid())
                        .customerId(getRandomUuid())
                        .build(), PaymentStatusUpdateResponse.builder().error(CUSTOMER_WALLET_IS_BLOCKED).build()));
    }

    static Stream<Arguments> getCustomerRejectionMissingInputData() {
        return Stream.of(
                of(CustomerRejectPaymentRequest
                        .builder()
                        .customerId(getRandomUuid())
                        .build(), (Consumer<ValidatableResponse>) (response -> response.body(
                        PAYMENT_REQUEST_ID_FIELD_PATH,
                        containsString(THE_PAYMENT_REQUEST_ID_FIELD_IS_REQUIRED_ERROR_MESSAGE)))
                ));
    }

    static Stream<Arguments> getCustomerRejectionInvalidInputData() {
        return Stream.of(
                of(CustomerRejectPaymentRequest
                                .builder()
                                .customerId(getRandomUuid())
                                .paymentRequestId(getRandomUuid())
                                .build(), SC_OK,
                        PaymentStatusUpdateResponse.builder().error(PAYMENT_DOES_NOT_EXIST).build()
                ));
    }

    static Stream<Arguments> getPartnerApprovalMissingInputData() {
        return Stream.of(
                // no payment request id
                of(ReceptionistProcessPaymentRequest
                        .builder()
                        .build(), (Consumer<ValidatableResponse>) (response -> response.body(
                        PAYMENT_REQUEST_ID_FIELD_PATH,
                        containsString(THE_PAYMENT_REQUEST_ID_FIELD_IS_REQUIRED_ERROR_MESSAGE)))
                ));
    }

    static Stream<Arguments> getPartnerApprovalInvalidInputData() {
        return Stream.of(
                of(ReceptionistProcessPaymentRequest
                        .builder()
                        .paymentRequestId(getRandomUuid())
                        .build(), PaymentStatusUpdateResponse.builder().error(PAYMENT_DOES_NOT_EXIST).build()
                ),
                of(ReceptionistProcessPaymentRequest
                        .builder()
                        .paymentRequestId(getRandomUuid())
                        .build(), PaymentStatusUpdateResponse.builder().error(PAYMENT_DOES_NOT_EXIST).build()));
    }

    static Stream<Arguments> getPartnerRejectionMissingInputData() {
        return Stream.of(
                of(ReceptionistProcessPaymentRequest
                        .builder()
                        .build(), (Consumer<ValidatableResponse>) (response -> response.body(
                        PAYMENT_REQUEST_ID_FIELD_PATH,
                        containsString(THE_PAYMENT_REQUEST_ID_FIELD_IS_REQUIRED_ERROR_MESSAGE)))
                ));
    }

    static Stream<Arguments> getPartnerRejectionInvalidInputData() {
        return Stream.of(
                of(ReceptionistProcessPaymentRequest
                        .builder()
                        .paymentRequestId(getRandomUuid())
                        .build(), PaymentStatusUpdateResponse.builder().error(PAYMENT_DOES_NOT_EXIST).build()
                ),
                of(ReceptionistProcessPaymentRequest
                        .builder()
                        .paymentRequestId(getRandomUuid())
                        .build(), PaymentStatusUpdateResponse.builder().error(PAYMENT_DOES_NOT_EXIST).build()));
    }

    static Stream<Arguments> getInvalidPaginationData() {
        return Stream.of(
                of(INVALID_CURRENT_PAGE_LOWER_BOUNDARY, PAGE_SIZE_LOWER_BOUNDARY,
                        (Consumer<ValidatableResponse>) (response -> response.body(
                                CURRENT_PAGE_FIELD_PATH,
                                containsString(THE_FIELD_CURRENT_PAGE_MUST_BE_BETWEEN_1_AND_10000_ERROR_MESSAGE)))),
                of(INVALID_CURRENT_PAGE_UPPER_BOUNDARY, PAGE_SIZE_UPPER_BOUNDARY,
                        (Consumer<ValidatableResponse>) (response -> response.body(
                                CURRENT_PAGE_FIELD_PATH,
                                containsString(
                                        THE_FIELD_CURRENT_PAGE_MUST_BE_BETWEEN_1_AND_10000_ERROR_MESSAGE)))),
                of(CURRENT_PAGE_LOWER_BOUNDARY, INVALID_PAGE_SIZE_LOWER_BOUNDARY,
                        (Consumer<ValidatableResponse>) (response -> response.body(
                                PAGE_SIZE_FIELD_PATH,
                                containsString(
                                        THE_FIELD_PAGE_SIZE_MUST_BE_BETWEEN_1_AND_500_ERROR_MESSAGE)))),
                of(CURRENT_PAGE_UPPER_BOUNDARY, INVALID_PAGE_SIZE_UPPER_BOUNDARY,
                        (Consumer<ValidatableResponse>) (response -> response.body(
                                PAGE_SIZE_FIELD_PATH,
                                containsString(
                                        THE_FIELD_PAGE_SIZE_MUST_BE_BETWEEN_1_AND_500_ERROR_MESSAGE)))));
    }

    @BeforeEach
    void setUp() {
        partnerPassword = generateValidPassword();
        email = generateRandomEmail();
        phone = generateRandomPhone(15);
        customerPassword = generateValidPassword();

        partnerId = getRandomUuid();
        partnerData = createDefaultPartner(partnerId, partnerPassword, generateRandomString(10),
                generateRandomString(10));
        locationId = getLocationId(partnerData);
        partnerToken = getPartnerToken(partnerId, partnerPassword, USER_INFO);
    }

    @Test
    @UserStoryId(2462)
    void shouldNotApprovePayment_SendingAmountGreaterThanTokenAmount() {
        val customerData = createCustomerFundedViaBonusReward(Double.valueOf(SOME_INITIAL_FIAT_AMOUNT));
        customerToken = getUserToken(customerData.getEmail(), customerData.getPassword());

        val actualResult = postPayment(PaymentRequestModel
                .builder()
                .currency(AED_CURRENCY)
                .customerId(customerData.getCustomerId())
                .partnerId(partnerData.getId())
                .tokensAmount(ANY_TOKENS_AMOUNT.toString())
                .locationId(locationId)
                .partnerMessageId(SOME_PAYMENT_INFO)
                .totalBillAmount(ANY_TOTAL_BILL_AMOUNT)
                .build(), partnerToken)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(PaymentRequestResponseModel.class);

        val approveRequestModel = CustomerApprovePaymentRequest
                .builder()
                .sendingAmount(Double.valueOf(customerData.getExtraAmount() + ANY_SENDING_AMOUNT).toString())
                .paymentRequestId(customerData.getCustomerId())
                .customerId(actualResult.getPaymentRequestId())
                .build();

        postCustomerApproval(approveRequestModel, partnerToken)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .body(ERR_FIELD, equalTo(CUSTOMER_WALLET_IS_BLOCKED.getCode()));
    }

    @Test
    @UserStoryId(2462)
    void shouldNotApprovePayment_SendingAmountGreaterThanFiatAmount() {
        val customerData = createCustomerFundedViaBonusReward(Double.valueOf(SOME_INITIAL_FIAT_AMOUNT));
        customerToken = getUserToken(customerData.getEmail(), customerData.getPassword());

        val actualResult = postPayment(PaymentRequestModel
                .builder()
                .currency(AED_CURRENCY)
                .customerId(customerData.getCustomerId())
                .partnerId(partnerData.getId())
                .fiatAmount(ANY_FIAT_AMOUNT)
                .locationId(locationId)
                .partnerMessageId(SOME_PAYMENT_INFO)
                .totalBillAmount(ANY_TOTAL_BILL_AMOUNT)
                .build(), partnerToken)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(PaymentRequestResponseModel.class);

        val approveRequestModel = CustomerApprovePaymentRequest
                .builder()
                .sendingAmount(Double.valueOf(ANY_FIAT_AMOUNT + ANY_SENDING_AMOUNT).toString())
                .paymentRequestId(customerData.getCustomerId())
                .customerId(actualResult.getPaymentRequestId())
                .build();

        postCustomerApproval(approveRequestModel, partnerToken)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .body(ERR_FIELD, equalTo(CUSTOMER_WALLET_IS_BLOCKED.getCode()));
    }

    @ParameterizedTest
    @ValueSource(strings = {AED_CURRENCY, USD_CURRENCY, MVN_CURRENCY})
    @UserStoryId(storyId = {2327, 3249})
    void shouldPostPayment(String currency) {
        val customerData = createCustomerFundedViaBonusReward(Double.valueOf(SOME_INITIAL_FIAT_AMOUNT));
        customerToken = getUserToken(customerData.getEmail(), customerData.getPassword());

        val actualResult = postPayment(PaymentRequestModel
                .builder()
                .currency(currency)
                .customerId(customerData.getCustomerId())
                .partnerId(partnerData.getId())
                .tokensAmount(SOME_TOKENS_AMOUNT.toString())
                .locationId(locationId)
                .partnerMessageId(SOME_PAYMENT_INFO)
                .totalBillAmount(SOME_TOTAL_FIAT_AMOUNT)
                .build(), partnerToken)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(PaymentRequestResponseModel.class);

        val expectedResult = PaymentRequestResponseModel
                .builder()
                .paymentRequestId(actualResult.getPaymentRequestId())
                .status(PaymentRequestStatus.CREATED)
                .error(PaymentRequestErrorCode.NONE)
                .build();

        assertEquals(expectedResult, actualResult);
    }

    @ParameterizedTest
    @MethodSource("getPostPaymentRequestMissingInputData")
    @UserStoryId(storyId = {2327, 2689})
    void shouldNotPostPaymentOnInvalidInput(PaymentRequestModel requestModel,
            Consumer<ValidatableResponse> assertAction) {
        val actualResponse = postPayment(requestModel, partnerToken)
                .thenReturn();

        assertAction.accept(actualResponse.then().assertThat());
    }

    @ParameterizedTest
    @MethodSource("getPostPaymentRequestInvalidInputData")
    @UserStoryId(storyId = {2327, 2689})
    void shouldNotSucceedWithPostPaymentOnInvalidInput(PaymentRequestModel requestModel, int status,
            PaymentRequestResponseModel expectedResult) {
        val actualResult = postPayment(requestModel, partnerToken)
                .then()
                .assertThat()
                .statusCode(status)
                .extract()
                .as(PaymentRequestResponseModel.class);

        assertEquals(expectedResult, actualResult);
    }

    @Test
    @Tag(SMOKE_TEST)
    @UserStoryId(2327)
    void shouldGetCustomerPending() {
        val customerData = createCustomerFundedViaBonusReward(Double.valueOf(SOME_INITIAL_FIAT_AMOUNT));
        customerToken = getUserToken(customerData.getEmail(), customerData.getPassword());

        val expectedResult = new PaymentResponseModel[]{};

        val actualResult = getCustomerPending(
                PaginatedRequestForCustomer
                        .customerRequestBuilder()
                        .currentPage(CURRENT_PAGE_LOWER_BOUNDARY)
                        .pageSize(PAGE_SIZE_UPPER_BOUNDARY)
                        .customerId(customerData.getCustomerId())
                        .build(), partnerToken)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(PaginatedPaymentRequestsResponse.class)
                .getPaymentRequests();

        assertArrayEquals(expectedResult, actualResult);
    }

    @Test
    @Tag(SMOKE_TEST)
    @UserStoryId(storyId = {3438, 3514})
    void shouldGetCustomerPendingWithTotal() {
        val customerData = createCustomerFundedViaBonusReward(Double.valueOf(SOME_INITIAL_FIAT_AMOUNT));
        customerToken = getUserToken(customerData.getEmail(), customerData.getPassword());

        postPayment(PaymentRequestModel
                .builder()
                .currency(AED_CURRENCY)
                .customerId(customerData.getCustomerId())
                .partnerId(partnerData.getId())
                .tokensAmount(SOME_TOKENS_AMOUNT.toString())
                .locationId(locationId)
                .partnerMessageId(SOME_PAYMENT_INFO)
                .totalBillAmount(SOME_TOTAL_FIAT_AMOUNT)
                .build(), partnerToken)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(PaymentRequestResponseModel.class);

        val actualResult = getCustomerPending(PaginatedRequestForCustomer
                .customerRequestBuilder()
                .currentPage(CURRENT_PAGE_LOWER_BOUNDARY)
                .pageSize(PAGE_SIZE_UPPER_BOUNDARY)
                .customerId(customerData.getCustomerId())
                .build(), partnerToken)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(PaginatedPaymentRequestsResponse.class)
                .getPaymentRequests();

        assertEquals(SOME_TOTAL_FIAT_AMOUNT, actualResult[0].getTotalBillAmount());
    }

    @Test
    @Tag(SMOKE_TEST)
    @UserStoryId(storyId = {2518, 3514})
    void shouldGetResponseWithLastUpdatedTimestamp() {
        val customerData = createCustomerFundedViaBonusReward(Double.valueOf(SOME_INITIAL_FIAT_AMOUNT));
        customerToken = getUserToken(customerData.getEmail(), customerData.getPassword());

        postPayment(PaymentRequestModel
                .builder()
                .currency(AED_CURRENCY)
                .customerId(customerData.getCustomerId())
                .partnerId(partnerData.getId())
                .tokensAmount(SOME_TOKENS_AMOUNT.toString())
                .locationId(locationId)
                .partnerMessageId(SOME_PAYMENT_INFO)
                .totalBillAmount(SOME_TOTAL_FIAT_AMOUNT)
                .build(), partnerToken)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(PaymentRequestResponseModel.class);

        val actualResult = getCustomerPending(
                PaginatedRequestForCustomer
                        .customerRequestBuilder()
                        .currentPage(CURRENT_PAGE_LOWER_BOUNDARY)
                        .pageSize(PAGE_SIZE_UPPER_BOUNDARY)
                        .customerId(customerData.getCustomerId())
                        .build(), partnerToken)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(PaginatedPaymentRequestsResponse.class)
                .getPaymentRequests();

        assertTrue(actualResult[0].getLastUpdatedDate()
                .toInstant()
                .isAfter(Instant.now().minusSeconds(TimeUnit.MINUTES.toSeconds(1))));
    }


    @Test
    @Tag(SMOKE_TEST)
    @UserStoryId(2327)
    void shouldGetCustomerSucceeded() {
        val customerData = createCustomerFundedViaBonusReward(Double.valueOf(SOME_INITIAL_FIAT_AMOUNT));
        customerToken = getUserToken(customerData.getEmail(), customerData.getPassword());

        val expectedResult = PaginatedPaymentRequestsResponse
                .builder()
                .currentPage(CURRENT_PAGE)
                .pageSize(PAGE_SIZE)
                .totalCount(ZERO_TOTAL_COUNT)
                .paymentRequests(new PaymentResponseModel[]{})
                .build();

        val actualResult = getCustomerSucceeded(PaginatedRequestForCustomer
                .customerRequestBuilder()
                .currentPage(CURRENT_PAGE)
                .pageSize(PAGE_SIZE)
                .customerId(customerData.getCustomerId())
                .build(), partnerToken)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(PaginatedPaymentRequestsResponse.class);

        assertEquals(expectedResult, actualResult);
    }

    @Test
    @Tag(SMOKE_TEST)
    @UserStoryId(2327)
    void shouldGetCustomerFailed() {
        val customerData = createCustomerFundedViaBonusReward(Double.valueOf(SOME_INITIAL_FIAT_AMOUNT));
        customerToken = getUserToken(customerData.getEmail(), customerData.getPassword());

        val expectedResult = PaginatedPaymentRequestsResponse
                .builder()
                .currentPage(CURRENT_PAGE)
                .pageSize(PAGE_SIZE)
                .totalCount(ZERO_TOTAL_COUNT)
                .paymentRequests(new PaymentResponseModel[]{})
                .build();

        val actualResult = getCustomerFailed(PaginatedRequestForCustomer
                .customerRequestBuilder()
                .currentPage(CURRENT_PAGE)
                .pageSize(PAGE_SIZE)
                .customerId(customerData.getCustomerId())
                .build(), partnerToken)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(PaginatedPaymentRequestsResponse.class);

        assertEquals(expectedResult, actualResult);
    }

    @Test
    @UserStoryId(2327)
    void shouldPostCustomerApproval() {
        val requestModel = CustomerApprovePaymentRequest
                .builder()
                .sendingAmount(SOME_SENDING_AMOUNT.toString())
                .paymentRequestId(getRandomUuid())
                .customerId(getRandomUuid())
                .build();

        val expectedResult = PaymentStatusUpdateResponse
                .builder()
                .error(CUSTOMER_WALLET_IS_BLOCKED)
                .build();

        val actualResult = postCustomerApproval(requestModel, partnerToken)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(PaymentStatusUpdateResponse.class);

        assertEquals(expectedResult, actualResult);
    }

    @Test
    @UserStoryId(2327)
    void shouldPostCustomerRejection() {
        val customerData = createCustomerFundedViaBonusReward(Double.valueOf(SOME_INITIAL_FIAT_AMOUNT));
        customerToken = getUserToken(customerData.getEmail(), customerData.getPassword());

        val actualResult = postPayment(PaymentRequestModel
                .builder()
                .currency(AED_CURRENCY)
                .customerId(customerData.getCustomerId())
                .partnerId(partnerData.getId())
                .tokensAmount(SOME_TOKENS_AMOUNT.toString())
                .locationId(locationId)
                .partnerMessageId(SOME_PAYMENT_INFO)
                .totalBillAmount(SOME_TOTAL_FIAT_AMOUNT)
                .build(), partnerToken)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(PaymentRequestResponseModel.class);

        val expectedResult = PaymentRequestResponseModel
                .builder()
                .paymentRequestId(actualResult.getPaymentRequestId())
                .status(PaymentRequestStatus.CREATED)
                .error(PaymentRequestErrorCode.NONE)
                .build();

        assertEquals(expectedResult, actualResult);
    }

    @Test
    @UserStoryId(2327)
    void shouldPostPartnerApproval() {
        val requestModel = ReceptionistProcessPaymentRequest
                .builder()
                .paymentRequestId(getRandomUuid())
                .build();

        val expectedResult = PaymentStatusUpdateResponse
                .builder()
                .error(PAYMENT_DOES_NOT_EXIST)
                .build();

        val actualResult = postPartnerApproval(requestModel, partnerToken)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(PaymentStatusUpdateResponse.class);

        assertEquals(expectedResult, actualResult);
    }

    @Test
    @UserStoryId(2327)
    void shouldPostPartnerRejection() {
        val requestModel = ReceptionistProcessPaymentRequest
                .builder()
                .paymentRequestId(getRandomUuid())
                .build();

        val expectedResult = PaymentStatusUpdateResponse
                .builder()
                .error(PAYMENT_DOES_NOT_EXIST)
                .build();

        val actualResult = postPartnerCancellation(requestModel, partnerToken)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(PaymentStatusUpdateResponse.class);

        assertEquals(expectedResult, actualResult);
    }

    @Test
    @Tag(SMOKE_TEST)
    @UserStoryId(storyId = {2327, 3324})
    void shouldGetPaymentByRequestId() {
        val customerData = createCustomerFundedViaBonusReward(Double.valueOf(SOME_INITIAL_FIAT_AMOUNT));
        customerToken = getUserToken(customerData.getEmail(), customerData.getPassword());

        val requestModel = PaymentRequestModel
                .builder()
                .currency(AED_CURRENCY)
                .partnerId(partnerData.getId())
                .customerId(customerData.getCustomerId())
                .fiatAmount(SOME_FIAT_AMOUNT)
                .tokensAmount(ZERO_TOKENS_AMOUNT.toString())
                .totalBillAmount(SOME_TOTAL_FIAT_AMOUNT)
                .build();

        val paymentRequestData = postPayment(requestModel, partnerToken)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(PaymentRequestResponseModel.class);

        val expectedResult = PaymentRequestResponseModel
                .builder()
                .paymentRequestId(paymentRequestData.getPaymentRequestId())
                .status(PaymentRequestStatus.CREATED)
                .build();

        val actualResult = getPaymentByRequestId(paymentRequestData.getPaymentRequestId(), partnerToken)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(PaymentRequestResponseModel.class);

        assertEquals(expectedResult, actualResult);
    }

    @Test
    @UserStoryId(storyId = {2327, 3324})
    void shouldNotGetPaymentByNonExistingRequestId() {
        getPaymentByRequestId(getRandomUuid(), partnerToken)
                .then()
                .assertThat()
                .statusCode(SC_NO_CONTENT);
        // should be 404?
    }

    @Test
    @UserStoryId(2327)
    void shouldNotGetCustomerPengingOnMissingInput() {
        getCustomerPending(
                PaginatedRequestForCustomer
                        .customerRequestBuilder()
                        .currentPage(CURRENT_PAGE_LOWER_BOUNDARY)
                        .pageSize(PAGE_SIZE_UPPER_BOUNDARY)
                        .build(), partnerToken)
                .then()
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .body(CUSTOMER_ID_FIELD_PATH, containsString(THE_CUSTOMER_ID_FIELD_IS_REQUIRED_ERROR_MESSAGE));
    }

    @ParameterizedTest
    @MethodSource("getInvalidPaginationData")
    void shouldNotGetCustomerFailedOnInvalidData(int currentPage, int pageSize,
            Consumer<ValidatableResponse> assertAction) {
        val actualResponse = getCustomerFailed(PaginatedRequestForCustomer
                .customerRequestBuilder()
                .customerId(getRandomUuid())
                .currentPage(currentPage)
                .pageSize(pageSize)
                .build(), partnerToken)
                .thenReturn();

        actualResponse
                .then()
                .assertThat()
                .statusCode(SC_BAD_REQUEST);

        assertAction.accept(actualResponse.then().assertThat());
    }

    @ParameterizedTest
    @MethodSource("getInvalidPaginationData")
    void shouldNotGetCustomerSucceededOnInvalidData(int currentPage, int pageSize,
            Consumer<ValidatableResponse> assertAction) {
        val actualResponse = getCustomerSucceeded(PaginatedRequestForCustomer
                .customerRequestBuilder()
                .currentPage(currentPage)
                .pageSize(pageSize)
                .build(), partnerToken)
                .thenReturn();

        actualResponse
                .then()
                .assertThat()
                .statusCode(SC_BAD_REQUEST);

        assertAction.accept(actualResponse.then().assertThat());
    }

    @ParameterizedTest
    @MethodSource("getCustomerApprovalMissingInputData")
    @UserStoryId(2327)
    void shouldNotPostCustomerApprovalOnMissingInput(CustomerApprovePaymentRequest requestModel,
            Consumer<ValidatableResponse> assertAction) {
        val actualResponse = postCustomerApproval(requestModel, partnerToken);

        assertAction.accept(actualResponse.then().assertThat());
    }

    @ParameterizedTest
    @MethodSource("getCustomerApprovalInvalidInputData")
    @UserStoryId(2327)
    void shouldNotPostCustomerApprovalOnInvalidInput(CustomerApprovePaymentRequest requestModel,
            PaymentStatusUpdateResponse expectedResult) {
        val actualResult = postCustomerApproval(requestModel, partnerToken)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(PaymentStatusUpdateResponse.class);

        assertEquals(expectedResult, actualResult);
    }

    @ParameterizedTest
    @MethodSource("getCustomerRejectionMissingInputData")
    @UserStoryId(2327)
    void shouldNotPostCustomerRejectionOnMissingInput(CustomerRejectPaymentRequest requestModel,
            Consumer<ValidatableResponse> assertAction) {
        val actualResponse = postCustomerRejection(requestModel, partnerToken);

        assertAction.accept(actualResponse.then().assertThat());
    }

    @ParameterizedTest
    @MethodSource("getCustomerRejectionInvalidInputData")
    @UserStoryId(2327)
    void shouldNotPostCustomerRejectionOnInvalidInput(CustomerRejectPaymentRequest requestModel, int status,
            PaymentStatusUpdateResponse expectedResult) {
        val actualResult = postCustomerRejection(requestModel, partnerToken)
                .then()
                .assertThat()
                .statusCode(status)
                .extract()
                .as(PaymentStatusUpdateResponse.class);

        assertEquals(expectedResult, actualResult);
    }

    @ParameterizedTest
    @MethodSource("getPartnerApprovalMissingInputData")
    @UserStoryId(2327)
    void shouldNotPostPartnerApprovalOnMissingInput(ReceptionistProcessPaymentRequest requestModel,
            Consumer<ValidatableResponse> assertAction) {
        val actualResponse = postPartnerApproval(requestModel, partnerToken);

        assertAction.accept(actualResponse.then().assertThat());
    }


    @ParameterizedTest
    @MethodSource("getPartnerApprovalInvalidInputData")
    @UserStoryId(2327)
    void shouldNotPostPartnerApprovalOnInvalidInput(ReceptionistProcessPaymentRequest requestModel,
            PaymentStatusUpdateResponse expectedResult) {
        val actualResult = postPartnerApproval(requestModel, partnerToken)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(PaymentStatusUpdateResponse.class);

        assertEquals(expectedResult, actualResult);
    }

    @ParameterizedTest
    @MethodSource("getPartnerRejectionMissingInputData")
    @UserStoryId(2327)
    void shouldNotPostPartnerRejectionOnMissingInput(ReceptionistProcessPaymentRequest requestModel,
            Consumer<ValidatableResponse> assertAction) {
        val actualResponse = postPartnerCancellation(requestModel, partnerToken);

        assertAction.accept(actualResponse.then().assertThat());
    }

    @ParameterizedTest
    @MethodSource("getPartnerRejectionInvalidInputData")
    @UserStoryId(2327)
    void shouldNotPostPartnerRejectionOnInvalidInput(ReceptionistProcessPaymentRequest requestModel,
            PaymentStatusUpdateResponse expectedResult) {
        val actualResult = postPartnerCancellation(requestModel, partnerToken)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(PaymentStatusUpdateResponse.class);

        assertEquals(expectedResult, actualResult);
    }

    // FAL-3325
    /*
    TotalBillAmount -> TotalFiatAmount

    If TokensSendingAmount is null
        TokensAmount -> TokensAmount
        FiatAmount -> FiatAmount
    else
        TokensSendingAmount -> TokensAmount
        FiatSendingAmount -> FiatAmount
    */

    @Disabled("TODO: this scenario is not support by customer approval endpoint: "
            + "sendingAmount == 0.0 leads to InvalidAmount"
            + "sendingAmount == null leads to 400 (The input was not valid.)")
    @Test
    @UserStoryId(storyId = {2327, 3324, 3325})
    void shouldFlowApprovedPaymentIfTokensSendingAmountIsNull() {
        val customerPercentOfTotalPayment = 0.5;
        val customerData = createCustomerFundedViaBonusReward(Double.valueOf(SOME_INITIAL_FIAT_AMOUNT));
        customerToken = getUserToken(customerData.getEmail(), customerData.getPassword());

        locationId = getLocationId(partnerData);

        val paymentRequestData = postPayment(PaymentRequestModel
                .builder()
                .currency(AED_CURRENCY)
                .customerId(customerData.getCustomerId())
                .partnerId(partnerData.getId())
                .tokensAmount(SOME_TOKENS_AMOUNT.toString())
                .locationId(locationId)
                .partnerMessageId(SOME_PAYMENT_INFO)
                .totalBillAmount(SOME_TOTAL_FIAT_AMOUNT)
                .build(), partnerToken)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(PaymentRequestResponseModel.class);

        val pendingPaymentsCollection = getCustomerPending(
                PaginatedRequestForCustomer
                        .customerRequestBuilder()
                        .currentPage(CURRENT_PAGE_LOWER_BOUNDARY)
                        .pageSize(PAGE_SIZE_UPPER_BOUNDARY)
                        .customerId(customerData.getCustomerId())
                        .build(), partnerToken)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(PaymentResponseModel[].class);

        val customerApprovalActualResult = postCustomerApproval(CustomerApprovePaymentRequest
                .builder()
                .customerId(customerData.getCustomerId())
                .paymentRequestId(paymentRequestData.getPaymentRequestId())
                // TODO: .sendingAmount("0.0")
                .build(), partnerToken)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(PaymentStatusUpdateResponse.class);

        assertEquals(PaymentStatusUpdateResponse
                .builder()
                .error(PaymentStatusUpdateErrorCode.NONE)
                .build(), customerApprovalActualResult);

        val partnerApprovalActualResult = postPartnerApproval(ReceptionistProcessPaymentRequest
                .builder()
                .paymentRequestId(paymentRequestData.getPaymentRequestId())
                .build(), partnerToken)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(PaymentStatusUpdateResponse.class);

        assertEquals(PaymentStatusUpdateResponse
                .builder()
                .error(PAYMENT_IS_IN_INVALID_STATUS)
                .build(), partnerApprovalActualResult);

        val actualResultPartnersPayments = getPaymentByRequestId(paymentRequestData.getPaymentRequestId(), partnerToken)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(PaymentDetailsResponseModel.class);

        assertEquals(actualResultPartnersPayments.getFiatAmount() * customerPercentOfTotalPayment,
                actualResultPartnersPayments.getFiatSendingAmount());

        val actualResultPartnersIntegration = PartnersIntegrationUtils.getPaymentRequest(
                PaymentRequestStatusRequestModel
                        .builder()
                        .partnerId(partnerData.getId())
                        .paymentRequestId(paymentRequestData.getPaymentRequestId())
                        .build(), partnerToken)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(PaymentRequestStatusResponseModel.class);

        assertAll(
                () -> assertEquals(actualResultPartnersPayments.getTotalBillAmount(),
                        actualResultPartnersIntegration.getTotalFiatAmount()),
                () -> assertEquals(actualResultPartnersPayments.getTokensAmount(),
                        actualResultPartnersIntegration.getTokensAmount()),
                () -> assertEquals(actualResultPartnersPayments.getFiatAmount(),
                        actualResultPartnersIntegration.getFiatAmount())
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {AED_CURRENCY, USD_CURRENCY, MVN_CURRENCY})
    @UserStoryId(storyId = {2327, 3324, 3325})
    void shouldFlowApprovedPaymentIfTokensSendingAmountIsNotNull(String currency) {
        val customerPercentOfTotalPayment = 0.5;
        val customerData = createCustomerFundedViaBonusReward(Double.valueOf(SOME_INITIAL_FIAT_AMOUNT));
        customerToken = getUserToken(customerData.getEmail(), customerData.getPassword());

        locationId = getLocationId(partnerData);

        val paymentRequestData = postPayment(PaymentRequestModel
                .builder()
                .currency(currency)
                .customerId(customerData.getCustomerId())
                .partnerId(partnerData.getId())
                .tokensAmount(SOME_TOKENS_AMOUNT.toString())
                .locationId(locationId)
                .partnerMessageId(SOME_PAYMENT_INFO)
                .totalBillAmount(SOME_TOTAL_FIAT_AMOUNT)
                .build(), partnerToken)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(PaymentRequestResponseModel.class);

        val pendingPaymentsCollection = getCustomerPending(PaginatedRequestForCustomer
                .customerRequestBuilder()
                .currentPage(CURRENT_PAGE_LOWER_BOUNDARY)
                .pageSize(PAGE_SIZE_UPPER_BOUNDARY)
                .customerId(customerData.getCustomerId())
                .build(), partnerToken)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(PaymentResponseModel[].class);

        val customerApprovalActualResult = postCustomerApproval(CustomerApprovePaymentRequest
                .builder()
                .customerId(customerData.getCustomerId())
                .paymentRequestId(paymentRequestData.getPaymentRequestId())
                .sendingAmount(Double.valueOf(SOME_TOKENS_AMOUNT * customerPercentOfTotalPayment).toString())
                .build(), partnerToken)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(PaymentStatusUpdateResponse.class);

        assertEquals(PaymentStatusUpdateResponse
                .builder()
                .error(PaymentStatusUpdateErrorCode.NONE)
                .build(), customerApprovalActualResult);

        val partnerApprovalActualResult = postPartnerApproval(ReceptionistProcessPaymentRequest
                .builder()
                .paymentRequestId(paymentRequestData.getPaymentRequestId())
                .build(), partnerToken)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(PaymentStatusUpdateResponse.class);

        assertEquals(PaymentStatusUpdateResponse
                .builder()
                .error(PAYMENT_IS_IN_INVALID_STATUS)
                .build(), partnerApprovalActualResult);

        val actualResultPartnersPayments = getPaymentByRequestId(paymentRequestData.getPaymentRequestId(), partnerToken)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(PaymentDetailsResponseModel.class);

        assertEquals(actualResultPartnersPayments.getFiatAmount() * customerPercentOfTotalPayment,
                actualResultPartnersPayments.getFiatSendingAmount());

        val actualResultPartnersIntegration = PartnersIntegrationUtils.getPaymentRequest(
                PaymentRequestStatusRequestModel
                        .builder()
                        .partnerId(partnerData.getId())
                        .paymentRequestId(paymentRequestData.getPaymentRequestId())
                        .build(), partnerToken)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(PaymentRequestStatusResponseModel.class);

        assertAll(
                () -> assertEquals(actualResultPartnersPayments.getTotalBillAmount(),
                        actualResultPartnersIntegration.getTotalFiatAmount()),
                () -> assertEquals(actualResultPartnersPayments.getTokensSendingAmount(),
                        actualResultPartnersIntegration.getTokensAmount()),
                () -> assertEquals(actualResultPartnersPayments.getFiatSendingAmount(),
                        actualResultPartnersIntegration.getFiatAmount())
        );
    }

    @Test
    @UserStoryId(3360)
    void shouldNotFlowApprovedPaymentIfExpirationFired() {
        val expirationSeconds = 2;
        val customerPercentOfTotalPayment = 0.5;
        val customerData = createCustomerFundedViaBonusReward(Double.valueOf(SOME_INITIAL_FIAT_AMOUNT));
        customerToken = getUserToken(customerData.getEmail(), customerData.getPassword());

        locationId = getLocationId(partnerData);

        val paymentRequestData = postPayment(PaymentRequestModel
                .builder()
                .currency(AED_CURRENCY)
                .customerId(customerData.getCustomerId())
                .partnerId(partnerData.getId())
                .tokensAmount(SOME_TOKENS_AMOUNT.toString())
                .locationId(locationId)
                .partnerMessageId(SOME_PAYMENT_INFO)
                .totalBillAmount(SOME_TOTAL_FIAT_AMOUNT)
                .customerExpirationInSeconds(expirationSeconds)
                .build(), partnerToken)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(PaymentRequestResponseModel.class);

        waitForExpiration(expirationSeconds);

        val pendingPaymentsCollection = getCustomerPending(PaginatedRequestForCustomer
                .customerRequestBuilder()
                .currentPage(CURRENT_PAGE_LOWER_BOUNDARY)
                .pageSize(PAGE_SIZE_UPPER_BOUNDARY)
                .customerId(customerData.getCustomerId())
                .build(), partnerToken)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(PaymentResponseModel[].class);

        val customerApprovalActualResult = postCustomerApproval(CustomerApprovePaymentRequest
                .builder()
                .customerId(customerData.getCustomerId())
                .paymentRequestId(paymentRequestData.getPaymentRequestId())
                .sendingAmount(Double.valueOf(SOME_TOKENS_AMOUNT * customerPercentOfTotalPayment).toString())
                .build(), partnerToken)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(PaymentStatusUpdateResponse.class);

        assertEquals(PaymentStatusUpdateResponse
                .builder()
                .error(PAYMENT_IS_IN_INVALID_STATUS)
                .build(), customerApprovalActualResult);

        val partnerApprovalActualResult = postPartnerApproval(ReceptionistProcessPaymentRequest
                .builder()
                .paymentRequestId(paymentRequestData.getPaymentRequestId())
                .build(), partnerToken)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(PaymentStatusUpdateResponse.class);

        assertEquals(PaymentStatusUpdateResponse
                .builder()
                .error(PAYMENT_IS_IN_INVALID_STATUS)
                .build(), partnerApprovalActualResult);

        val actualResultPartnersPayments = getPaymentByRequestId(paymentRequestData.getPaymentRequestId(), partnerToken)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(PaymentDetailsResponseModel.class);

        assertEquals(0.0,
                actualResultPartnersPayments.getFiatSendingAmount());
    }

    @Test
    @UserStoryId(2327)
    void shouldNotFlowPaymentRejectedByCustomer() {
        val customerData = createCustomerFundedViaBonusReward(Double.valueOf(SOME_INITIAL_FIAT_AMOUNT));
        customerToken = getUserToken(customerData.getEmail(), customerData.getPassword());

        val paymentRequestData = postPayment(PaymentRequestModel
                .builder()
                .currency(AED_CURRENCY)
                .customerId(customerData.getCustomerId())
                .partnerId(partnerData.getId())
                .tokensAmount(SOME_TOKENS_AMOUNT.toString())
                .locationId(locationId)
                .partnerMessageId(SOME_PAYMENT_INFO)
                .totalBillAmount(SOME_TOTAL_FIAT_AMOUNT)
                .build(), partnerToken)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(PaymentRequestResponseModel.class);

        val pendingPaymentsCollection = getCustomerPending(PaginatedRequestForCustomer
                .customerRequestBuilder()
                .currentPage(CURRENT_PAGE_LOWER_BOUNDARY)
                .pageSize(PAGE_SIZE_UPPER_BOUNDARY)
                .customerId(customerData.getCustomerId())
                .build(), partnerToken)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(PaymentResponseModel[].class);

        val actualPaymentRequest = Arrays.stream(pendingPaymentsCollection)
                .filter(paymentRequest -> paymentRequestData.getPaymentRequestId()
                        .equalsIgnoreCase(paymentRequest.getPaymentRequestId()))
                .findFirst()
                .orElse(new PaymentResponseModel());

        val customerRejectionActualResult = postCustomerRejection(CustomerRejectPaymentRequest
                .builder()
                .customerId(customerData.getCustomerId())
                .paymentRequestId(paymentRequestData.getPaymentRequestId())
                .build(), partnerToken)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(PaymentStatusUpdateResponse.class);

        assertEquals(PaymentStatusUpdateResponse
                .builder()
                .error(PaymentStatusUpdateErrorCode.NONE)
                .build(), customerRejectionActualResult);

        val partnerApprovalActualResult = postPartnerApproval(ReceptionistProcessPaymentRequest
                .builder()
                .paymentRequestId(paymentRequestData.getPaymentRequestId())
                .build(), partnerToken)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(PaymentStatusUpdateResponse.class);

        assertEquals(PaymentStatusUpdateResponse
                .builder()
                .error(PAYMENT_IS_IN_INVALID_STATUS)
                .build(), partnerApprovalActualResult);
    }

    @Test
    @UserStoryId(storyId = {2327, 2619})
    void shouldNotFlowPaymentCancelledByPartner() {
        val customerData = PrivateBlockchainFacadeUtils
                .createCustomerFundedViaBonusReward(SOME_INITIAL_TOKENS_AMOUNT, true);
        customerToken = getUserToken(customerData.getEmail(), customerData.getPassword());

        val paymentRequestData = postPayment(PaymentRequestModel
                .builder()
                .currency(AED_CURRENCY)
                .customerId(customerData.getCustomerId())
                .partnerId(partnerData.getId())
                .tokensAmount(SOME_TOKENS_AMOUNT.toString())
                .locationId(locationId)
                .partnerMessageId(SOME_PAYMENT_INFO)
                .totalBillAmount(SOME_TOTAL_FIAT_AMOUNT)
                .build(), partnerToken)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(PaymentRequestResponseModel.class);

        val pendingPaymentsCollection = getCustomerPending(PaginatedRequestForCustomer
                .customerRequestBuilder()
                .currentPage(CURRENT_PAGE_LOWER_BOUNDARY)
                .pageSize(PAGE_SIZE_UPPER_BOUNDARY)
                .customerId(customerData.getCustomerId())
                .build(), partnerToken)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(PaymentResponseModel[].class);

        val actualPaymentRequest = Arrays.stream(pendingPaymentsCollection)
                .filter(paymentRequest -> paymentRequestData.getPaymentRequestId()
                        .equalsIgnoreCase(paymentRequest.getPaymentRequestId()))
                .findFirst()
                .orElse(new PaymentResponseModel());

        assertEquals(PaymentRequestStatus.CREATED, actualPaymentRequest.getStatus());

        // TODO: Allow canceling the payment request in status `Created`
        val partnerCancellationActualResult = PartnersPaymentsUtils
                .postPartnerCancellation(ReceptionistProcessPaymentRequest
                        .builder()
                        .paymentRequestId(paymentRequestData.getPaymentRequestId())
                        .build(), partnerToken)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(PaymentStatusUpdateResponse.class);

        assertEquals(PaymentStatusUpdateResponse
                .builder()
                .error(NONE)
                .build(), partnerCancellationActualResult);
    }
}
