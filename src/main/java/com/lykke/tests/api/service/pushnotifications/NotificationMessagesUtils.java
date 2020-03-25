package com.lykke.tests.api.service.pushnotifications;

import static com.lykke.api.testing.api.base.RequestHeader.getHeader;
import static com.lykke.api.testing.api.common.QueryParamsUtils.getQueryParams;
import static com.lykke.tests.api.base.Paths.PushNotifications.NOTIFICATION_MESSAGES_API_PATH;
import static com.lykke.tests.api.base.Paths.PushNotifications.NOTIFICATION_MESSAGES_READ_ALL_API_PATH;
import static com.lykke.tests.api.base.Paths.PushNotifications.NOTIFICATION_MESSAGES_READ_API_PATH;
import static com.lykke.tests.api.base.Paths.PushNotifications.NOTIFICATION_MESSAGES_UNREAD_COUNT_API_PATH;

import com.lykke.tests.api.service.pushnotifications.model.notificationmessages.MarkAllMessagesAsReadRequestModel;
import com.lykke.tests.api.service.pushnotifications.model.notificationmessages.MarkMessageAsReadRequestModel;
import com.lykke.tests.api.service.pushnotifications.model.notificationmessages.NotificationMessagesRequestModel;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import lombok.experimental.UtilityClass;

@UtilityClass
public class NotificationMessagesUtils {

    private static final String CUSTOMER_ID_QUERY_PARAM = "customerId";
    private static final String MESSAGE_GROUP_ID_QUERY_PARAM = "messageGroupId";

    public Response getNotificationMessages(NotificationMessagesRequestModel requestModel) {
        return getHeader()
                .queryParams(getQueryParams(requestModel))
                .get(NOTIFICATION_MESSAGES_API_PATH)
                .thenReturn();
    }

    public Response postReadNotifications(MarkMessageAsReadRequestModel requestModel) {
        return getHeader()
                .body(requestModel)
                .post(NOTIFICATION_MESSAGES_READ_API_PATH)
                .thenReturn();
    }

    public Response postReadAllNotifications(MarkAllMessagesAsReadRequestModel requestModel) {
        return getHeader()
                .body(requestModel)
                .post(NOTIFICATION_MESSAGES_READ_ALL_API_PATH)
                .thenReturn();
    }

    public Response getUnreadCount(String customerId) {
        return getHeader()
                .queryParam(CUSTOMER_ID_QUERY_PARAM, customerId)
                .contentType(ContentType.JSON)
                .get(NOTIFICATION_MESSAGES_UNREAD_COUNT_API_PATH)
                .thenReturn();
    }
}
