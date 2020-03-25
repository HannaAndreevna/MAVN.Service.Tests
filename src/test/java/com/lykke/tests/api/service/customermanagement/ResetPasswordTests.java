package com.lykke.tests.api.service.customermanagement;

import static com.lykke.api.testing.api.common.GenerateUtils.generateRandomEmail;
import static com.lykke.api.testing.api.common.GenerateUtils.generateRandomString;
import static com.lykke.api.testing.api.common.PasswordGen.generateValidPassword;
import static com.lykke.tests.api.service.credentials.CredentialsUtils.resetCustomerIdentifier;
import static com.lykke.tests.api.service.customer.LoginLogoutCustomerUtils.getUserToken;
import static com.lykke.tests.api.service.customermanagement.LoginCustomerUtils.loginCustomerWithValidEmailAndPassword;
import static com.lykke.tests.api.service.customermanagement.RegisterCustomerUtils.registerCustomer;
import static com.lykke.tests.api.service.customermanagement.ResetPasswordUtils.resetPassword;
import static com.lykke.tests.api.service.customermanagement.ResetPasswordUtils.sendResetPasswordIdentifierByEmail;
import static org.apache.http.HttpStatus.SC_NOT_FOUND;
import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

import com.lykke.api.testing.annotations.UserStoryId;
import com.lykke.tests.api.base.BaseApiTest;
import com.lykke.tests.api.service.customer.CustomerInfoUtils;
import com.lykke.tests.api.service.customermanagement.model.register.RegistrationRequestModel;
import lombok.val;
import lombok.var;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public class ResetPasswordTests extends BaseApiTest {

    public static final String IDENTIFIER_MISMATCH = "IdentifierMismatch";
    public static final String THERE_IS_NO_IDENTIFIER_FOR_THIS_CUSTOMER = "ThereIsNoIdentifierForThisCustomer";
    public static final String CUSTOMER_DOES_NOT_EXIST = "CustomerDoesNotExist";
    public static final String CUSTOMER_NOT_VERIFIED = "CustomerIsNotVerified";
    private static final String ERROR_FIELD = "Error";
    private static final int RESET_IDENTIFIER_LENGTH = 30;
    private static final String NONE_ERROR = "None";
    private static final String IDENTIFIER_FIELD = "Identifier";
    private static String emailAddress;
    private static String pw;
    private static String customerId;

    @BeforeEach
    void setupUser() {
        var customer = new RegistrationRequestModel();
        emailAddress = customer.getEmail();
        pw = customer.getPassword();
        customerId = registerCustomer(customer);
    }

    @Test
    @UserStoryId(storyId = 3414)
    void shouldNotReceivePassResetLink_CustomerNotVerified() {
        sendResetPasswordIdentifierByEmail(emailAddress)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .body(ERROR_FIELD, equalTo(CUSTOMER_NOT_VERIFIED));
    }

    @Test
    @UserStoryId(storyId = 542)
    void shouldResetPwSuccessfully() {
        String resetIdentifier = resetCustomerIdentifier(customerId)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .path("Identifier");

        String newPw = generateValidPassword();
        resetPassword(emailAddress, resetIdentifier, newPw)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .body(ERROR_FIELD, equalTo("None"));

        loginCustomerWithValidEmailAndPassword(emailAddress, pw)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .body(ERROR_FIELD, equalTo("PasswordMismatch"));

        loginCustomerWithValidEmailAndPassword(emailAddress, newPw)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .body("Token", notNullValue());
    }

    @Test
    @UserStoryId(storyId = 542)
    void shouldNotResetPwWithInvalidResetIdentifier() {
        resetCustomerIdentifier(customerId)
                .then()
                .assertThat()
                .statusCode(SC_OK);

        resetPassword(emailAddress, generateRandomString(RESET_IDENTIFIER_LENGTH), generateValidPassword())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .body(ERROR_FIELD, equalTo(IDENTIFIER_MISMATCH));
    }

    @Test
    @UserStoryId(storyId = 542)
    void shouldReturnErrorIfCustomerHasNoIdentifier() {
        resetPassword(emailAddress, generateRandomString(RESET_IDENTIFIER_LENGTH), generateValidPassword())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .body(ERROR_FIELD, equalTo(THERE_IS_NO_IDENTIFIER_FOR_THIS_CUSTOMER));
    }

    @Test
    @UserStoryId(storyId = 542)
    void shouldNotUpdatePasswordForNonExistingCustomer() {
        resetPassword(generateRandomEmail(), generateRandomString(RESET_IDENTIFIER_LENGTH), generateValidPassword())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .body(ERROR_FIELD, equalTo(CUSTOMER_DOES_NOT_EXIST));
    }

    @Disabled("real verification of email will be done with implementation of email server")
    @Test
    @UserStoryId(storyId = 539)
    void shouldSendPasswordResendEmail() {
        // TODO: real verification of email will be done with implementation of email server
        sendResetPasswordIdentifierByEmail(emailAddress)
                .then()
                .assertThat()
                .statusCode(SC_OK);
    }

    @Test
    @UserStoryId(storyId = 539)
    void shouldNotSendPasswordResendEmailIfCustomerDoesNotExist() {
        sendResetPasswordIdentifierByEmail(generateRandomEmail())
                .then()
                .assertThat()
                .statusCode(SC_NOT_FOUND);
    }

    @Test
    @UserStoryId(storyId = 692)
    void oldTokenShouldNotWorkAfterPasswordReset() {
        val customerToken = getUserToken(emailAddress, pw);

        String resetIdentifier = resetCustomerIdentifier(customerId)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .path(IDENTIFIER_FIELD);

        String newPass = generateValidPassword();

        resetPassword(emailAddress, resetIdentifier, newPass)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .body(ERROR_FIELD, equalTo(NONE_ERROR));

        CustomerInfoUtils.getCurrentCustomerInfo(customerToken)
                .then()
                .assertThat()
                .statusCode(SC_UNAUTHORIZED);
    }
}
