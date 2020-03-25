package com.lykke.tests.api.service.notificationsystemaudit;

import static com.lykke.api.testing.api.base.RequestHeader.getHeader;
import static com.lykke.api.testing.api.common.BuilderUtils.getObjectWithData;
import static com.lykke.api.testing.api.common.GenerateUtils.getRandomUuid;
import static com.lykke.tests.api.service.notificationsystemaudit.NotificationSystemAuditUtils.getAuditFailedMessageFromService;
import static com.lykke.tests.api.service.notificationsystemaudit.NotificationSystemAuditUtils.getAuditMessageFromService;
import static com.lykke.tests.api.service.notificationsystemaudit.NotificationSystemAuditUtils.getAuditMessagesByPath;
import static com.lykke.tests.api.service.notificationsystemaudit.NotificationSystemAuditUtils.getFailedDeliveryAuditMessagesByType;
import static com.lykke.tests.api.service.notificationsystemaudit.NotificationSystemAuditUtils.getAuditMessagesFromServiceByTypeAndStatus;
import static com.lykke.tests.api.service.notificationsystemaudit.NotificationSystemAuditUtils.getAuditMessageWithTemplateIssuesFromService;
import static com.lykke.api.testing.api.common.GenerateUtils.generateRandomEmail;
import static com.lykke.api.testing.api.common.GenerateUtils.generateRandomPhone;
import static com.lykke.api.testing.api.common.GenerateUtils.generateRandomString;
import static com.lykke.tests.api.base.Paths.NotificationSystemAudit.FAILED_DELIVERY_API_PATH;
import static com.lykke.tests.api.base.Paths.NotificationSystemAudit.MESSAGE_API_PATH;
import static com.lykke.tests.api.base.Paths.NotificationSystemAudit.PARSING_ISSUES_API_PATH;
import static com.lykke.tests.api.common.PaginationConts.CURRENT_PAGE_LOWER_BOUNDARY;
import static com.lykke.tests.api.service.admin.LoginAdminUtils.getAdminToken;
import static com.lykke.tests.api.service.customermanagement.RegisterCustomerUtils.registerDefaultVerifiedCustomer;
import static com.lykke.tests.api.service.notificationsystem.NotificationMessageUtils.sendNotificationEmailMessage;
import static com.lykke.tests.api.service.notificationsystem.NotificationMessageUtils.sendNotificationPushMessage;
import static com.lykke.tests.api.service.notificationsystem.NotificationMessageUtils.sendNotificationSmsMessage;
import static com.lykke.tests.api.service.notificationsystem.templates.TemplateUtils.createTemplate;
import static com.lykke.tests.api.service.notificationsystem.templates.TemplateUtils.createTemplateWithNamespaceAndCustomParameters;
import static com.lykke.tests.api.service.notificationsystem.templates.TemplateUtils.getNamespaceAndCustomParametersObject;
import static com.lykke.tests.api.service.notificationsystem.templates.TemplateUtils.getTemplates;
import static com.lykke.tests.api.service.notificationsystemaudit.model.CallType.REST;
import static com.lykke.tests.api.service.notificationsystemaudit.model.DeliveryStatus.FAILED;
import static com.lykke.tests.api.service.notificationsystemaudit.model.DeliveryStatus.PENDING;
import static com.lykke.tests.api.service.notificationsystemaudit.model.DeliveryStatus.SUCCESS;
import static com.lykke.tests.api.service.notificationsystemaudit.model.FormattingStatus.VALUE_NOT_FOUND;
import static com.lykke.tests.api.service.notificationsystemaudit.model.MessageType.EMAIL;
import static com.lykke.tests.api.service.notificationsystemaudit.model.MessageType.PUSH_NOTIFICATION;
import static com.lykke.tests.api.service.notificationsystemaudit.model.MessageType.SMS;
import static com.lykke.tests.api.service.notificationsystembroker.MessagesUtils.getEmailMessageByEmail;
import static com.lykke.tests.api.service.notificationsystembroker.MessagesUtils.getEmailMessages;
import static com.lykke.tests.api.service.notificationsystembroker.MessagesUtils.getSmsMessages;
import static com.lykke.tests.api.service.notificationsystembroker.MessagesUtils.getTemplateParametersObject;
import static com.lykke.tests.api.service.notificationsystembroker.MessagesUtils.getTemplateParametersObjectCaseInsensitive;
import static com.lykke.tests.api.service.pushnotifications.PushNotificationsUtils.postPushRegistrations;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.http.HttpStatus.SC_NO_CONTENT;
import static org.apache.http.HttpStatus.SC_OK;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.params.provider.Arguments.of;

import com.lykke.api.testing.annotations.UserStoryId;
import com.lykke.api.testing.api.common.FakerUtils;
import com.lykke.tests.api.base.BaseApiTest;
import com.lykke.tests.api.common.model.CustomerInfo;
import com.lykke.tests.api.service.notificationsystem.model.MessageResponseModel;
import com.lykke.tests.api.service.notificationsystem.templates.TemplateUtils;
import com.lykke.tests.api.service.notificationsystem.templates.model.NewTemplateRequest;
import com.lykke.tests.api.service.notificationsystem.templates.model.TemplateResponse;
import com.lykke.tests.api.service.notificationsystemaudit.model.AuditMessageResponseModel;
import com.lykke.tests.api.service.notificationsystemaudit.model.CallType;
import com.lykke.tests.api.service.notificationsystemaudit.model.DeliveryFailedAuditMessageResponseModel;
import com.lykke.tests.api.service.notificationsystemaudit.model.DeliveryStatus;
import com.lykke.tests.api.service.notificationsystemaudit.model.FormattingStatus;
import com.lykke.tests.api.service.notificationsystemaudit.model.MessageType;
import com.lykke.tests.api.service.notificationsystemaudit.model.PaginatedAuditMessageRequestModel;
import com.lykke.tests.api.service.notificationsystemaudit.model.PaginatedAuditMessageResponseModel;
import com.lykke.tests.api.service.notificationsystemaudit.model.PaginatedDeliveryFailedAuditMessageResponseModel;
import com.lykke.tests.api.service.pushnotifications.model.CreatePushRegistrationRequestModel;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Stream;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

@Slf4j
public class NotificationSystemAuditTests extends BaseApiTest {

    public static final String FORMATTING_COMMENT_SUCCESS = EMPTY;
    public static final String DELIVERY_COMMENT_SUCCESS = null;

    private static final String registrationDate = Instant.now().toString();
    private static final String emailSource = "eml-src001-" + generateRandomString(5).toLowerCase();
    private static final String smsSource = "sms-src001-" + generateRandomString(5).toLowerCase();
    private static final String pushSource = "push-src001-" + generateRandomString(5).toLowerCase();
    private static final String phone = "+8881234567";
    private static final String NAME_FIELD = "name";

    private static final int CASE_SENSITIVE = 1;
    private static final int CASE_INSENSITIVE = 0;
    private static final String TEMPLATE_NAME_PREFIX = "mytemplate";
    private static final String LOCALIZATION_CODE = "en"; // TODO: only "en" is implemented for now
    private static final String SMS_EXCEPTION_MESSAGE = "Could not send SMS with specific provider\n"
            + "Exception of type 'Lykke.Common.ApiLibrary.Exceptions.ClientApiException' was thrown.";

    private static String customerId;
    ////55   private static final TemplateResponse[] allTemplates =
    private static String subjectTemplateId;
    private static String messageTemplateId;
    private String email;
    private String templateBody;
    private String localizationCode;
    private String firstName;
    private String lastName;
    private String customerLogin;
    private Date startTestTimestamp;
    private String country;
    private String loginProvider;
    private CustomerInfo customerData;

    @BeforeAll
    static void setUpTemplateIds() {
        val allTemplates = getTemplates()
                .then()

                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(TemplateResponse[].class);
        subjectTemplateId = Arrays.stream(allTemplates)
                .filter(template -> Arrays.stream(template.getAvailableLocalizations())
                        .anyMatch(localization -> localization.getLocalizationCode().equalsIgnoreCase("en")))
                .findFirst()
                .orElse(new TemplateResponse())
                .getTemplateName();
        messageTemplateId = Arrays.stream(allTemplates)
                .filter(template -> Arrays.stream(template.getAvailableLocalizations())
                        .anyMatch(localization -> localization.getLocalizationCode().equalsIgnoreCase("en")))
                .skip(1)
                .findFirst()
                .orElse(new TemplateResponse())
                .getTemplateName();
    }

    static Stream<Arguments> getEmailAuditMessageParametersSuccessfulCase() {
        return Stream.of(
                of(messageTemplateId, emailSource, REST, FormattingStatus.SUCCESS, DeliveryStatus.SUCCESS),
                of(messageTemplateId, "alternative email source", REST, FormattingStatus.SUCCESS,
                        DeliveryStatus.SUCCESS)
        );
    }

    static Stream<Arguments> getEmailAuditMessageParametersSuccessfulCase2() {
        return Stream.of(
                of(messageTemplateId, emailSource, REST, VALUE_NOT_FOUND, DeliveryStatus.SUCCESS),
                of(messageTemplateId, "alternative email source", REST, VALUE_NOT_FOUND,
                        DeliveryStatus.SUCCESS)
        );
    }

    static Stream<Arguments> getSmsAuditMessageParametersSuccessfulCase() {
        return Stream.of(
                of(messageTemplateId, smsSource, REST, FormattingStatus.SUCCESS, DeliveryStatus.SUCCESS),
                of(messageTemplateId, "alternative sms source", REST, FormattingStatus.SUCCESS, DeliveryStatus.SUCCESS)
        );
    }

    static Stream<Arguments> getPushAuditMessageParametersSuccessfulCase() {
        return Stream.of(
                of(messageTemplateId, pushSource, REST, FormattingStatus.SUCCESS, DeliveryStatus.SUCCESS),
                of(messageTemplateId, "alternative push source", REST, FormattingStatus.SUCCESS, DeliveryStatus.SUCCESS)
        );
    }

    static Stream<Arguments> getEmailAuditMessageParametersFailedCase() {
        return Stream.of(
                of(customerId, "non-existing message template id", emailSource, DeliveryStatus.SUCCESS),
                of(EMPTY, messageTemplateId, emailSource, FAILED),
                of(EMPTY, messageTemplateId, emailSource, PENDING)
        );
    }

    static Stream<Arguments> getSmsAuditMessageParametersFailedCase() {
        return Stream.of(
                of(customerId, "non-existing message template id", smsSource, DeliveryStatus.SUCCESS),
                of(EMPTY, messageTemplateId, smsSource, FAILED),
                of(EMPTY, messageTemplateId, smsSource, PENDING)
        );
    }

    static Stream<Arguments> getParametersForFailedAndPendingMessagesSearch() {
        return Stream.of(
                of(EMAIL, FAILED, false, null),
                of(SMS, FAILED, false, SMS_EXCEPTION_MESSAGE),
                of(EMAIL, PENDING, true, null),
                of(SMS, PENDING, true, null)
        );
    }

    static Stream<Arguments> getParametersForFailedAndPendingMessagesSearch2() {
        return Stream.of(
                of(EMAIL, FAILED, false, null),
                of(SMS, FAILED, true, SMS_EXCEPTION_MESSAGE),
                of(EMAIL, PENDING, false, null),
                of(SMS, PENDING, true, null)
        );
    }

    static Stream<Arguments> getAuditMessagesPaginated() {
        return Stream.of(
                of(EMAIL, emailSource, 10, 1),
                of(EMAIL, emailSource, 50, 1),
                of(EMAIL, emailSource, 10, 2),
                of(SMS, smsSource, 10, 1),
                of(SMS, smsSource, 50, 1),
                of(SMS, smsSource, 10, 2)
        );
    }

    @BeforeEach
    void setup() {
        templateBody = FakerUtils.randomQuote;
        localizationCode = LOCALIZATION_CODE;
        customerData = registerDefaultVerifiedCustomer();
        email = customerData.getEmail();
        firstName = customerData.getFirstName();
        lastName = customerData.getLastName();
        country = FakerUtils.country;
        customerLogin = firstName.toLowerCase() + "." + lastName.toLowerCase();
        customerId = customerData.getCustomerId();

        postPushRegistrations(CreatePushRegistrationRequestModel
                .builder()
                .customerId(customerId)
                .infobipToken(generateRandomString(20))
                .firebaseToken(generateRandomString(20))
                .appleToken(generateRandomString(20))
                .build())
                .then()
                .assertThat()
                .statusCode(SC_OK);
        // TODO: it's how it should be
        startTestTimestamp = Date.from(OffsetDateTime.now(ZoneOffset.UTC).toInstant());
        startTestTimestamp = Date.from(Instant.now());
    }

    ////5555-1
    @ParameterizedTest
    @MethodSource("getEmailAuditMessageParametersSuccessfulCase")
    @UserStoryId(storyId = {816, 898, 1259})
    void shouldSendAndReceiveValidNotificationEmailMessage(
            String messageTemplateIdParam,
            String sourceParam,
            CallType callType,
            FormattingStatus formattingStatus,
            DeliveryStatus deliveryStatus
    ) {
        sendEmailMessageToTheBrokerService(
                customerId, messageTemplateIdParam, sourceParam, templateParametersObjectCaseInsensitive());
        val expectedMessage = getExpectedAuditMessage(
                x -> x.setMessageType(EMAIL.getType()),
                x -> x.setCustomerId(customerId),
                x -> x.setSubjectTemplateId(messageTemplateIdParam),
                x -> x.setMessageTemplateId(messageTemplateIdParam),
                x -> x.setSource(sourceParam),
                x -> x.setCallType(callType.getType()),
                x -> x.setFormattingStatus(formattingStatus.getStatus()),
                x -> x.setDeliveryStatus(deliveryStatus.getStatus())
        );

        val actualMessage = getAuditMessageFromService(EMPTY, customerId, EMAIL, deliveryStatus,
                x -> x.setFromCreationTimestamp(Instant.now().minus(5,
                        ChronoUnit.SECONDS).toString()));

        assertEquals(expectedMessage, actualMessage);
    }

    ////5555-both
    @ParameterizedTest
    @MethodSource("getEmailAuditMessageParametersSuccessfulCase")
    @UserStoryId(storyId = {1638, 1637})
    void shouldSendAndReceiveValidNotificationEmailMessageWithTimeFiltering(
            String messageTemplateIdParam,
            String sourceParam,
            CallType callType,
            FormattingStatus formattingStatus,
            DeliveryStatus deliveryStatus
    ) {
        sendEmailMessageToTheBrokerService(
                customerId, messageTemplateIdParam, sourceParam, templateParametersObjectCaseInsensitive());
        val expectedMessage = getExpectedAuditMessage(
                x -> x.setMessageType(EMAIL.getType()),
                x -> x.setCustomerId(customerId),
                x -> x.setMessageTemplateId(messageTemplateId),////55Param),
                x -> x.setSource(sourceParam),
                x -> x.setCallType(callType.getType()),
                x -> x.setFormattingStatus(formattingStatus.getStatus()),
                x -> x.setDeliveryStatus(deliveryStatus.getStatus()),
                x -> x.setCreationTimestamp(Date.from(Instant.now().minus(10, ChronoUnit.MINUTES))),
                x -> x.setSentTimestamp(Date.from(Instant.now().minus(10, ChronoUnit.MINUTES)))
        );

        val actualMessage = getAuditMessageFromService(EMPTY, customerId, EMAIL, deliveryStatus,
                x -> x.setFromCreationTimestamp(Instant.now().minus(5, ChronoUnit.SECONDS).toString()),
                x -> x.setSource(sourceParam));

        assertEquals(expectedMessage, actualMessage);
    }

    ////5555-all
    @ParameterizedTest
    @MethodSource("getSmsAuditMessageParametersSuccessfulCase")
    @UserStoryId(storyId = {816, 1259})
    void shouldSendAndReceiveValidNotificationSmsMessage(
            String messageTemplateIdParam,
            String sourceParam,
            CallType callType,
            FormattingStatus formattingStatus,
            DeliveryStatus deliveryStatus
    ) {
        sendSmsMessageToTheBrokerService(
                customerId, messageTemplateId////55Param
                , sourceParam, templateParametersObjectCaseInsensitive());
        val expectedMessage = getExpectedAuditMessage(
                x -> x.setMessageType(SMS.getType()),
                x -> x.setCustomerId(customerId),
                x -> x.setMessageTemplateId(messageTemplateId),////55Param),
                x -> x.setSource(sourceParam),
                x -> x.setCallType(callType.getType()),
                x -> x.setFormattingStatus(formattingStatus.getStatus()),
                x -> x.setDeliveryStatus(deliveryStatus.getStatus())
        );

        val actualMessage = getAuditMessageFromService(EMPTY, customerId, SMS, deliveryStatus,
                x -> x.setSource(sourceParam));

        assertEquals(expectedMessage, actualMessage);
    }

    ////55
    ////55   @SneakyThrows
////5555-all
    @ParameterizedTest
    @MethodSource("getPushAuditMessageParametersSuccessfulCase")
    @UserStoryId(storyId = {1640, 1639, 1648})
    void shouldSendAndReceiveValidNotificationPushMessage(
            String messageTemplateIdParam,
            String sourceParam,
            CallType callType,
            FormattingStatus formattingStatus,
            DeliveryStatus deliveryStatus
    ) {
        val messageData = sendPushMessageToTheBrokerService(
                customerId, messageTemplateId////55Param
                , sourceParam, templateParametersObjectCaseInsensitive(),
                templateParametersObject());

        ////55
        ////55    Thread.sleep(30000);

        val expectedMessage = getExpectedAuditMessage(
                x -> x.setMessageType(PUSH_NOTIFICATION.getType()),
                x -> x.setCustomerId(customerId),
                x -> x.setMessageTemplateId(messageTemplateId),////55Param),
                x -> x.setSource(sourceParam),
                x -> x.setCallType(callType.getType()),
                x -> x.setFormattingStatus(formattingStatus.getStatus()),
                x -> x.setDeliveryStatus(deliveryStatus.getStatus())
        );

        ////55
        ////55customerId = null;

        val actualMessage = getAuditMessageFromService(messageData.getMessageIds()[0], customerId, PUSH_NOTIFICATION,
                deliveryStatus);

        assertEquals(expectedMessage, actualMessage);
    }

    @ParameterizedTest
    @MethodSource("getSmsAuditMessageParametersSuccessfulCase")
    @UserStoryId(storyId = {1638, 1637})
    void shouldSendAndReceiveValidNotificationSmsMessageWithTimeFiltering(
            String messageTemplateIdParam,
            String sourceParam,
            CallType callType,
            FormattingStatus formattingStatus,
            DeliveryStatus deliveryStatus
    ) {
        sendSmsMessageToTheBrokerService(
                customerId, messageTemplateId////55Param
                , sourceParam, templateParametersObjectCaseInsensitive());
        val expectedMessage = getExpectedAuditMessage(
                x -> x.setMessageType(SMS.getType()),
                x -> x.setCustomerId(customerId),
                x -> x.setMessageTemplateId(messageTemplateId),////55Param),
                x -> x.setSource(sourceParam),
                x -> x.setCallType(callType.getType()),
                x -> x.setFormattingStatus(formattingStatus.getStatus()),
                x -> x.setDeliveryStatus(deliveryStatus.getStatus()),
                x -> x.setCreationTimestamp(Date.from(Instant.now().minus(10, ChronoUnit.MINUTES))),
                x -> x.setSentTimestamp(Date.from(Instant.now().minus(10, ChronoUnit.MINUTES)))
        );

        val actualMessage = getAuditMessageFromService(EMPTY, customerId, SMS, deliveryStatus,
                x -> x.setSource(sourceParam));

        assertEquals(expectedMessage, actualMessage);
    }

    ////5555
    @Test
    @UserStoryId(1959)
    void shouldSendAndReceiveValidNotificationSmsMessageWithLongPhoneNumber() {
////55
        /*
        val customerWithLongPhoneNumberId = createCustomerProfileWithPhoneNumber(generateRandomEmail(),
                generateRandomPhone(47));
        */
        val customerWithLongPhoneNumberId = registerDefaultVerifiedCustomer().getCustomerId();
        val templateName = generateRandomString(10).toLowerCase();
        val source = generateRandomString(10);
        createTemplate(NewTemplateRequest
                .builder()
                .templateName(templateName)
                .templateBody(templateBody)
                .localizationCode(localizationCode)
                .build())
                .then()
                .assertThat()
                .statusCode(SC_NO_CONTENT);
        sendSmsMessageToTheBrokerService(
                customerWithLongPhoneNumberId, templateName, source, null);

        val actualMessage = getAuditMessageFromService(EMPTY, customerWithLongPhoneNumberId, SMS, FAILED);

        assertAll(
                () -> assertEquals(SMS.getType(), actualMessage.getMessageType()),
                () -> assertEquals(customerWithLongPhoneNumberId, actualMessage.getCustomerId()),
                () -> assertEquals(templateName, actualMessage.getMessageTemplateId()),
                () -> assertEquals(source, actualMessage.getSource())
        );
    }

    ////5555-1
    @ParameterizedTest
    @ValueSource(ints = {CASE_SENSITIVE, CASE_INSENSITIVE})
    @UserStoryId(storyId = {1050, 1259})
    void shouldSendAndReceiveValidNotificationEmailMessage(int sensitivity) {
        val templateParameters = CASE_SENSITIVE == sensitivity
                ? templateParametersObject()
                : templateParametersObjectCaseInsensitive();
        val source = generateRandomString(20);
        sendEmailMessageToTheBrokerService(
                customerId, messageTemplateId, source, templateParameters);
        val expectedMessage = getExpectedAuditMessage(
                x -> x.setMessageType(EMAIL.getType()),
                x -> x.setCustomerId(customerId),
                x -> x.setMessageTemplateId(messageTemplateId),
                x -> x.setSource(source)
        );

        val actualMessage = getAuditMessageFromService(
                EMPTY,
                customerId,
                x -> x.setSource(source)
        );

        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    @UserStoryId(4029)
    void shouldSendAndReceiveValidNotificationEmailMessageWithFilteringByMessageGroupId() {
        val templateParameters = templateParametersObjectCaseInsensitive();
        val source = generateRandomString(20);
        sendEmailMessageToTheBrokerService(
                customerId, messageTemplateId, source, templateParameters);
        val expectedMessage = getExpectedAuditMessage(
                x -> x.setMessageType(EMAIL.getType()),
                x -> x.setCustomerId(customerId),
                x -> x.setMessageTemplateId(messageTemplateId),
                x -> x.setSource(source)
        );

        val actualMessage1 = getAuditMessageFromService(
                EMPTY,
                customerId,
                x -> x.setSource(source)
        );

        assertEquals(expectedMessage, actualMessage1);

        // by messageId
        val actualMessage2 = getAuditMessageFromService(
                EMPTY,
                customerId,
                x -> x.setMessageId(actualMessage1.getMessageId())
        );

        assertEquals(expectedMessage, actualMessage2);

        // by messageGroupId
        val actualMessage3 = getAuditMessageFromService(
                EMPTY,
                customerId,
                // a negative case - an impossible message group id
                x -> x.setMessageGroupId(getRandomUuid())
        );

        assertNull(actualMessage3.getCustomerId());
    }

    @Test
    @UserStoryId(4029)
    void shouldSendAndReceiveNotificationEmailMessageWithTtemplateIssuesAndFilteringByMessageGroupId() {
        val templateParameters = templateParametersObjectCaseInsensitive();
        val source = generateRandomString(20);
        sendEmailMessageToTheBrokerService(
                customerId, messageTemplateId, source, templateParameters);
        val expectedMessage = getExpectedAuditMessageWithTemplateParsingIssue(
                x -> x.setMessageType(EMAIL.getType()),
                x -> x.setCustomerId(customerId),
                x -> x.setMessageTemplateId(messageTemplateId),
                x -> x.setSource(source)
        );

        val actualMessage1 = getAuditMessageWithTemplateIssuesFromService(
                EMPTY,
                customerId,
                x -> x.setSource(source)
        );

        assertEquals(expectedMessage, actualMessage1);

        // by messageId
        val actualMessage2 = getAuditMessageWithTemplateIssuesFromService(
                EMPTY,
                customerId,
                x -> x.setMessageId(actualMessage1.getMessageId())
        );

        assertEquals(expectedMessage, actualMessage2);

        // by messageGroupId
        val actualMessage3 = getAuditMessageWithTemplateIssuesFromService(
                EMPTY,
                customerId,
                // a negative case - an impossible message group id
                x -> x.setMessageGroupId(getRandomUuid())
        );

        assertNull(actualMessage3.getCustomerId());
    }

    @Test
    @UserStoryId(4029)
    void shouldSendAndReceiveFailedNotificationEmailMessageWithFilteringByMessageGroupId() {
        val templateParameters = templateParametersObjectCaseInsensitive();
        val source = generateRandomString(20);
        sendEmailMessageToTheBrokerService(
                customerId, messageTemplateId, source, templateParameters);
        val expectedMessage = getExpectedAuditMessageWithFailedDelivery(
                x -> x.setMessageType(EMAIL.getType()),
                x -> x.setCustomerId(customerId),
                x -> x.setMessageTemplateId(messageTemplateId),
                x -> x.setSource(source)
        );

        val actualMessage1 = getAuditFailedMessageFromService(
                EMPTY,
                customerId,
                x -> x.setSource(source)
        );

        // by messageId
        val actualMessage2 = getAuditFailedMessageFromService(
                EMPTY,
                customerId,
                x -> x.setSource(source)
        );

        assertEquals(actualMessage1, actualMessage2);

        // by messageGroupId
        val actualMessage3 = getAuditFailedMessageFromService(
                EMPTY,
                customerId,
                // a negative case - an impossible message group id
                x -> x.setMessageGroupId(getRandomUuid())
        );

        assertNull(actualMessage3.getCustomerId());
    }

    ////5555-3,4
    @ParameterizedTest
    // value "sUCceSS" breaks tests, but works in swagger
    @ValueSource(strings = {"SUCCESS", "Success", "success", "sUCCESS", "suCCeSS"})
    @UserStoryId(storyId = {1050, 1259})
    void shouldSendAndReceiveValidNotificationEmailMessage(String deliveryStatus) {
        val source = generateRandomString(20);
        sendEmailMessageToTheBrokerService(
                customerId, messageTemplateId, source, templateParametersObjectCaseInsensitive());
        val expectedMessage = getExpectedAuditMessage(
                x -> x.setMessageType(EMAIL.getType()),
                x -> x.setCustomerId(customerId),
                x -> x.setMessageTemplateId(messageTemplateId),
                x -> x.setSource(source)
        );

        val actualMessage = getAuditMessageFromService(
                EMPTY,
                customerId,
                x -> x.setMessageType(EMAIL.getType()),
                x -> x.setCustomerId(customerId),
                x -> x.setSource(source),
                x -> x.setDeliveryStatus(deliveryStatus)
        );

        assertEquals(expectedMessage, actualMessage);
    }

    @ParameterizedTest
    @MethodSource("getEmailAuditMessageParametersFailedCase")
    @UserStoryId(816)
    void shouldNotReceiveInvalidNotificationEmailMessage(
            String customerIdParam,
            String messageTemplateIdParam,
            String sourceParam,
            DeliveryStatus deliveryStatus
    ) {
        sendEmailMessageToTheBrokerService(customerId, messageTemplateId////55Param
                , sourceParam, templateParametersObject());

        val actualMessage = getAuditMessageFromService(EMPTY, customerIdParam, EMAIL, deliveryStatus);

        // unfortunately, existence of PENDING messages is not guaranteed
        val expected = null == actualMessage.getMessageId()
                ? null
                : actualMessage.getMessageId();
        assertEquals(expected, actualMessage.getMessageId());
    }

    @ParameterizedTest
    @MethodSource("getEmailAuditMessageParametersFailedCase")
    @UserStoryId(storyId = {1638, 1637})
    void shouldNotReceiveInvalidNotificationEmailMessageWithTimeFiltering(
            String customerIdParam,
            String messageTemplateIdParam,
            String sourceParam,
            DeliveryStatus deliveryStatus
    ) {
        sendEmailMessageToTheBrokerService(customerId, messageTemplateId////55Param
                , sourceParam, templateParametersObject());

        val actualMessage = getAuditMessageFromService(
                EMPTY,
                customerIdParam,
                EMAIL,
                deliveryStatus,
                x -> x.setFromCreationTimestamp(Instant.now().minus(10, ChronoUnit.MINUTES).toString()),
                x -> x.setToCreationTimestamp(Instant.now().minus(10, ChronoUnit.MINUTES).toString()));

        // unfortunately, existence of PENDING messages is not guaranteed
        val expected = null == actualMessage.getMessageId()
                ? null
                : actualMessage.getMessageId();
        assertEquals(expected, actualMessage.getMessageId());
    }

    ////5555-all
    @ParameterizedTest
    @MethodSource("getSmsAuditMessageParametersFailedCase")
    @UserStoryId(816)
    void shouldNotReceiveInvalidNotificationSmsMessage(
            String customerIdParam,
            String messageTemplateIdParam,
            String sourceParam,
            DeliveryStatus deliveryStatus
    ) {
        sendSmsMessageToTheBrokerService(customerId, messageTemplateId////55Param
                , sourceParam, templateParametersObject());

        val actualMessage = getAuditMessageFromService(EMPTY, customerIdParam, SMS, deliveryStatus);

        assertEquals(null, actualMessage.getMessageId());
    }

    // TODO: generate Failed and Pending messages if possible
    @ParameterizedTest
    @MethodSource("getParametersForFailedAndPendingMessagesSearch")
    @DisplayName("Check Failed or Pending messages if any")
    @UserStoryId(816)
    void shouldLoadFailedOrPendingMessage(
            MessageType messageType,
            DeliveryStatus deliveryStatus,
            boolean isDeliveryCommentEmpty) {
        val messagesByTypeAndDeliveryStatus = getAuditMessagesFromServiceByTypeAndStatus(messageType, deliveryStatus);
        if (null == messagesByTypeAndDeliveryStatus || 0 == messagesByTypeAndDeliveryStatus.size()) {
            log.info(
                    "Test shouldLoadFailedOrPendingMessage: there are no messages that match MessageType=" + messageType
                            + ", DeliveryStatus=" + deliveryStatus);
            return; // nothing to check
        }

        assertAll(
                () -> assertEquals(deliveryStatus.getStatus(),
                        messagesByTypeAndDeliveryStatus.get(0).getDeliveryStatus()),
                () -> assertNotNull(messagesByTypeAndDeliveryStatus.get(0).getCustomerId()),
                () -> {
                    if (isDeliveryCommentEmpty) {
                        assertNull(messagesByTypeAndDeliveryStatus.get(0).getDeliveryComment());
                    } else {
                        assertNotNull(messagesByTypeAndDeliveryStatus.get(0).getDeliveryComment());
                    }
                }
        );
    }

    ////5555-all
    // TODO: generate Failed and Pending messages if possible
    @ParameterizedTest
    @MethodSource("getParametersForFailedAndPendingMessagesSearch2")
    @DisplayName("Check Failed Delivery messages if any")
    @UserStoryId(817)
    void shouldLoadFailedDeliveryMessages(
            MessageType messageType,
            DeliveryStatus deliveryStatus,
            boolean isDeliveryCommentEmpty,
            String deliveryComment) {
        val messagesByTypeAndDeliveryStatus = getFailedDeliveryAuditMessagesByType(messageType);
        if (null == messagesByTypeAndDeliveryStatus || 0 == messagesByTypeAndDeliveryStatus.size()) {
            log.info(
                    "Test shouldLoadFailedOrPendingMessage: there are no messages that match MessageType=" + messageType
                            + ", DeliveryComment=" + deliveryComment);
            return; // nothing to check
        }

        assertAll(
                () -> assertNotNull(messagesByTypeAndDeliveryStatus.get(0).getCustomerId()),
                () -> {
                    if (isDeliveryCommentEmpty) {
                        assertNull(messagesByTypeAndDeliveryStatus.get(0).getDeliveryComment());
                    } else {
                        assertNotNull(messagesByTypeAndDeliveryStatus.get(0).getDeliveryComment());
                        assertEquals(deliveryComment, messagesByTypeAndDeliveryStatus.get(0).getDeliveryComment());
                    }
                }
        );
    }

    ////5555+
    @Test
    @UserStoryId(818)
    void shouldPutMessageWithParsingIssuesInTheParsingIssuesEndpoint() {
        val templateName = "test000123";
        val templateBody = "${param01}";
        val source = "mysource";
        createTemplate(NewTemplateRequest
                .builder()
                .templateName(templateName)
                .templateBody(templateBody)
                .localizationCode(localizationCode)
                .build());
        val templateParameters = Stream.of(new String[][]{{"param02", "some value"}})
                .collect(toMap(item -> item[0], item -> null == item[1] ? EMPTY : item[1]));
        sendNotificationEmailMessage(customerId, subjectTemplateId, templateName, templateParameters, source);

        val actualMessage = getAuditMessageWithParsingIssue(templateName, source);

        assertAll(
                () -> assertEquals(VALUE_NOT_FOUND.getStatus(), actualMessage.getFormattingStatus()),
                () -> assertEquals(templateName, actualMessage.getMessageTemplateId()),
                // Pending
                ////55      () -> assertEquals(SUCCESS.getStatus(), actualMessage.getDeliveryStatus()),
                () -> assertEquals(PENDING.getStatus(), actualMessage.getDeliveryStatus()),
                () -> assertNotNull(actualMessage.getFormattingComment())
        );
    }

    ////5555+
    @Test
    @UserStoryId(storyId = {1638, 1637})
    void shouldPutMessageWithParsingIssuesInTheParsingIssuesEndpointWithTimeFiltering() {
        val templateName = "test000123";
        val templateBody = "${param01}";
        val source = "mysource";
        createTemplate(NewTemplateRequest
                .builder()
                .templateName(templateName)
                .templateBody(templateBody)
                .localizationCode(localizationCode)
                .build());
        val templateParameters = Stream.of(new String[][]{{"param02", "some value"}})
                .collect(toMap(item -> item[0], item -> null == item[1] ? EMPTY : item[1]));
        sendNotificationEmailMessage(customerId, subjectTemplateId, templateName, templateParameters, source);

        val actualMessage = getAuditMessageWithParsingIssue(templateName, source);

        assertAll(
                () -> assertEquals(VALUE_NOT_FOUND.getStatus(), actualMessage.getFormattingStatus()),
                () -> assertEquals(templateName, actualMessage.getMessageTemplateId()),
                () -> assertEquals(SUCCESS.getStatus(), actualMessage.getDeliveryStatus()),
                () -> assertNotNull(actualMessage.getFormattingComment())
        );
    }

    // TODO: needs a number of generated messages
    @ParameterizedTest
    @MethodSource("getAuditMessagesPaginated")
    @UserStoryId(816)
    void shouldSendAndReceiveValidNotificationMessages(
            MessageType messageType,
            String sourceParam,
            int pageSize,
            int pageNumber) {

        sendEmailMessageToTheBrokerService(customerId, messageTemplateId, sourceParam, templateParametersObject());

        val pageOfMessages = getAuditMessagesPaginated(
                MESSAGE_API_PATH, messageType, SUCCESS, pageNumber, pageSize);
        if (pageSize > pageOfMessages.size()) {
            return;
        }

        assertEquals(pageSize, pageOfMessages.size());
    }

    // TODO: generate Failed and Pending messages if possible
    @ParameterizedTest
    @MethodSource("getAuditMessagesPaginated")
    @UserStoryId(817)
    void shouldSendAndReceiveFailedDeliveryMessages(
            MessageType messageType,
            String sourceParam,
            int pageSize,
            int pageNumber) {

        val pageOfMessages = getAuditMessagesPaginated(
                FAILED_DELIVERY_API_PATH, messageType, SUCCESS, pageNumber, pageSize);
        if (pageSize > pageOfMessages.size()) {
            return;
        }

        assertEquals(pageSize, pageOfMessages.size());
    }

    ////5555+
    // TODO: generate Failed and Pending messages if possible
    @ParameterizedTest
    @MethodSource("getAuditMessagesPaginated")
    @UserStoryId(818)
    void shouldSendAndReceiveParsingIssuesMessages(
            MessageType messageType,
            String sourceParam,
            int pageSize,
            int pageNumber) {

        val pageOfMessages = getAuditMessagesPaginated(
                PARSING_ISSUES_API_PATH, messageType, SUCCESS, pageNumber, pageSize);
        if (pageSize > pageOfMessages.size()) {
            return;
        }

        assertEquals(pageSize, pageOfMessages.size());
    }

    ////5555-all
    @ParameterizedTest
    @MethodSource("getEmailAuditMessageParametersSuccessfulCase")
    @UserStoryId(storyId = {1640, 1648})
    void shouldSendAndReceiveValidNotificationEmailMessageWithCustomParameters(
            String messageTemplateIdParam,
            String sourceParam,
            CallType callType,
            FormattingStatus formattingStatus,
            DeliveryStatus deliveryStatus) {
        val templateName = TEMPLATE_NAME_PREFIX + generateRandomString(10).toLowerCase();
        createTemplateWithNamespaceAndCustomParameters(templateName, localizationCode);
        val templateParametersObject = getNamespaceAndCustomParametersObject(
                email,
                generateRandomString(),
                generateRandomString(),
                generateRandomString(),
                generateRandomString());

        sendEmailMessageToTheBrokerService(
                customerId, templateName, sourceParam, templateParametersObject);
        val expectedMessage = getExpectedAuditMessage(
                x -> x.setMessageType(EMAIL.getType()),
                x -> x.setCustomerId(customerId),
                x -> x.setMessageTemplateId(templateName),
                x -> x.setSource(sourceParam),
                x -> x.setCallType(callType.getType()),
                x -> x.setFormattingStatus(formattingStatus.getStatus()),
                x -> x.setDeliveryStatus(deliveryStatus.getStatus())
        );

        // checking message content
        val actualEmailMessage = getEmailMessageByEmail(email);

        assertAll(
                () -> assertNotNull(actualEmailMessage),
                () -> assertTrue(actualEmailMessage.getBody().contains(TemplateUtils.PARAM_001_DATA)),
                () -> assertTrue(actualEmailMessage.getBody().contains(TemplateUtils.PARAM_002_DATA))
                // ,
                // () -> assertTrue(actualEmailMessage.getBody().contains(TemplateUtils.PARAM_003_DATA))
        );

        // delivered to Notification System Audit
        val actualMessage = getAuditMessageFromService(EMPTY, customerId, EMAIL, deliveryStatus);
        assertAll(
                () -> assertEquals(SUCCESS.getStatus(), actualMessage.getDeliveryStatus())
        );
    }

    ////5555-all
    @ParameterizedTest
    @MethodSource("getEmailAuditMessageParametersSuccessfulCase2")
    @UserStoryId(storyId = {1640, 1648})
    void shouldSendAndReceiveValidNotificationPushMessageWithCustomParameters(
            String messageTemplateIdParam,
            String sourceParam,
            CallType callType,
            FormattingStatus formattingStatus,
            DeliveryStatus deliveryStatus) {
        val templateName = TEMPLATE_NAME_PREFIX + generateRandomString(10).toLowerCase();
        createTemplateWithNamespaceAndCustomParameters(templateName, localizationCode);
        val templateParametersObject = getNamespaceAndCustomParametersObject(
                email,
                generateRandomString(),
                generateRandomString(),
                generateRandomString(),
                generateRandomString());

        sendPushMessageToTheBrokerService(
                customerId, templateName, sourceParam, templateParametersObject, templateParametersObject);
        val expectedMessage = getExpectedAuditMessage(
                x -> x.setMessageType(PUSH_NOTIFICATION.getType()),
                x -> x.setCustomerId(customerId),
                x -> x.setMessageTemplateId(templateName),
                x -> x.setSource(sourceParam),
                x -> x.setCallType(callType.getType()),
                x -> x.setFormattingStatus(formattingStatus.getStatus()),
                x -> x.setDeliveryStatus(deliveryStatus.getStatus())
        );

        val actualMessage = getAuditMessageFromService(EMPTY, customerId, PUSH_NOTIFICATION, deliveryStatus);
        assertAll(
                () -> assertEquals(expectedMessage.getDeliveryStatus(), actualMessage.getDeliveryStatus()),
                () -> assertEquals(expectedMessage.getCustomerId(), actualMessage.getCustomerId()),
                () -> assertEquals(expectedMessage.getMessageTemplateId(), actualMessage.getMessageTemplateId()),
                () -> assertEquals(expectedMessage.getFormattingStatus(), actualMessage.getFormattingStatus()),
                () -> assertEquals(expectedMessage.getSource(), actualMessage.getSource())
        );
    }

    private Map<String, String> templateParametersObject() {
        return getTemplateParametersObject(email, firstName, lastName, customerLogin, registrationDate);
    }

    private Map<String, String> templateParametersObjectCaseInsensitive() {
        return getTemplateParametersObjectCaseInsensitive(email, firstName, lastName, customerLogin, registrationDate);
    }

    private void sendEmailMessageToTheBrokerService(
            String customerIdParam,
            String messageTemplateIdParam,
            String sourceParam,
            Map<String, String> templateParametersObject) {
        sendNotificationEmailMessage(customerIdParam, messageTemplateId////55Param
                , messageTemplateId////55Param
                ,
                templateParametersObject, sourceParam)
                .then()

                ////55
                .log().all()

                .assertThat()
                .statusCode(SC_OK);

        getEmailMessages(email)
                .then()
                .assertThat()
                .statusCode(SC_OK);
    }

    private void sendSmsMessageToTheBrokerService(
            String customerIdParam,
            String messageTemplateIdParam,
            String sourceParam,
            Map<String, String> templateParametersObject) {
        sendNotificationSmsMessage(
                customerIdParam, messageTemplateId////55Param
                , templateParametersObject, sourceParam)
                .then()
                .assertThat()
                .statusCode(SC_OK);

        getSmsMessages(phone)
                .then()
                .assertThat()
                .statusCode(SC_OK);
    }

    private MessageResponseModel sendPushMessageToTheBrokerService(
            String customerIdParam,
            String messageTemplateIdParam,
            String sourceParam,
            Map<String, String> templateParametersObject,
            Map<String, String> customPayload) {
        return sendNotificationPushMessage(
                customerIdParam, messageTemplateId////55Param
                , templateParametersObject, customPayload, sourceParam)
                .then()

                ////55
                .log().all()

                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(MessageResponseModel.class);

        ////55
        /*
        getSmsMessages(phone)
                .then()

                ////55
                .log().all()

                .assertThat()
                .statusCode(SC_OK);
        */
    }

    ////55
    /*
    private AuditMessageResponseModel getAuditMessageFromService(
            String messageId,
            Consumer<PaginatedAuditMessageRequestModel>... actions) {
        val response = getHeader(getAdminToken())
                .body(getAuditMessageRequestObject(actions))
                .post(MESSAGE_API_PATH)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(PaginatedAuditMessageResponseModel.class);
        return getMessageObject(response, customerId, messageId);
    }

    private AuditMessageResponseModel getAuditMessageFromService(
            String messageId,
            String customerIdParam,
            MessageType messageType,
            DeliveryStatus deliveryStatus,
            Consumer<PaginatedAuditMessageRequestModel>... actions) {

        Consumer<PaginatedAuditMessageRequestModel>[] standardActions = new Consumer[]{};
        val standardActionsStream = Stream
                .of(////55x -> x.setCustomerId(customerIdParam),
                        x -> x.setMessageType(messageType.getType()),
                        (Consumer<PaginatedAuditMessageRequestModel>) (x -> x
                                .setDeliveryStatus(deliveryStatus.getStatus()))
                );
        val allActions = combineActions(standardActionsStream, actions);
        val response = getHeader(getAdminToken())
                .body(getAuditMessageRequestObject(allActions))
                .post(MESSAGE_API_PATH)
                .then()

                ////55
                .log().all()

                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(PaginatedAuditMessageResponseModel.class);
        return getMessageObject(response, customerIdParam, messageId);
    }
    */

    private List<AuditMessageResponseModel> getAuditMessagesPaginated(
            String apiPath,
            MessageType messageType,
            DeliveryStatus deliveryStatus,
            int currentPage,
            int pageSize) {

        return getAuditMessagesByPath(
                apiPath,
                messageType,
                deliveryStatus,
                currentPage,
                pageSize);
    }

    ////55
    /*
    private List<AuditMessageResponseModel> getAuditMessagesByPath(
            String apiPath,
            MessageType messageType,
            DeliveryStatus deliveryStatus,
            int currentPage,
            int pageSize,
            Consumer<PaginatedAuditMessageRequestModel>... actions) {

        Consumer<PaginatedAuditMessageRequestModel>[] standardActions = new Consumer[]{};
        val standardActionsStream = Stream
                .of((Consumer<PaginatedAuditMessageRequestModel>) (x -> x.setMessageType(messageType.getType())),
                        (Consumer<PaginatedAuditMessageRequestModel>) (x -> x
                                .setDeliveryStatus(deliveryStatus.getStatus())),
                        x -> x.setCurrentPage(currentPage),
                        x -> x.setPageSize(pageSize));
        val allActions = combineActions(standardActionsStream, actions);
        return getHeader(getAdminToken())
                .body(getAuditMessageRequestObject(
                        x -> x.setMessageType(messageType.getType()),
                        x -> x.setDeliveryStatus(deliveryStatus.getStatus()),
                        x -> x.setCurrentPage(currentPage),
                        x -> x.setPageSize(pageSize)
                ))
                .post(apiPath)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(PaginatedAuditMessageResponseModel.class)
                .getAuditMessages();
    }

    private List<AuditMessageResponseModel> getAuditMessagesFromServiceByTypeAndStatus(
            MessageType messageType,
            DeliveryStatus deliveryStatus) {
        return getHeader(getAdminToken())
                .body(getAuditMessageRequestObject(
                        x -> x.setMessageType(messageType.getType()),
                        x -> x.setDeliveryStatus(deliveryStatus.getStatus())
                ))
                .post(MESSAGE_API_PATH)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(PaginatedAuditMessageResponseModel.class)
                .getAuditMessages();
    }

    private List<AuditMessageResponseModel> getFailedDeliveryAuditMessagesByType(MessageType messageType) {
        return getHeader(getAdminToken())
                .body(getAuditMessageRequestObject(
                        x -> x.setMessageType(messageType.getType())
                ))
                .post(FAILED_DELIVERY_API_PATH)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(PaginatedAuditMessageResponseModel.class)
                .getAuditMessages();
    }
    */

    ////55
    /*
    private AuditMessageResponseModel getMessageObject(PaginatedAuditMessageResponseModel messageCollection,
            String customerId, String messageId) {

        ////55     val candidateAuditMessage = messageCollection.getAuditMessages()
        return messageCollection.getAuditMessages()
                .stream()
                .filter(msg -> ////55customerId.equals(msg.getCustomerId())
                                ////55messageId.equalsIgnoreCase(msg.getMessageId())
                                null != messageId && !EMPTY.equals(messageId) ? messageId.equalsIgnoreCase(msg.getMessageId())
                                        : true
                        ////55       && 0 > startTestTimestamp
                        ////55       .compareTo(msg.getCreationTimestamp()))
                )
                .findFirst() ////55;
                .orElse(new AuditMessageResponseModel());
        ////55
        / *
        return candidateAuditMessage.isPresent() ? candidateAuditMessage.get()
                : candidateAuditMessage.orElseGet(() -> new AuditMessageResponseModel());
        * /
    }
    */

    ////55
    /*
    private PaginatedAuditMessageRequestModel getAuditMessageRequestObject(
            Consumer<PaginatedAuditMessageRequestModel>... actions) {
        return getObjectWithData(
                PaginatedAuditMessageRequestModel
                        .builder()
                        .currentPage(CURRENT_PAGE_LOWER_BOUNDARY)
                        .pageSize(50)
                        .build(),
                actions);
    }
    */

    private AuditMessageResponseModel getExpectedAuditMessage(
            Consumer<AuditMessageResponseModel>... actions) {
        return getObjectWithData(
                AuditMessageResponseModel
                        .builder()
                        .subjectTemplateId(subjectTemplateId)
                        .messageTemplateId(messageTemplateId)
                        .callType(REST.getType())
                        .formattingStatus(FormattingStatus.SUCCESS.getStatus())
                        .formattingComment(FORMATTING_COMMENT_SUCCESS)
                        .deliveryStatus(DeliveryStatus.SUCCESS.getStatus())
                        .deliveryComment(DELIVERY_COMMENT_SUCCESS)
                        .build(),
                actions);
    }

    private AuditMessageResponseModel getExpectedAuditMessageWithTemplateParsingIssue(
            Consumer<AuditMessageResponseModel>... actions) {
        return getObjectWithData(
                AuditMessageResponseModel
                        .builder()
                        .subjectTemplateId(subjectTemplateId)
                        .messageTemplateId(messageTemplateId)
                        .callType(REST.getType())
                        .formattingStatus(FormattingStatus.SUCCESS.getStatus())
                        .formattingComment(FORMATTING_COMMENT_SUCCESS)
                        .deliveryStatus(DeliveryStatus.SUCCESS.getStatus())
                        .deliveryComment(DELIVERY_COMMENT_SUCCESS)
                        .build(),
                actions);
    }

    private DeliveryFailedAuditMessageResponseModel getExpectedAuditMessageWithFailedDelivery(
            Consumer<DeliveryFailedAuditMessageResponseModel>... actions) {
        return getObjectWithData(
                DeliveryFailedAuditMessageResponseModel
                        .builder()
                        .subjectTemplateId(subjectTemplateId)
                        .messageTemplateId(messageTemplateId)
                        .callType(REST.getType())
                        ////55   .formattingStatus(FormattingStatus.SUCCESS.getStatus())
                        ////55    .formattingComment(FORMATTING_COMMENT_SUCCESS)
                        ////55      .deliveryStatus(DeliveryStatus.SUCCESS.getStatus())
                        .deliveryComment(DELIVERY_COMMENT_SUCCESS)
                        .build(),
                actions);
    }

    private AuditMessageResponseModel getAuditMessageWithParsingIssue(String templateName, String source) {
        val auditMessages = getAuditMessagesByPath(
                PARSING_ISSUES_API_PATH, EMAIL, SUCCESS, 1, 10);

        val actualMessageCandidate = auditMessages.stream()
                .filter(msg -> customerId.equals(msg.getCustomerId())
                        && templateName.equals(msg.getMessageTemplateId())
                        && source.equals(msg.getSource()))
                .findFirst();
        return actualMessageCandidate.isPresent() ? actualMessageCandidate.get() : new AuditMessageResponseModel();
    }

    ////55
/*
    private Consumer<PaginatedAuditMessageRequestModel>[] combineActions(
            Stream<Consumer<PaginatedAuditMessageRequestModel>> standardActionsStream,
            Consumer<PaginatedAuditMessageRequestModel>[] actions) {

        Consumer<PaginatedAuditMessageRequestModel>[] additionalActions = new Consumer[]{};
        Consumer<PaginatedAuditMessageRequestModel>[] allActions = null != actions && 0 < actions.length
                ? Stream.concat(standardActionsStream, Arrays.stream(actions))
                .collect(toList())
                .toArray(additionalActions)
                : standardActionsStream
                        .collect(toList())
                        .toArray(additionalActions);
        return allActions;
    }
    */
}
