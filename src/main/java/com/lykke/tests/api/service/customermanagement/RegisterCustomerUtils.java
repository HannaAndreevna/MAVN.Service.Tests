package com.lykke.tests.api.service.customermanagement;

import static com.lykke.api.testing.api.base.RequestHeader.getHeader;
import static com.lykke.api.testing.api.common.GenerateUtils.generateRandomEmail;
import static com.lykke.api.testing.api.common.GenerateUtils.generateRandomPhone;
import static com.lykke.api.testing.api.common.PasswordGen.generateValidPassword;
import static com.lykke.tests.api.base.Paths.Customer.CUSTOMER_PHONES_API_PATH;
import static com.lykke.tests.api.base.Paths.Customer.CUSTOMER_PHONES_VERIFY_API_PATH;
import static com.lykke.tests.api.base.Paths.CustomerManagement.GENERATE_VERIFICATION_API_PATH;
import static com.lykke.tests.api.base.Paths.CustomerManagement.REGISTER_CUSTOMER_API_PATH;
import static com.lykke.tests.api.service.customer.LoginLogoutCustomerUtils.getUserToken;
import static com.lykke.tests.api.service.notificationsystembroker.EmailConfirmationUtils.confirmRegistration;
import static com.lykke.tests.api.service.smsprovidermock.SmsProviderMockUtils.querySms;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.http.HttpStatus.SC_NO_CONTENT;
import static org.apache.http.HttpStatus.SC_OK;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy.LowerCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.lykke.api.testing.api.common.FakerUtils;
import com.lykke.tests.api.common.model.CustomerInfo;
import com.lykke.tests.api.service.customer.model.SetCustomerPhoneInfoRequestModel;
import com.lykke.tests.api.service.customermanagement.model.register.LoginProvider;
import com.lykke.tests.api.service.customermanagement.model.register.RegistrationRequestModel;
import com.lykke.tests.api.service.smsprovidermock.model.PaginatedSmsRequestModel;
import com.lykke.tests.api.service.smsprovidermock.model.PaginatedSmsResponseModel;
import com.lykke.tests.api.service.smsprovidermock.model.SmsResponseModel;
import com.lykke.tests.exceptions.UnableToRegisterUserException;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import java.util.Arrays;
import java.util.regex.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import lombok.val;
import org.awaitility.Awaitility;
import org.awaitility.Duration;

@UtilityClass
public class RegisterCustomerUtils {

    public static final String CUSTOMER_ID_FIELD = "CustomerId";
    public static final String ERROR_MESSAGE = "Error";
    public static final String TOKEN_FIELD = "Token";

    private static final String EMAIL_FIELD = "Email";
    private static final String PASSWORD_FIELD = "Password";
    private static final String REFERRAL_CODE_FIELD = "ReferralCode";
    private static final int PHONE_NUMBER_LENGTH = 10;
    private static final int CURRENT_PAGE = 1;
    private static final int PAGE_SIZE = 500;

    //TODO: Return RegistrationResponseModel instead of Response and update all test to use this method.
    //Now we have different methods for registration which is confusing. Add the validation in the registration request.
    public Response registerCustomerResponse(RegistrationRequestModel registrationRequestModel) {
        return getHeader()
                .body(registrationRequestModel)
                .post(REGISTER_CUSTOMER_API_PATH);
    }

    public static String registerCustomer() {
        return registerCustomerAndReturnId(new RegistrationRequestModel());
    }

    public static String registerCustomer(RegistrationRequestModel customer) {
        return registerCustomerAndReturnId(customer);
    }

    public static String registerCustomerTheOldWay(RegistrationRequestModel user) {
        return registerCustomerWithEmailAndPassword(user)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .path(CUSTOMER_ID_FIELD);
    }

    @Step("Register customer")
    private String registerCustomerAndReturnId(RegistrationRequestModel user) {
        return registerCustomerResponse(user)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .path(CUSTOMER_ID_FIELD);
    }

    public Response registerCustomerWithEmailAndPasswordAndReferralCode(RegistrationRequestModel user) {
        return getHeader()
                .body(user)
                .post(REGISTER_CUSTOMER_API_PATH);
    }

    @Step("Register customer")
    public String registerCustomerWithReferralCode(RegistrationRequestModel user) {
        return registerCustomerWithEmailAndPasswordAndReferralCode(user)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .path(CUSTOMER_ID_FIELD);
    }

    public static CustomerInfo registerDefaultCustomer(String emailAddress, String password) {
        val firstName = FakerUtils.firstName;
        val lastName = FakerUtils.lastName;
        val phoneNumber = generateRandomPhone(PHONE_NUMBER_LENGTH);
        final Integer countryPhoneCodeId = 1;
        val customerId = registerCustomerAndReturnId(RegistrationRequestModel
                .completeCustomerBuilder()
                .firstName(firstName)
                .lastName(lastName)
                .phoneNumber(phoneNumber)
                .countryPhoneCodeId(countryPhoneCodeId)
                .email(emailAddress)
                .password(password)
                .loginProvider(LoginProvider.STANDARD)
                .build());

        val token = getUserToken(RegistrationRequestModel
                .completeCustomerBuilder()
                .firstName(firstName)
                .lastName(lastName)
                .email(emailAddress)
                .password(password)
                .loginProvider(LoginProvider.STANDARD)
                .phoneNumber(phoneNumber)
                .countryPhoneCodeId(countryPhoneCodeId)
                .build());

        getHeader(token)
                .body(SetCustomerPhoneInfoRequestModel
                        .builder()
                        .customerId(customerId)
                        .phoneNumber(phoneNumber)
                        .countryPhoneCodeId(countryPhoneCodeId)
                        .build())
                .post(CUSTOMER_PHONES_API_PATH)
                .then()
                .assertThat()
                .statusCode(SC_NO_CONTENT);

        return CustomerInfo
                .customerInfoBuilder()
                .customerId(customerId)
                .email(emailAddress)
                .firstName(firstName)
                .lastName(lastName)
                .phoneNumber(phoneNumber)
                .countryPhoneCodeId(countryPhoneCodeId)
                .build();
    }

    @Step
    public static CustomerInfo registerDefaultVerifiedCustomer() {
        return registerDefaultVerifiedCustomer(true);
    }

    @SneakyThrows
    @Step
    public static CustomerInfo registerDefaultVerifiedCustomer(boolean isEmailVerified) {

        CustomerInfo customerInfo = null;
        int i = 0;
        while (isCustomerInfoEmpty(customerInfo) && i < 10) {
            try {
                customerInfo = CustomerInfo
                        .customerInfoBuilder()
                        .email(generateRandomEmail())
                        .password(generateValidPassword())
                        .build();
                customerInfo = tryToRegisterCustomer(customerInfo.getEmail(), customerInfo.getPassword(),
                        isEmailVerified);
                i++;

                if (isCustomerInfoEmpty(customerInfo)) {
                    continue;
                }

            } catch (Exception e) {
                i++;
            }
        }

        if (isCustomerInfoEmpty(customerInfo)) {
            throw new UnableToRegisterUserException(
                    String.format("Failed to register customer with email %s and password %s", customerInfo.getEmail(),
                            customerInfo.getPassword()));
        }

        return customerInfo;
    }

    private boolean isCustomerInfoEmpty(CustomerInfo customerInfo) {
        return null == customerInfo || null == customerInfo.getCustomerId();
    }

    @SneakyThrows
    private CustomerInfo tryToRegisterCustomer(String emailAddress, String password, boolean isEmailVerified) {
        val firstName = FakerUtils.firstName;
        val lastName = FakerUtils.lastName;
        val phoneNumber = generateRandomPhone(PHONE_NUMBER_LENGTH);
        final Integer countryPhoneCodeId = 1;
        val customerId = registerCustomerAndReturnId(RegistrationRequestModel
                .completeCustomerBuilder()
                .firstName(firstName)
                .lastName(lastName)
                .email(emailAddress)
                .password(password)
                .loginProvider(LoginProvider.STANDARD)
                .phoneNumber(phoneNumber)
                .countryPhoneCodeId(countryPhoneCodeId)
                .countryOfNationalityId(1)
                .build());

        if (customerId.equalsIgnoreCase(EMPTY)) {
            return new CustomerInfo();
        }

        val token = getUserToken(CustomerInfo
                .customerInfoBuilder()
                .email(emailAddress)
                .password(password)
                .build());

        if (token.equalsIgnoreCase(EMPTY)) {
            return new CustomerInfo();
        }

        try {
            setPhoneNumber(customerId, token, phoneNumber, countryPhoneCodeId);
        } catch (Exception e) {
            return new CustomerInfo();
        }

        if (isEmailVerified) {
            confirmRegistration(emailAddress, password);
        }
        return CustomerInfo
                .customerInfoBuilder()
                .customerId(customerId)
                .email(emailAddress)
                .password(password)
                .firstName(firstName)
                .lastName(lastName)
                .phoneNumber(phoneNumber)
                .countryPhoneCodeId(countryPhoneCodeId)
                .token(token)
                .build();
    }

    @Step
    public void setPhoneNumber(String customerId, String token, String phoneNumber, Integer countryPhoneCodeId) {
        getHeader(token)
                .body(CustomerPhoneRequest
                        .builder()
                        .phoneNumber(phoneNumber)
                        .countryPhoneCodeId(countryPhoneCodeId.toString())
                        .build())
                .post(CUSTOMER_PHONES_API_PATH)
                .then()
                .assertThat()
                .statusCode(SC_NO_CONTENT);

        getHeader(token)
                .body(CustomerIdRequest
                        .builder()
                        .customerId(customerId)
                        .build())
                .post(GENERATE_VERIFICATION_API_PATH)
                .then()
                .assertThat()
                .statusCode(SC_OK);

        Awaitility.await()
                .atMost(Duration.TEN_SECONDS.plus(Duration.TEN_SECONDS))
                .pollInterval(Duration.TWO_SECONDS)
                .until(() -> {
                    val smsMessage = getPhoneVerificationSmsMessage(phoneNumber, countryPhoneCodeId);
                    if (null == smsMessage || null == smsMessage.getPhoneNumber()) {
                        return false;
                    }
                    return !smsMessage.getPhoneNumber()
                            .equalsIgnoreCase(EMPTY);
                });

        val smsMessage = getPhoneVerificationSmsMessage(phoneNumber, countryPhoneCodeId);

        val patternString = "(\\d+?)(?=\\sto\\sverify)";
        val pattern = Pattern.compile(patternString);
        val matcher = pattern.matcher(smsMessage.getMessage());
        String verificationCode = EMPTY;
        if (0 < matcher.groupCount()) {
            matcher.find();
            verificationCode = matcher.group();
        }

        getHeader(token)
                .body(VerificationCodeRequest
                        .builder()
                        .verificationCode(verificationCode)
                        .build())
                .post(CUSTOMER_PHONES_VERIFY_API_PATH)
                .then()
                .assertThat()
                .statusCode(SC_NO_CONTENT);
    }

    private String composePhoneNumber(String phoneNumber, Integer countryPhoneCodeId) {
        return String.format("+9%s%s", countryPhoneCodeId.toString(), phoneNumber);
    }

    private SmsResponseModel getPhoneVerificationSmsMessage(String phoneNumber, Integer countryPhoneCodeId) {

        val smsCollection = querySms(PaginatedSmsRequestModel
                .builder()
                .currentPage(CURRENT_PAGE)
                .pageSize(PAGE_SIZE)
                .phoneNumber(composePhoneNumber(phoneNumber, countryPhoneCodeId))
                .build())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(PaginatedSmsResponseModel.class);
        if (null == smsCollection || null == smsCollection.getSms() || 0 == smsCollection.getSms().length) {
            return new SmsResponseModel();
        }
        return Arrays.stream(smsCollection.getSms())
                .filter(sms -> sms.getPhoneNumber()
                        .equalsIgnoreCase(composePhoneNumber(phoneNumber, countryPhoneCodeId)))
                .findFirst()
                .orElse(new SmsResponseModel());
    }

    Response registerCustomerWithEmailAndPassword(RegistrationRequestModel user) {
        return getHeader()
                .body(user)
                .post(REGISTER_CUSTOMER_API_PATH);
    }

    @AllArgsConstructor
    @Builder
    @Data
    public static class CustomerPhoneRequest {

        private String phoneNumber;
        private String countryPhoneCodeId;
    }

    @AllArgsConstructor
    @Builder
    @Data
    public static class VerificationCodeRequest {

        private String verificationCode;
    }

    @AllArgsConstructor
    @Builder
    @Data
    public static class CustomerIdRequest {

        private String customerId;
    }

    @AllArgsConstructor
    @Builder
    @Data
    @NoArgsConstructor
    @JsonNaming(LowerCaseStrategy.class)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PartnerApiErrorResponse {

        private String error;
        private String message;
    }
}
