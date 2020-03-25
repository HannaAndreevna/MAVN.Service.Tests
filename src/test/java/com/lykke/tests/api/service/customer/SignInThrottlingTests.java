package com.lykke.tests.api.service.customer;

import static com.lykke.api.testing.api.common.GenerateUtils.generateRandomEmail;
import static com.lykke.api.testing.api.common.PasswordGen.generateValidPassword;
import static com.lykke.tests.api.common.CommonConsts.SignIn.NUMBER_OF_ATTEMPTS_BEFORE_LOCK;
import static com.lykke.tests.api.common.CommonConsts.SignIn.STATUS_CODE_TOO_MANY_ATTEMPTS;
import static com.lykke.tests.api.service.customer.LoginLogoutCustomerUtils.loginUserWithValidEmailAndPassword;
import static com.lykke.tests.api.service.customermanagement.RegisterCustomerUtils.registerDefaultCustomer;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;

import com.lykke.api.testing.annotations.UserStoryId;
import com.lykke.tests.api.base.BaseApiTest;
import java.util.stream.IntStream;
import lombok.val;
import org.junit.jupiter.api.Test;

public class SignInThrottlingTests extends BaseApiTest {

    @Test
    @UserStoryId(3003)
    void shouldGetUnauthorizedOnSingleSignInAttempt() {
        val email = generateRandomEmail();
        val password = generateValidPassword();
        registerDefaultCustomer(email, password);
        IntStream.range(0, NUMBER_OF_ATTEMPTS_BEFORE_LOCK)
                .forEach(index -> loginUserWithValidEmailAndPassword(email, generateValidPassword())
                        .then()
                        .assertThat()
                        .statusCode(SC_UNAUTHORIZED));
    }

    @Test
    @UserStoryId(3003)
    void shouldGetTooManyAttemptsOnMultipleSignInAttempt() {
        val email = generateRandomEmail();
        val password = generateValidPassword();
        registerDefaultCustomer(email, password);
        IntStream.range(0, NUMBER_OF_ATTEMPTS_BEFORE_LOCK)
                .forEach(index -> loginUserWithValidEmailAndPassword(email, generateValidPassword())
                        .then()
                        .assertThat()
                        .statusCode(SC_UNAUTHORIZED));
        loginUserWithValidEmailAndPassword(email, generateValidPassword())
                .then()
                .assertThat()
                .statusCode(STATUS_CODE_TOO_MANY_ATTEMPTS);
    }
}
