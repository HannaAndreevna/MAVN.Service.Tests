package com.lykke.tests.api.service.customer;

import static com.lykke.api.testing.api.common.GenerateUtils.getRandomUuid;
import static com.lykke.tests.api.service.customer.LoginLogoutCustomerUtils.getUserToken;
import static com.lykke.tests.api.service.customer.VouchersUtils.getVouchers;
import static com.lykke.tests.api.service.customer.VouchersUtils.postBuyVoucher;
import static com.lykke.tests.api.service.customermanagement.RegisterCustomerUtils.registerDefaultVerifiedCustomer;
import static com.lykke.tests.selftest.queryparams.model.a.ObjectListRequest.PaginationConts.CURRENT_PAGE_LOWER_BOUNDARY;
import static com.lykke.tests.selftest.queryparams.model.a.ObjectListRequest.PaginationConts.PAGE_SIZE_UPPER_BOUNDARY;
import static org.apache.http.HttpStatus.SC_NO_CONTENT;
import static org.apache.http.HttpStatus.SC_OK;

import com.lykke.api.testing.annotations.UserStoryId;
import com.lykke.tests.api.base.BaseApiTest;
import com.lykke.tests.api.common.model.CustomerInfo;
import com.lykke.tests.api.service.customer.model.PaginationRequestModel;
import com.lykke.tests.api.service.customer.model.VoucherListModel;
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
    @UserStoryId(storyId = {3908, 3909})
    void shouldGetVouchers() {
        val actualResult = getVouchers(PaginationRequestModel
                .builder()
                .currentPage(CURRENT_PAGE_LOWER_BOUNDARY)
                .pageSize(PAGE_SIZE_UPPER_BOUNDARY)
                .build(), customertoken)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(VoucherListModel.class);
        /*
        {
            "Vouchers": [

            ],
            "TotalCount": 0
        }
        */
    }

    @Test
    @UserStoryId(storyId = {3908, 3909})
    void shouldPostBuyVoucher() {
        val actualResult = postBuyVoucher(getRandomUuid(), customertoken)
                .then()
                .assertThat()
                .statusCode(SC_NO_CONTENT);
    }
}
