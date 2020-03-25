package com.lykke.tests.api.service.customer;

import static com.lykke.api.testing.api.common.GenerateUtils.generateRandomString;
import static com.lykke.api.testing.api.common.GenerateUtils.getRandomUuid;
import static com.lykke.api.testing.api.common.PasswordGen.generateValidPassword;
import static com.lykke.tests.api.common.CommonConsts.Currency.AED_CURRENCY;
import static com.lykke.tests.api.common.CommonConsts.Currency.MVN_CURRENCY;
import static com.lykke.tests.api.common.CommonConsts.Currency.SOME_CURRENCY_RATE;
import static com.lykke.tests.api.common.CommonConsts.Currency.USD_CURRENCY;
import static com.lykke.tests.api.common.CommonMethods.waitForExpiration;
import static com.lykke.tests.api.common.PaginationConts.CURRENT_PAGE_LOWER_BOUNDARY;
import static com.lykke.tests.api.common.PaginationConts.PAGE_SIZE_UPPER_BOUNDARY;
import static com.lykke.tests.api.service.customer.LoginLogoutCustomerUtils.getUserToken;
import static com.lykke.tests.api.service.customer.PartnersPaymentsUtils.approvePayment;
import static com.lykke.tests.api.service.customer.PartnersPaymentsUtils.getFailedPayments;
import static com.lykke.tests.api.service.customer.PartnersPaymentsUtils.getPartnersPayments;
import static com.lykke.tests.api.service.customer.PartnersPaymentsUtils.getPendingPayments;
import static com.lykke.tests.api.service.customer.PartnersPaymentsUtils.getSucceededPayments;
import static com.lykke.tests.api.service.customer.PartnersPaymentsUtils.rejectPayment;
import static com.lykke.tests.api.service.customermanagement.RegisterCustomerUtils.registerDefaultVerifiedCustomer;
import static com.lykke.tests.api.service.partnerapi.PartnerApiLogInLogOutTests.USER_INFO;
import static com.lykke.tests.api.service.partnerapi.PartnerApiUtils.getPartnerToken;
import static com.lykke.tests.api.service.partnermanagement.PartnerManagementUtils.createDefaultPartner;
import static com.lykke.tests.api.service.partnermanagement.PartnerManagementUtils.getLocationId;
import static com.lykke.tests.api.service.partnerspayments.PartnersPaymentsUtils.postPayment;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.apache.http.HttpStatus.SC_NO_CONTENT;
import static org.apache.http.HttpStatus.SC_OK;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.params.provider.Arguments.of;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy.LowerCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.lykke.api.testing.annotations.UserStoryId;
import com.lykke.tests.api.base.BaseApiTest;
import com.lykke.tests.api.service.customer.model.partnerspayments.ApprovePartnerPaymentRequest;
import com.lykke.tests.api.service.customer.model.partnerspayments.GetPartnerPaymentRequestDetailsRequest;
import com.lykke.tests.api.service.customer.model.partnerspayments.PaginatedPartnerPaymentRequestsResponse;
import com.lykke.tests.api.service.customer.model.partnerspayments.PaginatedRequestModel;
import com.lykke.tests.api.service.customer.model.partnerspayments.PartnerPaymentRequestDetailsResponse;
import com.lykke.tests.api.service.customer.model.partnerspayments.RejectPartnerPaymentRequest;
import com.lykke.tests.api.service.partnerspayments.model.PaymentRequestErrorCode;
import com.lykke.tests.api.service.partnerspayments.model.PaymentRequestModel;
import com.lykke.tests.api.service.partnerspayments.model.PaymentRequestResponseModel;
import com.lykke.tests.api.service.partnerspayments.model.PaymentRequestStatus;
import com.lykke.tests.api.service.privateblockchainfacade.PrivateBlockchainFacadeUtils;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class PartnersPaymentsTests extends BaseApiTest {

    private static final String INVALID_PAYMENT_REQUEST_ID = "aaa";
    private static final String MODEL_VALIDATION_FAILED_ERROR_CODE = "ModelValidationFailed";
    private static final String PAYMENT_DOES_NOT_EXIST_ERROR_CODE = "PaymentDoesNotExist";
    private static final String INVALID_AMOUNT_ERROR_CODE = "InvalidAmount";
    private static final String THE_PAYMENT_REQUEST_ID_FIELD_IS_REQUIRED_ERROR_MESSAGE = "The PaymentRequestId field is required.";
    private static final String PAYMENT_REQUEST_WITH_THE_PROVIDED_DETAILS_DOES_NOT_EXIST_IN_THE_SYSTEM_ERROR_MESSAGE = "Payment request with the provided details does not exist in the system";
    private static final String THE_VALUE_FOR_AMOUNT_IS_NOT_VALID_ERROR_MESSAGE = "The value for amount is not valid";
    private static final int SOME_FIAT_AMOUNT = 10;
    private static final Double SOME_TOTAL_FIAT_AMOUNT = 20.0;
    private static final Double SOME_INITIAL_TOKENS_AMOUNT = 200.0;
    private static final int SOME_INITIAL_FIAT_AMOUNT = SOME_FIAT_AMOUNT * 5;
    private static final String SOME_PAYMENT_INFO = "my payment";
    private static final Double SOME_TOKENS_AMOUNT = 5.0;
    private static final String ZERO_TOKENS_AMOUNT = "0.00";
    private static final Double ZERO_FIAT_AMOUNT = 0.0;
    private String customerId;
    private String customerToken;

    static Stream<Arguments> getInvalidPaymentRequestDataForApproval() {
        return Stream.of(
                of(EMPTY, ErrorValidationResponse
                        .builder()
                        .error(MODEL_VALIDATION_FAILED_ERROR_CODE)
                        .message(THE_PAYMENT_REQUEST_ID_FIELD_IS_REQUIRED_ERROR_MESSAGE)
                        .build()),
                of(INVALID_PAYMENT_REQUEST_ID, ErrorValidationResponse
                        .builder()
                        .error(INVALID_AMOUNT_ERROR_CODE)
                        .message(THE_VALUE_FOR_AMOUNT_IS_NOT_VALID_ERROR_MESSAGE)
                        .build()),
                of(getRandomUuid(), ErrorValidationResponse
                        .builder()
                        .error(INVALID_AMOUNT_ERROR_CODE)
                        .message(THE_VALUE_FOR_AMOUNT_IS_NOT_VALID_ERROR_MESSAGE)
                        .build())
        );
    }

    static Stream<Arguments> getInvalidPaymentRequestDataForRejection() {
        return Stream.of(
                of(EMPTY, ErrorValidationResponse
                        .builder()
                        .error(MODEL_VALIDATION_FAILED_ERROR_CODE)
                        .message(THE_PAYMENT_REQUEST_ID_FIELD_IS_REQUIRED_ERROR_MESSAGE)
                        .build()),
                of(INVALID_PAYMENT_REQUEST_ID, ErrorValidationResponse
                        .builder()
                        .error(PAYMENT_DOES_NOT_EXIST_ERROR_CODE)
                        .message(PAYMENT_REQUEST_WITH_THE_PROVIDED_DETAILS_DOES_NOT_EXIST_IN_THE_SYSTEM_ERROR_MESSAGE)
                        .build()),
                of(getRandomUuid(), ErrorValidationResponse
                        .builder()
                        .error(PAYMENT_DOES_NOT_EXIST_ERROR_CODE)
                        .message(PAYMENT_REQUEST_WITH_THE_PROVIDED_DETAILS_DOES_NOT_EXIST_IN_THE_SYSTEM_ERROR_MESSAGE)
                        .build())
        );
    }

    static Stream<Arguments> getInvalidPaymentRequestDataForGettingPayments() {
        return Stream.of(
                of(EMPTY, SC_BAD_REQUEST, ErrorValidationResponse
                        .builder()
                        .error(MODEL_VALIDATION_FAILED_ERROR_CODE)
                        .message(THE_PAYMENT_REQUEST_ID_FIELD_IS_REQUIRED_ERROR_MESSAGE)
                        .build()),
                of(INVALID_PAYMENT_REQUEST_ID, SC_OK, null),
                of(getRandomUuid(), SC_OK, null)
        );
    }

    // TODO: these data are result-based, switch to usage of currency convertor in the future
    static Stream<Arguments> getCurrencyForPayments() {
        return Stream.of(
                of(MVN_CURRENCY, SOME_TOKENS_AMOUNT.toString(), "20.00", ZERO_FIAT_AMOUNT),
                of(USD_CURRENCY, SOME_TOKENS_AMOUNT.toString(), ZERO_TOKENS_AMOUNT, 70.0),
                of(AED_CURRENCY, SOME_TOKENS_AMOUNT.toString(), ZERO_TOKENS_AMOUNT, 20.0)
        );
    }

    @BeforeEach
    void setUp() {
        val customerData = registerDefaultVerifiedCustomer(true);
        customerId = customerData.getCustomerId();
        customerToken = getUserToken(customerData);
    }

    @ParameterizedTest(name = "Run {index}: paymentRequestId={0}")
    @MethodSource("getInvalidPaymentRequestDataForApproval")
    @UserStoryId(2354)
    void shouldApprovePayment(String paymentRequestId, ErrorValidationResponse expectedResult) {
        val actualResult = approvePayment(ApprovePartnerPaymentRequest
                .builder()
                .paymentRequestId(paymentRequestId)
                .build(), customerToken)
                .then()
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .extract()
                .as(ErrorValidationResponse.class);

        assertEquals(expectedResult, actualResult);
    }

    @ParameterizedTest(name = "Run {index}: paymentRequestId={0}")
    @MethodSource("getInvalidPaymentRequestDataForRejection")
    @UserStoryId(2354)
    void shouldRejectPayment(String paymentRequestId, ErrorValidationResponse expectedResult) {
        val actualResult = rejectPayment(RejectPartnerPaymentRequest
                .builder()
                .paymentRequestId(paymentRequestId)
                .build(), customerToken)
                .then()
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .extract()
                .as(ErrorValidationResponse.class);

        assertEquals(expectedResult, actualResult);
    }

    @ParameterizedTest(name = "Run {index}: paymentRequestId={0}")
    @MethodSource("getInvalidPaymentRequestDataForGettingPayments")
    @UserStoryId(2519)
    void shouldGetApprovedPayments(String paymentRequestId, int status, ErrorValidationResponse expectedResult) {
        val actualResult = getPartnersPayments(GetPartnerPaymentRequestDetailsRequest
                .builder()
                .paymentRequestId(paymentRequestId)
                .build(), customerToken)
                .then()
                .assertThat()
                .statusCode(status)
                .extract()
                .as(ErrorValidationResponse.class);

        assertEquals(expectedResult, actualResult);
    }

    // TODO: ensure that String<->Date conversion works everywhere
    @Test
    @UserStoryId(storyId = {2519, 3631})
    void shouldPostPayment() {
        val partnerPassword = generateValidPassword();
        val partnerId = getRandomUuid();
        val partnerName = generateRandomString(10);
        val locationName = generateRandomString(10);
        val partnerData = createDefaultPartner(partnerId, partnerPassword, partnerName, locationName);
        val locationId = getLocationId(partnerData);
        val partnerToken = getPartnerToken(partnerId, partnerPassword, USER_INFO);
        val customerData = PrivateBlockchainFacadeUtils
                .createCustomerFundedViaBonusReward(SOME_INITIAL_TOKENS_AMOUNT, true);
        val customerToken = getUserToken(customerData.getEmail(), customerData.getPassword());

        val paymentData = postPayment(PaymentRequestModel
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

        val expectedPaymentData = PaymentRequestResponseModel
                .builder()
                .paymentRequestId(paymentData.getPaymentRequestId())
                .status(PaymentRequestStatus.CREATED)
                .error(PaymentRequestErrorCode.NONE)
                .build();
        assertEquals(expectedPaymentData, paymentData);

        // val formatPattern = DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm");
        val formatPattern = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm");
        val expectedResult = PartnerPaymentRequestDetailsResponse
                .builder()
                .paymentRequestId(paymentData.getPaymentRequestId())
                .status("Pending")
                //   .totalInToken(SOME_TOKENS_AMOUNT.toString())
                .totalInCurrency((float) (SOME_TOKENS_AMOUNT * SOME_CURRENCY_RATE))
                .currencyCode(AED_CURRENCY)
                .partnerId(partnerData.getId())
                .partnerName(partnerName)
                .locationId(locationId)
                .locationName(locationName)
                //  .paymentInfo(SOME_PAYMENT_INFO)
                .paymentInfo(EMPTY)
                .walletBalance(Double.valueOf(150).toString())
                .tokensToFiatConversionRate(0.33F)
                .lastUpdatedDate(ZonedDateTime.now(ZoneId.of("GMT")).format(formatPattern))
                .requestedAmountInTokens(SOME_TOKENS_AMOUNT.toString())
                .build();

        PartnerPaymentRequestDetailsResponse actualResult = getPartnersPayments(GetPartnerPaymentRequestDetailsRequest
                .builder()
                .paymentRequestId(paymentData.getPaymentRequestId())
                .build(), customerToken)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(PartnerPaymentRequestDetailsResponse.class);

        assertAll(
                () -> assertEquals(expectedResult, actualResult),
                () -> assertEquals(expectedResult.getLastUpdatedDate().substring(0, 16),
                        actualResult.getLastUpdatedDate().substring(0, 16))
        );
    }

    @Test
    @UserStoryId(3361)
    void shouldPostPaymentWithExpirationOn() {
        val expirationSeconds = 2;
        val partnerPassword = generateValidPassword();
        val partnerId = getRandomUuid();
        val partnerName = generateRandomString(10);
        val locationName = generateRandomString(10);
        val partnerData = createDefaultPartner(partnerId, partnerPassword, partnerName, locationName);
        val locationId = getLocationId(partnerData);
        val partnerToken = getPartnerToken(partnerId, partnerPassword, USER_INFO);
        val customerData = PrivateBlockchainFacadeUtils
                .createCustomerFundedViaBonusReward(SOME_INITIAL_TOKENS_AMOUNT, true);
        val customerToken = getUserToken(customerData.getEmail(), customerData.getPassword());

        val paymentData = postPayment(PaymentRequestModel
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

        val expectedPaymentData = PaymentRequestResponseModel
                .builder()
                .paymentRequestId(paymentData.getPaymentRequestId())
                .status(PaymentRequestStatus.CREATED)
                .error(PaymentRequestErrorCode.NONE)
                .build();
        assertEquals(expectedPaymentData, paymentData);

        PartnerPaymentRequestDetailsResponse actualResult = getPartnersPayments(GetPartnerPaymentRequestDetailsRequest
                .builder()
                .paymentRequestId(paymentData.getPaymentRequestId())
                .build(), customerToken)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(PartnerPaymentRequestDetailsResponse.class);

        assertEquals("RequestExpired", actualResult.getStatus());
    }

    @ParameterizedTest(name = "Run {index}: currency={0}, tokensAmount={1}, totalInToken={2}, totalInCurrency={3}")
    @MethodSource("getCurrencyForPayments")
    @UserStoryId(storyId = {3366, 3515})
    void shouldGetPendingPayments(String paymentCurrency, String tokensAmount, String expectedTotalInToken,
            Double expectedTotalInCurrency) {

        val partnerPassword = generateValidPassword();
        val partnerId = getRandomUuid();
        val partnerName = generateRandomString(10);
        val locationName = generateRandomString(10);
        val partnerData = createDefaultPartner(partnerId, partnerPassword, partnerName, locationName);
        val locationId = getLocationId(partnerData);
        val partnerToken = getPartnerToken(partnerId, partnerPassword, USER_INFO);
        val customerData = PrivateBlockchainFacadeUtils.createCustomerFundedViaBonusReward(100.0, true);

        val customerToken = getUserToken(customerData.getEmail(), customerData.getPassword());

        val paymentData = postPayment(PaymentRequestModel
                .builder()
                .currency(paymentCurrency)
                .customerId(customerData.getCustomerId())
                .partnerId(partnerData.getId())
                .tokensAmount(tokensAmount)
                .locationId(locationId)
                .partnerMessageId(SOME_PAYMENT_INFO)
                .totalBillAmount(SOME_TOTAL_FIAT_AMOUNT)
                .build(), partnerToken)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(PaymentRequestResponseModel.class);

        val actualApprovalResult = approvePayment(ApprovePartnerPaymentRequest
                .builder()
                .paymentRequestId(paymentData.getPaymentRequestId())
                .sendingAmount(tokensAmount)
                .build(), customerToken)
                .then()
                .assertThat()
                .statusCode(SC_NO_CONTENT);

        val actualResult = getPendingPayments(customerToken)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(PaginatedPartnerPaymentRequestsResponse.class)
                .getPaymentRequests();

        assertAll(
                () -> assertEquals(paymentData.getPaymentRequestId(), actualResult[0].getPaymentRequestId()),
                () -> assertEquals("Pending", actualResult[0].getStatus()),
                () -> assertNotNull(actualResult[0].getTotalInToken()),
                () -> assertEquals(expectedTotalInToken, actualResult[0].getTotalInToken()),
                () -> assertEquals(expectedTotalInCurrency, actualResult[0].getTotalInCurrency())
        );
    }

    // TODO: investigate into how to get succeeded payments
    @ParameterizedTest(name = "Run {index}: currency={0}, tokensAmount={1}, totalInToken={2}, totalInCurrency={3}")
    @MethodSource("getCurrencyForPayments")
    @UserStoryId(3366)
    void shouldGetSucceededPayments(String paymentCurrency, String tokensAmount, String expectedTotalInToken,
            Double expectedTotalInCurrency) {
        val partnerPassword = generateValidPassword();
        val partnerId = getRandomUuid();
        val partnerName = generateRandomString(10);
        val locationName = generateRandomString(10);
        val partnerData = createDefaultPartner(partnerId, partnerPassword, partnerName, locationName);
        val locationId = getLocationId(partnerData);
        val partnerToken = getPartnerToken(partnerId, partnerPassword, USER_INFO);
        val customerData = PrivateBlockchainFacadeUtils.createCustomerFundedViaBonusReward(100.0, true);
        val customerToken = getUserToken(customerData.getEmail(), customerData.getPassword());

        val paymentData = postPayment(PaymentRequestModel
                .builder()
                .currency(paymentCurrency)
                .customerId(customerData.getCustomerId())
                .partnerId(partnerData.getId())
                .tokensAmount(tokensAmount)
                .locationId(locationId)
                .partnerMessageId(SOME_PAYMENT_INFO)
                .totalBillAmount(SOME_TOTAL_FIAT_AMOUNT)
                .build(), partnerToken)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(PaymentRequestResponseModel.class);

        val actualApprovalResult = approvePayment(ApprovePartnerPaymentRequest
                .builder()
                .paymentRequestId(paymentData.getPaymentRequestId())
                .sendingAmount(tokensAmount)
                .build(), customerToken)
                .then()
                .assertThat()
                .statusCode(SC_NO_CONTENT);

        val actualResult = getSucceededPayments(PaginatedRequestModel
                .builder()
                .currentPage(CURRENT_PAGE_LOWER_BOUNDARY)
                .pageSize(PAGE_SIZE_UPPER_BOUNDARY)
                .build(), customerToken)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(PaginatedPartnerPaymentRequestsResponse.class);

        // TODO: get succeeded payments
    }

    @ParameterizedTest(name = "Run {index}: currency={0}, tokensAmount={1}, totalInToken={2}, totalInCurrency={3}")
    @MethodSource("getCurrencyForPayments")
    @UserStoryId(3366)
    void shouldGetFailedPayments(String paymentCurrency, String tokensAmount, String expectedTotalInToken,
            Double expectedTotalInCurrency) {
        val partnerPassword = generateValidPassword();
        val partnerId = getRandomUuid();
        val partnerName = generateRandomString(10);
        val locationName = generateRandomString(10);
        val partnerData = createDefaultPartner(partnerId, partnerPassword, partnerName, locationName);
        val locationId = getLocationId(partnerData);
        val partnerToken = getPartnerToken(partnerId, partnerPassword, USER_INFO);
        val customerData = PrivateBlockchainFacadeUtils.createCustomerFundedViaBonusReward(100.0, true);
        val customerToken = getUserToken(customerData.getEmail(), customerData.getPassword());

        val paymentData = postPayment(PaymentRequestModel
                .builder()
                .currency(paymentCurrency)
                .customerId(customerData.getCustomerId())
                .partnerId(partnerData.getId())
                .tokensAmount(tokensAmount)
                .locationId(locationId)
                .partnerMessageId(SOME_PAYMENT_INFO)
                .totalBillAmount(SOME_TOTAL_FIAT_AMOUNT)
                .build(), partnerToken)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(PaymentRequestResponseModel.class);

        val actualRejectResult = rejectPayment(RejectPartnerPaymentRequest

                .builder()
                .paymentRequestId(paymentData.getPaymentRequestId())
                .build(), customerToken)
                .then()
                .assertThat()
                .statusCode(SC_NO_CONTENT);

        val actualResult = getFailedPayments(PaginatedRequestModel
                .builder()
                .currentPage(CURRENT_PAGE_LOWER_BOUNDARY)
                .pageSize(PAGE_SIZE_UPPER_BOUNDARY)
                .build(), customerToken)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(PaginatedPartnerPaymentRequestsResponse.class);

        assertAll(
                () -> assertEquals(paymentData.getPaymentRequestId(),
                        actualResult.getPaymentRequests()[0].getPaymentRequestId()),
                () -> assertEquals("Cancelled", actualResult.getPaymentRequests()[0].getStatus()),
                () -> assertNotNull(actualResult.getPaymentRequests()[0].getTotalInToken()),
                () -> assertEquals(expectedTotalInToken, actualResult.getPaymentRequests()[0].getTotalInToken()),
                () -> assertEquals(expectedTotalInCurrency, actualResult.getPaymentRequests()[0].getTotalInCurrency())
        );
    }

    @AllArgsConstructor
    @Builder
    @Data
    @NoArgsConstructor
    @JsonNaming(LowerCaseStrategy.class)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ErrorValidationResponse {

        private String error;
        private String message;
    }
}
