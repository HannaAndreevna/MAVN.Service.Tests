package com.lykke.tests.api.service.customer;

import com.lykke.tests.api.base.BaseApiTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static com.lykke.api.testing.api.common.GenerateUtils.generateRandomEmail;
import static com.lykke.api.testing.api.common.GenerateUtils.generateRandomString;
import static com.lykke.tests.api.service.customer.LoginLogoutCustomerUtils.loginUserWithValidEmailAndPassword;
import static com.lykke.tests.api.service.customer.RegisterCustomerUtils.registerUser;
import static com.lykke.tests.api.service.customermanagement.RegisterCustomerUtils.TOKEN_FIELD;
import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.text.CharSequenceLength.hasLength;

public class LoginCustomerTests extends BaseApiTest {
    private static String emailAddress = generateRandomEmail();
    private static String password = generateRandomString();
    private static final String EMAIL_FIELD_IS_REQUIRED_MESSAGE = "The Email field is required.";
    private static final String VALID_EMAIL_FIELD_IS_REQUIRED_MESSAGE = "A valid email address is required.";
    private static final String PASSWORD_FIELD_IS_REQUIRED_MESSAGE = "The Password field is required.";
    private static final String INVALID_PASSWORD_LENGTH_MESSAGE = "Password length must be between 6 and 100";
    private static final String INVALID_EMAIL_FORMAT_MESSAGE = "Invalid email format";

    @Test
    void shouldLoginCustomerWithValidInput_customerIdAndEmailAreCorrect() {
        registerUser(emailAddress, password);

        loginUserWithValidEmailAndPassword(emailAddress, password)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .body(TOKEN_FIELD, hasLength(64));
    }

    @ParameterizedTest(name = "Run {index}: emailAddress={0}, password={1}, message={2}")
    @MethodSource("loginRequestCustomerApi_InvalidParameters")
    void shouldNotLoginCustomerWithInvalidInput_validationMessage(String emailAddress, String password, String message) {
        loginUserWithValidEmailAndPassword(emailAddress, password)
                .then()
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .body(containsString(message));
    }

    @Test
    void shouldNotLoginCustomerWithInvalidCredentials_unauthorizedMessage() {
        loginUserWithValidEmailAndPassword(generateRandomEmail(), generateRandomString())
                .then()
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .body(containsString("Attempted to perform an unauthorized operation."));
    }

    private static Stream<Arguments> loginRequestCustomerApi_InvalidParameters() {
        return Stream.of(
                Arguments.of(null, generateRandomString(), EMAIL_FIELD_IS_REQUIRED_MESSAGE),
                Arguments.of("", generateRandomString(), EMAIL_FIELD_IS_REQUIRED_MESSAGE),
                Arguments.of("plaintext", generateRandomString(), INVALID_EMAIL_FORMAT_MESSAGE),
                Arguments.of("#@%^%#$@#$@#.com", generateRandomString(), INVALID_EMAIL_FORMAT_MESSAGE),
                Arguments.of("email.example.com", generateRandomString(), INVALID_EMAIL_FORMAT_MESSAGE),
                Arguments.of("email@example@example.com", generateRandomString(), INVALID_EMAIL_FORMAT_MESSAGE),
                Arguments.of(".email@example.com", generateRandomString(), VALID_EMAIL_FIELD_IS_REQUIRED_MESSAGE),
                Arguments.of(".email@example", generateRandomString(), INVALID_EMAIL_FORMAT_MESSAGE),
                Arguments.of(generateRandomEmail(), null, PASSWORD_FIELD_IS_REQUIRED_MESSAGE),
                Arguments.of(generateRandomEmail(), "", PASSWORD_FIELD_IS_REQUIRED_MESSAGE),
                Arguments.of(generateRandomEmail(), "asdf", INVALID_PASSWORD_LENGTH_MESSAGE),
                Arguments.of(generateRandomEmail(),
                        generateRandomString(),
                        INVALID_PASSWORD_LENGTH_MESSAGE)
        );
    }
}
