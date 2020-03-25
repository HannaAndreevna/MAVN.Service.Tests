package com.lykke.tests.api.service.admin;

import static com.lykke.api.testing.api.common.GenerateUtils.generateRandomEmail;
import static com.lykke.api.testing.api.common.PasswordGen.generateValidPassword;
import static com.lykke.tests.api.common.CommonConsts.ERROR_FIELD;
import static com.lykke.tests.api.common.CommonConsts.MESSAGE_FIELD;
import static com.lykke.tests.api.common.CommonConsts.SMOKE_TEST;
import static com.lykke.tests.api.service.admin.LoginAdminUtils.loginAdminWithValidEmailAndPassword;
import static com.lykke.tests.api.service.adminmanagement.RegisterAdminUtils.registerAdmin;
import static com.lykke.tests.api.service.customermanagement.RegisterCustomerUtils.TOKEN_FIELD;
import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.apache.http.HttpStatus.SC_FORBIDDEN;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.text.CharSequenceLength.hasLength;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.params.provider.Arguments.of;

import com.lykke.api.testing.annotations.UserStoryId;
import com.lykke.api.testing.api.common.FakerUtils;
import com.lykke.tests.api.base.BaseApiTest;
import com.lykke.tests.api.service.admin.model.auth.AdminLogInResponseModel;
import java.util.stream.Stream;
import lombok.val;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class LoginAdminTests extends BaseApiTest {

    private static final String EMAIL_FIELD_IS_REQUIRED_MESSAGE = "The Email field is required.";
    private static final String PASSWORD_IS_REQUIRED_MESSAGE = "The Password field is required.";
    private static final String INVALID_EMAIL_FORMAT_MESSAGE = "Invalid email format.";
    private static final String UNAUTHORIZED_OPERATION_MESSAGE = "Login or password is not valid.";
    private static final String INVALID_EMAIL_FORMAT_ERROR = "InvalidEmailFormat";
    private static final String EMAIL_VALIDATION_ERROR = "Email[0]";
    private static final String PW_VALIDATION_ERROR = "Password[0]";
    private static final String INVALID_EMAIL_OR_PASSWORD_FORMAT_ERROR = "InvalidEmailOrPasswordFormat";
    private static final String INVALID_EMAIL_OR_PASSWORD_FORMAT_MESSAGE = "Invalid email or password format.";
    private static final String NAME_FIELD = "name";
    private static String emailAddress = generateRandomEmail();
    private static String password = generateValidPassword();
    private static String firstName = FakerUtils.firstName;
    private static String lastName = FakerUtils.lastName;

    private static Stream<Arguments> loginRequestAdminApi_InvalidParameters() {
        return Stream.of(
                of("plaintext", generateValidPassword(), INVALID_EMAIL_FORMAT_ERROR,
                        INVALID_EMAIL_FORMAT_MESSAGE),
                of("#@%^%#$@#$@#.com", generateValidPassword(), INVALID_EMAIL_FORMAT_ERROR,
                        INVALID_EMAIL_FORMAT_MESSAGE),
                of("email.example.com", generateValidPassword(), INVALID_EMAIL_FORMAT_ERROR,
                        INVALID_EMAIL_FORMAT_MESSAGE),
                of("email@example@example.com", generateValidPassword(), INVALID_EMAIL_FORMAT_ERROR,
                        INVALID_EMAIL_FORMAT_MESSAGE),
                of(".email@example.com", generateValidPassword(), INVALID_EMAIL_OR_PASSWORD_FORMAT_ERROR,
                        INVALID_EMAIL_OR_PASSWORD_FORMAT_MESSAGE),
                of(".email@example", generateValidPassword(), INVALID_EMAIL_FORMAT_ERROR,
                        INVALID_EMAIL_FORMAT_MESSAGE)
        );
    }

    private static Stream<Arguments> loginRequestAdminApi_NonExistEmail() {
        return Stream.of(
                of(null, generateValidPassword(), EMAIL_FIELD_IS_REQUIRED_MESSAGE),
                of("", generateValidPassword(), EMAIL_FIELD_IS_REQUIRED_MESSAGE)
        );
    }

    private static Stream<Arguments> loginRequestAdminApi_NonExistPw() {
        return Stream.of(
                of(generateRandomEmail(), null, PASSWORD_IS_REQUIRED_MESSAGE),
                of(generateRandomEmail(), "", PASSWORD_IS_REQUIRED_MESSAGE)
        );
    }

    @Test
    @UserStoryId(storyId = 2630)
    void shouldReturnAdminUserInfo() {
        registerAdmin(emailAddress, password, firstName, lastName);

        val actualResult = loginAdminWithValidEmailAndPassword(emailAddress, password)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(AdminLogInResponseModel.class);

        val actualAdmin = actualResult.getAdminUser();

        assertAll(
                () -> assertEquals(emailAddress, actualAdmin.getEmail()),
                () -> assertEquals(firstName, actualAdmin.getFirstName()),
                () -> assertEquals(lastName, actualAdmin.getLastName()),
                () -> assertNotNull(actualAdmin.getId())
        );
    }

    @Test
    @Tag(SMOKE_TEST)
    @UserStoryId(storyId = 226)
    void shouldLoginAdminWithValidInput_adminIdAndEmailAreCorrect() {
        registerAdmin(emailAddress, password, firstName, lastName);

        loginAdminWithValidEmailAndPassword(emailAddress, password)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .body(TOKEN_FIELD, hasLength(64));
    }

    // TODO: response code change from 400 to 403
    @UserStoryId(storyId = 226)
    @ParameterizedTest(name = "Run {index}: emailAddress={0}, password={1}, error={2}, messageText={3}")
    @MethodSource("loginRequestAdminApi_InvalidParameters")
    void shouldNotLoginAdminWithInvalidInput_validationMessage(String emailAddress, String password, String error,
            String messageText) {
        loginAdminWithValidEmailAndPassword(emailAddress, password)
                .then()
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .body(ERROR_FIELD, equalTo(error))
                .body(MESSAGE_FIELD, equalTo(messageText));
    }

    @Test
    @UserStoryId(storyId = 226)
    void shouldNotLoginAdminWithNonMatchingPw() {
        loginAdminWithValidEmailAndPassword(generateRandomEmail(), "asdf")
                .then()
                .assertThat()
                .statusCode(SC_FORBIDDEN);
    }

    @UserStoryId(storyId = 226)
    @ParameterizedTest(name = "Run {index}: emailAddress={0}, password={1}, messageText={2}")
    @MethodSource("loginRequestAdminApi_NonExistEmail")
    void shouldNotLoginAdminWithNonExistingEmail_validationMessage(String emailAddress, String password,
            String messageText) {
        loginAdminWithValidEmailAndPassword(emailAddress, password)
                .then()
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .body(EMAIL_VALIDATION_ERROR, equalTo(messageText));
    }

    @UserStoryId(storyId = 226)
    @ParameterizedTest(name = "Run {index}: emailAddress={0}, password={1}, messageText={2}")
    @MethodSource("loginRequestAdminApi_NonExistPw")
    void shouldNotLoginAdminWithNonExistingPw_validationMessage(String emailAddress, String password,
            String messageText) {
        loginAdminWithValidEmailAndPassword(emailAddress, password)
                .then()
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .body(PW_VALIDATION_ERROR, equalTo(messageText));
    }

    @Test
    @UserStoryId(storyId = 226)
    void shouldNotLoginAdminWithInvalidCredentials_UnauthorizedOperationMessage() {
        loginAdminWithValidEmailAndPassword(generateRandomEmail(), generateValidPassword())
                .then()
                .assertThat()
                .statusCode(SC_FORBIDDEN)
                .body(containsString(UNAUTHORIZED_OPERATION_MESSAGE));
    }
}
