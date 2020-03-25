package com.lykke.tests.api.service.admin;

import static com.lykke.tests.api.service.admin.CampaignUtils.CAMPAIGN_COMPLETION_COUNT;
import static com.lykke.tests.api.service.admin.CampaignUtils.CAMPAIGN_NAME;
import static com.lykke.tests.api.service.admin.CampaignUtils.createCampaignAndReturnId;
import static com.lykke.tests.api.service.admin.CampaignUtils.createCampaignObject;
import static com.lykke.tests.api.service.admin.CampaignUtils.createCampaignWithPercentageReward;
import static com.lykke.tests.api.service.admin.CampaignUtils.createCampaignWithVerticalAndReturnId;
import static com.lykke.tests.api.service.admin.CampaignUtils.createPendingCampaignAndReturnId;
import static com.lykke.tests.api.service.admin.CampaignUtils.createUpdateCampaignObject;
import static com.lykke.tests.api.service.admin.CampaignUtils.getCampaignsPaginatedResponse;
import static com.lykke.tests.api.service.admin.CampaignUtils.updateCampaign;
import static com.lykke.tests.api.service.admin.LoginAdminUtils.getAdminToken;
import static org.apache.http.HttpStatus.SC_NO_CONTENT;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.lykke.api.testing.annotations.UserStoryId;
import com.lykke.tests.api.base.BaseApiTest;
import com.lykke.tests.api.service.admin.model.CampaignListRequest;
import com.lykke.tests.api.service.admin.model.EarnRuleRowModel;
import com.lykke.tests.api.service.admin.model.bonustypes.Vertical;
import java.util.Arrays;
import lombok.val;
import lombok.var;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public class CampaignCompletionCountTests extends BaseApiTest {

    private static final int VALID_PAGE_SIZE = 1;
    private static final int VALID_1ST_CURRENT_PAGE = 1;

    @Test
    @UserStoryId(storyId = {860, 1337, 4333})
    void shouldCreateCampaignWithCompletionCountSet() {
        val campaignId = createCampaignWithVerticalAndReturnId();
        val requestObject = CampaignListRequest
                .campaignBuilder()
                .currentPage(VALID_1ST_CURRENT_PAGE)
                .pageSize(VALID_PAGE_SIZE)
                .campaignName(CAMPAIGN_NAME)
                .build();

        val actualCampaigns = getCampaignsPaginatedResponse(requestObject);
        val actualEarnRule = Arrays.stream(actualCampaigns.getEarnRules())
                .filter(rule -> rule.getId().equalsIgnoreCase(campaignId))
                .findFirst()
                .orElse(new EarnRuleRowModel());

        assertAll(
                () -> assertEquals(1, actualCampaigns.getEarnRules().length),
                () -> assertEquals(CAMPAIGN_NAME, actualEarnRule.getName()),
                () -> assertEquals(campaignId, actualEarnRule.getId()),
                () -> assertEquals(CAMPAIGN_COMPLETION_COUNT, actualEarnRule.getCompletionCount()),
                // FAL-4333
                () -> assertEquals(Vertical.REAL_ESTATE, actualEarnRule.getVertical())
        );
    }

    @Test
    @UserStoryId(storyId = {4059, 3869, 4333})
    void shouldCreateCampaignWithPercentageReward() {
        val campaignId = createCampaignWithPercentageReward();
        val requestObject = CampaignListRequest
                .campaignBuilder()
                .currentPage(VALID_1ST_CURRENT_PAGE)
                .pageSize(VALID_PAGE_SIZE)
                .campaignName(CAMPAIGN_NAME)
                .build();

        val actualCampaigns = getCampaignsPaginatedResponse(requestObject);
        val actualResult = Arrays.stream(actualCampaigns.getEarnRules())
            ////55    .filter(rule -> rule.getId().equalsIgnoreCase(campaignId))
                .findFirst()
                .orElse(new EarnRuleRowModel());

        assertAll(
           ////55     () -> assertEquals(CAMPAIGN_NAME, actualResult.getName()),
            ////55    () -> assertEquals(campaignId, actualResult.getId()),
                () -> assertEquals(CAMPAIGN_COMPLETION_COUNT, actualResult.getCompletionCount()),
                () -> assertNotNull(actualResult.getApproximateAward()),
                // FAL-3869
                () -> assertTrue(0 < actualResult.getOrder()),
                // FAL-4333
                () -> assertEquals(Vertical.REAL_ESTATE, actualResult.getVertical())
        );
    }

    ////55  @Disabled("updateCampaign - Extend for mobile content")
    @Test
    @UserStoryId(storyId = {860, 1337, 4333})
    void shouldChangeCampaignToNewCompletionCount() {
        val campaignId = createPendingCampaignAndReturnId();

        val requestObject = CampaignListRequest
                .campaignBuilder()
                .currentPage(VALID_1ST_CURRENT_PAGE)
                .pageSize(VALID_PAGE_SIZE)
                .campaignName(CAMPAIGN_NAME)
                .build();

        var actualCampaigns = getCampaignsPaginatedResponse(requestObject);
        val actualCampaign = actualCampaigns.getEarnRules()[0];

        assertAll(
         ////55       () -> assertEquals(CAMPAIGN_NAME, actualCampaign.getName()),
        ////55        () -> assertEquals(campaignId, actualCampaign.getId()),
                () -> assertEquals(CAMPAIGN_COMPLETION_COUNT, actualCampaign.getCompletionCount())
        );

        val campaignToUpdate = createUpdateCampaignObject();
        campaignToUpdate.setCompletionCount(actualCampaign.getCompletionCount() + 2);

        updateCampaign(campaignToUpdate, getAdminToken())
                .then()

                ////55
                .log().all()

                .assertThat()
                .statusCode(SC_NO_CONTENT);

        actualCampaigns = getCampaignsPaginatedResponse(requestObject);
        val actualUpdatedCampaign = actualCampaigns.getEarnRules()[0];

        assertAll(
                () -> assertEquals(CAMPAIGN_NAME, actualUpdatedCampaign.getName()),
                () -> assertEquals(campaignId, actualUpdatedCampaign.getId()),
                () -> assertEquals(CAMPAIGN_COMPLETION_COUNT + 2, actualUpdatedCampaign.getCompletionCount()),
                // FAL-4333
                () -> assertEquals(Vertical.REAL_ESTATE, actualUpdatedCampaign.getVertical())
        );
    }
}
