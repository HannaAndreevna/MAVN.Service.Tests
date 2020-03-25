package com.lykke.tests.api.service.customer;

import static com.lykke.tests.api.base.Paths.CUSTOMER_API_EMAILS_PATH;
import static com.lykke.api.testing.api.base.RequestHeader.getHeader;
import static com.lykke.tests.api.common.CommonConsts.VALID_PASSWORD;
import static com.lykke.api.testing.api.common.GenerateUtils.generateRandomEmail;
import static com.lykke.tests.api.service.customer.LoginLogoutCustomerUtils.getUserToken;
import static com.lykke.tests.api.service.customer.model.VerificationCodeError.REACHED_MAXIMUM_REQUEST_FOR_PERIOD;
import static com.lykke.tests.api.service.customermanagement.RegisterCustomerUtils.registerCustomer;
import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.apache.http.HttpStatus.SC_NO_CONTENT;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.lykke.api.testing.annotations.UserStoryId;
import com.lykke.tests.api.base.BaseApiTest;
import com.lykke.tests.api.service.customer.model.VerificationCodeResponseModel;
import java.util.stream.IntStream;

import com.lykke.tests.api.service.customermanagement.model.register.RegistrationRequestModel;
import lombok.val;
import lombok.var;
import org.junit.jupiter.api.Test;

public class EmailVerificationTests extends BaseApiTest {

    @Test
    @UserStoryId(650)
    void shouldVerifyEmail() {
        var customer = new RegistrationRequestModel();
        registerCustomer(customer);
        val token = getUserToken(customer);

        getHeader(token)
                .post(CUSTOMER_API_EMAILS_PATH)
                .then()
                .assertThat()
                .statusCode(SC_NO_CONTENT);
    }

    @Test
    @UserStoryId(650)
    void shouldNotVerifyAlreadyVerifiedEmail() {
        var customer = new RegistrationRequestModel();
        registerCustomer(customer);
        var token = getUserToken(customer);

        getHeader(token)
                .post(CUSTOMER_API_EMAILS_PATH)
                .then()
                .assertThat()
                .statusCode(SC_NO_CONTENT);
    }

    @Test
    @UserStoryId(650)
    void shouldNotVerifyEmailOfNonExistingCustomer() {
        var customer = new RegistrationRequestModel();
        registerCustomer(customer);
        var token = getUserToken(customer);

      /*  deleteCustomerCredentials(emailAddress)
                .then()
                .assertThat()
                .statusCode(SC_OK);*/

        getHeader(token)
                .post(CUSTOMER_API_EMAILS_PATH)
                .then()
                .assertThat()
                .statusCode(SC_NO_CONTENT);
    }

    @Test
    @UserStoryId(650)
    void shouldNotVerifyEmailIfMaximumRequestReached() {
        var customer = new RegistrationRequestModel();
        registerCustomer(customer);
        var token = getUserToken(customer.getEmail(), customer.getPassword());

        IntStream.range(0, 10)
                .forEach(retryAttempt -> {
                    getHeader(token)
                            .post(CUSTOMER_API_EMAILS_PATH);
                });

        val actualResult = getHeader(token)
                .post(CUSTOMER_API_EMAILS_PATH)
                .then()
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .extract()
                .as(VerificationCodeResponseModel.class);

        assertAll(
                () -> assertEquals(REACHED_MAXIMUM_REQUEST_FOR_PERIOD, actualResult.getError()),
                () -> assertEquals(REACHED_MAXIMUM_REQUEST_FOR_PERIOD.getMessage(),
                        actualResult.getError().getMessage())

        );
    }
}
