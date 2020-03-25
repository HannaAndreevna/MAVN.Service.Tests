package com.lykke.tests.api.service.customer;

import static com.lykke.api.testing.api.common.GenerateUtils.generateRandomString;
import static com.lykke.tests.api.service.customer.LoginLogoutCustomerUtils.getUserToken;
import static com.lykke.tests.api.service.customer.PushNotificationsUtils.deleteInfobipPushNotificaitonRegistration;
import static com.lykke.tests.api.service.customer.PushNotificationsUtils.deleteInfobipPushNotificaitonRegistration_Deprecated;
import static com.lykke.tests.api.service.customermanagement.RegisterCustomerUtils.registerDefaultVerifiedCustomer;
import static com.lykke.tests.api.service.pushnotifications.PushNotificationsUtils.getPushRegistrations;
import static com.lykke.tests.api.service.pushnotifications.PushNotificationsUtils.postPushRegistrations;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.http.HttpStatus.SC_NO_CONTENT;
import static org.apache.http.HttpStatus.SC_OK;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.params.provider.Arguments.of;

import com.lykke.api.testing.annotations.UserStoryId;
import com.lykke.tests.api.base.BaseApiTest;
import com.lykke.tests.api.service.pushnotifications.model.CreatePushRegistrationRequestModel;
import com.lykke.tests.api.service.pushnotifications.model.GetPushRegistrationResponseModel;
import com.lykke.tests.api.service.pushnotifications.model.PushTokenInsertionResult;
import java.util.Arrays;
import java.util.stream.Stream;
import lombok.val;
import lombok.var;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class PushNotificationsTests extends BaseApiTest {

    private static final int tokenLength = 10;

    static Stream<Arguments> getValidFirebaseAndOrAppleTokens() {
        return Stream.of(
                of(generateRandomString(tokenLength), EMPTY),
                of(EMPTY, generateRandomString(tokenLength)),
                of(generateRandomString(tokenLength), generateRandomString(tokenLength))
        );
    }

    @ParameterizedTest
    @MethodSource("getValidFirebaseAndOrAppleTokens")
    @UserStoryId(3815)
    void shouldDeletePushRegistrationsByCustomerId_Deprecated(String firebaseToken, String appleToken) {
        val customerData = registerDefaultVerifiedCustomer();
        val infobipToken = generateRandomString(tokenLength);
        val requestObject = CreatePushRegistrationRequestModel
                .builder()
                .customerId(customerData.getCustomerId())
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

        val actualResult = Arrays.stream(pushRegistrations)
                .filter(registration -> customerData.getCustomerId().equals(registration.getCustomerId()))
                .findFirst()
                .orElse(new GetPushRegistrationResponseModel());

        deleteInfobipPushNotificaitonRegistration_Deprecated(actualResult.getInfobipToken(), getUserToken(customerData))
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
                .filter(registration -> customerData.getCustomerId().equals(registration.getCustomerId()))
                .findAny().isPresent());
    }

    @ParameterizedTest
    @MethodSource("getValidFirebaseAndOrAppleTokens")
    @UserStoryId(3815)
    void shouldDeletePushRegistrationsByCustomerId(String firebaseToken, String appleToken) {
        val customerData = registerDefaultVerifiedCustomer();
        val infobipToken = generateRandomString(tokenLength);
        val requestObject = CreatePushRegistrationRequestModel
                .builder()
                .customerId(customerData.getCustomerId())
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

        val actualResult = Arrays.stream(pushRegistrations)
                .filter(registration -> customerData.getCustomerId().equals(registration.getCustomerId()))
                .findFirst()
                .orElse(new GetPushRegistrationResponseModel());

        deleteInfobipPushNotificaitonRegistration(actualResult.getInfobipToken(), getUserToken(customerData))
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
                .filter(registration -> customerData.getCustomerId().equals(registration.getCustomerId()))
                .findAny().isPresent());
    }
}
