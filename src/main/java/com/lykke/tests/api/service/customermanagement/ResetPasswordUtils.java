package com.lykke.tests.api.service.customermanagement;

import static com.lykke.tests.api.base.PathConsts.CustomerManagementEndpoint.PASSWORD_RESET_PATH;
import static com.lykke.tests.api.base.PathConsts.CustomerManagementEndpoint.RESET_PASSWORD_PATH;
import static com.lykke.tests.api.base.PathConsts.getFullPath;
import static com.lykke.tests.api.base.Paths.CustomerManagement.CUSTOMERS_API_PATH;
import static com.lykke.api.testing.api.base.RequestHeader.getHeader;

import io.restassured.response.Response;
import lombok.experimental.UtilityClass;
import org.json.simple.JSONObject;

@UtilityClass
public class ResetPasswordUtils {

    private static final String CUSTOMER_EMAIL_FIELD = "CustomerEmail";
    private static final String RESET_IDENTIFIER_FIELD = "ResetIdentifier";
    private static final String PASSWORD_FIELD = "Password";

    Response sendResetPasswordIdentifierByEmail(String emailAddress) {
        return getHeader()
                .body(createEmailObject(emailAddress))
                .post(CUSTOMERS_API_PATH + RESET_PASSWORD_PATH.getPath());
    }

    Response resetPassword(String customerEmail, String resetIdentifier, String newPassword) {
        return getHeader()
                .body(createResetPwObject(customerEmail, resetIdentifier, newPassword))
                .post(CUSTOMERS_API_PATH + PASSWORD_RESET_PATH.getPath());
    }

    private static JSONObject createResetPwObject(String customerEmail, String resetIdentifier, String newPassword) {
        JSONObject resetPwObject = new JSONObject();
        resetPwObject.put(CUSTOMER_EMAIL_FIELD, customerEmail);
        resetPwObject.put(RESET_IDENTIFIER_FIELD, resetIdentifier);
        resetPwObject.put(PASSWORD_FIELD, newPassword);
        return resetPwObject;
    }

    private static JSONObject createEmailObject(String emailAddress) {
        JSONObject emailObj = new JSONObject();
        emailObj.put("Email", emailAddress);
        return emailObj;
    }
}
