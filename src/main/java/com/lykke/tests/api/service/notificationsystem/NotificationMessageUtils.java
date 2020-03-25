package com.lykke.tests.api.service.notificationsystem;

import static com.lykke.api.testing.api.base.RequestHeader.getHeader;
import static com.lykke.api.testing.api.common.GenerateUtils.generateRandomString;
import static com.lykke.api.testing.api.common.QueryParamsUtils.getQueryParams;
import static com.lykke.tests.api.base.Paths.NotificationSystem.NOTIFICATION_MESSAGE_EMAIL_API_PATH;
import static com.lykke.tests.api.base.Paths.NotificationSystem.NOTIFICATION_MESSAGE_PUSH_API_PATH;
import static com.lykke.tests.api.base.Paths.NotificationSystem.NOTIFICATION_MESSAGE_SMS_API_PATH;
import static com.lykke.tests.api.service.pushnotifications.PushNotificationsUtils.postPushRegistrations;
import static org.apache.http.HttpStatus.SC_OK;

import com.lykke.tests.api.service.notificationsystem.model.EmailMessage;
import com.lykke.tests.api.service.notificationsystem.model.MessageResponseModel;
import com.lykke.tests.api.service.notificationsystem.model.SendPushNotificationRequest;
import com.lykke.tests.api.service.notificationsystem.model.SmsMessage;
import com.lykke.tests.api.service.pushnotifications.model.CreatePushRegistrationRequestModel;
import com.lykke.tests.api.service.pushnotifications.model.PushTokenInsertionResult;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.experimental.UtilityClass;

@UtilityClass
public class NotificationMessageUtils {

    private static final String AGENT_APPROVED_PUSH_NOTIFICATION_TEMPLATE = "agent-approved-push-notification";

    @Step("Send notification message")
    public Response sendNotificationEmailMessage(
            String customerId,
            String subjectTemplateId,
            String messageTemplateId,
            Map<String, String> templateParameters,
            String source) {
        return getHeader()
                .body(notificationEmailMessageObject(customerId, subjectTemplateId,
                        messageTemplateId, templateParameters, source))
                .post(NOTIFICATION_MESSAGE_EMAIL_API_PATH);
    }

    public Response sendNotificationEmailMessageAsMap(
            String customerId,
            String subjectTemplateId,
            String messageTemplateId,
            Map<String, String> templateParameters,
            String source) {
        Map<String, Object> map = getQueryParams(
                notificationEmailMessageObject(customerId, subjectTemplateId,
                        messageTemplateId, templateParameters, source))
                .entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue()));
        map.put("TemplateParameters", templateParameters);
        return getHeader()
                .body(map)
                .post(NOTIFICATION_MESSAGE_EMAIL_API_PATH);
    }

    @Step("Send notification sms")
    public Response sendNotificationSms(String customerId,
            String messageTemplateId, Map<String, String> templateParameters, String source) {
        return getHeader()
                .body(notificationSmsMessageObject(customerId, messageTemplateId, templateParameters, source))
                .post(NOTIFICATION_MESSAGE_EMAIL_API_PATH);
    }

    public Response sendNotificationSmsWithTemplateParametersAsMap(String customerId,
            String messageTemplateId, Map<String, String> templateParameters, String source) {
        Map<String, Object> map = getQueryParams(
                notificationSmsMessageObject(customerId,
                        messageTemplateId, templateParameters, source))
                .entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue()));
        map.put("TemplateParameters", templateParameters);
        return getHeader()
                .body(map)
                .post(NOTIFICATION_MESSAGE_EMAIL_API_PATH);
    }

    @Step("Send notification Push Message")
    public Response sendNotificationPushMessage(
            String customerId,
            String messageTemplateId,
            Map<String, String> templateParameters,
            Map<String, String> customPayload,
            String source) {
        return getHeader()
                .body(notificationPushMessageObject(customerId,
                        messageTemplateId, templateParameters, customPayload, source))
                .post(NOTIFICATION_MESSAGE_PUSH_API_PATH);
    }

    public Response sendNotificationPushMessageAsMap(
            String customerId,
            String messageTemplateId,
            Map<String, String> templateParameters,
            Map<String, String> customPayload,
            String source) {
        Map<String, Object> map = getQueryParams(
                notificationPushMessageObject(customerId,
                        messageTemplateId, templateParameters, customPayload, source))
                .entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue()));
        map.put("TemplateParameters", templateParameters);
        map.put("CustomPayload", customPayload);
        return getHeader()
                .body(map)
                .post(NOTIFICATION_MESSAGE_PUSH_API_PATH);
    }

    private static EmailMessage notificationMessageBaseObject(
            String customerId, String messageTemplateId, Map<String, String> templateParameters, String source) {
        return EmailMessage
                .builder()
                .customerId(customerId)
                .messageTemplateId(messageTemplateId)
                .templateParameters(templateParameters)
                .source(source)
                .build();
    }

    public Response sendNotificationSmsMessage(
            String customerId,
            String messageTemplateId,
            Map<String, String> templateParameters,
            String source) {
        return getHeader()
                .body(notificationSmsMessageObject(customerId,
                        messageTemplateId, templateParameters, source))
                .post(NOTIFICATION_MESSAGE_SMS_API_PATH);
    }

    public String registerPushUserAndSendPushMessage(String customerId) {
        postPushRegistrations(CreatePushRegistrationRequestModel
                .builder()
                .customerId(customerId)
                .infobipToken(generateRandomString(10))
                .firebaseToken(generateRandomString(10))
                .build())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(PushTokenInsertionResult.class);

        return getHeader()
                .body(SendPushNotificationRequest
                        .builder()
                        .customerId(customerId)
                        .messageTemplateId(AGENT_APPROVED_PUSH_NOTIFICATION_TEMPLATE)
                        .source("push src")
                        .build())
                .post(NOTIFICATION_MESSAGE_PUSH_API_PATH)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(MessageResponseModel.class)
                .getMessageIds()[0];
    }

    private EmailMessage notificationEmailMessageObject(
            String customerId, String subjectTemplateId,
            String messageTemplateId, Map<String, String> templateParameters, String source) {
        return EmailMessage
                .builder()
                .customerId(customerId)
                .subjectTemplateId(subjectTemplateId)
                .messageTemplateId(messageTemplateId)
                .templateParameters(templateParameters)
                .source(source)
                .build();
    }

    private SmsMessage notificationSmsMessageObject(
            String customerId,
            String messageTemplateId, Map<String, String> templateParameters, String source) {
        return SmsMessage
                .builder()
                .customerId(customerId)
                .messageTemplateId(messageTemplateId)
                .templateParameters(templateParameters)
                .source(source)
                .build();
    }

    private SendPushNotificationRequest notificationPushMessageObject(
            String customerId,
            String messageTemplateId,
            Map<String, String> templateParameters,
            Map<String, String> customPayload,
            String source) {
        return SendPushNotificationRequest
                .builder()
                .customerId(customerId)
                .messageTemplateId(messageTemplateId)
                .templateParameters(templateParameters)
                .customPayload(customPayload)
                .source(source)
                .build();
    }
}
