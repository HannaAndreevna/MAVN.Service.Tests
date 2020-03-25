package com.lykke.tests.api.service.admin;

import static com.lykke.api.testing.api.base.RequestHeader.getHeader;
import static com.lykke.tests.api.base.Paths.AdminApi.ADMINS_SEARCH_API_PATH;
import static com.lykke.tests.api.base.Paths.AdminApi.CUSTOMERS_SEARCH_API_PATH;

import com.lykke.tests.api.service.admin.model.AdminListRequest;
import com.lykke.tests.api.service.admin.model.CustomerListRequest;
import io.restassured.response.Response;
import lombok.experimental.UtilityClass;

@UtilityClass
public class SearchUtils {

    Response postCustomersSearch(CustomerListRequest requestModel, String token) {
        return getHeader(token)
                .body(requestModel)
                .post(CUSTOMERS_SEARCH_API_PATH)
                .thenReturn();
    }

    Response postAdminsSearch(AdminListRequest requestModel, String token) {
        return getHeader(token)
                .body(requestModel)
                .post(ADMINS_SEARCH_API_PATH)
                .thenReturn();
    }
}
