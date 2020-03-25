package com.lykke.tests.api.service.customermanagement;

import static com.lykke.tests.api.base.PathConsts.CustomerManagementEndpoint.CHANGE_PASSWORD_PATH;
import static com.lykke.tests.api.base.Paths.CustomerManagement.CUSTOMERS_API_PATH;
import static com.lykke.api.testing.api.base.RequestHeader.getHeader;

import io.restassured.response.Response;
import lombok.experimental.UtilityClass;
import org.json.simple.JSONObject;

@UtilityClass
public class ChangePasswordUtils {

    private static final String CUSTOMER_ID_FIELD = "CustomerId";
    private static final String PW_FIELD = "Password";

    Response changeCustomerPassword(String customerId, String newPassword) {
        return getHeader()
                .body(createPasswordUpdateObject(customerId, newPassword))
                .post(CUSTOMERS_API_PATH + CHANGE_PASSWORD_PATH.getPath());
    }

    private static JSONObject createPasswordUpdateObject(String customerId, String newPassword) {
        JSONObject passwordObj = new JSONObject();
        passwordObj.put(CUSTOMER_ID_FIELD, customerId);
        passwordObj.put(PW_FIELD, newPassword);
        return passwordObj;
    }
}
