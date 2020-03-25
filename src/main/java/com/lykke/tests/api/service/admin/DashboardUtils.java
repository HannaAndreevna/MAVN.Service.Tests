package com.lykke.tests.api.service.admin;

import static com.lykke.api.testing.api.base.RequestHeader.getHeader;
import static com.lykke.api.testing.api.common.QueryParamsUtils.getQueryParams;
import static com.lykke.tests.api.base.PathConsts.AdminApiService.CUSTOMERS_PATH;
import static com.lykke.tests.api.base.PathConsts.AdminApiService.LEADS_PATH;
import static com.lykke.tests.api.base.PathConsts.AdminApiService.TOKENS_PATH;
import static com.lykke.tests.api.base.Paths.AdminApi.ADMIN_DASHBOARD_STATISTICS_API_PATH;
import static com.lykke.tests.api.service.admin.LoginAdminUtils.getAdminToken;

import com.lykke.tests.api.service.admin.model.dashboard.DashboardStatisticsRequest;
import com.lykke.tests.api.service.admin.model.dashboard.ValidationErrorDashboardStatisticsResponse;
import com.lykke.tests.api.service.admin.model.dashboard.customerstatistics.CustomersStatisticResponse;
import com.lykke.tests.api.service.admin.model.dashboard.leadstatistics.LeadsListResponse;
import com.lykke.tests.api.service.admin.model.dashboard.tokenstatistics.TokensListResponse;
import io.qameta.allure.Step;
import lombok.experimental.UtilityClass;

@UtilityClass
public class DashboardUtils {

    @Step("Get Leads Statistics")
    public LeadsListResponse getLeadsStatistics(DashboardStatisticsRequest dashboardStatisticsRequest, String token) {
        return getHeader(token)
                .queryParams(getQueryParams(dashboardStatisticsRequest))
                .get(ADMIN_DASHBOARD_STATISTICS_API_PATH + LEADS_PATH.getPath())
                .then()
                .assertThat()
                .statusCode(dashboardStatisticsRequest.getHttpStatus())
                .extract()
                .as(LeadsListResponse.class);
    }

    public ValidationErrorDashboardStatisticsResponse getLeadsListValidationResponse(
            DashboardStatisticsRequest dashboardStatisticsRequest) {
        return getHeader()
                .queryParams(getQueryParams(dashboardStatisticsRequest))
                .get(ADMIN_DASHBOARD_STATISTICS_API_PATH + LEADS_PATH.getPath())
                .then()
                .assertThat()
                .statusCode(dashboardStatisticsRequest.getHttpStatus())
                .extract()
                .as(ValidationErrorDashboardStatisticsResponse.class);
    }

    @Step("Get Tokens Statistics")
    public TokensListResponse getTokensStatistics(DashboardStatisticsRequest dashboardStatisticsRequest, String token) {
        return getHeader(token)
                .queryParams(getQueryParams(dashboardStatisticsRequest))
                .get(ADMIN_DASHBOARD_STATISTICS_API_PATH + TOKENS_PATH.getPath())
                .then()
                .assertThat()
                .statusCode(dashboardStatisticsRequest.getHttpStatus())
                .extract()
                .as(TokensListResponse.class);
    }

    public ValidationErrorDashboardStatisticsResponse getTokensListValidationResponse(
            DashboardStatisticsRequest dashboardStatisticsRequest) {
        return getHeader()
                .queryParams(getQueryParams(dashboardStatisticsRequest))
                .get(ADMIN_DASHBOARD_STATISTICS_API_PATH + TOKENS_PATH.getPath())
                .then()
                .assertThat()
                .statusCode(dashboardStatisticsRequest.getHttpStatus())
                .extract()
                .as(ValidationErrorDashboardStatisticsResponse.class);
    }

    public CustomersStatisticResponse getCustomersStatistics(DashboardStatisticsRequest dashboardStatisticsRequest,
            String token) {
        return getHeader(token)
                .queryParams(getQueryParams(dashboardStatisticsRequest))
                .get(ADMIN_DASHBOARD_STATISTICS_API_PATH + CUSTOMERS_PATH.getPath())
                .then()
                .assertThat()
                .statusCode(dashboardStatisticsRequest.getHttpStatus())
                .extract()
                .as(CustomersStatisticResponse.class);
    }

    public ValidationErrorDashboardStatisticsResponse getCustomerListValidationResponse(
            DashboardStatisticsRequest dashboardStatisticsRequest) {
        return getHeader(getAdminToken())
                .queryParams(getQueryParams(dashboardStatisticsRequest))
                .get(ADMIN_DASHBOARD_STATISTICS_API_PATH + CUSTOMERS_PATH.getPath())
                .then()
                .assertThat()
                .statusCode(dashboardStatisticsRequest.getHttpStatus())
                .extract()
                .as(ValidationErrorDashboardStatisticsResponse.class);
    }
}
