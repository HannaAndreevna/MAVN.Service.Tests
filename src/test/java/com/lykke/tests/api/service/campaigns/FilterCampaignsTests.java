package com.lykke.tests.api.service.campaigns;

import static com.lykke.api.testing.api.common.GenerateUtils.generateRandomString;
import static com.lykke.tests.api.service.campaigns.CampaignUtils.CURRENT_PAGE_FIELD;
import static com.lykke.tests.api.service.campaigns.CampaignUtils.PAGE_SIZE_FIELD;
import static com.lykke.tests.api.service.campaigns.CampaignUtils.createCampaign;
import static com.lykke.tests.api.service.campaigns.CampaignUtils.createConditionArray;
import static com.lykke.tests.api.service.campaigns.CampaignUtils.createConditionEditArray;
import static com.lykke.tests.api.service.campaigns.CampaignUtils.deleteCampaign;
import static com.lykke.tests.api.service.campaigns.CampaignUtils.getCampaignById;
import static com.lykke.tests.api.service.campaigns.CampaignUtils.getCampaignId;
import static com.lykke.tests.api.service.campaigns.CampaignUtils.getCampaigns;
import static com.lykke.tests.api.service.campaigns.CampaignUtils.getEarnRuleId;
import static com.lykke.tests.api.service.campaigns.CampaignUtils.getFilteredCampaigns;
import static com.lykke.tests.api.service.campaigns.CampaignUtils.getTotalCount;
import static com.lykke.tests.api.service.campaigns.CampaignUtils.updateCampaignById;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.of;

import com.lykke.api.testing.annotations.UserStoryId;
import com.lykke.api.testing.api.common.FakerUtils;
import com.lykke.tests.api.base.BaseApiTest;
import com.lykke.tests.api.common.enums.Localization;
import com.lykke.tests.api.common.enums.RuleContentType;
import com.lykke.tests.api.service.campaigns.model.Campaign;
import com.lykke.tests.api.service.campaigns.model.CampaignEditModel;
import com.lykke.tests.api.service.campaigns.model.CampaignResponseModel;
import com.lykke.tests.api.service.campaigns.model.CampaignSortBy;
import com.lykke.tests.api.service.campaigns.model.CampaignStatus;
import com.lykke.tests.api.service.campaigns.model.ConditionCreateModel;
import com.lykke.tests.api.service.campaigns.model.EarnRule;
import com.lykke.tests.api.service.campaigns.model.ListSortDirection;
import com.lykke.tests.api.service.campaigns.model.PaginatedCampaignListResponseModel;
import com.lykke.tests.api.service.campaigns.model.RewardType;
import java.time.Instant;
import java.util.Arrays;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import lombok.val;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

public class FilterCampaignsTests extends BaseApiTest {

    private static final String CONDITION_TYPE_SIGNUP = "signup";
    private static final Integer CONDITION_REWARD = 5;
    private static final int CONDITION_COMPLETION_COUNT = 4;
    private static final String CAMPAIGNS_FIELD = "Campaigns";
    private static final String CAMPAIGNS_NAME_0_FIELD = "Campaigns[0].Name";
    private static final String CURRENT_PAGE_FIELD_0 = CURRENT_PAGE_FIELD + "[0]";
    private static final String PAGE_SIZE_FIELD_0 = PAGE_SIZE_FIELD + "[0]";
    private static final String CAMPAIGNS_NAME_1_FIELD = "Campaigns[1].Name";
    private static final String CURRENT_PAGE_MUST_BE_BETWEEN_1_AND_2147483647_MESSAGE = "The field CurrentPage must be between 1 and 2147483647.";
    private static final String PAGE_SIZE_MUST_BE_BETWEEN_1_AND_1000_MESSAGE = "The field PageSize must be between 1 and 1000.";
    private static final String CURRENT_PAGE_VALIDATION_MESSAGE = "'Current Page' must be less than or equal to '10000'.";
    private static final String ERROR_MESSAGE = "ErrorMessage";
    private static final String MODEL_ERRORS_FIELD = "ModelErrors";
    private static final String CAMPAIGN_NAME = generateRandomString();
    private static final String CAMPAIGN_CREATED_BY = generateRandomString();
    private static final String CAMPAIGN_FROM = Instant.now().toString();
    private static final String CAMPAIGN_TO = "2025-05-23T06:59:26.627Z";
    private static final String CAMPAIGN_DESC = generateRandomString();
    private static final float CAMPAIGN_REWARD = 500;
    private static final int CAMPAIGN_COMPLETION_COUNT = 100;
    private static final RewardType REWARD_TYPE = RewardType.FIXED;
    private static final int currentPage = 1;
    private static final int pageSize = 100;
    private static final int defaultPageSize = 500;
    private static final int ORDER = 29;
    private static Campaign campaign;
    private static ConditionCreateModel bonusType;
    private static EarnRule earnRule;
    private static ConditionCreateModel.ConditionCreateModelBuilder baseCondition;
    private static Campaign.CampaignBuilder baseCampaign;
    private static CampaignEditModel.CampaignEditModelBuilder baseEditCampaign;
    private static EarnRule.EarnRuleBuilder baseEarnRule;

    @BeforeAll
    static void createCampaigns() {
        if (getTotalCount() != 0) {
            deleteAllCampaigns();
        }

        baseCondition = ConditionCreateModel
                .conditionCreateBuilder()
                .type(CONDITION_TYPE_SIGNUP)
                .immediateReward(CONDITION_REWARD.toString())
                .completionCount(CONDITION_COMPLETION_COUNT);

        bonusType = baseCondition
                .build();

        baseCampaign = Campaign
                .campaignBuilder()
                .name(CAMPAIGN_NAME)
                .reward(String.valueOf(CAMPAIGN_REWARD))
                .fromDate(CAMPAIGN_FROM)
                .toDate(CAMPAIGN_TO)
                .description(CAMPAIGN_DESC)
                .conditions(createConditionArray(bonusType))
                .createdBy(CAMPAIGN_CREATED_BY)
                .rewardType(REWARD_TYPE)
                .completionCount(CAMPAIGN_COMPLETION_COUNT)
                .order(ORDER);

        baseEditCampaign = CampaignEditModel
                .campaignBuilder()
                .name(CAMPAIGN_NAME)
                .reward(String.valueOf(CAMPAIGN_REWARD))
                .fromDate(CAMPAIGN_FROM)
                .toDate(CAMPAIGN_TO)
                .description(CAMPAIGN_DESC)
                .conditions(createConditionEditArray(bonusType))
                .rewardType(REWARD_TYPE)
                .completionCount(CAMPAIGN_COMPLETION_COUNT);

        campaign = baseCampaign
                .build();

        baseEarnRule = EarnRule
                .builder()
                .ruleContentType(RuleContentType.TITLE)
                .localization(Localization.EN)
                .value(FakerUtils.title);

        earnRule = baseEarnRule
                .build();

        createMultipleCampaigns(5);
    }

    private static Stream paginated_InvalidParameters() {
        return Stream.of(
                of(-1, pageSize, CURRENT_PAGE_FIELD_0, CURRENT_PAGE_MUST_BE_BETWEEN_1_AND_2147483647_MESSAGE,
                        CURRENT_PAGE_MUST_BE_BETWEEN_1_AND_2147483647_MESSAGE),
                of(0, pageSize, CURRENT_PAGE_FIELD_0, CURRENT_PAGE_MUST_BE_BETWEEN_1_AND_2147483647_MESSAGE,
                        CURRENT_PAGE_MUST_BE_BETWEEN_1_AND_2147483647_MESSAGE),
                of(4294969, defaultPageSize, CURRENT_PAGE_FIELD_0, CURRENT_PAGE_VALIDATION_MESSAGE,
                        CURRENT_PAGE_VALIDATION_MESSAGE),
                of(2147483647, defaultPageSize, CURRENT_PAGE_FIELD_0, CURRENT_PAGE_VALIDATION_MESSAGE,
                        CURRENT_PAGE_VALIDATION_MESSAGE),
                of(currentPage, -1, PAGE_SIZE_FIELD_0, PAGE_SIZE_MUST_BE_BETWEEN_1_AND_1000_MESSAGE,
                        PAGE_SIZE_MUST_BE_BETWEEN_1_AND_1000_MESSAGE),
                of(currentPage, 0, PAGE_SIZE_FIELD_0, PAGE_SIZE_MUST_BE_BETWEEN_1_AND_1000_MESSAGE,
                        PAGE_SIZE_MUST_BE_BETWEEN_1_AND_1000_MESSAGE),
                of(currentPage, 1001, PAGE_SIZE_FIELD_0, PAGE_SIZE_MUST_BE_BETWEEN_1_AND_1000_MESSAGE,
                        PAGE_SIZE_MUST_BE_BETWEEN_1_AND_1000_MESSAGE)
        );
    }

    private static String getCampaignConditionId(String campaignId) {
        return getCampaignById(campaignId)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .path("Conditions[0].Id");
    }

    private static void createMultipleCampaigns(int numberOfCampaignsToBeCreated) {
        IntStream.range(0, numberOfCampaignsToBeCreated)
                .forEach(n -> createDefaultCampaign());
    }

    private static String createDefaultCampaign() {
        return createCampaign(campaign, bonusType, earnRule)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .path("CampaignId");
    }

    @AfterAll
    static void cleanUp() {
        deleteAllCampaigns();
    }

    private static void deleteAllCampaigns() {
        // TODO: deletion of campaigns
        /*
        while (!getCampaigns().jsonPath().getList(CAMPAIGNS_FIELD).isEmpty()) {
            String campaign = getCampaigns()
                    .then()
                    .assertThat()
                    .statusCode(SC_OK)
                    .extract()
                    .path(CAMPAIGNS_FIELD + "[0].Id");

            deleteCampaign(campaign)
                    .then()
                    .assertThat()
                    .statusCode(SC_OK);
        }
        */
    }

    @Test
    @UserStoryId(storyId = {917, 3871})
    void shouldFilterCampaignsByName() {
        val commonNamePart = "zwa";
        val campName1 = commonNamePart + generateRandomString(2);
        val campName2 = generateRandomString(2) + commonNamePart;
        val campName3 = generateRandomString();

        val campaign1 = baseCampaign.name(campName1).build();
        val campaign2 = baseCampaign.name(campName2).build();
        val campaign3 = baseCampaign.name(campName3).build();

        createCampaign(campaign1, bonusType, earnRule);
        createCampaign(campaign2, bonusType, earnRule);
        createCampaign(campaign3, bonusType, earnRule);

        getFilteredCampaigns(commonNamePart, EMPTY, CampaignStatus.ACTIVE, CampaignSortBy.CAMPAIGN_NAME,
                ListSortDirection.ASCENDING, 1, 100)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(PaginatedCampaignListResponseModel.class);
    }

    ////??
    @Test
    @UserStoryId(storyId = {917, 3871})
    void shouldFilterCampaignsByCreationDateAscendingAndDescendingOrder() {
        val campaignName = "NewestCamp";
        val campaign = baseCampaign.name(campaignName).toDate(CAMPAIGN_TO).build();
        createCampaign(campaign, bonusType, earnRule);

        val filteredCampaigns = getFilteredCampaigns(
                EMPTY, EMPTY, CampaignStatus.ACTIVE, CampaignSortBy.CREATION_DATE,
                ListSortDirection.ASCENDING, 1, 500)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(PaginatedCampaignListResponseModel.class);

        val actualCampaign = Arrays.stream(filteredCampaigns.getCampaigns())
                .filter(cmp -> campaignName.equals(cmp.getName()))
                .findFirst();
        assertEquals(campaignName,
                actualCampaign.isPresent() ? actualCampaign.get().getName() : new CampaignResponseModel().getName());
    }

    @Test
    @UserStoryId(storyId = 917)
    void shouldFilterCampaignsByCampaignNameAscendingAndDescendingOrder() {
        val campaignName1 = "00000AaaaCamp";
        val campaignName2 = "ZzzzCamp";
        val campaign1 = baseCampaign.name(campaignName1).build();
        val campaign2 = baseCampaign.name(campaignName2).build();

        createCampaign(campaign1, bonusType, earnRule);
        createCampaign(campaign2, bonusType, earnRule);

        getFilteredCampaigns(EMPTY, EMPTY, CampaignStatus.ACTIVE, CampaignSortBy.CAMPAIGN_NAME,
                ListSortDirection.ASCENDING, 1, 500)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .body(CAMPAIGNS_NAME_0_FIELD, equalTo(campaignName1));

        getFilteredCampaigns(EMPTY, EMPTY, CampaignStatus.ACTIVE, CampaignSortBy.CAMPAIGN_NAME,
                ListSortDirection.DESCENDING, 1, 100)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .body(CAMPAIGNS_NAME_0_FIELD, equalTo(campaignName2));
    }

    @Test
    @UserStoryId(storyId = 917)
    void shouldFilterCompletedCampaignsByCampaignStatus() {
        val campaignName = "CompletedCamp";
        val campaign2 = baseCampaign.name(campaignName).toDate(Instant.now().toString()).build();

        createCampaign(campaign2, bonusType, earnRule);

        getFilteredCampaigns(EMPTY, EMPTY, CampaignStatus.COMPLETED, CampaignSortBy.CREATION_DATE,
                ListSortDirection.DESCENDING, 1, 100)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .body(CAMPAIGNS_FIELD, hasSize(1))
                .body(CAMPAIGNS_NAME_0_FIELD, equalTo(campaignName));
    }

    @Test
    @UserStoryId(storyId = 917)
    void shouldFilterInactiveCampaignsByCampaignStatus() {
        val campaignName = "InactiveCamp";
        val campaign = baseEditCampaign.name(campaignName).build();

        Campaign camp = baseCampaign.name(campaignName).build();
        createCampaign(camp, bonusType, earnRule);

        val campaignId = getCampaignId(camp, bonusType, earnRule);
        val conditionId = getCampaignConditionId(campaignId);

        updateCampaignById(campaign, bonusType, campaignId, conditionId, earnRule, getEarnRuleId(campaignId));

        getFilteredCampaigns(EMPTY, EMPTY, CampaignStatus.INACTIVE, CampaignSortBy.CREATION_DATE,
                ListSortDirection.DESCENDING, 1, 100)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .body(CAMPAIGNS_FIELD, hasSize(1))
                .body(CAMPAIGNS_NAME_0_FIELD, equalTo(campaignName));
    }

    @Test
    @UserStoryId(storyId = 917)
    void shouldFilterPendingCampaignsByCampaignStatus() {
        val campaignName = "PendingCamp";
        val campaign = baseCampaign.name(campaignName).fromDate("2022-05-23T06:59:26.627Z")
                .toDate("2029-05-23T06:59:26.627Z").build();

        createCampaign(campaign, bonusType, earnRule);

        getFilteredCampaigns(EMPTY, EMPTY, CampaignStatus.PENDING, CampaignSortBy.CREATION_DATE,
                ListSortDirection.DESCENDING, 1, 100)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .body(CAMPAIGNS_FIELD, hasSize(1))
                .body(CAMPAIGNS_NAME_0_FIELD, equalTo(campaignName));
    }

    @ParameterizedTest(name = "Run {index}: currentPage={0}, pageSize={1}, field={2}, errorMessage={3}, message={4}")
    @MethodSource("paginated_InvalidParameters")
    void shouldNotGetCampaignsPaginatedForInvalidParams(int currentPage, int pageSize, String field,
            String errorMessage, String message) {
        getFilteredCampaigns(EMPTY, EMPTY, CampaignStatus.ACTIVE, CampaignSortBy.CAMPAIGN_NAME,
                ListSortDirection.ASCENDING, currentPage, pageSize)
                .then()
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .body(ERROR_MESSAGE, equalTo(errorMessage))
                .body(MODEL_ERRORS_FIELD + "." + field, equalTo(message));
    }

    @Test
    @UserStoryId(storyId = 917)
    void shouldUseDefaultCurrentPageAndPageSizeValuesWhenNoValuesSpecified() {
        getCampaigns()
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .body(PAGE_SIZE_FIELD, equalTo(500))
                .body(CURRENT_PAGE_FIELD, equalTo(1));
    }
}
