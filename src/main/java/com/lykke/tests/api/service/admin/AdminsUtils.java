package com.lykke.tests.api.service.admin;

import static com.lykke.api.testing.api.base.RequestHeader.getHeader;
import static com.lykke.api.testing.api.common.GenerateUtils.generateRandomEmail;
import static com.lykke.api.testing.api.common.GenerateUtils.generateRandomString;
import static com.lykke.api.testing.api.common.PasswordGen.generateValidPassword;
import static com.lykke.api.testing.api.common.QueryParamsUtils.getQueryParams;
import static com.lykke.tests.api.base.Paths.AdminApi.ADMINS_API_PATH;
import static com.lykke.tests.api.base.Paths.AdminApi.AUTOFILL_DATA_API_PATH;
import static com.lykke.tests.api.base.Paths.AdminApi.GENERATE_SUGGESTED_PASSWORD_API_PATH;
import static com.lykke.tests.api.service.admin.LoginAdminUtils.getAdminToken;
import static org.apache.http.HttpStatus.SC_OK;

import com.lykke.api.testing.api.common.FakerUtils;
import com.lykke.tests.api.service.admin.model.AdminListRequest;
import com.lykke.tests.api.service.admin.model.admins.AdminCreateModel;
import com.lykke.tests.api.service.admin.model.admins.AdminModel;
import com.lykke.tests.api.service.admin.model.admins.GeneratedPasswordModel;
import io.restassured.response.Response;
import lombok.experimental.UtilityClass;

@UtilityClass
public class AdminsUtils {

    public Response postAdmin(AdminCreateModel requestModel) {
        return getHeader(getAdminToken())
                .body(requestModel)
                .post(ADMINS_API_PATH)
                .thenReturn();
    }

    public AdminModel createDefaultAdmin() {
        return getHeader(getAdminToken())
                .body(AdminCreateModel
                        .adminCreateModelBuilder()
                        .email(generateRandomEmail())
                        .password(generateValidPassword())
                        .firstName(FakerUtils.firstName)
                        .lastName(FakerUtils.lastName)
                        .phoneNumber(FakerUtils.phoneNumber)
                        .company(FakerUtils.companyName)
                        .department(generateRandomString(10))
                        .jobTitle(generateRandomString(10))
                        .build())
                .post(ADMINS_API_PATH)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(AdminModel.class);
    }

    public Response generateSugggestedPassword() {
        return getHeader(getAdminToken())
                .get(GENERATE_SUGGESTED_PASSWORD_API_PATH)
                .thenReturn();
    }

    public String generateAdminPassword() {
        return getHeader(getAdminToken())
                .get(GENERATE_SUGGESTED_PASSWORD_API_PATH)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(GeneratedPasswordModel.class)
                .getPassword();
    }

    public Response getAdmin(AdminListRequest requestModel) {
        return getHeader(getAdminToken())
                .queryParams(getQueryParams(requestModel))
                .get(ADMINS_API_PATH)
                .thenReturn();
    }

    public Response getAutofillData() {
        return getHeader(getAdminToken())
                .get(AUTOFILL_DATA_API_PATH)
                .thenReturn();
    }
}
