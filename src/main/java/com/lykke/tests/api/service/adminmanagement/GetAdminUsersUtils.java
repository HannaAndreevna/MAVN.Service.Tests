package com.lykke.tests.api.service.adminmanagement;

import static com.lykke.api.testing.api.base.RequestHeader.getHeader;
import static com.lykke.tests.api.base.PathConsts.AdminManagementService.PAGINATED_PATH;
import static com.lykke.tests.api.base.Paths.ADMIN_MANAGEMENT_API_PATH;
import static com.lykke.tests.api.base.Paths.AdminManagement.ADMIN_BY_EMAIL_API_PATH;
import static com.lykke.tests.api.base.Paths.AdminManagement.GET_ADMIN_USERSP_API_PATH;

import com.lykke.tests.api.service.adminmanagement.model.AdminUsersPaginated;
import com.lykke.tests.api.service.adminmanagement.model.GetByEmailRequestModel;
import io.restassured.response.Response;
import lombok.experimental.UtilityClass;
import org.apache.http.HttpStatus;

@UtilityClass
public class GetAdminUsersUtils {

    private static final String EMAIL_PARAM_NAME = "email";

    public Response getAdminUsers() {
        return getHeader()
                .get(GET_ADMIN_USERSP_API_PATH);
    }

    Response getAdminUsersPaginated(int currentPage, int pageSize) {
        return getHeader()
                .body(adminUsersPaginatedObject(currentPage, pageSize))
                .post(ADMIN_MANAGEMENT_API_PATH + PAGINATED_PATH.getPath());
    }

    int getAdminUsersTotalCount() {
        return getAdminUsers()
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .path("AdminUsers.size()");
    }

    Response getAdminUserByEmail(GetByEmailRequestModel requestModel) {
        return getHeader()
                .body(requestModel)
                .post(ADMIN_BY_EMAIL_API_PATH);
    }

    private static AdminUsersPaginated adminUsersPaginatedObject(int currentPage, int pageSize) {
        return AdminUsersPaginated
                .builder()
                .currentPage(currentPage)
                .pageSize(pageSize)
                .build();
    }
}
