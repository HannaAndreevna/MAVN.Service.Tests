package com.lykke.tests.api.service.adminmanagement;

import static com.lykke.api.testing.api.common.GenerateUtils.generateRandomEmail;
import static com.lykke.api.testing.api.common.GenerateUtils.generateRandomString;
import static com.lykke.api.testing.api.common.PasswordGen.generateValidPassword;
import static com.lykke.tests.api.service.adminmanagement.RegisterAdminUtils.getAdminPermissions;
import static com.lykke.tests.api.service.adminmanagement.RegisterAdminUtils.registerAdmin;
import static com.lykke.tests.api.service.adminmanagement.RegisterAdminUtils.updateAdmin;
import static java.util.stream.Collectors.toList;
import static org.apache.http.HttpStatus.SC_OK;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.lykke.api.testing.annotations.UserStoryId;
import com.lykke.api.testing.api.common.FakerUtils;
import com.lykke.tests.api.base.BaseApiTest;
import com.lykke.tests.api.service.admin.model.permissions.PermissionType;
import com.lykke.tests.api.service.adminmanagement.model.AdminPermission;
import com.lykke.tests.api.service.adminmanagement.model.AdminPermissionLevel;
import com.lykke.tests.api.service.adminmanagement.model.AdminUserResponseModel;
import com.lykke.tests.api.service.adminmanagement.model.GetAdminByIdRequestModel;
import com.lykke.tests.api.service.adminmanagement.model.RegistrationRequestModel;
import com.lykke.tests.api.service.adminmanagement.model.UpdateAdminRequestModel;
import com.lykke.tests.api.service.adminmanagement.model.UpdatePermissionsRequestModel;
import java.util.Arrays;
import java.util.stream.Stream;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class AdminPermissionsTests extends BaseApiTest {

    static Stream<Arguments> getAllPermissions() {
        return Stream.concat(
                getPermissions(AdminPermissionLevel.EDIT),
                getPermissions(AdminPermissionLevel.VIEW));
    }

    private static Stream<Arguments> getPermissions(AdminPermissionLevel level) {
        return Arrays.stream(PermissionType.values())
                .map(perm -> Arguments.of(AdminPermission
                        .builder()
                        .type(perm.getType())
                        .level(level)
                        .build()))
                .collect(toList()).stream();
    }

    @ParameterizedTest
    @MethodSource("getAllPermissions")
    @UserStoryId(3504)
    void shouldRegisterAdminWithPermissions(AdminPermission permission) {
        val actualResult = registerAdmin(RegistrationRequestModel
                .builder()
                .email(generateRandomEmail())
                .password(generateValidPassword())
                .firstName(FakerUtils.firstName)
                .lastName(FakerUtils.lastName)
                .phoneNumber(FakerUtils.phoneNumber)
                .company(FakerUtils.companyName)
                .department(generateRandomString(10))
                .jobTitle(generateRandomString(10))
                .permissions(new AdminPermission[]{permission})
                .build());

        assertAll(
                () -> assertEquals(permission.getType(), actualResult.getAdmin().getPermissions()[0].getType()),
                () -> assertEquals(permission.getLevel(), actualResult.getAdmin().getPermissions()[0].getLevel())
        );
    }

    @Test
    @UserStoryId(3504)
    void shouldUpdateAdminWithPermissions() {
        val creationResult = registerAdmin(RegistrationRequestModel
                .builder()
                .email(generateRandomEmail())
                .password(generateValidPassword())
                .firstName(FakerUtils.firstName)
                .lastName(FakerUtils.lastName)
                .phoneNumber(FakerUtils.phoneNumber)
                .company(FakerUtils.companyName)
                .department(generateRandomString(10))
                .jobTitle(generateRandomString(10))
                .permissions(new AdminPermission[]{AdminPermission
                        .builder()
                        .level(AdminPermissionLevel.EDIT)
                        .type(PermissionType.CUSTOMERS.getType())
                        .build()})
                .build());

        val actualResult = updateAdmin(UpdateAdminRequestModel
                .builder()
                .adminUserId(creationResult.getAdmin().getAdminUserId())
                .firstName(FakerUtils.firstName)
                .lastName(FakerUtils.lastName)
                .phoneNumber(FakerUtils.phoneNumber)
                .company(FakerUtils.companyName)
                .department(generateRandomString(10))
                .jobTitle(generateRandomString(10))
                .build())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(AdminUserResponseModel.class);

        assertAll(
                () -> assertEquals(creationResult.getAdmin().getPermissions()[0].getType(),
                        actualResult.getProfile().getPermissions()[0].getType()),
                () -> assertEquals(creationResult.getAdmin().getPermissions()[0].getLevel(),
                        actualResult.getProfile().getPermissions()[0].getLevel())
        );
    }

    @ParameterizedTest
    @MethodSource("getAllPermissions")
    @UserStoryId(storyId = {3504, 4361})
    void shouldUpdatePermissions(AdminPermission permission) {
        val expectedCreationResult = AdminPermission
                .builder()
                .type(PermissionType.CUSTOMERS.getType())
                .level(AdminPermissionLevel.VIEW)
                .build();
        val creationResult = registerAdmin(RegistrationRequestModel
                .builder()
                .email(generateRandomEmail())
                .password(generateValidPassword())
                .firstName(FakerUtils.firstName)
                .lastName(FakerUtils.lastName)
                .phoneNumber(FakerUtils.phoneNumber)
                .company(FakerUtils.companyName)
                .department(generateRandomString(10))
                .jobTitle(generateRandomString(10))
                .permissions(new AdminPermission[]{expectedCreationResult})
                .build());

        assertAll(
                () -> assertEquals(expectedCreationResult.getType(),
                        creationResult.getAdmin().getPermissions()[0].getType()),
                () -> assertEquals(expectedCreationResult.getLevel(),
                        creationResult.getAdmin().getPermissions()[0].getLevel())
        );

        val actualUpdatePermissionsResult = updateAdmin(UpdatePermissionsRequestModel
                .builder()
                .adminUserId(creationResult.getAdmin().getAdminUserId())
                .permissions(new AdminPermission[]{permission})
                .build())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(AdminUserResponseModel.class);

        assertAll(
                () -> assertEquals(permission.getType(),
                        actualUpdatePermissionsResult.getProfile().getPermissions()[0].getType()),
                () -> assertEquals(permission.getLevel(),
                        actualUpdatePermissionsResult.getProfile().getPermissions()[0].getLevel())
        );

        // FAL-4361
        val actualGetPermissionsResult = getAdminPermissions(GetAdminByIdRequestModel
                .builder()
                .adminUserId(creationResult.getAdmin().getAdminUserId())
                .build())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(AdminPermission[].class);

        assertAll(
                () -> assertEquals(permission.getType(),
                        actualGetPermissionsResult[0].getType()),
                () -> assertEquals(permission.getLevel(),
                        actualGetPermissionsResult[0].getLevel())
        );
    }
}
