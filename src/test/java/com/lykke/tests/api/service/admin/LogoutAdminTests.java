package com.lykke.tests.api.service.admin;

import static com.lykke.api.testing.api.common.GenerateUtils.generateRandomEmail;
import static com.lykke.api.testing.api.common.PasswordGen.generateValidPassword;
import static com.lykke.tests.api.service.admin.LoginAdminUtils.getTokenForAdminUser;
import static com.lykke.tests.api.service.adminmanagement.RegisterAdminUtils.registerAdmin;
import static com.lykke.tests.api.service.admin.LoginAdminUtils.logoutAdmin;
import static org.apache.http.HttpStatus.SC_NO_CONTENT;
import static com.lykke.tests.api.common.CommonConsts.SMOKE_TEST;

import com.lykke.api.testing.annotations.UserStoryId;
import com.lykke.tests.api.base.BaseApiTest;
import com.lykke.api.testing.api.common.FakerUtils;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

public class LogoutAdminTests extends BaseApiTest {

    @Test
    @Tag(SMOKE_TEST)
    @UserStoryId(storyId = 226)
    void shouldLogout_statusCodeOK() {
        String emailAddress = generateRandomEmail();
        String password = generateValidPassword();
        String firstName = FakerUtils.firstName;
        String lastName = FakerUtils.lastName;

        registerAdmin(emailAddress, password, firstName, lastName);
        logoutAdmin(getTokenForAdminUser(emailAddress, password))
                .then()
                .assertThat()
                .statusCode(SC_NO_CONTENT);
    }
}
