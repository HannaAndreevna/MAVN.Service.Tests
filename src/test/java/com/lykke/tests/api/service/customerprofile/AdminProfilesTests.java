package com.lykke.tests.api.service.customerprofile;

import static com.lykke.api.testing.api.common.GenerateUtils.generateRandomEmail;
import static com.lykke.api.testing.api.common.GenerateUtils.generateRandomString;
import static com.lykke.api.testing.api.common.GenerateUtils.getRandomUuid;
import static com.lykke.tests.api.common.CommonConsts.SMOKE_TEST;
import static com.lykke.tests.api.service.admin.AdminsUtils.generateAdminPassword;
import static com.lykke.tests.api.service.admin.AdminsUtils.postAdmin;
import static com.lykke.tests.api.service.admin.LoginAdminUtils.getTokenForAdminUser;
import static com.lykke.tests.api.service.customerprofile.AdminProfileUtils.createDefaultAdminProfile;
import static com.lykke.tests.api.service.customerprofile.AdminProfileUtils.deleteAdminProfileById;
import static com.lykke.tests.api.service.customerprofile.AdminProfileUtils.getAdminProfileById;
import static com.lykke.tests.api.service.customerprofile.AdminProfileUtils.getAdminProfilesByIds;
import static com.lykke.tests.api.service.customerprofile.AdminProfileUtils.postAdminProfile;
import static com.lykke.tests.api.service.customerprofile.AdminProfileUtils.putAdminProfile;
import static java.util.stream.Collectors.toList;
import static org.apache.http.HttpStatus.SC_OK;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.lykke.api.testing.annotations.UserStoryId;
import com.lykke.api.testing.api.common.FakerUtils;
import com.lykke.tests.api.base.BaseApiTest;
import com.lykke.tests.api.service.admin.model.admins.AdminCreateModel;
import com.lykke.tests.api.service.admin.model.admins.AdminModel;
import com.lykke.tests.api.service.customerprofile.model.admins.AdminProfile;
import com.lykke.tests.api.service.customerprofile.model.admins.AdminProfileErrorCode;
import com.lykke.tests.api.service.customerprofile.model.admins.AdminProfileRequest;
import com.lykke.tests.api.service.customerprofile.model.admins.AdminProfileResponse;
import java.util.Arrays;
import java.util.stream.Stream;
import lombok.val;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

public class AdminProfilesTests extends BaseApiTest {

    @Test
    @Tag(SMOKE_TEST)
    @UserStoryId(3623)
    void shouldReturnAdminProfilesByIds() {
        val adminProfileIds = Stream
                .of(createDefaultAdminProfile(), createDefaultAdminProfile(), createDefaultAdminProfile())
                .map(admin -> admin.getData().getAdminId())
                .sorted()
                .collect(toList())
                .toArray(new String[]{});

        val searchResult = getAdminProfilesByIds(adminProfileIds)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(AdminProfile[].class);

        assertArrayEquals(
                adminProfileIds,
                Arrays.stream(searchResult)
                        .map(admin -> admin.getAdminId())
                        .sorted()
                        .collect(toList())
                        .toArray(new String[]{}));
    }

    @Test
    @Tag(SMOKE_TEST)
    @UserStoryId(3623)
    void shouldReturnAdminProfileById() {
        val adminProfile = createDefaultAdminProfile();

        val searchResult = getAdminProfileById(adminProfile.getData().getAdminId())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(AdminProfileResponse.class);

        assertEquals(adminProfile, searchResult);
    }

    @Test
    @Tag(SMOKE_TEST)
    @UserStoryId(3623)
    void shouldNotCreateAdminProfileForAlreadyRegisteredAdminAndReturnExistingProfile() {
        val email = generateRandomEmail();
        val password = generateAdminPassword();
        val department = generateRandomString(10);
        val adminCreationResult = postAdmin(AdminCreateModel
                .adminCreateModelBuilder()
                .email(email)
                .password(password)
                .firstName(FakerUtils.firstName)
                .lastName(FakerUtils.lastName)
                .phoneNumber(FakerUtils.phoneNumber)
                .company(FakerUtils.companyName)
                .department(department)
                .jobTitle(FakerUtils.title)
                .build())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(AdminModel.class);

        val token = getTokenForAdminUser(email, password);

        val actualResult = postAdminProfile(AdminProfileRequest
                .builder()
                .adminId(adminCreationResult.getId())
                .email(email)
                .firstName(FakerUtils.firstName)
                .lastName(FakerUtils.lastName)
                .phoneNumber(FakerUtils.phoneNumber)
                .company(FakerUtils.companyName)
                .department(department)
                .jobTitle(FakerUtils.title)
                .build())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(AdminProfileResponse.class);

        assertAll(
                () -> assertEquals(AdminProfileErrorCode.ADMIN_PROFILE_ALREADY_EXISTS, actualResult.getErrorCode()),
                () -> assertEquals(adminCreationResult.getEmail(), actualResult.getData().getEmail()),
                () -> assertEquals(adminCreationResult.getFirstName(), actualResult.getData().getFirstName()),
                () -> assertEquals(adminCreationResult.getLastName(), actualResult.getData().getLastName()),
                () -> assertEquals(adminCreationResult.getPhoneNumber(), actualResult.getData().getPhoneNumber()),
                () -> assertEquals(adminCreationResult.getCompany(), actualResult.getData().getCompany()),
                () -> assertEquals(adminCreationResult.getDepartment(), actualResult.getData().getDepartment()),
                () -> assertEquals(adminCreationResult.getJobTitle(), actualResult.getData().getJobTitle())
        );
    }

    @Test
    @Tag(SMOKE_TEST)
    @UserStoryId(3623)
    void shouldCreateAdminProfile() {
        val email = generateRandomEmail();

        val requestModel = AdminProfileRequest
                .builder()
                .adminId(getRandomUuid())
                .email(email)
                .firstName(FakerUtils.firstName)
                .lastName(FakerUtils.lastName)
                .phoneNumber(FakerUtils.phoneNumber)
                .company(FakerUtils.companyName)
                .department(generateRandomString(10))
                .jobTitle(FakerUtils.title)
                .build();

        val actualResult = postAdminProfile(requestModel)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(AdminProfileResponse.class);

        assertAll(
                () -> assertEquals(requestModel.getAdminId(), actualResult.getData().getAdminId()),
                () -> assertEquals(requestModel.getEmail(), actualResult.getData().getEmail()),
                () -> assertEquals(requestModel.getFirstName(), actualResult.getData().getFirstName()),
                () -> assertEquals(requestModel.getLastName(), actualResult.getData().getLastName()),
                () -> assertEquals(requestModel.getPhoneNumber(), actualResult.getData().getPhoneNumber()),
                () -> assertEquals(requestModel.getCompany(), actualResult.getData().getCompany()),
                () -> assertEquals(requestModel.getDepartment(), actualResult.getData().getDepartment()),
                () -> assertEquals(requestModel.getJobTitle(), actualResult.getData().getJobTitle())
        );
    }

    @Test
    @Tag(SMOKE_TEST)
    @UserStoryId(3623)
    void shouldUpdateAdminProfile() {
        val email = generateRandomEmail();

        val profileCreationRequestModel = AdminProfileRequest
                .builder()
                .adminId(getRandomUuid())
                .email(email)
                .firstName(FakerUtils.firstName)
                .lastName(FakerUtils.lastName)
                .phoneNumber(FakerUtils.phoneNumber)
                .company(FakerUtils.companyName)
                .department(generateRandomString(10))
                .jobTitle(FakerUtils.title)
                .build();

        val actualProfileCreationResult = postAdminProfile(profileCreationRequestModel)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(AdminProfileResponse.class);

        assertAll(
                () -> assertEquals(profileCreationRequestModel.getAdminId(),
                        actualProfileCreationResult.getData().getAdminId()),
                () -> assertEquals(profileCreationRequestModel.getEmail(),
                        actualProfileCreationResult.getData().getEmail()),
                () -> assertEquals(profileCreationRequestModel.getFirstName(),
                        actualProfileCreationResult.getData().getFirstName()),
                () -> assertEquals(profileCreationRequestModel.getLastName(),
                        actualProfileCreationResult.getData().getLastName()),
                () -> assertEquals(profileCreationRequestModel.getPhoneNumber(),
                        actualProfileCreationResult.getData().getPhoneNumber()),
                () -> assertEquals(profileCreationRequestModel.getCompany(),
                        actualProfileCreationResult.getData().getCompany()),
                () -> assertEquals(profileCreationRequestModel.getDepartment(),
                        actualProfileCreationResult.getData().getDepartment()),
                () -> assertEquals(profileCreationRequestModel.getJobTitle(),
                        actualProfileCreationResult.getData().getJobTitle())
        );

        val profileUpdateRequestModel = AdminProfileRequest
                .builder()
                .adminId(profileCreationRequestModel.getAdminId())
                .email(email)
                .firstName(FakerUtils.firstName)
                .lastName(FakerUtils.lastName)
                .phoneNumber(FakerUtils.phoneNumber)
                .company(FakerUtils.companyName)
                .department(generateRandomString(10))
                .jobTitle(FakerUtils.title)
                .build();

        val actualProfileUpdateResult = putAdminProfile(profileUpdateRequestModel)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(AdminProfileResponse.class);

        assertAll(
                () -> assertEquals(profileUpdateRequestModel.getAdminId(),
                        actualProfileUpdateResult.getData().getAdminId()),
                () -> assertEquals(profileUpdateRequestModel.getEmail(),
                        actualProfileUpdateResult.getData().getEmail()),
                () -> assertEquals(profileUpdateRequestModel.getFirstName(),
                        actualProfileUpdateResult.getData().getFirstName()),
                () -> assertEquals(profileUpdateRequestModel.getLastName(),
                        actualProfileUpdateResult.getData().getLastName()),
                () -> assertEquals(profileUpdateRequestModel.getPhoneNumber(),
                        actualProfileUpdateResult.getData().getPhoneNumber()),
                () -> assertEquals(profileUpdateRequestModel.getCompany(),
                        actualProfileUpdateResult.getData().getCompany()),
                () -> assertEquals(profileUpdateRequestModel.getDepartment(),
                        actualProfileUpdateResult.getData().getDepartment()),
                () -> assertEquals(profileUpdateRequestModel.getJobTitle(),
                        actualProfileUpdateResult.getData().getJobTitle())
        );
    }

    @Test
    @Tag(SMOKE_TEST)
    @UserStoryId(3623)
    void shouldDeleteAdminProfile() {
        val actualAdminProfileCreationResult = postAdminProfile(AdminProfileRequest
                .builder()
                .adminId(getRandomUuid())
                .email(generateRandomEmail())
                .firstName(FakerUtils.firstName)
                .lastName(FakerUtils.lastName)
                .phoneNumber(FakerUtils.phoneNumber)
                .company(FakerUtils.companyName)
                .department(generateRandomString(10))
                .jobTitle(FakerUtils.title)
                .build())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(AdminProfileResponse.class);

        deleteAdminProfileById(actualAdminProfileCreationResult.getData().getAdminId())
                .then()
                .assertThat()
                .statusCode(SC_OK);

        val checkingExistenseResult = getAdminProfileById(actualAdminProfileCreationResult.getData().getAdminId())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(AdminProfileResponse.class);

        assertAll(
                () -> assertNull(checkingExistenseResult.getData()),
                () -> assertEquals(AdminProfileErrorCode.ADMIN_PROFILE_DOES_NOT_EXIST,
                        checkingExistenseResult.getErrorCode())
        );
    }
}
