package com.lykke.tests.api.service.admin;

import static com.lykke.api.testing.api.common.GenerateUtils.generateName;
import static com.lykke.api.testing.api.common.GenerateUtils.generateRandomEmail;
import static com.lykke.api.testing.api.common.GenerateUtils.generateRandomString;
import static com.lykke.api.testing.api.common.PasswordGen.generateValidPassword;
import static com.lykke.tests.api.common.CommonConsts.SMOKE_TEST;
import static com.lykke.tests.api.service.admin.GetAdminsUtils.getAdminsPaginatedResponse;
import static com.lykke.tests.api.service.admin.GetAdminsUtils.getAdminsPaginatedValidationResponse;
import static com.lykke.tests.api.service.adminmanagement.RegisterAdminUtils.registerAdmin;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.lykke.api.testing.annotations.UserStoryId;
import com.lykke.tests.api.base.BaseApiTest;
import com.lykke.tests.api.service.admin.model.AdminListRequest;
import com.lykke.tests.api.service.admin.model.CustomerListRequest;
import java.util.stream.Stream;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class GetAdminsPaginatedTests extends BaseApiTest {

    private static final int VALID_PAGE_SIZE = 1;
    private static final int VALID_1ST_CURRENT_PAGE = 1;
    private static final int VALID_2ND_CURRENT_PAGE = 2;

    private static String adminEmail;
    private static String adminPassword;
    private static String adminId;
    private static String adminFirstName;
    private static String adminLastName;

    @BeforeEach
    void setup() {
        adminEmail = generateRandomEmail();
        adminPassword = generateValidPassword();
        adminFirstName = generateName(6);
        adminLastName = generateName(6);
        adminId = registerAdmin(adminEmail, adminPassword, adminFirstName, adminLastName);
    }

    @ParameterizedTest(name = "Run {index}: currentPage={0}, pageSize={1}")
    @MethodSource("getWrongPaginationParameters")
    @UserStoryId(storyId = {925})
    void shouldNotReturnAdminsPaginated(int currentPage, int pageSize) {

        val requestObject = CustomerListRequest
                .builder()
                .currentPage(currentPage)
                .pageSize(pageSize)
                .build();

        val validationResponse = getAdminsPaginatedValidationResponse(requestObject);

        assertEquals(requestObject.getValidationResponse(), validationResponse);
    }

    @Test
    @UserStoryId(storyId = {925})
    @Tag(SMOKE_TEST)
    void shouldGetAdminByFullEmail() {
        val requestObject = AdminListRequest
                .builder()
                .currentPage(VALID_1ST_CURRENT_PAGE)
                .pageSize(VALID_PAGE_SIZE)
                .searchValue(adminEmail)
                .build();

        val actualAdmins = getAdminsPaginatedResponse(requestObject);

        assertAll(
                () -> assertEquals(1, actualAdmins.getItems().length),
                () -> assertEquals(adminEmail, actualAdmins.getItems()[0].getEmail()),
                () -> assertEquals(adminId, actualAdmins.getItems()[0].getId())
        );
    }

    @Test
    @UserStoryId(storyId = {925})
    @Tag(SMOKE_TEST)
    void shouldGetAdminPaginated() {
        val secondAdminEmail = generateRandomEmail();
        val secondAdminPassword = generateValidPassword();
        val secondAdminFirstName = generateName(6);
        val secondAdminLastName = generateName(6);
        registerAdmin(secondAdminEmail, secondAdminPassword, secondAdminFirstName, secondAdminLastName);

        val requestObject = AdminListRequest
                .builder()
                .currentPage(VALID_2ND_CURRENT_PAGE)
                .pageSize(VALID_PAGE_SIZE)
                .build();

        val actualAdmins = getAdminsPaginatedResponse(requestObject);

        assertAll(
                () -> assertEquals(1, actualAdmins.getItems().length),
                () -> assertEquals(adminEmail, actualAdmins.getItems()[0].getEmail()),
                () -> assertEquals(adminId, actualAdmins.getItems()[0].getId())
        );
    }

    static Stream<Arguments> getWrongPaginationParameters() {
        return TestDataForPaginatedTests.getWrongPaginationParameters();
    }
}
