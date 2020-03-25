package com.lykke.tests.api.service.customer.earnrules;

import static com.lykke.tests.api.common.CommonConsts.CustomerCredentials.getUsetToken;
import static com.lykke.tests.api.common.CommonConsts.SMOKE_TEST;
import static com.lykke.api.testing.api.common.GenerateUtils.getRandomUuid;
import static com.lykke.tests.api.common.prerequisites.EarnRules.createCompletedEarnRule;
import static com.lykke.tests.api.common.prerequisites.EarnRules.createEarnRuleWithAllContentTypes;
import static com.lykke.tests.api.common.prerequisites.EarnRules.createEarnRuleWithAllContentTypesAndPercentageReward;
import static com.lykke.tests.api.common.prerequisites.EarnRules.createEarnRuleWithAllContentTypesAndStaking;
import static com.lykke.tests.api.common.prerequisites.EarnRules.createPendingEarnRule;
import static com.lykke.tests.api.service.customer.EarnRulesUtils.getEarnRuleById_Response_Deprecated;
import static com.lykke.tests.api.service.customer.EarnRulesUtils.getEarnRules;
import static com.lykke.tests.api.service.customer.EarnRulesUtils.getEarnRulesById;
import static com.lykke.tests.api.service.customer.EarnRulesUtils.getEarnRulesById_Deprecated;
import static com.lykke.tests.api.service.customer.EarnRulesUtils.getEarnRulesStakingById;
import static com.lykke.tests.api.service.customer.EarnRulesUtils.getEarnRulesStakingById_Deprecated;
import static com.lykke.tests.api.service.customer.LoginLogoutCustomerUtils.getCustomerToken;
import static com.lykke.tests.api.service.customer.LoginLogoutCustomerUtils.getUserToken;
import static com.lykke.tests.api.service.customermanagement.RegisterCustomerUtils.registerDefaultVerifiedCustomer;
import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.lykke.api.testing.annotations.UserStoryId;
import com.lykke.tests.api.base.BaseApiTest;
import com.lykke.tests.api.common.CommonConsts.Image;
import com.lykke.tests.api.common.enums.campaign.CampaignStatus;
import com.lykke.tests.api.common.enums.campaign.ConditionType;
import com.lykke.tests.api.common.model.CustomerInfo;
import com.lykke.tests.api.service.customer.model.earnrule.EarnRuleModel;
import java.util.Arrays;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

public class EarnRulesTests extends BaseApiTest {

    protected static final String CAMPAIGNS_FIELD = "Campaigns";
    private static final String EARN_RULE_CONTENT_TITLE_EN_VALUE = "ENGLISH TITLE";
    private static final String EARN_RULE_CONTENT_DESCRIPTION_EN_VALUE = "ENGLISH DESCRIPTION";
    private static final String STATUS_ACTIVE = CampaignStatus.ACTIVE.getValue();
    private static final CampaignStatus STATUS_PENDING = CampaignStatus.PENDING;
    private static final String STATUS_COMPLETED = CampaignStatus.COMPLETED.getValue();
    private static final String STATUS_INACTIVE = CampaignStatus.INACTIVE.getValue();
    private static final String EARN_RULE_TYPE = ConditionType.SIGNUP.getValue();
    private static final String DISPLAY_NAME = "Sign up";
    private static final String IMAGE_URL = Image.IMAGE_URL;
    private static final String ERROR_FIELD = "error";
    private static final String MESSAGE_FIELD = "message";
    private static final String ERROR_TYPE = "ModelValidationFailed";
    private static final String ERROR_MESSAGE = "The value '%s' is not valid.";
    private static final int ORDER = 19;

    private CustomerInfo customerData;
    private String customerToken;

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

        customerData = registerDefaultVerifiedCustomer();
        customerToken = getUserToken(customerData);
    }

    @Test
    @Tag(SMOKE_TEST)
    @UserStoryId(storyId = {2174, 4061, 3870})
    void shouldGetEarnRules() {
        val earnRuleId = createEarnRuleWithAllContentTypesAndPercentageReward(true);
        val actualResultCollection = getEarnRules(STATUS_ACTIVE, customerToken).getEarnRules();
        val actualResult = Arrays.stream(actualResultCollection)
                .filter(item -> item.getId().equalsIgnoreCase(earnRuleId))
                .findFirst()
                .orElse(new EarnRuleModel());

        assertAll(
                () -> assertEquals(earnRuleId, actualResult.getId()),
                () -> assertEquals(EARN_RULE_CONTENT_TITLE_EN_VALUE, actualResult.getTitle()),
                () -> assertEquals(EARN_RULE_CONTENT_DESCRIPTION_EN_VALUE, actualResult.getDescription()),
                () -> assertEquals(com.lykke.tests.api.service.customer.model.earnrule.CampaignStatus.ACTIVE,
                        actualResult.getStatus()),
                () -> assertTrue(actualResult.getImageUrl().contains(IMAGE_URL)),
                () -> assertEquals(EARN_RULE_TYPE, actualResult.getConditions()[0].getType()),
                () -> assertEquals(DISPLAY_NAME, actualResult.getConditions()[0].getDisplayName()),
                // FAL-4061
                () -> assertNotNull(actualResult.getApproximateAward()),
                () -> assertNotNull(actualResult.getConditions()[0].getApproximateAward()),
                // FAL-3870
                () -> assertTrue(0 < actualResult.getOrder())
        );
    }

    @Test
    @UserStoryId(storyId = {2174, 4061, 3870})
    void shouldFilterByPendingCampaigns() {
        createEarnRuleWithAllContentTypes(true);
        createCompletedEarnRule(true);
        val earnRuleId = createPendingEarnRule(true);
        val actualResultCollection = getEarnRules(STATUS_PENDING.getValue(), customerToken).getEarnRules();
        val actualResult = Arrays.stream(actualResultCollection)
                .filter(rule -> rule.getId().equalsIgnoreCase(earnRuleId))
                .findFirst()
                .orElse(new EarnRuleModel());

        assertAll(
                () -> assertEquals(earnRuleId, actualResult.getId()),
                () -> assertEquals(EARN_RULE_CONTENT_TITLE_EN_VALUE, actualResult.getTitle()),
                () -> assertEquals(EARN_RULE_CONTENT_DESCRIPTION_EN_VALUE, actualResult.getDescription()),
                () -> assertEquals(com.lykke.tests.api.service.customer.model.earnrule.CampaignStatus.PENDING,
                        actualResult.getStatus()),
                () -> assertTrue(actualResult.getImageUrl().contains(IMAGE_URL)),
                () -> assertEquals(EARN_RULE_TYPE, actualResult.getConditions()[0].getType()),
                () -> assertEquals(DISPLAY_NAME, actualResult.getConditions()[0].getDisplayName()),
                // FAL-3870
                () -> assertTrue(0 < actualResult.getOrder())
        );
    }

    @Test
    @UserStoryId(storyId = {2174, 4061, 3870})
    void shouldFilterByCompletedCampaigns() {
        createEarnRuleWithAllContentTypes(true);
        val earnRuleId = createCompletedEarnRule(true);
        createPendingEarnRule(true);

        val actualResultCollection = getEarnRules(STATUS_COMPLETED, customerToken).getEarnRules();
        val actualResult = Arrays.stream(actualResultCollection)
                .filter(rule -> rule.getId().equalsIgnoreCase(earnRuleId))
                .findFirst()
                .orElse(new EarnRuleModel());

        assertAll(
                () -> assertEquals(earnRuleId, actualResult.getId()),
                () -> assertEquals(EARN_RULE_CONTENT_TITLE_EN_VALUE, actualResult.getTitle()),
                () -> assertEquals(EARN_RULE_CONTENT_DESCRIPTION_EN_VALUE, actualResult.getDescription()),
                () -> assertEquals(com.lykke.tests.api.service.customer.model.earnrule.CampaignStatus.COMPLETED,
                        actualResult.getStatus()),
                () -> assertTrue(actualResult.getImageUrl().contains(IMAGE_URL)),
                () -> assertEquals(EARN_RULE_TYPE, actualResult.getConditions()[0].getType()),
                () -> assertEquals(DISPLAY_NAME, actualResult.getConditions()[0].getDisplayName()),
                // FAL-3870
                () -> assertTrue(0 < actualResult.getOrder())
        );
    }

    @Test
    @Tag(SMOKE_TEST)
    @UserStoryId(storyId = {2174, 4061, 3870})
    void shouldGetEarnRuleById_Deprecated() {
        val earnRuleId = createEarnRuleWithAllContentTypes(true);
        val actualResult = getEarnRulesById_Deprecated(earnRuleId, customerToken);

        assertAll(
                () -> assertEquals(earnRuleId, actualResult.getId()),
                () -> assertEquals(EARN_RULE_CONTENT_TITLE_EN_VALUE, actualResult.getTitle()),
                () -> assertEquals(EARN_RULE_CONTENT_DESCRIPTION_EN_VALUE, actualResult.getDescription()),
                () -> assertEquals(com.lykke.tests.api.service.customer.model.earnrule.CampaignStatus.ACTIVE,
                        actualResult.getStatus()),
                () -> assertTrue(actualResult.getImageUrl().contains(IMAGE_URL)),
                () -> assertEquals(EARN_RULE_TYPE, actualResult.getConditions()[0].getType()),
                () -> assertEquals(DISPLAY_NAME, actualResult.getConditions()[0].getDisplayName()),
                // FAL-3870
                () -> assertTrue(0 < actualResult.getOrder())
        );
    }

    @Test
    @Tag(SMOKE_TEST)
    @UserStoryId(storyId = {3815, 3971, 4061, 3870})
    void shouldGetEarnRuleById() {
        val earnRuleId = createEarnRuleWithAllContentTypes(true);
        val actualResult = getEarnRulesById(earnRuleId, customerToken);

        assertAll(
                () -> assertEquals(earnRuleId, actualResult.getId()),
                () -> assertEquals(EARN_RULE_CONTENT_TITLE_EN_VALUE, actualResult.getTitle()),
                () -> assertEquals(EARN_RULE_CONTENT_DESCRIPTION_EN_VALUE, actualResult.getDescription()),
                () -> assertEquals(com.lykke.tests.api.service.customer.model.earnrule.CampaignStatus.ACTIVE,
                        actualResult.getStatus()),
                () -> assertTrue(actualResult.getImageUrl().contains(IMAGE_URL)),
                () -> assertEquals(EARN_RULE_TYPE, actualResult.getConditions()[0].getType()),
                () -> assertEquals(DISPLAY_NAME, actualResult.getConditions()[0].getDisplayName()),
                // FAL-3870
                () -> assertTrue(0 < actualResult.getOrder())
        );
    }

    @Test
    @Tag(SMOKE_TEST)
    @UserStoryId(storyId = {3815, 3971})
    void shouldGetEarnRuleStakingById_Deprecated() {
        val earnRuleId = createEarnRuleWithAllContentTypesAndStaking(true);

        // TODO: no rule stakings
        val actualResult = getEarnRulesStakingById_Deprecated(earnRuleId, customerToken).getEarnRuleStakings()[0];
    }

    @Test
    @Tag(SMOKE_TEST)
    @UserStoryId(storyId = {3815, 3971})
    void shouldGetEarnRuleStakingById() {
        val earnRuleId = createEarnRuleWithAllContentTypesAndStaking(true);

        // TODO: no rule stakings
        val actualResult = getEarnRulesStakingById(earnRuleId, customerToken).getEarnRuleStakings()[0];
    }

    @Test
    @UserStoryId(storyId = 2174)
    void shouldNotGetEarnRuleWhenIdIsNotValid() {
        val invalidEarnRule = "dedaeqeq-dadaddede-deead333-afe";
        getEarnRuleById_Response_Deprecated(invalidEarnRule, customerToken)
                .then()
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .body(ERROR_FIELD, equalTo(ERROR_TYPE))
                .body(MESSAGE_FIELD, equalTo(String.format(ERROR_MESSAGE, invalidEarnRule)));
    }

    @Disabled("Fails because FAL-2801")
    @Test
    @UserStoryId(storyId = 2174)
    void shouldNotReturnContentWhenEarnRuleDoesNotExist() {
        getEarnRuleById_Response_Deprecated(getRandomUuid(), customerToken)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .body(equalTo("null"));
    }
}
