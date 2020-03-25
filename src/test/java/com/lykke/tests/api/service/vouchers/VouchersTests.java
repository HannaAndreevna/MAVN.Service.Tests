package com.lykke.tests.api.service.vouchers;

import static com.lykke.api.testing.api.common.GenerateUtils.getRandomUuid;
import static com.lykke.tests.api.service.customer.LoginLogoutCustomerUtils.getUserToken;
import static com.lykke.tests.api.service.customermanagement.RegisterCustomerUtils.registerDefaultVerifiedCustomer;
import static com.lykke.tests.api.service.vouchers.VouchersUtils.getSpendRuleVouchers;
import static com.lykke.tests.api.service.vouchers.VouchersUtils.getVoucherByVoucherId;
import static com.lykke.tests.api.service.vouchers.VouchersUtils.getVouchersByCustomerId;
import static com.lykke.tests.api.service.vouchers.VouchersUtils.getVouchersBySpendRuleId;
import static com.lykke.tests.api.service.vouchers.VouchersUtils.postCustomersVouchers;
import static com.lykke.tests.api.service.vouchers.VouchersUtils.postVouchers;
import static org.apache.http.HttpStatus.SC_NO_CONTENT;
import static org.apache.http.HttpStatus.SC_OK;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.lykke.api.testing.annotations.UserStoryId;
import com.lykke.tests.api.base.BaseApiTest;
import com.lykke.tests.api.common.model.CustomerInfo;
import com.lykke.tests.api.service.vouchers.model.CustomerVoucherModel;
import com.lykke.tests.api.service.vouchers.model.SpendRuleVouchersReportModel;
import com.lykke.tests.api.service.vouchers.model.VoucherBuyModel;
import com.lykke.tests.api.service.vouchers.model.VoucherBuyResultModel;
import com.lykke.tests.api.service.vouchers.model.VoucherCreateModel;
import com.lykke.tests.api.service.vouchers.model.VoucherCreateResultModel;
import com.lykke.tests.api.service.vouchers.model.VoucherErrorCode;
import com.lykke.tests.api.service.vouchers.model.VoucherModel;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class VouchersTests extends BaseApiTest {

    private CustomerInfo customerData;
    private String customertoken;

    @BeforeEach
    void setUp() {
        customerData = registerDefaultVerifiedCustomer();
        customertoken = getUserToken(customerData);
    }

    @Test
    @UserStoryId(3916)
    void shouldGetSpendRuleVouchers() {
        val actualResult = getSpendRuleVouchers(getRandomUuid())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(SpendRuleVouchersReportModel.class);
        /*
        {
            "Total": 0,
            "InStock": 0
        }
        */
    }

    @Test
    @UserStoryId(3916)
    void shouldGetVoucherByVoucherId() {
        val actualResult = getVoucherByVoucherId(getRandomUuid())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(VoucherModel.class);
    }

    @Test
    @UserStoryId(3916)
    void shouldNotGetVoucherByNonExistingVoucherId() {
        val actualResult = getVoucherByVoucherId(getRandomUuid())
                .then()
                .assertThat()
                .statusCode(SC_NO_CONTENT);
    }

    @Test
    @UserStoryId(3916)
    void shouldGetVouchersBySpendRuleId() {
        val actualResult = getVouchersBySpendRuleId(getRandomUuid())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(VoucherModel[].class);
    }

    @Test
    @UserStoryId(3916)
    void shouldGetCustomersVouchers() {
        val actualResult = getVouchersByCustomerId(getRandomUuid())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(CustomerVoucherModel[].class);
    }

    @Test
    @UserStoryId(3916)
    void shouldPostVoucher() {
        val expectedResult = VoucherCreateResultModel
                .builder()
                .errorCode(VoucherErrorCode.SPEND_RULE_NOT_FOUND)
                .build();

        val actualResult = postVouchers(VoucherCreateModel
                .builder()
                .spendRuleId(getRandomUuid())
                .codes(new String[]{"aaa", "bbb", "ccc"})
                .build())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(VoucherCreateResultModel.class);

        assertEquals(expectedResult, actualResult);
    }

    @Test
    @UserStoryId(3916)
    void shouldPostCustomerVoucher() {
        val expectedResult = VoucherBuyResultModel
                .builder()
                .errorCode(VoucherErrorCode.SPEND_RULE_NOT_FOUND)
                .build();

        val actualResult = postCustomersVouchers(VoucherBuyModel
                .builder()
                .spendRuleId(getRandomUuid())
                .customerId(customerData.getCustomerId())
                .build())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(VoucherBuyResultModel.class);

        assertEquals(expectedResult, actualResult);
    }

    @Test
    @UserStoryId(3916)
    void shouldNotFailOnPostingCustomerVoucherWithNonExistingCustomerIdAndSpendRuleId() {
        val expectedResult = VoucherBuyResultModel
                .builder()
                .errorCode(VoucherErrorCode.SPEND_RULE_NOT_FOUND)
                .build();

        val actualResult = postCustomersVouchers(VoucherBuyModel
                .builder()
                .spendRuleId(getRandomUuid())
                .customerId(getRandomUuid())
                .build())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(VoucherBuyResultModel.class);

        assertEquals(expectedResult, actualResult);
    }
}
