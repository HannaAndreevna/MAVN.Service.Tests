package com.lykke.tests.api.service.privateblockchainfacade;

import static com.lykke.api.testing.api.common.GenerateUtils.generateRandomString;
import static com.lykke.api.testing.api.common.GenerateUtils.getRandomUuid;
import static com.lykke.tests.api.common.CommonConsts.AWAITILITY_INITIAL_BALANCE_WAIT_SECONDS;
import static com.lykke.tests.api.common.CommonConsts.AWAITILITY_PBF_TRANSFER_BALANCE_SEC;
import static com.lykke.tests.api.service.campaigns.CampaignUtils.createCampaign;
import static com.lykke.tests.api.service.campaigns.CampaignUtils.createConditionArray;
import static com.lykke.tests.api.service.campaigns.model.burnrules.Vertical.HOSPITALITY;
import static com.lykke.tests.api.service.customermanagement.RegisterCustomerUtils.registerCustomer;
import static com.lykke.tests.api.service.customermanagement.RegisterCustomerUtils.registerDefaultVerifiedCustomer;
import static com.lykke.tests.api.service.operationshistory.OperationsUtils.getTransactionsByCustomerId;
import static com.lykke.tests.api.service.privateblockchainfacade.PrivateBlockchainFacadeUtils.getCustomerBalance;
import static com.lykke.tests.api.service.privateblockchainfacade.PrivateBlockchainFacadeUtils.getNewOperations;
import static com.lykke.tests.api.service.privateblockchainfacade.PrivateBlockchainFacadeUtils.postBonuses;
import static com.lykke.tests.api.service.privateblockchainfacade.PrivateBlockchainFacadeUtils.postTransfer;
import static com.lykke.tests.api.service.privateblockchainfacade.model.TransferError.INVALID_RECEIVER_ID;
import static com.lykke.tests.api.service.privateblockchainfacade.model.TransferError.INVALID_SENDER_ID;
import static com.lykke.tests.api.service.privateblockchainfacade.model.TransferError.NOT_ENOUGH_FUNDS;
import static com.lykke.tests.api.service.privateblockchainfacade.model.TransferError.RECIPIENT_WALLET_MISSING;
import static com.lykke.tests.api.service.privateblockchainfacade.model.TransferError.SENDER_WALLET_MISSING;
import static com.lykke.tests.api.service.walletmanagement.WalletManagementUtils.balanceTransfer;
import static com.lykke.tests.api.service.walletmanagement.model.TransferErrorCode.SOURCE_CUSTOMER_NOT_FOUND;
import static com.lykke.tests.api.service.walletmanagement.model.TransferErrorCode.TARGET_CUSTOMER_NOT_FOUND;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.apache.http.HttpStatus.SC_OK;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.params.provider.Arguments.of;

import com.lykke.api.testing.annotations.UserStoryId;
import com.lykke.api.testing.api.common.FakerUtils;
import com.lykke.api.testing.api.common.GenerateUtils;
import com.lykke.tests.api.base.BaseApiTest;
import com.lykke.tests.api.common.enums.Localization;
import com.lykke.tests.api.common.enums.RuleContentType;
import com.lykke.tests.api.service.campaigns.model.BonusType;
import com.lykke.tests.api.service.campaigns.model.Campaign;
import com.lykke.tests.api.service.campaigns.model.EarnRule;
import com.lykke.tests.api.service.campaigns.model.RewardType;
import com.lykke.tests.api.service.operationshistory.model.PaginatedCustomerOperationsResponse;
import com.lykke.tests.api.service.privateblockchainfacade.model.BonusRewardError;
import com.lykke.tests.api.service.privateblockchainfacade.model.BonusRewardRequestModel;
import com.lykke.tests.api.service.privateblockchainfacade.model.BonusRewardResponseModel;
import com.lykke.tests.api.service.privateblockchainfacade.model.CustomerBalanceRequestModel;
import com.lykke.tests.api.service.privateblockchainfacade.model.CustomerBalanceResponseModel;
import com.lykke.tests.api.service.privateblockchainfacade.model.PrivateBlockChainFacadeCommonErrorResponseModel;
import com.lykke.tests.api.service.privateblockchainfacade.model.TransferError;
import com.lykke.tests.api.service.privateblockchainfacade.model.TransferRequestModel;
import com.lykke.tests.api.service.privateblockchainfacade.model.TransferResponseModel;
import com.lykke.tests.api.service.walletmanagement.model.TransferBalanceRequestModel;
import com.lykke.tests.api.service.walletmanagement.model.TransferBalanceResponse;
import com.lykke.tests.api.service.walletmanagement.model.TransferErrorCode;
import java.time.Instant;
import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import lombok.var;
import org.awaitility.Awaitility;
import org.awaitility.Duration;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

@Slf4j
public class TransferTests extends BaseApiTest {

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
    private static final String INVALID_CUSTOMER_ID_01 = "aaa";
    private static final String INVALID_CUSTOMER_ID_02 = "111";
    private static final Double INITIAL_AMOUNT_01 = 1000.0;
    private static final Double INITIAL_AMOUNT_02 = 0.0;
    private static final Double INITIAL_AMOUNT_03 = 1_000_000.0;
    private static final Double INITIAL_AMOUNT_04 = 1_500_000.0;
    private static final Double INITIAL_AMOUNT_05 = 500_000_000.0;
    private static final Double INITIAL_AMOUNT_06 = 500_000_000.0;
    private static final Double VALID_TRANSFER_AMOUNT_01 = 100.0;
    private static final Double VALID_TRANSFER_AMOUNT_02 = 100_000.0;
    private static final Double VALID_TRANSFER_AMOUNT_03 = 1_000_000_000.0;
    private static final long INVALID_TRANSFER_AMOUNT_01 = -1;
    private static final long INVALID_TRANSFER_AMOUNT_02 = 0;
    private static final long INVALID_TRANSFER_AMOUNT_03 = Long.MAX_VALUE + 1;

    private static final int TRANSFER_OPERATION_WAIT_SECONDS = 30;
    private static final String BALANCE_0_FIELD = "Balance[0]";

    static Stream<Arguments> getNonExistingCustomerData() {
        return Stream.of(
                of(registerCustomer(),
                        UUID.randomUUID().toString(), RECIPIENT_WALLET_MISSING, SC_OK),
                of(UUID.randomUUID().toString(),
                        registerCustomer(),
                        SENDER_WALLET_MISSING, SC_OK)
        );
    }

    static Stream<Arguments> getInvalidCustomerData() {
        return Stream.of(
                of(EMPTY, registerCustomer(),
                        INVALID_SENDER_ID, SC_BAD_REQUEST,
                        (Function<TransferRequestModel, String[]>) TransferRequestModel::getSenderCustomerIdMessage,
                        (Function<PrivateBlockChainFacadeCommonErrorResponseModel, String[]>)
                                PrivateBlockChainFacadeCommonErrorResponseModel::getSenderCustomerId),
                of(INVALID_CUSTOMER_ID_01,
                        registerCustomer(),
                        INVALID_SENDER_ID, SC_BAD_REQUEST,
                        (Function<TransferRequestModel, String[]>) TransferRequestModel::getSenderCustomerIdMessage,
                        (Function<PrivateBlockChainFacadeCommonErrorResponseModel, String[]>)
                                PrivateBlockChainFacadeCommonErrorResponseModel::getSenderCustomerId),
                of(INVALID_CUSTOMER_ID_02,
                        registerCustomer(),
                        INVALID_SENDER_ID, SC_BAD_REQUEST,
                        (Function<TransferRequestModel, String[]>) TransferRequestModel::getSenderCustomerIdMessage,
                        (Function<PrivateBlockChainFacadeCommonErrorResponseModel, String[]>)
                                PrivateBlockChainFacadeCommonErrorResponseModel::getSenderCustomerId),
                of(registerCustomer(), EMPTY,
                        INVALID_RECEIVER_ID, SC_BAD_REQUEST,
                        (Function<TransferRequestModel, String[]>) TransferRequestModel::getRecipientCustomerIdMessage,
                        (Function<PrivateBlockChainFacadeCommonErrorResponseModel, String[]>)
                                PrivateBlockChainFacadeCommonErrorResponseModel::getRecipientCustomerId),
                of(registerCustomer(),
                        INVALID_CUSTOMER_ID_01, INVALID_RECEIVER_ID, SC_BAD_REQUEST,
                        (Function<TransferRequestModel, String[]>) TransferRequestModel::getRecipientCustomerIdMessage,
                        (Function<PrivateBlockChainFacadeCommonErrorResponseModel, String[]>)
                                PrivateBlockChainFacadeCommonErrorResponseModel::getRecipientCustomerId),
                of(registerCustomer(),
                        INVALID_CUSTOMER_ID_02, INVALID_RECEIVER_ID, SC_BAD_REQUEST,
                        (Function<TransferRequestModel, String[]>) TransferRequestModel::getRecipientCustomerIdMessage,
                        (Function<PrivateBlockChainFacadeCommonErrorResponseModel, String[]>)
                                PrivateBlockChainFacadeCommonErrorResponseModel::getRecipientCustomerId)
        );
    }

    public static void deleteAllCampaigns() {
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

    static Stream<Arguments> getValidAmountsToTransfer() {
        return Stream.of(
                of(INITIAL_AMOUNT_01, INITIAL_AMOUNT_02, VALID_TRANSFER_AMOUNT_01),
                of(INITIAL_AMOUNT_03, INITIAL_AMOUNT_04, VALID_TRANSFER_AMOUNT_02),
                of(INITIAL_AMOUNT_05, INITIAL_AMOUNT_06, VALID_TRANSFER_AMOUNT_03)
        );
    }

    public static String getCustomerIdFundedViaCampaign(Double amountToSupply) {

        val amountToSupplyViaCampaignAndCondition = amountToSupply * 3;

        log.info("=======================================");
        log.info("%s", amountToSupplyViaCampaignAndCondition);
        log.info("=======================================");

        Campaign campaign;
        BonusType bonusType;
        EarnRule earnRule;
        BonusType.BonusTypeBuilder baseCondition;
        Campaign.CampaignBuilder baseCampaign;

        deleteAllCampaigns();

        baseCondition = BonusType
                .builder()
                .immediateReward(amountToSupply.toString())
                .completionCount(CONDITION_COMPLETION_COUNT)
                .allowConversionRate(false)
                .isAvailable(true)
                .isStakeable(false)
                .vertical(HOSPITALITY);

        baseCampaign = Campaign
                .campaignBuilder()
                .name(CAMPAIGN_NAME)
                .fromDate(CAMPAIGN_FROM)
                .toDate(CAMPAIGN_TO)
                .description(CAMPAIGN_DESC)
                .createdBy(CAMPAIGN_CREATED_BY)
                .completionCount(CAMPAIGN_COMPLETION_COUNT)
                .usePartnerCurrencyRate(false)
                .amountInTokens(Double.valueOf(100_000.0).toString())
                .amountInCurrency(50_000);

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

        val customerData = registerDefaultVerifiedCustomer();

        Awaitility.await()
                .atMost(AWAITILITY_INITIAL_BALANCE_WAIT_SECONDS, TimeUnit.SECONDS)
                .pollInterval(Duration.TWO_SECONDS)
                .until(() -> {
                    var balance = getCustomerBalance(customerData.getCustomerId());
                    log.info("===================================================");
                    log.info("Expected balance: " + amountToSupplyViaCampaignAndCondition);
                    log.info("Actual balance: " + balance);
                    log.info("===================================================");

                    return Double.valueOf(getCustomerBalance(customerData.getCustomerId()))
                            == amountToSupplyViaCampaignAndCondition;
                });

        return customerData.getCustomerId();
    }

    static Stream<Arguments> getNonExistingCustomerIds() {
        return Stream.of(
                of(getRandomUuid(), getRandomUuid(), SOURCE_CUSTOMER_NOT_FOUND),
                of(getRandomUuid(), registerCustomer(),
                        SOURCE_CUSTOMER_NOT_FOUND),
                of(registerCustomer(), getRandomUuid(),
                        TARGET_CUSTOMER_NOT_FOUND)
        );
    }

    @Disabled("sending POST to PBF is not recommended now, use WM")
    @ParameterizedTest(name = "Run {index}: senderAmount={0}, recipientAmount={1}, transferAmount={2}")
    @MethodSource("getValidAmountsToTransfer")
    @UserStoryId(storyId = {976, 1562})
    void shouldAddBalanceIfValidAmountViaPrivateBlockchainFacade(Double senderAmount, Double recipientAmount,
            Double transferAmount) {
        val initialSenderAmount = senderAmount * 3;
        val initialRecipientAmount = recipientAmount * 3;
        val expectedSenderAmount = initialSenderAmount - transferAmount.longValue();
        val expectedRecipientAmount = initialRecipientAmount + transferAmount.longValue();
        val senderId = PrivateBlockchainFacadeUtils.createCustomerFundedViaBonusReward(senderAmount).getCustomerId();
        val recipientId = PrivateBlockchainFacadeUtils.createCustomerFundedViaBonusReward(recipientAmount)
                .getCustomerId();

        Awaitility.await()
                .atMost(AWAITILITY_INITIAL_BALANCE_WAIT_SECONDS, TimeUnit.SECONDS)
                .pollInterval(Duration.TWO_SECONDS)
                .until(() -> {
                    log.info("===============================================================================");
                    log.info("Sender's expected initial balance: " + initialSenderAmount);
                    log.info("Recipient's expected initial balance: " + initialRecipientAmount);
                    log.info("Sender's actual initial balance: " + getCustomerBalance(senderId));
                    log.info("Recipient's actual initial balance: " + getCustomerBalance(recipientId));
                    log.info("===============================================================================");
                    return initialSenderAmount == Double.valueOf(getCustomerBalance(senderId))
                            && initialRecipientAmount == Double.valueOf(getCustomerBalance(recipientId));
                });

        TransferRequestModel requestObject = TransferRequestModel
                .builder()
                .senderCustomerId(senderId)
                .recipientCustomerId(recipientId)
                .amount(transferAmount.toString())
                .transferId(generateRandomString(100))
                .build();
        val actualResult = postTransfer(requestObject)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(TransferResponseModel.class);

        Awaitility.await()
                .atMost(AWAITILITY_PBF_TRANSFER_BALANCE_SEC, TimeUnit.SECONDS)
                .pollInterval(Duration.TWO_SECONDS)
                .until(() -> {

                    log.info("===============================================================================");
                    log.info("Sender's expected resulting balance: " + expectedSenderAmount);
                    log.info("Recipient's expected resulting balance: " + expectedRecipientAmount);
                    log.info("Sender's actual resulting balance: " + getCustomerBalance(senderId));
                    log.info("Recipient's actual resulting balance: " + getCustomerBalance(recipientId));
                    log.info("===============================================================================");
                    return expectedSenderAmount == Double.valueOf(getCustomerBalance(senderId))
                            && expectedRecipientAmount == Double.valueOf(getCustomerBalance(recipientId));
                });

        assertAll(
                () -> assertEquals(TransferError.NONE, actualResult.getError()),
                () -> assertEquals(expectedSenderAmount, getCustomerBalance(senderId)),
                () -> assertEquals(expectedRecipientAmount, getCustomerBalance(recipientId))
        );
    }

    @ParameterizedTest(name = "Run {index}: senderAmount={0}, recipientAmount={1}, transferAmount={2}")
    @MethodSource("getValidAmountsToTransfer")
    @UserStoryId(storyId = {976, 1562, 1179, 1022})
    void shouldAddBalanceIfValidAmountViaWalletManagement(Double senderAmount, Double recipientAmount,
            Double transferAmount) {
        final Double initialSenderAmount = senderAmount * 3;
        final Double initialRecipientAmount = recipientAmount * 3;
        val expectedSenderAmount = initialSenderAmount - transferAmount.longValue();
        val expectedRecipientAmount = initialRecipientAmount + transferAmount.longValue();

        val senderData = PrivateBlockchainFacadeUtils.createCustomerFundedViaBonusReward(senderAmount);
        val recipientData = PrivateBlockchainFacadeUtils.createCustomerFundedViaBonusReward(recipientAmount);

        val senderId = senderData.getCustomerId();
        val recipientId = recipientData.getCustomerId();

        Awaitility.await()
                .atMost(AWAITILITY_INITIAL_BALANCE_WAIT_SECONDS, TimeUnit.SECONDS)
                .pollInterval(Duration.TWO_SECONDS)
                .until(() -> {
                    log.info("===============================================================================");
                    log.info("Sender's expected initial balance: " + initialSenderAmount);
                    log.info("Recipient's expected initial balance: " + initialRecipientAmount);
                    log.info("Sender's actual initial balance: " + getCustomerBalance(senderId));
                    log.info("Recipient's actual initial balance: " + getCustomerBalance(recipientId));
                    log.info("===============================================================================");

                    return senderData.getNewAmount() + senderData.getExtraAmount() == Double
                            .valueOf(getCustomerBalance(senderData.getCustomerId()))
                            && recipientData.getNewAmount() + recipientData.getExtraAmount() == Double
                            .valueOf(getCustomerBalance(recipientData.getCustomerId()));
                });

        val requestObject = TransferBalanceRequestModel
                .builder()
                .senderCustomerId(senderId)
                .receiverCustomerId(recipientId)
                .amount(transferAmount.toString())
                .operationId(generateRandomString(100))
                .build();
        val actualResult = balanceTransfer(requestObject)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(TransferBalanceResponse.class);

        Awaitility.await()
                .atMost(AWAITILITY_PBF_TRANSFER_BALANCE_SEC, TimeUnit.SECONDS)
                .pollInterval(Duration.TWO_SECONDS)
                .until(() -> {

                    log.info("===============================================================================");
                    log.info("Sender's expected resulting balance: " + expectedSenderAmount);
                    log.info("Recipient's expected resulting balance: " + expectedRecipientAmount);
                    log.info("Sender's actual resulting balance: " + getCustomerBalance(senderId));
                    log.info("Recipient's actual resulting balance: " + getCustomerBalance(recipientId));
                    log.info("===============================================================================");

                    return senderData.getNewAmount() - transferAmount + senderData.getExtraAmount() == Double
                            .valueOf(getCustomerBalance(senderData.getCustomerId()))
                            && recipientData.getNewAmount() + transferAmount + recipientData.getExtraAmount() == Double
                            .valueOf(getCustomerBalance(recipientData.getCustomerId()));
                });

        assertAll(
                () -> assertEquals(TransferErrorCode.NONE, actualResult.getErrorCode()),
                () -> assertEquals(expectedSenderAmount, getCustomerBalance(senderId)),
                () -> assertEquals(expectedRecipientAmount, getCustomerBalance(recipientId))
        );

        val senderTransactions = getTransactionsByCustomerId(senderId)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(PaginatedCustomerOperationsResponse.class);

        val recipientTransactions = getTransactionsByCustomerId(recipientId)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(PaginatedCustomerOperationsResponse.class);

        assertAll(
                () -> assertEquals(initialSenderAmount, Arrays.stream(senderTransactions.getBonusCashIns())
                        .map(tran -> Double.valueOf(tran.getAmount())).reduce(0.0, (a, b) -> a + b)),
                () -> assertEquals(initialRecipientAmount,
                        0.0 == initialRecipientAmount ? 0.0
                                : Arrays.stream(recipientTransactions.getBonusCashIns())
                                        .mapToDouble(tran -> Double.valueOf(tran.getAmount()))
                                        .reduce(0.0, (a, b) -> a + b)),
                () -> assertEquals(transferAmount, Arrays.stream(senderTransactions.getTransfers())
                        .map(tran -> Double.valueOf(tran.getAmount())).reduce(0.0, (a, b) -> a + b)),
                () -> assertEquals(transferAmount, Arrays.stream(recipientTransactions.getTransfers())
                        .map(tran -> Double.valueOf(tran.getAmount())).reduce(0.0, (a, b) -> a + b))
        );
    }

    @ParameterizedTest(name = "Run {index}: senderId={0}, recipientId={1}, errorCode={2}")
    @MethodSource("getNonExistingCustomerIds")
    @UserStoryId(2080)
    void shouldNotAddBalanceIfCustomerIsNotRegistered(String senderId, String recipientId,
            TransferErrorCode errorCode) {
        val requestObject = TransferBalanceRequestModel
                .builder()
                .senderCustomerId(senderId)
                .receiverCustomerId(recipientId)
                .amount("1.0")
                .operationId(generateRandomString(100))
                .build();
        val actualResult = balanceTransfer(requestObject)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(TransferBalanceResponse.class);

        assertAll(
                () -> assertNull(actualResult.getExternalOperationId()),
                () -> assertNull(actualResult.getTransactionId()),
                () -> assertEquals(errorCode, actualResult.getErrorCode())
        );
    }

    @Test
    @UserStoryId(976)
    void shouldNotAddBalanceIfNotEnoughFunds() {
        val requestObject = TransferRequestModel
                .builder()
                .senderCustomerId(registerCustomer())
                .recipientCustomerId(
                        registerCustomer())
                .amount(VALID_TRANSFER_AMOUNT_01.toString())
                .transferId(generateRandomString(100))
                .build();
        val actualResult = postTransfer(requestObject)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(TransferResponseModel.class);

        assertEquals(NOT_ENOUGH_FUNDS, actualResult.getError());
    }

    @ParameterizedTest
    @MethodSource("getNonExistingCustomerData")
    @UserStoryId(976)
    void shouldNotAddBalanceIfNonExistingCustomerId(String senderId, String receiverId, TransferError errorCode,
            int expectedStatus) {
        val requestObject = TransferRequestModel
                .builder()
                .senderCustomerId(senderId)
                .recipientCustomerId(receiverId)
                .amount(VALID_TRANSFER_AMOUNT_01.toString())
                .transferId(generateRandomString(100))
                .build();
        val actualResult = postTransfer(requestObject)
                .then()
                .assertThat()
                .statusCode(expectedStatus)
                .extract()
                .as(TransferResponseModel.class);

        assertEquals(errorCode, actualResult.getError());
    }

    @ParameterizedTest
    @MethodSource("getInvalidCustomerData")
    @UserStoryId(976)
    void shouldNotAddBalanceIfInvalidCustomerId(
            String senderId,
            String receiverId,
            TransferError errorCode,
            int expectedStatus,
            Function<TransferRequestModel, String[]> expectedAction,
            Function<PrivateBlockChainFacadeCommonErrorResponseModel, String[]> actualAction) {
        val requestObject = TransferRequestModel
                .builder()
                .senderCustomerId(senderId)
                .recipientCustomerId(receiverId)
                .amount(VALID_TRANSFER_AMOUNT_01.toString())
                .transferId(generateRandomString(100))
                .build();
        val actualResult = postTransfer(requestObject)
                .then()
                .assertThat()
                .statusCode(expectedStatus)
                .extract()
                .as(PrivateBlockChainFacadeCommonErrorResponseModel.class);

        assertEquals(expectedAction.apply(requestObject)[0], actualAction.apply(actualResult)[0]);
    }

    @ParameterizedTest
    @ValueSource(doubles = {INVALID_TRANSFER_AMOUNT_01, INVALID_TRANSFER_AMOUNT_02, INVALID_TRANSFER_AMOUNT_03})
    @UserStoryId(storyId = {976, 1851})
    void shouldNotAddBalanceIfInvalidAmount(Double amount) {
        val senderId = registerCustomer();

        val requestObject = TransferRequestModel
                .builder()
                .senderCustomerId(senderId)
                .recipientCustomerId(
                        registerCustomer())
                .amount(amount.toString())
                .transferId(generateRandomString(100))
                .build();
        PrivateBlockChainFacadeCommonErrorResponseModel actualResult = postTransfer(requestObject)
                .then()
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .extract()
                .as(PrivateBlockChainFacadeCommonErrorResponseModel.class);

        assertEquals(requestObject.getAmountMessage()[0], actualResult.getAmount()[0]);
    }

    @Test
    @UserStoryId(976)
    void shouldNotAddBalanceIfEmptyTransferId() {
        val requestObject = TransferRequestModel
                .builder()
                .senderCustomerId(registerCustomer())
                .recipientCustomerId(
                        registerCustomer())
                .amount(VALID_TRANSFER_AMOUNT_01.toString())
                .transferId(EMPTY)
                .build();
        val actualResult = postTransfer(requestObject)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(TransferResponseModel.class);

        assertEquals(NOT_ENOUGH_FUNDS, actualResult.getError());
    }

    private String getFundedCustomerId(Double amountToSupply) {
        val customerId = registerCustomer();

        val postBonusResponse = postBonuses(BonusRewardRequestModel
                .builder()
                .customerId(customerId)
                .rewardRequestId(GenerateUtils.generateRandomString(100))
                .amount(amountToSupply.toString())
                .bonusReason("any reason")
                .build())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(BonusRewardResponseModel.class);

        assertEquals(BonusRewardError.NONE, postBonusResponse.getError());

        Awaitility.await().atMost(TRANSFER_OPERATION_WAIT_SECONDS, TimeUnit.SECONDS)
                .with()
                .pollInterval(Duration.FIVE_SECONDS)
                .until(() -> {
                    getNewOperations();
                    val resultingBalance = getCustomerBalance(CustomerBalanceRequestModel
                            .builder()
                            .customerId(customerId)
                            .build())
                            .then()
                            .assertThat()
                            .statusCode(SC_OK)
                            .extract()
                            .as(CustomerBalanceResponseModel.class)
                            .getTotal();
                    log.info("Expected new amount: " + amountToSupply);
                    log.info("Actual new amount: " + resultingBalance);
                    return amountToSupply == Double.valueOf(resultingBalance);
                });

        val customerBalance = getCustomerBalance(CustomerBalanceRequestModel
                .builder()
                .customerId(customerId)
                .build())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(CustomerBalanceResponseModel.class)
                .getTotal();
        assertEquals(amountToSupply, customerBalance);

        return customerId;
    }

    private String getFundedCustomerIdForBigAmount(Double amountToSupply) {
        val customerId = registerCustomer();

        val postBonusResponse = postBonuses(BonusRewardRequestModel
                .builder()
                .customerId(customerId)
                .rewardRequestId(GenerateUtils.generateRandomString(100))
                .amount(amountToSupply.toString())
                .bonusReason("some reason")
                .build())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(BonusRewardResponseModel.class);

        assertEquals(BonusRewardError.NONE, postBonusResponse.getError());

        Awaitility.await().atMost(TRANSFER_OPERATION_WAIT_SECONDS, TimeUnit.SECONDS)
                .with()
                .pollInterval(Duration.FIVE_SECONDS)
                .until(() -> {
                    getNewOperations();
                    val resultingBalance = getCustomerBalance(CustomerBalanceRequestModel
                            .builder()
                            .customerId(customerId)
                            .build())
                            .then()
                            .assertThat()
                            .statusCode(SC_OK)
                            .extract()
                            .as(CustomerBalanceResponseModel.class)
                            .getTotal();
                    log.info("Expected new amount: " + amountToSupply);
                    log.info("Actual new amount: " + resultingBalance);
                    return amountToSupply - 1000 < Double.valueOf(resultingBalance)
                            && Double.valueOf(resultingBalance) < amountToSupply + 1000;
                });

        val customerBalance = getCustomerBalance(CustomerBalanceRequestModel
                .builder()
                .customerId(customerId)
                .build())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(CustomerBalanceResponseModel.class)
                .getTotal();
        assertTrue(amountToSupply - 1000 < Double.valueOf(customerBalance)
                && Double.valueOf(customerBalance) < amountToSupply + 1000);

        return customerId;
    }
}
