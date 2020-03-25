package com.lykke.tests.api.service.admin;

import static com.lykke.tests.api.base.Paths.AdminApi.ADMINS_API_PATH;
import static com.lykke.api.testing.api.base.RequestHeader.getHeader;
import static com.lykke.api.testing.api.common.QueryParamsUtils.getQueryParams;
import static com.lykke.tests.api.common.PaginationConts.CURRENT_PAGE_LOWER_BOUNDARY;
import static com.lykke.tests.api.common.PaginationConts.PAGE_SIZE_LOWER_BOUNDARY;
import static com.lykke.tests.api.service.admin.LoginAdminUtils.getAdminToken;

import com.lykke.tests.api.base.PathConsts.AdminApiService;
import com.lykke.tests.api.service.admin.model.AdminListRequest;
import com.lykke.tests.api.service.admin.model.CustomerListRequest;
import com.lykke.tests.api.service.admin.model.ValidationErrorResponse;
import com.lykke.tests.api.service.admin.model.admins.AdminListResponse;
import lombok.experimental.UtilityClass;
import lombok.val;

@UtilityClass
public class GetAdminsUtils {

    public ValidationErrorResponse getAdminsPaginatedValidationResponse(CustomerListRequest requestObject) {
        return getHeader(getAdminToken())
                .queryParams(getQueryParams(requestObject))
                .get(ADMINS_API_PATH)
                .then()
                .assertThat()
                .statusCode(requestObject.getHttpStatus())
                .extract()
                .as(ValidationErrorResponse.class);
    }

    public AdminListResponse getAdminsPaginatedResponse(AdminListRequest requestObject) {
        return getHeader(getAdminToken())
                .body(requestObject)
                .post(ADMINS_API_PATH + AdminApiService.SEARCH_PATH.getPath())
                .then()
                .assertThat()
                .statusCode(requestObject.getHttpStatus())
                .extract()
                .as(AdminListResponse.class);
    }

    public static String getAdminUserId(String email) {
        val requestObject = AdminListRequest
                .builder()
                .searchValue(email)
                .currentPage(CURRENT_PAGE_LOWER_BOUNDARY)
                .pageSize(PAGE_SIZE_LOWER_BOUNDARY)
                .build();

        val response = getAdminsPaginatedResponse(requestObject);

        return response.getItems()[0].getId();
    }
}
