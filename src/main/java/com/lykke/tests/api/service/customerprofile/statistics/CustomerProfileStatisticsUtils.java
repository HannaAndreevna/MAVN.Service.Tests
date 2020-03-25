package com.lykke.tests.api.service.customerprofile.statistics;

import static com.lykke.api.testing.api.base.RequestHeader.getHeaderWithKey;
import static com.lykke.api.testing.api.common.QueryParamsUtils.getQueryParams;
import static com.lykke.tests.api.base.Paths.CustomerProfile.CUSTOMER_PROFILE_STATISTICS_API_PATH;
import static com.lykke.tests.api.common.CommonConsts.API_KEY;

import com.lykke.tests.api.service.customerprofile.statistics.model.CustomerStatiscticsRequest;
import com.lykke.tests.api.service.customerprofile.statistics.model.CustomerStatisticsResponse;
import com.lykke.tests.api.service.customerprofile.statistics.model.ValidationErrorResponse;
import lombok.experimental.UtilityClass;

@UtilityClass
public class CustomerProfileStatisticsUtils {

    private static final String START_DATE_FIELD = "startDate";
    private static final String END_DATE_FIELD = "endDate";

    public static CustomerStatisticsResponse getCustomerProfileStatistics(CustomerStatiscticsRequest requestObject) {
        return getHeaderWithKey(API_KEY)
                .queryParams(getQueryParams(requestObject))
                .get(CUSTOMER_PROFILE_STATISTICS_API_PATH)
                .then()
                .assertThat()
                .statusCode(requestObject.getHttpStatus())
                .extract()
                .as(CustomerStatisticsResponse.class);
    }

    public static ValidationErrorResponse getCustomerProfileStatisticsValidationResponse(
            CustomerStatiscticsRequest requestObject) {
        return getHeaderWithKey(API_KEY)
                .queryParams(getQueryParams(requestObject))
                .get(CUSTOMER_PROFILE_STATISTICS_API_PATH)
                .then()
                .assertThat()
                .statusCode(requestObject.getHttpStatus())
                .extract()
                .as(ValidationErrorResponse.class);
    }
}
