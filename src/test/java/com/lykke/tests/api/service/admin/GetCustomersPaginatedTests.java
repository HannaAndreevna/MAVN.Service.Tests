package com.lykke.tests.api.service.admin;

import static com.lykke.api.testing.api.common.GenerateUtils.generateRandomString;
import static com.lykke.tests.api.base.BasicFunctionalities.BASE_ASSET;
import static com.lykke.tests.api.base.BasicFunctionalities.EARN_TRANSACTION;
import static com.lykke.tests.api.base.BasicFunctionalities.P2P_TRANSFER;
import static com.lykke.tests.api.common.CommonConsts.AWAITILITY_140_SEC;
import static com.lykke.tests.api.common.CommonConsts.SMOKE_TEST;
import static com.lykke.tests.api.common.HelperUtils.simulateTrigger;
import static com.lykke.tests.api.service.admin.CampaignUtils.*;
import static com.lykke.tests.api.service.admin.GetCustomersUtils.getCustomerHistoryResponse;
import static com.lykke.tests.api.service.admin.GetCustomersUtils.getCustomersPaginatedResponse;
import static com.lykke.tests.api.service.admin.GetCustomersUtils.getCustomersPaginatedValidationResponse;
import static com.lykke.tests.api.service.campaigns.CampaignTests.STAKED_CAMPAIGN_ID_KEY;
import static com.lykke.tests.api.service.customer.CustomerWalletUtils.transferAsset;
import static com.lykke.tests.api.service.customer.LoginLogoutCustomerUtils.getUserToken;
import static com.lykke.tests.api.service.customermanagement.RegisterCustomerUtils.registerCustomer;
import static com.lykke.tests.api.service.customermanagement.RegisterCustomerUtils.registerDefaultVerifiedCustomer;
import static com.lykke.tests.api.service.operationshistory.OperationsUtils.getTransactionsByCustomerId;
import static com.lykke.tests.api.service.operationshistory.TransfersUtils.getOtherSideWalletAddress_LastTransfer;
import static com.lykke.tests.api.service.partnermanagement.PartnerManagementUtils.createPartner;
import static com.lykke.tests.api.service.referral.ReferralUtils.postReferral;
import static java.util.Collections.singletonMap;
import static org.apache.http.HttpStatus.SC_OK;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.lykke.api.testing.annotations.UserStoryId;
import com.lykke.tests.api.base.BaseApiTest;
import com.lykke.tests.api.common.HelperUtils.SimulateTriggerRequest;
import com.lykke.tests.api.common.enums.campaign.ConditionType;
import com.lykke.tests.api.service.admin.model.CustomerListRequest;
import com.lykke.tests.api.service.admin.model.customerhistory.CustomerOperationsHistoryRequest;
import com.lykke.tests.api.service.customer.model.TransferOperationResponse;
import com.lykke.tests.api.service.customermanagement.model.register.RegistrationRequestModel;
import com.lykke.tests.api.service.operationshistory.model.PaginatedCustomerOperationsResponse;
import com.lykke.tests.api.service.referral.model.ReferralCreateResponse;
import com.lykke.tests.api.service.referral.model.ReferralErrorCode;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;
import lombok.val;
import lombok.var;
import org.awaitility.Awaitility;
import org.awaitility.Duration;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class GetCustomersPaginatedTests extends BaseApiTest {

    private static final int VALID_PAGE_SIZE = 1;
    private static final int VALID_1ST_CURRENT_PAGE = 1;
    private static final int VALID_2ND_CURRENT_PAGE = 2;
    private static final String INVALID_CUSTOMER_ID = "8aa6a375-3e5a-42c2-bbed-3d74f87a7936";
    private static final Double SOME_AMOUNT = 2.0;

    static Stream<Arguments> getWrongPaginationParameters() {
        return TestDataForPaginatedTests.getWrongPaginationParameters();
    }

    static Stream<Arguments> getInvalidCustomerIds() {
        return TestDataForPaginatedTests.getInvalidCustomerIds();
    }

    @ParameterizedTest(name = "Run {index}: currentPage={0}, pageSize={1}")
    @MethodSource("getWrongPaginationParameters")
    @UserStoryId(storyId = {1040, 1037})
    void shouldReturnCustomersPaginated(int currentPage, int pageSize) {

        val requestObject = CustomerListRequest
                .builder()
                .currentPage(currentPage)
                .pageSize(pageSize)
                .searchValue("")
                .build();

        val validationResponse = getCustomersPaginatedValidationResponse(requestObject);

        assertEquals(requestObject.getValidationResponse(), validationResponse);
    }

    @Test
    @Tag(SMOKE_TEST)
    @UserStoryId(storyId = {1040, 1037})
    void shouldGetCustomerByFullEmail() {
        var user = new RegistrationRequestModel();
        val customerId = registerCustomer(user);
        val requestObject = CustomerListRequest
                .builder()
                .currentPage(VALID_1ST_CURRENT_PAGE)
                .pageSize(VALID_PAGE_SIZE)
                .searchValue(user.getEmail())
                .build();

        val actualCustomers = getCustomersPaginatedResponse(requestObject);

        assertAll(
                () -> assertEquals(1, actualCustomers.getCustomers().length),
                () -> assertEquals(user.getEmail(), actualCustomers.getCustomers()[0].getEmail()),
                () -> assertEquals(customerId, actualCustomers.getCustomers()[0].getCustomerId())
        );
    }

    @Test
    @Tag(SMOKE_TEST)
    @UserStoryId(storyId = {1040, 1037})
    void shouldGetCustomerByCustomerId() {
        var user = new RegistrationRequestModel();
        val customerId = registerCustomer(user);
        val requestObject = CustomerListRequest
                .builder()
                .currentPage(VALID_1ST_CURRENT_PAGE)
                .pageSize(VALID_PAGE_SIZE)
                .searchValue(customerId)
                .build();

        val actualCustomers = getCustomersPaginatedResponse(requestObject);

        assertAll(
                () -> assertEquals(1, actualCustomers.getCustomers().length),
                () -> assertEquals(user.getEmail(), actualCustomers.getCustomers()[0].getEmail()),
                () -> assertEquals(customerId, actualCustomers.getCustomers()[0].getCustomerId())
        );
    }

    @Test
    @Tag(SMOKE_TEST)
    @UserStoryId(storyId = {1040, 1037})
    void shouldGetCustomersPaginated() {
        var user1 = new RegistrationRequestModel();
        var user2 = new RegistrationRequestModel();
        val emailAddress01 = user1.getEmail();
        val customerId01 = registerCustomer(user1);
        val emailAddress02 = user2.getEmail();
        registerCustomer(user2);
        val requestObject = CustomerListRequest
                .builder()
                .currentPage(VALID_2ND_CURRENT_PAGE)
                .pageSize(VALID_PAGE_SIZE)
                .searchValue("")
                .build();

        val actualCustomers = getCustomersPaginatedResponse(requestObject);

        assertAll(
                () -> assertEquals(1, actualCustomers.getCustomers().length),
                () -> assertEquals(emailAddress01, actualCustomers.getCustomers()[0].getEmail()),
                () -> assertEquals(customerId01, actualCustomers.getCustomers()[0].getCustomerId())
        );
    }

    @Test
    @Tag(SMOKE_TEST)
    @UserStoryId(storyId = {1165, 1549})
    void shouldGetCustomerHistoryById() {
        val campaignId = createCampaignAndReturnId();
        val customer01Data = registerDefaultVerifiedCustomer();
        val senderToken = getUserToken(customer01Data.getEmail(), customer01Data.getPassword());
        val customer02Data = registerDefaultVerifiedCustomer();

        val requestObject = CustomerOperationsHistoryRequest
                .customerOperationsHistoryRequestBuilder()
                .customerId(customer01Data.getCustomerId())
                .pageSize(VALID_PAGE_SIZE)
                .currentPage(VALID_1ST_CURRENT_PAGE)
                .build();

        Awaitility.await()
                .atMost(AWAITILITY_140_SEC, TimeUnit.SECONDS)
                .pollInterval(Duration.TWO_SECONDS)
                .until(() -> {
                    val array = getCustomerHistoryResponse(requestObject).getOperations();
                    return array.length != 0 && array[0].getTransactionType() == EARN_TRANSACTION;
                });

        val transferResult = transferAsset(senderToken, customer02Data.getEmail(), SOME_AMOUNT, BASE_ASSET)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(TransferOperationResponse.class);
        assertNotNull(transferResult.getTransactionId());

        Awaitility.await().atMost(AWAITILITY_140_SEC, TimeUnit.SECONDS)
                .pollInterval(Duration.TWO_SECONDS)
                .until(() -> {
                    val array = getCustomerHistoryResponse(requestObject).getOperations();
                    return array.length != 0 && array[0].getTransactionType() == P2P_TRANSFER;
                });

        val otherSideWallet = getOtherSideWalletAddress_LastTransfer(customer01Data.getCustomerId(), 1, 1);

        val actualCustomerHistory = getCustomerHistoryResponse(requestObject);

        assertAll(
                () -> assertEquals(1, actualCustomerHistory.getOperations().length),
                () -> assertEquals(customer02Data.getCustomerId(),
                        actualCustomerHistory.getOperations()[0].getReceiverCustomerId()),
                () -> assertEquals("-2", actualCustomerHistory.getOperations()[0].getAmount()),
                () -> assertEquals(BASE_ASSET, actualCustomerHistory.getOperations()[0].getAssetSymbol()),
                () -> assertEquals(P2P_TRANSFER, actualCustomerHistory.getOperations()[0].getTransactionType()),
                () -> assertEquals(otherSideWallet, actualCustomerHistory.getOperations()[0].getWalletAddress())
        );
        deleteCampaign(campaignId);
    }

    @Disabled("neeeds investigationm, not yet ready")
    // TODO: finish the method
    @Test
    // TODO:   @Tag(SMOKE_TEST)
    @UserStoryId(3335)
    void shouldGetCustomerHistoryByIdForStaking() {
        val partnerData = createPartner(generateRandomString(10));
        val campaignId = createCampaignAndReturnId();

        val customer01Data = registerDefaultVerifiedCustomer();
        val senderToken = getUserToken(customer01Data.getEmail(), customer01Data.getPassword());
        val customer02Data = registerDefaultVerifiedCustomer();

        val postReferralResult = postReferral(customer02Data.getCustomerId())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(ReferralCreateResponse.class);

        assertEquals(ReferralErrorCode.NONE.getCode(), postReferralResult.getErrorCode());

        simulateTrigger(SimulateTriggerRequest
                .builder()
                .customerId(customer01Data.getCustomerId())
                .partnerId(partnerData.getId())
                .conditionType(ConditionType.ESTATE_LEAD_REFERRAL.getValue())
                .build(), singletonMap(STAKED_CAMPAIGN_ID_KEY, campaignId));

        val requestObject = CustomerOperationsHistoryRequest
                .customerOperationsHistoryRequestBuilder()
                .customerId(customer01Data.getCustomerId())
                .pageSize(VALID_PAGE_SIZE)
                .currentPage(VALID_1ST_CURRENT_PAGE)
                .build();

        Awaitility.await().atMost(AWAITILITY_140_SEC, TimeUnit.SECONDS)
                .pollInterval(Duration.TWO_SECONDS)
                .until(() -> {
                    val array = getCustomerHistoryResponse(requestObject).getOperations();
                    return array.length != 0 && array[0].getTransactionType() == EARN_TRANSACTION;
                });

        /*
        public IEnumerable<CustomerOperation> FromReferralStakes(IEnumerable<ReferralStakeResponse> source)
        {
            if (source != null)
            {
                return source.Select(item => new CustomerOperation
                {
                    Timestamp = item.Timestamp,
                    PartnerId = item.ReferralId,
                    TransactionId = item.ReferralId,
                    TransactionType = CustomerOperationTransactionType.ReferralStake,
                    ReceiverCustomerId = item.CustomerId,
                    CampaignName = item.CampaignName,
                    Amount = Money18.Abs(item.Amount),
                    AssetSymbol = item.AssetSymbol
                });
            }

            return new List<CustomerOperation>();
        }

        public IEnumerable<CustomerOperation> FromReleasedReferralStakes(IEnumerable<ReferralStakeResponse> source)
        {
            if (source != null)
            {
                return source.Select(item => new CustomerOperation
                {
                    Timestamp = item.Timestamp,
                    PartnerId = item.ReferralId,
                    TransactionId = item.ReferralId,
                    TransactionType = CustomerOperationTransactionType.ReleasedReferralStake,
                    ReceiverCustomerId = item.CustomerId,
                    CampaignName = item.CampaignName,
                    Amount = Money18.Abs(item.Amount),
                    AssetSymbol = item.AssetSymbol
                });
            }

            return new List<CustomerOperation>();
        }
        */

        val requestObject01 = CustomerOperationsHistoryRequest
                .customerOperationsHistoryRequestBuilder()
                .customerId(customer01Data.getCustomerId())
                .pageSize(VALID_PAGE_SIZE)
                .currentPage(VALID_1ST_CURRENT_PAGE)
                .build();

        Awaitility.await().atMost(AWAITILITY_140_SEC, TimeUnit.SECONDS)
                .pollInterval(Duration.TWO_SECONDS)
                .until(() -> {
                    val array = getCustomerHistoryResponse(requestObject01).getOperations();
                    return array.length != 0 && array[0].getTransactionType() == EARN_TRANSACTION;
                });

        simulateTrigger(SimulateTriggerRequest
                .builder()
                .customerId(customer02Data.getCustomerId())
                .partnerId(partnerData.getId())
                .conditionType(ConditionType.ESTATE_LEAD_REFERRAL.getValue())
                .build(), singletonMap(STAKED_CAMPAIGN_ID_KEY, campaignId));

        val requestObject02 = CustomerOperationsHistoryRequest
                .customerOperationsHistoryRequestBuilder()
                .customerId(customer02Data.getCustomerId())
                .pageSize(VALID_PAGE_SIZE)
                .currentPage(VALID_1ST_CURRENT_PAGE)
                .build();

        Awaitility.await().atMost(AWAITILITY_140_SEC, TimeUnit.SECONDS)
                .pollInterval(Duration.TWO_SECONDS)
                .until(() -> {
                    val array = getCustomerHistoryResponse(requestObject02).getOperations();
                    return array.length != 0 && array[0].getTransactionType() == EARN_TRANSACTION;
                });

        simulateTrigger(SimulateTriggerRequest
                .builder()
                .customerId(partnerData.getId())
                .partnerId(partnerData.getId())
                .conditionType(ConditionType.ESTATE_LEAD_REFERRAL.getValue())
                .build(), singletonMap(STAKED_CAMPAIGN_ID_KEY, campaignId));

        val requestObject03 = CustomerOperationsHistoryRequest
                .customerOperationsHistoryRequestBuilder()
                .customerId(customer02Data.getCustomerId())
                .pageSize(VALID_PAGE_SIZE)
                .currentPage(VALID_1ST_CURRENT_PAGE)
                .build();

        Awaitility.await().atMost(AWAITILITY_140_SEC, TimeUnit.SECONDS)
                .pollInterval(Duration.TWO_SECONDS)
                .until(() -> {
                    val array = getCustomerHistoryResponse(requestObject03).getOperations();
                    return array.length != 0 && array[0].getTransactionType() == EARN_TRANSACTION;
                });

        simulateTrigger(SimulateTriggerRequest
                .builder()
                .customerId(partnerData.getClientId())
                .partnerId(partnerData.getId())
                .conditionType(ConditionType.ESTATE_LEAD_REFERRAL.getValue())
                .build(), singletonMap(STAKED_CAMPAIGN_ID_KEY, campaignId));

        val requestObject04 = CustomerOperationsHistoryRequest
                .customerOperationsHistoryRequestBuilder()
                .customerId(customer02Data.getCustomerId())
                .pageSize(VALID_PAGE_SIZE)
                .currentPage(VALID_1ST_CURRENT_PAGE)
                .build();

        Awaitility.await().atMost(AWAITILITY_140_SEC, TimeUnit.SECONDS)
                .pollInterval(Duration.TWO_SECONDS)
                .until(() -> {
                    val array = getCustomerHistoryResponse(requestObject04).getOperations();
                    return array.length != 0 && array[0].getTransactionType() == EARN_TRANSACTION;
                });

        getTransactionsByCustomerId(customer01Data.getCustomerId())
                .then()
                .assertThat()
                .statusCode(SC_OK);

        getTransactionsByCustomerId(customer02Data.getCustomerId())
                .then()
                .assertThat()
                .statusCode(SC_OK);

        getTransactionsByCustomerId(partnerData.getId())
                .then()
                .assertThat()
                .statusCode(SC_OK);

        getTransactionsByCustomerId(partnerData.getClientId())
                .then()
                .assertThat()
                .statusCode(SC_OK);

        // checking operations history
        val senderTransactions = getTransactionsByCustomerId(customer01Data.getCustomerId())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(PaginatedCustomerOperationsResponse.class);

        val transferResult = transferAsset(senderToken, customer02Data.getEmail(), SOME_AMOUNT, BASE_ASSET)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(TransferOperationResponse.class);
        assertNotNull(transferResult.getTransactionId());

        Awaitility.await().atMost(AWAITILITY_140_SEC, TimeUnit.SECONDS)
                .pollInterval(Duration.TWO_SECONDS)
                .until(() -> {
                    val array = getCustomerHistoryResponse(requestObject).getOperations();
                    return array.length != 0 && array[0].getTransactionType() == P2P_TRANSFER;
                });

        val otherSideWallet = getOtherSideWalletAddress_LastTransfer(customer01Data.getCustomerId(), 1, 1);

        val actualCustomerHistory = getCustomerHistoryResponse(requestObject);

        assertAll(
                () -> assertEquals(1, actualCustomerHistory.getOperations().length),
                () -> assertEquals(customer02Data.getCustomerId(),
                        actualCustomerHistory.getOperations()[0].getReceiverCustomerId()),
                () -> assertEquals("-2", actualCustomerHistory.getOperations()[0].getAmount()),
                () -> assertEquals(BASE_ASSET, actualCustomerHistory.getOperations()[0].getAssetSymbol()),
                () -> assertEquals(P2P_TRANSFER, actualCustomerHistory.getOperations()[0].getTransactionType()),
                () -> assertEquals(otherSideWallet, actualCustomerHistory.getOperations()[0].getWalletAddress())
        );

        getCustomerHistoryResponse(requestObject).getOperations();
        getCustomerHistoryResponse(requestObject02).getOperations();

        deleteCampaign(campaignId);
    }

    // TODO: these tests are probably to be removed due to API change
    /*
    @ParameterizedTest(name = "Run {index}: currentPage={0}, pageSize={1}")
    @MethodSource("getWrongPaginationParameters")
    @UserStoryId(storyId = 1165)
    void shouldNotGetCustomerHistoryById_InvalidPagination(int currentPage, int pageSize) {
        val customerId = registerCustomer(new RegistrationRequestModel());
        val requestObject = CustomerListRequest
                .builder()
                .currentPage(currentPage)
                .pageSize(pageSize)
                .customerId(customerId)
                .build();

        val validationResponse = getCustomersPaginatedValidationResponse(requestObject);

        assertEquals(requestObject.getValidationResponse(), validationResponse);
    }

    @ParameterizedTest(name = "Run {index}: customerId={0}")
    @MethodSource("getInvalidCustomerIds")
    @UserStoryId(storyId = 1165)
    void shouldNotGetCustomerHistoryById_InvalidId(String customerId) {
        val requestObject = CustomerListRequest
                .builder()
                .currentPage(VALID_1ST_CURRENT_PAGE)
                .pageSize(VALID_PAGE_SIZE)
                .customerId(customerId)
                .build();

        val invalidCustomerIdResponse = getCustomerHistoryErrorResponse(requestObject);

        assertEquals(requestObject.getInvalidCustomerIdError(), invalidCustomerIdResponse);
    }

    @Test
    @UserStoryId(storyId = 1165)
    void shouldNotGetCustomerHistoryById_NonExisting() {
        val requestObject = CustomerListRequest
                .builder()
                .currentPage(VALID_1ST_CURRENT_PAGE)
                .pageSize(VALID_PAGE_SIZE)
          ////44      .customerId(INVALID_CUSTOMER_ID)
                .build();

        val nonExistingCustomerIdResponse = getNonExistingCustomerIdErrorResponse(requestObject);

        assertEquals(requestObject.getNonExistingCustomerIdError(), nonExistingCustomerIdResponse);
    }
    */
}
