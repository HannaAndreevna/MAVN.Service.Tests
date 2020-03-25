package com.lykke.tests.api.service.privateblockchainfacade;

import static com.lykke.api.testing.api.base.RequestHeader.getHeader;
import static com.lykke.api.testing.api.common.GenerateUtils.generateRandomString;
import static com.lykke.tests.api.base.Paths.PRIVATE_BLOCKCHAIN_ACCEPTED_API_PATH;
import static com.lykke.tests.api.base.Paths.PRIVATE_BLOCKCHAIN_ALL_ACCEPTED_API_PATH;
import static com.lykke.tests.api.base.Paths.PRIVATE_BLOCKCHAIN_BONUSES_API_PATH;
import static com.lykke.tests.api.base.Paths.PRIVATE_BLOCKCHAIN_CUSTOMER_BALANCE_API_PATH;
import static com.lykke.tests.api.base.Paths.PRIVATE_BLOCKCHAIN_FAILED_API_PATH;
import static com.lykke.tests.api.base.Paths.PRIVATE_BLOCKCHAIN_NEW_API_PATH;
import static com.lykke.tests.api.base.Paths.PRIVATE_BLOCKCHAIN_SUCCEEDED_API_PATH;
import static com.lykke.tests.api.base.Paths.PRIVATE_BLOCKCHAIN_TOTAL_AMOUNT_API_PATH;
import static com.lykke.tests.api.base.Paths.PRIVATE_BLOCKCHAIN_TRANSFERS_API_PATH;
import static com.lykke.tests.api.base.Paths.PRIVATE_BLOCKCHAIN_WALLETS_API_PATH;
import static com.lykke.tests.api.base.Paths.PrivateBlockchainFacade.GENERIC_TRANSFERS_API_PATH;
import static com.lykke.tests.api.common.CommonConsts.AWAITILITY_INITIAL_BALANCE_WAIT_SECONDS;
import static com.lykke.tests.api.service.admin.LoginAdminUtils.getAdminToken;
import static com.lykke.tests.api.service.customermanagement.RegisterCustomerUtils.registerDefaultVerifiedCustomer;
import static com.lykke.tests.api.service.operationshistory.OperationsUtils.getTransactionsByCustomerId;
import static org.apache.http.HttpStatus.SC_OK;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.lykke.tests.api.common.model.CustomerBalanceInfo;
import com.lykke.tests.api.service.operationshistory.model.PaginatedCustomerOperationsResponse;
import com.lykke.tests.api.service.privateblockchainfacade.model.BonusRewardError;
import com.lykke.tests.api.service.privateblockchainfacade.model.BonusRewardRequestModel;
import com.lykke.tests.api.service.privateblockchainfacade.model.BonusRewardResponseModel;
import com.lykke.tests.api.service.privateblockchainfacade.model.CustomerBalanceRequestModel;
import com.lykke.tests.api.service.privateblockchainfacade.model.CustomerBalanceResponseModel;
import com.lykke.tests.api.service.privateblockchainfacade.model.CustomerWalletCreationRequestModel;
import com.lykke.tests.api.service.privateblockchainfacade.model.GenericTransferRequestModel;
import com.lykke.tests.api.service.privateblockchainfacade.model.TransferRequestModel;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.awaitility.Awaitility;
import org.awaitility.Duration;

@Slf4j
@UtilityClass
public class PrivateBlockchainFacadeUtils {

    // TODO: learn whether we need the admin account or don't
    Response getNewOperations() {
        return getHeader(getAdminToken())
                .get(PRIVATE_BLOCKCHAIN_NEW_API_PATH)
                .thenReturn();
    }

    Response getAcceptedOperations() {
        return getHeader(getAdminToken())
                .get(PRIVATE_BLOCKCHAIN_ALL_ACCEPTED_API_PATH)
                .thenReturn();
    }

    Response putAcceptedOperations(String transactionId, String transationHash) {
        return getHeader(getAdminToken())
                .queryParam("hash", transationHash)
                .put(PRIVATE_BLOCKCHAIN_ACCEPTED_API_PATH.apply(transactionId))
                .thenReturn();
    }

    Response putFailedOperations(String hash) {
        return getHeader(getAdminToken())
                .put(PRIVATE_BLOCKCHAIN_FAILED_API_PATH.apply(hash))
                .thenReturn();
    }

    Response putSucceededOperations(String hash) {
        return getHeader(getAdminToken())
                .put(PRIVATE_BLOCKCHAIN_SUCCEEDED_API_PATH.apply(hash))
                .thenReturn();
    }

    Response postWallets(String customerId) {
        return getHeader(getAdminToken())
                .body(CustomerWalletCreationRequestModel
                        .builder()
                        .customerId(customerId)
                        .build())
                .post(PRIVATE_BLOCKCHAIN_WALLETS_API_PATH)
                .thenReturn();
    }

    Response getTotalAmount() {
        return getHeader(getAdminToken())
                .get(PRIVATE_BLOCKCHAIN_TOTAL_AMOUNT_API_PATH)
                .thenReturn();
    }

    Response postBonuses(BonusRewardRequestModel requestObject) {
        return getHeader(getAdminToken())
                .body(requestObject)
                .post(PRIVATE_BLOCKCHAIN_BONUSES_API_PATH)
                .thenReturn();
    }

    @Step
    Response getCustomerBalance(CustomerBalanceRequestModel requestObject) {
        return getHeader(getAdminToken())
                .get(PRIVATE_BLOCKCHAIN_CUSTOMER_BALANCE_API_PATH.apply(requestObject.getCustomerId()))
                .thenReturn();
    }

    @Step
    public static String getCustomerBalance(String customerId) {
        return getCustomerBalance(CustomerBalanceRequestModel
                .builder()
                .customerId(customerId)
                .build())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(CustomerBalanceResponseModel.class)
                .getTotal();
    }

    @Step
    public Response postTransfer(TransferRequestModel requestObject) {
        return getHeader(getAdminToken())
                .body(requestObject)
                .post(PRIVATE_BLOCKCHAIN_TRANSFERS_API_PATH)
                .thenReturn();
    }

    Response postGenericTransfer(GenericTransferRequestModel requestModel) {
        return getHeader(getAdminToken())
                .body(requestModel)
                .post(GENERIC_TRANSFERS_API_PATH)
                .thenReturn();
    }

    public static CustomerBalanceInfo createCustomerFundedViaBonusReward(Double amountToSupply,
            boolean isEmailVerified) {
        val SOME_BONUS_REASON = "some bonus reason";
        val customerData = isEmailVerified ? registerDefaultVerifiedCustomer(true)
                : registerDefaultVerifiedCustomer(false);



/*
        assertAll(
                () -> assertEquals(initialSenderAmount, Arrays.stream(senderTransactions.getBonusCashIns())
                        .map(tran -> Double.valueOf(tran.getAmount())).reduce(0.0, (a, b) -> a + b)),
                () -> assertEquals(initialRecipientAmount,
                        0.0 == initialRecipientAmount ? 0.0
                                : Arrays.stream(recipientTransactions.getBonusCashIns())
                                        .mapToDouble(tran -> Double.valueOf(tran.getAmount()))
                                        .reduce(0.0, (a, b) -> a + b)),
        */

        Awaitility.await()
                // TODO:
                .atMost(AWAITILITY_INITIAL_BALANCE_WAIT_SECONDS, TimeUnit.SECONDS)
                //.atMost(20, TimeUnit.SECONDS)
                .pollInterval(Duration.TWO_SECONDS)
                ////xx
                .until(() -> {
                    val balance = getCustomerBalance(customerData.getCustomerId());
                    val customerBonusCashInTransactions = getTransactionsByCustomerId(customerData.getCustomerId())
                            .then()
                            .assertThat()
                            .statusCode(SC_OK)
                            .extract()
                            .as(PaginatedCustomerOperationsResponse.class);
                    log.info("===================================================");
                    log.info("Expected balance: " + amountToSupply);
                    log.info("Actual balance: " + Double.valueOf(balance));
                    log.info("===================================================");

                    System.out.println("===================================================");
                    System.out.println("Expected balance: " + amountToSupply);
                    System.out.println("Actual balance: " + Double.valueOf(balance));
                    System.out.println("Bonuses: " + Arrays.stream(customerBonusCashInTransactions.getBonusCashIns())
                            .map(tran -> Double.valueOf(tran.getAmount()))
                            .reduce(0.0, (a, b) -> a + b));
                    System.out.println("===================================================");

                    return Double.valueOf(balance) >= amountToSupply
                            && null != customerBonusCashInTransactions.getBonusCashIns()
                            && 0 < customerBonusCashInTransactions.getBonusCashIns().length
                            && Arrays.stream(customerBonusCashInTransactions.getBonusCashIns())
                            .map(tran -> Double.valueOf(tran.getAmount()))
                            .reduce(0.0, (a, b) -> a + b) >= amountToSupply;
                });

        val campaignId = UUID.randomUUID();
        val conditionId = UUID.randomUUID();
        BonusRewardResponseModel actualResult = postBonuses(
                BonusRewardRequestModel
                        .builder()
                        .customerId(customerData.getCustomerId())
                        .amount(amountToSupply.toString())
                        .rewardRequestId(generateRandomString(10))
                        .bonusReason(SOME_BONUS_REASON)
                        .campaignId(campaignId)
                        .conditionId(conditionId)
                        .build())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(BonusRewardResponseModel.class);

        assertEquals(BonusRewardError.NONE, actualResult.getError(), "failed to POST bonus");

        Awaitility.await()
                .atMost(AWAITILITY_INITIAL_BALANCE_WAIT_SECONDS, TimeUnit.SECONDS)
                .pollInterval(Duration.TWO_SECONDS)
                .until(() -> {
                    String balance = getCustomerBalance(customerData.getCustomerId());
                    log.info("===================================================");
                    log.info("Expected balance: " + amountToSupply);
                    log.info("Actual balance: " + Double.valueOf(balance));
                    log.info("===================================================");

                    return Double.valueOf(balance) >= amountToSupply;
                });

        String customerInitialBalance = getCustomerBalance(customerData.getCustomerId());
        log.info("===================================================");
        log.info("Expected balance: " + amountToSupply);
        log.info("Actual balance: " + customerInitialBalance);
        log.info("===================================================");

        assertTrue(amountToSupply <= Double.valueOf(customerInitialBalance));

        return CustomerBalanceInfo
                .customerBalanceInfoBuilder()
                .customerId(customerData.getCustomerId())
                .newAmount(amountToSupply)
                .extraAmount(Double.valueOf(customerInitialBalance) - amountToSupply)
                .email(customerData.getEmail())
                .phoneNumber(customerData.getPhoneNumber())
                .firstName(customerData.getFirstName())
                .lastName(customerData.getLastName())
                .countryPhoneCodeId(customerData.getCountryPhoneCodeId())
                .password(customerData.getPassword())
                .campaignId(campaignId.toString())
                .conditionId(conditionId.toString())
                .build();
    }

    public static CustomerBalanceInfo createCustomerFundedViaBonusReward(Double amountToSupply) {
        return createCustomerFundedViaBonusReward(amountToSupply, true);
    }

    public static CustomerBalanceInfo getCustomerIdFundedViaBonusEngine(Double amountToSupply,
            boolean isEmailVerified) {
        val SOME_BONUS_REASON = "some bonus reason";
        val customerData = isEmailVerified ? registerDefaultVerifiedCustomer(true)
                : registerDefaultVerifiedCustomer(false);

        Awaitility.await()
                // TODO: .atMost(AWAITILITY_INITIAL_BALANCE_WAIT_SECONDS, TimeUnit.SECONDS)
                .atMost(20, TimeUnit.SECONDS)
                .pollInterval(Duration.TWO_SECONDS)
                .until(() -> {
                    String balance = getCustomerBalance(customerData.getCustomerId());
                    log.info("===================================================");
                    log.info("Expected balance: " + amountToSupply);
                    log.info("Actual balance: " + Double.valueOf(balance));
                    log.info("===================================================");

                    return Double.valueOf(balance) >= 0.0;
                });

        BonusRewardResponseModel actualResult = postBonuses(
                BonusRewardRequestModel
                        .builder()
                        .customerId(customerData.getCustomerId())
                        .amount(amountToSupply.toString())
                        .rewardRequestId(generateRandomString(10))
                        .bonusReason(SOME_BONUS_REASON)
                        .campaignId(UUID.randomUUID())
                        .conditionId(UUID.randomUUID())
                        .build())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(BonusRewardResponseModel.class);

        assertEquals(BonusRewardError.NONE, actualResult.getError(), "failed to POST bonus");

        Awaitility.await()
                .atMost(AWAITILITY_INITIAL_BALANCE_WAIT_SECONDS, TimeUnit.SECONDS)
                .pollInterval(Duration.TWO_SECONDS)
                .until(() -> {
                    String balance = getCustomerBalance(customerData.getCustomerId());
                    log.info("===================================================");
                    log.info("Expected balance: " + amountToSupply);
                    log.info("Actual balance: " + Double.valueOf(balance));
                    log.info("===================================================");

                    return Double.valueOf(balance) >= amountToSupply;
                });

        String customerInitialBalance = getCustomerBalance(customerData.getCustomerId());
        log.info("===================================================");
        log.info("Expected balance: " + amountToSupply);
        log.info("Actual balance: " + customerInitialBalance);
        log.info("===================================================");

        assertTrue(amountToSupply <= Double.valueOf(customerInitialBalance));

        return CustomerBalanceInfo
                .customerBalanceInfoBuilder()
                .customerId(customerData.getCustomerId())
                .newAmount(amountToSupply)
                .extraAmount(Double.valueOf(customerInitialBalance) - amountToSupply)
                .email(customerData.getEmail())
                .phoneNumber(customerData.getPhoneNumber())
                .firstName(customerData.getFirstName())
                .lastName(customerData.getLastName())
                .countryPhoneCodeId(customerData.getCountryPhoneCodeId())
                .password(customerData.getPassword())
                .build();
    }
}
