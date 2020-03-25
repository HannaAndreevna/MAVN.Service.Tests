package com.lykke.tests.api.service.pushnotifications;

import static com.lykke.api.testing.api.common.GenerateUtils.generateRandomString;
import static com.lykke.tests.api.common.CommonConsts.SMOKE_TEST;
import static com.lykke.tests.api.service.customermanagement.RegisterCustomerUtils.registerDefaultVerifiedCustomer;
import static com.lykke.tests.api.service.pushnotifications.PushNotificationsUtils.deletePushRegistrations;
import static com.lykke.tests.api.service.pushnotifications.PushNotificationsUtils.deletePushRegistrationsForCustomer;
import static com.lykke.tests.api.service.pushnotifications.PushNotificationsUtils.deletePushRegistrationsInfobip;
import static com.lykke.tests.api.service.pushnotifications.PushNotificationsUtils.getPushRegistrations;
import static com.lykke.tests.api.service.pushnotifications.PushNotificationsUtils.postPushRegistrations;
import static com.lykke.tests.api.service.pushnotifications.model.PushTokenInsertionResult.APPLE_TOKEN_ALREADY_EXISTS;
import static com.lykke.tests.api.service.pushnotifications.model.PushTokenInsertionResult.FIREBASE_TOKEN_ALREADY_EXISTS;
import static com.lykke.tests.api.service.pushnotifications.model.PushTokenInsertionResult.INFOBIP_TOKEN_ALREADY_EXISTS;
import static com.lykke.tests.api.service.pushnotifications.model.PushTokenInsertionResult.OK;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.http.HttpStatus.SC_NO_CONTENT;
import static org.apache.http.HttpStatus.SC_OK;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.params.provider.Arguments.of;

import com.lykke.api.testing.annotations.UserStoryId;
import com.lykke.tests.api.base.BaseApiTest;
import com.lykke.tests.api.common.model.CustomerInfo;
import com.lykke.tests.api.service.pushnotifications.model.CreatePushRegistrationRequestModel;
import com.lykke.tests.api.service.pushnotifications.model.GetPushRegistrationResponseModel;
import com.lykke.tests.api.service.pushnotifications.model.PushTokenInsertionResult;
import java.util.Arrays;
import java.util.stream.Stream;
import lombok.val;
import lombok.var;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class PushNotificationsTests extends BaseApiTest {

    private static final int tokenLength = 10;
    private CustomerInfo customerData;
    private String customerId;

    static Stream<Arguments> getValidFirebaseAndOrAppleTokens() {
        return Stream.of(
                of(generateRandomString(tokenLength), EMPTY),
                of(EMPTY, generateRandomString(tokenLength)),
                of(generateRandomString(tokenLength), generateRandomString(tokenLength))
        );
    }

    static Stream<Arguments> getInfobipFirebaseAndAppleTokens() {
        val generatedString = generateRandomString(tokenLength);
        return Stream.of(
                of(generatedString, generateRandomString(tokenLength), generateRandomString(tokenLength),
                        generatedString, generateRandomString(tokenLength), generateRandomString(tokenLength),
                        INFOBIP_TOKEN_ALREADY_EXISTS),
                of(generateRandomString(tokenLength), generatedString, generateRandomString(tokenLength),
                        generateRandomString(tokenLength), generatedString, generateRandomString(tokenLength),
                        FIREBASE_TOKEN_ALREADY_EXISTS),
                of(generateRandomString(tokenLength), generateRandomString(tokenLength), generatedString,
                        generateRandomString(tokenLength), generateRandomString(tokenLength), generatedString,
                        APPLE_TOKEN_ALREADY_EXISTS)
        );
    }

    @BeforeEach
    void setUp() {
        customerData = registerDefaultVerifiedCustomer();
        customerId = customerData.getCustomerId();
    }

    @ParameterizedTest
    @MethodSource("getValidFirebaseAndOrAppleTokens")
    @UserStoryId(1326)
    void shouldPostPushRegistrationsWithFirebaseOrAppleToken(String firebaseToken, String appleToken) {
        val requestObject = CreatePushRegistrationRequestModel
                .builder()
                .customerId(customerId)
                .infobipToken(generateRandomString(tokenLength))
                .firebaseToken(firebaseToken)
                .appleToken(appleToken)
                .build();
        val actualResult = postPushRegistrations(requestObject)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(PushTokenInsertionResult.class);

        assertEquals(OK.getCode(), actualResult.getCode());
    }

    @Disabled("there's a bug by Dmitry")
    @ParameterizedTest
    @MethodSource("getInfobipFirebaseAndAppleTokens")
    @UserStoryId(1326)
    void shouldNotPostDuplicatedTokensOnPushRegistrationsForTheSameCustomerId(
            String infobipToken1, String firebaseToken1, String appleToken1,
            String infobipToken2, String firebaseToken2, String appleToken2,
            PushTokenInsertionResult errorCode) {
        var requestObject = CreatePushRegistrationRequestModel
                .builder()
                .customerId(customerId)
                .infobipToken(infobipToken1)
                .firebaseToken(firebaseToken1)
                .appleToken(appleToken1)
                .build();
        var actualResult = postPushRegistrations(requestObject)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(PushTokenInsertionResult.class);

        assertEquals(OK.getCode(), actualResult.getCode());

        requestObject = CreatePushRegistrationRequestModel
                .builder()
                .customerId(customerId)
                .infobipToken(infobipToken2)
                .firebaseToken(firebaseToken2)
                .appleToken(appleToken2)
                .build();
        actualResult = postPushRegistrations(requestObject)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(PushTokenInsertionResult.class);

        assertEquals(errorCode.getCode(), actualResult.getCode());

        val pushRegistrations = getPushRegistrations()
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(GetPushRegistrationResponseModel[].class);

        assertTrue(!Arrays.stream(pushRegistrations)
                .filter(registration -> customerId.equals(registration.getCustomerId()))
                .findAny().isPresent());
    }

    @Disabled("there's a bug by Dmitry")
    @ParameterizedTest
    @MethodSource("getInfobipFirebaseAndAppleTokens")
    @UserStoryId(1326)
    void shouldNotPostDuplicatedTokensOnPushRegistrationsForDifferentCustomerIds(
            String infobipToken1, String firebaseToken1, String appleToken1,
            String infobipToken2, String firebaseToken2, String appleToken2,
            PushTokenInsertionResult errorCode) {
        var requestObject = CreatePushRegistrationRequestModel
                .builder()
                .customerId(customerId)
                .infobipToken(infobipToken1)
                .firebaseToken(firebaseToken1)
                .appleToken(appleToken1)
                .build();
        var actualResult = postPushRegistrations(requestObject)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(PushTokenInsertionResult.class);

        assertEquals(OK.getCode(), actualResult.getCode());

        customerId = registerDefaultVerifiedCustomer().getCustomerId();
        requestObject = CreatePushRegistrationRequestModel
                .builder()
                .customerId(customerId)
                .infobipToken(infobipToken2)
                .firebaseToken(firebaseToken2)
                .appleToken(appleToken2)
                .build();
        actualResult = postPushRegistrations(requestObject)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(PushTokenInsertionResult.class);

        assertEquals(errorCode.getCode(), actualResult.getCode());

        val pushRegistrations = getPushRegistrations()
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(GetPushRegistrationResponseModel[].class);

        val customerIdToSearchFor = customerId;
        assertTrue(!Arrays.stream(pushRegistrations)
                .filter(registration -> customerIdToSearchFor.equals(registration.getCustomerId()))
                .findAny().isPresent());
    }

    @ParameterizedTest
    @MethodSource("getValidFirebaseAndOrAppleTokens")
    @Tag(SMOKE_TEST)
    @UserStoryId(1326)
    void shouldGetPushRegistrationsWithFirebaseOrAppleToken(String firebaseToken, String appleToken) {
        val infobipToken = generateRandomString(tokenLength);
        val requestObject = CreatePushRegistrationRequestModel
                .builder()
                .customerId(customerId)
                .infobipToken(infobipToken)
                .firebaseToken(firebaseToken)
                .appleToken(appleToken)
                .build();
        postPushRegistrations(requestObject)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(PushTokenInsertionResult.class);

        val pushRegistrations = getPushRegistrations()
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(GetPushRegistrationResponseModel[].class);

        val actualResultCandidate = Arrays.stream(pushRegistrations)
                .filter(registration -> customerId.equals(registration.getCustomerId()))
                .findFirst();
        val actualResult = actualResultCandidate.isPresent() ? actualResultCandidate.get()
                : new GetPushRegistrationResponseModel();

        assertAll(
                () -> assertEquals(customerId, actualResult.getCustomerId()),
                () -> assertEquals(infobipToken, actualResult.getInfobipToken()),
                () -> assertEquals(firebaseToken, actualResult.getFirebaseToken()),
                () -> assertEquals(appleToken, actualResult.getAppleToken())
        );
    }

    @ParameterizedTest
    @MethodSource("getValidFirebaseAndOrAppleTokens")
    @UserStoryId(1326)
    void shouldDeletePushRegistrationsByRegistrationId(String firebaseToken, String appleToken) {
        val infobipToken = generateRandomString(tokenLength);
        val requestObject = CreatePushRegistrationRequestModel
                .builder()
                .customerId(customerId)
                .infobipToken(infobipToken)
                .firebaseToken(firebaseToken)
                .appleToken(appleToken)
                .build();
        postPushRegistrations(requestObject)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(PushTokenInsertionResult.class);

        var pushRegistrations = getPushRegistrations()
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(GetPushRegistrationResponseModel[].class);

        var actualResultCandidate = Arrays.stream(pushRegistrations)
                .filter(registration -> customerId.equals(registration.getCustomerId()))
                .findFirst();
        val actualResult = actualResultCandidate.isPresent() ? actualResultCandidate.get()
                : new GetPushRegistrationResponseModel();
        val registrationId = actualResult.getId();

        deletePushRegistrations(registrationId.toString())
                .then()
                .assertThat()
                .statusCode(SC_NO_CONTENT);

        pushRegistrations = getPushRegistrations()
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(GetPushRegistrationResponseModel[].class);

        assertTrue(!Arrays.stream(pushRegistrations)
                .filter(registration -> customerId.equals(registration.getCustomerId()))
                .findAny().isPresent());
    }

    @ParameterizedTest
    @MethodSource("getValidFirebaseAndOrAppleTokens")
    @Tag(SMOKE_TEST)
    @UserStoryId(1326)
    void shouldGetPushRegistrationByCustomerId(String firebaseToken, String appleToken) {
        val infobipToken = generateRandomString(tokenLength);
        val requestObject = CreatePushRegistrationRequestModel
                .builder()
                .customerId(customerId)
                .infobipToken(infobipToken)
                .firebaseToken(firebaseToken)
                .appleToken(appleToken)
                .build();
        postPushRegistrations(requestObject)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(PushTokenInsertionResult.class);

        val pushRegistrations = getPushRegistrations()
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(GetPushRegistrationResponseModel[].class);

        val actualResultCandidate = Arrays.stream(pushRegistrations)
                .filter(registration -> customerId.equals(registration.getCustomerId()))
                .findFirst();
        val actualResult = actualResultCandidate.isPresent() ? actualResultCandidate.get()
                : new GetPushRegistrationResponseModel();

        assertAll(
                () -> assertEquals(customerId, actualResult.getCustomerId()),
                () -> assertEquals(infobipToken, actualResult.getInfobipToken()),
                () -> assertEquals(firebaseToken, actualResult.getFirebaseToken()),
                () -> assertEquals(appleToken, actualResult.getAppleToken())
        );
    }

    @ParameterizedTest
    @MethodSource("getValidFirebaseAndOrAppleTokens")
    @UserStoryId(1326)
    void shouldDeletePushRegistrationsByCustomerId(String firebaseToken, String appleToken) {
        val infobipToken = generateRandomString(tokenLength);
        val requestObject = CreatePushRegistrationRequestModel
                .builder()
                .customerId(customerId)
                .infobipToken(infobipToken)
                .firebaseToken(firebaseToken)
                .appleToken(appleToken)
                .build();
        postPushRegistrations(requestObject)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(PushTokenInsertionResult.class);

        var pushRegistrations = getPushRegistrations()
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(GetPushRegistrationResponseModel[].class);

        var actualResultCandidate = Arrays.stream(pushRegistrations)
                .filter(registration -> customerId.equals(registration.getCustomerId()))
                .findFirst();
        val actualResult = actualResultCandidate.isPresent() ? actualResultCandidate.get()
                : new GetPushRegistrationResponseModel();
        val registrationId = actualResult.getId();

        deletePushRegistrationsForCustomer(customerId)
                .then()
                .assertThat()
                .statusCode(SC_NO_CONTENT);

        pushRegistrations = getPushRegistrations()
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(GetPushRegistrationResponseModel[].class);

        assertTrue(!Arrays.stream(pushRegistrations)
                .filter(registration -> customerId.equals(registration.getCustomerId()))
                .findAny().isPresent());
    }

    @Test
    @UserStoryId(2923)
    void shouldDeleteInfobipToken() {
        val infobipToken = generateRandomString(tokenLength);
        val firebaseToken = generateRandomString(tokenLength);
        var appleToken = generateRandomString(tokenLength);
        val requestObject = CreatePushRegistrationRequestModel
                .builder()
                .customerId(customerId)
                .infobipToken(infobipToken)
                .firebaseToken(firebaseToken)
                .appleToken(appleToken)
                .build();
        postPushRegistrations(requestObject)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(PushTokenInsertionResult.class);

        var pushRegistrations = getPushRegistrations()
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(GetPushRegistrationResponseModel[].class);

        var actualResultCandidate = Arrays.stream(pushRegistrations)
                .filter(registration -> customerId.equals(registration.getCustomerId()))
                .findFirst();

        assertTrue(actualResultCandidate.isPresent(), "customer not present in pushRegistration GET response");

        deletePushRegistrationsInfobip(infobipToken)
                .then()
                .assertThat()
                .statusCode(SC_NO_CONTENT);

        pushRegistrations = getPushRegistrations()
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(GetPushRegistrationResponseModel[].class);

        assertTrue(!Arrays.stream(pushRegistrations)
                .filter(registration -> customerId.equals(registration.getCustomerId()))
                .findAny().isPresent(), "customer has not been deleted after DELETE infobip request");
    }
}
