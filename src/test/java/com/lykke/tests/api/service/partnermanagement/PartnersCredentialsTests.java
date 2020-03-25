package com.lykke.tests.api.service.partnermanagement;

import static com.lykke.api.testing.api.common.GenerateUtils.generateRandomString;
import static com.lykke.api.testing.api.common.GenerateUtils.getRandomUuid;
import static com.lykke.api.testing.api.common.PasswordGen.generateValidPassword;
import static com.lykke.tests.api.common.CommonConsts.SMOKE_TEST;
import static com.lykke.tests.api.service.partnerapi.PartnerApiLogInLogOutTests.USER_INFO;
import static com.lykke.tests.api.service.partnerapi.PartnerApiUtils.getPartnerToken;
import static com.lykke.tests.api.service.partnerapi.PartnerApiUtils.loginPartner;
import static com.lykke.tests.api.service.partnermanagement.PartnerManagementUtils.createPartner;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.apache.http.HttpStatus.SC_INTERNAL_SERVER_ERROR;
import static org.apache.http.HttpStatus.SC_OK;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.params.provider.Arguments.of;

import com.lykke.api.testing.annotations.UserStoryId;
import com.lykke.tests.api.base.BaseApiTest;
import com.lykke.tests.api.service.partnerapi.model.LoginRequestModel;
import com.lykke.tests.api.service.partnermanagement.PartnerManagementUtils.ValidationErrorResponse;
import java.util.function.Function;
import java.util.stream.Stream;
import lombok.val;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class PartnersCredentialsTests extends BaseApiTest {

    private static final Function<Integer, String> CLIENT_ID_LENGTH_ERROR_MESSAGE =
            (input) -> String
                    .format("The length of 'Client Id' must be at least 6 characters. You entered %s characters.",
                            input);
    private static final String CLIENT_ID_LENGTH_LIMIT_ERROR_MESSAGE = "The Client Id should be present and within range of 6 to 64 characters long.";
    private static final Function<Integer, String> CLIENT_SECRET_ERROR_MESSAGE =
            (input) -> String
                    .format("The length of 'Client Secret' must be at least 6 characters. You entered %s characters.",
                            input);
    private static final String CLIENT_SECRET_NOT_EMPTY_ERROR_MESSAGE = "'Client Secret' must not be empty.";
    private static final String CLIENT_SECRET_LENGTH_LIMIT_ERROR_MESSAGE = "The Client Secret should be present and within range of 6 to 64 characters long.";

    static Stream<Arguments> getLengthForClientId() {
        return Stream.of(
                of(1, PartnerManagementUtils.ValidationErrorResponse
                        .builder()
                        .clientId(new String[]{CLIENT_ID_LENGTH_ERROR_MESSAGE.apply(1)})
                        .build()),
                of(100, PartnerManagementUtils.ValidationErrorResponse
                        .builder()
                        .clientId(new String[]{CLIENT_ID_LENGTH_LIMIT_ERROR_MESSAGE})
                        .build()),
                of(1_000, PartnerManagementUtils.ValidationErrorResponse
                        .builder()
                        .clientId(new String[]{CLIENT_ID_LENGTH_LIMIT_ERROR_MESSAGE})
                        .build()),
                of(100_000, PartnerManagementUtils.ValidationErrorResponse
                        .builder()
                        .clientId(new String[]{CLIENT_ID_LENGTH_LIMIT_ERROR_MESSAGE})
                        .build())
        );
    }

    static Stream<Arguments> getLengthForClientSecret() {
        return Stream.of(
                of(0, PartnerManagementUtils.ValidationErrorResponse
                        .builder()
                        .clientSecret(new String[]{CLIENT_SECRET_NOT_EMPTY_ERROR_MESSAGE,
                                CLIENT_SECRET_ERROR_MESSAGE.apply(0)})
                        .build()),
                of(1, PartnerManagementUtils.ValidationErrorResponse
                        .builder()
                        .clientSecret(new String[]{CLIENT_SECRET_ERROR_MESSAGE.apply(1)})
                        .build()),
                of(1_000, PartnerManagementUtils.ValidationErrorResponse
                        .builder()
                        .clientSecret(new String[]{CLIENT_SECRET_LENGTH_LIMIT_ERROR_MESSAGE})
                        .build())
        );
    }

    @Test
    @UserStoryId(2677)
    void shouldNotCreatePartnerWithEmptyClientId() {
        val partnerPassword = generateValidPassword();
        val clientId = EMPTY;

        createPartner(generateRandomString(10), clientId, partnerPassword)
                .then()
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .extract()
                .as(ValidationErrorResponse.class);

        loginPartner(LoginRequestModel
                .builder()
                .clientId(clientId)
                .clientSecret(partnerPassword)
                .userInfo(USER_INFO)
                .build())
                .then()
                .assertThat()
                .statusCode(SC_INTERNAL_SERVER_ERROR);
    }

    @ParameterizedTest(name = "Run {index}: length={0}, expected result={1}")
    @MethodSource("getLengthForClientId")
    @UserStoryId(2677)
    void shouldNotCreatePartnerWithInvalidClientId(int length, ValidationErrorResponse expectedResult) {
        val partnerPassword = generateValidPassword();
        val clientId = generateRandomString(length);

        val actualResult = createPartner(generateRandomString(10), clientId, partnerPassword)
                .then()
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .extract()
                .as(ValidationErrorResponse.class);

        assertEquals(expectedResult, actualResult);
    }

    @ParameterizedTest(name = "Run {index}: length={0}, expected result={1}")
    @MethodSource("getLengthForClientSecret")
    @UserStoryId(2677)
    void shouldNotCreatePartnerWithInvalidPassword(int length, ValidationErrorResponse expectedResult) {
        val partnerPassword = generateRandomString(length);
        val clientId = getRandomUuid();

        val actualResult = createPartner(generateRandomString(10), clientId, partnerPassword)
                .then()
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .extract()
                .as(ValidationErrorResponse.class);

        assertEquals(expectedResult, actualResult);
    }

    @Test
    @Tag(SMOKE_TEST)
    @UserStoryId(2677)
    void shouldCreatePartnerWithValidClientIdAndPassword() {
        val partnerPassword = generateValidPassword();
        val clientId = generateRandomString(10);

        val actualResult = createPartner(generateRandomString(10), clientId, partnerPassword)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(ValidationErrorResponse.class);

        val partnerToken = getPartnerToken(clientId, partnerPassword, USER_INFO);
        assertNotNull(partnerToken);
    }
}
