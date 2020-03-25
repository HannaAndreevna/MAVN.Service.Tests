package com.lykke.tests.api.service.notificationsystembroker;

import static com.lykke.api.testing.api.common.GenerateUtils.generateRandomEmail;
import static com.lykke.api.testing.api.common.GenerateUtils.generateRandomString;
import static com.lykke.api.testing.api.common.PasswordGen.generateValidPassword;
import static com.lykke.tests.api.common.CommonConsts.AWAITILITY_WAITING_FOR_EMAIL_MESSAGE_SEC;
import static com.lykke.tests.api.common.CommonConsts.SMOKE_TEST;
import static com.lykke.tests.api.service.customermanagement.RegisterCustomerUtils.registerCustomer;
import static com.lykke.tests.api.service.notificationsystem.NotificationMessageUtils.sendNotificationEmailMessage;
import static com.lykke.tests.api.service.notificationsystem.templates.TemplateUtils.createTemplate;
import static com.lykke.tests.api.service.notificationsystem.templates.TemplateUtils.deleteTemplateByName;
import static com.lykke.tests.api.service.notificationsystembroker.MessagesUtils.getEmailMessages;
import static com.lykke.tests.api.service.notificationsystembroker.MessagesUtils.getTemplateParametersObject;
import static org.apache.http.HttpStatus.SC_NO_CONTENT;
import static org.apache.http.HttpStatus.SC_OK;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.lykke.api.testing.annotations.UserStoryId;
import com.lykke.api.testing.api.common.FakerUtils;
import com.lykke.tests.api.base.BaseApiTest;
import com.lykke.tests.api.service.customermanagement.model.register.RegistrationRequestModel;
import com.lykke.tests.api.service.notificationsystem.model.MessageResponseModel;
import com.lykke.tests.api.service.notificationsystem.templates.model.NewTemplateRequest;
import com.lykke.tests.api.service.notificationsystembroker.model.EmailMessageResponseModel;
import java.time.Instant;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import lombok.val;
import lombok.var;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

public class EmailMessagesTests extends BaseApiTest {

    private static final String source = "6996";
    private static final String emailAddress = generateRandomEmail();
    private static final String password = generateValidPassword();
    private static final String firstName = FakerUtils.firstName;
    private static final String lastName = FakerUtils.lastName;
    private static final String customerLogin = firstName.toLowerCase() + "." + lastName.toLowerCase();
    private static final String registrationDate = Instant.now().toString();
    private static final String NAME_FIELD = "name";
    private static String messageTemplateName;
    private static String subjectTemplateName;
    private static String messageTemplateBody;
    private static String subjectTemplateBody;
    private static String localizationCode;
    private static NewTemplateRequest messageTemplate;
    private static NewTemplateRequest subjectTemplate;

    private static Map<String, String> templateParametersObject() {

        return getTemplateParametersObject(emailAddress, firstName, lastName, customerLogin, registrationDate);
    }

    @BeforeEach
    void setup() {
        messageTemplateName = generateRandomString().toLowerCase();
        subjectTemplateName = generateRandomString().toLowerCase();
        messageTemplateBody = FakerUtils.randomQuote;
        subjectTemplateBody = FakerUtils.randomQuote;
        localizationCode = "en"; // only "en" is implemented for now

        messageTemplate = NewTemplateRequest
                .builder()
                .templateName(messageTemplateName)
                .templateBody(messageTemplateBody)
                .localizationCode(localizationCode)
                .build();

        subjectTemplate = NewTemplateRequest
                .builder()
                .templateName(subjectTemplateName)
                .templateBody(subjectTemplateBody)
                .localizationCode(localizationCode)
                .build();

        createTemplate(messageTemplate)
                .then()
                .assertThat()
                .statusCode(SC_NO_CONTENT);

        createTemplate(subjectTemplate)
                .then()
                .assertThat()
                .statusCode(SC_NO_CONTENT);
    }

    @AfterEach
    void cleanup() {
        deleteTemplateByName(messageTemplateName)
                .then()
                .assertThat()
                .statusCode(SC_NO_CONTENT);

        deleteTemplateByName(subjectTemplateName)
                .then()
                .assertThat()
                .statusCode(SC_NO_CONTENT);
    }

    @Test
    @Tag(SMOKE_TEST)
    @UserStoryId(storyId = 636)
    void getEmailMessagesByEmail() {
        val templateParameters = templateParametersObject();
        var customer = new RegistrationRequestModel();
        customer.setEmail(emailAddress);
        customer.setPassword(password);
        String customerId = registerCustomer(customer);

        // TODO: now it works only if send a message twice
        sendNotificationEmailMessage(customerId, subjectTemplateName, messageTemplateName,
                templateParameters, source)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(MessageResponseModel.class)
                .getMessageIds();

        val messageId = sendNotificationEmailMessage(customerId, subjectTemplateName, messageTemplateName,
                templateParameters, source)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(MessageResponseModel.class)
                .getMessageIds()[0];

        Awaitility.await()
                .atMost(AWAITILITY_WAITING_FOR_EMAIL_MESSAGE_SEC * 2, TimeUnit.SECONDS)
                .pollInterval(500, TimeUnit.MILLISECONDS)
                .until(() -> 1 < getEmailMessages(emailAddress)
                        .then()
                        .assertThat()
                        .statusCode(SC_OK)
                        .extract()
                        .as(EmailMessageResponseModel[].class).length);

        val emailMessages = getEmailMessages(emailAddress)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(EmailMessageResponseModel[].class);

        val actualEmailMessage = Arrays.stream(emailMessages)
                .filter(msg -> messageId.equalsIgnoreCase(msg.getMessageId().toString()))
                .findFirst()
                .orElse(new EmailMessageResponseModel());

        assertAll(
                () -> assertEquals(subjectTemplateBody, actualEmailMessage.getSubject()),
                () -> assertEquals(messageTemplateBody, actualEmailMessage.getBody()),
                () -> assertEquals(emailAddress, actualEmailMessage.getEmail()),
                // obsolete?
                () -> assertEquals(Date.from(Instant.now()).getDate(), actualEmailMessage.getTimestamp().getDate())
        );
    }
}
