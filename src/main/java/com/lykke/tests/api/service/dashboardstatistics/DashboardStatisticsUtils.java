package com.lykke.tests.api.service.dashboardstatistics;

import static com.lykke.api.testing.api.base.RequestHeader.getHeader;
import static com.lykke.api.testing.api.common.QueryParamsUtils.getQueryParams;
import static com.lykke.tests.api.base.Paths.DashboardStatistics.CUSTOMERS_API_PATH;
import static com.lykke.tests.api.base.Paths.DashboardStatistics.LEADS_API_PATH;
import static com.lykke.tests.api.base.Paths.DashboardStatistics.TOKENS_API_PATH;

import com.lykke.tests.api.service.dashboardstatistics.models.DashboardStatisticsRequest;
import com.lykke.tests.api.service.dashboardstatistics.models.ValidationErrorDashboardStatisticsResponse;
import com.lykke.tests.api.service.dashboardstatistics.models.customers.CustomersListResponseModel;
import com.lykke.tests.api.service.dashboardstatistics.models.leads.LeadsListResponseModel;
import com.lykke.tests.api.service.dashboardstatistics.models.tokens.TokensListResponseModel;
import lombok.experimental.UtilityClass;

@UtilityClass
public class DashboardStatisticsUtils {

    public CustomersListResponseModel getCustomersList(DashboardStatisticsRequest dashboardStatisticsRequest) {
        return getHeader()
                .queryParams(getQueryParams(dashboardStatisticsRequest))
                .get(CUSTOMERS_API_PATH)
                .then()
                .assertThat()
                .statusCode(dashboardStatisticsRequest.getHttpStatus())
                .extract()
                .as(CustomersListResponseModel.class);
    }

    public LeadsListResponseModel getLeadsList(DashboardStatisticsRequest dashboardStatisticsRequest) {
        return getHeader()
                .queryParams(getQueryParams(dashboardStatisticsRequest))
                .get(LEADS_API_PATH)
                .then()
                .assertThat()
                .statusCode(dashboardStatisticsRequest.getHttpStatus())
                .extract()
                .as(LeadsListResponseModel.class);
    }

    public TokensListResponseModel getTokensList(DashboardStatisticsRequest dashboardStatisticsRequest) {
        return getHeader()
                .queryParams(getQueryParams(dashboardStatisticsRequest))
                .get(TOKENS_API_PATH)
                .then()
                .assertThat()
                .statusCode(dashboardStatisticsRequest.getHttpStatus())
                .extract()
                .as(TokensListResponseModel.class);
    }

    public ValidationErrorDashboardStatisticsResponse getCustomersListValidationResponse(
            DashboardStatisticsRequest dashboardStatisticsRequest) {
        return getHeader()
                .queryParams(getQueryParams(dashboardStatisticsRequest))
                .get(CUSTOMERS_API_PATH)
                .then()
                .assertThat()
                .statusCode(dashboardStatisticsRequest.getHttpStatus())
                .extract()
                .as(ValidationErrorDashboardStatisticsResponse.class);
    }

    public ValidationErrorDashboardStatisticsResponse getLeadsListValidationResponse(
            DashboardStatisticsRequest dashboardStatisticsRequest) {
        return getHeader()
                .queryParams(getQueryParams(dashboardStatisticsRequest))
                .get(LEADS_API_PATH)
                .then()
                .assertThat()
                .statusCode(dashboardStatisticsRequest.getHttpStatus())
                .extract()
                .as(ValidationErrorDashboardStatisticsResponse.class);
    }

    public ValidationErrorDashboardStatisticsResponse getTokensListValidationResponse(
            DashboardStatisticsRequest dashboardStatisticsRequest) {
        return getHeader()
                .queryParams(getQueryParams(dashboardStatisticsRequest))
                .get(TOKENS_API_PATH)
                .then()
                .assertThat()
                .statusCode(dashboardStatisticsRequest.getHttpStatus())
                .extract()
                .as(ValidationErrorDashboardStatisticsResponse.class);
    }

}
