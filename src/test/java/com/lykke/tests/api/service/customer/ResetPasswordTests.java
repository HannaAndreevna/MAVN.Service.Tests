package com.lykke.tests.api.service.customer;

import static com.lykke.api.testing.api.common.GenerateUtils.generateRandomEmail;
import static com.lykke.api.testing.api.common.GenerateUtils.getRandomUuid;
import static com.lykke.api.testing.api.common.PasswordGen.ALLOWED_SPECIAL_SYMBOLS;
import static com.lykke.api.testing.api.common.PasswordGen.ALLOW_WHITE_SPACES;
import static com.lykke.api.testing.api.common.PasswordGen.MAX_LENGTH;
import static com.lykke.api.testing.api.common.PasswordGen.MIN_LENGTH;
import static com.lykke.api.testing.api.common.PasswordGen.MIN_LOWER_CASE;
import static com.lykke.api.testing.api.common.PasswordGen.MIN_NUMBERS;
import static com.lykke.api.testing.api.common.PasswordGen.MIN_SPECIAL_SYMBOLS;
import static com.lykke.api.testing.api.common.PasswordGen.MIN_UPPER_CASE;
import static com.lykke.api.testing.api.common.PasswordGen.generateValidPassword;
import static com.lykke.tests.api.common.CommonConsts.EMAIL_FIELD;
import static com.lykke.tests.api.common.CommonConsts.SMOKE_TEST;
import static com.lykke.tests.api.service.credentials.model.PasswordResetError.IDENTIFIER_DOES_NOT_EXIST;
import static com.lykke.tests.api.service.customer.GeneratePwLinkUtils.generateResetPasswordLink;
import static com.lykke.tests.api.service.customer.ResetPasswordUtils.getPasswordValidationRules;
import static com.lykke.tests.api.service.customer.ResetPasswordUtils.getResetPasswordIdentifier;
import static com.lykke.tests.api.service.customer.ResetPasswordUtils.getValidationErrorResponse;
import static com.lykke.tests.api.service.customer.ResetPasswordUtils.validateResetPasswordIdentifier;
import static com.lykke.tests.api.service.customermanagement.RegisterCustomerUtils.registerCustomer;
import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.apache.http.HttpStatus.SC_NO_CONTENT;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.lykke.api.testing.annotations.UserStoryId;
import com.lykke.tests.api.base.BaseApiTest;
import com.lykke.tests.api.base.BasicFunctionalities;
import com.lykke.tests.api.common.model.ValidationErrorResponseModel;
import com.lykke.tests.api.service.customer.model.PasswordValidationRulesDto;
import com.lykke.tests.api.service.customer.model.ResetPasswordRequest;
import com.lykke.tests.api.service.customer.model.ValidateResetPasswordIdentifierRequest;
import com.lykke.tests.api.service.customermanagement.model.register.RegistrationRequestModel;
import java.util.stream.Stream;
import lombok.val;
import lombok.var;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class ResetPasswordTests extends BaseApiTest {

    private static final String WRONG_PASSWORD_LINK = "aaa";
    private static final String THE_PROVIDED_IDENTIFIER_DOES_NOT_EXIST_ERROR_MESSAGE = "The provided identifier does not exist";
    private static final String CUSTOMER_NOT_VERIFIED_ERR_MSG = "The customer is not verified";
    private static final String MESSAGE_FIELD = "message";

    private static Stream<Arguments> resetPassword_InvalidPassword() {
        return BasicFunctionalities.getInvalidPasswords();
    }

    @UserStoryId(storyId = 3371)
    @Test
    void shouldNotReceivePassResetLink_UserNotVerified() {
        var user = new RegistrationRequestModel();
        registerCustomer(user);
        generateResetPasswordLink(user.getEmail())
                .then()
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .body(MESSAGE_FIELD, equalTo(CUSTOMER_NOT_VERIFIED_ERR_MSG));
    }

    @UserStoryId(storyId = {1445, 2729})
    @ParameterizedTest(name = "Run {index}: invalidPassword={0}")
    @MethodSource("resetPassword_InvalidPassword")
    void shouldNotResetPassword(String invalidPassword) {

        val requestObject = ResetPasswordRequest
                .builder()
                .customerEmail(generateRandomEmail())
                .resetIdentifier(getRandomUuid())
                .password(invalidPassword)
                .build();

        val validationResponse = getValidationErrorResponse(requestObject);

        assertEquals(requestObject.getInvalidPasswordResponse(), validationResponse);
    }

    @Test
    @UserStoryId(2209)
    void shouldNotResetPasswordForNonExistingCustomer() {
        val requestObject = ResetPasswordRequest
                .builder()
                .customerEmail(generateRandomEmail())
                .resetIdentifier(getRandomUuid())
                .password(generateValidPassword())
                .build();

        val validationResponse = getValidationErrorResponse(requestObject);

        assertEquals(requestObject.getNonExistingCustomerResponse(), validationResponse);
    }

    @Test
    @Tag(SMOKE_TEST)
    @UserStoryId(2209)
    void shouldGetPasswordValidationRules() {
        val expectedRules = PasswordValidationRulesDto
                .builder()
                .minLength(MIN_LENGTH)
                .maxLength(MAX_LENGTH)
                .minUpperCase(MIN_UPPER_CASE)
                .minLowerCase(MIN_LOWER_CASE)
                .minSpecialSymbols(MIN_SPECIAL_SYMBOLS)
                .minNumbers(MIN_NUMBERS)
                .allowedSpecialSymbols(ALLOWED_SPECIAL_SYMBOLS)
                .allowWhiteSpaces(!ALLOW_WHITE_SPACES)
                .build();

        val actualRules = getPasswordValidationRules()
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(PasswordValidationRulesDto.class);

        assertEquals(expectedRules, actualRules);
    }

    @Test
    @UserStoryId(2209)
    void shouldValidateResetPasswordIdentifier() {
        var user = new RegistrationRequestModel();
        registerCustomer(user);
        generateResetPasswordLink(user.getEmail())
                .then()
                .assertThat()
                .statusCode(SC_NO_CONTENT);

        val passwordLink = getResetPasswordIdentifier(user.getEmail());

        validateResetPasswordIdentifier(ValidateResetPasswordIdentifierRequest
                .builder()
                .resetPasswordIdentifier(passwordLink)
                .build())
                .then()
                .assertThat()
                .statusCode(SC_NO_CONTENT);

        val requestObject = ResetPasswordRequest
                .builder()
                .customerEmail(generateRandomEmail())
                .resetIdentifier(getRandomUuid())
                .password(passwordLink)
                .build();

        val validationResponse = getValidationErrorResponse(requestObject);

        assertEquals(requestObject.getInvalidPasswordResponse(), validationResponse);
    }

    @Test
    @UserStoryId(2209)
    void shouldNotValidateInvalidResetPasswordIdentifier() {
        var user = new RegistrationRequestModel();
        registerCustomer(user);
        generateResetPasswordLink(user.getEmail())
                .then()
                .assertThat()
                .statusCode(SC_NO_CONTENT);

        val passwordLink = WRONG_PASSWORD_LINK;

        val expectedResult = ValidationErrorResponseModel
                .builder()
                .error(IDENTIFIER_DOES_NOT_EXIST.getCode())
                .message(THE_PROVIDED_IDENTIFIER_DOES_NOT_EXIST_ERROR_MESSAGE)
                .build();

        val actualResult = validateResetPasswordIdentifier(ValidateResetPasswordIdentifierRequest
                .builder()
                .resetPasswordIdentifier(passwordLink)
                .build())
                .then()
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .extract()
                .as(ValidationErrorResponseModel.class);

        assertEquals(expectedResult, actualResult);

        val requestObject = ResetPasswordRequest
                .builder()
                .customerEmail(generateRandomEmail())
                .resetIdentifier(getRandomUuid())
                .password(passwordLink)
                .build();

        val validationResponse = getValidationErrorResponse(requestObject);

        assertEquals(requestObject.getInvalidPasswordResponse(), validationResponse);
    }
}
