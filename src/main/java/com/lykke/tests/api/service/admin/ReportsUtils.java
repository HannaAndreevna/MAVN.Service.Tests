package com.lykke.tests.api.service.admin;

import static com.lykke.tests.api.base.Paths.AdminApi.REPORTS_API_PATH;

import com.lykke.api.testing.api.base.RequestHeader;
import com.lykke.tests.api.service.admin.model.reports.ReportRequestModel;
import io.restassured.response.Response;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ReportsUtils {

    public Response getReports(ReportRequestModel requestModel, String token) {
        return RequestHeader.getHeader(token)
                .body(requestModel)
                .post(REPORTS_API_PATH)
                .thenReturn();
    }
}
