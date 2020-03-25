package com.lykke.tests.api.service.bonuscustomerprofile;

import static com.lykke.tests.api.common.CommonConsts.AWAITILITY_DEFAULT_SEC;
import static com.lykke.tests.api.common.CommonConsts.AWAITILITY_DEFAULT_MAX_SEC;
import static com.lykke.tests.api.common.CommonConsts.SMOKE_TEST;
import static com.lykke.tests.api.service.bonuscustomerprofile.model.AggregationsUtils.getAggregationsByCustomerId;
import static com.lykke.tests.api.service.campaigns.CampaignUtils.getTotalCount;
import static com.lykke.tests.api.service.customermanagement.RegisterCustomerUtils.registerDefaultVerifiedCustomer;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.lykke.api.testing.annotations.UserStoryId;
import com.lykke.tests.api.base.BaseApiTest;
import com.lykke.tests.api.common.CommonMethods;
import com.lykke.tests.api.common.prerequisites.EarnRules;
import com.lykke.tests.api.service.bonuscustomerprofile.model.BonusCustomerProfileErrorCode;
import java.util.concurrent.TimeUnit;
import lombok.val;
import org.awaitility.Awaitility;
import org.awaitility.Duration;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

public class CustomerAggregationsTests extends BaseApiTest {

    private String customerId;

    @Test
    @Tag(SMOKE_TEST)
    @UserStoryId(storyId = 2126)
    void shouldGetAggregationsByCustomerId() {
        ////xx
        EarnRules.createBasicSignUpEarnRule();
        customerId = registerDefaultVerifiedCustomer().getCustomerId();

        Awaitility.await()
                .atMost(AWAITILITY_DEFAULT_MAX_SEC, TimeUnit.SECONDS)
                .pollInterval(Duration.TWO_SECONDS)
                .until(() -> 0 < CommonMethods.getCustomerBalanceForDefaultAsset(customerId));

        Awaitility.await()
                .atMost(AWAITILITY_DEFAULT_MAX_SEC, TimeUnit.SECONDS)
                .pollInterval(Duration.TWO_SECONDS)
                .until(() -> 0 != getAggregationsByCustomerId(customerId).getTotalCampaignsContributedCount());

        val responseObject = getAggregationsByCustomerId(customerId);

        assertAll(
                () -> assertEquals(customerId, responseObject.getCustomerId()),
                // TODO: due to external activity the number of campaign may vary
                () -> assertTrue(getTotalCount() <= responseObject.getTotalCampaignsContributedCount()),
                () -> assertEquals(0, responseObject.getTotalReferredFriendCount()),
                () -> assertEquals(0, responseObject.getTotalPurchasedAmount()),
                () -> assertEquals(0, responseObject.getTotalReferredPurchaseCount()),
                () -> assertEquals(0, responseObject.getTotalReferredPurchasedAmount()),
                () -> assertEquals(0, responseObject.getTotalReferredEstateLeadsCount()),
                () -> assertEquals(0, responseObject.getTotalReferredEstatePurchasesCount()),
                () -> assertEquals(0, responseObject.getTotalPropertyPurchasesByLeadCount()),
                () -> assertEquals(0, responseObject.getTotalOfferToPurchaseByLeadCount()),
                () -> assertEquals(0, responseObject.getTotalHotelStayCount()),
                () -> assertEquals(0, responseObject.getTotalHotelStayAmount()),
                () -> assertEquals(0, responseObject.getTotalHotelReferralStayCount()),
                () -> assertEquals(0, responseObject.getTotalHotelReferralStayAmount()),
                () -> assertEquals(BonusCustomerProfileErrorCode.NONE, responseObject.getErrorCode()),
                () -> assertNull(responseObject.getErrorMessage())
        );
    }
}
