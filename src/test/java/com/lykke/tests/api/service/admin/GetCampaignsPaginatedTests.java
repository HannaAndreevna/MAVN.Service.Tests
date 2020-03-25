package com.lykke.tests.api.service.admin;

import static com.lykke.tests.api.common.CommonConsts.SMOKE_TEST;
import static com.lykke.tests.api.service.admin.CampaignUtils.CAMPAIGN_NAME;
import static com.lykke.tests.api.service.admin.CampaignUtils.createCampaignAndReturnId;
import static com.lykke.tests.api.service.admin.CampaignUtils.getCampaignsPaginatedResponse;
import static com.lykke.tests.api.service.admin.CampaignUtils.getCampaignsPaginatedValidationResponse;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.lykke.api.testing.annotations.UserStoryId;
import com.lykke.tests.api.base.BaseApiTest;
import com.lykke.tests.api.service.admin.model.CampaignListRequest;
import java.util.stream.Stream;
import lombok.val;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class GetCampaignsPaginatedTests extends BaseApiTest {

    private static final int VALID_PAGE_SIZE = 1;
    private static final int VALID_1ST_CURRENT_PAGE = 1;
    private static final int VALID_2ND_CURRENT_PAGE = 2;

    static Stream<Arguments> getWrongPaginationParameters() {
        return TestDataForPaginatedTests.getWrongPaginationParameters();
    }

    @ParameterizedTest(name = "Run {index}: currentPage={0}, pageSize={1}")
    @MethodSource("getWrongPaginationParameters")
    @UserStoryId(storyId = {1058, 922})
    void shouldReturnCampaignsPaginated(int currentPage, int pageSize) {

        val requestObject = CampaignListRequest
                .campaignBuilder()
                .currentPage(currentPage)
                .pageSize(pageSize)
                .build();

        val validationResponse = getCampaignsPaginatedValidationResponse(requestObject);

        assertEquals(requestObject.getValidationResponse(), validationResponse);
    }

    @Test
    @UserStoryId(storyId = {1058, 922})
    @Tag(SMOKE_TEST)
    void shouldGetCampaignByName() {
        val campaignId = createCampaignAndReturnId();
        val requestObject = CampaignListRequest
                .campaignBuilder()
                .currentPage(VALID_1ST_CURRENT_PAGE)
                .pageSize(VALID_PAGE_SIZE)
                .campaignName(CAMPAIGN_NAME)
                .build();

        val actualCampaigns = getCampaignsPaginatedResponse(requestObject);

        assertAll(
                () -> assertEquals(1, actualCampaigns.getEarnRules().length),
                () -> assertEquals(CAMPAIGN_NAME, actualCampaigns.getEarnRules()[0].getName()),
                () -> assertEquals(campaignId, actualCampaigns.getEarnRules()[0].getId())
        );
    }

    // TODO: null pointer
    @Test
    @Tag(SMOKE_TEST)
    @UserStoryId(storyId = {1058, 922})
    void shouldGetCampaignByCampaignId() {
        val campaignId = createCampaignAndReturnId();
        val requestObject = CampaignListRequest
                .campaignBuilder()
                .currentPage(VALID_1ST_CURRENT_PAGE)
                .pageSize(VALID_PAGE_SIZE)
                .campaignName(CAMPAIGN_NAME)
                .build();

        val actualCampaigns = getCampaignsPaginatedResponse(requestObject);

        assertAll(
                () -> assertEquals(1, actualCampaigns.getEarnRules().length),
                () -> assertEquals(CAMPAIGN_NAME, actualCampaigns.getEarnRules()[0].getName()),
                () -> assertEquals(campaignId, actualCampaigns.getEarnRules()[0].getId())
        );
    }

    @Test
    @Tag(SMOKE_TEST)
    @UserStoryId(storyId = {1058, 922})
    void shouldGetCampaignsPaginated() {
        val campaignId01 = createCampaignAndReturnId();
        createCampaignAndReturnId();
        val requestObject = CampaignListRequest
                .campaignBuilder()
                .currentPage(VALID_2ND_CURRENT_PAGE)
                .pageSize(VALID_PAGE_SIZE)
                .build();

        val actualCampaigns = getCampaignsPaginatedResponse(requestObject);

        assertAll(
                () -> assertEquals(1, actualCampaigns.getEarnRules().length),
                () -> assertEquals(CAMPAIGN_NAME, actualCampaigns.getEarnRules()[0].getName()),
                () -> assertEquals(campaignId01, actualCampaigns.getEarnRules()[0].getId())
        );
    }
}
