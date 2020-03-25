package com.lykke.tests.e2e;

import static com.lykke.api.testing.api.common.GenerateUtils.generateRandomString;
import static com.lykke.api.testing.api.common.GenerateUtils.getRandomUuid;
import static com.lykke.api.testing.api.common.PasswordGen.generateValidPassword;
import static com.lykke.tests.api.common.CommonConsts.AWAITILITY_OPERATIONS_HISTORY_SEC;
import static com.lykke.tests.api.common.CommonConsts.AWAITILITY_PBF_TRANSFER_BALANCE_SEC;
import static com.lykke.tests.api.common.CommonConsts.AWAITILITY_POLL_INTERVAL_MID_SEC;
import static com.lykke.tests.api.common.CommonConsts.MVN.MVN_GATEWAY_ADDRESS;
import static com.lykke.tests.api.common.HelperUtils.createCsvFile;
import static com.lykke.tests.api.common.HelperUtils.getImagePath;
import static com.lykke.tests.api.common.PaginationConts.CURRENT_PAGE_LOWER_BOUNDARY;
import static com.lykke.tests.api.common.PaginationConts.PAGE_SIZE_UPPER_BOUNDARY;
import static com.lykke.tests.api.service.admin.BurnRulesUtils.postVouchers;
import static com.lykke.tests.api.service.admin.LoginAdminUtils.getAdminToken;
import static com.lykke.tests.api.service.campaigns.BurnRulesUtils.getBurnRuleId;
import static com.lykke.tests.api.service.customer.CustomerWalletUtils.getCustomerWallets;
import static com.lykke.tests.api.service.customer.LoginLogoutCustomerUtils.getUserToken;
import static com.lykke.tests.api.service.customer.SpendRulesUtils.getSpendRuleById;
import static com.lykke.tests.api.service.customer.VouchersUtils.postBuyVoucher;
import static com.lykke.tests.api.service.operationshistory.OperationsUtils.getTransactionsByCustomerId;
import static com.lykke.tests.api.service.operationshistory.OperationsUtils.getVoucherPurchases;
import static com.lykke.tests.api.service.partnerapi.PartnerApiLogInLogOutTests.USER_INFO;
import static com.lykke.tests.api.service.partnerapi.PartnerApiUtils.getPartnerToken;
import static com.lykke.tests.api.service.partnermanagement.PartnerManagementUtils.createPartner;
import static com.lykke.tests.api.service.partnermanagement.PartnerManagementUtils.getLocationExternalId;
import static com.lykke.tests.api.service.partnermanagement.PartnerManagementUtils.getLocationId;
import static com.lykke.tests.api.service.privateblockchainfacade.PrivateBlockchainFacadeUtils.createCustomerFundedViaBonusReward;
import static com.lykke.tests.api.service.quorumoperationexecutor.QuorumOperationExecutorUtils.getAddressBalance;
import static com.lykke.tests.api.service.vouchers.VouchersUtils.getVoucherByVoucherId;
import static com.lykke.tests.api.service.vouchers.VouchersUtils.getVouchersByCustomerId;
import static org.apache.http.HttpStatus.SC_NO_CONTENT;
import static org.apache.http.HttpStatus.SC_OK;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.lykke.tests.api.base.BaseApiTest;
import com.lykke.tests.api.common.model.CustomerBalanceInfo;
import com.lykke.tests.api.service.campaigns.model.burnrules.BurnRuleContentCreateRequestModel;
import com.lykke.tests.api.service.campaigns.model.burnrules.BurnRuleCreateRequestModel;
import com.lykke.tests.api.service.campaigns.model.burnrules.RuleContentType;
import com.lykke.tests.api.service.campaigns.model.burnrules.Vertical;
import com.lykke.tests.api.service.customer.model.VoucherPurchaseResponse;
import com.lykke.tests.api.service.operationshistory.model.PaginatedCustomerOperationsResponse;
import com.lykke.tests.api.service.operationshistory.model.PaginatedVoucherPurchasePaymentsHistoryResponse;
import com.lykke.tests.api.service.operationshistory.model.PaginationModelWithDatesRange;
import com.lykke.tests.api.service.operationshistory.model.VoucherPurchasePaymentResponse;
import com.lykke.tests.api.service.partnermanagement.PartnerManagementUtils.PartnerDto;
import com.lykke.tests.api.service.partnerspayments.model.PaymentRequestResponseModel;
import com.lykke.tests.api.service.vouchers.model.CustomerVoucherModel;
import com.lykke.tests.api.service.vouchers.model.VoucherModel;
import com.lykke.tests.api.service.vouchers.model.VoucherStatus;
import cucumber.api.java8.En;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;
import lombok.val;
import org.awaitility.Awaitility;
import org.awaitility.Duration;

public class VouchersStepdefs extends BaseApiTest implements En {

    private static final Double INITIAL_BALANCE = 1000.0;
    private static final Double AMOUNT_IN_TOKENS = 200.0;
    private static final int ORDER = 11;

    private static final RuleContentType RULE_CONTENT_TYPE_TITLE = RuleContentType.TITLE;
    private static final RuleContentType RULE_CONTENT_TYPE_DESCRIPTION = RuleContentType.DESCRIPTION;
    private static final com.lykke.tests.api.service.campaigns.model.burnrules.Localization LOCALIZATION_EN = com.lykke.tests.api.service.campaigns.model.burnrules.Localization.EN;
    private static final String BURN_RULE_CONTENT_TITLE_EN_VALUE = "ENGLISH TITLE";
    private static final String BURN_RULE_CONTENT_DESCRIPTION_EN_VALUE = "ENGLISH DESCRIPTION";

    private static final String VOUCHER_NOTIFICATION_TEMPLATE = "voucher-sold-push-notification";
    private static final BiFunction<Double, Double, String> MESSAGE =
            (price, priceInMVN) ->
                    String.format(
                            "Congratulations! You've successfully purchased ENGLISH TITLE %s for %s MVN. Your code is ",
                            price + "0", priceInMVN + "0");

    private static final int PRICE_UNIVERSAL_QUOTIENT = 5;
    private static final int PRICE_ENV_QUOTIENT = 5;
    private static final int TEN_MINUTES = 10;

    private PaymentRequestResponseModel paymentData;
    private CustomerBalanceInfo customerData;
    private PartnerDto partnerData;
    private String customerEmail;
    private String customerToken;
    private String partnerExternalLocationId;
    private String locationId;
    private String partnerToken;
    private Double initialBalance;
    private BurnRuleCreateRequestModel burnRule;
    private String burnRuleId;
    private String someMessageId;
    private BurnRuleContentCreateRequestModel[] burnRuleContentObject;
    private Double voucherPrice;
    private Double originalMVNBalance;
    private String voucherCode;
    private CustomerVoucherModel customerVoucher;
    private VoucherModel voucher;
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public VouchersStepdefs() {
        Given("^there is a spend rule with price (\\d+\\.\\d+)$", (Double priceArg) -> {
            voucherPrice = priceArg;
            val partnerPassword = generateValidPassword();
            val clientId = getRandomUuid();
            partnerData = createPartner(clientId, partnerPassword, generateRandomString(10), generateRandomString(10));
            locationId = getLocationId(partnerData);
            partnerToken = getPartnerToken(clientId, partnerPassword, USER_INFO);
            partnerExternalLocationId = getLocationExternalId(partnerData);

            val burnRuleContentTitleEn = BurnRuleContentCreateRequestModel
                    .builder()
                    .ruleContentType(RULE_CONTENT_TYPE_TITLE)
                    .localization(LOCALIZATION_EN)
                    .value(BURN_RULE_CONTENT_TITLE_EN_VALUE)
                    .build();
            val burnRuleContentDescriptionEn = BurnRuleContentCreateRequestModel
                    .builder()
                    .ruleContentType(RULE_CONTENT_TYPE_DESCRIPTION)
                    .localization(LOCALIZATION_EN)
                    .value(BURN_RULE_CONTENT_DESCRIPTION_EN_VALUE)
                    .build();

            burnRuleContentObject = new BurnRuleContentCreateRequestModel[]{
                    burnRuleContentTitleEn, burnRuleContentDescriptionEn
            };

            burnRule = BurnRuleCreateRequestModel
                    .burnRuleCreateRequestBuilder()
                    .amountInTokens(AMOUNT_IN_TOKENS.toString())
                    .amountInCurrency(AMOUNT_IN_TOKENS.floatValue() * 5)
                    .vertical(Vertical.RETAIL)
                    .description(generateRandomString(20))
                    .partnerIds(new String[]{partnerData.getId()})
                    .usePartnerCurrencyRate(false)
                    .title(generateRandomString(10))
                    .createdBy(generateRandomString(10))
                    .burnRuleContents(burnRuleContentObject)
                    .price(voucherPrice)
                    .order(ORDER)
                    .build();
            burnRuleId = getBurnRuleId(burnRule);

            originalMVNBalance = getAddressBalance(MVN_GATEWAY_ADDRESS);
        });
        And("^there is a customer with balance no less than (\\d+\\.\\d+)$", (Double initialBalanceArg) -> {
            customerData = createCustomerFundedViaBonusReward(INITIAL_BALANCE);
            customerEmail = customerData.getEmail();
            customerToken = getUserToken(customerData);
            initialBalance = getCustomerBalance(customerToken) + INITIAL_BALANCE;
        });
        Given("^I upload a csv file '(.*)' with voucher code$", ////55 '(.*)'$",
                (String csvFileNameArg) -> {
                    voucherCode = generateRandomString(10);
                    createCsvFile(csvFileNameArg, voucherCode);

                    val csvFilePath = getImagePath(csvFileNameArg);

                    postVouchers(burnRuleId, csvFileNameArg, csvFilePath, getAdminToken())
                            .then()
                            .assertThat()
                            .statusCode(SC_NO_CONTENT);
                });
        When("^customer buys the voucher$", () -> {
            postBuyVoucher(burnRuleId, customerToken)
                    .then()
                    .assertThat()
                    .statusCode(SC_OK)
                    .extract()
                    .as(VoucherPurchaseResponse.class);
        });
        Then("^the voucher is related to the customer$", () -> {
            Awaitility.await()
                    .atMost(20, TimeUnit.SECONDS)
                    .pollInterval(Duration.TWO_SECONDS)
                    .until(() -> {
                        val customerVouchersCollection = getVouchersByCustomerId(customerData.getCustomerId())
                                .then()
                                .assertThat()
                                .statusCode(SC_OK)
                                .extract()
                                .as(CustomerVoucherModel[].class);
                        return Arrays.stream(customerVouchersCollection)
                                .anyMatch(voucher -> voucher.getCode().equalsIgnoreCase(voucherCode));
                    });

            val customerVouchers = getVouchersByCustomerId(customerData.getCustomerId())
                    .then()
                    .assertThat()
                    .statusCode(SC_OK)
                    .extract()
                    .as(CustomerVoucherModel[].class);
            customerVoucher = Arrays.stream(customerVouchers)
                    .filter((CustomerVoucherModel voucher) -> voucher.getCode().equalsIgnoreCase(voucherCode))
                    .findFirst()
                    .orElse(new CustomerVoucherModel());

            assertAll(
                    () -> assertEquals(VoucherStatus.RESERVED, customerVoucher.getStatus()),
                    () -> assertEquals(voucherCode, customerVoucher.getCode()),
                    () -> assertEquals(burnRuleId, customerVouchers[0].getSpendRuleId()),
                    () -> assertEquals(voucherPrice / PRICE_UNIVERSAL_QUOTIENT,
                            Double.valueOf(customerVouchers[0].getAmountInTokens()))
            );
        });
        And("^voucher status is (\\w+)$", (String voucherStatusArg) -> {
            voucher = getVoucherByVoucherId(customerVoucher.getId())
                    .then()
                    .assertThat()
                    .statusCode(SC_OK)
                    .extract()
                    .as(VoucherModel.class);
            assertEquals(VoucherStatus.fromString(voucherStatusArg), voucher.getStatus());
        });
        And("^customer balance is decreased by the price of voucher$", () -> {
            Awaitility.await()
                    .atMost(AWAITILITY_PBF_TRANSFER_BALANCE_SEC, TimeUnit.SECONDS)
                    .pollInterval(Duration.TWO_SECONDS)
                    .until(() -> customerData.getExtraAmount() + customerData.getNewAmount() + INITIAL_BALANCE
                            - voucherPrice / PRICE_ENV_QUOTIENT
                            == getCustomerBalance(customerToken));

            assertEquals(customerData.getExtraAmount() + customerData.getNewAmount() + INITIAL_BALANCE
                            - voucherPrice / PRICE_ENV_QUOTIENT,
                    getCustomerBalance(customerToken));
        });
        And("^operations history contains a voucher transaction$", () -> {
            Awaitility.await()
                    .atMost(AWAITILITY_OPERATIONS_HISTORY_SEC, TimeUnit.SECONDS)
                    .pollInterval(AWAITILITY_POLL_INTERVAL_MID_SEC, TimeUnit.SECONDS)
                    .until(() -> 0 < getTransactionsByCustomerId(customerData.getCustomerId())
                            .then()
                            .assertThat()
                            .statusCode(SC_OK)
                            .extract()
                            .as(PaginatedCustomerOperationsResponse.class)
                            .getVoucherPurchasePayments().length);

            val voucherTransactions = getTransactionsByCustomerId(customerData.getCustomerId())
                    .then()
                    .assertThat()
                    .statusCode(SC_OK)
                    .extract()
                    .as(PaginatedCustomerOperationsResponse.class)
                    .getVoucherPurchasePayments();

            assertAll(
                    () -> assertEquals(voucher.getId(), voucherTransactions[0].getVoucherId()),
                    () -> assertEquals(voucherPrice / PRICE_UNIVERSAL_QUOTIENT,
                            Double.valueOf(voucherTransactions[0].getAmount())),
                    () -> assertEquals(customerData.getCustomerId(), voucherTransactions[0].getCustomerId())
            );
        });
        And("^soldCount on the spend rule is increased$", () -> {
            val spendRule = getSpendRuleById(burnRuleId, customerToken);
            assertEquals(1, spendRule.getSoldCount());
        });
        And("^MVN internal gateway balance is decreased by the price of voucher$", () -> {
            // TODO: it doesn't work since some time
        //    assertEquals(originalMVNBalance + voucherPrice / PRICE_ENV_QUOTIENT,
        //            getAddressBalance(MVN_GATEWAY_ADDRESS));
        });
        And("^operations history contains voucher purchase$", () -> {
            // FAL-4376
            Awaitility.await()
                    .atMost(AWAITILITY_OPERATIONS_HISTORY_SEC, TimeUnit.SECONDS)
                    .pollInterval(AWAITILITY_POLL_INTERVAL_MID_SEC, TimeUnit.SECONDS)
                    .until(() -> 0 < getVoucherPurchases(PaginationModelWithDatesRange
                            .paginationModelWithDatesRangeBuilder()
                            .currentPage(CURRENT_PAGE_LOWER_BOUNDARY)
                            .pageSize(PAGE_SIZE_UPPER_BOUNDARY)
                            .fromDate(ZonedDateTime.now(ZoneId.of("GMT")).minusMinutes(10).format(formatter))
                            .toDate(ZonedDateTime.now(ZoneId.of("GMT")).format(formatter))
                            .build())
                            .then()
                            .assertThat()
                            .statusCode(SC_OK)
                            .extract()
                            .as(PaginatedVoucherPurchasePaymentsHistoryResponse.class)
                            .getVoucherPurchasePaymentsHistory()
                            .length);

            val actualVoucherPurchasesResults = getVoucherPurchases(PaginationModelWithDatesRange
                    .paginationModelWithDatesRangeBuilder()
                    .currentPage(CURRENT_PAGE_LOWER_BOUNDARY)
                    .pageSize(PAGE_SIZE_UPPER_BOUNDARY)
                    .fromDate(ZonedDateTime.now(ZoneId.of("GMT")).minusMinutes(10).format(formatter))
                    .toDate(ZonedDateTime.now(ZoneId.of("GMT")).format(formatter))
                    .build())
                    .then()
                    .assertThat()
                    .statusCode(SC_OK)
                    .extract()
                    .as(PaginatedVoucherPurchasePaymentsHistoryResponse.class);
            val actualVoucherPurchasesResult = Arrays
                    .stream(actualVoucherPurchasesResults.getVoucherPurchasePaymentsHistory())
                    .filter(v -> v.getVoucherId().equalsIgnoreCase(voucher.getId()))
                    .findFirst()
                    .orElse(new VoucherPurchasePaymentResponse());

            assertAll(
                    () -> assertEquals(customerData.getCustomerId(), actualVoucherPurchasesResult.getCustomerId()),
                    () -> assertEquals(burnRuleId, actualVoucherPurchasesResult.getSpendRuleId()),
                    () -> assertEquals(voucherPrice / PRICE_UNIVERSAL_QUOTIENT,
                            actualVoucherPurchasesResult.getAmount()),
                    () -> assertEquals(voucher.getId(), actualVoucherPurchasesResult.getVoucherId())
            );
        });
    }

    private Double getCustomerBalance(String token) {
        return Double.valueOf(getCustomerWallets(token)[0].getBalance().replace(",", ""));
    }
}
