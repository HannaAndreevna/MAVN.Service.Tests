package com.lykke.tests.api.service.campaigns.mobile;

import static com.lykke.tests.api.common.CommonConsts.SMOKE_TEST;
import static com.lykke.tests.api.common.prerequisites.EarnRules.createCompletedEarnRule;
import static com.lykke.tests.api.common.prerequisites.EarnRules.createEarnRuleWithAllContentTypes;
import static com.lykke.tests.api.common.prerequisites.EarnRules.createEarnRuleWithENContent;
import static com.lykke.tests.api.common.prerequisites.EarnRules.createPendingEarnRule;
import static com.lykke.tests.api.service.campaigns.CampaignUtils.deleteCampaign;
import static com.lykke.tests.api.service.campaigns.CampaignUtils.getCampaigns;
import static com.lykke.tests.api.service.campaigns.MobileUtils.getMobilEarnRuleById;
import static com.lykke.tests.api.service.campaigns.MobileUtils.getMobileEarnRuleByIdValidationResponse;
import static com.lykke.tests.api.service.campaigns.MobileUtils.getMobileEarnRules;
import static com.lykke.tests.api.service.campaigns.MobileUtils.getMobileEarnRulesByIdResp;
import static com.lykke.tests.api.service.campaigns.MobileUtils.getMobileEarnRulesResp;
import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.apache.http.HttpStatus.SC_OK;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.lykke.api.testing.annotations.UserStoryId;
import com.lykke.tests.api.base.BaseApiTest;
import com.lykke.tests.api.common.CommonConsts;
import com.lykke.tests.api.common.enums.Localization;
import com.lykke.tests.api.common.enums.campaign.ConditionType;
import com.lykke.tests.api.service.campaigns.model.CampaignStatus;
import com.lykke.tests.api.service.campaigns.model.EarnRule;
import com.lykke.tests.api.service.campaigns.model.mobile.EarnRuleLocalizedResponse;
import com.lykke.tests.api.service.campaigns.model.mobile.EarnRulePaginatedResponseModel;
import com.lykke.tests.api.service.campaigns.model.mobile.MobileGetByIdRequest;
import lombok.val;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Arrays;

public class MobileEarnRulesTests extends BaseApiTest {

    protected static final String CAMPAIGNS_FIELD = "Campaigns";
    private static final Localization LOCALIZATION_EN = Localization.EN;
    private static final Localization LOCALIZATION_AR = Localization.AR;
    private static final String EARN_RULE_CONTENT_TITLE_EN_VALUE = "ENGLISH TITLE";
    private static final String EARN_RULE_CONTENT_TITLE_AR_VALUE = "ARABIC TITLE";
    private static final String EARN_RULE_CONTENT_DESCRIPTION_EN_VALUE = "ENGLISH DESCRIPTION";
    private static final String EARN_RULE_CONTENT_DESCRIPTION_AR_VALUE = "ARABIC DESCRIPTION";
    private static final CampaignStatus STATUS_ACTIVE = CampaignStatus.ACTIVE;
    private static final CampaignStatus STATUS_PENDING = CampaignStatus.PENDING;
    private static final CampaignStatus STATUS_COMPLETED = CampaignStatus.COMPLETED;
    private static final CampaignStatus STATUS_INACTIVE = CampaignStatus.INACTIVE;
    private static final String EARN_RULE_TYPE = ConditionType.SIGNUP.getValue();
    private static final String DISPLAY_NAME = "Sign up";
    private static final String IMAGE_URL = CommonConsts.Image.IMAGE_URL;
    private static final String ERROR_MESSAGE_FIELD = "ErrorMessage";
    private static final String MODEL_ERRORS_LANGUAGE_FIELD = "ModelErrors.language[0]";
    private static final String INVALID_VALUE_MSG = "The value 'Bg' is not valid.";

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

    @BeforeEach
    void setup() {
        deleteAllCampaigns();
    }

    @Test
    @Tag(SMOKE_TEST)
    @UserStoryId(storyId = 2245)
    void shouldGetEnMobileEarnRules() {
        val earnRuleId = createEarnRuleWithAllContentTypes(true);
        val actualResultCollection = getMobileEarnRules(LOCALIZATION_EN, STATUS_ACTIVE).getEarnRules();
        val actualResult = Arrays.stream(actualResultCollection)
                .filter(item -> item.getId().equalsIgnoreCase(earnRuleId))
                .findFirst()
                .orElse(new EarnRuleLocalizedResponse());
////xx
        assertAll(
                () -> assertEquals(earnRuleId, actualResult.getId()),
                () -> assertEquals(EARN_RULE_CONTENT_TITLE_EN_VALUE, actualResult.getTitle()),
                () -> assertEquals(EARN_RULE_CONTENT_DESCRIPTION_EN_VALUE, actualResult.getDescription()),
                () -> assertEquals(STATUS_ACTIVE, actualResult.getStatus()),
                () -> assertTrue(actualResult.getImageUrl().contains(IMAGE_URL)),
                () -> assertEquals(EARN_RULE_TYPE, actualResult.getConditions()[0].getType()),
                () -> assertEquals(DISPLAY_NAME, actualResult.getConditions()[0].getDisplayName())
        );
    }

    @Test
    @Tag(SMOKE_TEST)
    @UserStoryId(storyId = 2245)
    void shouldGetArMobileEarnRules() {
        val earnRuleId = createEarnRuleWithAllContentTypes(true);
        val actualResultCollection = getMobileEarnRules(LOCALIZATION_AR, STATUS_ACTIVE).getEarnRules();
        val actualResult = Arrays.stream(actualResultCollection)
                .filter(item -> item.getId().equalsIgnoreCase(earnRuleId))
                .findFirst()
                .orElse(new EarnRuleLocalizedResponse());
////xx
        assertAll(
                () -> assertEquals(earnRuleId, actualResult.getId()),
                () -> assertEquals(EARN_RULE_CONTENT_TITLE_AR_VALUE, actualResult.getTitle()),
                () -> assertEquals(EARN_RULE_CONTENT_DESCRIPTION_AR_VALUE, actualResult.getDescription()),
                () -> assertEquals(STATUS_ACTIVE, actualResult.getStatus()),
                () -> assertTrue(actualResult.getImageUrl().contains(IMAGE_URL)),
                () -> assertEquals(EARN_RULE_TYPE, actualResult.getConditions()[0].getType()),
                () -> assertEquals(DISPLAY_NAME, actualResult.getConditions()[0].getDisplayName())
        );
    }

    @Test
    @Tag(SMOKE_TEST)
    @UserStoryId(storyId = 2245)
    void shouldGetEnMobileEarnRulesWhenNotSpecified() {
        val earnRuleId = createEarnRuleWithAllContentTypes(true);
        val actualResultCollection = getMobileEarnRules(STATUS_ACTIVE).getEarnRules();////55[0];
        val actualResult = Arrays.stream(actualResultCollection)
                .filter(item -> item.getId().equalsIgnoreCase(earnRuleId))
                .findFirst()
                .orElse(new EarnRuleLocalizedResponse());
////55
        // TODO: filter
        assertAll(
                () -> assertEquals(earnRuleId, actualResult.getId()),
                () -> assertEquals(EARN_RULE_CONTENT_TITLE_EN_VALUE, actualResult.getTitle()),
                () -> assertEquals(EARN_RULE_CONTENT_DESCRIPTION_EN_VALUE, actualResult.getDescription()),
                () -> assertEquals(STATUS_ACTIVE, actualResult.getStatus()),
                () -> assertTrue(actualResult.getImageUrl().contains(IMAGE_URL)),
                () -> assertEquals(EARN_RULE_TYPE, actualResult.getConditions()[0].getType()),
                () -> assertEquals(DISPLAY_NAME, actualResult.getConditions()[0].getDisplayName())
        );
    }

    @Test
    @Tag(SMOKE_TEST)
    @UserStoryId(storyId = 2245)
    void shouldGetEnMobileBurnRulesWhenIsNoArContent() {
        val earnRuleId = createEarnRuleWithENContent(true);
        val actualResultCollection = getMobileEarnRules(LOCALIZATION_AR, STATUS_ACTIVE).getEarnRules();
        val actualResult = Arrays.stream(actualResultCollection)
                .filter(item -> item.getId().equalsIgnoreCase(earnRuleId))
                .findFirst()
                .orElse(new EarnRuleLocalizedResponse());
////xx
        assertAll(
                () -> assertEquals(earnRuleId, actualResult.getId()),
                () -> assertEquals(EARN_RULE_CONTENT_TITLE_EN_VALUE, actualResult.getTitle()),
                () -> assertEquals(EARN_RULE_CONTENT_DESCRIPTION_EN_VALUE, actualResult.getDescription()),
                () -> assertEquals(STATUS_ACTIVE, actualResult.getStatus()),
                () -> assertTrue(actualResult.getImageUrl().contains(IMAGE_URL)),
                () -> assertEquals(EARN_RULE_TYPE, actualResult.getConditions()[0].getType()),
                () -> assertEquals(DISPLAY_NAME, actualResult.getConditions()[0].getDisplayName())
        );
    }

    @Test
    @UserStoryId(storyId = 2245)
    void shouldNotGetMobileEarnRuleWhenLanguageIsNotValid() {
        getMobileEarnRulesResp("Bg", STATUS_ACTIVE)
                .then()
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .body(ERROR_MESSAGE_FIELD, CoreMatchers.equalTo(INVALID_VALUE_MSG))
                .body(MODEL_ERRORS_LANGUAGE_FIELD, CoreMatchers.equalTo(INVALID_VALUE_MSG));
    }

    @Test
    @Tag(SMOKE_TEST)
    @UserStoryId(storyId = 2245)
    void shouldGetMobileEarnRuleByIdWhenLanguageIsNotSpecified_EN() {
        val earnRuleId = createEarnRuleWithAllContentTypes(true);
        val actualResult = getMobilEarnRuleById(earnRuleId);

        assertAll(
                () -> assertEquals(earnRuleId, actualResult.getId()),
                () -> assertEquals(EARN_RULE_CONTENT_TITLE_EN_VALUE, actualResult.getTitle()),
                () -> assertEquals(EARN_RULE_CONTENT_DESCRIPTION_EN_VALUE, actualResult.getDescription()),
                () -> assertEquals(STATUS_ACTIVE, actualResult.getStatus()),
                () -> assertTrue(actualResult.getImageUrl().contains(IMAGE_URL)),
                () -> assertEquals(EARN_RULE_TYPE, actualResult.getConditions()[0].getType()),
                () -> assertEquals(DISPLAY_NAME, actualResult.getConditions()[0].getDisplayName())
        );
    }

    @Test
    @Tag(SMOKE_TEST)
    @UserStoryId(storyId = 2245)
    void shouldGetMobileEarnRuleByIdAndLanguage_EN() {
        val earnRuleId = createEarnRuleWithAllContentTypes(true);
        val actualResult = getMobilEarnRuleById(earnRuleId, LOCALIZATION_EN);

        assertAll(
                () -> assertEquals(earnRuleId, actualResult.getId()),
                () -> assertEquals(EARN_RULE_CONTENT_TITLE_EN_VALUE, actualResult.getTitle()),
                () -> assertEquals(EARN_RULE_CONTENT_DESCRIPTION_EN_VALUE, actualResult.getDescription()),
                () -> assertEquals(STATUS_ACTIVE, actualResult.getStatus()),
                () -> assertTrue(actualResult.getImageUrl().contains(IMAGE_URL)),
                () -> assertEquals(EARN_RULE_TYPE, actualResult.getConditions()[0].getType()),
                () -> assertEquals(DISPLAY_NAME, actualResult.getConditions()[0].getDisplayName())
        );
    }

    @Test
    @Tag(SMOKE_TEST)
    @UserStoryId(storyId = 2245)
    void shouldGetMobileEarnRuleByIdAndLanguage_AR() {
        val earnRuleId = createEarnRuleWithAllContentTypes(true);
        val actualResult = getMobilEarnRuleById(earnRuleId, LOCALIZATION_AR);

        assertAll(
                () -> assertEquals(earnRuleId, actualResult.getId()),
                () -> assertEquals(EARN_RULE_CONTENT_TITLE_AR_VALUE, actualResult.getTitle()),
                () -> assertEquals(EARN_RULE_CONTENT_DESCRIPTION_AR_VALUE, actualResult.getDescription()),
                () -> assertEquals(STATUS_ACTIVE, actualResult.getStatus()),
                () -> assertTrue(actualResult.getImageUrl().contains(IMAGE_URL)),
                () -> assertEquals(EARN_RULE_TYPE, actualResult.getConditions()[0].getType()),
                () -> assertEquals(DISPLAY_NAME, actualResult.getConditions()[0].getDisplayName())
        );
    }

    @Test
    @Tag(SMOKE_TEST)
    @UserStoryId(storyId = 2245)
    void shouldGetEnMobileEarnRuleByIdWhenLanguageIsArButThereIsNoSuchContent() {
        val earnRuleId = createEarnRuleWithENContent(true);
        val actualResult = getMobilEarnRuleById(earnRuleId, LOCALIZATION_AR);

        assertAll(
                () -> assertEquals(earnRuleId, actualResult.getId()),
                () -> assertEquals(EARN_RULE_CONTENT_TITLE_EN_VALUE, actualResult.getTitle()),
                () -> assertEquals(EARN_RULE_CONTENT_DESCRIPTION_EN_VALUE, actualResult.getDescription()),
                () -> assertEquals(STATUS_ACTIVE, actualResult.getStatus()),
                () -> assertTrue(actualResult.getImageUrl().contains(IMAGE_URL)),
                () -> assertEquals(EARN_RULE_TYPE, actualResult.getConditions()[0].getType()),
                () -> assertEquals(DISPLAY_NAME, actualResult.getConditions()[0].getDisplayName())
        );
    }

    @Test
    @UserStoryId(storyId = 2245)
    void shouldNotGetEarnRuleWhenLanguageIsNotValid() {
        val earnRuleId = createEarnRuleWithENContent(true);
        getMobileEarnRulesByIdResp(earnRuleId, "Bg")
                .then()
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .body(ERROR_MESSAGE_FIELD, CoreMatchers.equalTo(INVALID_VALUE_MSG))
                .body(MODEL_ERRORS_LANGUAGE_FIELD, CoreMatchers.equalTo(INVALID_VALUE_MSG));
    }

    @Disabled
    @ParameterizedTest
    @CsvSource({"fa32b67a-3945-44e0-4a11-08d71fd82b3",
            "fa32b67a-3945-44e0-4a11-08d71fd82b312",
            "saxas"})
    @UserStoryId(storyId = 2245)
    void shouldValidateEarnRuleIdFormat(String invalidEarnRuleId) {
        val requestObject = MobileGetByIdRequest
                .builder()
                .burnRuleId(invalidEarnRuleId)
                .build();

        val actualResult = getMobileEarnRuleByIdValidationResponse(requestObject);

        assertEquals(requestObject.getValidationResponse(), actualResult);
    }

    @Test
    @UserStoryId(storyId = 2245)
    void shouldFilterByPendingCampaigns() {
        createEarnRuleWithAllContentTypes(true);
        createCompletedEarnRule(true);
        val earnRuleId = createPendingEarnRule(true);

        val actualResult = getMobileEarnRules(LOCALIZATION_EN, STATUS_PENDING).getEarnRules();

        assertAll(
                () -> assertEquals(1, actualResult.length),
                () -> assertEquals(earnRuleId, actualResult[0].getId()),
                () -> assertEquals(EARN_RULE_CONTENT_TITLE_EN_VALUE, actualResult[0].getTitle()),
                () -> assertEquals(EARN_RULE_CONTENT_DESCRIPTION_EN_VALUE, actualResult[0].getDescription()),
                () -> assertEquals(STATUS_PENDING, actualResult[0].getStatus()),
                () -> assertTrue(actualResult[0].getImageUrl().contains(IMAGE_URL)),
                () -> assertEquals(EARN_RULE_TYPE, actualResult[0].getConditions()[0].getType()),
                () -> assertEquals(DISPLAY_NAME, actualResult[0].getConditions()[0].getDisplayName())
        );
    }

    @Test
    @UserStoryId(storyId = 2245)
    void shouldFilterByCompletedCampaigns() {
        createEarnRuleWithAllContentTypes(true);
        val earnRuleId = createCompletedEarnRule(true);
        createPendingEarnRule(true);

        val actualResult = getMobileEarnRules(LOCALIZATION_EN, STATUS_COMPLETED).getEarnRules();

        assertAll(
                () -> assertEquals(1, actualResult.length),
                () -> assertEquals(earnRuleId, actualResult[0].getId()),
                () -> assertEquals(EARN_RULE_CONTENT_TITLE_EN_VALUE, actualResult[0].getTitle()),
                () -> assertEquals(EARN_RULE_CONTENT_DESCRIPTION_EN_VALUE, actualResult[0].getDescription()),
                () -> assertEquals(STATUS_COMPLETED, actualResult[0].getStatus()),
                () -> assertTrue(actualResult[0].getImageUrl().contains(IMAGE_URL)),
                () -> assertEquals(EARN_RULE_TYPE, actualResult[0].getConditions()[0].getType()),
                () -> assertEquals(DISPLAY_NAME, actualResult[0].getConditions()[0].getDisplayName())
        );
    }
}
