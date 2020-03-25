package com.lykke.tests.api.service.partnersintegration;

import static com.lykke.api.testing.api.base.RequestHeader.getHeader;
import static com.lykke.api.testing.api.common.GenerateUtils.generateRandomString;
import static com.lykke.api.testing.api.common.QueryParamsUtils.getQueryParams;
import static com.lykke.tests.api.base.Paths.PartnersIntegration.BONUS_CUSTOMERS_API_PATH;
import static com.lykke.tests.api.base.Paths.PartnersIntegration.CUSTOMERS_QUERY_API_PATH;
import static com.lykke.tests.api.base.Paths.PartnersIntegration.CUSTOMER_BALANCE_QUERY_API_PATH;
import static com.lykke.tests.api.base.Paths.PartnersIntegration.MESSAGES_API_PATH;
import static com.lykke.tests.api.base.Paths.PartnersIntegration.MESSAGE_API_PATH;
import static com.lykke.tests.api.base.Paths.PartnersIntegration.PAYMENTS_API_PATH;
import static com.lykke.tests.api.base.Paths.PartnersIntegration.REFERRALS_QUERY_API_PATH;
import static com.lykke.tests.api.base.Paths.PartnersIntegration.REQUESTS_API_PATH;
import static com.lykke.tests.api.common.CommonConsts.Currency.SOME_AMOUNT_IN_CURRENCY;
import static com.lykke.tests.api.common.CommonConsts.Currency.SOME_AMOUNT_IN_TOKENS;
import static com.lykke.tests.api.service.campaigns.CampaignUtils.createCampaign;
import static com.lykke.tests.api.service.campaigns.CampaignUtils.createConditionArray;
import static com.lykke.tests.api.service.campaigns.CampaignUtils.deleteCampaign;
import static com.lykke.tests.api.service.campaigns.CampaignUtils.getCampaigns;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.http.HttpStatus.SC_OK;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy.UpperCamelCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.lykke.api.testing.api.common.FakerUtils;
import com.lykke.tests.api.common.enums.Localization;
import com.lykke.tests.api.common.enums.RuleContentType;
import com.lykke.tests.api.service.campaigns.model.BonusType;
import com.lykke.tests.api.service.campaigns.model.Campaign;
import com.lykke.tests.api.service.campaigns.model.EarnRule;
import com.lykke.tests.api.service.campaigns.model.PaginatedCampaignListResponseModel;
import com.lykke.tests.api.service.campaigns.model.RewardType;
import com.lykke.tests.api.service.customermanagement.RegisterCustomerUtils;
import com.lykke.tests.api.service.partnersintegration.model.BonusCustomersRequestModel;
import com.lykke.tests.api.service.partnersintegration.model.CustomerBalanceRequestModel;
import com.lykke.tests.api.service.partnersintegration.model.CustomerInformationRequestModel;
import com.lykke.tests.api.service.partnersintegration.model.MessagesPostRequestModel;
import com.lykke.tests.api.service.partnersintegration.model.PaymentRequestStatusRequestModel;
import com.lykke.tests.api.service.partnersintegration.model.PaymentsCreateRequestModel;
import com.lykke.tests.api.service.partnersintegration.model.PaymentsExecuteRequestModel;
import com.lykke.tests.api.service.partnersintegration.model.ReferralInformationRequestModel;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import java.time.Instant;
import java.util.Arrays;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.UtilityClass;
import lombok.val;

@UtilityClass
public class PartnersIntegrationUtils {

    private static final String CONDITION_TYPE_SIGNUP = "signup";
    private static final int CONDITION_COMPLETION_COUNT = 1;
    private static final String CAMPAIGN_NAME = generateRandomString();
    private static final String CAMPAIGN_CREATED_BY = generateRandomString();
    private static final String CAMPAIGN_FROM = Instant.now().toString();
    private static final String CAMPAIGN_TO = "2025-05-23T06:59:26.627Z";
    private static final String CAMPAIGN_DESC = generateRandomString();
    private static final int CAMPAIGN_COMPLETION_COUNT = 1;
    private static final RewardType REWARD_TYPE_FIXED = RewardType.FIXED;
    private static final String CAMPAIGNS_FIELD = "Campaigns";

    @Step("Get customers")
    public Response getCustomersQuery(CustomerInformationRequestModel requestModel) {
        return getHeader()
                .body(requestModel)
                .post(CUSTOMERS_QUERY_API_PATH)
                .thenReturn();
    }

    @Step("Get customer balance")
    public Response getCustomerBalance(CustomerBalanceRequestModel requestModel, String customerId, String token) {
        return getHeader(token)
                .queryParams(getQueryParams(requestModel))
                .get(CUSTOMER_BALANCE_QUERY_API_PATH.apply(customerId))
                .thenReturn();
    }

    @Step("Get referral information")
    public Response getReferralInformation(ReferralInformationRequestModel requestModel, String token) {
        return getHeader(token)
                .body(requestModel)
                .post(REFERRALS_QUERY_API_PATH)
                .thenReturn();
    }

    @Step("Trigger bonus to customer")
    public Response postTriggerBonusToCustomer(BonusCustomersRequestModel requestModel, String token) {
        return getHeader(token)
                .body(requestModel)
                .post(BONUS_CUSTOMERS_API_PATH)
                .thenReturn();
    }

    @Step("Create payment request")
    public <E extends PaymentsCreateRequestModel> Response postPaymentRequest(E requestModel, String token) {
        val body = getQueryParams(requestModel,
                x -> !EMPTY.equalsIgnoreCase(x) && !"0.0".equalsIgnoreCase(x));

        return getHeader(token)
                .body(body)
                .post(REQUESTS_API_PATH)
                .thenReturn();
    }

    @Step("Get payment request")
    public Response getPaymentRequest(PaymentRequestStatusRequestModel requestModel, String token) {
        return getHeader(token)
                .queryParams(getQueryParams(requestModel))
                .get(REQUESTS_API_PATH)
                .thenReturn();
    }

    @Step("Delete payment request")
    public Response deletePaymentRequest(PaymentRequestStatusRequestModel requestModel, String token) {
        return getHeader(token)
                .queryParams(getQueryParams(requestModel))
                .delete(REQUESTS_API_PATH)
                .thenReturn();
    }

    @Step("Execute payment request")
    public Response executePaymentRequest(PaymentsExecuteRequestModel requestModel, String token) {
        return getHeader(token)
                .body(requestModel)
                .post(PAYMENTS_API_PATH)
                .thenReturn();
    }

    public static String getFundedCustomerIdViaCampaigns(String email, String phone, String password,
            int amountToSupply,
            String partnerIdParam) {

        val amountToSupplyViaCampaignAndCondition = String.valueOf(amountToSupply * 3);
        Campaign campaign;
        BonusType bonusType;
        EarnRule earnRule;
        BonusType.BonusTypeBuilder baseCondition;
        Campaign.CampaignBuilder baseCampaign;

        deleteAllCampaigns();

        baseCondition = BonusType
                .builder()
                .immediateReward(String.valueOf(amountToSupply))
                .completionCount(CONDITION_COMPLETION_COUNT);

        baseCampaign = Campaign
                .campaignBuilder()
                .name(CAMPAIGN_NAME)
                .fromDate(CAMPAIGN_FROM)
                .toDate(CAMPAIGN_TO)
                .description(CAMPAIGN_DESC)
                .createdBy(CAMPAIGN_CREATED_BY)
                .completionCount(CAMPAIGN_COMPLETION_COUNT)
                .amountInCurrency(SOME_AMOUNT_IN_CURRENCY)
                .amountInTokens(SOME_AMOUNT_IN_TOKENS.toString());

        bonusType = baseCondition
                .type(CONDITION_TYPE_SIGNUP)
                .build();

        campaign = baseCampaign
                .rewardType(REWARD_TYPE_FIXED)
                .reward(String.valueOf(amountToSupply * 2))
                .conditions(createConditionArray(bonusType))
                .build();

        earnRule = EarnRule
                .builder()
                .ruleContentType(RuleContentType.TITLE)
                .localization(Localization.EN)
                .value(FakerUtils.title)
                .build();

        createCampaign(campaign, bonusType, earnRule)
                .then()
                .assertThat()
                .statusCode(SC_OK);

        val customerId = RegisterCustomerUtils.registerDefaultVerifiedCustomer().getCustomerId();

        return customerId;
    }

    public Response postMessage(MessagesPostRequestModel requestModel) {
        return getHeader()
                .body(requestModel)
                .post(MESSAGES_API_PATH)
                .thenReturn();
    }

    public Response getMessage(String partnerMessageId) {
        return getHeader()
                .get(MESSAGE_API_PATH.apply(partnerMessageId))
                .thenReturn();
    }

    Response deleteMessage(String partnerMessageId) {
        return getHeader()
                .delete(MESSAGE_API_PATH.apply(partnerMessageId))
                .thenReturn();
    }

    @Step("Delete All compaigns")
    private void deleteAllCampaigns() {
        // TODO: deletion of campaigns
        /*
        val campaigns = getCampaigns()
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(PaginatedCampaignListResponseModel.class)
                .getCampaigns();

        val campaignIds = Arrays.stream(campaigns)
                .map(campaign -> campaign.getId());

        campaignIds.forEach(id -> deleteCampaign(id));
        */
    }

    @AllArgsConstructor
    @Builder
    @Data
    @NoArgsConstructor
    @JsonNaming(UpperCamelCaseStrategy.class)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class ValidationErrorResponse {

        private String[] externalLocationId;
    }
}
