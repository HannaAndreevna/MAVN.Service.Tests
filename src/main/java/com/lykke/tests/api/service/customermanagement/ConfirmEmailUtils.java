package com.lykke.tests.api.service.customermanagement;

import static com.lykke.tests.api.base.PathConsts.CustomerManagementEndpoint.EMAILS_PATH;
import static com.lykke.tests.api.base.Paths.CustomerManagement.EMAILS_API_PATH;
import static com.lykke.api.testing.api.base.RequestHeader.getHeader;

import io.restassured.response.Response;
import lombok.experimental.UtilityClass;
import org.json.simple.JSONObject;

@UtilityClass
public class ConfirmEmailUtils {

    private static final String CUSTOMER_ID_FIELD = "CustomerId";
    private static final String VERIFICATION_CODE_FIELD = "VerificationCode";

    Response shouldConfirmEmail(String customerId, String verificationCode) {
        return getHeader()
                .body(createEmailConfirmObject(customerId, verificationCode))
                .post(EMAILS_API_PATH + EMAILS_PATH.getPath());
    }

    private static JSONObject createEmailConfirmObject(String customerId, String verificationCode) {
        JSONObject emailConfirmObj = new JSONObject();
        emailConfirmObj.put(CUSTOMER_ID_FIELD, customerId);
        emailConfirmObj.put(VERIFICATION_CODE_FIELD, verificationCode);
        return emailConfirmObj;
    }
}
