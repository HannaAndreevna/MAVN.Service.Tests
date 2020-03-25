package com.lykke.tests.api.service.pushnotifications;

import static com.lykke.tests.api.common.PaginationConts.CURRENT_PAGE_LOWER_BOUNDARY;
import static com.lykke.tests.api.common.PaginationConts.PAGE_SIZE_UPPER_BOUNDARY;
import static com.lykke.tests.api.service.customermanagement.RegisterCustomerUtils.registerDefaultVerifiedCustomer;
import static com.lykke.tests.api.service.notificationsystem.NotificationMessageUtils.registerPushUserAndSendPushMessage;
import static com.lykke.tests.api.service.pushnotifications.NotificationMessagesUtils.getNotificationMessages;
import static com.lykke.tests.api.service.pushnotifications.NotificationMessagesUtils.getUnreadCount;
import static com.lykke.tests.api.service.pushnotifications.NotificationMessagesUtils.postReadAllNotifications;
import static com.lykke.tests.api.service.pushnotifications.NotificationMessagesUtils.postReadNotifications;
import static org.apache.http.HttpStatus.SC_NO_CONTENT;
import static org.apache.http.HttpStatus.SC_OK;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import com.lykke.api.testing.annotations.UserStoryId;
import com.lykke.tests.api.base.BaseApiTest;
import com.lykke.tests.api.common.model.CustomerInfo;
import com.lykke.tests.api.service.pushnotifications.model.notificationmessages.MarkAllMessagesAsReadRequestModel;
import com.lykke.tests.api.service.pushnotifications.model.notificationmessages.MarkMessageAsReadRequestModel;
import com.lykke.tests.api.service.pushnotifications.model.notificationmessages.NotificationMessageResponseModel;
import com.lykke.tests.api.service.pushnotifications.model.notificationmessages.NotificationMessagesRequestModel;
import com.lykke.tests.api.service.pushnotifications.model.notificationmessages.PaginatedResponseModel;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class NotificatitonMessagesTests extends BaseApiTest {

    private CustomerInfo customerData;
    private String customerId;
    private String messageId;

    @BeforeEach
    void setUp() {
        customerData = registerDefaultVerifiedCustomer();
        customerId = customerData.getCustomerId();
        messageId = registerPushUserAndSendPushMessage(customerId);
    }

    @Test
    @UserStoryId(3992)
    void shouldGetNotificationMessages() {
        val response = getNotificationMessages(
                NotificationMessagesRequestModel
                        .notificationMessagesRequestModelBuilder()
                        .currentPage(CURRENT_PAGE_LOWER_BOUNDARY)
                        .pageSize(CURRENT_PAGE_LOWER_BOUNDARY)
                        .customerId(customerId)
                        .build())
                .thenReturn();

        val actualPaginatedResponseModel = response
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(PaginatedResponseModel.class);

        val actualResult = response
                .then()
                .extract()
                .jsonPath()
                .getObject("Data[0]", NotificationMessageResponseModel.class);

        assertAll(
                () -> assertEquals(CURRENT_PAGE_LOWER_BOUNDARY, actualPaginatedResponseModel.getCurrentPage()),
                () -> assertEquals(CURRENT_PAGE_LOWER_BOUNDARY, actualPaginatedResponseModel.getPageSize()),
                () -> assertEquals(1, actualPaginatedResponseModel.getTotalCount()),
                () -> assertEquals(false, actualResult.isRead())
        );
    }

    @Test
    @UserStoryId(3992)
    void shouldPostReadNotifications() {
        val messageGroupId = getMessageGroupId(customerId);

        postReadNotifications(MarkMessageAsReadRequestModel
                .builder()
                .messageGroupId(messageGroupId)
                .build())
                .then()
                .assertThat()
                .statusCode(SC_NO_CONTENT);
    }

    @Test
    @UserStoryId(3992)
    void shouldPostReadAllNotifications() {
        postReadAllNotifications(MarkAllMessagesAsReadRequestModel
                .builder()
                .customerId(customerId)
                .build())
                .then()
                .assertThat()
                .statusCode(SC_NO_CONTENT);
    }

    @Test
    @UserStoryId(3992)
    void shouldGetUnreadCount() {
        val actualResult = getUnreadCount(customerId)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .response();

        assertNotEquals(0, actualResult.as(Integer.class));

    }

    private String getMessageGroupId(String customerIdParam) {
        val response = getNotificationMessages(
                NotificationMessagesRequestModel
                        .notificationMessagesRequestModelBuilder()
                        .currentPage(CURRENT_PAGE_LOWER_BOUNDARY)
                        .pageSize(PAGE_SIZE_UPPER_BOUNDARY)
                        .customerId(customerIdParam)
                        .build())
                .thenReturn();

        return response
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .jsonPath()
                .getObject("Data[0]", NotificationMessageResponseModel.class)
                .getMessageGroupId();
    }
}
