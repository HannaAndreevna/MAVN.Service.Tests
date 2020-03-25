package com.lykke.tests.api.service.credentials;

import static com.lykke.api.testing.api.common.GenerateUtils.getRandomUuid;
import static com.lykke.api.testing.api.common.PasswordGen.generateInvalidPassword;
import static com.lykke.api.testing.api.common.PasswordGen.generateValidPassword;
import static com.lykke.tests.api.service.credentials.CredentialsUtils.createPartnerCredentials;
import static com.lykke.tests.api.service.credentials.CredentialsUtils.removePartnerCredentials;
import static com.lykke.tests.api.service.credentials.CredentialsUtils.updatePartnerCredentials;
import static com.lykke.tests.api.service.credentials.CredentialsUtils.validatePartnerCredentials;
import static com.lykke.tests.api.service.credentials.model.CredentialsError.LOGIN_ALREADY_EXISTS;
import static com.lykke.tests.api.service.credentials.model.CredentialsError.LOGIN_NOT_FOUND;
import static com.lykke.tests.api.service.credentials.model.CredentialsError.NONE;
import static com.lykke.tests.api.service.credentials.model.CredentialsErrorResponse.CLIENT_ID_ERROR_MESSAGE;
import static com.lykke.tests.api.service.credentials.model.CredentialsErrorResponse.CLIENT_SECRET_EMPTY_ERROR_MESSAGE;
import static com.lykke.tests.api.service.credentials.model.CredentialsErrorResponse.CLIENT_SECRET_ERROR_MESSAGE;
import static com.lykke.tests.api.service.credentials.model.CredentialsErrorResponse.EMAIL_ADDRESS_MESSAGE;
import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.apache.http.HttpStatus.SC_OK;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.of;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy.UpperCamelCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.lykke.api.testing.annotations.UserStoryId;
import com.lykke.tests.api.base.BaseApiTest;
import com.lykke.tests.api.service.credentials.model.CredentialsCreateResponse;
import com.lykke.tests.api.service.credentials.model.CredentialsErrorResponse;
import com.lykke.tests.api.service.credentials.model.CredentialsUpdateResponse;
import com.lykke.tests.api.service.credentials.model.PartnerCredentialsCreateRequest;
import com.lykke.tests.api.service.credentials.model.PartnerCredentialsRemoveRequest;
import com.lykke.tests.api.service.credentials.model.PartnerCredentialsUpdateRequest;
import com.lykke.tests.api.service.credentials.model.PartnerCredentialsValidationRequest;
import com.lykke.tests.api.service.credentials.model.PartnerCredentialsValidationResponse;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class PartnersTests extends BaseApiTest {

    private static final String CLIENT_ID_REQUIRED_ERROR_MESSAGE = "Client id required.";
    private static final String PARTNER_ID_MUST_NOT_BE_EMPTY_ERROR_MESSAGE = "'Partner Id' must not be empty.";
    private static final String PASSWORD_REQUIRED_ERROR_MESSAGE = "Password required";
    private static final String PASSWORD_RULES_ERROR_MESSAGE = "Password length should be between 8 and 50 characters. Password should contain 1 lowercase, 1 uppercase, 1 digits and 1 special symbols. Allowed special symbols are: !@#$%&. Whitespaces are not allowed";

    static Stream<Arguments> getInvalidInput() {
        return Stream.of(
                of(StringUtils.EMPTY, generateValidPassword(),
                        CredentialsErrorResponse.credentialsErrorBuilder().clientIdMessage(CLIENT_ID_ERROR_MESSAGE)
                                .build()),
                of(getRandomUuid(), generateInvalidPassword(0, 0, true, true, true, true),
                        CredentialsErrorResponse.credentialsErrorBuilder()
                                .clientSecretMessage(CLIENT_SECRET_EMPTY_ERROR_MESSAGE)
                                .build()),
                of(getRandomUuid(), generateInvalidPassword(55, 60, true, true, true, true),
                        CredentialsErrorResponse.credentialsErrorBuilder()
                                .clientSecretMessage(CLIENT_SECRET_ERROR_MESSAGE)
                                .build())
        );
    }

    static Stream<Arguments> getMissingInput() {
        return Stream.of(
                of(StringUtils.EMPTY, generateValidPassword(),
                        ErrorValidationResponse
                                .builder()
                                .errorMessage(CLIENT_ID_REQUIRED_ERROR_MESSAGE)
                                .modelErrors(ModelErrors
                                        .builder().clientId(new String[]{CLIENT_ID_REQUIRED_ERROR_MESSAGE})
                                        .partnerId(new String[]{PARTNER_ID_MUST_NOT_BE_EMPTY_ERROR_MESSAGE})
                                        .build())
                                .build()),
                of(getRandomUuid(), generateInvalidPassword(0, 0, true, true, true, true),
                        ErrorValidationResponse
                                .builder()
                                .errorMessage(PARTNER_ID_MUST_NOT_BE_EMPTY_ERROR_MESSAGE)
                                .modelErrors(ModelErrors
                                        .builder()
                                        .clientSecret(new String[]{PASSWORD_REQUIRED_ERROR_MESSAGE})
                                        .partnerId(new String[]{PARTNER_ID_MUST_NOT_BE_EMPTY_ERROR_MESSAGE})
                                        .build())
                                .build()),
                of(getRandomUuid(), generateInvalidPassword(55, 60, true, true, true, true),
                        ErrorValidationResponse
                                .builder()
                                .errorMessage(PARTNER_ID_MUST_NOT_BE_EMPTY_ERROR_MESSAGE)
                                .modelErrors(ModelErrors
                                        .builder()
                                        .clientSecret(new String[]{
                                                PASSWORD_RULES_ERROR_MESSAGE})
                                        .partnerId(new String[]{PARTNER_ID_MUST_NOT_BE_EMPTY_ERROR_MESSAGE})
                                        .build())
                                .build()

                ));
    }

    static Stream<Arguments> getInvalidInputForValidation() {
        return Stream.of(
                of(StringUtils.EMPTY, generateValidPassword(),
                        CredentialsErrorResponse.credentialsErrorBuilder().clientIdMessage(EMAIL_ADDRESS_MESSAGE)
                                .build()),
                of(getRandomUuid(), generateInvalidPassword(0, 0, true, true, true, true),
                        CredentialsErrorResponse.credentialsErrorBuilder()
                                .clientSecretMessage(CredentialsErrorResponse.PASSWORD_REQUIRED_ERROR_MESSAGE)
                                .build())
        );
    }

    static Stream<Arguments> getInvalidPartnerIdInputForValidation() {
        return Stream.of(
                of(getRandomUuid(), generateInvalidPassword(55, 60, true, true, true, true),
                        PartnerCredentialsValidationResponse.builder().error(LOGIN_NOT_FOUND).build())
        );
    }

    @Test
    @UserStoryId(2087)
    void shouldCreatePartnerCredentials() {
        val actualResult = createPartnerCredentials(PartnerCredentialsCreateRequest
                .credentialsCreateRequestBuilder()
                .clientId(getRandomUuid())
                .clientSecret(generateValidPassword())
                .partnerId(getRandomUuid())
                .build())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(CredentialsCreateResponse.class);

        assertEquals(NONE, actualResult.getError());
    }

    @Disabled("not to delete customers' credentials")
    @Test
    @UserStoryId(storyId = {2087, 2371})
    void shouldRemovePartnerCredentials() {
        val partnerId = getRandomUuid();
        val credsCreationResult = createPartnerCredentials(PartnerCredentialsCreateRequest
                .credentialsCreateRequestBuilder()
                .clientId(partnerId)
                .clientSecret(generateValidPassword())
                .build())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(CredentialsCreateResponse.class);

        assertEquals(NONE, credsCreationResult.getError());

        removePartnerCredentials(PartnerCredentialsRemoveRequest
                .builder()
                .partnerId(partnerId)
                .build())
                .then()
                .assertThat()
                .statusCode(SC_OK);
    }

    @Test
    @UserStoryId(storyId = {2087, 2371})
    void shouldUpdatePartnerCredentials() {
        val partnerId = getRandomUuid();
        val credsCreationResult = createPartnerCredentials(PartnerCredentialsCreateRequest
                .credentialsCreateRequestBuilder()
                .clientId(partnerId)
                .clientSecret(generateValidPassword())
                .partnerId(partnerId)
                .build())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(CredentialsCreateResponse.class);

        assertEquals(NONE, credsCreationResult.getError());

        val actualResult = updatePartnerCredentials(PartnerCredentialsUpdateRequest
                .credentialsUpdateRequestBuilder()
                .clientId(partnerId)
                .clientSecret(generateValidPassword())
                .partnerId(partnerId)
                .build())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(CredentialsUpdateResponse.class);

        assertEquals(NONE, actualResult.getError());
    }

    @Test
    @UserStoryId(storyId = {2087, 2371})
    void shouldValidatePartnerCredentials() {
        val partnerId = getRandomUuid();
        val password = generateValidPassword();
        val credsCreationResult = createPartnerCredentials(PartnerCredentialsCreateRequest
                .credentialsCreateRequestBuilder()
                .clientId(partnerId)
                .clientSecret(password)
                .partnerId(partnerId)
                .build())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(CredentialsCreateResponse.class);

        assertEquals(NONE, credsCreationResult.getError());

        val actualResult = validatePartnerCredentials(
                PartnerCredentialsValidationRequest
                        .credentialsUpdateRequestBuilder()
                        .clientId(partnerId)
                        .clientSecret(password)
                        .build())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(CredentialsUpdateResponse.class);

        assertEquals(NONE, actualResult.getError());
    }

    @Test
    @UserStoryId(storyId = {2087, 2371})
    void shouldNotCreatePartnerCredentialsIfThereAreAlreadyCreatedTheSame() {
        val partnerId = getRandomUuid();
        val password = generateValidPassword();
        createPartnerCredentials(PartnerCredentialsCreateRequest
                .credentialsCreateRequestBuilder()
                .clientId(partnerId)
                .partnerId(partnerId)
                .clientSecret(password)
                .build())
                .then()
                .assertThat()
                .statusCode(SC_OK);

        val actualResult = createPartnerCredentials(PartnerCredentialsCreateRequest
                .credentialsCreateRequestBuilder()
                .clientId(partnerId)
                .partnerId(partnerId)
                .clientSecret(password)
                .build())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(CredentialsCreateResponse.class);

        assertEquals(LOGIN_ALREADY_EXISTS, actualResult.getError());
    }

    @ParameterizedTest
    @MethodSource("getInvalidInput")
    @UserStoryId(storyId = {2087, 2371})
    void shouldNotCreatePartnerCredentialsOnInvalidInput(String clientId, String clientSecret,
            CredentialsErrorResponse expectedResult) {
        val actualResult = createPartnerCredentials(PartnerCredentialsCreateRequest
                .credentialsCreateRequestBuilder()
                .clientId(clientId)
                .clientSecret(clientSecret)
                .partnerId(getRandomUuid())
                .build())
                .then()
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .extract()
                .as(CredentialsErrorResponse.class);

        assertEquals(expectedResult, actualResult);
    }

    @ParameterizedTest
    @MethodSource("getMissingInput")
    @UserStoryId(storyId = {2087, 2371})
    void shouldNotCreatePartnerCredentialsOnMissingInput(String clientId, String clientSecret,
            ErrorValidationResponse expectedResult) {
        val actualResult = createPartnerCredentials(PartnerCredentialsCreateRequest
                .credentialsCreateRequestBuilder()
                .clientId(clientId)
                .clientSecret(clientSecret)
                .build())
                .then()
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .extract()
                .as(ErrorValidationResponse.class);

        assertEquals(expectedResult, actualResult);
    }

    @Disabled("not to delete customers' credentials")
    @Test
    @UserStoryId(storyId = {2087, 2371})
    void shouldNotRemovePartnerCredentialsOnInvalidInput() {
        val partnerId = getRandomUuid();
        val password = generateValidPassword();
        val credsCreationResult = createPartnerCredentials(PartnerCredentialsCreateRequest
                .credentialsCreateRequestBuilder()
                .clientId(partnerId)
                .clientSecret(password)
                .build())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(CredentialsCreateResponse.class);

        assertEquals(NONE, credsCreationResult.getError());

        removePartnerCredentials(PartnerCredentialsRemoveRequest
                .builder()
                .partnerId(getRandomUuid())
                .build())
                .then()
                .assertThat()
                .statusCode(SC_OK);

        val actualResult = validatePartnerCredentials(
                PartnerCredentialsValidationRequest
                        .credentialsUpdateRequestBuilder()
                        .clientId(partnerId)
                        .clientSecret(password)
                        .build())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(CredentialsUpdateResponse.class);

        assertEquals(NONE, actualResult.getError());
    }

    @ParameterizedTest
    @MethodSource("getInvalidInput")
    @UserStoryId(storyId = {2087, 2371})
    void shouldNotUpdatePartnerCredentialsOnInvalidInput(String clientId, String clientSecret,
            CredentialsErrorResponse expectedResult) {
        val partnerId = getRandomUuid();
        val credsCreationResult = createPartnerCredentials(PartnerCredentialsCreateRequest
                .credentialsCreateRequestBuilder()
                .clientId(partnerId)
                .clientSecret(generateValidPassword())
                .partnerId(partnerId)
                .build())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(CredentialsCreateResponse.class);

        assertEquals(NONE, credsCreationResult.getError());

        val actualResult = updatePartnerCredentials(PartnerCredentialsUpdateRequest
                .credentialsUpdateRequestBuilder()
                .clientId(clientId)
                .clientSecret(clientSecret)
                .partnerId(partnerId)
                .build())
                .then()
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .extract()
                .as(CredentialsErrorResponse.class);

        assertEquals(expectedResult, actualResult);
    }

    @ParameterizedTest
    @MethodSource("getInvalidInputForValidation")
    @UserStoryId(storyId = {2087, 2371})
    void shouldNotValidatePartnerCredentialsOnInvalidInput(String clientId, String clientSecret,
            CredentialsErrorResponse expectedResult) {
        val partnerId = getRandomUuid();
        val password = generateValidPassword();
        val credsCreationResult = createPartnerCredentials(PartnerCredentialsCreateRequest
                .credentialsCreateRequestBuilder()
                .clientId(partnerId)
                .clientSecret(password)
                .partnerId(partnerId)
                .build())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(CredentialsCreateResponse.class);

        assertEquals(NONE, credsCreationResult.getError());

        val actualResult = validatePartnerCredentials(
                PartnerCredentialsValidationRequest
                        .credentialsUpdateRequestBuilder()
                        .clientId(clientId)
                        .clientSecret(clientSecret)
                        .build())
                .then()
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .extract()
                .as(CredentialsErrorResponse.class);

        assertEquals(expectedResult, actualResult);
    }

    @ParameterizedTest
    @MethodSource("getInvalidPartnerIdInputForValidation")
    @UserStoryId(storyId = {2087, 2371})
    void shouldNotValidatePartnerCredentialsOnInvalidInputOfPartnerId(String clientId, String clientSecret,
            PartnerCredentialsValidationResponse expectedResult) {
        val partnerId = getRandomUuid();
        val password = generateValidPassword();
        val credsCreationResult = createPartnerCredentials(PartnerCredentialsCreateRequest
                .credentialsCreateRequestBuilder()
                .clientId(partnerId)
                .clientSecret(password)
                .partnerId(partnerId)
                .build())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(CredentialsCreateResponse.class);

        assertEquals(NONE, credsCreationResult.getError());

        val actualResult = validatePartnerCredentials(
                PartnerCredentialsValidationRequest
                        .credentialsUpdateRequestBuilder()
                        .clientId(clientId)
                        .clientSecret(clientSecret)
                        .build())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(PartnerCredentialsValidationResponse.class);

        assertEquals(expectedResult, actualResult);
    }

    @AllArgsConstructor
    @Builder
    @Data
    @NoArgsConstructor
    @JsonNaming(UpperCamelCaseStrategy.class)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ErrorValidationResponse {

        private String errorMessage;
        private PartnersTests.ModelErrors modelErrors;
    }

    @AllArgsConstructor
    @Builder
    @Data
    @NoArgsConstructor
    @JsonNaming(UpperCamelCaseStrategy.class)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ModelErrors {

        private String[] clientId;
        private String[] partnerId;
        private String[] clientSecret;
    }
}
