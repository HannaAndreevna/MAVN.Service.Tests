package com.lykke.tests.api.service.pushnotifications;

import static com.lykke.api.testing.api.base.RequestHeader.getHeader;
import static com.lykke.tests.api.base.Paths.PUSH_NOTIFICATIONS_CUSTOMER_ID_API_PATH;
import static com.lykke.tests.api.base.Paths.PUSH_NOTIFICATIONS_INFOBIP_TOKEN_API_PATH;
import static com.lykke.tests.api.base.Paths.PUSH_NOTIFICATIONS_REGISTRATIONS_API_PATH;
import static com.lykke.tests.api.base.Paths.PUSH_NOTIFICATIONS_REGISTRATION_ID_API_PATH;
import static com.lykke.tests.api.service.admin.LoginAdminUtils.getAdminToken;

import com.lykke.tests.api.service.pushnotifications.model.CreatePushRegistrationRequestModel;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import lombok.experimental.UtilityClass;

@UtilityClass
public class PushNotificationsUtils {

    public static final String INFOBIP_TOKEN = "Y3Rhc21zYXBpOkkhQ3RBcEkxMzUk";

    @Step
    public static Response postPushRegistrations(CreatePushRegistrationRequestModel requestObject) {
        return getHeader(getAdminToken())
                .body(requestObject)
                .post(PUSH_NOTIFICATIONS_REGISTRATIONS_API_PATH)
                .thenReturn();
    }

    @Step
    public Response getPushRegistrations() {
        return getHeader(getAdminToken())
                .get(PUSH_NOTIFICATIONS_REGISTRATIONS_API_PATH)
                .thenReturn();
    }

    @Step
    Response getPushRegistrations(String customerId) {
        return getHeader(getAdminToken())
                .get(PUSH_NOTIFICATIONS_CUSTOMER_ID_API_PATH.apply(customerId))
                .thenReturn();
    }

    @Step
    Response deletePushRegistrations(String registrationId) {
        return getHeader(getAdminToken())
                .delete(PUSH_NOTIFICATIONS_REGISTRATION_ID_API_PATH.apply(registrationId))
                .thenReturn();
    }

    @Step
    Response deletePushRegistrationsInfobip(String infoToken) {
        return getHeader(getAdminToken())
                .delete(PUSH_NOTIFICATIONS_INFOBIP_TOKEN_API_PATH.apply(infoToken))
                .thenReturn();
    }

    @Step
    public Response deletePushRegistrationsForCustomer(String customerId) {
        return getHeader(getAdminToken())
                .delete(PUSH_NOTIFICATIONS_CUSTOMER_ID_API_PATH.apply(customerId))
                .thenReturn();
    }
}
