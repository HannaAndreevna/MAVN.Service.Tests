package com.lykke.tests.api.service.customer;

import static com.lykke.tests.api.common.CommonConsts.SMOKE_TEST;
import static com.lykke.tests.api.common.CommonConsts.VALID_PASSWORD;
import static com.lykke.api.testing.api.common.GenerateUtils.generateRandomEmail;
import static com.lykke.tests.api.service.customer.LoginLogoutCustomerUtils.loginUser;
import static com.lykke.tests.api.service.customer.RegisterCustomerUtils.registerUser;

import com.lykke.api.testing.annotations.UserStoryId;
import com.lykke.tests.api.base.BaseApiTest;
import com.lykke.tests.api.service.customer.model.RegistrationRequestModel;
import io.restassured.http.Cookies;
import io.restassured.response.Response;
import lombok.var;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

public class LogoutCustomerTests extends BaseApiTest {

    @Test
    @Tag(SMOKE_TEST)
    @UserStoryId(storyId = 87)
    void shouldLogout_statusCodeOK() {
        var user = new RegistrationRequestModel();
        registerUser();

        Response response = loginUser(user.getEmail(), user.getPassword());
        Cookies cookie = response.getDetailedCookies();

        /*logoutUser(cookie)
                .then()
                .assertThat()
                .statusCode(SC_OK);*/
    }
}
