package com.lykke.tests.api.service.customer;

import static com.lykke.api.testing.api.base.RequestHeader.getHeader;
import static com.lykke.tests.api.base.PathConsts.CustomerApiEndpoint.LOGOUT;
import static com.lykke.tests.api.base.Paths.CUSTOMER_API_PATH;
import static com.lykke.tests.api.base.Paths.Customer.CUSTOMER_LOGIN_API_PATH;
import static com.lykke.tests.api.service.customer.RegisterCustomerUtils.registerUser;
import static org.apache.http.HttpStatus.SC_OK;

import com.lykke.tests.api.common.model.CustomerInfo;
import com.lykke.tests.api.service.customer.model.RegistrationRequestModel;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import lombok.experimental.UtilityClass;
import lombok.var;
import org.json.simple.JSONObject;

@UtilityClass
public class LoginLogoutCustomerUtils {

    private static final String EMAIL_FIELD = "Email";
    private static final String PASSWORD_FIELD = "Password";
    private static String customerToken;
    private static int counter = 0;

    @Deprecated // TODO: customer registrattion now requires verification
    @Step("Get user token")
    public String getCustomerToken() {
        if (customerToken == null) {
            var user = new RegistrationRequestModel();
            registerUser(user);
            customerToken = getUserToken(user);
        }
        return customerToken;
    }

    public static String getUserToken(RegistrationRequestModel user) {
        return getUserToken(user.getEmail(), user.getPassword());
    }

    public static String getUserToken(CustomerInfo user) {
        return getUserToken(user.getEmail(), user.getPassword());
    }

    public static String getUserToken(com.lykke.tests.api.service.customermanagement.model.register
            .RegistrationRequestModel user) {
        return getUserToken(user.getEmail(), user.getPassword());
    }

    public static String getUserToken(String emailAddress, String password) {
        return loginUser(emailAddress, password)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .path("Token");
    }

    public Response loginUser(String emailAddress, String password) {
        return loginUserWithValidEmailAndPassword(emailAddress, password);
    }

    @Step("Log in user with email: {emailAddress}  password: {password}")
    public Response loginUserWithValidEmailAndPassword(String emailAddress, String password) {
        return getHeader()
                .body(userRegisterObject(emailAddress, password))
                .post(CUSTOMER_LOGIN_API_PATH);
    }

    Response logoutUser(String token) {
        return getHeader(token)
                .post(CUSTOMER_API_PATH + LOGOUT.getPath()); // TODO: check it has Bearer (?)
    }

    private static JSONObject userRegisterObject(String emailAddress, String password) {
        JSONObject userRegisterObject = new JSONObject();
        userRegisterObject.put(EMAIL_FIELD, emailAddress);
        userRegisterObject.put(PASSWORD_FIELD, password);
        return userRegisterObject;
    }
}
