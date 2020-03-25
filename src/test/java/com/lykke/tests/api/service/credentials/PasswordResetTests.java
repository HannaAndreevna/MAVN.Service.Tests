package com.lykke.tests.api.service.credentials;

import static com.lykke.api.testing.api.common.GenerateUtils.generateRandomEmail;
import static com.lykke.api.testing.api.common.GenerateUtils.generateRandomString;
import static com.lykke.api.testing.api.common.PasswordGen.generateValidPassword;
import static com.lykke.tests.api.service.credentials.CredentialsUtils.resetCustomerIdentifier;
import static com.lykke.tests.api.service.credentials.CredentialsUtils.resetPassword;
import static com.lykke.tests.api.service.credentials.model.PasswordResetError.NONE;
import static com.lykke.tests.api.service.customermanagement.LoginCustomerUtils.loginCustomerWithValidEmailAndPassword;
import static com.lykke.tests.api.service.customermanagement.RegisterCustomerUtils.registerCustomer;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

import com.lykke.api.testing.annotations.UserStoryId;
import com.lykke.tests.api.base.BaseApiTest;
import com.lykke.tests.api.service.credentials.model.PasswordResetErrorResponse;
import com.lykke.tests.api.service.customermanagement.model.register.RegistrationRequestModel;
import lombok.val;
import lombok.var;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PasswordResetTests extends BaseApiTest {

    public static final String IDENTIFIER_MISMATCH = "IdentifierMismatch";
    public static final String THERE_IS_NO_IDENTIFIER_FOR_THIS_CUSTOMER = "ThereIsNoIdentifierForThisCustomer";
    public static final String CUSTOMER_DOES_NOT_EXIST = "CustomerDoesNotExist";
    private static final int RESET_IDENTIFIER_LENGTH = 30;
    private static final String ERROR_FIELD = "Error";
    private static String email;
    private static String pw;
    private static String customerId;

    @BeforeEach
    void setupUser() {
        var customer = new RegistrationRequestModel();
        email = customer.getEmail();
        pw = customer.getPassword();
        customerId = registerCustomer(customer);
    }

    @Test
    @UserStoryId(storyId = 543)
    void shouldResetPwSuccessfully() {
        String resetIdentifier = resetCustomerIdentifier(customerId)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .path("Identifier");

        String newPw = generateValidPassword();
        val actualResetPasswordResult = resetPassword(email, resetIdentifier, newPw)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(PasswordResetErrorResponse.class);
        Assertions.assertEquals(NONE, actualResetPasswordResult.getError());

        loginCustomerWithValidEmailAndPassword(email, pw)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .body(ERROR_FIELD, equalTo("PasswordMismatch"));

        loginCustomerWithValidEmailAndPassword(email, newPw)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .body("Token", notNullValue());
    }

    @Test
    @UserStoryId(storyId = 543)
    void shouldNotResetPwWithInvalidResetIdentifier() {
        resetCustomerIdentifier(customerId)
                .then()
                .assertThat()
                .statusCode(SC_OK);

        resetPassword(email, generateRandomString(RESET_IDENTIFIER_LENGTH), generateValidPassword())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .body(ERROR_FIELD, equalTo(IDENTIFIER_MISMATCH));
    }

    @Test
    @UserStoryId(storyId = 543)
    void shouldReturnErrorIfCustomerHasNoIdentifier() {
        resetPassword(email, generateRandomString(RESET_IDENTIFIER_LENGTH), generateValidPassword())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .body(ERROR_FIELD, equalTo(THERE_IS_NO_IDENTIFIER_FOR_THIS_CUSTOMER));
    }

    @Test
    @UserStoryId(storyId = 543)
    void shouldNotUpdatePasswordForNonExistingCustomer() {
        resetPassword(generateRandomEmail(), generateRandomString(RESET_IDENTIFIER_LENGTH), generateValidPassword())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .body(ERROR_FIELD, equalTo(CUSTOMER_DOES_NOT_EXIST));
    }
}
