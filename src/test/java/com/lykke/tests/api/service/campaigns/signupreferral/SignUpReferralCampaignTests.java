package com.lykke.tests.api.service.campaigns.signupreferral;

import static com.lykke.tests.api.common.CommonConsts.AWAITILITY_DEFAULT_SEC;
import static com.lykke.tests.api.common.CommonMethods.getCustomerBalanceForDefaultAsset;
import static com.lykke.tests.api.service.customermanagement.RegisterCustomerUtils.registerCustomer;
import static com.lykke.tests.api.service.customermanagement.RegisterCustomerUtils.registerCustomerWithReferralCode;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.lykke.api.testing.annotations.UserStoryId;
import com.lykke.tests.api.service.campaigns.BaseCampaignTest;
import com.lykke.tests.api.service.customermanagement.model.register.RegistrationRequestModel;
import java.util.concurrent.TimeUnit;
import lombok.var;
import lombok.val;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public class SignUpReferralCampaignTests extends BaseCampaignTest {

    @BeforeAll
    static void conditionTypeCheck() {
        deleteAllCampaigns();
    }

    @Disabled("Fails because wallet service is removed.")
    @Test
    @UserStoryId(storyId = 484)
    void shouldReceiveRewardForSignUpForReferrerAndReferred() {
        //test disabled
    }

    private static void createCampaignForFriendReferral() {
        bonusType = baseCondition
                .type(CONDITION_TYPE_FRIEND_REFERRAL)
                .build();

        campaign = baseCampaign
                .rewardType(REWARD_TYPE_FIXED)
                .reward(CAMPAIGN_REWARD.toString())
                .build();

        campaignId = createCampaignAndGetId();
    }

    @Test
    @UserStoryId(storyId = 484)
    void shouldReceiveRewardForFriendReferral() {
        String referrerCustomer = registerCustomer();
        createCampaignForFriendReferral();

        // TODO: NPE ----- due to new fields

        String referralCode = getReferralCode(referrerCustomer);
        var customer = new RegistrationRequestModel();
        customer.setReferralCode(referralCode);
        String referredCustomer = registerCustomerWithReferralCode(customer);

        Awaitility
                .await()
                .atMost(AWAITILITY_DEFAULT_SEC, TimeUnit.SECONDS)
                .until(() -> {
                    val balance = getCustomerBalanceForDefaultAsset(referrerCustomer);
                    return TOTAL_REWARD == balance;
                });

        assertEquals(0, getCustomerBalanceForDefaultAsset(referredCustomer));
    }
}
