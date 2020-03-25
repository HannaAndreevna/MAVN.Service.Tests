package com.lykke.tests.api.service.adminmanagement;

import static com.lykke.api.testing.api.base.RequestHeader.getHeader;
import static com.lykke.api.testing.api.common.GenerateUtils.generateRandomString;
import static com.lykke.tests.api.base.Paths.AdminManagement.ADMIN_LOGIN_API_PATH;
import static com.lykke.tests.api.base.Paths.AdminManagement.ADMIN_REGISTER_API_PATH;
import static com.lykke.tests.api.base.Paths.AdminManagement.AUTO_FILL_VALUES_API_PATH;
import static com.lykke.tests.api.base.Paths.AdminManagement.GET_PERMISSIONS_API_PATH;
import static com.lykke.tests.api.base.Paths.AdminManagement.UPDATE_API_PATH;
import static com.lykke.tests.api.base.Paths.AdminManagement.UPDATE_PERMISSIONS_API_PATH;
import static org.apache.http.HttpStatus.SC_OK;

import com.lykke.api.testing.api.common.FakerUtils;
import com.lykke.tests.api.service.adminmanagement.model.AdminPermission;
import com.lykke.tests.api.service.adminmanagement.model.AuthenticateRequestModel;
import com.lykke.tests.api.service.adminmanagement.model.AuthenticateResponseModel;
import com.lykke.tests.api.service.adminmanagement.model.GetAdminByIdRequestModel;
import com.lykke.tests.api.service.adminmanagement.model.RegistrationRequestModel;
import com.lykke.tests.api.service.adminmanagement.model.RegistrationResponseModel;
import com.lykke.tests.api.service.adminmanagement.model.UpdateAdminRequestModel;
import com.lykke.tests.api.service.adminmanagement.model.UpdatePermissionsRequestModel;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import lombok.experimental.UtilityClass;

@UtilityClass
public class RegisterAdminUtils {

    public static final String CUSTOMER_ID_FIELD = "Admin.AdminUserId";

    @Step("Register new Admin")
    public static String registerAdmin(String emailAddress, String password, String firstName, String lastName,
            AdminPermission... permissions) {
        return registerAdminWithEmailAndPassword(emailAddress, password, firstName, lastName, permissions)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .path(CUSTOMER_ID_FIELD);
    }

    public static Response registerAdminWithEmailAndPassword(String emailAddress, String password, String firstName,
            String lastName, AdminPermission... permissions) {
        return getHeader()
                .body(RegistrationRequestModel
                        .builder()
                        .email(emailAddress)
                        .password(password)
                        .firstName(firstName)
                        .lastName(lastName)
                        .company(FakerUtils.companyName)
                        .jobTitle(generateRandomString(10))
                        .department(generateRandomString(10))
                        .phoneNumber(FakerUtils.phoneNumber)
                        .permissions(null == permissions ? new AdminPermission[]{} : permissions)
                        .build())
                .post(ADMIN_REGISTER_API_PATH);
    }

    public static Response updateAdmin(UpdateAdminRequestModel requestModel) {
        return getHeader()
                .body(requestModel)
                .post(UPDATE_API_PATH);
    }

    public static Response updateAdmin(UpdatePermissionsRequestModel requestModel) {
        return getHeader()
                .body(requestModel)
                .post(UPDATE_PERMISSIONS_API_PATH);
    }

    public static Response getAdminPermissions(GetAdminByIdRequestModel requestModel) {
        return getHeader()
                .body(requestModel)
                .post(GET_PERMISSIONS_API_PATH);
    }

    @Step("Register new Admin")
    public RegistrationResponseModel registerAdmin(RegistrationRequestModel requestModel) {
        return getHeader()
                .body(requestModel)
                .post(ADMIN_REGISTER_API_PATH)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(RegistrationResponseModel.class);
    }

    public AuthenticateResponseModel loginAdmin(AuthenticateRequestModel requestModel) {
        return getHeader()
                .body(requestModel)
                .post(ADMIN_LOGIN_API_PATH)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(AuthenticateResponseModel.class);
    }

    public Response getAutofillValues() {
        return getHeader()
                .get(AUTO_FILL_VALUES_API_PATH)
                .thenReturn();
    }
}
