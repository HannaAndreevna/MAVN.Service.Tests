package com.lykke.tests.api.service.notificationsystembroker;

import static com.lykke.api.testing.api.base.RequestHeader.getHeader;
import static com.lykke.tests.api.base.Paths.CUSTOMER_API_EMAILS_PATH;
import static com.lykke.tests.api.base.Paths.EMAIL_MESSAGE_API_PATH;
import static com.lykke.tests.api.common.CommonConsts.AWAITILITY_DEFAULT_MAX_SEC;
import static com.lykke.tests.api.common.CommonConsts.AWAITILITY_DEFAULT_MID_SEC;
import static com.lykke.tests.api.service.admin.LoginAdminUtils.getAdminToken;
import static com.lykke.tests.api.service.customer.LoginLogoutCustomerUtils.getUserToken;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.trim;
import static org.apache.http.HttpStatus.SC_NO_CONTENT;
import static org.apache.http.HttpStatus.SC_OK;

import com.lykke.tests.api.base.Paths.Customer;
import com.lykke.tests.api.service.customer.model.email.EmailVerificationRequest;
import com.lykke.tests.api.service.notificationsystembroker.model.EmailMessageResponseModel;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import lombok.val;
import org.awaitility.Awaitility;
import org.awaitility.Duration;

@UtilityClass
public class EmailConfirmationUtils {

    public static void confirmRegistration(String emailAddress, String password) {
        if (!confirmRegistrationViaAnonymousEndpoint(emailAddress)) {
            confirmRegistrationViaEndpointWithAuthorization(emailAddress, password);
        }
    }

    @SneakyThrows
    private static boolean confirmRegistrationViaAnonymousEndpoint(String emailAddress) {
        try {
            Awaitility.await()
                    .given()
                    .atMost(AWAITILITY_DEFAULT_MID_SEC, TimeUnit.SECONDS)
                    .pollInterval(Duration.ONE_SECOND)
                    .then()
                    .until(() -> {
                        val messages = retrieveEmailMessages(emailAddress);
                        if (null == messages || 0 == messages.length) {
                            return false;
                        }
                        val messageCandidate = Arrays.stream(messages)
                                .reduce((first, second) -> second);
                        val body = messageCandidate.isPresent() ? messageCandidate.get().getBody() : EMPTY;

                        if (EMPTY.equals(body)) {
                            return false;
                        }

                        // email-confirmation%3Fcode%3DOWQ2ODdjZWUtMjc1Yi00ZWY1LTk2ZWQtYmViMjgzNzJkZDNh&apn=com.lykke.falcon.dev&isi=1470065092&ibi=com.lykke.falcon.dev
                        // takes everything between %3D and &apn
                        val patternString = "(\\w+)(?=\\&apn\\=)";
                        val pattern = Pattern.compile(patternString);
                        val matcher = pattern.matcher(body);
                        String code = EMPTY;
                        if (1 <= matcher.groupCount()) {
                            matcher.find();
                            code = matcher.group().substring(2);
                        } else {
                            return false;
                        }

                        getHeader()
                                .body(EmailVerificationRequest
                                        .builder()
                                        .verificationCode(code)
                                        .build())
                                .post(Customer.VERIFY_EMAIL_API_PATH)
                                .then()
                                .assertThat()
                                .statusCode(SC_NO_CONTENT);

                        return true;
                    });
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private static void confirmRegistrationViaEndpointWithAuthorization(String emailAddress, String password) {
        val token = getUserToken(emailAddress, password);
        getHeader(token)
                .post(CUSTOMER_API_EMAILS_PATH)
                .then()
                .assertThat()
                .statusCode(SC_NO_CONTENT);
        confirmRegistrationViaAnonymousEndpoint(emailAddress);
    }

    public static EmailMessageResponseModel[] retrieveEmailMessages(String emailAddress) {
        return getHeader(getAdminToken())
                .body("\"" + emailAddress + "\"")
                .post(EMAIL_MESSAGE_API_PATH)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(EmailMessageResponseModel[].class);
    }
}
