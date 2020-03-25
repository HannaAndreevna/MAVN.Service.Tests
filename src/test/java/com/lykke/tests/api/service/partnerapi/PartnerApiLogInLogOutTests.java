package com.lykke.tests.api.service.partnerapi;

import static com.lykke.tests.api.common.CommonConsts.SMOKE_TEST;
import static com.lykke.api.testing.api.common.GenerateUtils.generateRandomString;
import static com.lykke.api.testing.api.common.GenerateUtils.getRandomUuid;
import static com.lykke.api.testing.api.common.PasswordGen.generateValidPassword;
import static com.lykke.tests.api.service.credentials.CredentialsUtils.createPartnerCredentials;
import static com.lykke.tests.api.service.credentials.model.CredentialsError.NONE;
import static com.lykke.tests.api.service.partnerapi.PartnerApiUtils.getPartnerToken;
import static com.lykke.tests.api.service.partnerapi.PartnerApiUtils.logOutPartner;
import static com.lykke.tests.api.service.partnermanagement.PartnerManagementUtils.createDefaultPartner;
import static org.apache.http.HttpStatus.SC_NO_CONTENT;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy.UpperCamelCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.lykke.api.testing.annotations.UserStoryId;
import com.lykke.tests.api.base.BaseApiTest;
import java.util.function.Function;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

public class PartnerApiLogInLogOutTests extends BaseApiTest {

    public static final String USER_INFO = "some info";
    public static final String NOT_AUTHENTICATED_ERROR_MESSAGE = "Not authenticated";
    private static final Function<String, String> CLIENT_PREFIX = (id) -> "non-existing-client_" + id;
    private String partnerId;
    private String partnerPassword;

    @BeforeEach
    void setUp() {
        partnerId = getRandomUuid();
        partnerPassword = generateValidPassword();
        createDefaultPartner(partnerId, partnerPassword, generateRandomString(10),
                generateRandomString(10));
    }

    @Test
    @Tag(SMOKE_TEST)
    @UserStoryId(2088)
    void shouldLogInPartner() {
        val partnerCredentials = createPartnerCredentials(partnerId, partnerPassword, CLIENT_PREFIX.apply(partnerId));
        assertEquals(NONE, partnerCredentials.getError());

        val token = getPartnerToken(partnerId, partnerPassword, USER_INFO);

        logOutPartner(token)
                .then()
                .assertThat()
                .statusCode(SC_NO_CONTENT);
    }

    @Test
    @UserStoryId(2088)
    void shouldAllowLoggingInPartnerTwice() {
        val partnerCredentials = createPartnerCredentials(partnerId, partnerPassword, CLIENT_PREFIX.apply(partnerId));
        assertEquals(NONE, partnerCredentials.getError());

        val token = getPartnerToken(partnerId, partnerPassword, USER_INFO);

        logOutPartner(token)
                .then()
                .assertThat()
                .statusCode(SC_NO_CONTENT);

        val newToken = getPartnerToken(partnerId, partnerPassword, USER_INFO);

        assertNotEquals(token, newToken);
    }

    @Test
    @UserStoryId(2088)
    void shouldLogOutPartner() {
        val expectedResult = ErrorResponse.builder().error(NOT_AUTHENTICATED_ERROR_MESSAGE).build();
        val partnerCredentials = createPartnerCredentials(partnerId, partnerPassword, CLIENT_PREFIX.apply(partnerId));
        assertEquals(NONE, partnerCredentials.getError());

        val token = getPartnerToken(partnerId, partnerPassword, USER_INFO);

        logOutPartner(token)
                .then()
                .assertThat()
                .statusCode(SC_NO_CONTENT);

        // the second logout returns an error that the token is already invalid
        val actualResult = logOutPartner(token)
                .then()
                .assertThat()
                .statusCode(SC_UNAUTHORIZED)
                .extract()
                .as(ErrorResponse.class);

        assertEquals(expectedResult, actualResult);
    }

    @Test
    @UserStoryId(2088)
    void shouldNotLogOutAlreadyLoggedOffPartner() {
        val partnerCredentials = createPartnerCredentials(partnerId, partnerPassword, CLIENT_PREFIX.apply(partnerId));
        assertEquals(NONE, partnerCredentials.getError());

        val token = getPartnerToken(partnerId, partnerPassword, USER_INFO);

        logOutPartner(token)
                .then()
                .assertThat()
                .statusCode(SC_NO_CONTENT);

        // each next log out attempt returns the same
        val actualResult1 = logOutPartner(token)
                .then()
                .assertThat()
                .statusCode(SC_UNAUTHORIZED)
                .extract()
                .as(ErrorResponse.class);
        val actualResult2 = logOutPartner(token)
                .then()
                .assertThat()
                .statusCode(SC_UNAUTHORIZED)
                .extract()
                .as(ErrorResponse.class);

        assertEquals(actualResult1, actualResult2);
    }

    @AllArgsConstructor
    @Builder
    @Data
    @NoArgsConstructor
    @JsonNaming(UpperCamelCaseStrategy.class)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ErrorResponse {

        private String error;
    }
}
