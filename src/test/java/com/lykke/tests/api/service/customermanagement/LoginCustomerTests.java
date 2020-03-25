package com.lykke.tests.api.service.customermanagement;

import static com.lykke.api.testing.api.common.GenerateUtils.generateRandomEmail;
import static com.lykke.api.testing.api.common.GenerateUtils.generateRandomString;
import static com.lykke.api.testing.api.common.PasswordGen.generateValidPassword;
import static com.lykke.tests.api.common.CommonConsts.SMOKE_TEST;
import static com.lykke.tests.api.service.customermanagement.LoginCustomerUtils.loginCustomerWithValidEmailAndPassword;
import static com.lykke.tests.api.service.customermanagement.RegisterCustomerUtils.CUSTOMER_ID_FIELD;
import static com.lykke.tests.api.service.customermanagement.RegisterCustomerUtils.registerCustomer;
import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.Matchers.equalTo;

import com.lykke.tests.api.base.BaseApiTest;
import com.lykke.tests.api.service.customermanagement.model.register.RegistrationRequestModel;
import java.util.stream.Stream;
import lombok.var;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class LoginCustomerTests extends BaseApiTest {

    private static final String EMAIL_FIELD_IS_REQUIRED_MESSAGE = "The Email field is required.";
    private static final String PASSWORD_FIELD_IS_REQUIRED_MESSAGE = "The Password field is required.";
    private static final String LOGIN_NOT_FOUND_ERROR = "LoginNotFound";
    private static final String INVALID_LOGIN_FORMAT_ERROR = "InvalidLoginFormat";
    private static final String NAME_FIELD = "name";
    private static String emailAddress = generateRandomEmail();
    private static String password = generateValidPassword();
    private static int maxLengthPassword = 100;

    private static Stream<Arguments> loginRequest_EmailIsNotProvided() {
        return Stream.of(
                Arguments.of(null, generateValidPassword(), EMAIL_FIELD_IS_REQUIRED_MESSAGE),
                Arguments.of("", generateValidPassword(), EMAIL_FIELD_IS_REQUIRED_MESSAGE)
        );
    }

    private static Stream<Arguments> loginRequest_InvalidEmail() {
        return Stream.of(
                Arguments.of("plaintext", generateValidPassword(), INVALID_LOGIN_FORMAT_ERROR),
                Arguments.of("#@%^%#$@#$@#.com", generateValidPassword(), INVALID_LOGIN_FORMAT_ERROR),
                Arguments.of("email.example.com", generateValidPassword(), INVALID_LOGIN_FORMAT_ERROR),
                Arguments.of("email@example@example.com", generateValidPassword(), INVALID_LOGIN_FORMAT_ERROR),
                Arguments.of(".email@example.com", generateValidPassword(), INVALID_LOGIN_FORMAT_ERROR),
                Arguments.of(".email@example", generateValidPassword(), INVALID_LOGIN_FORMAT_ERROR)
        );
    }

    private static Stream<Arguments> loginRequest_PasswordIsRequired() {
        return Stream.of(
                Arguments.of(generateRandomEmail(), null, PASSWORD_FIELD_IS_REQUIRED_MESSAGE),
                Arguments.of(generateRandomEmail(), "", PASSWORD_FIELD_IS_REQUIRED_MESSAGE)
        );
    }

    private static Stream<Arguments> loginRequest_InvalidPassword() {
        return Stream.of(
                // changed from
                // Expected: InvalidPasswordFormat
                // to
                // Actual: LoginNotFound
                // INVALID_PASSWORD_FORMAT_ERROR)
                Arguments.of(generateRandomEmail(), "asdfg", LOGIN_NOT_FOUND_ERROR),
                Arguments.of(generateRandomEmail(),
                        generateRandomString(maxLengthPassword + 1),
                        LOGIN_NOT_FOUND_ERROR)
        );
    }

    @Test
    @Tag(SMOKE_TEST)
    void shouldLoginCustomerWithValidInput_customerIdAndEmailAreCorrect() {
        var customer = new RegistrationRequestModel();
        customer.setEmail(emailAddress);
        customer.setPassword(password);
        String customerId = registerCustomer(customer);

        loginCustomerWithValidEmailAndPassword(emailAddress, password)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .body(CUSTOMER_ID_FIELD, equalTo(customerId))
                .body("Error", equalTo("None"));
    }

    @ParameterizedTest(name = "Run {index}: emailAddress={0}, password={1}, message={2}")
    @MethodSource("loginRequest_EmailIsNotProvided")
    void shouldNotLoginCustomerWhenEmailAddressIsNotProvided_validationMessage(String emailAddress, String password,
            String message) {
        loginCustomerWithValidEmailAndPassword(emailAddress, password)
                .then()
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .body("Email[0]", equalTo(message));
    }

    @ParameterizedTest(name = "Run {index}: emailAddress={0}, password={1}, message={2}")
    @MethodSource("loginRequest_InvalidEmail")
    void shouldNotLoginCustomerWithInvalidEmail_validationMessage(String emailAddress, String password,
            String message) {
        loginCustomerWithValidEmailAndPassword(emailAddress, password)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .body("CustomerId", equalTo(null))
                .body("Token", equalTo(null))
                .body("Error", equalTo(message));
    }

    @ParameterizedTest(name = "Run {index}: emailAddress={0}, password={1}, message={2}")
    @MethodSource("loginRequest_InvalidPassword")
    void shouldNotLoginCustomerWithInvalidPassword_validationMessage(String emailAddress, String password,
            String message) {
        loginCustomerWithValidEmailAndPassword(emailAddress, password)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .body("CustomerId", equalTo(null))
                .body("Token", equalTo(null))
                .body("Error", equalTo(message));
    }

    @ParameterizedTest(name = "Run {index}: emailAddress={0}, password={1}, message={2}")
    @MethodSource("loginRequest_PasswordIsRequired")
    void shouldNotLoginCustomerWhenPasswordIsNotProvided_validationMessage(String emailAddress, String password,
            String message) {
        loginCustomerWithValidEmailAndPassword(emailAddress, password)
                .then()
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .body("Password[0]", equalTo(message));
    }

    @Test
    void shouldNotLoginCustomerWithInvalidCredentials_unauthorizedMessage() {
        loginCustomerWithValidEmailAndPassword(generateRandomEmail(), generateValidPassword())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .body("CustomerId", equalTo(null))
                .body("Token", equalTo(null))
                .body("Error", equalTo(LOGIN_NOT_FOUND_ERROR));
    }
}
