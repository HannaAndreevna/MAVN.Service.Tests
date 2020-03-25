package com.lykke.tests.api.service.credentials;

import static com.lykke.api.testing.api.common.GenerateUtils.generateRandomEmail;
import static com.lykke.api.testing.api.common.GenerateUtils.getRandomUuid;
import static com.lykke.api.testing.api.common.PasswordGen.generateInvalidPassword;
import static com.lykke.api.testing.api.common.PasswordGen.generateValidPassword;
import static com.lykke.tests.api.common.CommonConsts.EMPTY_PASSWORD_ERR_MSG;
import static com.lykke.tests.api.common.CommonConsts.INVALID_PASSWORD_ERR_MSG;
import static com.lykke.tests.api.common.CommonConsts.NON_LATIN_PWD;
import static com.lykke.tests.api.common.CommonConsts.WHITE_SPACE_PWD;
import static com.lykke.tests.api.service.credentials.CredentialsUtils.createCredentials;
import static com.lykke.tests.api.service.credentials.CredentialsUtils.generateClientId;
import static com.lykke.tests.api.service.credentials.CredentialsUtils.generateClientSecret;
import static com.lykke.tests.api.service.credentials.CredentialsUtils.resetCustomerIdentifier;
import static com.lykke.tests.api.service.credentials.CredentialsUtils.updateCredentials;
import static com.lykke.tests.api.service.customermanagement.RegisterCustomerUtils.registerCustomer;
import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.text.CharSequenceLength.hasLength;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.params.provider.Arguments.of;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy.UpperCamelCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.google.common.util.concurrent.Uninterruptibles;
import com.lykke.api.testing.annotations.UserStoryId;
import com.lykke.tests.api.base.BaseApiTest;
import com.lykke.tests.api.service.credentials.CredentialsUtils.ValidationErrorResponse;
import com.lykke.tests.api.service.credentials.CredentialsUtils.ValidationErrorResponse.ModelErrors;
import com.lykke.tests.api.service.credentials.model.GenerateClientIdRequest;
import com.lykke.tests.api.service.credentials.model.GenerateClientSecretRequest;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class CredentialsTests extends BaseApiTest {

    private static final int RESET_IDENTIFIER_LENGTH = 20;
    private static final String ERROR_CODE_FIELD = "ErrorCode";
    private static final String ERROR_FIELD = "Error";
    private static final String IDENTIFIER_FIELD = "Identifier";
    private static final String NAME_FIELD = "name";
    private static final String MODEL_ERR_FIELD = "ModelErrors";
    private static final String PASSWORD_ERR_FIELD = MODEL_ERR_FIELD + ".Password[0]";
    private static final String NO_ERR_MSG = "None";
    private static final String REACHED_MAXIMUM_REQUEST_ERR_MSG = "ReachedMaximumRequestForPeriod";
    private static final String INVALID_CLIENT_ID_LENGTH_ERR_MSG = "A valid Length between 2 and 100 (inclusive) is required.";
    private static final String INVALID_CLIENT_SECRET_LENGTH_ERR_MSG = "A valid Length between 8 and 100 (inclusive) is required.";
    private static String customerId;

    // TODO: test on enabling reset after 15 min. is not added

    private static String getResetCustomerIdentifier(String customerId) {
        Uninterruptibles.sleepUninterruptibly(1, TimeUnit.SECONDS); // a requirement for generating new pw
        return resetCustomerIdentifier(customerId)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .body(ERROR_CODE_FIELD, equalTo(NO_ERR_MSG))
                .body(IDENTIFIER_FIELD, hasLength(RESET_IDENTIFIER_LENGTH))
                .extract()
                .path(IDENTIFIER_FIELD);
    }

    private static Stream<Arguments> getInvalidPasswords() {
        return Stream.of(
                of(StringUtils.EMPTY, EMPTY_PASSWORD_ERR_MSG),
                of(null, EMPTY_PASSWORD_ERR_MSG),
                of(generateInvalidPassword(1, 3, true, true, true, true), INVALID_PASSWORD_ERR_MSG),
                of(generateInvalidPassword(60, 100, true, true, true, true), INVALID_PASSWORD_ERR_MSG),
                of(generateInvalidPassword(8, 50, true, true, true, false), INVALID_PASSWORD_ERR_MSG),
                of(generateInvalidPassword(8, 50, true, true, false, true), INVALID_PASSWORD_ERR_MSG),
                of(generateInvalidPassword(8, 50, true, false, true, true), INVALID_PASSWORD_ERR_MSG),
                of(generateInvalidPassword(8, 50, false, true, true, true), INVALID_PASSWORD_ERR_MSG),
                of(NON_LATIN_PWD, INVALID_PASSWORD_ERR_MSG)
        );
    }

    @BeforeEach
    void setupCustomer() {
        customerId = registerCustomer();
    }

    @Test
    @UserStoryId(storyId = 2730)
    void shouldCreateCredentials_WhiteSpace() {
        createCredentials(generateRandomEmail(), WHITE_SPACE_PWD, getRandomUuid())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .body(ERROR_FIELD, equalTo(NO_ERR_MSG));
    }

    @Test
    @UserStoryId(storyId = 718)
    void shouldNotBeAbleResetCustomerIdentifierMoreThanAllowed() {
        getResetCustomerIdentifier(customerId);
        getResetCustomerIdentifier(customerId);
        getResetCustomerIdentifier(customerId);

        Uninterruptibles.sleepUninterruptibly(1, TimeUnit.SECONDS); // a requirement for generating new pw
        resetCustomerIdentifier(customerId)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .body(IDENTIFIER_FIELD, nullValue())
                .body(ERROR_CODE_FIELD, equalTo(REACHED_MAXIMUM_REQUEST_ERR_MSG));
    }

    @Test
    @UserStoryId(storyId = 718)
    void shouldCreateDifferentIdentifierWithReset() {
        String initialIdentifier = getResetCustomerIdentifier(customerId);
        String secondaryIdentifier = getResetCustomerIdentifier(customerId);

        assertNotEquals(initialIdentifier, secondaryIdentifier);
    }

    @ParameterizedTest(name = "Run {index}: invalidPassword={0}, errMsg={1}")
    @MethodSource("getInvalidPasswords")
    @UserStoryId(storyId = {1443, 2730})
    void shouldNotCreateCredentials_InvalidPassword(String invalidPassword, String errMsg) {
        createCredentials(generateRandomEmail(), invalidPassword, getRandomUuid())
                .then()
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .body(PASSWORD_ERR_FIELD, equalTo(errMsg));
    }

    @ParameterizedTest(name = "Run {index}: invalidPassword={0}, errMsg={1}")
    @MethodSource("getInvalidPasswords")
    @UserStoryId(storyId = {1443, 2730})
    void shouldNotUpdateCredentials_InvalidPassword(String invalidPassword, String errMsg) {
        val email = generateRandomEmail();
        val validPass = generateValidPassword();
        val customerId = getRandomUuid();

        createCredentials(email, validPass, customerId);

        updateCredentials(email, invalidPassword, customerId)
                .then()
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .body(PASSWORD_ERR_FIELD, equalTo(errMsg));
    }

    @Test
    @UserStoryId(2676)
    void shouldGenerateClientId() {
        generateClientId(GenerateClientIdRequest
                .builder()
                .length(10)
                .build())
                .then()
                .assertThat()
                .statusCode(SC_OK);
    }

    @Test
    @UserStoryId(2676)
    void shouldGenerateClientSecret() {
        generateClientSecret(GenerateClientSecretRequest
                .builder()
                .length(10)
                .build())
                .then()
                .assertThat()
                .statusCode(SC_OK);
    }

    static Stream<Arguments> getLength() {
        return Stream.of(
                of(0),
                of(1),
                of(Integer.MAX_VALUE)
        );
    }

    @ParameterizedTest(name = "Run {index}: length={0}")
    @MethodSource("getLength")
    @UserStoryId(2676)
    void shouldNotGenerateClientId(int length) {
        val expectedResult = ValidationErrorResponse
                .builder()
                .errorMessage(INVALID_CLIENT_ID_LENGTH_ERR_MSG)
                .modelErrors(ModelErrors
                        .builder()
                        .length(new String[]{INVALID_CLIENT_ID_LENGTH_ERR_MSG})
                        .build())
                .build();

        val actualResult = generateClientId(GenerateClientIdRequest
                .builder()
                .length(length)
                .build())
                .then()
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .extract()
                .as(ValidationErrorResponse.class);

        assertEquals(expectedResult, actualResult);
    }

    @ParameterizedTest(name = "Run {index}: length={0}")
    @MethodSource("getLength")
    @UserStoryId(2676)
    void shouldNotGenerateClientSecret(int length) {
        val expectedResult = ValidationErrorResponse
                .builder()
                .errorMessage(INVALID_CLIENT_SECRET_LENGTH_ERR_MSG)
                .modelErrors(ModelErrors
                        .builder()
                        .length(new String[]{INVALID_CLIENT_SECRET_LENGTH_ERR_MSG})
                        .build())
                .build();

        val actualResult = generateClientSecret(GenerateClientSecretRequest
                .builder()
                .length(length)
                .build())
                .then()
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .extract()
                .as(ValidationErrorResponse.class);

        assertEquals(expectedResult, actualResult);
    }
}
