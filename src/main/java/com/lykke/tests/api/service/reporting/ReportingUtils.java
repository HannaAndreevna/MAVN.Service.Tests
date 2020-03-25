package com.lykke.tests.api.service.reporting;

import static com.lykke.api.testing.api.base.RequestHeader.getHeader;
import static com.lykke.api.testing.api.common.QueryParamsUtils.getQueryParams;
import static com.lykke.tests.api.base.Paths.Reporting.REPORT_API_URL;
import static com.lykke.tests.api.base.Paths.Reporting.REPORT_CSV_API_URL;

import com.lykke.api.testing.api.base.RequestHeader;
import com.lykke.api.testing.api.common.QueryParamsUtils;
import com.lykke.tests.api.service.reporting.model.TransactionReportByTimeRequest;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ReportingUtils {

    public Response getReport(TransactionReportByTimeRequest requestModel) {
        return getHeader()
                .queryParams(getQueryParams(requestModel))
                .get(REPORT_API_URL)
                .thenReturn();
    }

    public Response getReportCsv(String from, String to) {
        return getHeader()
                .queryParam("from", from)
                .queryParam("to", to)
                .contentType("text/csv")
                .get(REPORT_CSV_API_URL)
                .thenReturn();
    }
}
