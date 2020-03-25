package com.lykke.tests.api.service.notificationsystembroker;

import static com.lykke.tests.api.base.Paths.EMAIL_MESSAGE_API_PATH;
import static com.lykke.tests.api.common.CommonConsts.AWAITILITY_WAITING_FOR_EMAIL_MESSAGE_SEC;
import static com.lykke.tests.api.service.notificationsystembroker.EmailConfirmationUtils.retrieveEmailMessages;
import static io.restassured.RestAssured.given;
import static java.util.stream.Collectors.toMap;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.lykke.tests.api.service.notificationsystembroker.model.EmailMessageResponseModel;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;
import lombok.experimental.UtilityClass;
import lombok.val;
import org.awaitility.Awaitility;
import org.awaitility.Duration;

@UtilityClass
public class MessagesUtils {

    private static final String TEXT_JSON = "text/json";

    @Step("Get email messages for email {emailAddress}")
    public static Response getEmailMessages(String emailAddress) {
        return given()
                .contentType(TEXT_JSON)
                .when()
                .body("\"" + emailAddress + "\"")
                .post(EMAIL_MESSAGE_API_PATH);
    }

    @Step("Get sms messages for phone {phone}")
    public Response getSmsMessages(String phone) {
        return given()
                .contentType(TEXT_JSON)
                .when()
                .body("\"" + phone + "\"")
                .post(EMAIL_MESSAGE_API_PATH);
    }

    public Response getPushMessages(String phone) {
        return given()
                .contentType(TEXT_JSON)
                .when()
                .body("\"" + phone + "\"")
                .post(EMAIL_MESSAGE_API_PATH);
    }

    public static Map<String, String> getTemplateParametersObject(
            String emailAddress, String firstName, String lastName, String customerLogin, String registrationDate) {

        return Stream.of(new String[][]{
                {"PersonalData::Email", emailAddress},
                {"PersonalData::FirstName", firstName},
                {"PersonalData::LastName", lastName},
                {"PersonalData::CustomerLogin", customerLogin},
                {"RegistrationDate", registrationDate}
        })
                .collect(toMap(item -> item[0], item -> null == item[1] ? EMPTY : item[1]));
    }

    public Map<String, String> getTemplateParametersObjectCaseInsensitive(
            String emailAddress, String firstName, String lastName, String customerLogin, String registrationDate) {

        return Stream.of(new String[][]{
                {"PersonalData::Email".toLowerCase(), emailAddress.toLowerCase()},
                {"PersonalData::FirstName".toLowerCase(), firstName.toLowerCase()},
                {"PersonalData::LastName".toLowerCase(), lastName.toLowerCase()},
                {"PersonalData::CustomerLogin".toLowerCase(), customerLogin.toLowerCase()},
                {"RegistrationDate".toLowerCase(), registrationDate.toLowerCase()}
        })
                .collect(toMap(item -> item[0], item -> null == item[1] ? EMPTY : item[1]));
    }

    public EmailMessageResponseModel getEmailMessageByEmail(String email) {
        Awaitility.await()
                .atMost(AWAITILITY_WAITING_FOR_EMAIL_MESSAGE_SEC, TimeUnit.SECONDS)
                .pollInterval(Duration.TWO_SECONDS)
                .until(() -> 0 < retrieveEmailMessages(email).length);
        val emailCollection = retrieveEmailMessages(email);
        assertTrue(0 < emailCollection.length);
        val emailMessageCandidate = Arrays.stream(emailCollection)
                .filter(msg -> email.equals(msg.getEmail()))
                .findFirst();
        val emailMessage =
                emailMessageCandidate.isPresent() ? emailMessageCandidate.get() : new EmailMessageResponseModel();
        return emailMessage;
    }
}
