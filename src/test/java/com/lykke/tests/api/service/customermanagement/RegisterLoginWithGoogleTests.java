package com.lykke.tests.api.service.customermanagement;

import static com.lykke.api.testing.api.common.GenerateUtils.generateRandomEmail;
import static com.lykke.api.testing.api.common.PasswordGen.generateValidPassword;
import static com.lykke.tests.api.service.customermanagement.LoginCustomerUtils.loginCustomer;
import static com.lykke.tests.api.service.customermanagement.RegisterCustomerUtils.registerCustomer;
import static com.lykke.tests.api.service.customermanagement.RegisterCustomerUtils.registerCustomerResponse;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.http.HttpStatus.SC_OK;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.lykke.api.testing.annotations.UserStoryId;
import com.lykke.tests.api.base.BaseApiTest;
import com.lykke.tests.api.service.customermanagement.model.login.AuthenticateRequestModel;
import com.lykke.tests.api.service.customermanagement.model.login.AuthenticateResponseModel;
import com.lykke.tests.api.service.customermanagement.model.register.LoginProvider;
import com.lykke.tests.api.service.customermanagement.model.register.RegistrationRequestModel;
import com.lykke.tests.api.service.customermanagement.model.register.RegistrationResponseModel;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class RegisterLoginWithGoogleTests extends BaseApiTest {

    private static final String ALREADY_REGISTERED_WITH_GOOGLE_ERR = "AlreadyRegisteredWithGoogle";
    private static final String ALREADY_REGISTERED_ERR = "AlreadyRegistered";
    private static final String LOGIN_EXISTS_ERR = "LoginExistsWithDifferentProvider";
    private static final String LOGIN_NOT_FOUND_ERR = "LoginNotFound";
    private static final String NONE_ERR = "None";
    private static final String CUSTOMER_ID_FIELD = "CustomerId";
    private static String emailAddress;
    private static String password;
    private static RegistrationRequestModel googleRegistrationObj;
    private static RegistrationRequestModel standardRegistrationObj;

    @BeforeEach
    void setup() {
        emailAddress = generateRandomEmail();
        password = generateValidPassword();
        googleRegistrationObj = RegistrationRequestModel
                .builder()
                .email(emailAddress)
                .referralCode(EMPTY)
                .password(EMPTY)
                .loginProvider(LoginProvider.GOOGLE)
                .build();

        standardRegistrationObj = RegistrationRequestModel
                .builder()
                .email(emailAddress)
                .referralCode(EMPTY)
                .password(password)
                .loginProvider(LoginProvider.STANDARD)
                .build();
    }

    @Test
    @UserStoryId(storyId = 1569)
    void shouldRegisterWithGoogle() {
        val actualResult = registerCustomerResponse(googleRegistrationObj)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(RegistrationResponseModel.class);

        assertAll(
                () -> assertNotNull(actualResult.getCustomerId()),
                () -> assertEquals(NONE_ERR, actualResult.getError())
        );
    }

    @Test
    @UserStoryId(storyId = 1569)
    void shouldNotRegisterWithGoogleWhenAlreadyRegisteredWithGoogle() {
        registerCustomer(googleRegistrationObj);

        val actualResult = registerCustomerResponse(googleRegistrationObj)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(RegistrationResponseModel.class);

        assertAll(
                () -> assertNull(actualResult.getCustomerId()),
                () -> assertEquals(ALREADY_REGISTERED_WITH_GOOGLE_ERR, actualResult.getError())
        );
    }

    @Test
    @UserStoryId(storyId = 1569)
    void shouldNotRegisterWithStandardProviderWhenAlreadyRegisteredWithGoogle() {
        registerCustomer(googleRegistrationObj);

        val actualResult = registerCustomerResponse(standardRegistrationObj)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(RegistrationResponseModel.class);

        assertAll(
                () -> assertNull(actualResult.getCustomerId()),
                () -> assertEquals(ALREADY_REGISTERED_WITH_GOOGLE_ERR, actualResult.getError())
        );
    }

    @Test
    @UserStoryId(storyId = 1569)
    void shouldNotRegisterWithGoogleWhenAlreadyRegisteredWithStandardProvider() {
        registerCustomer(standardRegistrationObj);

        val actualResult = registerCustomerResponse(googleRegistrationObj)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(RegistrationResponseModel.class);

        assertAll(
                () -> assertNull(actualResult.getCustomerId()),
                () -> assertEquals(ALREADY_REGISTERED_ERR, actualResult.getError())
        );
    }

    @Test
    @UserStoryId(storyId = 1569)
    void shouldLoginWithGoogle() {
        val customerId = registerCustomer(googleRegistrationObj);

        val loginRequestObj = AuthenticateRequestModel
                .builder()
                .email(emailAddress)
                .password(EMPTY)
                .loginProvider(LoginProvider.GOOGLE.getCode())
                .build();

        val actualResult = loginCustomer(loginRequestObj)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(AuthenticateResponseModel.class);

        assertAll(
                () -> assertEquals(customerId, actualResult.getCustomerId()),
                () -> assertNotNull(actualResult.getToken()),
                () -> assertEquals(NONE_ERR, actualResult.getError())
        );
    }

    @Test
    @UserStoryId(storyId = 1569)
    void shouldNotLoginWithGoogleWhenRegisteredWithStandardProvider() {
        registerCustomer(standardRegistrationObj);

        val loginRequestObj = AuthenticateRequestModel
                .builder()
                .email(emailAddress)
                .password(EMPTY)
                .loginProvider(LoginProvider.GOOGLE.getCode())
                .build();

        val actualResult = loginCustomer(loginRequestObj)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(AuthenticateResponseModel.class);

        assertAll(
                () -> assertNull(actualResult.getCustomerId()),
                () -> assertNull(actualResult.getToken()),
                () -> assertEquals(LOGIN_EXISTS_ERR, actualResult.getError())
        );
    }

    @Test
    @UserStoryId(storyId = 1569)
    void shouldNotLoginWithStandardProviderWhenRegisteredWithGoogle() {
        registerCustomer(googleRegistrationObj);

        val loginRequestObj = AuthenticateRequestModel
                .builder()
                .email(emailAddress)
                .password(password)
                .loginProvider(LoginProvider.STANDARD.getCode())
                .build();

        val actualResult = loginCustomer(loginRequestObj)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(AuthenticateResponseModel.class);

        assertAll(
                () -> assertNull(actualResult.getCustomerId()),
                () -> assertNull(actualResult.getToken()),
                () -> assertEquals(LOGIN_NOT_FOUND_ERR, actualResult.getError())
        );
    }
}
