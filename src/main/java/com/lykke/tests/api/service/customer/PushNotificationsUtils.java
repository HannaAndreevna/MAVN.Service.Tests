package com.lykke.tests.api.service.customer;

import static com.lykke.api.testing.api.base.RequestHeader.getHeader;
import static com.lykke.tests.api.base.Paths.Customer.PUSH_NOTIFICATIONA_REGISTRATIONS_API_PATH;
import static com.lykke.tests.api.base.Paths.Customer.PUSH_NOTIFICATION_REGISTRATIONS_BY_ID_API_PATH;

import com.lykke.api.testing.api.common.QueryParamsUtils;
import io.restassured.response.Response;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.UtilityClass;

@UtilityClass
public class PushNotificationsUtils {

    @Deprecated
    public Response deleteInfobipPushNotificaitonRegistration_Deprecated(String infobipPushRegistrationId,
            String token) {
        return getHeader(token)
                .delete(PUSH_NOTIFICATION_REGISTRATIONS_BY_ID_API_PATH.apply(infobipPushRegistrationId))
                .thenReturn();
    }

    public Response deleteInfobipPushNotificaitonRegistration(String infobipPushRegistrationId, String token) {
        return getHeader(token)
                .queryParams(QueryParamsUtils.getQueryParams(ByInfobipPushRegistrationId
                        .builder()
                        .infobipPushRegistrationId(infobipPushRegistrationId)
                        .build()))
                .delete(PUSH_NOTIFICATIONA_REGISTRATIONS_API_PATH)
                .thenReturn();
    }

    @AllArgsConstructor
    @Builder
    @Data
    public static final class ByInfobipPushRegistrationId {

        private String infobipPushRegistrationId;
    }
}
