package com.lykke.tests.api.service.bonuscustomerprofile.model;

import static com.lykke.api.testing.api.base.RequestHeader.getHeader;
import static com.lykke.tests.api.base.Paths.CustomerBonusProfile.CUSTOMER_AGGREGATIONS_PATH;
import static org.apache.http.HttpStatus.SC_OK;

import lombok.experimental.UtilityClass;

@UtilityClass
public class AggregationsUtils {

    public CustomerResponseModel getAggregationsByCustomerId(String customerId) {
        return getHeader()
                .get(CUSTOMER_AGGREGATIONS_PATH.apply(customerId))
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(CustomerResponseModel.class);
    }
}
