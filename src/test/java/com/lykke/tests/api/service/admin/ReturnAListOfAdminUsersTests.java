package com.lykke.tests.api.service.admin;

import static com.lykke.api.testing.api.common.GenerateUtils.generateRandomEmail;
import static com.lykke.api.testing.api.common.PasswordGen.generateValidPassword;
import static com.lykke.tests.api.common.CommonConsts.SMOKE_TEST;
import static com.lykke.tests.api.service.admin.LoginAdminUtils.getTokenForAdminUser;
import static com.lykke.tests.api.service.admin.ReturnAListOfAdminUsersUtils.getAdminUsers;
import static com.lykke.tests.api.service.adminmanagement.RegisterAdminUtils.registerAdmin;
import static java.util.stream.Collectors.toList;
import static org.apache.http.HttpStatus.SC_OK;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.lykke.api.testing.annotations.UserStoryId;
import com.lykke.api.testing.api.common.FakerUtils;
import com.lykke.tests.api.base.BaseApiTest;
import com.lykke.tests.api.service.admin.model.admins.AdminListResponse;
import java.util.Arrays;
import lombok.val;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

public class ReturnAListOfAdminUsersTests extends BaseApiTest {

    private static String emailAddress = generateRandomEmail();
    private static String password = generateValidPassword();
    private static String firstName = FakerUtils.firstName;
    private static String lastName = FakerUtils.lastName;

    @Tag(SMOKE_TEST)
    @Test
    @UserStoryId(storyId = 504)
    void shouldGetListOfAdminUsers() {
        registerAdmin(emailAddress, password, firstName, lastName);
        val token = getTokenForAdminUser(emailAddress, password);
        val listOfAdminUsers = Arrays.stream(
                getAdminUsers(token)
                        .then()
                        .assertThat()
                        .statusCode(SC_OK)
                        .extract()
                        .as(AdminListResponse.class)
                        .getItems())
                .filter(admin -> emailAddress.equals(admin.getEmail()))
                .collect(toList());

        assertAll(
                () -> assertEquals(1, listOfAdminUsers.size()),
                () -> assertEquals(emailAddress, listOfAdminUsers.get(0).getEmail())
        );
        // TODO: Can add DB comparison as well
    }
}
