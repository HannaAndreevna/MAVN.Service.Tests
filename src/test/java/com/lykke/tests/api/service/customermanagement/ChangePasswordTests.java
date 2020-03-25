package com.lykke.tests.api.service.customermanagement;

import static com.lykke.api.testing.api.common.GenerateUtils.generateRandomEmail;
import static com.lykke.api.testing.api.common.PasswordGen.generateValidPassword;
import static com.lykke.tests.api.service.customermanagement.ChangePasswordUtils.changeCustomerPassword;
import static com.lykke.tests.api.service.customermanagement.LoginCustomerUtils.loginCustomerWithValidEmailAndPassword;
import static com.lykke.tests.api.service.customermanagement.RegisterCustomerUtils.registerCustomer;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

import com.lykke.api.testing.annotations.UserStoryId;
import com.lykke.tests.api.base.BaseApiTest;
import com.lykke.tests.api.service.customermanagement.model.register.RegistrationRequestModel;
import lombok.var;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class ChangePasswordTests extends BaseApiTest {

    private static final String ERROR_FIELD = "Error";
    private static final String TOKEN_FIELD = "Token";
    private static final String PASSWORD_MISMATCH_ERROR = "PasswordMismatch";
    private static final String INVALID_PW_FORMAT = "InvalidPasswordFormat";

    @Test
    @UserStoryId(storyId = 537)
    void shouldChangeUserPassword() {
        var customer = new RegistrationRequestModel();
        String customerId = registerCustomer(customer);
        String newPassword = generateValidPassword();

        changeCustomerPassword(customerId, newPassword)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .body(ERROR_FIELD, equalTo("None"));

        loginCustomerWithValidEmailAndPassword(customer.getEmail(), customer.getPassword())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .body(ERROR_FIELD, equalTo(PASSWORD_MISMATCH_ERROR));

        loginCustomerWithValidEmailAndPassword(customer.getEmail(), newPassword)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .body(TOKEN_FIELD, notNullValue());
    }

    @ParameterizedTest
    @ValueSource(strings = {"-", "a", "asdfg", "00"})
    @UserStoryId(storyId = 537)
    void shouldNotUpdatePwToInvalidValues(String newInvalidPw) {
        String customerId = registerCustomer();

        changeCustomerPassword(customerId, newInvalidPw)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .body(ERROR_FIELD, equalTo(INVALID_PW_FORMAT));
    }

    @ParameterizedTest
    @ValueSource(strings = {"-", "a", "asdfg", "00"})
    @UserStoryId(storyId = 691)
    void shouldNotUpdatePwToInvalidCustomerId(String customerId) {
        changeCustomerPassword(customerId, generateValidPassword())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .body(ERROR_FIELD, equalTo("LoginNotFound"));
    }
}
