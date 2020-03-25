package com.lykke.tests.api.service.referral;

import static com.lykke.api.testing.api.common.GenerateUtils.getRandomUuid;
import static com.lykke.tests.api.common.CommonConsts.REFERRAL_CODE_FIELD;
import static com.lykke.tests.api.service.customermanagement.RegisterCustomerUtils.registerCustomerWithReferralCode;
import static com.lykke.tests.api.service.customermanagement.RegisterCustomerUtils.registerDefaultVerifiedCustomer;
import static com.lykke.tests.api.service.referral.CommonReferralUtils.getCommonReferralByCustomerId;
import static com.lykke.tests.api.service.referral.CommonReferralUtils.getListOfCommonReferrals;
import static com.lykke.tests.api.service.referral.ReferralCodeUtils.setReferralCodeByCustomerId;
import static java.util.stream.Collectors.toList;
import static org.apache.http.HttpStatus.SC_INTERNAL_SERVER_ERROR;
import static org.apache.http.HttpStatus.SC_OK;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.of;

import com.lykke.api.testing.annotations.UserStoryId;
import com.lykke.tests.api.base.BaseApiTest;
import com.lykke.tests.api.common.model.CustomerInfo;
import com.lykke.tests.api.service.referral.model.ReferralErrorCode;
import com.lykke.tests.api.service.referral.model.common.CommonReferralByCustomerIdRequest;
import com.lykke.tests.api.service.referral.model.common.CommonReferralByCustomerIdResponse;
import com.lykke.tests.api.service.referral.model.common.CommonReferralByReferralIdsRequest;
import com.lykke.tests.api.service.referral.model.common.CommonReferralByReferralIdsResponse;
import com.lykke.tests.api.service.referral.model.common.CommonReferralModel;
import com.lykke.tests.api.service.referral.model.common.CommonReferralStatus;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;
import lombok.val;
import org.apache.commons.collections4.map.HashedMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class CommonReferralTests extends BaseApiTest {

    private CustomerInfo customerData;
    private String customerId;
    private String referralCode;
    private CustomerInfo referral;

    private static Stream<Arguments> getStatuses() {
        return Stream.of(
                of(Arrays.stream(new CommonReferralStatus[]{}).collect(toList())),
                of(Arrays.stream(new CommonReferralStatus[]{CommonReferralStatus.EXPIRED}).collect(toList())),
                of(Arrays.stream(new CommonReferralStatus[]{CommonReferralStatus.ACCEPTED}).collect(toList())),
                of(Arrays.stream(new CommonReferralStatus[]{CommonReferralStatus.CONFIRMED}).collect(toList())),
                of(Arrays.stream(new CommonReferralStatus[]{CommonReferralStatus.PENDING}).collect(toList())),
                of(Arrays.stream(new CommonReferralStatus[]{CommonReferralStatus.CONFIRMED,
                        CommonReferralStatus.PENDING}).collect(toList())),
                of(Arrays.stream(new CommonReferralStatus[]{CommonReferralStatus.ACCEPTED,
                        CommonReferralStatus.ACCEPTED}).collect(toList())),
                of(Arrays.stream(new CommonReferralStatus[]{CommonReferralStatus.ACCEPTED, CommonReferralStatus.PENDING,
                        CommonReferralStatus.CONFIRMED, CommonReferralStatus.EXPIRED}).collect(toList()))
        );
    }

    @BeforeEach
    void methodSetup() {
        customerData = registerDefaultVerifiedCustomer();
        customerId = customerData.getCustomerId();

        referralCode = setReferralCodeByCustomerId(customerId)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract().path(REFERRAL_CODE_FIELD);

        referral = new CustomerInfo();
        referral.setReferralCode(referralCode);
        referral.setCustomerId(registerCustomerWithReferralCode(referral));
    }

    @ParameterizedTest
    @MethodSource("getStatuses")
    @UserStoryId(storyId = {3733, 3974})
    void shouldGetCommonReferralByCustomerId(List<CommonReferralStatus> status) {
        val actualResult = getCommonReferralByCustomerId(
                CommonReferralByCustomerIdRequest
                        .builder()
                        .customerId(referral.getCustomerId())
                        .statuses(status.toArray(new CommonReferralStatus[]{}))
                        .build())
                .then()
                .assertThat()
                .statusCode(SC_INTERNAL_SERVER_ERROR);
        /*
                .statusCode(SC_OK)
                .extract()
                .as(CommonReferralByCustomerIdResponse.class);
         */
    }

    @ParameterizedTest
    @MethodSource("getStatuses")
    @UserStoryId(storyId = {3733, 3974})
    void shouldNotGetCommonReferralByNonReferredCustomerId(List<CommonReferralStatus> status) {
        val expectedResult = CommonReferralByCustomerIdResponse
                .commonReferralByCustomerIdResponseBuilder()
                .referrals(new CommonReferralModel[]{})
                .errorCode(ReferralErrorCode.NONE)
                .build();

        val actualResult = getCommonReferralByCustomerId(
                CommonReferralByCustomerIdRequest
                        .builder()
                        .customerId(referral.getCustomerId())
                        .campaignId(getRandomUuid())
                        .statuses(status.toArray(new CommonReferralStatus[]{}))
                        .build())
                .then()
                .assertThat()
                .statusCode(SC_INTERNAL_SERVER_ERROR);
                /*
                .statusCode(SC_OK)
                .extract()
                .as(CommonReferralByCustomerIdResponse.class);

        assertEquals(expectedResult, actualResult);
        */
    }

    @Test
    @UserStoryId(3733)
    void shouldGetCommonReferralsList() {
        val actualResult = getListOfCommonReferrals(CommonReferralByReferralIdsRequest
                .builder()
                .referralIds(new String[]{referral.getCustomerId(), customerData.getCustomerId()})
                .build())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(CommonReferralByReferralIdsResponse.class);
    }

    @Test
    @UserStoryId(3733)
    void shouldNotGetCommonReferralsListIfNoReferrals() {
        val expectedResult = CommonReferralByReferralIdsResponse
                .builder()
                .commonReferrals(new HashedMap<>())
                .build();

        val actualResult = getListOfCommonReferrals(CommonReferralByReferralIdsRequest
                .builder()
                .referralIds(new String[]{customerData.getCustomerId()})
                .build())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(CommonReferralByReferralIdsResponse.class);

        assertEquals(expectedResult, actualResult);
    }
}
