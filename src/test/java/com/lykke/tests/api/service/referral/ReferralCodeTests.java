package com.lykke.tests.api.service.referral;

import static com.lykke.tests.api.common.CommonConsts.AWAITILITY_DEFAULT_MAX_SEC;
import static com.lykke.tests.api.common.CommonConsts.ERROR_CODE_FIELD;
import static com.lykke.tests.api.common.CommonConsts.ERROR_CODE_NONE;
import static com.lykke.tests.api.common.CommonConsts.ERROR_MESSAGE_FIELD;
import static com.lykke.tests.api.common.CommonConsts.REFERRAL_CODE_FIELD;
import static com.lykke.tests.api.common.CommonConsts.SMOKE_TEST;
import static com.lykke.tests.api.service.customermanagement.RegisterCustomerUtils.registerCustomer;
import static com.lykke.tests.api.service.customermanagement.RegisterCustomerUtils.registerCustomerWithReferralCode;
import static com.lykke.tests.api.service.customermanagement.RegisterCustomerUtils.registerDefaultVerifiedCustomer;
import static com.lykke.tests.api.service.referral.ReferralCodeUtils.getReferralCodeByCustomerId;
import static com.lykke.tests.api.service.referral.ReferralCodeUtils.getReferralInfoByCustomerId;
import static com.lykke.tests.api.service.referral.ReferralCodeUtils.setReferralCodeByCustomerId;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.text.CharSequenceLength.hasLength;
import static org.hamcrest.text.MatchesPattern.matchesPattern;

import com.lykke.api.testing.annotations.UserStoryId;
import com.lykke.tests.api.base.BaseApiTest;
import com.lykke.tests.api.common.model.CustomerInfo;
import com.lykke.tests.api.service.customermanagement.model.register.RegistrationRequestModel;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import lombok.var;
import org.awaitility.Awaitility;
import org.awaitility.Duration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

public class ReferralCodeTests extends BaseApiTest {

    private static final int REFERRAL_CODE_LENGTH = 6;
    private static final String REFERRAL_CODE_REGEX = "^[-a-np-z1-9._]+";
    private static final String REFERRED_CUSTOMERS_0_FIELD = "ReferredCustomers[0]";
    private static final String REFERRED_CUSTOMERS_FIELD = "ReferredCustomers";
    private static final String NAME_FIELD = "name";

    private static CustomerInfo customerData;
    private static String customerId;
    private static String referralCode;

    @BeforeEach
    void methodSetup() {
        customerData = registerDefaultVerifiedCustomer();
        customerId = customerData.getCustomerId();

        referralCode = setReferralCodeByCustomerId(customerId)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract().path(REFERRAL_CODE_FIELD);
    }

    ////xx
    ////55   @Disabled("TODO: needs investigation")
    @Test
    @Tag(SMOKE_TEST)
    @UserStoryId(storyId = 701)
    void shouldGetReferralsInfo() {
        var user = new RegistrationRequestModel();
        user.setReferralCode(referralCode);
        var referredCustomerReferralCode = registerCustomerWithReferralCode(user);

        Awaitility.await().atMost(AWAITILITY_DEFAULT_MAX_SEC, TimeUnit.SECONDS)
                .pollInterval(Duration.FIVE_SECONDS)
                .until(() -> {
                    ArrayList<String> referredCustomers = getReferralInfoByCustomerId(customerId)
                            .then()
                            .assertThat()
                            .statusCode(SC_OK)
                            .extract()
                            .path(REFERRED_CUSTOMERS_FIELD);

                    return referredCustomers.size() != 0;
                });

        getReferralInfoByCustomerId(customerId)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .body(REFERRED_CUSTOMERS_0_FIELD, equalTo(referredCustomerReferralCode))
                .body(ERROR_CODE_FIELD, equalTo(ERROR_CODE_NONE))
                .body(ERROR_MESSAGE_FIELD, nullValue());
    }

    @Test
    @UserStoryId(storyId = 701)
    void shouldGetNoReferralsInfoWhenNotReferredCustomers() {
        getReferralInfoByCustomerId(customerId)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .body(REFERRED_CUSTOMERS_0_FIELD, nullValue())
                .body(ERROR_CODE_FIELD, equalTo(ERROR_CODE_NONE))
                .body(ERROR_MESSAGE_FIELD, nullValue());
    }

    @Test
    @Tag(SMOKE_TEST)
    @UserStoryId(storyId = 386)
    void shouldGetReferralCodeForCustomer_ByCustomerId() {
        getReferralCodeByCustomerId(customerId)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .body(REFERRAL_CODE_FIELD, hasLength(REFERRAL_CODE_LENGTH))
                .body(REFERRAL_CODE_FIELD, equalTo(referralCode))
                .body(REFERRAL_CODE_FIELD, matchesPattern(REFERRAL_CODE_REGEX))
                .body(ERROR_CODE_FIELD, equalTo(ERROR_CODE_NONE))
                .body(ERROR_MESSAGE_FIELD, nullValue());
    }

    @Test
    @UserStoryId(storyId = 386)
    void shouldNotGetReferralCodeForCustomer_NoReferralCodeAssigned() {
        customerId = registerCustomer();
        String referralNotFoundErrMsg = "Referral code for Customer with id '" + customerId + "' not found.";

        getReferralCodeByCustomerId(customerId)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .body(REFERRAL_CODE_FIELD, nullValue())
                .body(ERROR_CODE_FIELD, equalTo("ReferralNotFound"))
                .body(ERROR_MESSAGE_FIELD, equalTo(referralNotFoundErrMsg));
    }

    @Test
    @UserStoryId(storyId = 386)
    void shouldCreateReferralCodeForCustomer_OneCodePerCustomer() {

        setReferralCodeByCustomerId(customerId)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .body(REFERRAL_CODE_FIELD, hasLength(REFERRAL_CODE_LENGTH))
                .body(REFERRAL_CODE_FIELD, equalTo(referralCode))
                .body(REFERRAL_CODE_FIELD, matchesPattern(REFERRAL_CODE_REGEX));
    }

    @Test
    @UserStoryId(storyId = 386)
    void shouldCreateReferralCodeForCustomer_UniqueCodes() {
        customerId = registerCustomer();

        setReferralCodeByCustomerId(customerId)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .body(REFERRAL_CODE_FIELD, hasLength(REFERRAL_CODE_LENGTH))
                .body(REFERRAL_CODE_FIELD, not(referralCode))
                .body(REFERRAL_CODE_FIELD, matchesPattern(REFERRAL_CODE_REGEX));
        //TO-DO Currently this test check only for 2 customer. This doesn't guarantees us that on the 3th customer wont fail.
        //There is a custom mechanism for ensuring uniqueness of the "referral code"
    }
}
