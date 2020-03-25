package com.lykke.tests.api.service.notificationsystem.templates;

import static com.lykke.tests.api.base.PathConsts.NotificationSystemService.TEMPLATES_BY_NAME_PATH;
import static com.lykke.tests.api.base.PathConsts.NotificationSystemService.TEMPLATE_NAME_BY_LANGUAGE;
import static com.lykke.tests.api.base.PathConsts.getFullPath;
import static com.lykke.tests.api.base.Paths.NotificationSystem.TEMPLATES_API_PATH;
import static com.lykke.api.testing.api.base.RequestHeader.getHeader;
import static com.lykke.api.testing.api.common.GenerateUtils.generateRandomString;
import static java.util.stream.Collectors.toMap;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.http.HttpStatus.SC_NO_CONTENT;

import com.lykke.tests.api.service.notificationsystem.templates.model.NewTemplateRequest;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import java.util.Map;
import java.util.stream.Stream;
import lombok.experimental.UtilityClass;
import lombok.val;

@UtilityClass
public class TemplateUtils {

    public static final String PARAM_001_DATA = "param001data";
    public static final String PARAM_002_DATA = "param 002 data";
    public static final String PARAM_003_DATA = "//param \\003 ^^data$$";
    private static final String PARAM_001 = "Param001";
    private static final String PARAM_002 = "Param002";
    private static final String PARAM_003 = "Param003";

    public Response getTemplates() {
        return getHeader()
                .get(TEMPLATES_API_PATH);
    }

    @Step("Create template")
    public Response createTemplate(NewTemplateRequest template) {
        return getHeader()
                .body(template)
                .post(TEMPLATES_API_PATH);
    }

    @Step("Update template")
    Response updateTemplate(NewTemplateRequest template) {
        return getHeader()
                .body(template)
                .put(TEMPLATES_API_PATH);
    }

    @Step("Get Template with Language")
    Response getTemplateWithLanguage(String templateName, String templateLanguage) {
        return getHeader()
                .get(TEMPLATES_API_PATH + getFullPath(TEMPLATE_NAME_BY_LANGUAGE.getPath(), templateName,
                        templateLanguage));
    }

    @Step("Delete template language")
    Response deleteTemplateLanguage(String templateName, String templateLanguage) {
        return getHeader()
                .delete(TEMPLATES_API_PATH + getFullPath(TEMPLATE_NAME_BY_LANGUAGE.getPath(), templateName,
                        templateLanguage));
    }

    @Step("Delete template by name {templateName}")
    public Response deleteTemplateByName(String templateName) {
        return getHeader()
                .delete(TEMPLATES_API_PATH + getFullPath(TEMPLATES_BY_NAME_PATH.getPath(), templateName));
    }

    public static Response createTemplateWithNameAndLocalizationCode(String templateName, String localizationCode) {
        val template = NewTemplateRequest
                .builder()
                .templateName(templateName)
                .templateBody(generateRandomString())
                .localizationCode(
                        localizationCode)
                .build();

        return createTemplate(template);
    }

    public Response createTemplateWithNamespaceAndCustomParameters(
            String templateName, String localizationCode, String templateBody) {
        val template = NewTemplateRequest
                .builder()
                .templateName(templateName)
                .templateBody(templateBody)
                .localizationCode(localizationCode)
                .build();

        return createTemplate(template)
                .thenReturn();
    }

    public void createTemplateWithNamespaceAndCustomParameters(
            String templateName, String localizationCode) {

        val json = "@@@\n{\n"
                + "\"param003\":\"zzz\",\n"
                + "\"Param004\":\"abc\""
                + "\n}\n@@@\n";
        val templateBody =
                json + "${" + PARAM_001 + "}:${" + PARAM_001_DATA + "} aaaaaaaa ${" + PARAM_002 + ":" + PARAM_002_DATA
                        + "}"
                        + "\n${param003}"
                        + "\n${Param004}\n";

        NewTemplateRequest template = NewTemplateRequest
                .builder()
                .templateName(templateName)
                .templateBody(templateBody)
                .localizationCode(localizationCode)
                .build();

        createTemplate(template)
                .then()
                .assertThat()
                .statusCode(SC_NO_CONTENT);
    }

    public Map<String, String> getNamespaceAndCustomParametersObject(
            String emailAddress, String firstName, String lastName, String customerLogin, String registrationDate) {

        val customParameters = Stream.of(new String[][]{
                {PARAM_001, PARAM_001_DATA},
                {PARAM_002, PARAM_002_DATA},

                {PARAM_001_DATA, "aaaa"},
                {PARAM_002_DATA, "bbb"}
        })
                .collect(toMap(item -> item[0], item -> null == item[1] ? EMPTY : item[1]));

        return customParameters;
    }
}
