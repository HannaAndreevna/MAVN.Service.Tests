package com.lykke.tests.api.service.notificationsystem;

import static com.lykke.api.testing.api.common.GenerateUtils.generateRandomEmail;
import static com.lykke.api.testing.api.common.GenerateUtils.generateRandomString;
import static com.lykke.api.testing.api.common.GenerateUtils.getRandomUuid;
import static com.lykke.tests.api.common.CommonConsts.IS_GUID;
import static com.lykke.tests.api.service.customermanagement.RegisterCustomerUtils.registerCustomer;
import static com.lykke.tests.api.service.customerprofile.CustomerProfileUtils.createCustomerProfileWithPhoneNumber;
import static com.lykke.tests.api.service.notificationsystem.NotificationMessageUtils.sendNotificationEmailMessage;
import static com.lykke.tests.api.service.notificationsystem.NotificationMessageUtils.sendNotificationEmailMessageAsMap;
import static com.lykke.tests.api.service.notificationsystem.NotificationMessageUtils.sendNotificationPushMessage;
import static com.lykke.tests.api.service.notificationsystem.NotificationMessageUtils.sendNotificationPushMessageAsMap;
import static com.lykke.tests.api.service.notificationsystem.NotificationMessageUtils.sendNotificationSms;
import static com.lykke.tests.api.service.notificationsystem.NotificationMessageUtils.sendNotificationSmsMessage;
import static com.lykke.tests.api.service.notificationsystem.NotificationMessageUtils.sendNotificationSmsWithTemplateParametersAsMap;
import static com.lykke.tests.api.service.notificationsystem.model.ResponseStatus.ERROR;
import static com.lykke.tests.api.service.notificationsystem.model.ResponseStatus.SUCCESS;
import static com.lykke.tests.api.service.notificationsystembroker.MessagesUtils.getTemplateParametersObject;
import static com.lykke.tests.api.service.notificationsystembroker.MessagesUtils.getTemplateParametersObjectCaseInsensitive;
import static com.lykke.tests.api.service.pushnotifications.PushNotificationsUtils.postPushRegistrations;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.apache.http.HttpStatus.SC_OK;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.params.provider.Arguments.of;

import com.lykke.api.testing.annotations.UserStoryId;
import com.lykke.api.testing.api.common.FakerUtils;
import com.lykke.tests.api.base.BaseApiTest;
import com.lykke.tests.api.common.enums.LoginProviders;
import com.lykke.tests.api.service.notificationsystem.model.MessageResponseModel;
import com.lykke.tests.api.service.notificationsystem.model.NotificationSystemValidationErrorResponseModel;
import com.lykke.tests.api.service.notificationsystem.model.ResponseStatus;
import com.lykke.tests.api.service.pushnotifications.model.CreatePushRegistrationRequestModel;
import java.time.Instant;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;
import lombok.val;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

public class NotificationSystemTests extends BaseApiTest {

    private static final String DEFAULT_RANDOM_EMAIL_ADDRESS = generateRandomEmail();
    private static final String DEFAULT_RANDOM_CUSTOMER_ID = createCustomerProfileWithPhoneNumber(
            DEFAULT_RANDOM_EMAIL_ADDRESS);
    private static final String subjectTemplateId = "123ctclg"; //TODO: should be created new
    private static final String messageTemplateId = "123"; // can user the same id with subjectTemplateId
    private static final String source = "6996";
    private static final String firstName = FakerUtils.firstName;
    private static final String lastName = FakerUtils.lastName;
    private static final String customerLogin = firstName.toLowerCase() + "." + lastName.toLowerCase();
    private static final String registrationDate = Instant.now().toString();
    private static final String NAME_FIELD = "name";
    private static final String CUSTOMER_ID_REQUIRED_ERROR_MSG = "Customer id is required";
    private static final String MESSAGE_TEMPLATE_REQUIRED_ERROR_MSG = "Message template id is required";
    private static final String SOURCE_REQUIRED_ERROR_MSG = "Source is required";
    private static final String SUBJECT_TEMPLATE_REQUIRED_ERROR_MSG = "Subject template id is required";
    private static final String COULD_NOT_FIND_CUSTOMER_EMAIL_ERROR_MSG = "Could not find customer's email";
    private static final String COULD_NOT_FIND_CUSTOMER_ID_ERROR_MSG = "Could not find customer id";
    private static final String COULD_NOT_FIND_SUBJECT_TEMPLATE_ERROR_MSG = "Could not find subject template";
    private static final String COULD_NOT_FIND_MESSAGE_TEMPLATE_ERROR_MSG = "Could not find message template";
    private static final String SUBJECT_TEMPLATE_ID_IS_REQUIRED_ERROR_MSG = "Subject template id is required";
    private static final String THE_INPUT_WAS_NOT_VALID_ERROR_MSG = "The input was not valid.";
    private static final String MESSAGE_TEMPLATE_ID_IS_NOT_SET_ERROR_MSG = "Message template id is not set";
    private static final String COULD_NOT_FIND_ANY_CUSTOMER_PUSH_REGISTRATION_IDS = "Could not find any customer push registration ids";
    private static final int CASE_SENSITIVE = 1;
    private static final int CASE_INSENSITIVE = 0;
    private static final String NONE_ERR_CODE = "None";
    private String loginProviderStandard = LoginProviders.STANDARD.getValue();
    private String loginProviderGoogle = LoginProviders.GOOGLE.getValue();

    private static Stream<Arguments> notificationMessage_InvalidParameters() {
        val templateParameters = templateParametersObjectCaseInsensitive();
        return Stream.of(
                of(null, subjectTemplateId, messageTemplateId, templateParameters, source, "CustomerId[0]",
                        CUSTOMER_ID_REQUIRED_ERROR_MSG),
                of(EMPTY, subjectTemplateId, messageTemplateId, templateParameters, source, "CustomerId[0]",
                        CUSTOMER_ID_REQUIRED_ERROR_MSG),
                of(DEFAULT_RANDOM_CUSTOMER_ID, null, messageTemplateId, templateParameters, source,
                        "SubjectTemplateId[0]",
                        SUBJECT_TEMPLATE_REQUIRED_ERROR_MSG),
                of(DEFAULT_RANDOM_CUSTOMER_ID, EMPTY, messageTemplateId, templateParameters, source,
                        "SubjectTemplateId[0]",
                        SUBJECT_TEMPLATE_REQUIRED_ERROR_MSG),
                of(DEFAULT_RANDOM_CUSTOMER_ID, subjectTemplateId, null, templateParameters, source,
                        "MessageTemplateId[0]",
                        MESSAGE_TEMPLATE_REQUIRED_ERROR_MSG),
                of(DEFAULT_RANDOM_CUSTOMER_ID, subjectTemplateId, EMPTY, templateParameters, source,
                        "MessageTemplateId[0]",
                        MESSAGE_TEMPLATE_REQUIRED_ERROR_MSG),
                of(DEFAULT_RANDOM_CUSTOMER_ID, subjectTemplateId, messageTemplateId, templateParameters, null,
                        "Source[0]",
                        SOURCE_REQUIRED_ERROR_MSG),
                of(DEFAULT_RANDOM_CUSTOMER_ID, subjectTemplateId, messageTemplateId, templateParameters, EMPTY,
                        "Source[0]",
                        SOURCE_REQUIRED_ERROR_MSG)
        );
    }

    private static Stream<Arguments> notificationSms_InvalidParameters() {
        val templateParameters = templateParametersObject();
        return Stream.of(
                of(null, messageTemplateId, templateParameters, source, "CustomerId[0]",
                        CUSTOMER_ID_REQUIRED_ERROR_MSG),
                of(EMPTY, messageTemplateId, templateParameters, source, "CustomerId[0]",
                        CUSTOMER_ID_REQUIRED_ERROR_MSG),
                of(DEFAULT_RANDOM_CUSTOMER_ID, null, templateParameters, source, "MessageTemplateId[0]",
                        MESSAGE_TEMPLATE_REQUIRED_ERROR_MSG),
                of(DEFAULT_RANDOM_CUSTOMER_ID, EMPTY, templateParameters, source, "MessageTemplateId[0]",
                        MESSAGE_TEMPLATE_REQUIRED_ERROR_MSG),
                of(DEFAULT_RANDOM_CUSTOMER_ID, messageTemplateId, templateParameters, null, "Source[0]",
                        SOURCE_REQUIRED_ERROR_MSG),
                of(DEFAULT_RANDOM_CUSTOMER_ID, messageTemplateId, templateParameters, EMPTY, "Source[0]",
                        SOURCE_REQUIRED_ERROR_MSG)
        );
    }

    private static Map<String, String> templateParametersObject() {

        return getTemplateParametersObject(DEFAULT_RANDOM_EMAIL_ADDRESS, firstName, lastName, customerLogin,
                registrationDate);
    }

    private static Map<String, String> templateParametersObjectCaseInsensitive() {

        return getTemplateParametersObjectCaseInsensitive(DEFAULT_RANDOM_EMAIL_ADDRESS, firstName, lastName,
                customerLogin,
                registrationDate);
    }

    static Stream<Arguments> getInvalidEmailMessagesForStandardResponse() {
        return Stream.of(
                of(getRandomUuid(), subjectTemplateId, messageTemplateId, source,
                        (Function<NotificationSystemValidationErrorResponseModel, String>) (NotificationSystemValidationErrorResponseModel x) -> x
                                .getCustomerId()[0],
                        COULD_NOT_FIND_CUSTOMER_EMAIL_ERROR_MSG),
                of(DEFAULT_RANDOM_CUSTOMER_ID, generateRandomString(10), messageTemplateId, source,
                        (Function<NotificationSystemValidationErrorResponseModel, String>) (NotificationSystemValidationErrorResponseModel x) -> x
                                .getSubjectTemplateId()[0],
                        COULD_NOT_FIND_SUBJECT_TEMPLATE_ERROR_MSG),
                of(DEFAULT_RANDOM_CUSTOMER_ID, messageTemplateId, generateRandomString(10), source,
                        (Function<NotificationSystemValidationErrorResponseModel, String>) (NotificationSystemValidationErrorResponseModel x) -> x
                                .getMessageTemplateId()[0],
                        COULD_NOT_FIND_MESSAGE_TEMPLATE_ERROR_MSG)
        );
    }

    static Stream<Arguments> getInvalidEmailMessagesForUnifiedResponse() {
        return Stream.of(
                of(DEFAULT_RANDOM_CUSTOMER_ID, EMPTY, messageTemplateId, source,
                        (Function<NotificationSystemValidationErrorResponseModel, String>) (NotificationSystemValidationErrorResponseModel x) -> x
                                .getSubjectTemplateId()[0], SUBJECT_TEMPLATE_ID_IS_REQUIRED_ERROR_MSG),
                of(DEFAULT_RANDOM_CUSTOMER_ID, messageTemplateId, EMPTY, source,
                        (Function<NotificationSystemValidationErrorResponseModel, String>) (NotificationSystemValidationErrorResponseModel x) -> x
                                .getMessageTemplateId()[0], MESSAGE_TEMPLATE_REQUIRED_ERROR_MSG),
                of(DEFAULT_RANDOM_CUSTOMER_ID, messageTemplateId, null, source,
                        (Function<NotificationSystemValidationErrorResponseModel, String>) (NotificationSystemValidationErrorResponseModel x) -> x
                                .getMessageTemplateId()[0], MESSAGE_TEMPLATE_REQUIRED_ERROR_MSG),
                of(null, subjectTemplateId, messageTemplateId, source,
                        (Function<NotificationSystemValidationErrorResponseModel, String>) (NotificationSystemValidationErrorResponseModel x) -> x
                                .getCustomerId()[0],
                        CUSTOMER_ID_REQUIRED_ERROR_MSG),
                of(DEFAULT_RANDOM_CUSTOMER_ID, null, messageTemplateId, source,
                        (Function<NotificationSystemValidationErrorResponseModel, String>) (NotificationSystemValidationErrorResponseModel x) -> x
                                .getSubjectTemplateId()[0],
                        SUBJECT_TEMPLATE_REQUIRED_ERROR_MSG),
                of(DEFAULT_RANDOM_CUSTOMER_ID, messageTemplateId, null, source,
                        (Function<NotificationSystemValidationErrorResponseModel, String>) (NotificationSystemValidationErrorResponseModel x) -> x
                                .getMessageTemplateId()[0],
                        MESSAGE_TEMPLATE_REQUIRED_ERROR_MSG)
        );
    }

    static Stream<Arguments> getInvalidEmailMessagesForUnifiedResponseWithPartiallyMissingInput() {
        return Stream.of(
                of(null, subjectTemplateId, messageTemplateId, source,
                        (Function<NotificationSystemValidationErrorResponseModel, String>) (NotificationSystemValidationErrorResponseModel x) -> x
                                .getCustomerId()[0],
                        CUSTOMER_ID_REQUIRED_ERROR_MSG),
                of(DEFAULT_RANDOM_CUSTOMER_ID, null, messageTemplateId, source,
                        (Function<NotificationSystemValidationErrorResponseModel, String>) (NotificationSystemValidationErrorResponseModel x) -> x
                                .getSubjectTemplateId()[0],
                        SUBJECT_TEMPLATE_REQUIRED_ERROR_MSG),
                of(DEFAULT_RANDOM_CUSTOMER_ID, messageTemplateId, null, source,
                        (Function<NotificationSystemValidationErrorResponseModel, String>) (NotificationSystemValidationErrorResponseModel x) -> x
                                .getMessageTemplateId()[0],
                        MESSAGE_TEMPLATE_REQUIRED_ERROR_MSG)
        );
    }

    static Stream<Arguments> getInvalidSmsMessagesForUnifiedResponse() {
        return Stream.of(
                of(DEFAULT_RANDOM_CUSTOMER_ID, EMPTY, source,
                        (Function<NotificationSystemValidationErrorResponseModel, String>) (NotificationSystemValidationErrorResponseModel x) -> x
                                .getMessageTemplateId()[0], MESSAGE_TEMPLATE_REQUIRED_ERROR_MSG),
                of(DEFAULT_RANDOM_CUSTOMER_ID, null, source,
                        (Function<NotificationSystemValidationErrorResponseModel, String>) (NotificationSystemValidationErrorResponseModel x) -> x
                                .getMessageTemplateId()[0], MESSAGE_TEMPLATE_REQUIRED_ERROR_MSG),
                of(null, messageTemplateId, source,
                        (Function<NotificationSystemValidationErrorResponseModel, String>) (NotificationSystemValidationErrorResponseModel x) -> x
                                .getCustomerId()[0],
                        CUSTOMER_ID_REQUIRED_ERROR_MSG),
                of(DEFAULT_RANDOM_CUSTOMER_ID, null, source,
                        (Function<NotificationSystemValidationErrorResponseModel, String>) (NotificationSystemValidationErrorResponseModel x) -> x
                                .getMessageTemplateId()[0],
                        MESSAGE_TEMPLATE_REQUIRED_ERROR_MSG)
        );
    }

    static Stream<Arguments> getInvalidSmsMessagesForUnifiedResponseWithPartiallyMissingInput() {
        return Stream.of(
                of(null, messageTemplateId, source,
                        (Function<NotificationSystemValidationErrorResponseModel, String>) (NotificationSystemValidationErrorResponseModel x) -> x
                                .getCustomerId()[0],
                        CUSTOMER_ID_REQUIRED_ERROR_MSG),
                of(DEFAULT_RANDOM_CUSTOMER_ID, null, source,
                        (Function<NotificationSystemValidationErrorResponseModel, String>) (NotificationSystemValidationErrorResponseModel x) -> x
                                .getMessageTemplateId()[0],
                        MESSAGE_TEMPLATE_REQUIRED_ERROR_MSG),
                of(getRandomUuid(), messageTemplateId, source,
                        (Function<NotificationSystemValidationErrorResponseModel, String>) (NotificationSystemValidationErrorResponseModel x) -> x
                                .getSubjectTemplateId()[0],
                        SUBJECT_TEMPLATE_ID_IS_REQUIRED_ERROR_MSG),
                of(DEFAULT_RANDOM_CUSTOMER_ID, generateRandomString(10), source,
                        (Function<NotificationSystemValidationErrorResponseModel, String>) (NotificationSystemValidationErrorResponseModel x) -> x
                                .getSubjectTemplateId()[0],
                        SUBJECT_TEMPLATE_ID_IS_REQUIRED_ERROR_MSG)
        );
    }

    static Stream<Arguments> getInvalidPushMessagesForStandardResponse() {
        return Stream.of(
                of(DEFAULT_RANDOM_CUSTOMER_ID, messageTemplateId, generateRandomString(10), source,
                        ERROR,
                        COULD_NOT_FIND_MESSAGE_TEMPLATE_ERROR_MSG),

                of(DEFAULT_RANDOM_CUSTOMER_ID, messageTemplateId, EMPTY, source,
                        ERROR, MESSAGE_TEMPLATE_ID_IS_NOT_SET_ERROR_MSG),
                of(DEFAULT_RANDOM_CUSTOMER_ID, messageTemplateId, null, source,
                        ERROR, MESSAGE_TEMPLATE_ID_IS_NOT_SET_ERROR_MSG),
                of(null, subjectTemplateId, messageTemplateId, source,
                        ERROR,
                        COULD_NOT_FIND_CUSTOMER_ID_ERROR_MSG),

                of(DEFAULT_RANDOM_CUSTOMER_ID, messageTemplateId, null, source,
                        ERROR,
                        MESSAGE_TEMPLATE_ID_IS_NOT_SET_ERROR_MSG)
        );
    }

    static Stream<Arguments> getValidPushMessagesForStandardResponse() {
        return Stream.of(
                of(registerCustomer(), generateRandomString(10),
                        messageTemplateId, source,
                        SUCCESS,
                        null),
                of(registerCustomer(), EMPTY, messageTemplateId, source,
                        SUCCESS,
                        null),
                of(registerCustomer(), null, messageTemplateId, source,
                        SUCCESS,
                        null)
        );
    }

    static Stream<Arguments> getInvalidPushMessagesForStandardResponseWithPartiallyMissingInput() {
        return Stream.of(
                of(getRandomUuid(), messageTemplateId, source,
                        (Function<NotificationSystemValidationErrorResponseModel, String>) (NotificationSystemValidationErrorResponseModel x) -> x
                                .getCustomPayload()[0],
                        COULD_NOT_FIND_ANY_CUSTOMER_PUSH_REGISTRATION_IDS),
                of(null, messageTemplateId, source,
                        (Function<NotificationSystemValidationErrorResponseModel, String>) (NotificationSystemValidationErrorResponseModel x) -> x
                                .getCustomPayload()[0],
                        COULD_NOT_FIND_CUSTOMER_ID_ERROR_MSG),
                of(DEFAULT_RANDOM_CUSTOMER_ID, null, source,
                        (Function<NotificationSystemValidationErrorResponseModel, String>) (NotificationSystemValidationErrorResponseModel x) -> x
                                .getMessageTemplateId()[0],
                        MESSAGE_TEMPLATE_ID_IS_NOT_SET_ERROR_MSG)
        );
    }

    @ParameterizedTest
    @ValueSource(ints = {CASE_SENSITIVE, CASE_INSENSITIVE})
    @UserStoryId(storyId = {528, 1259})
    void shouldSendEmailMessage(int sensitivity) {
        val templateParameters = CASE_SENSITIVE == sensitivity
                ? templateParametersObject()
                : templateParametersObjectCaseInsensitive();
        sendNotificationEmailMessage(DEFAULT_RANDOM_CUSTOMER_ID, subjectTemplateId, messageTemplateId,
                templateParameters, source)
                .then()
                .assertThat()
                .statusCode(SC_OK);
    }

    @Test
    @UserStoryId(1639)
    void shouldSendEmailMessageAndReturnIds() {
        val templateParameters = templateParametersObjectCaseInsensitive();
        val actualResult = sendNotificationEmailMessage(DEFAULT_RANDOM_CUSTOMER_ID, messageTemplateId,
                messageTemplateId,
                templateParameters, source)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(MessageResponseModel.class);

        assertAll(
                () -> assertEquals(SUCCESS, actualResult.getStatus()),
                () -> assertEquals(null, actualResult.getErrorDescription()),
                () -> assertEquals(1, actualResult.getMessageIds().length),
                () -> assertTrue(IS_GUID.apply(actualResult.getMessageIds()[0]))
        );
    }

    @Test
    @UserStoryId(1639)
    void shouldSendSmsMessageAndReturnIds() {
        val templateParameters = templateParametersObjectCaseInsensitive();
        val actualResult = sendNotificationSmsMessage(DEFAULT_RANDOM_CUSTOMER_ID, messageTemplateId,
                templateParameters, source)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(MessageResponseModel.class);

        assertAll(
                () -> assertEquals(SUCCESS, actualResult.getStatus()),
                () -> assertEquals(null, actualResult.getErrorDescription()),
                () -> assertEquals(1, actualResult.getMessageIds().length),
                () -> assertTrue(IS_GUID.apply(actualResult.getMessageIds()[0]))
        );
    }

    @UserStoryId(storyId = {528, 962, 1259})
    @ParameterizedTest(name =
            "Run {index}: customerId={0}, subjectTemplateId={1}, messageTemplateId={2}, templateParameters={3}, " +
                    "source={4}, field={5}, message={6}")
    @MethodSource("notificationMessage_InvalidParameters")
    void shouldNotSendEmailWhenRequiredFieldValueIsNullOrEmptyString(
            String customerId, String subjectTemplateId,
            String messageTemplateId, Map<String, String> templateParameters,
            String source, String field, String message) {
        sendNotificationEmailMessage(customerId, subjectTemplateId, messageTemplateId, templateParameters, source)
                .then()
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .body(field, CoreMatchers.equalTo(message));
    }

    @UserStoryId(storyId = {962})
    @ParameterizedTest(name =
            "Run {index}: customerId={0}, messageTemplateId={1}, templateParameters={2}, " +
                    "source={3}, field={4}, message={5}")
    @MethodSource("notificationSms_InvalidParameters")
    void shouldNotSendSmsWhenRequiredFieldValueIsNullOrEmptyString(String customerId,
            String messageTemplateId,
            Map<String, String> templateParameters, String source, String field, String message) {
        sendNotificationSms(customerId, messageTemplateId, templateParameters, source)
                .then()
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .body(field, CoreMatchers.equalTo(message));
    }

    @ParameterizedTest
    @MethodSource("getInvalidEmailMessagesForStandardResponse")
    @UserStoryId(1639)
    void shouldNotSendEmailMessageOnInvalidInputReturnsStandardResponse(
            String customerIdParam, String subjectTemplateIdParam, String messageTemplateIdParam, String sourceParam,
            Function<NotificationSystemValidationErrorResponseModel, String> errorAction,
            String errorMessage) {
        val templateParameters = templateParametersObjectCaseInsensitive();
        val actualResult = sendNotificationEmailMessage(customerIdParam, subjectTemplateIdParam, messageTemplateIdParam,
                templateParameters, sourceParam)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(MessageResponseModel.class);

        assertAll(
                () -> assertEquals(ERROR, actualResult.getStatus()),
                () -> assertEquals(errorMessage, actualResult.getErrorDescription()),
                () -> assertEquals(0, actualResult.getMessageIds().length)
        );
    }

    @ParameterizedTest
    @MethodSource("getInvalidEmailMessagesForUnifiedResponse")
    @UserStoryId(1639)
    void shouldNotSendEmailMessageOnInvalidInputReturnsUnifiedResponse(
            String customerIdParam,
            String subjectTemplateIdParam,
            String messageTemplateIdParam,
            String sourceParam,
            Function<NotificationSystemValidationErrorResponseModel, String> errorAction,
            String errorMessage) {
        val templateParameters = templateParametersObjectCaseInsensitive();
        val actualResult = sendNotificationEmailMessage(customerIdParam, subjectTemplateId////55Param
                , messageTemplateIdParam,
                templateParameters, sourceParam)
                .then()
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .extract()
                .as(NotificationSystemValidationErrorResponseModel.class);

        assertEquals(errorMessage, errorAction.apply(actualResult));
    }

    @ParameterizedTest
    @MethodSource("getInvalidEmailMessagesForUnifiedResponseWithPartiallyMissingInput")
    @UserStoryId(1639)
    void shouldNotSendEmailMessageOnPartiallyMissingInputReturnsUnifiedResponse(
            String customerIdParam,
            String subjectTemplateIdParam,
            String messageTemplateIdParam,
            String sourceParam,
            Function<NotificationSystemValidationErrorResponseModel, String> errorAction,
            String errorMessage) {
        val templateParameters = templateParametersObjectCaseInsensitive();
        val actualResult = sendNotificationEmailMessageAsMap(customerIdParam, subjectTemplateId////55Param
                ,
                messageTemplateIdParam,
                templateParameters, sourceParam)
                .then()
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .extract()
                .as(NotificationSystemValidationErrorResponseModel.class);

        assertEquals(errorMessage, errorAction.apply(actualResult));
    }

    @ParameterizedTest
    @MethodSource("getInvalidSmsMessagesForUnifiedResponse")
    @UserStoryId(1639)
    void shouldNotSendSmsMessageOnInvalidInputReturnsUnifiedResponse(
            String customerIdParam,
            String messageTemplateIdParam,
            String sourceParam,
            Function<NotificationSystemValidationErrorResponseModel, String> errorAction,
            String errorMessage) {
        val templateParameters = templateParametersObjectCaseInsensitive();
        val actualResult = sendNotificationSms(customerIdParam, messageTemplateIdParam,
                templateParameters, sourceParam)
                .then()
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .extract()
                .as(NotificationSystemValidationErrorResponseModel.class);

        assertEquals(errorMessage, errorAction.apply(actualResult));
    }

    @ParameterizedTest
    @MethodSource("getInvalidSmsMessagesForUnifiedResponseWithPartiallyMissingInput")
    @UserStoryId(1639)
    void shouldNotSendSmsMessageOnPartiallyMissingInputReturnsUnifiedResponse(
            String customerIdParam,
            String messageTemplateIdParam,
            String sourceParam,
            Function<NotificationSystemValidationErrorResponseModel, String> errorAction,
            String errorMessage) {
        val templateParameters = templateParametersObjectCaseInsensitive();
        val actualResult = sendNotificationSmsWithTemplateParametersAsMap(customerIdParam,
                messageTemplateIdParam,
                templateParameters, sourceParam)
                .then()
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .extract()
                .as(NotificationSystemValidationErrorResponseModel.class);

        assertEquals(errorMessage, errorAction.apply(actualResult));
    }

    @ParameterizedTest
    @MethodSource("getInvalidPushMessagesForStandardResponse")
    @UserStoryId(1639)
    void shouldNotSendPushMessageOnInvalidInputReturnsStandardResponse(
            String customerIdParam, String subjectTemplateIdParam, String messageTemplateIdParam, String sourceParam,
            ResponseStatus status,
            String errorMessage) {
        val templateParameters = templateParametersObjectCaseInsensitive();
        val actualResult = sendNotificationPushMessage(customerIdParam,
                messageTemplateIdParam,
                templateParameters, templateParameters, sourceParam)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(MessageResponseModel.class);

        assertAll(
                () -> assertEquals(status, actualResult.getStatus()),
                () -> assertEquals(errorMessage, actualResult.getErrorDescription()),
                () -> assertEquals(0, actualResult.getMessageIds().length)
        );
    }

    @ParameterizedTest
    @MethodSource("getValidPushMessagesForStandardResponse")
    @UserStoryId(1639)
    void shouldSendPushMessageOnInvalidInputReturnsStandardResponse(
            String customerIdParam, String subjectTemplateIdParam, String messageTemplateIdParam, String sourceParam,
            ResponseStatus status,
            String errorMessage) {
        postPushRegistrations(CreatePushRegistrationRequestModel
                .builder()
                .customerId(customerIdParam)
                .infobipToken(generateRandomString(20))
                .firebaseToken(generateRandomString(20))
                .appleToken(generateRandomString(20))
                .build())
                .then()
                .assertThat()
                .statusCode(SC_OK);
        val templateParameters = templateParametersObjectCaseInsensitive();
        val actualResult = sendNotificationPushMessage(customerIdParam,
                messageTemplateIdParam,
                templateParameters, templateParameters, sourceParam)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(MessageResponseModel.class);

        assertAll(
                () -> assertEquals(status, actualResult.getStatus()),
                () -> assertEquals(errorMessage, actualResult.getErrorDescription()),
                () -> assertEquals(1, actualResult.getMessageIds().length)
        );
    }

    @ParameterizedTest
    @MethodSource("getValidPushMessagesForStandardResponse")
    @UserStoryId(storyId = {1639, 1944})
    void shouldSendPushMessageIfCustomerIsNotEnabledToSendPushNotificaitons(
            String customerIdParam, String subjectTemplateIdParam, String messageTemplateIdParam, String sourceParam,
            ResponseStatus status,
            String errorMessage) {

        postPushRegistrations(CreatePushRegistrationRequestModel
                .builder()
                .customerId(customerIdParam)
                .infobipToken(generateRandomString(20))
                .firebaseToken(generateRandomString(20))
                .appleToken(generateRandomString(20))
                .build());

        val templateParameters = templateParametersObjectCaseInsensitive();
        val actualResult = sendNotificationPushMessage(customerIdParam,
                messageTemplateIdParam,
                templateParameters, templateParameters, sourceParam)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(MessageResponseModel.class);

        assertAll(
                () -> assertEquals(status, actualResult.getStatus()),
                () -> assertEquals(errorMessage, actualResult.getErrorDescription()),
                () -> assertEquals(1, actualResult.getMessageIds().length)
        );
    }

    @ParameterizedTest
    @MethodSource("getInvalidPushMessagesForStandardResponseWithPartiallyMissingInput")
    @UserStoryId(1639)
    void shouldNotSendPushMessageOnPartiallyMissingInputReturnsUnifiedResponse(
            String customerIdParam,
            String messageTemplateIdParam,
            String sourceParam,
            Function<NotificationSystemValidationErrorResponseModel, String> errorAction,
            String errorMessage) {
        val templateParameters = templateParametersObjectCaseInsensitive();
        val actualResult = sendNotificationPushMessageAsMap(customerIdParam,
                messageTemplateIdParam,
                templateParameters, templateParameters, sourceParam)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(MessageResponseModel.class);

        assertAll(
                () -> assertEquals(ERROR, actualResult.getStatus()),
                () -> assertEquals(errorMessage, actualResult.getErrorDescription()),
                () -> assertEquals(0, actualResult.getMessageIds().length)
        );
    }
}
