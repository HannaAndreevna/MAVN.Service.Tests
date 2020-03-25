package com.lykke.tests.api.service.customer;

import static com.lykke.tests.api.common.CommonConsts.MODEL_VALIDATION_FAILURE;
import static com.lykke.tests.api.common.CommonConsts.SMOKE_TEST;
import static com.lykke.api.testing.api.common.FakerUtils.firstName;
import static com.lykke.api.testing.api.common.FakerUtils.lastName;
import static com.lykke.api.testing.api.common.FakerUtils.phoneNumber;
import static com.lykke.api.testing.api.common.GenerateUtils.generateRandomEmail;
import static com.lykke.api.testing.api.common.GenerateUtils.generateRandomString;
import static com.lykke.api.testing.api.common.PasswordGen.generateValidPassword;

import static com.lykke.tests.api.common.CommonConsts.INVALID_PASSWORD_ERR_MSG;
import static com.lykke.tests.api.common.CommonConsts.NON_LATIN_PWD;
import static com.lykke.tests.api.common.CommonConsts.WHITE_SPACE_PWD;
import static com.lykke.tests.api.service.customer.RegisterCustomerUtils.registerUserWithEmailAndPassword;
import static com.lykke.tests.api.service.customer.RegisterCustomerUtils.registerWithGoogle;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.params.provider.Arguments.of;

import com.lykke.api.testing.annotations.UserStoryId;
import com.lykke.tests.api.base.BaseApiTest;
import com.lykke.tests.api.service.customer.model.GoogleRegistrationRequestModel;
import java.util.stream.Stream;
import lombok.val;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

public class RegisterCustomerTests extends BaseApiTest {

    public static final String ERROR_FIELD = "error";
    public static final String MESSAGE_FIELD = "message";
    private static final String EMAIL_FIELD_IS_REQUIRED_MESSAGE = "The Email field is required.";
    private static final String INVALID_EMAIL_FORMAT_MESSAGE = "A valid email address is required.";
    private static final String INVALID_EMAIL_FORMAT_ERROR = "InvalidEmailFormat";
    private static final String INVALID_PASSWORD_FORMAT_ERROR = "InvalidPasswordFormat";
    private static final String EMAIL_FIELD_IS_EMPTY_MESSAGE =
            EMAIL_FIELD_IS_REQUIRED_MESSAGE + " " + INVALID_EMAIL_FORMAT_MESSAGE;
    private static final String INVALID_OR_EXPIRED_GAT_ERR = "InvalidOrExpiredGoogleAccessToken";
    private static final String INVALID_OR_EXPIRED_GAT_MSG = "The provided google access token is not valid or expired";
    private static final String GAT_IS_REQUIRED_MSG = "The AccessToken field is required.";
    private static final String THE_LAST_NAME_FIRST_NAME_PHONE_NUMBER_FIELDS_ARE_REQUIRED_ERROR_MESSAGE = "The LastName field is required. The FirstName field is required.";
    private static int maxLengthPassword = 100;

    private static Stream<Arguments> registerRequestCustomerApi_InvalidParameters() {
        return Stream.of(
                of(null, generateValidPassword(), INVALID_EMAIL_FORMAT_ERROR, EMAIL_FIELD_IS_REQUIRED_MESSAGE),
                of("", generateValidPassword(), INVALID_EMAIL_FORMAT_ERROR, EMAIL_FIELD_IS_EMPTY_MESSAGE),
                of("plaintext", generateValidPassword(), INVALID_EMAIL_FORMAT_ERROR,
                        INVALID_EMAIL_FORMAT_MESSAGE),
                of("#@%^%#$@#$@#.com", generateValidPassword(), INVALID_EMAIL_FORMAT_ERROR,
                        INVALID_EMAIL_FORMAT_MESSAGE),
                of("email.example.com", generateValidPassword(), INVALID_EMAIL_FORMAT_ERROR,
                        INVALID_EMAIL_FORMAT_MESSAGE),
                of("email@example@example.com", generateValidPassword(), INVALID_EMAIL_FORMAT_ERROR,
                        INVALID_EMAIL_FORMAT_MESSAGE),
                of(".email@example.com", generateValidPassword(), INVALID_EMAIL_FORMAT_ERROR,
                        INVALID_EMAIL_FORMAT_MESSAGE),
                of(".email@example", generateValidPassword(), INVALID_EMAIL_FORMAT_ERROR,
                        INVALID_EMAIL_FORMAT_MESSAGE),
                of(generateRandomEmail(), null, INVALID_PASSWORD_FORMAT_ERROR,
                        INVALID_PASSWORD_ERR_MSG),
                of(generateRandomEmail(), "", INVALID_PASSWORD_FORMAT_ERROR,
                        INVALID_PASSWORD_ERR_MSG),
                of(generateRandomEmail(), "asdf", INVALID_PASSWORD_FORMAT_ERROR,
                        INVALID_PASSWORD_ERR_MSG),
                of(generateRandomEmail(),
                        generateRandomString(maxLengthPassword + 1),
                        INVALID_PASSWORD_FORMAT_ERROR,
                        INVALID_PASSWORD_ERR_MSG),
                of(generateRandomEmail(),
                        NON_LATIN_PWD,
                        INVALID_PASSWORD_FORMAT_ERROR,
                        INVALID_PASSWORD_ERR_MSG)
        );
    }

    private static Stream<Arguments> registerRequestCustomerApi_ValidParameters() {
        return Stream.of(
                of(generateRandomEmail(), generateValidPassword()),
                of(generateRandomEmail(), WHITE_SPACE_PWD)
        );
    }

    @ParameterizedTest(name = "Run {index}: emailAddress={0}, password={1}")
    @MethodSource("registerRequestCustomerApi_ValidParameters")
    @Tag(SMOKE_TEST)
    @UserStoryId(storyId = {2729, 2621})
    void shouldRegisterUserWithValidInput(String emailAddress, String password) {
        registerUserWithEmailAndPassword(emailAddress, password)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .body(Matchers.equalTo("{}"));
    }

    @Test
    @UserStoryId(storyId = 2621)
    void shouldNotRegisterUserTwice() {
        String customerEmail = generateRandomEmail();
        String customerPassword = generateValidPassword();
        registerUserWithEmailAndPassword(customerEmail, customerPassword)
                .then()
                .assertThat()
                .statusCode(SC_OK);

        registerUserWithEmailAndPassword(customerEmail, customerPassword)
                .then()
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .body(ERROR_FIELD, equalTo("LoginAlreadyInUse"))
                .body(MESSAGE_FIELD, equalTo("This login is already in use."));
    }

    @ParameterizedTest(name = "Run {index}: emailAddress={0}, password={1}, error={2}, message={3}")
    @MethodSource("registerRequestCustomerApi_InvalidParameters")
    @UserStoryId(storyId = {2729, 2621})
    void shouldNotRegisterWithInvalidInput(String emailAddress, String password, String error, String message) {
        registerUserWithEmailAndPassword(emailAddress, password)
                .then()
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .body(ERROR_FIELD, equalTo(error))
                .body(MESSAGE_FIELD, equalTo(message));
    }

    @ParameterizedTest
    @CsvSource(
            {"ya29.GltXB55iLxTmIjp4kD4PtLGEl4cEkqUQrqibJdbO3sTuv9jAalvWPQALunXit1nwQxEE2zL3twIKHxCUWt"
                    + "8dJSEHLEXo6AB1LcMk7UygftSXOYGiMp2LH79ncEHE",
                    "invalid.access.token.B1LcMk7UygftSXOYGiMp2LH79ncEHE"})
    @UserStoryId(storyId = {1569, 3768})
    void shouldNotRegisterWithInvalidGoogleAccessToken(String accessToken) {
        val googleRegistrationRequestObj = GoogleRegistrationRequestModel
                .builder()
                .accessToken(accessToken)
                .referralCode(generateRandomString(6))
                .firstName(firstName)
                .lastName(lastName)
                .build();

        registerWithGoogle(googleRegistrationRequestObj)
                .then()
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .body(ERROR_FIELD, equalTo(INVALID_OR_EXPIRED_GAT_ERR))
                .body(MESSAGE_FIELD, equalTo(INVALID_OR_EXPIRED_GAT_MSG));
    }

    @ParameterizedTest
    @CsvSource(
            {"ya29.GltXB55iLxTmIjp4kD4PtLGEl4cEkqUQrqibJdbO3sTuv9jAalvWPQALunXit1nwQxEE2zL3twIKHxCUWt"
                    + "8dJSEHLEXo6AB1LcMk7UygftSXOYGiMp2LH79ncEHE",
                    "invalid.access.token.B1LcMk7UygftSXOYGiMp2LH79ncEHE"})
    @UserStoryId(storyId = {1569, 3768})
    void shouldNotRegisterWithInvalidGoogleAccessTokenAndModelValidationFailure(String accessToken) {
        val googleRegistrationRequestObj = GoogleRegistrationRequestModel
                .builder()
                .accessToken(accessToken)
                .referralCode(generateRandomString(6))
                .build();

        registerWithGoogle(googleRegistrationRequestObj)
                .then()
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .body(ERROR_FIELD, equalTo(MODEL_VALIDATION_FAILURE))
                .body(MESSAGE_FIELD, equalTo(
                        THE_LAST_NAME_FIRST_NAME_PHONE_NUMBER_FIELDS_ARE_REQUIRED_ERROR_MESSAGE));
    }

    @Test
    @UserStoryId(storyId = {1569, 3768})
    void shouldNotRegisterWithEmptyGoogleAccessToken() {
        val googleRegistrationRequestObj = GoogleRegistrationRequestModel
                .builder()
                .accessToken(EMPTY)
                .referralCode(generateRandomString(6))
                .firstName(firstName)
                .lastName(lastName)
                .build();

        registerWithGoogle(googleRegistrationRequestObj)
                .then()
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .body(ERROR_FIELD, equalTo(MODEL_VALIDATION_FAILURE))
                .body(MESSAGE_FIELD, equalTo(GAT_IS_REQUIRED_MSG));
    }
}
