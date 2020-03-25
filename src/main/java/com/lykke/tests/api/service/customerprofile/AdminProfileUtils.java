package com.lykke.tests.api.service.customerprofile;

import static com.lykke.api.testing.api.base.RequestHeader.getHeader;
import static com.lykke.api.testing.api.base.RequestHeader.getHeaderWithKey;
import static com.lykke.api.testing.api.common.GenerateUtils.generateRandomEmail;
import static com.lykke.api.testing.api.common.GenerateUtils.generateRandomString;
import static com.lykke.api.testing.api.common.GenerateUtils.getRandomUuid;
import static com.lykke.api.testing.api.common.QueryParamsUtils.getQueryParams;
import static com.lykke.tests.api.base.Paths.CustomerProfile.ADMIN_PROFILES_API_PATH;
import static com.lykke.tests.api.base.Paths.CustomerProfile.ADMIN_PROFILE_API_PATH;
import static com.lykke.tests.api.common.CommonConsts.API_KEY;
import static com.lykke.tests.api.service.admin.LoginAdminUtils.getAdminToken;
import static org.apache.http.HttpStatus.SC_OK;

import com.lykke.api.testing.api.base.RequestHeader;
import com.lykke.api.testing.api.common.FakerUtils;
import com.lykke.api.testing.api.common.QueryParamsUtils;
import com.lykke.tests.api.base.Paths;
import com.lykke.tests.api.base.Paths.CustomerProfile;
import com.lykke.tests.api.service.customerprofile.model.admins.AdminProfileRequest;
import com.lykke.tests.api.service.customerprofile.model.admins.AdminProfileResponse;
import io.restassured.response.Response;
import lombok.experimental.UtilityClass;
import org.apache.http.HttpStatus;

@UtilityClass
public class AdminProfileUtils {

    public Response getAdminProfilesByIds(String[] adminIds) {
        return getHeaderWithKey(API_KEY)
                .queryParams(getQueryParams(adminIds, "identifiers"))
                .get(ADMIN_PROFILES_API_PATH)
                .thenReturn();
    }

    public Response getAdminProfileById(String adminId) {
        return getHeaderWithKey(API_KEY)
                .get(ADMIN_PROFILE_API_PATH.apply(adminId))
                .thenReturn();
    }

    public Response postAdminProfile(AdminProfileRequest requestModel) {
        return getHeaderWithKey(API_KEY)
                .body(requestModel)
                .post(ADMIN_PROFILES_API_PATH)
                .thenReturn();
    }

    public Response putAdminProfile(AdminProfileRequest requestModel) {
        return getHeaderWithKey(API_KEY)
                .body(requestModel)
                .put(ADMIN_PROFILES_API_PATH)
                .thenReturn();
    }

    public Response deleteAdminProfileById(String adminId) {
        return getHeaderWithKey(API_KEY)
                .delete(ADMIN_PROFILE_API_PATH.apply(adminId))
                .thenReturn();
    }

    public AdminProfileResponse createDefaultAdminProfile() {
        return postAdminProfile(AdminProfileRequest
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
    }
}
