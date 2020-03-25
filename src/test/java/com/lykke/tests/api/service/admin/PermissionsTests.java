package com.lykke.tests.api.service.admin;

import static com.lykke.api.testing.api.base.RequestHeader.getHeader;
import static com.lykke.api.testing.api.common.GenerateUtils.generateRandomEmail;
import static com.lykke.api.testing.api.common.GenerateUtils.generateRandomString;
import static com.lykke.api.testing.api.common.PasswordGen.generateValidPassword;
import static com.lykke.tests.api.base.PathConsts.ComponentBaseUrl.ADMIN_API_COMPONENT_URL;
import static com.lykke.tests.api.base.PathConsts.getBaseUrl;
import static com.lykke.tests.api.service.admin.LoginAdminUtils.getTokenForAdminUser;
import static com.lykke.tests.api.service.adminmanagement.RegisterAdminUtils.registerAdmin;
import static com.lykke.tests.api.service.adminmanagement.RegisterAdminUtils.updateAdmin;
import static java.util.stream.Collectors.toList;
import static org.apache.http.HttpStatus.SC_FORBIDDEN;
import static org.apache.http.HttpStatus.SC_OK;

import com.lykke.api.testing.annotations.UserStoryId;
import com.lykke.api.testing.api.common.FakerUtils;
import com.lykke.tests.api.base.BaseApiTest;
import com.lykke.tests.api.common.Urls.AdminApi.Admins;
import com.lykke.tests.api.common.Urls.AdminApi.Blockchain;
import com.lykke.tests.api.common.Urls.AdminApi.BonusTypes;
import com.lykke.tests.api.common.Urls.AdminApi.BurnRules;
import com.lykke.tests.api.common.Urls.AdminApi.Customers;
import com.lykke.tests.api.common.Urls.AdminApi.Dashboard;
import com.lykke.tests.api.common.Urls.AdminApi.EarnRules;
import com.lykke.tests.api.common.Urls.AdminApi.Partners;
import com.lykke.tests.api.common.Urls.AdminApi.Reports;
import com.lykke.tests.api.common.Urls.AdminApi.Settings;
import com.lykke.tests.api.common.Urls.AdminApi.Statistics;
import com.lykke.tests.api.service.admin.model.permissions.PermissionType;
import com.lykke.tests.api.service.adminmanagement.model.AdminPermission;
import com.lykke.tests.api.service.adminmanagement.model.AdminPermissionLevel;
import com.lykke.tests.api.service.adminmanagement.model.RegistrationRequestModel;
import com.lykke.tests.api.service.adminmanagement.model.RegistrationResponseModel;
import com.lykke.tests.api.service.adminmanagement.model.UpdatePermissionsRequestModel;
import java.util.Arrays;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

public class PermissionsTests extends BaseApiTest {

    private RegistrationResponseModel admin;
    private String adminPassword;

    @BeforeEach
    void setUpAdmin() {
        // create admin with full set of permissions
        adminPassword = generateValidPassword();
        admin = registerAdmin(RegistrationRequestModel
                .builder()
                .email(generateRandomEmail())
                .password(adminPassword)
                .firstName(FakerUtils.firstName)
                .lastName(FakerUtils.lastName)
                .phoneNumber(FakerUtils.phoneNumber)
                .company(FakerUtils.companyName)
                .department(generateRandomString(10))
                .jobTitle(generateRandomString(10))
                .permissions(new AdminPermission[]{
                        AdminPermission
                                .builder()
                                .type(PermissionType.ACTION_RULES.getType())
                                .level(AdminPermissionLevel.EDIT)
                                .build(),
                        AdminPermission
                                .builder()
                                .type(PermissionType.ADMIN_USERS.getType())
                                .level(AdminPermissionLevel.EDIT)
                                .build(),
                        AdminPermission
                                .builder()
                                .type(PermissionType.BLOCKCHAIN_OPERATIONS.getType())
                                .level(AdminPermissionLevel.EDIT)
                                .build(),
                        AdminPermission
                                .builder()
                                .type(PermissionType.CUSTOMERS.getType())
                                .level(AdminPermissionLevel.EDIT)
                                .build(),
                        AdminPermission
                                .builder()
                                .type(PermissionType.DASHBOARD.getType())
                                .level(AdminPermissionLevel.EDIT)
                                .build(),
                        AdminPermission
                                .builder()
                                .type(PermissionType.PROGRAM_PARTNERS.getType())
                                .level(AdminPermissionLevel.EDIT)
                                .build(),
                        AdminPermission
                                .builder()
                                .type(PermissionType.REPORTS.getType())
                                .level(AdminPermissionLevel.EDIT)
                                .build(),
                        AdminPermission
                                .builder()
                                .type(PermissionType.SETTINGS.getType())
                                .level(AdminPermissionLevel.EDIT)
                                .build()
                })
                .build());
    }

    @ParameterizedTest
    @EnumSource(Admins.class)
    @UserStoryId(storyId = {3505, 3868})
        // checks PermissionType, level is always Edit
    void shouldGetForbiddenOnWrongPermissionAdminsCtrl(Admins urlData) {
        removePermissionSpecified(urlData.getPermissionType());
        val adminToken = getTokenForAdminUser(admin.getAdmin().getEmail(), adminPassword);

        val response = urlData.getVerbAction().getVerbAction()
                .apply(getHeader(adminToken), getBaseUrl(ADMIN_API_COMPONENT_URL) + urlData.getPath()).thenReturn();
        response
                .then()
                .assertThat()
                .statusCode(SC_FORBIDDEN);
    }

    @ParameterizedTest
    @EnumSource(Blockchain.class)
    @UserStoryId(storyId = {3505, 3868})
        // checks PermissionType, level is always Edit
    void shouldGetForbiddenOnWrongPermissionBlockchainCtrl(Blockchain urlData) {
        removePermissionSpecified(urlData.getPermissionType());
        val adminToken = getTokenForAdminUser(admin.getAdmin().getEmail(), adminPassword);

        val response = urlData.getVerbAction().getVerbAction()
                .apply(getHeader(adminToken), getBaseUrl(ADMIN_API_COMPONENT_URL) + urlData.getPath()).thenReturn();
        response
                .then()
                .assertThat()
                .statusCode(SC_FORBIDDEN);
    }

    @ParameterizedTest
    @EnumSource(BonusTypes.class)
    @UserStoryId(storyId = {3505, 3868})
        // checks PermissionType, level is always Edit
    void shouldGetForbiddenOnWrongPermissionBonusTypesCtrl(BonusTypes urlData) {
        removePermissionSpecified(urlData.getPermissionType());
        val adminToken = getTokenForAdminUser(admin.getAdmin().getEmail(), adminPassword);

        val response = urlData.getVerbAction().getVerbAction()
                .apply(getHeader(adminToken), getBaseUrl(ADMIN_API_COMPONENT_URL) + urlData.getPath()).thenReturn();
        response
                .then()
                .assertThat()
                .statusCode(SC_FORBIDDEN);
    }

    @ParameterizedTest
    @EnumSource(BurnRules.class)
    @UserStoryId(storyId = {3505, 3868})
        // checks PermissionType, level is always Edit
    void shouldGetForbiddenOnWrongPermissionBurnRulesCtrl(BurnRules urlData) {
        removePermissionSpecified(urlData.getPermissionType());
        val adminToken = getTokenForAdminUser(admin.getAdmin().getEmail(), adminPassword);

        val response = urlData.getVerbAction().getVerbAction()
                .apply(getHeader(adminToken), getBaseUrl(ADMIN_API_COMPONENT_URL) + urlData.getPath()).thenReturn();
        response
                .then()
                .assertThat()
                .statusCode(SC_FORBIDDEN);
    }

    @ParameterizedTest
    @EnumSource(Customers.class)
    @UserStoryId(storyId = {3505, 3868})
        // checks PermissionType, level is always Edit
    void shouldGetForbiddenOnWrongPermissionCustomersCtrl(Customers urlData) {
        removePermissionSpecified(urlData.getPermissionType());
        val adminToken = getTokenForAdminUser(admin.getAdmin().getEmail(), adminPassword);

        val response = urlData.getVerbAction().getVerbAction()
                .apply(getHeader(adminToken), getBaseUrl(ADMIN_API_COMPONENT_URL) + urlData.getPath()).thenReturn();
        response
                .then()
                .assertThat()
                .statusCode(SC_FORBIDDEN);
    }

    @ParameterizedTest
    @EnumSource(Dashboard.class)
    @UserStoryId(storyId = {3505, 3868})
        // checks PermissionType, level is always Edit
    void shouldGetForbiddenOnWrongPermissionDashboardCtrl(Dashboard urlData) {
        removePermissionSpecified(urlData.getPermissionType());
        val adminToken = getTokenForAdminUser(admin.getAdmin().getEmail(), adminPassword);

        val response = urlData.getVerbAction().getVerbAction()
                .apply(getHeader(adminToken), getBaseUrl(ADMIN_API_COMPONENT_URL) + urlData.getPath()).thenReturn();
        response
                .then()
                .assertThat()
                .statusCode(SC_FORBIDDEN);
    }

    @ParameterizedTest
    @EnumSource(EarnRules.class)
    @UserStoryId(storyId = {3505, 3868})
        // checks PermissionType, level is always Edit
    void shouldGetForbiddenOnWrongPermissionEarnRulesCtrl(EarnRules urlData) {
        removePermissionSpecified(urlData.getPermissionType());
        val adminToken = getTokenForAdminUser(admin.getAdmin().getEmail(), adminPassword);

        val response = urlData.getVerbAction().getVerbAction()
                .apply(getHeader(adminToken), getBaseUrl(ADMIN_API_COMPONENT_URL) + urlData.getPath()).thenReturn();
        response
                .then()
                .assertThat()
                .statusCode(SC_FORBIDDEN);
    }

    @ParameterizedTest
    @EnumSource(Partners.class)
    @UserStoryId(storyId = {3505, 3868})
        // checks PermissionType, level is always Edit
    void shouldGetForbiddenOnWrongPermissionPartnersCtrl(Partners urlData) {
        removePermissionSpecified(urlData.getPermissionType());
        val adminToken = getTokenForAdminUser(admin.getAdmin().getEmail(), adminPassword);

        val response = urlData.getVerbAction().getVerbAction()
                .apply(getHeader(adminToken), getBaseUrl(ADMIN_API_COMPONENT_URL) + urlData.getPath()).thenReturn();
        // there is an exclusion for Web UI
        response
                .then()
                .assertThat()
                .statusCode(SC_FORBIDDEN);
    }

    @ParameterizedTest
    @EnumSource(Reports.class)
    @UserStoryId(storyId = {3505, 3868})
        // checks PermissionType, level is always Edit
    void shouldGetForbiddenOnWrongPermissionReportsCtrl(Reports urlData) {
        removePermissionSpecified(urlData.getPermissionType());
        val adminToken = getTokenForAdminUser(admin.getAdmin().getEmail(), adminPassword);

        val response = urlData.getVerbAction().getVerbAction()
                .apply(getHeader(adminToken), getBaseUrl(ADMIN_API_COMPONENT_URL) + urlData.getPath()).thenReturn();
        response
                .then()
                .assertThat()
                .statusCode(SC_FORBIDDEN);
    }

    @ParameterizedTest
    @EnumSource(Settings.class)
    @UserStoryId(storyId = {3505, 3868})
        // checks PermissionType, level is always Edit
    void shouldGetForbiddenOnWrongPermissionSettingsCtrl(Settings urlData) {
        removePermissionSpecified(urlData.getPermissionType());
        val adminToken = getTokenForAdminUser(admin.getAdmin().getEmail(), adminPassword);

        val response = urlData.getVerbAction().getVerbAction()
                .apply(getHeader(adminToken), getBaseUrl(ADMIN_API_COMPONENT_URL) + urlData.getPath()).thenReturn();
        response
                .then()
                .assertThat()
                .statusCode(SC_FORBIDDEN);
    }

    @ParameterizedTest
    @EnumSource(Statistics.class)
    @UserStoryId(storyId = {3505, 3868})
        // checks PermissionType, level is always Edit
    void shouldGetForbiddenOnWrongPermissionStatisticsCtrl(Statistics urlData) {
        removePermissionSpecified(urlData.getPermissionType());
        val adminToken = getTokenForAdminUser(admin.getAdmin().getEmail(), adminPassword);

        val response = urlData.getVerbAction().getVerbAction()
                .apply(getHeader(adminToken), getBaseUrl(ADMIN_API_COMPONENT_URL) + urlData.getPath()).thenReturn();
        response
                .then()
                .assertThat()
                .statusCode(SC_FORBIDDEN);
    }

    private void removePermissionSpecified(PermissionType permissionType) {
        val fullPermissions = admin.getAdmin().getPermissions();
        val lessPermissions = Arrays.stream(fullPermissions)
                .filter(perm -> !perm.getType().equalsIgnoreCase(permissionType.getType()))
                .collect(toList())
                .toArray(new AdminPermission[]{});
        updateAdmin(UpdatePermissionsRequestModel
                .builder()
                .permissions(lessPermissions)
                .adminUserId(admin.getAdmin().getAdminUserId())
                .build())
                .then()

                ////55
                .log().all()

                .assertThat()
                .statusCode(SC_OK);
        //   .extract()
        //   .as(updatead)
    }
}
