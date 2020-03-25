package com.lykke.tests.api.service.customer;

import static com.lykke.tests.api.base.Paths.Customer.CUSTOMER_API_RESET_PASSWORD_PATH;
import static com.lykke.tests.api.base.Paths.Customer.PASSWORD_VALIDATION_RULES_API_PATH;
import static com.lykke.tests.api.base.Paths.Customer.VALIDATE_RESET_PASSWORD_API_PATH;
import static com.lykke.api.testing.api.base.RequestHeader.getHeader;
import static com.lykke.tests.api.service.notificationsystembroker.MessagesUtils.getEmailMessages;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.apache.http.HttpStatus.SC_OK;

import com.lykke.tests.api.common.model.ValidationErrorResponseModel;
import com.lykke.tests.api.service.customer.model.ResetPasswordRequest;
import com.lykke.tests.api.service.customer.model.ValidateResetPasswordIdentifierRequest;
import com.lykke.tests.api.service.notificationsystembroker.model.EmailMessageResponseModel;
import io.restassured.response.Response;
import java.util.Arrays;
import java.util.regex.Pattern;
import lombok.experimental.UtilityClass;
import lombok.val;

@UtilityClass
public class ResetPasswordUtils {

    private static final String PASSWORD_RESET_IDENTIFIER_MSG_SUBJECT = "MAVN: password reset link";
    private static final Pattern PASSWORD_RESET_IDENTIFIER_PATTERN = Pattern.compile("(?<=esetIdentifier\\=)\\w+(?=$)");

    public ValidationErrorResponseModel getValidationErrorResponse(ResetPasswordRequest requestObject) {
        return getHeader()
                .body(requestObject)
                .post(CUSTOMER_API_RESET_PASSWORD_PATH)
                .then()
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .extract()
                .as(ValidationErrorResponseModel.class);
    }

    public Response validateResetPasswordIdentifier(ValidateResetPasswordIdentifierRequest requestObject) {
        return getHeader()
                .body(requestObject)
                .post(VALIDATE_RESET_PASSWORD_API_PATH)
                .thenReturn();
    }

    public Response getPasswordValidationRules() {
        return getHeader()
                .get(PASSWORD_VALIDATION_RULES_API_PATH)
                .thenReturn();
    }

    public Response resetPassword(ResetPasswordRequest requestObject) {
        return getHeader()
                .body(requestObject)
                .post(CUSTOMER_API_RESET_PASSWORD_PATH)
                .thenReturn();
    }

    public String getResetPasswordIdentifier(String email) {
        val emailMessages = getEmailMessages(email)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(EmailMessageResponseModel[].class);

        val passwordResetLinkMessage = Arrays.stream(emailMessages)
                .filter(msg -> PASSWORD_RESET_IDENTIFIER_MSG_SUBJECT.equalsIgnoreCase(msg.getSubject()))
                .findFirst()
                .orElse(new EmailMessageResponseModel());

        val matcher = PASSWORD_RESET_IDENTIFIER_PATTERN.matcher(passwordResetLinkMessage.getBody());
        matcher.find();
        return EMPTY.equalsIgnoreCase(matcher.group(0))
                ? EMPTY
                : matcher.group(0);
    }
}
