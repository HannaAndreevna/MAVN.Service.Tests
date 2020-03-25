package com.lykke.tests.api.service.customer;

import static com.lykke.api.testing.api.base.RequestHeader.getHeader;
import static com.lykke.api.testing.api.common.QueryParamsUtils.getQueryParams;
import static com.lykke.tests.api.base.Paths.Customer.Vouchers.BUY_VOUCHERS_API_PATH;
import static com.lykke.tests.api.base.Paths.Customer.Vouchers.VOUCHERS_API_PATH;

import com.lykke.tests.api.service.customer.model.PaginationRequestModel;
import io.restassured.response.Response;
import lombok.experimental.UtilityClass;

@UtilityClass
public class VouchersUtils {

    public Response getVouchers(PaginationRequestModel requestModel, String token) {
        return getHeader(token)
                .queryParams(getQueryParams(requestModel))
                .get(VOUCHERS_API_PATH)
                .thenReturn();
    }

    public Response postBuyVoucher(String spendRuleId, String token) {
        return getHeader(token)
                .queryParam("spendRuleId", spendRuleId)
                .post(BUY_VOUCHERS_API_PATH)
                .thenReturn();
    }
}
