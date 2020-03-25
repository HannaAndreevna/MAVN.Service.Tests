package com.lykke.tests.api.service.adminmanagement;

import static com.lykke.api.testing.api.common.JsonConversionUtils.convertFromJsonFile;
import static com.lykke.tests.api.common.CommonConsts.SMOKE_TEST;
import static com.lykke.api.testing.api.common.GenerateUtils.generateRandomEmail;
import static com.lykke.api.testing.api.common.GenerateUtils.generateRandomString;
import static com.lykke.api.testing.api.common.PasswordGen.generateValidPassword;
import static com.lykke.tests.api.common.HelperUtils.getTestFilePath;
import static com.lykke.tests.api.common.ResourcesConsts.Admins.AUTOFILL_DATA;
import static com.lykke.tests.api.common.ResourcesConsts.Admins.AUTOFILL_VALUES;
import static com.lykke.tests.api.service.admin.AdminsUtils.getAutofillData;
import static com.lykke.tests.api.service.adminmanagement.RegisterAdminUtils.CUSTOMER_ID_FIELD;
import static com.lykke.tests.api.service.adminmanagement.RegisterAdminUtils.getAutofillValues;
import static com.lykke.tests.api.service.adminmanagement.RegisterAdminUtils.registerAdminWithEmailAndPassword;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.of;

import com.lykke.api.testing.annotations.UserStoryId;
import com.lykke.tests.api.base.BaseApiTest;
import com.lykke.api.testing.api.common.FakerUtils;
import com.lykke.api.testing.api.common.PasswordGen;
import com.lykke.tests.api.service.admin.model.admins.SuggestedValueMapping;
import com.lykke.tests.api.service.adminmanagement.model.AutofillValuesResponseModel;
import java.util.Arrays;
import java.util.stream.Stream;
import lombok.val;
import org.apache.http.HttpStatus;
import org.hamcrest.text.CharSequenceLength;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

public class RegisterAdminUsersTests extends BaseApiTest {

    private static final String EMAIL_FIELD_IS_REQUIRED_MESSAGE = "The Email field is required.";
    private static final String PASSWORD_FIELD_IS_REQUIRED_MESSAGE = "The Password field is required.";
    private static final String INVALID_EMAIL_OR_PASSWORD_MESSAGE = "InvalidEmailOrPasswordFormat";
    private static final String REGISTERED_WITH_ANOTHER_PASSWORD_MESSAGE = "RegisteredWithAnotherPassword";
    private static final String ALREADY_REGISTERED_ERROR_MESSAGE = "AlreadyRegistered";
    private static final String EMAIL_FIELD = "Email[0]";
    private static final String PASSWORD_FIELD = "Password[0]";
    private static final String ERROR_FIELD = "Error";
    private static int maxLengthPassword = 100;
    private static String superAdminEmailAddress;
    private static String superAdminPassword;
    private static String firstName;
    private static String lastName;

    @BeforeEach
    void registerSuperAdminUser() {
        superAdminEmailAddress = generateRandomEmail();
        superAdminPassword = generateValidPassword();
        firstName = FakerUtils.firstName;
        lastName = FakerUtils.lastName;
    }

    @Test
    @Tag(SMOKE_TEST)
    @UserStoryId(storyId = {226, 2678})
    void shouldRegisterAdminWithValidInput() {
        registerAdminWithEmailAndPassword(superAdminEmailAddress, superAdminPassword, firstName, lastName)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .body(CUSTOMER_ID_FIELD, CharSequenceLength.hasLength(36));
    }

    @Test
    @UserStoryId(storyId = {226, 2678})
    void shouldNotRegisterAdminTwice() {
        String emailAddress = superAdminEmailAddress;

        registerAdminWithEmailAndPassword(emailAddress, superAdminPassword, firstName, lastName)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .body(ERROR_FIELD, equalTo("None"));

        registerAdminWithEmailAndPassword(emailAddress, superAdminPassword, firstName, lastName)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .body(CUSTOMER_ID_FIELD, nullValue())
                .body(ERROR_FIELD, equalTo(ALREADY_REGISTERED_ERROR_MESSAGE));
    }

    @ParameterizedTest(name = "Run {index}: emailAddress={0}, password={1}, statusCode={2}, field={3}, message={4}")
    @MethodSource("registerRequestAdminApi_InvalidParameters")
    @UserStoryId(storyId = {226, 2678})
    void shouldNotRegisterAdminWithInvalidInput(String emailAddress, String password, int statusCode, String field,
            String message) {
        registerAdminWithEmailAndPassword(emailAddress, password, firstName, lastName)
                .then()
                .assertThat()
                .statusCode(statusCode)
                .body(CUSTOMER_ID_FIELD, nullValue())
                .body(field, equalTo(message));
    }

    @Test
    @Tag(SMOKE_TEST)
    @UserStoryId(2678)
    void shouldProvideWithAutofillValues() {
        val jsonFile = getTestFilePath(AUTOFILL_VALUES);
        val expectedAutofillValues = convertFromJsonFile(jsonFile, AutofillValuesResponseModel.class).getValues();

        val actualAutofillValues = getAutofillValues()
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(AutofillValuesResponseModel.class)
                .getValues();

        assertAll(
                () -> assertEquals(expectedAutofillValues[0].getType(), actualAutofillValues[0].getType()),
                () -> assertEquals(Arrays.stream(expectedAutofillValues[0].getValues()).sorted().findFirst(),
                        Arrays.stream(actualAutofillValues[0].getValues()).sorted().findFirst()),
                () -> assertEquals(expectedAutofillValues[1].getType(), actualAutofillValues[1].getType()),
                () -> assertEquals(Arrays.stream(expectedAutofillValues[1].getValues()).sorted().findFirst(),
                        Arrays.stream(actualAutofillValues[1].getValues()).sorted().findFirst())
        );
    }

    private static Stream registerRequestAdminApi_InvalidParameters() {
        return Stream.of(
                of(null, generateValidPassword(), SC_BAD_REQUEST, EMAIL_FIELD, EMAIL_FIELD_IS_REQUIRED_MESSAGE),
                of(EMPTY, generateValidPassword(), SC_BAD_REQUEST, EMAIL_FIELD, EMAIL_FIELD_IS_REQUIRED_MESSAGE),
                of("plaintext", generateValidPassword(), SC_OK, ERROR_FIELD, INVALID_EMAIL_OR_PASSWORD_MESSAGE),
                of("#@%^%#$@#$@#.com", generateValidPassword(), SC_OK, ERROR_FIELD, INVALID_EMAIL_OR_PASSWORD_MESSAGE),
                of("email.example.com", generateValidPassword(), SC_OK, ERROR_FIELD, INVALID_EMAIL_OR_PASSWORD_MESSAGE),
                of("email@example@example.com", generateValidPassword(), SC_OK, ERROR_FIELD,
                        INVALID_EMAIL_OR_PASSWORD_MESSAGE),
                of(".email@example.com", generateValidPassword(), SC_OK, ERROR_FIELD,
                        INVALID_EMAIL_OR_PASSWORD_MESSAGE),
                of(".email@example", generateValidPassword(), SC_OK, ERROR_FIELD, INVALID_EMAIL_OR_PASSWORD_MESSAGE),
                of(generateRandomEmail(), null, SC_BAD_REQUEST, PASSWORD_FIELD, PASSWORD_FIELD_IS_REQUIRED_MESSAGE),
                of(generateRandomEmail(), EMPTY, SC_BAD_REQUEST, PASSWORD_FIELD, PASSWORD_FIELD_IS_REQUIRED_MESSAGE),
                of(generateRandomEmail(), "asdf", SC_OK, ERROR_FIELD, INVALID_EMAIL_OR_PASSWORD_MESSAGE),
                of(generateRandomEmail(), generateRandomString(maxLengthPassword + 1),
                        SC_OK, ERROR_FIELD, INVALID_EMAIL_OR_PASSWORD_MESSAGE)
        );
    }
}
