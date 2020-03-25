package com.lykke.tests.api.service.notificationsystem.templates;

import static com.lykke.api.testing.api.common.GenerateUtils.generateRandomString;
import static com.lykke.tests.api.common.CommonConsts.SMOKE_TEST;
import static com.lykke.tests.api.service.customermanagement.RegisterCustomerUtils.registerCustomer;
import static com.lykke.tests.api.service.notificationsystem.NotificationMessageUtils.sendNotificationPushMessage;
import static com.lykke.tests.api.service.notificationsystem.templates.TemplateUtils.createTemplate;
import static com.lykke.tests.api.service.notificationsystem.templates.TemplateUtils.createTemplateWithNameAndLocalizationCode;
import static com.lykke.tests.api.service.notificationsystem.templates.TemplateUtils.createTemplateWithNamespaceAndCustomParameters;
import static com.lykke.tests.api.service.notificationsystem.templates.TemplateUtils.deleteTemplateByName;
import static com.lykke.tests.api.service.notificationsystem.templates.TemplateUtils.deleteTemplateLanguage;
import static com.lykke.tests.api.service.notificationsystem.templates.TemplateUtils.getTemplateWithLanguage;
import static com.lykke.tests.api.service.notificationsystem.templates.TemplateUtils.getTemplates;
import static com.lykke.tests.api.service.notificationsystem.templates.TemplateUtils.updateTemplate;
import static com.lykke.tests.api.service.pushnotifications.PushNotificationsUtils.postPushRegistrations;
import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.apache.http.HttpStatus.SC_NO_CONTENT;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.of;

import com.lykke.api.testing.annotations.UserStoryId;
import com.lykke.tests.api.base.BaseApiTest;
import com.lykke.tests.api.service.notificationsystem.model.MessageResponseModel;
import com.lykke.tests.api.service.notificationsystem.model.ResponseStatus;
import com.lykke.tests.api.service.notificationsystem.templates.model.NewTemplateRequest;
import com.lykke.tests.api.service.notificationsystem.templates.model.TemplateByLanguageResponse;
import com.lykke.tests.api.service.notificationsystem.templates.model.TemplateResponse;
import com.lykke.tests.api.service.pushnotifications.model.CreatePushRegistrationRequestModel;
import io.restassured.response.Response;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;
import lombok.val;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class TemplatesTests extends BaseApiTest {

    private static final String TEMPLATE_NAME_FIELD = "TemplateName";
    private static final String TEMPLATE_FIELD = "Template";
    private static final String TEMPLATE_BODY_FIELD = "TemplateBody";
    private static final String AVAILABLE_LOCALIZATION_FIELD = "AvailableLocalizations.LocalizationCode";
    private static final String LOCALIZATION_FIELD = "LocalizationCode.LocalizationCode";
    private static final String TEMPLATE_NAME_CHARACTER_TYPE_ERR_MSG = "Template Name can only contain lowercase alphanumeric characters and hyphen (except as the first or the last character)";
    private static final String TEMPLATE_NAME_CHARACTER_MIN_LENGTH_ERR_MSG = "Template Name cannot be less then 3 characters in length";
    private static final String TEMPLATE_NAME_CHARACTER_MAX_LENGTH_ERR_MSG = "Template Name cannot be more then 63 characters in length";
    private static final String TEMPLATE_EXISTS_ERR_MSG = "Template with this localization already exist";
    private static final String TEXT_BEGINNING = "Text beginning_";
    private static final String TEXT_IN_THE_MIDDLE = "_in_the_middle_";
    private static final String TEXT_ENDING = "_text ending";
    private static final String TEMPLATE_SECTION = "@@@\n{\n"
            + "\"param003\":\"zzz\",\n"
            + "\"Param004\":\"abc\""
            + "\n}\n@@@\n";
    private static final String TEXT_BEFORE_TEMPLATE_SECTION = TEXT_BEGINNING + TEMPLATE_SECTION + TEXT_ENDING;
    private static final String TWO_TEMPLATE_SECTIONS = TEMPLATE_SECTION + TEXT_IN_THE_MIDDLE + TEMPLATE_SECTION;
    private static String templateName;
    private static String templateBody;
    private static String localizationCode;
    private static NewTemplateRequest template;

    static Stream<Arguments> getInvalidTemplateWithCustomParameters() {
        return Stream.of(
                of(TEXT_BEFORE_TEMPLATE_SECTION),
                of(TWO_TEMPLATE_SECTIONS)
        );
    }

    private static void createTemplateSuccessfully(NewTemplateRequest template) {
        createTemplate(template)
                .then()
                .assertThat()
                .statusCode(SC_NO_CONTENT);
    }

    private static Stream<Arguments> templateInvalidNames() {
        return Stream.of(
                of(generateRandomString(3) + " " + generateRandomString(),
                        TEMPLATE_NAME_CHARACTER_TYPE_ERR_MSG),
                of(generateRandomString(2), TEMPLATE_NAME_CHARACTER_MIN_LENGTH_ERR_MSG),
                of(generateRandomString(64), TEMPLATE_NAME_CHARACTER_MAX_LENGTH_ERR_MSG),
                of("-" + generateRandomString(5), TEMPLATE_NAME_CHARACTER_TYPE_ERR_MSG),
                of(generateRandomString(5) + "-",
                        TEMPLATE_NAME_CHARACTER_TYPE_ERR_MSG),
                of(generateRandomString(3) + "--" + generateRandomString(3),
                        TEMPLATE_NAME_CHARACTER_TYPE_ERR_MSG)
        );
    }

    private static Stream<Arguments> templateNames() {
        return Stream.of(
                of(generateRandomString(3)),
                of(generateRandomString(63)),
                of(generateRandomString(5) + "-" + generateRandomString(5)),
                of(generateRandomString(5) + "-" + "123"),
                of("123" + generateRandomString(5))
        );
    }

    private static String getTemplateField(String fieldName) {
        return TEMPLATE_FIELD + "." + fieldName;
    }

    private static void checkTemplateWithLocalizationExists(String templateName, String localizationCode) {
        val template = getTemplateWithLanguage(templateName, localizationCode)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(TemplateByLanguageResponse.class);
        Assertions.assertAll(
                () -> Assertions.assertEquals(templateName, template.getTemplate().getTemplateName()),
                () -> Assertions.assertEquals(localizationCode,
                        template.getTemplate().getLocalizationCode().getLocalizationCode())
        );
    }

    @BeforeEach
    void setUpMethod() {
        templateName = generateRandomString().toLowerCase();
        templateBody = generateRandomString();
        localizationCode = generateRandomString().toLowerCase();

        template = NewTemplateRequest
                .builder()
                .templateName(templateName)
                .templateBody(templateBody)
                .localizationCode(localizationCode)
                .build();
    }

    @AfterEach
    void templateCleanup() {
        Response templates = getTemplates();
        templates
                .then()
                .assertThat()
                .statusCode(SC_OK);

        List<String> listOfTemplates = templates
                .jsonPath()
                .getList(TEMPLATE_NAME_FIELD);

        if (listOfTemplates.contains(templateName)) {
            deleteTemplateByName(templateName)
                    .then()
                    .assertThat()
                    .statusCode(SC_NO_CONTENT);
        }
    }

    @Test
    @Tag(SMOKE_TEST)
    @UserStoryId(storyId = 526)
    void shouldReturnAllTemplates() {
        createTemplateSuccessfully(template);

        getTemplates()
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .body(TEMPLATE_NAME_FIELD, hasSize(greaterThanOrEqualTo(1)))
                .body(AVAILABLE_LOCALIZATION_FIELD, hasSize(greaterThanOrEqualTo(1)));
    }

    @Test
    @UserStoryId(storyId = 526)
    void shouldCreateTemplate() {
        createTemplateSuccessfully(template);
        getTemplates()
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .body(TEMPLATE_NAME_FIELD, hasItem(templateName));
    }

    @Test
    @UserStoryId(1640)
    void shouldCreateTemplateWithMessageParameters() {
        createTemplateSuccessfully(template);
        getTemplates()
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .body(TEMPLATE_NAME_FIELD, hasItem(templateName));
    }

    @ParameterizedTest
    @MethodSource("getInvalidTemplateWithCustomParameters")
    @UserStoryId(1874)
    void shouldCreateTemplateWithInvalidContent(String tempalte) {

        val templateName = generateRandomString(10).toLowerCase();

        createTemplateWithNamespaceAndCustomParameters(templateName, generateRandomString(), tempalte)
                .then()
                .assertThat()
                .statusCode(SC_NO_CONTENT);

        val actualTemplate = getTemplateByConditions(t -> templateName.equals(t.getTemplateName()));

        Assertions.assertEquals(templateName, actualTemplate.getTemplateName());
    }

    @ParameterizedTest
    @MethodSource("getInvalidTemplateWithCustomParameters")
    @UserStoryId(1874)
    void shouldNotAcceptPushMessageWithInvalidContent(String templateBody) {

        val templateName = generateRandomString(10).toLowerCase();

        createTemplateWithNamespaceAndCustomParameters(templateName, "en", templateBody)
                .then()
                .assertThat()
                .statusCode(SC_NO_CONTENT);

        val actualTemplate = getTemplateByConditions(t -> templateName.equals(t.getTemplateName()));

        Assertions.assertEquals(templateName, actualTemplate.getTemplateName());

        val customerId = registerCustomer();
        postPushRegistrations(CreatePushRegistrationRequestModel
                .builder()
                .customerId(customerId)
                .infobipToken(generateRandomString(20))
                .firebaseToken(generateRandomString(20))
                .appleToken(generateRandomString(20))
                .build());

        val actualResult = sendNotificationPushMessage(customerId,
                templateName,
                null,
                null,
                generateRandomString(10))
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(MessageResponseModel.class);

        assertAll(
                () -> assertEquals(ResponseStatus.SUCCESS, actualResult.getStatus()),
                () -> assertEquals(null, actualResult.getErrorDescription()),
                () -> assertEquals(1, actualResult.getMessageIds().length)
        );
    }

    @ParameterizedTest
    @MethodSource("templateNames")
    @UserStoryId(1640)
    void shouldCreateTemplateForValidNameFormats(String name) {
        createTemplateWithNameAndLocalizationCode(name.toLowerCase(), generateRandomString())
                .then()
                .assertThat()
                .statusCode(SC_NO_CONTENT);

        val actualTemplate = getTemplateByConditions(t -> name.toLowerCase().equals(t.getTemplateName()));

        Assertions.assertEquals(name.toLowerCase(), actualTemplate.getTemplateName());
    }

    @ParameterizedTest
    @MethodSource("templateInvalidNames")
    @UserStoryId(storyId = 526)
    void shouldReturnErrorForCreateTemplateWithInvalidNames(String name, String errorMessage) {
        createTemplateWithNameAndLocalizationCode(name.toLowerCase(), generateRandomString())
                .then()
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .body(TEMPLATE_NAME_FIELD + "[0]", equalTo(errorMessage));
    }

    @Test
    @UserStoryId(storyId = 526)
    void shouldAddMultipleLocalizationToTemplate() {
        createTemplateSuccessfully(template);
        String secondLocalization = generateRandomString().toLowerCase();

        val templateWithNewLocalization = NewTemplateRequest
                .builder()
                .templateName(templateName)
                .templateBody(templateBody)
                .localizationCode(secondLocalization)
                .build();

        createTemplateSuccessfully(templateWithNewLocalization);

        checkTemplateWithLocalizationExists(templateName, localizationCode);
        checkTemplateWithLocalizationExists(templateName, secondLocalization);
    }

    @Test
    @UserStoryId(storyId = 526)
    void shouldNotCreateTemplateWithSameLocalizationCode() {
        createTemplateWithNameAndLocalizationCode(templateName, localizationCode)
                .then()
                .assertThat()
                .statusCode(SC_NO_CONTENT);

        createTemplateWithNameAndLocalizationCode(templateName, localizationCode)
                .then()
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .body("ErrorMessage",
                        equalTo(TEMPLATE_EXISTS_ERR_MSG));
    }

    @Test
    @UserStoryId(storyId = 526)
    void shouldUpdateTemplate() {
        createTemplateSuccessfully(template);

        String updateTemplateBody = generateRandomString();
        val updateTemplate = NewTemplateRequest
                .builder()
                .templateName(templateName)
                .templateBody(updateTemplateBody)
                .localizationCode(localizationCode)
                .build();

        updateTemplate(updateTemplate)
                .then()
                .assertThat()
                .statusCode(SC_NO_CONTENT);

        getTemplateWithLanguage(templateName, localizationCode)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .body(getTemplateField(TEMPLATE_NAME_FIELD), equalTo(templateName))
                .body(getTemplateField(TEMPLATE_BODY_FIELD), equalTo(updateTemplateBody))
                .body(getTemplateField(LOCALIZATION_FIELD),
                        equalTo(localizationCode));
    }

    @Test
    @Tag(SMOKE_TEST)
    @UserStoryId(storyId = 526)
    void shouldGetTemplateByNameAndLanguage() {
        createTemplateSuccessfully(template);
        getTemplateWithLanguage(templateName, localizationCode)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .body(getTemplateField(TEMPLATE_NAME_FIELD), equalTo(templateName))
                .body(getTemplateField(TEMPLATE_BODY_FIELD), equalTo(templateBody))
                .body(getTemplateField(LOCALIZATION_FIELD),
                        equalTo(localizationCode));
    }

    @Test
    @UserStoryId(storyId = 526)
    void shouldNotGetNonExistingTemplate() {
        getTemplateWithLanguage(generateRandomString(), generateRandomString())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .body(TEMPLATE_FIELD, equalTo(null));
    }

    @Test
    @UserStoryId(storyId = 526)
    void shouldDeleteTemplateLanguage() {
        createTemplateSuccessfully(template);

        deleteTemplateLanguage(templateName, localizationCode)
                .then()
                .assertThat()
                .statusCode(SC_NO_CONTENT);

        getTemplates()
                .then()
                .assertThat()
                .body(TEMPLATE_NAME_FIELD, not(hasItem(templateName)));
    }

    @Test
    @UserStoryId(storyId = 526)
    void shouldDeleteTemplateLanguageFromMultipleLanguages() {
        createTemplateSuccessfully(template);

        String secondLocalization = generateRandomString();

        val templateWithNewLocalization = NewTemplateRequest
                .builder()
                .templateName(templateName)
                .templateBody(templateBody)
                .localizationCode(secondLocalization)
                .build();

        createTemplateSuccessfully(templateWithNewLocalization);

        deleteTemplateLanguage(templateName, localizationCode)
                .then()
                .assertThat()
                .statusCode(SC_NO_CONTENT);

        getTemplateWithLanguage(templateName, localizationCode)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .body(TEMPLATE_FIELD, equalTo(null));
    }

    @Test
    @UserStoryId(storyId = 526)
    void shouldDeleteTemplateByName() {
        createTemplateSuccessfully(template);

        deleteTemplateByName(templateName)
                .then()
                .assertThat()
                .statusCode(SC_NO_CONTENT);

        getTemplates()
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .body(TEMPLATE_NAME_FIELD, not(hasItem(templateName)));
    }

    private TemplateResponse getTemplateByConditions(Predicate<? super TemplateResponse>... predicates) {
        val templates = getTemplates()
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(TemplateResponse[].class);

        Optional<TemplateResponse> candidateTemplate = Stream.of(templates)
                .filter(Arrays.stream(predicates).<Predicate>map((Predicate predicate) -> predicate::test)
                        .reduce(Predicate::or).orElse(t -> false))
                .findFirst();

        return candidateTemplate.orElseGet(() -> candidateTemplate.orElse(new TemplateResponse()));
    }
}
