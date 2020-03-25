package com.lykke.tests.api.service.referral;

import static com.lykke.tests.api.base.BasicFunctionalities.BASE_ASSET;
import static com.lykke.tests.api.common.CommonConsts.ERROR_CODE_FIELD;
import static com.lykke.tests.api.common.CommonConsts.ERROR_CODE_NONE;
import static com.lykke.tests.api.common.CommonConsts.REFERRAL_CODE_FIELD;
import static com.lykke.tests.api.common.CommonConsts.SMOKE_TEST;
import static com.lykke.tests.api.service.bonusengine.purchaseproducttriggers.MVNIntegrationUtils.makePurchaseWithReferral;
import static com.lykke.tests.api.service.customermanagement.RegisterCustomerUtils.registerCustomer;
import static com.lykke.tests.api.service.referral.PurchaseReferralUtils.getPurchaseReferralByCustomerId;
import static com.lykke.tests.api.service.referral.ReferralCodeUtils.setReferralCodeByCustomerId;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.Matchers.equalTo;

import com.lykke.api.testing.annotations.UserStoryId;
import com.lykke.tests.api.base.BaseApiTest;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

public class PurchaseReferralTests extends BaseApiTest {

    @Disabled("Disabled because makes purchase which causes errors, "
            + " purchase product and purchase product referral are deprecated and not in the MVP")
    @Test
    @UserStoryId(storyId = 696)
    @Tag(SMOKE_TEST)
    void shouldGetPurchaseReferralByCustomerId() {
        String referrerCustomer = registerCustomer();
        String referredCustomer = registerCustomer();

        String referralCode = setReferralCodeByCustomerId(referrerCustomer)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .path(REFERRAL_CODE_FIELD);

        makePurchaseWithReferral(referredCustomer, 5f, BASE_ASSET, referralCode)
                .then()
                .assertThat()
                .statusCode(SC_OK);

        getPurchaseReferralByCustomerId(referrerCustomer)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .body("ReferredCustomers[0]", equalTo(referredCustomer))
                .body(ERROR_CODE_FIELD, equalTo(ERROR_CODE_NONE));
    }
}
