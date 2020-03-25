package com.lykke.tests.api.service.bonuscustomerprofile;

import static com.lykke.api.testing.api.base.RequestHeader.getHeader;
import static com.lykke.api.testing.api.common.GenerateUtils.generateRandomString;
import static com.lykke.tests.api.base.PathConsts.BonusCustomerProfileApiEndpoint.CUSTOMER_BY_ID_PATH;
import static com.lykke.tests.api.base.Paths.CustomerProfile.CUSTOMER_PROFILE_CONTRIBUTIONS_API_PATH;
import static com.lykke.tests.api.common.CommonConsts.AWAITILITY_INITIAL_BALANCE_WAIT_SECONDS;
import static com.lykke.tests.api.common.CommonConsts.SMOKE_TEST;
import static com.lykke.tests.api.service.admin.LoginAdminUtils.getAdminToken;
import static com.lykke.tests.api.service.bonuscustomerprofile.model.ErrorCode.GUID_CANNOT_BE_PARSED;
import static com.lykke.tests.api.service.bonuscustomerprofile.model.ErrorCode.NONE;
import static com.lykke.tests.api.service.campaigns.CampaignUtils.createConditionArray;
import static com.lykke.tests.api.service.campaigns.CampaignUtils.deleteCampaign;
import static com.lykke.tests.api.service.campaigns.CampaignUtils.getCampaignId;
import static com.lykke.tests.api.service.campaigns.CampaignUtils.getCampaigns;
import static com.lykke.tests.api.service.customer.LoginLogoutCustomerUtils.getUserToken;
import static com.lykke.tests.api.service.customermanagement.RegisterCustomerUtils.registerCustomer;
import static com.lykke.tests.api.service.privateblockchainfacade.PrivateBlockchainFacadeUtils.getCustomerBalance;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.http.HttpStatus.SC_NOT_FOUND;
import static org.apache.http.HttpStatus.SC_OK;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.of;

import com.lykke.api.testing.annotations.UserStoryId;
import com.lykke.api.testing.api.common.FakerUtils;
import com.lykke.tests.api.base.BaseApiTest;
import com.lykke.tests.api.common.enums.Localization;
import com.lykke.tests.api.common.enums.RuleContentType;
import com.lykke.tests.api.service.bonuscustomerprofile.model.BonusCustomerProfileResponse;
import com.lykke.tests.api.service.campaigns.model.Campaign;
import com.lykke.tests.api.service.campaigns.model.ConditionCreateModel;
import com.lykke.tests.api.service.campaigns.model.EarnRule;
import com.lykke.tests.api.service.campaigns.model.RewardType;
import com.lykke.tests.api.service.customermanagement.model.register.RegistrationRequestModel;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;
import lombok.val;
import lombok.var;
import org.awaitility.Awaitility;
import org.awaitility.Duration;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class CustomerProfileContributionsTests extends BaseApiTest {

    protected static final String CAMPAIGNS_FIELD = "Campaigns";
    private static final String INVALID_CUSTOMER_ID_O1 = "aaa";
    private static final String INVALID_CUSTOMER_ID_O2 = "de0f60ea-7ea1-4146-a401-98967d28d23e";
    private static final String CONDITION_TYPE_SIGNUP = "signup";
    private static final RewardType REWARD_TYPE_FIXED = RewardType.FIXED;
    private static final Integer CAMPAIGN_REWARD = 123;
    private static final Integer CONDITION_REWARD = 5;
    private static final int CONDITION_COMPLETION_COUNT = 1;
    private static final String CAMPAIGN_NAME = generateRandomString();
    private static final String CAMPAIGN_CREATED_BY = generateRandomString();
    private static final String CAMPAIGN_FROM = Instant.now().toString();
    private static final String CAMPAIGN_TO = "2025-05-23T06:59:26.627Z";
    private static final String CAMPAIGN_DESC = generateRandomString();
    private static final int CAMPAIGN_COMPLETION_COUNT = 1;
    private static String VALID_CUSTOMER_WITH_CAMPAIGN_ID;
    private static String VALID_CUSTOMER_WITHOUT_CAMPAIGN_ID;
    private static Campaign campaign;
    private static ConditionCreateModel bonusType;
    private static EarnRule earnRule;
    private static ConditionCreateModel.ConditionCreateModelBuilder baseCondition;
    private static Campaign.CampaignBuilder baseCampaign;
    private String email;
    private String password;
    private String token;
    private String campaignId;

    @AfterAll
    static void cleanup() {
        deleteAllCampaigns();
    }

    private static Stream<Arguments> getInvalidCustomerId() {
        return Stream.of(
                of(
                        null,
                        SC_OK,
                        GUID_CANNOT_BE_PARSED.getCodeName(),
                        GUID_CANNOT_BE_PARSED.getErrorMessage(),
                        GUID_CANNOT_BE_PARSED.getIds()),
                of(EMPTY, SC_NOT_FOUND, "", "", null),
                of(
                        INVALID_CUSTOMER_ID_O1,
                        SC_OK,
                        GUID_CANNOT_BE_PARSED.getCodeName(),
                        GUID_CANNOT_BE_PARSED.getErrorMessage(),
                        GUID_CANNOT_BE_PARSED.getIds()),
                of(INVALID_CUSTOMER_ID_O2, SC_OK, NONE.getCodeName(), NONE.getErrorMessage(), NONE.getIds())
        );
    }

    private static void deleteAllCampaigns() {
        // //55
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
    void testSetUp1() {
        deleteAllCampaigns();
        VALID_CUSTOMER_WITHOUT_CAMPAIGN_ID = registerCustomer();

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
                .reward(CAMPAIGN_REWARD.toString())
                .fromDate(CAMPAIGN_FROM)
                .toDate(CAMPAIGN_TO)
                .description(CAMPAIGN_DESC)
                .conditions(createConditionArray(bonusType))
                .createdBy(CAMPAIGN_CREATED_BY)
                .rewardType(REWARD_TYPE_FIXED)
                .completionCount(CAMPAIGN_COMPLETION_COUNT);

        campaign = baseCampaign
                .build();

        earnRule = EarnRule
                .builder()
                .ruleContentType(RuleContentType.TITLE)
                .localization(Localization.EN)
                .value(FakerUtils.title)
                .build();

        campaignId = getCampaignId(campaign, bonusType, earnRule);

        var customer = new RegistrationRequestModel();

        VALID_CUSTOMER_WITH_CAMPAIGN_ID = registerCustomer(customer);
        token = getUserToken(customer.getEmail(), customer.getPassword());
    }

    @Test
    @Tag(SMOKE_TEST)
    @UserStoryId(645)
    void shouldReturnOkIfSomeCustomerIdProvided() {
        val response = getHeader(token)
                .get(CUSTOMER_PROFILE_CONTRIBUTIONS_API_PATH + CUSTOMER_BY_ID_PATH
                        .getFilledInPath(VALID_CUSTOMER_WITHOUT_CAMPAIGN_ID))
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract().as(BonusCustomerProfileResponse.class);

        assertAll(
                () -> assertEquals(NONE, response.getErrorCode().get()),
                () -> assertEquals(NONE.getErrorMessage(), response.getErrorMessage()),
                () -> assertEquals(NONE.getIds(), response.getContributionIds())
        );
    }

    @Disabled("TODO: needs investigation")
    @Test
    @Tag(SMOKE_TEST)
    @UserStoryId(645)
    void shouldReturnIdIfExistingCustomerIdProvided() {
        Awaitility.await()
                .atMost(AWAITILITY_INITIAL_BALANCE_WAIT_SECONDS, TimeUnit.SECONDS)
                .pollInterval(Duration.TWO_SECONDS)
                .until(() -> 0.0 < Double.valueOf(getCustomerBalance(VALID_CUSTOMER_WITH_CAMPAIGN_ID)));

        val response = getHeader(getAdminToken())
                .get(CUSTOMER_PROFILE_CONTRIBUTIONS_API_PATH + CUSTOMER_BY_ID_PATH
                        .getFilledInPath(VALID_CUSTOMER_WITH_CAMPAIGN_ID))
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(BonusCustomerProfileResponse.class);

        assertAll(
                () -> assertEquals(NONE, response.getErrorCode().get()),
                () -> assertEquals(NONE.getErrorMessage(), response.getErrorMessage()),
                () -> assertEquals(campaignId, response.getContributionIds().get(0))
        );
    }

    @ParameterizedTest(
            name = "Run {index}: customerId={0}, HTTP status={1}, error code={2}, error message={3}, ids={4}")
    @MethodSource("getInvalidCustomerId")
    @UserStoryId(645)
    void shouldNotTakeDataIfNoCustomerIdProvided(
            String customerId, int status, String errorCode, String errorMessage, List<UUID> collection) {

        final BonusCustomerProfileResponse responseObject;
        val response = getHeader(token)
                .get(CUSTOMER_PROFILE_CONTRIBUTIONS_API_PATH + CUSTOMER_BY_ID_PATH.getFilledInPath(customerId))
                .then()
                .assertThat()
                .statusCode(status);

        if (SC_NOT_FOUND != status) {
            responseObject = response.extract().as(BonusCustomerProfileResponse.class);
        } else {
            return;
        }

        assertAll(
                () -> assertEquals(errorCode, responseObject.getErrorCode().get().getCodeName()),
                () -> assertEquals(errorMessage, responseObject.getErrorMessage()),
                () -> assertEquals(collection, responseObject.getContributionIds())
        );
    }
}
