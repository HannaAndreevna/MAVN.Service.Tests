package com.lykke.tests.api.service.admin;

import static com.lykke.api.testing.api.base.RequestHeader.getHeader;
import static com.lykke.tests.api.base.Credentials.ADMIN_TEST_USER_EMAIL;
import static com.lykke.tests.api.base.Credentials.ADMIN_TEST_USER_PW;
import static com.lykke.tests.api.base.PathConsts.AdminApiService.LOGIN;
import static com.lykke.tests.api.base.PathConsts.AdminApiService.LOGOUT;
import static com.lykke.tests.api.base.Paths.AdminApi.ADMIN_API_PATH;
import static org.apache.http.HttpStatus.SC_OK;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import java.util.logging.Logger;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;

@Slf4j
@UtilityClass
public class LoginAdminUtils {

    private static final String EMAIL_FIELD = "Email";
    private static final String PASSWORD_FIELD = "Password";

    private static String adminToken;

    @SneakyThrows
    @Step("Get Admin token")
    public static String getAdminToken() {
        if (adminToken == null) {
            adminToken = getTokenForAdminUser(ADMIN_TEST_USER_EMAIL, ADMIN_TEST_USER_PW);
        }
        return adminToken;
    }

    public String getTokenForAdminUser(String emailAddress, String password) {

        return loginAdminWithValidEmailAndPassword(emailAddress, password)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .path("Token");
    }

    public Response loginAdminWithValidEmailAndPassword(String emailAddress, String password) {
        return getHeader()
                .body(adminRegisterObject(emailAddress, password))
                .post(ADMIN_API_PATH + LOGIN.getPath());
    }

    public Response loginAdmin(String emailAddress, String password) {
        return loginAdminWithValidEmailAndPassword(emailAddress, password);
    }

    @Step("Log out admin")
    Response logoutAdmin(String token) {
        return getHeader(token)
                .post(ADMIN_API_PATH + LOGOUT.getPath());
    }

    private static JSONObject adminRegisterObject(String emailAddress, String password) {
        JSONObject userRegisterObject = new JSONObject();
        userRegisterObject.put(EMAIL_FIELD, emailAddress);
        userRegisterObject.put(PASSWORD_FIELD, password);
        return userRegisterObject;
    }
}

