package com.lykke.tests.api.service.partnerapi;

import static com.lykke.api.testing.api.common.BuilderUtils.getObjectWithData;
import static com.lykke.api.testing.api.common.GenerateUtils.generateRandomString;
import static com.lykke.api.testing.api.common.GenerateUtils.getRandomUuid;
import static com.lykke.api.testing.api.common.PasswordGen.generateValidPassword;
import static com.lykke.tests.api.common.CommonConsts.Currency.AED_CURRENCY;
import static com.lykke.api.testing.api.common.GenerateUtils.generateRandomEmail;
import static com.lykke.api.testing.api.common.GenerateUtils.generateRandomString;
import static com.lykke.api.testing.api.common.GenerateUtils.getRandomUuid;
import static com.lykke.api.testing.api.common.PasswordGen.generateValidPassword;
import static com.lykke.tests.api.common.CommonConsts.SMOKE_TEST;
import static com.lykke.tests.api.common.CommonMethods.waitForExpiration;
import static com.lykke.tests.api.service.customer.LoginLogoutCustomerUtils.getUserToken;
import static com.lykke.tests.api.service.customer.PartnersPaymentsUtils.approvePayment;
import static com.lykke.tests.api.service.partnerapi.PartnerApiLogInLogOutTests.USER_INFO;
import static com.lykke.tests.api.service.partnerapi.PartnerApiUtils.cancelPaymentByRequestId;
import static com.lykke.tests.api.service.partnerapi.PartnerApiUtils.getPartnerToken;
import static com.lykke.tests.api.service.partnerapi.PartnerApiUtils.getPaymentByRequestId;
import static com.lykke.tests.api.service.partnerapi.PartnerApiUtils.postPaymentRequest;
import static com.lykke.tests.api.service.partnermanagement.PartnerManagementUtils.SOME_EXTERNAL_ID;
import static com.lykke.tests.api.service.partnermanagement.PartnerManagementUtils.createDefaultPartner;
import static com.lykke.tests.api.service.partnersintegration.PartnersCommonUtils.getFiatAmountExternal;
import static com.lykke.tests.api.service.partnersintegration.PartnersCommonUtils.getSendingAmountExternal;
import static com.lykke.tests.api.service.partnersintegration.PartnersCommonUtils.getTokensAmountExternal;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.apache.http.HttpStatus.SC_NO_CONTENT;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.params.provider.Arguments.of;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy.LowerCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.lykke.api.testing.annotations.UserStoryId;
import com.lykke.tests.api.base.BaseApiTest;
import com.lykke.tests.api.common.model.CustomerBalanceInfo;
import com.lykke.tests.api.service.customer.model.partnerspayments.ApprovePartnerPaymentRequest;
import com.lykke.tests.api.service.customermanagement.RegisterCustomerUtils.PartnerApiErrorResponse;
import com.lykke.tests.api.service.partnerapi.model.CreatePaymentRequestRequestModel;
import com.lykke.tests.api.service.partnerapi.model.CreatePaymentRequestResponseModel;
import com.lykke.tests.api.service.partnerapi.model.CreatePaymentRequestStatus;
import com.lykke.tests.api.service.partnerapi.model.GetPaymentRequestRequestModel;
import com.lykke.tests.api.service.partnerapi.model.GetPaymentRequestStatus;
import com.lykke.tests.api.service.partnerapi.model.GetPaymentRequestStatusResponseModel;
import com.lykke.tests.api.service.partnermanagement.model.PartnerCreateResponse;
import com.lykke.tests.api.service.privateblockchainfacade.PrivateBlockchainFacadeUtils;
import io.restassured.response.ValidatableResponse;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.val;
import org.awaitility.Awaitility;
import org.awaitility.Duration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class SpendingTests extends BaseApiTest {

    private static final Integer FIAT_AMOUNT_01 = 10;
    private static final Integer FIAT_AMOUNT_02 = 100;
    private static final Integer FIAT_AMOUNT_03 = 1_000_000;
    private static final Integer FIAT_AMOUNT_04 = 1_900_000;
    private static final Integer TOTAL_FIAT_AMOUNT_01 = FIAT_AMOUNT_01 * 2;
    private static final Integer TOTAL_FIAT_AMOUNT_02 = FIAT_AMOUNT_02 * 2;
    private static final Integer TOTAL_FIAT_AMOUNT_03 = FIAT_AMOUNT_03 * 2;
    private static final Integer TOTAL_FIAT_AMOUNT_04 = FIAT_AMOUNT_04 * 2;
    private static final Integer INITIAL_FIAT_AMOUNT_01 = FIAT_AMOUNT_01 * 5;
    private static final Integer INITIAL_FIAT_AMOUNT_02 = FIAT_AMOUNT_02 * 5;
    private static final Integer INITIAL_FIAT_AMOUNT_03 = FIAT_AMOUNT_03 * 5;
    private static final Integer INITIAL_FIAT_AMOUNT_04 = FIAT_AMOUNT_04 * 5;
    private static final String SOME_PAYMENT_INFO = "my payment";
    private static final Double TOKENS_AMOUNT_01 = 15.0;
    private static final Double TOKENS_AMOUNT_02 = 150.0;
    private static final Double TOKENS_AMOUNT_03 = 150_000.0;
    private static final Double TOKENS_AMOUNT_04 = 300_000.0;
    private static final Double ZERO_TOKENS_AMOUNT = 0.0;
    private static final Integer ZERO_FIAT_AMOUNT = 0;
    private static final String SOME_POS_ID = "pos_id";
    private static final String EMPTY_FIELD_PATH = "\"\"[0]";
    private static final String CURRENCY_FIELD_PATH = "Currency[0]";
    private static final String CUSTOMER_ID_FIELD_PATH = "CustomerId[0]";
    private static final String FIAT_AMOUNT_FIELD_PATH = "FiatAmount[0]";
    private static final String TOTAL_FIAT_AMOUNT_FIELD_PATH = "TotalFiatAmount[0]";
    private static final String CUSTOMER_ID_MUST_NOT_BE_EMPTY_ERROR_MESSAGE = "'Customer Id' must not be empty.";
    private static final String FIAT_OR_TOKEN_AMOUNT_REQUIRED_ERROR_MESSAGE = "Fiat or token amount is required";
    private static final String CURRENCY_MUST_NOT_BE_EMPTY_ERROR_MESSAGE = "'Currency' must not be empty.";
    private static final String LOCATION_ID_MUST_NOT_BE_EMPTY = "'Location Id' must not be empty.";
    private static final String TOTAL_FIAT_AMOUNT_MUST_NOT_BE_EMPTY = "'Total Fiat Amount' must not be empty.";
    private static final int MAX_PAYMENT_INFO_LENGTH = 5120;
    private static final String TOTAL_FIAT_AMOUNT_IS_MUST_BE_GREATER_THEN_0_ERROR_MESSAGE = "Total Fiat amount is must be greater then 0";
    private static final String FIAT_AMOUNT_IS_MUST_BE_GREATER_THEN_0_ERROR_MESSAGE = "Fiat amount is must be greater then 0";
    private static final String PAYMENT_IS_NOT_IN_A_CORRECT_STATUS_TO_BE_UPDATED_ERROR = "PaymentIsNotInACorrectStatusToBeUpdated";
    private static final String THE_PAYMENT_REQUEST_IS_NOT_IN_A_CORRECT_STATUS_TO_BE_UPDATED_ERROR_MESSAGE = "The payment request is not in a correct status to be updated";
    private static final String NO_FIELD = "\"\"[0]";
    private static final String LOCATION_ID_FIELD = "LocationId[0]";
    private static final String TOTAL_FIAT_AMOUNT_FIELD = "TotalFiatAmount[0]";
    private static String partnerId;
    private static String partnerPassword;
    private static String partnerToken;
    private static String customerId;
    private static String customerToken;
    private static String externalLocationId;
    private PartnerCreateResponse partnerData;

    static Stream<Arguments> getAmountData() {
        return Stream.of(
                of("zero tokens amount",
                        Arrays.asList(
                                (Consumer<CreatePaymentRequestRequestModel>) x -> x
                                        .setFiatAmount(FIAT_AMOUNT_01.toString()),
                                (Consumer<CreatePaymentRequestRequestModel>) x -> x
                                        .setTotalFiatAmount(TOTAL_FIAT_AMOUNT_01.toString()),
                                (Consumer<CreatePaymentRequestRequestModel>) x -> x
                                        .setTokensAmount(ZERO_TOKENS_AMOUNT.toString())),
                        CreatePaymentRequestResponseModel
                                .builder()
                                .status(CreatePaymentRequestStatus.OK)
                                .build()),
                of("zero fiat amount",
                        Arrays.asList(
                                (Consumer<CreatePaymentRequestRequestModel>) x -> x
                                        .setFiatAmount(ZERO_FIAT_AMOUNT.toString()),
                                (Consumer<CreatePaymentRequestRequestModel>) x -> x
                                        .setTotalFiatAmount(TOTAL_FIAT_AMOUNT_01.toString()),
                                (Consumer<CreatePaymentRequestRequestModel>) x -> x
                                        .setTokensAmount(TOKENS_AMOUNT_01.toString())),
                        CreatePaymentRequestResponseModel
                                .builder()
                                .status(CreatePaymentRequestStatus.OK)
                                .build()),
                of("CannotPassBothFiatAndTokensAmount",
                        Arrays.asList(
                                (Consumer<CreatePaymentRequestRequestModel>) x -> x
                                        .setFiatAmount(FIAT_AMOUNT_01.toString()),
                                (Consumer<CreatePaymentRequestRequestModel>) x -> x
                                        .setTotalFiatAmount(TOTAL_FIAT_AMOUNT_01.toString()),
                                (Consumer<CreatePaymentRequestRequestModel>) x -> x
                                        .setTokensAmount(TOKENS_AMOUNT_01.toString())),
                        CreatePaymentRequestResponseModel
                                .builder()
                                .status(CreatePaymentRequestStatus.CANNOT_PASS_BOTH_FIAT_AND_TOKENS_AMOUNT)
                                .build()),
                of("no tokens amount",
                        Arrays.asList(
                                (Consumer<CreatePaymentRequestRequestModel>) x -> x
                                        .setFiatAmount(FIAT_AMOUNT_01.toString()),
                                (Consumer<CreatePaymentRequestRequestModel>) x -> x
                                        .setTotalFiatAmount(TOTAL_FIAT_AMOUNT_01.toString())),
                        CreatePaymentRequestResponseModel
                                .builder()
                                .status(CreatePaymentRequestStatus.OK)
                                .build()),
                of("no fiat amount",
                        Arrays.asList(
                                (Consumer<CreatePaymentRequestRequestModel>) x -> x
                                        .setTokensAmount(TOKENS_AMOUNT_01.toString()),
                                (Consumer<CreatePaymentRequestRequestModel>) x -> x
                                        .setTotalFiatAmount(TOTAL_FIAT_AMOUNT_01.toString())),
                        CreatePaymentRequestResponseModel
                                .builder()
                                .status(CreatePaymentRequestStatus.OK)
                                .build())
        );
    }

    static Stream<Arguments> getInvalidInputDataForPostingPayment() {
        return Stream.of(
                of("no fiat or tokens",
                        Arrays.asList(
                                (Consumer<CreatePaymentRequestRequestModel>) x -> x
                                        .setFiatAmount(ZERO_FIAT_AMOUNT.toString()),
                                (Consumer<CreatePaymentRequestRequestModel>) x -> x
                                        .setTotalFiatAmount(TOTAL_FIAT_AMOUNT_01.toString()),
                                (Consumer<CreatePaymentRequestRequestModel>) x -> x
                                        .setTokensAmount(ZERO_TOKENS_AMOUNT.toString())),
                        (Consumer<ValidatableResponse>) (response -> response
                                .body(EMPTY_FIELD_PATH, containsString(
                                        FIAT_OR_TOKEN_AMOUNT_REQUIRED_ERROR_MESSAGE)))),
                of("no fiat,tokens, currency, customer id",
                        Arrays.asList(
                                (Consumer<CreatePaymentRequestRequestModel>) x -> x
                                        .setFiatAmount(ZERO_FIAT_AMOUNT.toString()),
                                (Consumer<CreatePaymentRequestRequestModel>) x -> x
                                        .setTotalFiatAmount(TOTAL_FIAT_AMOUNT_01.toString()),
                                (Consumer<CreatePaymentRequestRequestModel>) x -> x
                                        .setTokensAmount(ZERO_TOKENS_AMOUNT.toString()),
                                (Consumer<CreatePaymentRequestRequestModel>) x -> x.setCurrency(EMPTY),
                                (Consumer<CreatePaymentRequestRequestModel>) x -> x.setCustomerId(EMPTY)),
                        (Consumer<ValidatableResponse>) (response -> response
                                .body(EMPTY_FIELD_PATH, containsString(
                                        FIAT_OR_TOKEN_AMOUNT_REQUIRED_ERROR_MESSAGE))
                                .body(CURRENCY_FIELD_PATH, containsString(CURRENCY_MUST_NOT_BE_EMPTY_ERROR_MESSAGE))
                                .body(CUSTOMER_ID_FIELD_PATH,
                                        containsString(CUSTOMER_ID_MUST_NOT_BE_EMPTY_ERROR_MESSAGE)))),
                of("invalid fiat amount",
                        Arrays.asList(
                                (Consumer<CreatePaymentRequestRequestModel>) x -> x
                                        .setFiatAmount(ZERO_FIAT_AMOUNT.toString()),
                                (Consumer<CreatePaymentRequestRequestModel>) x -> x
                                        .setTotalFiatAmount(TOTAL_FIAT_AMOUNT_01.toString()),
                                (Consumer<CreatePaymentRequestRequestModel>) x -> x
                                        .setTokensAmount(ZERO_TOKENS_AMOUNT.toString())),
                        (Consumer<ValidatableResponse>) (response -> response
                                .body(EMPTY_FIELD_PATH, containsString(
                                        FIAT_OR_TOKEN_AMOUNT_REQUIRED_ERROR_MESSAGE)))),
                of("negative fiat amount",
                        Arrays.asList(
                                (Consumer<CreatePaymentRequestRequestModel>) x -> x.setFiatAmount("-1"),
                                (Consumer<CreatePaymentRequestRequestModel>) x -> x
                                        .setTotalFiatAmount(TOTAL_FIAT_AMOUNT_01.toString()),
                                (Consumer<CreatePaymentRequestRequestModel>) x -> x
                                        .setTokensAmount(ZERO_TOKENS_AMOUNT.toString())),
                        (Consumer<ValidatableResponse>) (response -> response
                                .body(FIAT_AMOUNT_FIELD_PATH, containsString(
                                        FIAT_AMOUNT_IS_MUST_BE_GREATER_THEN_0_ERROR_MESSAGE)))),
                of("negative total fiat amount",
                        Arrays.asList(
                                (Consumer<CreatePaymentRequestRequestModel>) x -> x
                                        .setFiatAmount(FIAT_AMOUNT_01.toString()),
                                (Consumer<CreatePaymentRequestRequestModel>) x -> x
                                        .setTotalFiatAmount("-1"),
                                (Consumer<CreatePaymentRequestRequestModel>) x -> x
                                        .setTokensAmount(ZERO_TOKENS_AMOUNT.toString())),
                        (Consumer<ValidatableResponse>) (response -> response
                                .body(TOTAL_FIAT_AMOUNT_FIELD_PATH,
                                        containsString(
                                                TOTAL_FIAT_AMOUNT_IS_MUST_BE_GREATER_THEN_0_ERROR_MESSAGE))))
        );
    }

    static Stream<Arguments> getDifferentValidAmounts() {
        return Stream.of(
                of(INITIAL_FIAT_AMOUNT_01, ZERO_FIAT_AMOUNT, TOKENS_AMOUNT_01, TOTAL_FIAT_AMOUNT_01),
                of(INITIAL_FIAT_AMOUNT_03, ZERO_FIAT_AMOUNT, TOKENS_AMOUNT_03, TOTAL_FIAT_AMOUNT_03)
        );
    }

    static Stream<Arguments> getDifferentValidAmountsWithExpirationOn() {
        return Stream.of(
                of(INITIAL_FIAT_AMOUNT_01, ZERO_FIAT_AMOUNT, TOKENS_AMOUNT_01, TOTAL_FIAT_AMOUNT_01, 2)
        );
    }

    static Stream<Arguments> getDifferentValidAmountsWithExpirationOff() {
        return Stream.of(
                of(INITIAL_FIAT_AMOUNT_01, ZERO_FIAT_AMOUNT, TOKENS_AMOUNT_01, TOTAL_FIAT_AMOUNT_01, 0)
        );
    }

    static Stream<Arguments> getDifferentInvalidAmounts() {
        return Stream.of(
                of(INITIAL_FIAT_AMOUNT_04, FIAT_AMOUNT_04, TOKENS_AMOUNT_04, TOTAL_FIAT_AMOUNT_04,
                        PartnerApiErrorResponse
                                .builder()
                                .error("ModelValidationFailed")
                                .message("The PaymentRequestId field is required.")
                                .build()),
                of(INITIAL_FIAT_AMOUNT_02, FIAT_AMOUNT_02, ZERO_TOKENS_AMOUNT, TOTAL_FIAT_AMOUNT_02,
                        PartnerApiErrorResponse
                                .builder()
                                .error("InvalidAmount")
                                .message("The value for amount is not valid")
                                .build())
        );
    }

    @BeforeEach
    void setUp() {
        partnerPassword = generateValidPassword();
        partnerId = getRandomUuid();
        val locationSuffix = generateRandomString(10);
        externalLocationId = SOME_EXTERNAL_ID + locationSuffix;
        partnerData = createDefaultPartner(partnerId, partnerPassword, generateRandomString(10),
                locationSuffix);
        partnerToken = getPartnerToken(partnerId, partnerPassword, USER_INFO);
    }

    @ParameterizedTest(name = "Run {index}: {0}")
    @MethodSource("getAmountData")
    @UserStoryId(2374)
    void shouldPostPaymentRequest(String description, List<Consumer<CreatePaymentRequestRequestModel>> requestModelData,
            CreatePaymentRequestResponseModel expectedResult) {
        val customerData = registerNewFundedCustomerAndGetOnesData(Double.valueOf(INITIAL_FIAT_AMOUNT_01));

        val requestModel = getObjectWithData(CreatePaymentRequestRequestModel
                .builder()
                .currency(AED_CURRENCY)
                .customerId(customerId)
                .locationId(externalLocationId)
                .partnerId(partnerData.getId())
                .paymentInfo(SOME_PAYMENT_INFO)
                .build(), requestModelData);

        val actualResult = postPaymentRequest(requestModel, partnerToken)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(CreatePaymentRequestResponseModel.class);

        assertEquals(expectedResult.getStatus(), actualResult.getStatus());
    }

    @Test
    @UserStoryId(2779)
    void shouldNotPostPaymentRequestIfPaymentInfoExceeds5120() {
        val customerData = registerNewFundedCustomerAndGetOnesData(Double.valueOf(INITIAL_FIAT_AMOUNT_01));

        val requestModel = getObjectWithData(CreatePaymentRequestRequestModel
                .builder()
                .currency(AED_CURRENCY)
                .customerId(customerId)
                .locationId(externalLocationId)
                .partnerId(partnerData.getId())
                .paymentInfo(generateRandomString(MAX_PAYMENT_INFO_LENGTH + 1))
                .build());

        postPaymentRequest(requestModel, partnerToken)
                .then()
                .assertThat()
                .statusCode(SC_BAD_REQUEST);
    }

    @Test
    @UserStoryId(4099)
    void shouldNotPostPaymentRequestIfLocationIdIsNotSet() {
        val customerData = registerNewFundedCustomerAndGetOnesData(Double.valueOf(INITIAL_FIAT_AMOUNT_01));

        val requestModel = getObjectWithData(CreatePaymentRequestRequestModel
                .builder()
                .currency(AED_CURRENCY)
                .customerId(customerId)
                .partnerId(partnerData.getId())
                .paymentInfo(generateRandomString(MAX_PAYMENT_INFO_LENGTH))
                .build());

        postPaymentRequest(requestModel, partnerToken)
                .then()
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .body(NO_FIELD, equalTo(FIAT_OR_TOKEN_AMOUNT_REQUIRED_ERROR_MESSAGE))
                .body(LOCATION_ID_FIELD, equalTo(LOCATION_ID_MUST_NOT_BE_EMPTY))
                .body(TOTAL_FIAT_AMOUNT_FIELD, equalTo(TOTAL_FIAT_AMOUNT_MUST_NOT_BE_EMPTY));
    }

    @Test
    @UserStoryId(2779)
    void shouldPostPaymentRequestIfPaymentInfoIsNoGreaterThan5120() {
        val customerData = registerNewFundedCustomerAndGetOnesData(Double.valueOf(INITIAL_FIAT_AMOUNT_01));

        val actualResult = postPaymentRequest(CreatePaymentRequestRequestModel
                .builder()
                .currency(AED_CURRENCY)
                .fiatAmount(FIAT_AMOUNT_01.toString())
                .customerId(customerId)
                .locationId(externalLocationId)
                .partnerId(partnerData.getId())
                .paymentInfo(generateRandomString(MAX_PAYMENT_INFO_LENGTH))
                .posId(SOME_POS_ID)
                .tokensAmount(ZERO_TOKENS_AMOUNT.toString())
                .totalFiatAmount(TOTAL_FIAT_AMOUNT_01.toString())
                .build(), partnerToken)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(CreatePaymentRequestResponseModel.class);

        assertNotNull(actualResult.getPaymentRequestId());
    }

    @ParameterizedTest(name = "Run {index}: {0}")
    @MethodSource("getInvalidInputDataForPostingPayment")
    @UserStoryId(2374)
    void shouldPostPaymentRequest(String description, List<Consumer<CreatePaymentRequestRequestModel>> requestModelData,
            Consumer<ValidatableResponse> assertAction) {

        val requestModel = getObjectWithData(CreatePaymentRequestRequestModel
                .builder()
                .currency(AED_CURRENCY)
                .customerId(customerId)
                .locationId(externalLocationId)
                .partnerId(partnerData.getId())
                .paymentInfo(SOME_PAYMENT_INFO)
                .build(), requestModelData);

        val actualResponse = postPaymentRequest(requestModel, partnerToken)
                .thenReturn();
        actualResponse
                .then()
                .assertThat()
                .statusCode(SC_BAD_REQUEST);
        assertAction.accept(actualResponse.then().assertThat());
    }

    @Test
    @UserStoryId(2374)
    void shouldPostPaymentRequest() {
        val customerData = registerNewFundedCustomerAndGetOnesData(Double.valueOf(INITIAL_FIAT_AMOUNT_01));

        val actualResult = postPaymentRequest(CreatePaymentRequestRequestModel
                .builder()
                .currency(AED_CURRENCY)
                .fiatAmount(FIAT_AMOUNT_01.toString())
                .customerId(customerId)
                .locationId(externalLocationId)
                .partnerId(partnerData.getId())
                .paymentInfo(SOME_PAYMENT_INFO)
                .posId(SOME_POS_ID)
                .tokensAmount(ZERO_TOKENS_AMOUNT.toString())
                .totalFiatAmount(TOTAL_FIAT_AMOUNT_01.toString())
                .build(), partnerToken)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(CreatePaymentRequestResponseModel.class);

        assertNotNull(actualResult.getPaymentRequestId());
    }

    ////5555-2
    @ParameterizedTest(name = "Run {index}: initial fiat amount={0}, fiat amount={1}, tokens amount={2}, total fiat amount={3}")
    @MethodSource("getDifferentValidAmounts")
    @Tag(SMOKE_TEST)
    @UserStoryId(2374)
    void shouldGetApprovedPaymentRequest(int initialFiatAmount, Integer fiatAmount, Double tokensAmount,
            Integer totalFiatAmount) {
        val customerData = registerNewFundedCustomerAndGetOnesData(Double.valueOf(initialFiatAmount));

        val paymentData = postPaymentRequest(CreatePaymentRequestRequestModel
                .builder()
                .currency(AED_CURRENCY)
                .customerId(customerData.getCustomerId())
                .fiatAmount(fiatAmount.toString())
                .locationId(externalLocationId)
                .partnerId(partnerData.getId())
                .paymentInfo(SOME_PAYMENT_INFO)
                .tokensAmount(tokensAmount.toString())
                .totalFiatAmount(totalFiatAmount.toString())
                .build(), partnerToken)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(CreatePaymentRequestResponseModel.class);

        // needs customer's confirmation via Customer-Api
        approvePayment(ApprovePartnerPaymentRequest
                .builder()
                .paymentRequestId(paymentData.getPaymentRequestId())
                .sendingAmount(getSendingAmountExternal(tokensAmount, fiatAmount).toString())
                .build(), customerToken)
                .then()
                .assertThat()
                .statusCode(SC_NO_CONTENT);

        Awaitility.await()
                .atMost(Duration.ONE_MINUTE.getValue(), TimeUnit.SECONDS)
                .pollInterval(Duration.FIVE_SECONDS)
                .until(() -> GetPaymentRequestStatus.PENDING_PARTNER_CONFIRMATION == getPaymentByRequestId(
                        GetPaymentRequestRequestModel
                                .builder()
                                .paymentRequestId(paymentData.getPaymentRequestId())
                                .build(), partnerToken)
                        .then()
                        .assertThat()
                        .statusCode(SC_OK)
                        .extract()
                        .as(GetPaymentRequestStatusResponseModel.class).getStatus()
                );

        val expectedResult = GetPaymentRequestStatusResponseModel
                .builder()
                .status(GetPaymentRequestStatus.PENDING_PARTNER_CONFIRMATION)
                .totalFiatAmount(totalFiatAmount.toString())
                .fiatAmount(getFiatAmountExternal(tokensAmount, fiatAmount).toString())
                .fiatCurrency(AED_CURRENCY)
                .tokensAmount(getTokensAmountExternal(tokensAmount, fiatAmount).toString())
                .build();

        val actualResult = getPaymentByRequestId(GetPaymentRequestRequestModel
                .builder()
                .paymentRequestId(paymentData.getPaymentRequestId())
                .build(), partnerToken)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(GetPaymentRequestStatusResponseModel.class);

        assertEquals(expectedResult, actualResult);
    }

    @ParameterizedTest(name = "Run {index}: initial fiat amount={0}, fiat amount={1}, tokens amount={2}, total fiat amount={3}, seconds={4}")
    @MethodSource("getDifferentValidAmountsWithExpirationOn")
    @Tag(SMOKE_TEST)
    @UserStoryId(3320)
    void shouldGetApprovedPaymentRequestWithExpirationOn(int initialFiatAmount, Integer fiatAmount, Double tokensAmount,
            Integer totalFiatAmount, int expirationSeconds) {
        val customerData = registerNewFundedCustomerAndGetOnesData(Double.valueOf(initialFiatAmount));

        val paymentData = postPaymentRequest(CreatePaymentRequestRequestModel
                .builder()
                .currency(AED_CURRENCY)
                .customerId(customerData.getCustomerId())
                .fiatAmount(fiatAmount.toString())
                .locationId(externalLocationId)
                .partnerId(partnerData.getId())
                .paymentInfo(SOME_PAYMENT_INFO)
                .tokensAmount(tokensAmount.toString())
                .totalFiatAmount(totalFiatAmount.toString())
                .expirationTimeoutInSeconds(expirationSeconds)
                .build(), partnerToken)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(CreatePaymentRequestResponseModel.class);

        waitForExpiration(expirationSeconds);

        // needs customer's confirmation via Customer-Api
        val actualResult = approvePayment(ApprovePartnerPaymentRequest
                .builder()
                .paymentRequestId(paymentData.getPaymentRequestId())
                .sendingAmount(getSendingAmountExternal(tokensAmount, fiatAmount).toString())
                .build(), customerToken)
                .then()
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .extract()
                .as(PartnerApiValidationErrorResponse.class);

        val expectedResult = PartnerApiValidationErrorResponse
                .builder()
                .error(PAYMENT_IS_NOT_IN_A_CORRECT_STATUS_TO_BE_UPDATED_ERROR)
                .message(THE_PAYMENT_REQUEST_IS_NOT_IN_A_CORRECT_STATUS_TO_BE_UPDATED_ERROR_MESSAGE)
                .build();

        assertEquals(expectedResult, actualResult);
    }

    @ParameterizedTest(name = "Run {index}: initial fiat amount={0}, fiat amount={1}, tokens amount={2}, total fiat amount={3}, seconds={4}")
    @MethodSource("getDifferentValidAmountsWithExpirationOff")
    @Tag(SMOKE_TEST)
    @UserStoryId(3320)
    void shouldGetApprovedPaymentRequestWithExpirationOff(int initialFiatAmount, Integer fiatAmount,
            Double tokensAmount,
            Integer totalFiatAmount, int expirationSeconds) {
        val customerData = registerNewFundedCustomerAndGetOnesData(Double.valueOf(initialFiatAmount));

        val paymentData = postPaymentRequest(CreatePaymentRequestRequestModel
                .builder()
                .currency(AED_CURRENCY)
                .customerId(customerData.getCustomerId())
                .fiatAmount(fiatAmount.toString())
                .locationId(externalLocationId)
                .partnerId(partnerData.getId())
                .paymentInfo(SOME_PAYMENT_INFO)
                .tokensAmount(tokensAmount.toString())
                .totalFiatAmount(totalFiatAmount.toString())
                .expirationTimeoutInSeconds(expirationSeconds)
                .build(), partnerToken)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(CreatePaymentRequestResponseModel.class);

        waitForExpiration(expirationSeconds);

        // needs customer's confirmation via Customer-Api
        approvePayment(ApprovePartnerPaymentRequest
                .builder()
                .paymentRequestId(paymentData.getPaymentRequestId())
                .sendingAmount(getSendingAmountExternal(tokensAmount, fiatAmount).toString())
                .build(), customerToken)
                .then()
                .assertThat()
                .statusCode(SC_NO_CONTENT);

        Awaitility.await()
                .atMost(Duration.ONE_MINUTE.getValue(), TimeUnit.SECONDS)
                .pollInterval(Duration.FIVE_SECONDS)
                .until(() -> GetPaymentRequestStatus.PENDING_PARTNER_CONFIRMATION == getPaymentByRequestId(
                        GetPaymentRequestRequestModel
                                .builder()
                                .paymentRequestId(paymentData.getPaymentRequestId())
                                .build(), partnerToken)
                        .then()
                        .assertThat()
                        .statusCode(SC_OK)
                        .extract()
                        .as(GetPaymentRequestStatusResponseModel.class).getStatus()
                );

        val expectedResult = GetPaymentRequestStatusResponseModel
                .builder()
                .status(GetPaymentRequestStatus.PENDING_PARTNER_CONFIRMATION)
                .totalFiatAmount(totalFiatAmount.toString())
                .fiatAmount(getFiatAmountExternal(tokensAmount, fiatAmount).toString())
                .fiatCurrency(AED_CURRENCY)
                .tokensAmount(getTokensAmountExternal(tokensAmount, fiatAmount).toString())
                .build();

        val actualResult = getPaymentByRequestId(GetPaymentRequestRequestModel
                .builder()
                .paymentRequestId(paymentData.getPaymentRequestId())
                .build(), partnerToken)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(GetPaymentRequestStatusResponseModel.class);

        assertAll(
                () -> assertEquals(expectedResult, actualResult),
                // TODO: payment execution timestamp is empty
                //  () -> assertNotNull(actualResult.getPaymentExecutionTimestamp()),
                () -> assertNotNull(actualResult.getPaymentRequestApprovedTimestamp()),
                () -> assertNotNull(actualResult.getPaymentRequestCustomerExpirationTimestamp()),
                () -> assertNotNull(actualResult.getPaymentRequestTimestamp())
        );
    }

    @Disabled("there's difference between Partners Integration and Partner-Api")
    @ParameterizedTest(name = "Run {index}: initial fiat amount={0}, fiat amount={1}, tokens amount={2}, total fiat amount={3}")
    @MethodSource("getDifferentInvalidAmounts")
    @Tag(SMOKE_TEST)
    @UserStoryId(2374)
    void shouldGetRejectedPaymentRequest(int initialFiatAmount, Integer fiatAmount, Double tokensAmount,
            Integer totalFiatAmount) {
        val customerData = registerNewFundedCustomerAndGetOnesData(Double.valueOf(initialFiatAmount));

        val paymentData = postPaymentRequest(CreatePaymentRequestRequestModel
                .builder()
                .currency(AED_CURRENCY)
                .customerId(customerId)
                .fiatAmount(fiatAmount.toString())
                .locationId(externalLocationId)
                .partnerId(partnerData.getId())
                .paymentInfo(SOME_PAYMENT_INFO)
                .tokensAmount(tokensAmount.toString())
                .totalFiatAmount(totalFiatAmount.toString())
                .build(), partnerToken)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(CreatePaymentRequestResponseModel.class);

        // needs customer's confirmation via Customer-Api
        cancelPaymentByRequestId(GetPaymentRequestRequestModel
                .builder()
                .paymentRequestId(paymentData.getPaymentRequestId())
                .build(), partnerToken)
                .then()
                .assertThat()
                .statusCode(SC_NO_CONTENT);

        Awaitility.await()
                .atMost(Duration.ONE_MINUTE.getValue(), TimeUnit.SECONDS)
                .pollInterval(Duration.FIVE_SECONDS)
                .until(() -> GetPaymentRequestStatus.REJECTED_BY_CUSTOMER == getPaymentByRequestId(
                        GetPaymentRequestRequestModel
                                .builder()
                                .paymentRequestId(paymentData.getPaymentRequestId())
                                .build(), partnerToken)
                        .then()
                        .assertThat()
                        .statusCode(SC_OK)
                        .extract()
                        .as(GetPaymentRequestStatusResponseModel.class).getStatus()
                );

        val expectedResult = GetPaymentRequestStatusResponseModel
                .builder()
                .status(GetPaymentRequestStatus.REJECTED_BY_CUSTOMER)
                .totalFiatAmount(totalFiatAmount.toString())
                .fiatAmount(getFiatAmountExternal(tokensAmount, fiatAmount).toString())
                .fiatCurrency(AED_CURRENCY)
                .tokensAmount(getTokensAmountExternal(tokensAmount, fiatAmount).toString())
                .build();

        val actualResult = getPaymentByRequestId(GetPaymentRequestRequestModel
                .builder()
                .paymentRequestId(paymentData.getPaymentRequestId())
                .build(), partnerToken)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(GetPaymentRequestStatusResponseModel.class);

        assertEquals(expectedResult, actualResult);
    }

    ////5555-1
    @ParameterizedTest(name = "Run {index}: initial fiat amount={0}, fiat amount={1}, tokens amount={2}, total fiat amount={3}")
    @MethodSource("getDifferentInvalidAmounts")
    @Tag(SMOKE_TEST)
    @UserStoryId(2374)
    void shouldNotGetApprovedPaymentRequest(int initialFiatAmount, Integer fiatAmount, Double tokensAmount,
            Integer totalFiatAmount, PartnerApiErrorResponse expectedResult) {
        val customerData = registerNewFundedCustomerAndGetOnesData(Double.valueOf(initialFiatAmount));

        val paymentData = postPaymentRequest(CreatePaymentRequestRequestModel
                .builder()
                .currency(AED_CURRENCY)
                .customerId(customerId)
                .fiatAmount(fiatAmount.toString())
                .locationId(externalLocationId)
                .partnerId(partnerData.getId())
                .paymentInfo(SOME_PAYMENT_INFO)
                .tokensAmount(tokensAmount.toString())
                .totalFiatAmount(totalFiatAmount.toString())
                .build(), partnerToken)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(CreatePaymentRequestResponseModel.class);

        // needs customer's confirmation via Customer-Api
        val actualResult = approvePayment(ApprovePartnerPaymentRequest
                .builder()
                .paymentRequestId(paymentData.getPaymentRequestId())
                .sendingAmount(getSendingAmountExternal(tokensAmount, fiatAmount).toString())
                .build(), customerToken)
                .then()
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .extract()
                .as(PartnerApiErrorResponse.class);

        assertEquals(expectedResult, actualResult);
    }

    private CustomerBalanceInfo registerNewFundedCustomerAndGetOnesData(Double initialAmount) {
        val customerData = PrivateBlockchainFacadeUtils.createCustomerFundedViaBonusReward(initialAmount);
        customerId = customerData.getCustomerId();
        customerToken = getUserToken(customerData.getEmail(), customerData.getPassword());
        return customerData;
    }

    @AllArgsConstructor
    @Builder
    @Data
    @NoArgsConstructor
    @JsonNaming(LowerCaseStrategy.class)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PartnerApiValidationErrorResponse {

        private String error;
        private String message;
    }
}
