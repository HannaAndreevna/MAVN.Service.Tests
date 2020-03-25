package com.lykke.tests.api.service.partnersintegration;

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
import static com.lykke.tests.api.service.customer.PartnersPaymentsUtils.rejectPayment;
import static com.lykke.tests.api.service.partnerapi.PartnerApiLogInLogOutTests.USER_INFO;
import static com.lykke.tests.api.service.partnerapi.PartnerApiUtils.getPartnerToken;
import static com.lykke.tests.api.service.partnermanagement.PartnerManagementUtils.createDefaultPartner;
import static com.lykke.tests.api.service.partnermanagement.PartnerManagementUtils.getLocationExternalId;
import static com.lykke.tests.api.service.partnersintegration.PartnersCommonUtils.getFiatAmountInternal;
import static com.lykke.tests.api.service.partnersintegration.PartnersCommonUtils.getSendingAmountInternal;
import static com.lykke.tests.api.service.partnersintegration.PartnersCommonUtils.getTokensAmountInternal;
import static com.lykke.tests.api.service.partnersintegration.PartnersIntegrationUtils.deletePaymentRequest;
import static com.lykke.tests.api.service.partnersintegration.PartnersIntegrationUtils.executePaymentRequest;
import static com.lykke.tests.api.service.partnersintegration.PartnersIntegrationUtils.getPaymentRequest;
import static com.lykke.tests.api.service.partnersintegration.PartnersIntegrationUtils.postPaymentRequest;
import static com.lykke.tests.api.service.partnerspayments.PartnersPaymentsUtils.postPartnerApproval;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.apache.http.HttpStatus.SC_NO_CONTENT;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.params.provider.Arguments.of;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy.LowerCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.github.javafaker.Faker;
import com.lykke.api.testing.annotations.UserStoryId;
import com.lykke.tests.api.base.BaseApiTest;
import com.lykke.tests.api.service.customer.model.partnerspayments.ApprovePartnerPaymentRequest;
import com.lykke.tests.api.service.customer.model.partnerspayments.RejectPartnerPaymentRequest;
import com.lykke.tests.api.service.partnermanagement.model.PartnerCreateResponse;
import com.lykke.tests.api.service.partnersintegration.PartnersIntegrationUtils.ValidationErrorResponse;
import com.lykke.tests.api.service.partnersintegration.model.PaymentCreateStatus;
import com.lykke.tests.api.service.partnersintegration.model.PaymentExecuteStatus;
import com.lykke.tests.api.service.partnersintegration.model.PaymentRequestStatus;
import com.lykke.tests.api.service.partnersintegration.model.PaymentRequestStatusRequestModel;
import com.lykke.tests.api.service.partnersintegration.model.PaymentRequestStatusResponseModel;
import com.lykke.tests.api.service.partnersintegration.model.PaymentsCreateRequestModel;
import com.lykke.tests.api.service.partnersintegration.model.PaymentsCreateResponseModel;
import com.lykke.tests.api.service.partnersintegration.model.PaymentsExecuteRequestModel;
import com.lykke.tests.api.service.partnersintegration.model.PaymentsExecuteResponseModel;
import com.lykke.tests.api.service.partnerspayments.model.ReceptionistProcessPaymentRequest;
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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class SpendingTests extends BaseApiTest {

    private static final int FIAT_AMOUNT_01 = 10;
    private static final int FIAT_AMOUNT_02 = 100;
    private static final int FIAT_AMOUNT_03 = 1_000_000;
    private static final int FIAT_AMOUNT_04 = 1_900_000;
    private static final int TOTAL_FIAT_AMOUNT_01 = 20;
    private static final int TOTAL_FIAT_AMOUNT_02 = 200;
    private static final int TOTAL_FIAT_AMOUNT_03 = 2_000_000;
    private static final int TOTAL_FIAT_AMOUNT_04 = 4_000_000;
    private static final int INITIAL_FIAT_AMOUNT_01 = FIAT_AMOUNT_01 * 5;
    private static final int INITIAL_FIAT_AMOUNT_02 = FIAT_AMOUNT_02 * 5;
    private static final int INITIAL_FIAT_AMOUNT_03 = FIAT_AMOUNT_03 * 5;
    private static final int INITIAL_FIAT_AMOUNT_04 = FIAT_AMOUNT_04 * 5;
    private static final String SOME_PAYMENT_INFO = "my payment";
    private static final Double TOKENS_AMOUNT_01 = 15.0;
    private static final Double TOKENS_AMOUNT_02 = 150.0;
    private static final Double TOKENS_AMOUNT_03 = 150_000.0;
    private static final Double TOKENS_AMOUNT_04 = 300_000.0;
    private static final Double ZERO_TOKENS_AMOUNT = 0.0;
    private static final int ZERO_FIAT_AMOUNT = 0;

    private static final String EMPTY_FIELD_PATH = "\"\"[0]";
    private static final String FIAT_AMOUNT_FIELD_PATH = "FiatAmount[0]";
    private static final String TOKENS_AMOUNT_FIELD_PATH = "TokensAmount[0]";
    private static final String CURRENCY_FIELD_PATH = "Currency[0]";
    private static final String CUSTOMER_ID_FIELD_PATH = "CustomerId[0]";

    private static final String CUSTOMER_ID_MUST_NOT_BE_EMPTY_ERROR_MESSAGE = "'Customer Id' must not be empty.";
    private static final String FIAT_OR_TOKEN_AMOUNT_REQUIRED_ERROR_MESSAGE = "Fiat or Token amount required";
    private static final String FIAT_AMOUNT_MUST_BE_A_POSITIVE_NUMBER_ERROR_MESSAGE = "FiatAmount must be a positive number";
    private static final String TOKENS_AMOUNT_MUST_BE_A_POSITIVE_NUMBER_ERROR_MESSAGE = "TokensAmount must be a positive number";
    private static final String CURRENCY_MUST_NOT_BE_EMPTY_ERROR_MESSAGE = "'Currency' must not be empty.";
    private static final String PAYMENT_IS_NOT_IN_A_CORRECT_STATUS_TO_BE_UPDATED_ERROR = "PaymentIsNotInACorrectStatusToBeUpdated";
    private static final String THE_PAYMENT_REQUEST_IS_NOT_IN_A_CORRECT_STATUS_TO_BE_UPDATED_ERROR_MESSAGE = "The payment request is not in a correct status to be updated";
    private static final String THE_EXTERNAL_LOCATION_ID_FIELD_IS_REQUIRED = "The ExternalLocationId field is required.";
    private static final String EXTERNAL_LOCATION_ID_MUST_NOT_BE_EMPTY = "'External Location Id' must not be empty.";

    private String partnerId;
    private String partnerPassword;
    private String partnerToken;
    private String customerToken;
    private String externalLocationId;
    private PartnerCreateResponse partnerData;

    static Stream<Arguments> getAmountData() {
        return Stream.of(
                of("zero tokens amount",
                        Arrays.asList((Consumer<PaymentsCreateRequestModel>) x -> x.setFiatAmount(FIAT_AMOUNT_01),
                                (Consumer<PaymentsCreateRequestModel>) x -> x
                                        .setTotalFiatAmount(TOTAL_FIAT_AMOUNT_01),
                                (Consumer<PaymentsCreateRequestModel>) x -> x
                                        .setTokensAmount(ZERO_TOKENS_AMOUNT.toString())),
                        PaymentsCreateResponseModel
                                .builder()
                                .status(PaymentCreateStatus.OK)
                                .build()),
                of("zero fiat amount",
                        Arrays.asList((Consumer<PaymentsCreateRequestModel>) x -> x.setFiatAmount(ZERO_FIAT_AMOUNT),
                                (Consumer<PaymentsCreateRequestModel>) x -> x
                                        .setTotalFiatAmount(TOTAL_FIAT_AMOUNT_01),
                                (Consumer<PaymentsCreateRequestModel>) x -> x
                                        .setTokensAmount(TOKENS_AMOUNT_01.toString())),
                        PaymentsCreateResponseModel
                                .builder()
                                .status(PaymentCreateStatus.OK)
                                .build()),
                of("CannotPassBothFiatAndTokensAmount",
                        Arrays.asList((Consumer<PaymentsCreateRequestModel>) x -> x.setFiatAmount(FIAT_AMOUNT_01),
                                (Consumer<PaymentsCreateRequestModel>) x -> x
                                        .setTotalFiatAmount(TOTAL_FIAT_AMOUNT_01),
                                (Consumer<PaymentsCreateRequestModel>) x -> x
                                        .setTokensAmount(TOKENS_AMOUNT_01.toString())),
                        PaymentsCreateResponseModel
                                .builder()
                                .status(PaymentCreateStatus.CANNOT_PASS_BOTH_FIAT_AND_TOKENS_AMOUNT)
                                .build()),
                of("no tokens amount",
                        Arrays.asList((Consumer<PaymentsCreateRequestModel>) x -> x.setFiatAmount(FIAT_AMOUNT_01),
                                (Consumer<PaymentsCreateRequestModel>) x -> x
                                        .setTotalFiatAmount(TOTAL_FIAT_AMOUNT_01)),
                        PaymentsCreateResponseModel
                                .builder()
                                .status(PaymentCreateStatus.OK)
                                .build()),
                of("no fiat amount",
                        Arrays.asList((Consumer<PaymentsCreateRequestModel>) x -> x
                                        .setTokensAmount(TOKENS_AMOUNT_01.toString()),
                                (Consumer<PaymentsCreateRequestModel>) x -> x
                                        .setTotalFiatAmount(TOTAL_FIAT_AMOUNT_01)),
                        PaymentsCreateResponseModel
                                .builder()
                                .status(PaymentCreateStatus.OK)
                                .build())
        );
    }

    static Stream<Arguments> getInvalidInputDataForPostingPayment() {
        return Stream.of(
                of("no fiat or tokens",
                        Arrays.asList((Consumer<PaymentsCreateRequestModel>) x -> x.setFiatAmount(ZERO_FIAT_AMOUNT),
                                (Consumer<PaymentsCreateRequestModel>) x -> x
                                        .setTotalFiatAmount(TOTAL_FIAT_AMOUNT_01),
                                (Consumer<PaymentsCreateRequestModel>) x -> x
                                        .setTokensAmount(ZERO_TOKENS_AMOUNT.toString())),
                        (Consumer<ValidatableResponse>) (response -> response
                                .body(EMPTY_FIELD_PATH, containsString(
                                        FIAT_OR_TOKEN_AMOUNT_REQUIRED_ERROR_MESSAGE))
                                .body(FIAT_AMOUNT_FIELD_PATH,
                                        containsString(FIAT_AMOUNT_MUST_BE_A_POSITIVE_NUMBER_ERROR_MESSAGE))
                                .body(TOKENS_AMOUNT_FIELD_PATH,
                                        containsString(TOKENS_AMOUNT_MUST_BE_A_POSITIVE_NUMBER_ERROR_MESSAGE)))),
                of("no fiat,tokens, currency, customer id",
                        Arrays.asList((Consumer<PaymentsCreateRequestModel>) x -> x.setFiatAmount(ZERO_FIAT_AMOUNT),
                                (Consumer<PaymentsCreateRequestModel>) x -> x
                                        .setTotalFiatAmount(TOTAL_FIAT_AMOUNT_01),
                                (Consumer<PaymentsCreateRequestModel>) x -> x
                                        .setTokensAmount(ZERO_TOKENS_AMOUNT.toString()),
                                (Consumer<PaymentsCreateRequestModel>) x -> x.setCurrency(EMPTY),
                                (Consumer<PaymentsCreateRequestModel>) x -> x.setCustomerId(EMPTY)),
                        (Consumer<ValidatableResponse>) (response -> response
                                .body(EMPTY_FIELD_PATH, containsString(
                                        FIAT_OR_TOKEN_AMOUNT_REQUIRED_ERROR_MESSAGE))
                                .body(FIAT_AMOUNT_FIELD_PATH,
                                        containsString(FIAT_AMOUNT_MUST_BE_A_POSITIVE_NUMBER_ERROR_MESSAGE))
                                .body(TOKENS_AMOUNT_FIELD_PATH,
                                        containsString(TOKENS_AMOUNT_MUST_BE_A_POSITIVE_NUMBER_ERROR_MESSAGE))
                                .body(CURRENCY_FIELD_PATH, containsString(CURRENCY_MUST_NOT_BE_EMPTY_ERROR_MESSAGE))
                                .body(CUSTOMER_ID_FIELD_PATH,
                                        containsString(CUSTOMER_ID_MUST_NOT_BE_EMPTY_ERROR_MESSAGE))))
        );
    }

    static Stream<Arguments> getDifferentAmountsLongListOf() {
        return Stream.of(
                of(INITIAL_FIAT_AMOUNT_01, ZERO_FIAT_AMOUNT, TOKENS_AMOUNT_01, TOTAL_FIAT_AMOUNT_01),
                of(INITIAL_FIAT_AMOUNT_02, FIAT_AMOUNT_02, ZERO_TOKENS_AMOUNT, TOTAL_FIAT_AMOUNT_02),
                of(INITIAL_FIAT_AMOUNT_03, ZERO_FIAT_AMOUNT, TOKENS_AMOUNT_03, TOTAL_FIAT_AMOUNT_03),
                of(INITIAL_FIAT_AMOUNT_04, FIAT_AMOUNT_04, ZERO_TOKENS_AMOUNT, TOTAL_FIAT_AMOUNT_04)
        );
    }

    static Stream<Arguments> getDifferentAmountsShortListOf() {
        return Stream.of(
                of(INITIAL_FIAT_AMOUNT_01, ZERO_FIAT_AMOUNT, TOKENS_AMOUNT_01, TOTAL_FIAT_AMOUNT_01),
                of(INITIAL_FIAT_AMOUNT_04, FIAT_AMOUNT_04, ZERO_TOKENS_AMOUNT, TOTAL_FIAT_AMOUNT_04)
        );
    }

    static Stream<Arguments> getSomeAmountForPaymentExpiration() {
        return Stream.of(
                of(INITIAL_FIAT_AMOUNT_01, ZERO_FIAT_AMOUNT, TOKENS_AMOUNT_01, TOTAL_FIAT_AMOUNT_01)
        );
    }

    static Stream<Arguments> getSomeDataForValidation() {
        return Stream.of(
                of("null",
                        Arrays.asList((Consumer<PaymentsCreateRequestModel>) x -> x.setFiatAmount(FIAT_AMOUNT_01),
                                (Consumer<PaymentsCreateRequestModel>) x -> x
                                        .setTotalFiatAmount(TOTAL_FIAT_AMOUNT_01),
                                (Consumer<PaymentsCreateRequestModel>) x -> x
                                        .setExternalLocationId(null),
                                (Consumer<PaymentsCreateRequestModel>) x -> x
                                        .setTokensAmount(ZERO_TOKENS_AMOUNT.toString())),
                        ValidationErrorResponse
                                .builder()
                                // TODO: needs THE_EXTERNAL_LOCATION_ID_FIELD_IS_REQUIRED
                                .externalLocationId(new String[]{EXTERNAL_LOCATION_ID_MUST_NOT_BE_EMPTY})
                                .build()),
                of("empty",
                        Arrays.asList((Consumer<PaymentsCreateRequestModel>) x -> x.setFiatAmount(FIAT_AMOUNT_01),
                                (Consumer<PaymentsCreateRequestModel>) x -> x
                                        .setTotalFiatAmount(TOTAL_FIAT_AMOUNT_01),
                                (Consumer<PaymentsCreateRequestModel>) x -> x
                                        .setExternalLocationId(EMPTY),
                                (Consumer<PaymentsCreateRequestModel>) x -> x
                                        .setTokensAmount(ZERO_TOKENS_AMOUNT.toString())),
                        ValidationErrorResponse
                                .builder()
                                .externalLocationId(new String[]{EXTERNAL_LOCATION_ID_MUST_NOT_BE_EMPTY})
                                .build())
        );
    }

    @BeforeEach
    void setUp() {
        partnerPassword = generateValidPassword();
        partnerId = getRandomUuid();
        partnerData = createDefaultPartner(partnerId, partnerPassword, generateRandomString(10),
                generateRandomString(10));
        externalLocationId = getLocationExternalId(partnerData);
        partnerToken = getPartnerToken(partnerId, partnerPassword, USER_INFO);
    }

    @ParameterizedTest(name = "Run {index}: {0}")
    @MethodSource("getAmountData")
    @UserStoryId(2372)
    void shouldPostPaymentRequest(String description, List<Consumer<PaymentsCreateRequestModel>> requestModelData,
            PaymentsCreateResponseModel expectedResult) {

        val customerData = PrivateBlockchainFacadeUtils
                .createCustomerFundedViaBonusReward(Double.valueOf(INITIAL_FIAT_AMOUNT_01));

        val requestModel = getObjectWithData(PaymentsCreateRequestModel
                .builder()
                .currency(AED_CURRENCY)
                .customerId(customerData.getCustomerId())
                .externalLocationId(externalLocationId)
                .partnerId(partnerData.getId())
                .paymentInfo(SOME_PAYMENT_INFO)
                .build(), requestModelData);

        val actualResult = postPaymentRequest(requestModel, partnerToken)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(PaymentsCreateResponseModel.class);

        assertEquals(expectedResult, actualResult);
    }

    @ParameterizedTest(name = "Run {index}: {0}")
    @MethodSource("getSomeDataForValidation")
    @UserStoryId(4098)
    void shouldNotPostPaymentRequestWithoutLocationId(String description,
            List<Consumer<PaymentsCreateRequestModel>> requestModelData,
            ValidationErrorResponse expectedResult) {

        val requestModel = getObjectWithData(PaymentsCreateRequestModel
                .builder()
                .currency(AED_CURRENCY)
                .customerId(getRandomUuid())
                .partnerId(partnerData.getId())
                .paymentInfo(SOME_PAYMENT_INFO)
                .build(), requestModelData);

        val actualResult = postPaymentRequest(requestModel, partnerToken)
                .then()
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .extract()
                .as(ValidationErrorResponse.class);

        assertEquals(expectedResult, actualResult);
    }

    @ParameterizedTest(name = "Run {index}: {0}")
    @MethodSource("getInvalidInputDataForPostingPayment")
    @UserStoryId(2372)
    void shouldPostPaymentRequest(String description, List<Consumer<PaymentsCreateRequestModel>> requestModelData,
            Consumer<ValidatableResponse> assertAction) {

        val customerData = PrivateBlockchainFacadeUtils
                .createCustomerFundedViaBonusReward(Double.valueOf(INITIAL_FIAT_AMOUNT_01));

        val requestModel = getObjectWithData(PaymentsCreateRequestModel
                .builder()
                .currency(AED_CURRENCY)
                .customerId(customerData.getCustomerId())
                .externalLocationId(externalLocationId)
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

    @ParameterizedTest(name = "Run {index}: initial fiat amount={0}, fiat amount={1}, tokens amount={2}, total fiat amount={3}")
    @MethodSource("getDifferentAmountsLongListOf")
    @UserStoryId(storyId = {2372, 2354, 2938})
    void shouldGetApprovedPaymentRequest(int initialFiatAmount, int fiatAmount, Double tokensAmount,
            int totalFiatAmount) {

        val customerData = PrivateBlockchainFacadeUtils
                .createCustomerFundedViaBonusReward(Double.valueOf(initialFiatAmount));
        customerToken = getUserToken(customerData.getEmail(), customerData.getPassword());

        val paymentData = postPaymentRequest(PaymentsCreateRequestModel
                .builder()
                .currency(AED_CURRENCY)
                .customerId(customerData.getCustomerId())
                .fiatAmount(fiatAmount)
                .externalLocationId(externalLocationId)
                .partnerId(partnerData.getId())
                .paymentInfo(SOME_PAYMENT_INFO)
                .tokensAmount(tokensAmount.toString())
                .totalFiatAmount(totalFiatAmount)
                .paymentProcessedCallbackUrl(new Faker().internet().url())
                .requestAuthToken(partnerToken)
                .build(), partnerToken)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(PaymentsCreateResponseModel.class);

        // needs customer's confirmation via Customer-Api
        approvePayment(ApprovePartnerPaymentRequest
                .builder()
                .paymentRequestId(paymentData.getPaymentRequestId())
                .sendingAmount(getSendingAmountInternal(tokensAmount, fiatAmount).toString())
                .build(), customerToken)
                .then()
                .assertThat()
                .statusCode(SC_NO_CONTENT);

        Awaitility.await()
                .atMost(Duration.ONE_MINUTE.getValue(), TimeUnit.SECONDS)
                .pollInterval(Duration.FIVE_SECONDS)
                .until(() -> PaymentRequestStatus.PENDING_PARTNER_CONFIRMATION == getPaymentRequest(
                        PaymentRequestStatusRequestModel
                                .builder()
                                .partnerId(partnerData.getId())
                                .paymentRequestId(paymentData.getPaymentRequestId())
                                .build(), partnerToken)
                        .then()
                        .assertThat()
                        .statusCode(SC_OK)
                        .extract()
                        .as(PaymentRequestStatusResponseModel.class).getStatus()
                );

        val expectedResult = PaymentRequestStatusResponseModel
                .builder()
                .status(PaymentRequestStatus.PENDING_PARTNER_CONFIRMATION)
                .totalFiatAmount(totalFiatAmount)
                .fiatAmount(getFiatAmountInternal(tokensAmount, fiatAmount))
                .fiatCurrency(AED_CURRENCY)
                .tokensAmount(getTokensAmountInternal(tokensAmount, fiatAmount).toString())
                .build();

        val actualResult = getPaymentRequest(PaymentRequestStatusRequestModel
                .builder()
                .partnerId(partnerData.getId())
                .paymentRequestId(paymentData.getPaymentRequestId())
                .build(), partnerToken)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(PaymentRequestStatusResponseModel.class);

        assertEquals(expectedResult, actualResult);
    }

    @Disabled("TODO: needs investigation")
    @ParameterizedTest(name = "Run {index}: initial fiat amount={0}, fiat amount={1}, tokens amount={2}, total fiat amount={3}")
    @MethodSource("getDifferentAmountsShortListOf")
    @Tag(SMOKE_TEST)
    @UserStoryId(storyId = {2372, 2354})
    void shouldGetRejectedPaymentRequest(int initialFiatAmount, int fiatAmount, Double tokensAmount,
            int totalFiatAmount) {

        val customerData = PrivateBlockchainFacadeUtils
                .createCustomerFundedViaBonusReward(Double.valueOf(initialFiatAmount));
        customerToken = getUserToken(customerData.getEmail(), customerData.getPassword());

        val paymentData = postPaymentRequest(PaymentsCreateRequestModel
                .builder()
                .currency(AED_CURRENCY)
                .customerId(customerData.getCustomerId())
                .fiatAmount(fiatAmount)
                .externalLocationId(externalLocationId)
                .partnerId(partnerData.getId())
                .paymentInfo(SOME_PAYMENT_INFO)
                .tokensAmount(tokensAmount.toString())
                .totalFiatAmount(totalFiatAmount)
                .build(), partnerToken)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(PaymentsCreateResponseModel.class);

        // needs customer's confirmation via Customer-Api
        rejectPayment(RejectPartnerPaymentRequest
                .builder()
                .paymentRequestId(paymentData.getPaymentRequestId())
                .build(), customerToken)
                .then()
                .assertThat()
                .statusCode(SC_NO_CONTENT);

        Awaitility.await()
                .atMost(Duration.ONE_MINUTE.getValue(), TimeUnit.SECONDS)
                .pollInterval(Duration.FIVE_SECONDS)
                .until(() -> PaymentRequestStatus.REJECTED_BY_CUSTOMER == getPaymentRequest(
                        PaymentRequestStatusRequestModel
                                .builder()
                                .partnerId(partnerData.getId())
                                .paymentRequestId(paymentData.getPaymentRequestId())
                                .build(), partnerToken)
                        .then()
                        .assertThat()
                        .statusCode(SC_OK)
                        .extract()
                        .as(PaymentRequestStatusResponseModel.class).getStatus()
                );

        val expectedResult = PaymentRequestStatusResponseModel
                .builder()
                .status(PaymentRequestStatus.REJECTED_BY_CUSTOMER)
                .totalFiatAmount(totalFiatAmount)
                .fiatAmount(getFiatAmountInternal(tokensAmount, fiatAmount))
                .fiatCurrency(AED_CURRENCY)
                .tokensAmount(getTokensAmountInternal(tokensAmount, fiatAmount).toString())
                .build();

        val actualResult = getPaymentRequest(PaymentRequestStatusRequestModel
                .builder()
                .partnerId(partnerData.getId())
                .paymentRequestId(paymentData.getPaymentRequestId())
                .build(), partnerToken)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(PaymentRequestStatusResponseModel.class);

        assertAll(
                () -> assertEquals(expectedResult.getStatus(), actualResult.getStatus()),
                () -> assertEquals(expectedResult.getTotalFiatAmount(), actualResult.getTotalFiatAmount()),
                () -> assertEquals(expectedResult.getFiatAmount(), actualResult.getFiatAmount()),
                () -> assertEquals(expectedResult.getFiatCurrency(), actualResult.getFiatCurrency())
                // TODO: investigate into calculation of tokens amount
                // , () -> assertEquals(expectedResult.getTokensAmount(), actualResult.getTokensAmount())
        );
    }

    @ParameterizedTest(name = "Run {index}: initial fiat amount={0}, fiat amount={1}, tokens amount={2}, total fiat amount={3}")
    @MethodSource("getDifferentAmountsShortListOf")
    @UserStoryId(2372)
    void shouldDeletePaymentRequest(int initialFiatAmount, int fiatAmount, Double tokensAmount, int totalFiatAmount) {

        val customerData = PrivateBlockchainFacadeUtils
                .createCustomerFundedViaBonusReward(Double.valueOf(initialFiatAmount));
        customerToken = getUserToken(customerData.getEmail(), customerData.getPassword());

        val paymentData = postPaymentRequest(PaymentsCreateRequestModel
                .builder()
                .currency(AED_CURRENCY)
                .customerId(customerData.getCustomerId())
                .fiatAmount(fiatAmount)
                .externalLocationId(externalLocationId)
                .partnerId(partnerData.getId())
                .paymentInfo(SOME_PAYMENT_INFO)
                .tokensAmount(tokensAmount.toString())
                .totalFiatAmount(totalFiatAmount)
                .build(), partnerToken)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(PaymentsCreateResponseModel.class);

        // needs customer's confirmation Customer-Api
        approvePayment(ApprovePartnerPaymentRequest
                .builder()
                .paymentRequestId(paymentData.getPaymentRequestId())
                .sendingAmount(getSendingAmountInternal(tokensAmount, fiatAmount).toString())
                .build(), customerToken)
                .then()
                .assertThat()
                .statusCode(SC_NO_CONTENT);

        Awaitility.await()
                .atMost(Duration.ONE_MINUTE.getValue(), TimeUnit.SECONDS)
                .pollInterval(Duration.FIVE_SECONDS)
                .until(() -> PaymentRequestStatus.PENDING_PARTNER_CONFIRMATION == getPaymentRequest(
                        PaymentRequestStatusRequestModel
                                .builder()
                                .partnerId(partnerData.getId())
                                .paymentRequestId(paymentData.getPaymentRequestId())
                                .build(), partnerToken)
                        .then()
                        .assertThat()
                        .statusCode(SC_OK)
                        .extract()
                        .as(PaymentRequestStatusResponseModel.class).getStatus()
                );

        deletePaymentRequest(PaymentRequestStatusRequestModel
                .builder()
                .partnerId(partnerData.getId())
                .paymentRequestId(paymentData.getPaymentRequestId())
                .build(), partnerToken)
                .then()
                .assertThat()
                .statusCode(SC_OK);

        // why the payment is still in the PaymentExecuted state even after deletion and five minutes' wait?
        val actualResult = getPaymentRequest(PaymentRequestStatusRequestModel
                .builder()
                .partnerId(partnerData.getId())
                .paymentRequestId(paymentData.getPaymentRequestId())
                .build(), partnerToken)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(PaymentRequestStatusResponseModel.class);

        assertEquals(PaymentRequestStatus.PENDING_PARTNER_CONFIRMATION, actualResult.getStatus());
    }

    @ParameterizedTest(name = "Run {index}: initial fiat amount={0}, fiat amount={1}, tokens amount={2}, total fiat amount={3}")
    @MethodSource("getDifferentAmountsShortListOf")
    @UserStoryId(3325)
    void shouldExecutePaymentRequest1111(int initialFiatAmount, int fiatAmount, Double tokensAmount,
            int totalFiatAmount) {

        val customerData = PrivateBlockchainFacadeUtils
                .createCustomerFundedViaBonusReward(Double.valueOf(initialFiatAmount));
        customerToken = getUserToken(customerData.getEmail(), customerData.getPassword());

        val paymentData = postPaymentRequest(PaymentsCreateRequestModel
                .builder()
                .currency(AED_CURRENCY)
                .customerId(customerData.getCustomerId())
                .fiatAmount(fiatAmount)
                .externalLocationId(externalLocationId)
                .partnerId(partnerData.getId())
                .paymentInfo(SOME_PAYMENT_INFO)
                .tokensAmount(tokensAmount.toString())
                .totalFiatAmount(totalFiatAmount)
                .build(), partnerToken)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(PaymentsCreateResponseModel.class);

        // needs customer's confirmation Customer-Api
        approvePayment(ApprovePartnerPaymentRequest
                .builder()
                .paymentRequestId(paymentData.getPaymentRequestId())
                .sendingAmount(getSendingAmountInternal(tokensAmount, fiatAmount).toString())
                .build(), customerToken)
                .then()
                .assertThat()
                .statusCode(SC_NO_CONTENT);

        Awaitility.await()
                .atMost(Duration.ONE_MINUTE.getValue(), TimeUnit.SECONDS)
                .pollInterval(Duration.FIVE_SECONDS)
                .until(() -> PaymentRequestStatus.PENDING_PARTNER_CONFIRMATION == getPaymentRequest(
                        PaymentRequestStatusRequestModel
                                .builder()
                                .partnerId(partnerData.getId())
                                .paymentRequestId(paymentData.getPaymentRequestId())
                                .build(), partnerToken)
                        .then()
                        .assertThat()
                        .statusCode(SC_OK)
                        .extract()
                        .as(PaymentRequestStatusResponseModel.class).getStatus()
                );

        val expectedResult = PaymentsExecuteResponseModel
                .builder()
                .status(PaymentExecuteStatus.OK)
                .customerId(customerData.getCustomerId())
                .tokensAmount(getTokensAmountInternal(tokensAmount, fiatAmount).toString())
                .fiatAmount(getFiatAmountInternal(tokensAmount, fiatAmount))
                .currency(AED_CURRENCY)
                .build();

        val actualResult = executePaymentRequest(PaymentsExecuteRequestModel
                .builder()
                .partnerId(partnerData.getId())
                .paymentRequestId(paymentData.getPaymentRequestId())
                .build(), partnerToken)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(PaymentsExecuteResponseModel.class);

        assertEquals(expectedResult, actualResult);
    }

    @ParameterizedTest(name = "Run {index}: initial fiat amount={0}, fiat amount={1}, tokens amount={2}, total fiat amount={3}")
    @MethodSource("getDifferentAmountsShortListOf")
    @UserStoryId(2372)
    void shouldNotDeletePaymentRequestApprovedByPartner(int initialFiatAmount, int fiatAmount, Double tokensAmount,
            int totalFiatAmount) {

        val customerData = PrivateBlockchainFacadeUtils
                .createCustomerFundedViaBonusReward(Double.valueOf(initialFiatAmount));
        customerToken = getUserToken(customerData.getEmail(), customerData.getPassword());

        val paymentData = postPaymentRequest(PaymentsCreateRequestModel
                .builder()
                .currency(AED_CURRENCY)
                .customerId(customerData.getCustomerId())
                .fiatAmount(fiatAmount)
                .externalLocationId(externalLocationId)
                .partnerId(partnerData.getId())
                .paymentInfo(SOME_PAYMENT_INFO)
                .tokensAmount(tokensAmount.toString())
                .totalFiatAmount(totalFiatAmount)
                .build(), partnerToken)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(PaymentsCreateResponseModel.class);

        // needs customer's confirmation Customer-Api
        approvePayment(ApprovePartnerPaymentRequest
                .builder()
                .paymentRequestId(paymentData.getPaymentRequestId())
                .sendingAmount(getSendingAmountInternal(tokensAmount, fiatAmount).toString())
                .build(), customerToken)
                .then()
                .assertThat()
                .statusCode(SC_NO_CONTENT);

        Awaitility.await()
                .atMost(Duration.ONE_MINUTE.getValue(), TimeUnit.SECONDS)
                .pollInterval(Duration.FIVE_SECONDS)
                .until(() -> PaymentRequestStatus.PENDING_PARTNER_CONFIRMATION == getPaymentRequest(
                        PaymentRequestStatusRequestModel
                                .builder()
                                .partnerId(partnerData.getId())
                                .paymentRequestId(paymentData.getPaymentRequestId())
                                .build(), partnerToken)
                        .then()
                        .assertThat()
                        .statusCode(SC_OK)
                        .extract()
                        .as(PaymentRequestStatusResponseModel.class).getStatus()
                );

        // needs receptionist's confirmation
        postPartnerApproval(ReceptionistProcessPaymentRequest
                .builder()
                .paymentRequestId(paymentData.getPaymentRequestId())
                .build(), partnerToken)
                .then()
                .assertThat()
                .statusCode(SC_OK);

        Awaitility.await()
                .atMost(Duration.ONE_MINUTE.getValue(), TimeUnit.SECONDS)
                .pollInterval(Duration.FIVE_SECONDS)
                .until(() -> PaymentRequestStatus.PAYMENT_EXECUTED == getPaymentRequest(
                        PaymentRequestStatusRequestModel
                                .builder()
                                .partnerId(partnerData.getId())
                                .paymentRequestId(paymentData.getPaymentRequestId())
                                .build(), partnerToken)
                        .then()
                        .assertThat()
                        .statusCode(SC_OK)
                        .extract()
                        .as(PaymentRequestStatusResponseModel.class).getStatus()
                );

        deletePaymentRequest(PaymentRequestStatusRequestModel
                .builder()
                .partnerId(partnerData.getId())
                .paymentRequestId(paymentData.getPaymentRequestId())
                .build(), partnerToken)
                .then()
                .assertThat()
                .statusCode(SC_OK);

        val expectedResult = PaymentRequestStatusResponseModel
                .builder()
                .status(PaymentRequestStatus.PAYMENT_EXECUTED)
                .totalFiatAmount(totalFiatAmount)
                .fiatAmount(getFiatAmountInternal(tokensAmount, fiatAmount))
                .fiatCurrency(AED_CURRENCY)
                .tokensAmount(getTokensAmountInternal(tokensAmount, fiatAmount).toString())
                .build();

        val actualResult = getPaymentRequest(PaymentRequestStatusRequestModel
                .builder()
                .partnerId(partnerData.getId())
                .paymentRequestId(paymentData.getPaymentRequestId())
                .build(), partnerToken)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(PaymentRequestStatusResponseModel.class);

        assertEquals(expectedResult, actualResult);
    }

    @ParameterizedTest(name = "Run {index}: initial fiat amount={0}, fiat amount={1}, tokens amount={2}, total fiat amount={3}")
    @MethodSource("getDifferentAmountsShortListOf")
    @UserStoryId(2372)
    void shouldExecutePaymentRequest(int initialFiatAmount, int fiatAmount, Double tokensAmount,
            int totalFiatAmount) {

        val customerData = PrivateBlockchainFacadeUtils
                .createCustomerFundedViaBonusReward(Double.valueOf(initialFiatAmount));
        customerToken = getUserToken(customerData.getEmail(), customerData.getPassword());

        val paymentData = postPaymentRequest(PaymentsCreateRequestModel
                .builder()
                .currency(AED_CURRENCY)
                .customerId(customerData.getCustomerId())
                .fiatAmount(fiatAmount)
                .externalLocationId(externalLocationId)
                .partnerId(partnerData.getId())
                .paymentInfo(SOME_PAYMENT_INFO)
                .tokensAmount(tokensAmount.toString())
                .totalFiatAmount(totalFiatAmount)
                .build(), partnerToken)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(PaymentsCreateResponseModel.class);

        // needs customer's confirmation Customer-Api
        approvePayment(ApprovePartnerPaymentRequest
                .builder()
                .paymentRequestId(paymentData.getPaymentRequestId())
                .sendingAmount(getSendingAmountInternal(tokensAmount, fiatAmount).toString())
                .build(), customerToken)
                .then()
                .assertThat()
                .statusCode(SC_NO_CONTENT);

        Awaitility.await()
                .atMost(Duration.ONE_MINUTE.getValue(), TimeUnit.SECONDS)
                .pollInterval(Duration.FIVE_SECONDS)
                .until(() -> PaymentRequestStatus.PENDING_PARTNER_CONFIRMATION == getPaymentRequest(
                        PaymentRequestStatusRequestModel
                                .builder()
                                .partnerId(partnerData.getId())
                                .paymentRequestId(paymentData.getPaymentRequestId())
                                .build(), partnerToken)
                        .then()
                        .assertThat()
                        .statusCode(SC_OK)
                        .extract()
                        .as(PaymentRequestStatusResponseModel.class).getStatus()
                );

        val expectedResult = PaymentsExecuteResponseModel
                .builder()
                .status(PaymentExecuteStatus.OK)
                .customerId(customerData.getCustomerId())
                .tokensAmount(getTokensAmountInternal(tokensAmount, fiatAmount).toString())
                .fiatAmount(getFiatAmountInternal(tokensAmount, fiatAmount))
                .currency(AED_CURRENCY)
                .build();

        val actualResult = executePaymentRequest(PaymentsExecuteRequestModel
                .builder()
                .partnerId(partnerData.getId())
                .paymentRequestId(paymentData.getPaymentRequestId())
                .build(), partnerToken)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(PaymentsExecuteResponseModel.class);

        assertEquals(expectedResult, actualResult);
    }

    @ParameterizedTest(name = "Run {index}: initial fiat amount={0}, fiat amount={1}, tokens amount={2}, total fiat amount={3}")
    @MethodSource("getSomeAmountForPaymentExpiration")
    @UserStoryId(3321)
    void shouldExecutePaymentRequestWithExpirationOn(int initialFiatAmount, int fiatAmount, Double tokensAmount,
            int totalFiatAmount) {
        val expirationSeconds = 2;
        val customerData = PrivateBlockchainFacadeUtils
                .createCustomerFundedViaBonusReward(Double.valueOf(initialFiatAmount));
        customerToken = getUserToken(customerData.getEmail(), customerData.getPassword());

        val paymentData = postPaymentRequest(PaymentsCreateRequestModel
                .builder()
                .currency(AED_CURRENCY)
                .customerId(customerData.getCustomerId())
                .fiatAmount(fiatAmount)
                .externalLocationId(externalLocationId)
                .partnerId(partnerData.getId())
                .paymentInfo(SOME_PAYMENT_INFO)
                .tokensAmount(tokensAmount.toString())
                .totalFiatAmount(totalFiatAmount)
                .expirationTimeoutInSeconds(expirationSeconds)
                .build(), partnerToken)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(PaymentsCreateResponseModel.class);

        waitForExpiration(expirationSeconds);

        // needs customer's confirmation Customer-Api
        val actualResult = approvePayment(ApprovePartnerPaymentRequest
                .builder()
                .paymentRequestId(paymentData.getPaymentRequestId())
                .sendingAmount(getSendingAmountInternal(tokensAmount, fiatAmount).toString())
                .build(), customerToken)
                .then()
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .extract()
                .as(PartnersIntegrationValidationErrorResponse.class);

        val expectedResult = PartnersIntegrationValidationErrorResponse
                .builder()
                .error(PAYMENT_IS_NOT_IN_A_CORRECT_STATUS_TO_BE_UPDATED_ERROR)
                .message(THE_PAYMENT_REQUEST_IS_NOT_IN_A_CORRECT_STATUS_TO_BE_UPDATED_ERROR_MESSAGE)
                .build();

        assertEquals(expectedResult, actualResult);
    }

    @ParameterizedTest(name = "Run {index}: initial fiat amount={0}, fiat amount={1}, tokens amount={2}, total fiat amount={3}")
    @MethodSource("getSomeAmountForPaymentExpiration")
    @UserStoryId(3321)
    void shouldExecutePaymentRequestWithExpirationOff(int initialFiatAmount, int fiatAmount, Double tokensAmount,
            int totalFiatAmount) {

        val expirationSeconds = 0;
        val customerData = PrivateBlockchainFacadeUtils
                .createCustomerFundedViaBonusReward(Double.valueOf(initialFiatAmount));
        customerToken = getUserToken(customerData.getEmail(), customerData.getPassword());

        val paymentData = postPaymentRequest(PaymentsCreateRequestModel
                .builder()
                .currency(AED_CURRENCY)
                .customerId(customerData.getCustomerId())
                .fiatAmount(fiatAmount)
                .externalLocationId(externalLocationId)
                .partnerId(partnerData.getId())
                .paymentInfo(SOME_PAYMENT_INFO)
                .tokensAmount(tokensAmount.toString())
                .totalFiatAmount(totalFiatAmount)
                .expirationTimeoutInSeconds(expirationSeconds)
                .build(), partnerToken)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(PaymentsCreateResponseModel.class);

        waitForExpiration(expirationSeconds);

        // needs customer's confirmation Customer-Api
        approvePayment(ApprovePartnerPaymentRequest
                .builder()
                .paymentRequestId(paymentData.getPaymentRequestId())
                .sendingAmount(getSendingAmountInternal(tokensAmount, fiatAmount).toString())
                .build(), customerToken)
                .then()
                .assertThat()
                .statusCode(SC_NO_CONTENT);

        Awaitility.await()
                .atMost(Duration.ONE_MINUTE.getValue(), TimeUnit.SECONDS)
                .pollInterval(Duration.FIVE_SECONDS)
                .until(() -> PaymentRequestStatus.PENDING_PARTNER_CONFIRMATION == getPaymentRequest(
                        PaymentRequestStatusRequestModel
                                .builder()
                                .partnerId(partnerData.getId())
                                .paymentRequestId(paymentData.getPaymentRequestId())
                                .build(), partnerToken)
                        .then()
                        .assertThat()
                        .statusCode(SC_OK)
                        .extract()
                        .as(PaymentRequestStatusResponseModel.class).getStatus()
                );

        executePaymentRequest(PaymentsExecuteRequestModel
                .builder()
                .partnerId(partnerData.getId())
                .paymentRequestId(paymentData.getPaymentRequestId())
                .build(), partnerToken)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(PaymentsExecuteResponseModel.class);

        val actualResult = getPaymentRequest(
                PaymentRequestStatusRequestModel
                        .builder()
                        .partnerId(partnerData.getId())
                        .paymentRequestId(paymentData.getPaymentRequestId())
                        .build(), partnerToken)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(PaymentRequestStatusResponseModel.class);

        assertAll(
                () -> assertNotNull(actualResult.getPaymentRequestApprovedTimestamp()),
                // TODO: () -> assertNotNull(actualResult.getPaymentExecutionTimestamp()),
                () -> assertNotNull(actualResult.getPaymentRequestCustomerExpirationTimestamp()),
                () -> assertNotNull(actualResult.getPaymentRequestTimestamp())
        );
    }

    @AllArgsConstructor
    @Builder
    @Data
    @NoArgsConstructor
    @JsonNaming(LowerCaseStrategy.class)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PartnersIntegrationValidationErrorResponse {

        private String error;
        private String message;
    }
}
