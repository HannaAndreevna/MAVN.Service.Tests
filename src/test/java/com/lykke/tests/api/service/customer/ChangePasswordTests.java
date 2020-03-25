package com.lykke.tests.api.service.customer;

import static com.lykke.api.testing.api.common.GenerateUtils.generateRandomEmail;
import static com.lykke.api.testing.api.common.GenerateUtils.generateRandomString;
import static com.lykke.api.testing.api.common.PasswordGen.generateValidPassword;
import static com.lykke.api.testing.api.common.PasswordGen.generateInvalidPassword;
import static com.lykke.tests.api.common.CommonConsts.INVALID_PASSWORD_ERR_MSG;
import static com.lykke.tests.api.common.CommonConsts.NON_LATIN_PWD;
import static com.lykke.tests.api.common.CommonConsts.WHITE_SPACE_PWD;
import static com.lykke.tests.api.service.customer.ChangePasswordUtils.changePassword;
import static com.lykke.tests.api.service.customer.LoginLogoutCustomerUtils.getUserToken;
import static com.lykke.tests.api.service.customer.LoginLogoutCustomerUtils.loginUser;
import static com.lykke.tests.api.service.customermanagement.RegisterCustomerUtils.registerCustomer;
import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.params.provider.Arguments.of;

import com.lykke.api.testing.annotations.UserStoryId;
import com.lykke.tests.api.base.BaseApiTest;
import java.util.stream.Stream;

import com.lykke.tests.api.service.customermanagement.model.register.RegistrationRequestModel;
import lombok.var;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class ChangePasswordTests extends BaseApiTest {

    private static String TOKEN_FIELD = "Token";
    private static String MSG_FIELD = "message";

    private static String INVALID_CREDENTIALS_ERR_MSG = "Login or password is not valid.";

    private static String PASS_INVALID_SHORT = generateInvalidPassword(3, 3, true, true, true, true);
    private static String PASS_INVALID_LONG = generateInvalidPassword(101, 102, true, true, true, true);

    private static String customerToken;
    private static String customerEmail;
    private static String customerPass;
    private static String newToken;

    private static Stream<Arguments> changePassword_Invalid() {
        return Stream.of(
                of(PASS_INVALID_SHORT),
                of(PASS_INVALID_LONG),
                of(NON_LATIN_PWD)
        );
    }

    private static Stream<Arguments> registerRequestCustomerApi_ValidParameters() {
        return Stream.of(
                of(generateValidPassword()),
                of(WHITE_SPACE_PWD)
        );
    }

    @BeforeEach
    void methodSetup() {
        var customer = new RegistrationRequestModel();
        registerCustomer(customer);
        customerToken = getUserToken(customer);
    }

    @ParameterizedTest(name = "Run {index}: validPassword={0}")
    @MethodSource("registerRequestCustomerApi_ValidParameters")
    @UserStoryId(storyId = {2328,2729})
    void shouldRelogAfterPasswordChange(String validPassword) {

        newToken = changePassword(customerToken, validPassword)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .path(TOKEN_FIELD);

        assertNotEquals(customerToken, newToken);

        AgentsUtils.getAgents(newToken);
    }

    @ParameterizedTest(name = "Run {index}: validPassword={0}")
    @MethodSource("registerRequestCustomerApi_ValidParameters")
    @UserStoryId(storyId = {873, 2729})
    void shouldChangeCustomerPassword_Valid(String validPassword) {

        changePassword(customerToken, validPassword)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .body(TOKEN_FIELD, notNullValue());

        loginUser(customerEmail, validPassword)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .body(TOKEN_FIELD, notNullValue());
    }

    @ParameterizedTest(name = "Run {index}: newPassword={0}")
    @UserStoryId(storyId = {873, 2729})
    @MethodSource("changePassword_Invalid")
    void shouldNotChangeCustomerPassword_Invalid(String newPassword) {
        changePassword(customerToken, newPassword)
                .then()
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .body(MSG_FIELD, equalTo(INVALID_PASSWORD_ERR_MSG));

        loginUser(customerEmail, newPassword)
                .then()
                .assertThat()
                .statusCode(SC_UNAUTHORIZED)
                .body(MSG_FIELD, equalTo(INVALID_CREDENTIALS_ERR_MSG));
    }
}
