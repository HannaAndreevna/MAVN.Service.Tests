package com.lykke.tests.api.service.customermanagement;

import static com.lykke.api.testing.api.base.RequestHeader.getHeader;
import static com.lykke.tests.api.base.PathConsts.CustomerManagementEndpoint.LOGIN;
import static com.lykke.tests.api.base.Paths.CustomerManagement.AUTH_API_PATH;

import com.lykke.tests.api.service.customermanagement.model.login.AuthenticateRequestModel;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import lombok.experimental.UtilityClass;
import org.json.simple.JSONObject;

@UtilityClass
public class LoginCustomerUtils {

    private static final String EMAIL_FIELD = "Email";
    private static final String PASSWORD_FIELD = "Password";

    @Step("Log in customer with email: {emailAddress}  password: {password}")
    public Response loginCustomerWithValidEmailAndPassword(String emailAddress, String password) {
        return getHeader()
                .body(customerRegisterObject(emailAddress, password))
                .post(AUTH_API_PATH + LOGIN.getPath());
    }

    @Step("Log in customer")
    public Response loginCustomer(AuthenticateRequestModel authenticateRequestModel) {
        return getHeader()
                .body(authenticateRequestModel)
                .post(AUTH_API_PATH + LOGIN.getPath());
    }

    private static JSONObject customerRegisterObject(String emailAddress, String password) {
        JSONObject customerRegisterObject = new JSONObject();
        customerRegisterObject.put(EMAIL_FIELD, emailAddress);
        customerRegisterObject.put(PASSWORD_FIELD, password);
        return customerRegisterObject;
    }
}
