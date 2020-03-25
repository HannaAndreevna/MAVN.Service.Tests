package com.lykke.tests.api.service.referral;

import static com.lykke.tests.api.common.CommonConsts.ERROR_CODE_FIELD;
import static com.lykke.tests.api.common.CommonConsts.ERROR_CODE_NONE;
import static com.lykke.tests.api.common.CommonConsts.REFERRAL_CODE_FIELD;
import static com.lykke.api.testing.api.common.GenerateUtils.generateRandomEmail;
import static com.lykke.tests.api.service.customermanagement.RegisterCustomerUtils.registerCustomer;
import static com.lykke.tests.api.service.customermanagement.RegisterCustomerUtils.registerCustomerWithReferralCode;
import static com.lykke.tests.api.service.referral.ReferralCodeUtils.setReferralCodeByCustomerId;
import static com.lykke.tests.api.service.referral.SignUpReferralUtils.getFriendReferralByCustomerId;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.Matchers.equalTo;

import com.lykke.api.testing.annotations.UserStoryId;
import com.lykke.tests.api.base.BaseApiTest;
import com.lykke.tests.api.service.customermanagement.model.register.RegistrationRequestModel;
import lombok.var;
import org.junit.jupiter.api.Test;

public class SignUpReferralTests extends BaseApiTest {

    @Test
    @UserStoryId(storyId = 696)
    void shouldGetFriendReferralByCustomerId() {
        String referrerCustomer = registerCustomer();

        String referralCode = setReferralCodeByCustomerId(referrerCustomer)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .path(REFERRAL_CODE_FIELD);

        var refferedCustomer = new RegistrationRequestModel();
        refferedCustomer.setReferralCode(referralCode);
        String referredCustomer = registerCustomerWithReferralCode(refferedCustomer);

        getFriendReferralByCustomerId(referrerCustomer)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .body("ReferredCustomers[0]", equalTo(referredCustomer))
                .body(ERROR_CODE_FIELD, equalTo(ERROR_CODE_NONE));
    }
}
