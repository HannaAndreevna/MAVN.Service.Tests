package com.lykke.tests.api.service.customer;

import static com.lykke.tests.api.common.CommonConsts.SMOKE_TEST;
import static com.lykke.tests.api.common.PaginationConts.CURRENT_PAGE_LOWER_BOUNDARY;
import static com.lykke.tests.api.common.PaginationConts.PAGE_SIZE_UPPER_BOUNDARY;
import static com.lykke.tests.api.service.customer.CustomerReferralsUtils.getAllReferrals;
import static com.lykke.tests.api.service.customer.CustomerReferralsUtils.getCustomerReferrals;
import static com.lykke.tests.api.service.customer.LoginLogoutCustomerUtils.getUserToken;
import static com.lykke.tests.api.service.customer.LoginLogoutCustomerUtils.loginUserWithValidEmailAndPassword;
import static com.lykke.tests.api.service.customermanagement.RegisterCustomerUtils.TOKEN_FIELD;
import static com.lykke.tests.api.service.customermanagement.RegisterCustomerUtils.registerDefaultVerifiedCustomer;
import static com.lykke.tests.api.service.referral.ReferralCodeUtils.getReferralCodeByCustomerId;
import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.text.CharSequenceLength.hasLength;
import static org.hamcrest.text.MatchesPattern.matchesPattern;

import com.lykke.api.testing.annotations.UserStoryId;
import com.lykke.tests.api.base.BaseApiTest;
import com.lykke.tests.api.service.customer.model.referral.CommonReferralStatus;
import com.lykke.tests.api.service.customer.model.referral.ReferralPaginationRequestModel;
import com.lykke.tests.api.service.customer.model.referral.ReferralsListResponseModel;
import java.util.UUID;
import lombok.val;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;


public class ReferralCodeTests extends BaseApiTest {

    private static final String REFERRAL_CODE_FIELD = "ReferralCode";
    private static final String ERROR_CODE_FIELD = "ErrorCode";
    private static final String ERROR_MESSAGE_FIELD = "ErrorMessage";
    private static final int REFERRAL_CODE_LENGTH = 6;
    private static final String REFERRAL_CODE_REGEX = "^[-a-np-z1-9._]+";

    @Test
    @Tag(SMOKE_TEST)
    @UserStoryId(storyId = 471)
    void shouldGetReferralCodeForCustomer() {
        val customerData = registerDefaultVerifiedCustomer();

        getCustomerReferrals(getUserToken(customerData))
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .body(REFERRAL_CODE_FIELD, notNullValue())
                .body(REFERRAL_CODE_FIELD, hasLength(REFERRAL_CODE_LENGTH))
                .body(REFERRAL_CODE_FIELD, matchesPattern(REFERRAL_CODE_REGEX));
    }

    @Test
    @UserStoryId(storyId = 471)
    void shouldNotGetReferralCodeForCustomer_Unauthorized() {
        getCustomerReferrals(UUID.randomUUID().toString())
                .then()
                .assertThat()
                .statusCode(SC_UNAUTHORIZED);
    }

    @Test
    @UserStoryId(storyId = 385)
    void referralCodeIsGeneratedOnLogin() {
        val customerData = registerDefaultVerifiedCustomer();

        String referralNotFoundErrMsg =
                "Referral code for Customer with id '" + customerData.getCustomerId() + "' not found.";
        getReferralCodeByCustomerId(customerData.getCustomerId())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .body(REFERRAL_CODE_FIELD, nullValue())
                .body(ERROR_CODE_FIELD, equalTo("ReferralNotFound"))
                .body(ERROR_MESSAGE_FIELD, equalTo(referralNotFoundErrMsg));

        loginUserWithValidEmailAndPassword(customerData.getEmail(), customerData.getPassword())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .body(TOKEN_FIELD, hasLength(64));

        getCustomerReferrals(getUserToken(customerData))
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .body(REFERRAL_CODE_FIELD, notNullValue())
                .body(REFERRAL_CODE_FIELD, hasLength(REFERRAL_CODE_LENGTH))
                .body(REFERRAL_CODE_FIELD, matchesPattern(REFERRAL_CODE_REGEX));
    }

    @ParameterizedTest
    @EnumSource(CommonReferralStatus.class)
    @UserStoryId(storyId = {3759, 3993, 3971})
    void shouldGetAllReferrals(CommonReferralStatus status) {
        val customerData = registerDefaultVerifiedCustomer();

        val expectedResult = ReferralsListResponseModel
                .builder()
                .build();

        val actualResult = getAllReferrals(ReferralPaginationRequestModel
                .referralPaginationRequestModelBuilder()
                .status(status)
                .currentPage(CURRENT_PAGE_LOWER_BOUNDARY)
                .pageSize(PAGE_SIZE_UPPER_BOUNDARY)
                .build(), getUserToken(customerData))
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(ReferralsListResponseModel.class);
    }
}
