package com.lykke.tests.api.service.admin;

import static com.lykke.tests.api.base.Paths.AdminApi.ADMINS_API_PATH;
import static com.lykke.api.testing.api.base.RequestHeader.getHeader;
import static com.lykke.tests.api.common.PaginationConts.CURRENT_PAGE_LOWER_BOUNDARY;
import static com.lykke.tests.api.common.PaginationConts.PAGE_SIZE_UPPER_BOUNDARY;
import static com.lykke.tests.api.service.admin.LoginAdminUtils.getAdminToken;
import static org.apache.http.HttpStatus.SC_OK;

import com.lykke.tests.api.base.PathConsts.AdminApiService;
import com.lykke.tests.api.service.admin.model.AdminListRequest;
import io.restassured.response.Response;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ReturnAListOfAdminUsersUtils {

    public static final String EMAIL_FIELD = "Email";

    Response getAdminUsers(String token) {
        return getHeader(token)
                .body(AdminListRequest
                        .builder()
                        .pageSize(PAGE_SIZE_UPPER_BOUNDARY)
                        .currentPage(CURRENT_PAGE_LOWER_BOUNDARY)
                        .build())
                .post(ADMINS_API_PATH + AdminApiService.SEARCH_PATH.getPath());
    }

    public int getTotalNumberOfAdminUsers() {
        return getAdminUsers(getAdminToken())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .jsonPath()
                .getInt("TotalCount");
    }
}
