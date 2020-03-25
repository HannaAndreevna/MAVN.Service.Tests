package com.lykke.tests.api.service.customer;

import static com.lykke.tests.api.common.CommonConsts.ERROR_FIELD;
import static com.lykke.tests.api.common.CommonConsts.MESSAGE_FIELD;
import static com.lykke.tests.api.service.customer.GeneratePwLinkUtils.generateResetPasswordLink;
import static com.lykke.tests.api.service.customer.RegisterCustomerUtils.registerUser;
import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.apache.http.HttpStatus.SC_NO_CONTENT;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;

import com.lykke.api.testing.annotations.UserStoryId;
import com.lykke.tests.api.base.BaseApiTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class GenerateResetPasswordLinkTests extends BaseApiTest {

    private static String email;
    private static String pw;

    @BeforeEach
    void userSetup() {
        registerUser();
    }

    @Test
    @UserStoryId(storyId = 538)
    void shouldGenerateResetPasswordLink() {
        generateResetPasswordLink(email)
                .then()
                .assertThat()
                .statusCode(SC_NO_CONTENT);
    }

    @ParameterizedTest
    @CsvSource({
            "' ', InvalidEmailFormat, The Email field is required.",
            "abc, InvalidEmailFormat, The field Email must match the regular expression '\\A(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?)\\Z'.",
            "0000123abcxx@example.com, NoCustomerWithSuchEmail, Customer with such email does not exist"
    })
    @UserStoryId(storyId = 538)
    void shouldNotGenerateResetPasswordLinkForInvalidInput(String emailAddress, String error, String message) {
        generateResetPasswordLink(emailAddress)
                .then()
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .body(ERROR_FIELD, equalTo(error))
                .body(MESSAGE_FIELD, containsString(message));
    }
}
