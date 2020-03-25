package com.lykke.tests.api.service.customer;

import static com.lykke.tests.api.base.Paths.CUSTOMER_API_CHANGE_PASSWORD_PATH;
import static com.lykke.api.testing.api.base.RequestHeader.getHeader;

import io.restassured.response.Response;
import lombok.experimental.UtilityClass;
import org.json.simple.JSONObject;

@UtilityClass
public class ChangePasswordUtils {

    private static final String PASSWORD_FIELD = "Password";

    public static Response changePassword(String token, String password) {
        return getHeader(token)
                .body(createPasswordObject(password))
                .post(CUSTOMER_API_CHANGE_PASSWORD_PATH);
    }

    private static JSONObject createPasswordObject(String password) {
        JSONObject createPasswordObject = new JSONObject();
        createPasswordObject.put(PASSWORD_FIELD, password);
        return createPasswordObject;
    }
}
