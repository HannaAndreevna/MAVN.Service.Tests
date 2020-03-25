package com.lykke.tests.api.service.vouchers;

import static com.lykke.api.testing.api.base.RequestHeader.getHeader;
import static com.lykke.tests.api.base.Paths.Vouchers.VouchersCtrl.CUSTOMERS_API_PATH;
import static com.lykke.tests.api.base.Paths.Vouchers.VouchersCtrl.GET_BY_VOUCHER_ID_API_PATH;
import static com.lykke.tests.api.base.Paths.Vouchers.VouchersCtrl.GET_CUSTOMERS_VOUCHERS_API_PATH;
import static com.lykke.tests.api.base.Paths.Vouchers.VouchersCtrl.GET_VOUCHERS_BY_SPEND_RULE_ID_API_PATH;
import static com.lykke.tests.api.base.Paths.Vouchers.VouchersCtrl.VOUCHERS_API_PATH;

import com.lykke.tests.api.base.Paths.Vouchers.Reports;
import com.lykke.tests.api.service.vouchers.model.VoucherBuyModel;
import com.lykke.tests.api.service.vouchers.model.VoucherCreateModel;
import io.restassured.response.Response;
import lombok.experimental.UtilityClass;

@UtilityClass
public class VouchersUtils {

    public Response getSpendRuleVouchers(String spendRuleId) {
        return getHeader()
                .queryParam("spendRuleId", spendRuleId)
                .get(Reports.SPEND_RULE_VOUCHERS_API_PATH)
                .thenReturn();

    }

    public Response getVoucherByVoucherId(String voucherId) {
        return getHeader()
                .get(GET_BY_VOUCHER_ID_API_PATH.apply(voucherId))
                .thenReturn();
    }

    public Response getVouchersBySpendRuleId(String spendRuleId) {
        return getHeader()
                .get(GET_VOUCHERS_BY_SPEND_RULE_ID_API_PATH.apply(spendRuleId))
                .thenReturn();
    }

    public Response getVouchersByCustomerId(String customerId) {
        return getHeader()
                .get(GET_CUSTOMERS_VOUCHERS_API_PATH.apply(customerId))
                .thenReturn();
    }

    public Response postVouchers(VoucherCreateModel requestModel) {
        return getHeader()
                .body(requestModel)
                .post(VOUCHERS_API_PATH)
                .thenReturn();
    }

    public Response postCustomersVouchers(VoucherBuyModel requestModel) {
        return getHeader()
                .body(requestModel)
                .post(CUSTOMERS_API_PATH)
                .thenReturn();
    }
}
