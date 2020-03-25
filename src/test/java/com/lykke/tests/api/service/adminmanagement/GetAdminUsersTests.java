package com.lykke.tests.api.service.adminmanagement;

import static com.lykke.api.testing.api.common.GenerateUtils.generateRandomEmail;
import static com.lykke.api.testing.api.common.GenerateUtils.generateRandomString;
import static com.lykke.api.testing.api.common.PasswordGen.generateValidPassword;
import static com.lykke.tests.api.common.CommonConsts.SMOKE_TEST;
import static com.lykke.tests.api.service.adminmanagement.GetAdminUsersUtils.getAdminUserByEmail;
import static com.lykke.tests.api.service.adminmanagement.GetAdminUsersUtils.getAdminUsers;
import static com.lykke.tests.api.service.adminmanagement.GetAdminUsersUtils.getAdminUsersPaginated;
import static com.lykke.tests.api.service.adminmanagement.GetAdminUsersUtils.getAdminUsersTotalCount;
import static com.lykke.tests.api.service.adminmanagement.RegisterAdminUtils.registerAdmin;
import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.of;

import com.lykke.api.testing.annotations.UserStoryId;
import com.lykke.api.testing.api.common.FakerUtils;
import com.lykke.tests.api.base.BaseApiTest;
import com.lykke.tests.api.service.adminmanagement.model.AdminUser;
import com.lykke.tests.api.service.adminmanagement.model.GetByEmailRequestModel;
import com.lykke.tests.api.service.adminmanagement.model.RegistrationRequestModel;
import io.restassured.response.Response;
import java.util.Arrays;
import java.util.stream.Stream;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

public class GetAdminUsersTests extends BaseApiTest {

    public static final String CURRENT_PAGE_FIELD = "CurrentPage";
    public static final String PAGE_SIZE_FIELD = "PageSize";
    public static final String CURRENT_PAGE_FIELD_0 = CURRENT_PAGE_FIELD + "[0]";
    public static final String PAGE_SIZE_FIELD_0 = PAGE_SIZE_FIELD + "[0]";
    private static final String NAME_FIELD = "name";
    private static final String ADMIN_USER_ID_FIELD = "Profile.AdminUserId";
    private static final String EMAIL_PR_FIELD = "Profile.Email";
    private static final String FIRST_NAME_FIELD = "Profile.FirstName";
    private static final String LAST_NAME_FIELD = "Profile.LastName";
    private static final String ERROR_FIELD = "Error";
    private static final String PROFILE_FIELD = "Profile";
    private static final String ERROR_MESSAGE = "AdminUserDoesNotExist";
    private static final String TOTAL_COUNT_FIELD = "TotalCount";
    private static final String CURRENT_PAGE_LOWER_BOUND_MESSAGE = "Current page can't be less than 1";
    private static final String PAGE_SIZE_LOWER_BOUND_MESSAGE = "Page Size can't be less than 1";
    private static final String PAGE_SIZE_UPPER_BOUND_MESSAGE = "Page Size cannot exceed more then 1000";

    private static final int currentPage = 1;
    private static final int pageSize = 100;

    private String emailAddress;
    private String firstName;
    private String lastName;
    private String adminUserId;
    private String notRegisteredEmail = generateRandomEmail();
    private RegistrationRequestModel newAdmin;

    private static Stream paginated_InvalidParameters() {
        return Stream.of(
                of(-1, pageSize, CURRENT_PAGE_FIELD_0, CURRENT_PAGE_LOWER_BOUND_MESSAGE),
                of(0, pageSize, CURRENT_PAGE_FIELD_0, CURRENT_PAGE_LOWER_BOUND_MESSAGE),
                of(currentPage, -1, PAGE_SIZE_FIELD_0, PAGE_SIZE_LOWER_BOUND_MESSAGE),
                of(currentPage, 0, PAGE_SIZE_FIELD_0, PAGE_SIZE_LOWER_BOUND_MESSAGE),
                of(currentPage, 1001, PAGE_SIZE_FIELD_0, PAGE_SIZE_UPPER_BOUND_MESSAGE)
        );
    }

    @BeforeEach
    void setup() {
        emailAddress = generateRandomEmail();
        firstName = FakerUtils.firstName;
        lastName = FakerUtils.lastName;
        newAdmin = RegistrationRequestModel
                .builder()
                .email(emailAddress)
                .password(generateValidPassword())
                .firstName(firstName)
                .lastName(lastName)
                .company(FakerUtils.companyName)
                .jobTitle(generateRandomString(10))
                .department(generateRandomString(10))
                .phoneNumber(FakerUtils.phoneNumber)
                .build();
        adminUserId = registerAdmin(newAdmin).getAdmin().getAdminUserId();
    }

    @Test
    @Tag(SMOKE_TEST)
    @UserStoryId(storyId = {503, 2678})
    void shouldGetAdminUsers() {
        val expectedResult = AdminUser
                .builder()
                .adminUserId(adminUserId)
                .firstName(newAdmin.getFirstName())
                .lastName(newAdmin.getLastName())
                .email(newAdmin.getEmail())
                .phoneNumber(newAdmin.getPhoneNumber())
                .company(newAdmin.getCompany())
                .department(newAdmin.getDepartment())
                .jobTitle(newAdmin.getJobTitle())
                .build();

        Response getAdminUsers = getAdminUsers();
        val adminUsers = getAdminUsers
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(AdminUser[].class);

        val actualResult = Arrays.stream(adminUsers)
                .filter(adminUser -> adminUser.getAdminUserId().equalsIgnoreCase(adminUserId))
                .findFirst()
                .orElse(new AdminUser());

        assertAll(
                () -> assertEquals(expectedResult.getAdminUserId(), actualResult.getAdminUserId()),
                () -> assertEquals(expectedResult.getFirstName(), actualResult.getFirstName()),
                () -> assertEquals(expectedResult.getLastName(), actualResult.getLastName()),
                () -> assertEquals(expectedResult.getEmail(), actualResult.getEmail()),
                () -> assertEquals(expectedResult.getPhoneNumber(), actualResult.getPhoneNumber()),
                () -> assertEquals(expectedResult.getCompany(), actualResult.getCompany()),
                () -> assertEquals(expectedResult.getDepartment(), actualResult.getDepartment()),
                () -> assertEquals(expectedResult.getJobTitle(), actualResult.getJobTitle())
        );
    }

    @Test
    @Tag(SMOKE_TEST)
    @UserStoryId(storyId = 821)
    void shouldGetAdminUserByEmail() {
        getAdminUserByEmail(GetByEmailRequestModel
                .builder()
                .email(emailAddress)
                .build())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .body(ADMIN_USER_ID_FIELD, equalTo(adminUserId))
                .body(EMAIL_PR_FIELD, equalTo(emailAddress))
                .body(FIRST_NAME_FIELD, equalTo(firstName))
                .body(LAST_NAME_FIELD, equalTo(lastName))
                .body(ERROR_FIELD, equalTo("None"));
    }

    @Test
    @UserStoryId(storyId = 821)
    void shouldNotGetAdminUserByEmailWhenUserWithSuchEmailIsNotRegistered() {
        getAdminUserByEmail(GetByEmailRequestModel
                .builder()
                .email(notRegisteredEmail)
                .build())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .body(PROFILE_FIELD, nullValue())
                .body(ERROR_FIELD, equalTo(ERROR_MESSAGE));
    }

    @ParameterizedTest
    @UserStoryId(storyId = 821)
    @Tag(SMOKE_TEST)
    @CsvSource({"1, 100",
            "10, 50",
            "1, 1"})
    void shouldGetUsersPaginated(int currentPage, int pageSize) {
        getAdminUsersPaginated(currentPage, pageSize)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .body(CURRENT_PAGE_FIELD, equalTo(currentPage))
                .body(PAGE_SIZE_FIELD, equalTo(pageSize))
                .body(TOTAL_COUNT_FIELD, equalTo(getAdminUsersTotalCount()));
    }

    @ParameterizedTest(name = "Run {index}: currentPage={0}, pageSize={1}, field={2}, message={3}")
    @MethodSource("paginated_InvalidParameters")
    void shouldNotGetAdminUsersPaginatedFirInvalidParams(int currentPage, int pageSize, String field, String message) {
        getAdminUsersPaginated(currentPage, pageSize)
                .then()
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .body(field, equalTo(message));
    }
}
