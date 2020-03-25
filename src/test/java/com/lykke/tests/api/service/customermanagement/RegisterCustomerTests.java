package com.lykke.tests.api.service.customermanagement;

import static com.lykke.api.testing.api.common.GenerateUtils.generateRandomEmail;
import static com.lykke.api.testing.api.common.GenerateUtils.generateRandomString;
import static com.lykke.api.testing.api.common.PasswordGen.generateValidPassword;
import static com.lykke.tests.api.common.CommonConsts.ERROR_CODE_NONE;
import static com.lykke.tests.api.common.CommonConsts.INVALID_EMAIL_FORMAT_ERROR_MESSAGE;
import static com.lykke.tests.api.common.CommonConsts.REFERRAL_CODE_FIELD;
import static com.lykke.tests.api.common.CommonConsts.SMOKE_TEST;
import static com.lykke.tests.api.service.customer.CustomerReferralsUtils.getCustomerReferrals;
import static com.lykke.tests.api.service.customer.LoginLogoutCustomerUtils.getUserToken;
import static com.lykke.tests.api.service.customermanagement.RegisterCustomerUtils.CUSTOMER_ID_FIELD;
import static com.lykke.tests.api.service.customermanagement.RegisterCustomerUtils.ERROR_MESSAGE;
import static com.lykke.tests.api.service.customermanagement.RegisterCustomerUtils.registerCustomer;
import static com.lykke.tests.api.service.customermanagement.RegisterCustomerUtils.registerCustomerWithEmailAndPassword;
import static com.lykke.tests.api.service.customermanagement.RegisterCustomerUtils.registerCustomerWithEmailAndPasswordAndReferralCode;
import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.text.CharSequenceLength.hasLength;
import static org.junit.jupiter.params.provider.Arguments.of;

import com.lykke.api.testing.annotations.UserStoryId;
import com.lykke.api.testing.api.common.FakerUtils;
import com.lykke.tests.api.base.BaseApiTest;
import com.lykke.tests.api.service.customermanagement.model.register.LoginProvider;
import com.lykke.tests.api.service.customermanagement.model.register.RegistrationRequestModel;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import lombok.var;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class RegisterCustomerTests extends BaseApiTest {

    private static final String EMAIL_FIELD_IS_REQUIRED_MESSAGE = "The Email field is required.";
    private static final String PASSWORD_FIELD_IS_REQUIRED_MESSAGE = "The Password field is required.";
    private static final String FIRSTNAME_FIELD_IS_REQUIRED_MESSAGE = "The FirstName field is required.";
    private static final String FIRSTNAME_FIELD_DESCRIPTION_MESSAGE = "The field FirstName must be a string or array type with a maximum length of '50'.";
    private static final String LASTNAME_FIELD_IS_REQUIRED_MESSAGE = "The LastName field is required.";
    private static final String LASTNAME_FIELD_DESCRIPTION_MESSAGE = "The field LastName must be a string or array type with a maximum length of '50'.";
    private static final String PHONENUMBER_FIELD_IS_REQUIRED_MESSAGE = "The PhoneNumber field is required.";
    private static final String PHONENUMBER_FIELD_DESCRIPTION_MESSAGE =
            "The field PhoneNumber must be a string or array"
                    + " type with a maximum length of '50'.";
    private static final String INVALID_PASSWORD_FORMAT_ERROR = "The field Password must match the regular expression '"
            + "\\A(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?"
            + "\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?)\\Z'.";
    private static final String ALREADY_REGISTERED_ERROR = "AlreadyRegistered";
    private static final LoginProvider LOGIN_PROVIDER = LoginProvider.STANDARD;

    private static int maxLengthPassword = 100;

    private static Stream<Arguments> registerRequest_EmailIsNotProvided() {
        return Stream.of(
                of(RegistrationRequestModel.builder().password(generateValidPassword()).firstName(FakerUtils.firstName)
                        .lastName(FakerUtils.lastName).phoneNumber(FakerUtils.phoneNumber)
                        .countryPhoneCodeId(FakerUtils.countryPhoneCode)
                        .loginProvider(LOGIN_PROVIDER).build(), EMAIL_FIELD_IS_REQUIRED_MESSAGE),
                of(RegistrationRequestModel.builder().email("").password(generateValidPassword())
                        .firstName(FakerUtils.firstName)
                        .lastName(FakerUtils.lastName).phoneNumber(FakerUtils.phoneNumber)
                        .countryPhoneCodeId(FakerUtils.countryPhoneCode)
                        .loginProvider(LOGIN_PROVIDER).build(), EMAIL_FIELD_IS_REQUIRED_MESSAGE)
        );
    }

    private static Stream<Arguments> registerRequest_InvalidEmail() {
        return Stream.of(
                of(RegistrationRequestModel.builder().email("plaintext").password(generateValidPassword())
                                .firstName(FakerUtils.firstName).lastName(FakerUtils.lastName)
                                .phoneNumber(FakerUtils.phoneNumber)
                                .countryPhoneCodeId(FakerUtils.countryPhoneCode).loginProvider(LOGIN_PROVIDER).build(),
                        INVALID_EMAIL_FORMAT_ERROR_MESSAGE),
                of(RegistrationRequestModel.builder().email("#@%^%#$@#$@#.com").password(generateValidPassword())
                                .firstName(FakerUtils.firstName).lastName(FakerUtils.lastName)
                                .phoneNumber(FakerUtils.phoneNumber)
                                .countryPhoneCodeId(FakerUtils.countryPhoneCode).loginProvider(LOGIN_PROVIDER).build(),
                        INVALID_EMAIL_FORMAT_ERROR_MESSAGE),
                of(RegistrationRequestModel.builder().email("email.example.com").password(generateValidPassword())
                                .firstName(FakerUtils.firstName).lastName(FakerUtils.lastName)
                                .phoneNumber(FakerUtils.phoneNumber)
                                .countryPhoneCodeId(FakerUtils.countryPhoneCode).loginProvider(LOGIN_PROVIDER).build(),
                        INVALID_EMAIL_FORMAT_ERROR_MESSAGE),
                of(RegistrationRequestModel.builder().email("email@example@example.com")
                                .password(generateValidPassword())
                                .firstName(FakerUtils.firstName).lastName(FakerUtils.lastName)
                                .phoneNumber(FakerUtils.phoneNumber)
                                .countryPhoneCodeId(FakerUtils.countryPhoneCode).loginProvider(LOGIN_PROVIDER).build(),
                        INVALID_EMAIL_FORMAT_ERROR_MESSAGE),
                of(RegistrationRequestModel.builder().email(".email@example.com").password(generateValidPassword())
                                .firstName(FakerUtils.firstName).lastName(FakerUtils.lastName)
                                .phoneNumber(FakerUtils.phoneNumber)
                                .countryPhoneCodeId(FakerUtils.countryPhoneCode).loginProvider(LOGIN_PROVIDER).build(),
                        INVALID_EMAIL_FORMAT_ERROR_MESSAGE),
                of(RegistrationRequestModel.builder().email(".email@example").password(generateValidPassword())
                                .firstName(FakerUtils.firstName).lastName(FakerUtils.lastName)
                                .phoneNumber(FakerUtils.phoneNumber)
                                .countryPhoneCodeId(FakerUtils.countryPhoneCode).loginProvider(LOGIN_PROVIDER).build(),
                        INVALID_EMAIL_FORMAT_ERROR_MESSAGE)
        );
    }

    private static Stream<Arguments> registerRequest_PasswordIsNotProvided() {
        return Stream.of(
                of(RegistrationRequestModel.builder().email(generateRandomEmail()).firstName(FakerUtils.firstName)
                                .lastName(FakerUtils.lastName).phoneNumber(FakerUtils.phoneNumber)
                                .countryPhoneCodeId(FakerUtils.countryPhoneCode).loginProvider(LOGIN_PROVIDER).build(),
                        INVALID_PASSWORD_FORMAT_ERROR),
                of(RegistrationRequestModel.builder().email(generateRandomEmail()).password("")
                                .firstName(FakerUtils.firstName)
                                .lastName(FakerUtils.lastName).phoneNumber(FakerUtils.phoneNumber)
                                .countryPhoneCodeId(FakerUtils.countryPhoneCode).loginProvider(LOGIN_PROVIDER).build(),
                        INVALID_PASSWORD_FORMAT_ERROR)
        );
    }

    private static Stream<Arguments> registerRequest_InvalidPassword() {
        return Stream.of(
                of(RegistrationRequestModel.builder().email(generateRandomEmail()).password("asdf")
                                .firstName(FakerUtils.firstName)
                                .lastName(FakerUtils.lastName).phoneNumber(FakerUtils.phoneNumber)
                                .countryPhoneCodeId(FakerUtils.countryPhoneCode).loginProvider(LOGIN_PROVIDER).build(),
                        INVALID_PASSWORD_FORMAT_ERROR),
                of(RegistrationRequestModel.builder().email(generateRandomEmail())
                                .password(generateRandomString(maxLengthPassword + 1)).firstName(FakerUtils.firstName)
                                .lastName(FakerUtils.lastName).phoneNumber(FakerUtils.phoneNumber)
                                .countryPhoneCodeId(FakerUtils.countryPhoneCode).loginProvider(LOGIN_PROVIDER).build(),
                        INVALID_PASSWORD_FORMAT_ERROR)
        );
    }

    private static Stream<Arguments> registerRequest_InvalidFirstName() {
        return Stream.of(
                of(RegistrationRequestModel.builder().email(generateRandomEmail()).password(generateValidPassword())
                                .lastName(FakerUtils.lastName).phoneNumber(FakerUtils.phoneNumber)
                                .countryPhoneCodeId(FakerUtils.countryPhoneCode).loginProvider(LOGIN_PROVIDER).build(),
                        FIRSTNAME_FIELD_IS_REQUIRED_MESSAGE),
                of(RegistrationRequestModel.builder().email(generateRandomEmail()).password(generateValidPassword())
                                .firstName(generateRandomString(maxLengthPassword + 1)).lastName(FakerUtils.lastName)
                                .phoneNumber(FakerUtils.phoneNumber).countryPhoneCodeId(FakerUtils.countryPhoneCode)
                                .loginProvider(LOGIN_PROVIDER).build(),
                        FIRSTNAME_FIELD_DESCRIPTION_MESSAGE),
                of(RegistrationRequestModel.builder().email(generateRandomEmail()).password(generateValidPassword())
                                .firstName("").lastName(FakerUtils.lastName).phoneNumber(FakerUtils.phoneNumber)
                                .countryPhoneCodeId(FakerUtils.countryPhoneCode).loginProvider(LOGIN_PROVIDER).build(),
                        FIRSTNAME_FIELD_IS_REQUIRED_MESSAGE)
        );
    }

    private static Stream<Arguments> registerRequest_InvalidLastName() {
        return Stream.of(
                of(RegistrationRequestModel.builder().email(generateRandomEmail()).password(generateValidPassword())
                                .firstName(FakerUtils.firstName).phoneNumber(FakerUtils.phoneNumber)
                                .countryPhoneCodeId(FakerUtils.countryPhoneCode).loginProvider(LOGIN_PROVIDER).build(),
                        LASTNAME_FIELD_IS_REQUIRED_MESSAGE),
                of(RegistrationRequestModel.builder().email(generateRandomEmail()).password(generateValidPassword())
                                .firstName(FakerUtils.firstName).lastName(generateRandomString(maxLengthPassword + 1))
                                .phoneNumber(FakerUtils.phoneNumber).countryPhoneCodeId(FakerUtils.countryPhoneCode)
                                .loginProvider(LOGIN_PROVIDER).build(),
                        LASTNAME_FIELD_DESCRIPTION_MESSAGE),
                of(RegistrationRequestModel.builder().email(generateRandomEmail()).password(generateValidPassword())
                                .firstName(FakerUtils.firstName).lastName("").phoneNumber(FakerUtils.phoneNumber)
                                .countryPhoneCodeId(FakerUtils.countryPhoneCode).loginProvider(LOGIN_PROVIDER).build(),
                        LASTNAME_FIELD_IS_REQUIRED_MESSAGE)
        );
    }

    private static Stream<Arguments> registerRequest_InvalidPhoneNumber() {
        return Stream.of(
                of(RegistrationRequestModel.builder().email(generateRandomEmail()).password(generateValidPassword())
                                .firstName(FakerUtils.firstName).lastName(FakerUtils.lastName)
                                .countryPhoneCodeId(FakerUtils.countryPhoneCode).loginProvider(LOGIN_PROVIDER).build(),
                        PHONENUMBER_FIELD_IS_REQUIRED_MESSAGE),
                of(RegistrationRequestModel.builder().email(generateRandomEmail()).password(generateValidPassword())
                                .firstName(FakerUtils.firstName).lastName(FakerUtils.lastName)
                                .loginProvider(LOGIN_PROVIDER).build(),
                        PHONENUMBER_FIELD_IS_REQUIRED_MESSAGE),
                of(RegistrationRequestModel.builder().email(generateRandomEmail()).password(generateValidPassword())
                                .firstName(FakerUtils.firstName).lastName(FakerUtils.lastName)
                                .phoneNumber("").countryPhoneCodeId(FakerUtils.countryPhoneCode)
                                .loginProvider(LOGIN_PROVIDER).build(),
                        PHONENUMBER_FIELD_IS_REQUIRED_MESSAGE),
                of(RegistrationRequestModel.builder().email(generateRandomEmail()).password(generateValidPassword())
                                .firstName(FakerUtils.firstName).lastName(FakerUtils.lastName)
                                .phoneNumber("")
                                .loginProvider(LOGIN_PROVIDER).build(),
                        PHONENUMBER_FIELD_IS_REQUIRED_MESSAGE),
                of(RegistrationRequestModel.builder().email(generateRandomEmail()).password(generateValidPassword())
                                .firstName(FakerUtils.firstName).lastName(FakerUtils.lastName)
                                .phoneNumber(generateRandomString(maxLengthPassword + 1))
                                .countryPhoneCodeId(FakerUtils.countryPhoneCode).loginProvider(LOGIN_PROVIDER).build(),
                        PHONENUMBER_FIELD_DESCRIPTION_MESSAGE)
        );
    }

    @Test
    @Tag(SMOKE_TEST)
    void shouldRegisterCustomerWithValidInput() {
        registerCustomerWithEmailAndPassword(new RegistrationRequestModel())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .body(CUSTOMER_ID_FIELD, hasLength(36));
    }

    @Test
    void shouldRegisterCustomerWithValidReferralCode() {
        String referralCode = getCustomerReferrals(getReferrerToken())
                .then()
                .extract()
                .path(REFERRAL_CODE_FIELD);

        IntStream.range(1, 4)
                .forEach(iter -> {
                            var user = new RegistrationRequestModel();
                            user.setReferralCode(referralCode);
                            registerCustomerWithEmailAndPasswordAndReferralCode(user)
                                    .then()
                                    .assertThat()
                                    .statusCode(SC_OK)
                                    .body(CUSTOMER_ID_FIELD, notNullValue())
                                    .body(ERROR_MESSAGE, equalTo(ERROR_CODE_NONE));
                        }
                );
    }

    @Test
    void shouldNotRegisterCustomerTwice() {
        var user = new RegistrationRequestModel();
        registerCustomerWithEmailAndPassword(user)
                .then()
                .assertThat()
                .statusCode(SC_OK);

        registerCustomerWithEmailAndPassword(user)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .body(ERROR_MESSAGE, equalTo(ALREADY_REGISTERED_ERROR));
    }

    @ParameterizedTest(name = "Run {index}: user model = {0}, message={1}")
    @MethodSource("registerRequest_EmailIsNotProvided")
    @UserStoryId(3769)
    void shouldNotRegisterWhenEmailIsNotProvided(RegistrationRequestModel user, String message) {
        registerCustomerWithEmailAndPassword(user)
                .then()
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .body("ErrorMessage", equalTo(message));
    }

    @ParameterizedTest(name = "Run {index}: user model = {0}, message={1}")
    @MethodSource("registerRequest_InvalidEmail")
    @UserStoryId(3769)
    void shouldNotRegisterWithInvalidEmail(RegistrationRequestModel user, String message) {
        registerCustomerWithEmailAndPassword(user)
                .then()
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .body("ErrorMessage", equalTo(message));
    }

    @ParameterizedTest(name = "Run {index}: user model = {0}, message={1}")
    @MethodSource("registerRequest_PasswordIsNotProvided")
    @UserStoryId(3769)
    void shouldNotRegisterWhenPasswordIsNotProvided(RegistrationRequestModel user, String message) {
        registerCustomerWithEmailAndPassword(user)
                .then()
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .body("ErrorMessage", equalTo(message));
    }

    @ParameterizedTest(name = "Run {index}: user model = {0}, message={1}")
    @MethodSource("registerRequest_InvalidPassword")
    @UserStoryId(3769)
    void shouldNotRegisterWithInvalidPassword(RegistrationRequestModel user, String message) {
        registerCustomerWithEmailAndPassword(user)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .body("CustomerId", equalTo(null))
                .body("ErrorMessage", equalTo(message));
    }

    @ParameterizedTest(name = "Run {index}: user model = {0}, message={1}")
    @MethodSource("registerRequest_InvalidFirstName")
    @UserStoryId(storyId = {2622, 3769})
    void shouldNotRegisterWithInvalidFirstName(RegistrationRequestModel user, String message) {
        registerCustomerWithEmailAndPassword(user)
                .then()
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .body("CustomerId", equalTo(null))
                .body("ErrorMessage", equalTo(message));
    }

    @ParameterizedTest(name = "Run {index}: user model = {0}, message={1}")
    @MethodSource("registerRequest_InvalidLastName")
    @UserStoryId(storyId = {2622, 3769})
    void shouldNotRegisterWithInvalidLastName(RegistrationRequestModel user, String message) {
        registerCustomerWithEmailAndPassword(user)
                .then()
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .body("CustomerId", equalTo(null))
                .body("ErrorMessage", equalTo(message));
    }

    @ParameterizedTest(name = "Run {index}: user model = {0}, message={1}")
    @MethodSource("registerRequest_InvalidPhoneNumber")
    @UserStoryId(storyId = {2622, 3769})
    void shouldNotRegisterWithInvalidPhoneNumber(RegistrationRequestModel user, String message) {
        registerCustomerWithEmailAndPassword(user)
                .then()
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .body("CustomerId", equalTo(null))
                .body("ErrorMessage", equalTo(message));
    }

    private String getReferrerToken() {
        var user = new RegistrationRequestModel();
        registerCustomer(user);
        return getUserToken(user.getEmail(), user.getPassword());
    }
}
