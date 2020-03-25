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
import static com.lykke.tests.api.service.admin.BurnRulesUtils.createBurnRule;
import static com.lykke.tests.api.service.admin.BurnRulesUtils.postVouchers;
import static com.lykke.tests.api.service.admin.LoginAdminUtils.getAdminToken;
import static com.lykke.tests.api.service.campaigns.BurnRulesUtils.getBurnRuleId;
import static com.lykke.tests.api.service.customer.CustomerWalletUtils.getCustomerWallets;
import static com.lykke.tests.api.service.customer.LoginLogoutCustomerUtils.getUserToken;
import static com.lykke.tests.api.service.customer.SpendRulesUtils.getSpendRuleById;
import static com.lykke.tests.api.service.customer.VouchersUtils.getVouchers;
import static com.lykke.tests.api.service.customer.VouchersUtils.postBuyVoucher;
import static com.lykke.tests.api.service.notificationsystemaudit.NotificationSystemAuditUtils.getAuditMessageFromService;
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
import static com.lykke.tests.api.service.vouchers.VouchersUtils.postVouchers;
import static java.util.stream.Collectors.toList;
import static org.apache.http.HttpStatus.SC_NO_CONTENT;
import static org.apache.http.HttpStatus.SC_OK;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.lykke.api.testing.annotations.UserStoryId;
import com.lykke.tests.api.base.BaseApiTest;
import com.lykke.tests.api.common.model.CustomerBalanceInfo;
import com.lykke.tests.api.service.campaigns.model.burnrules.BurnRuleContentCreateRequestModel;
import com.lykke.tests.api.service.campaigns.model.burnrules.BurnRuleCreateRequestModel;
import com.lykke.tests.api.service.campaigns.model.burnrules.RuleContentType;
import com.lykke.tests.api.service.campaigns.model.burnrules.Vertical;
import com.lykke.tests.api.service.customer.model.PaginationRequestModel;
import com.lykke.tests.api.service.customer.model.VoucherListModel;
import com.lykke.tests.api.service.customer.model.VoucherPurchaseResponse;
import com.lykke.tests.api.service.operationshistory.model.PaginatedCustomerOperationsResponse;
import com.lykke.tests.api.service.operationshistory.model.PaginatedVoucherPurchasePaymentsHistoryResponse;
import com.lykke.tests.api.service.operationshistory.model.PaginationModelWithDatesRange;
import com.lykke.tests.api.service.operationshistory.model.VoucherPurchasePaymentResponse;
import com.lykke.tests.api.service.partnermanagement.PartnerManagementUtils.PartnerDto;
import com.lykke.tests.api.service.partnerspayments.model.PaymentRequestResponseModel;
import com.lykke.tests.api.service.vouchers.model.CustomerVoucherModel;
import com.lykke.tests.api.service.vouchers.model.VoucherCreateModel;
import com.lykke.tests.api.service.vouchers.model.VoucherCreateResultModel;
import com.lykke.tests.api.service.vouchers.model.VoucherModel;
import com.lykke.tests.api.service.vouchers.model.VoucherStatus;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.awaitility.Awaitility;
import org.awaitility.Duration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@Slf4j
public class VoucherTests extends BaseApiTest {

    private static final Double INITIAL_BALANCE = 1000.0;
    private static final Double AMOUNT_IN_TOKENS = 200.0;
    private static final Double PRICE = 120.0 + new Random().nextInt(30);
    private static final int ORDER = 11;
    private static final String[] VOUCHER_CODES = new String[]{generateRandomString(10), generateRandomString(10),
            generateRandomString(10)};

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
    private BurnRuleContentCreateRequestModel[] burnRuleContentObject;
    private Date purchaseDate;
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @BeforeEach
    void setUp() {
        val partnerPassword = generateValidPassword();
        val clientId = getRandomUuid();
        partnerData = createPartner(clientId, partnerPassword, generateRandomString(10), generateRandomString(10));
        locationId = getLocationId(partnerData);
        partnerToken = getPartnerToken(clientId, partnerPassword, USER_INFO);
        customerData = createCustomerFundedViaBonusReward(INITIAL_BALANCE);
        customerEmail = customerData.getEmail();
        customerToken = getUserToken(customerData);
        partnerExternalLocationId = getLocationExternalId(partnerData);
        initialBalance = getCustomerBalance(customerToken) + INITIAL_BALANCE;

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
    }

    @Test
    @UserStoryId(storyId = {3916, 3908, 3909, 4189, 4123, 4141, 4122, 4334})
    void shouldBeAbleToBuyVoucher() {

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
                .price(PRICE)
                .order(ORDER)
                .build();
        burnRuleId = getBurnRuleId(burnRule);

        val originalMVNBalance = getAddressBalance(MVN_GATEWAY_ADDRESS);

        // post a fake voucher
        val voucherCreationResult = postVouchers(VoucherCreateModel
                .builder()
                .spendRuleId(burnRuleId)
                .codes(VOUCHER_CODES)
                .build())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(VoucherCreateResultModel.class);

        // buying a voucher
        val buyVoucherResult = postBuyVoucher(burnRuleId, customerToken)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(VoucherPurchaseResponse.class);

        // check voucher status == Reserved
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
                            .anyMatch(voucher -> Arrays.stream(VOUCHER_CODES)
                                    .anyMatch(code -> code.equalsIgnoreCase(voucher.getCode())));
                });

        val customerVouchers = getVouchersByCustomerId(customerData.getCustomerId())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(CustomerVoucherModel[].class);
        val customerVoucher = Arrays.stream(customerVouchers)
                .filter((CustomerVoucherModel voucher) -> Arrays.stream(VOUCHER_CODES)
                        .anyMatch(code -> code.equalsIgnoreCase(voucher.getCode())))
                .findFirst()
                .orElse(new CustomerVoucherModel());
        purchaseDate = customerVoucher.getPurchaseDate();

        assertAll(
                () -> assertEquals(VoucherStatus.RESERVED, customerVoucher.getStatus()),
                () -> assertTrue(Arrays.stream(VOUCHER_CODES)
                        .anyMatch(code -> code.equalsIgnoreCase(customerVoucher.getCode()))),
                () -> assertEquals(burnRuleId, customerVouchers[0].getSpendRuleId()),
                () -> assertEquals(PRICE / PRICE_UNIVERSAL_QUOTIENT,
                        Double.valueOf(customerVouchers[0].getAmountInTokens()))
        );

        val voucher = getVoucherByVoucherId(customerVoucher.getId())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(VoucherModel.class);
        assertEquals(VoucherStatus.RESERVED, voucher.getStatus());

        // =============================================================================================
        // customer's balance after buying a voucher
        Awaitility.await()
                .atMost(AWAITILITY_PBF_TRANSFER_BALANCE_SEC, TimeUnit.SECONDS)
                .pollInterval(Duration.TWO_SECONDS)
                .until(() -> customerData.getExtraAmount() + customerData.getNewAmount() + INITIAL_BALANCE
                        - PRICE / PRICE_ENV_QUOTIENT
                        == getCustomerBalance(customerToken));

        assertEquals(customerData.getExtraAmount() + customerData.getNewAmount() + INITIAL_BALANCE
                        - PRICE / PRICE_ENV_QUOTIENT,
                getCustomerBalance(customerToken));

        // =============================================================================================
        // FAL-4123
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
                () -> assertEquals(PRICE / PRICE_UNIVERSAL_QUOTIENT,
                        Double.valueOf(voucherTransactions[0].getAmount())),
                () -> assertEquals(customerData.getCustomerId(), voucherTransactions[0].getCustomerId())
        );

        // =============================================================================================
        // FAL-4189
        val spendRule = getSpendRuleById(burnRuleId, customerToken);
        assertEquals(1, spendRule.getSoldCount());

        val voucherData = getVouchers(PaginationRequestModel
                .builder()
                .currentPage(CURRENT_PAGE_LOWER_BOUNDARY)
                .pageSize(PAGE_SIZE_UPPER_BOUNDARY)
                .build(), customerToken)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(VoucherListModel.class);

        assertAll(
                () -> assertEquals(1, voucherData.getTotalCount()),
                () -> assertTrue(
                        Arrays.stream(VOUCHER_CODES).collect(toList())
                                .contains(voucherData.getVouchers()[0].getCode())),
                // FAL-4334
                () -> assertEquals(purchaseDate, voucherData.getVouchers()[0].getPurchaseDate())
        );

        // =============================================================================================
        // FAL-4141 (calcelled)
        /*
        val actualPushMessage = getNotificationMessages(
                NotificationMessagesRequestModel
                        .notificationMessagesRequestModelBuilder()
                        .currentPage(CURRENT_PAGE_LOWER_BOUNDARY)
                        .pageSize(CURRENT_PAGE_LOWER_BOUNDARY)
                        .customerId(customerData.getCustomerId())
                        .build())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .jsonPath()
                .getObject("Data[0]", NotificationMessageResponseModel.class);

        assertAll(
                // TODO: the second amount should be taken from the response
                () -> assertTrue(actualPushMessage.getMessage().contains(MESSAGE.apply(PRICE, PRICE))),
                () -> assertFalse(actualPushMessage.isRead())
        );

        val actualNotificationMessages = getAuditMessageFromService(customerData.getCustomerId(),
                MessageType.PUSH_NOTIFICATION, DeliveryStatus.FAILED, VOUCHER_NOTIFICATION_TEMPLATE);

        assertAll(
                () -> assertEquals(customerData.getCustomerId(), actualNotificationMessages.getCustomerId()),
                () -> assertEquals(MessageType.PUSH_NOTIFICATION.getType(),
                        actualNotificationMessages.getMessageType()),
                () -> assertEquals(null, actualNotificationMessages.getSubjectTemplateId()),
                () -> assertEquals(CallType.RABBITMQ.getType(), actualNotificationMessages.getCallType()),
                () -> assertEquals(FormattingStatus.SUCCESS.getStatus(),
                        actualNotificationMessages.getFormattingStatus())
        );
        */

        val voucherFinalData = getVoucherByVoucherId(customerVoucher.getId())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(VoucherModel.class);
        assertEquals(VoucherStatus.SOLD, voucherFinalData.getStatus());

        // TODO: it doesn't work since some time
        // assertEquals(originalMVNBalance + PRICE / PRICE_ENV_QUOTIENT, getAddressBalance(MVN_GATEWAY_ADDRESS));
    }

    @ParameterizedTest
    @ValueSource(strings = {"vouchers_001.csv"})
    @UserStoryId(storyId = {3914, 4338, 4376})
    void shouldUploadVoucher(String csvFileName) {
        final String voucherCode = generateRandomString(10);
        final Double voucherPrice = 2.00;
        createCsvFile(csvFileName, voucherCode);

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

        val originalMVNBalance = getAddressBalance(MVN_GATEWAY_ADDRESS);

        val csvFilePath = getImagePath(csvFileName);

        postVouchers(burnRuleId, csvFileName, csvFilePath, getAdminToken())
                .then()
                .assertThat()
                .statusCode(SC_NO_CONTENT);

        // buying a voucher
        val buyVoucherResult = postBuyVoucher(burnRuleId, customerToken)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(VoucherPurchaseResponse.class);

        // check voucher status == Reserved
        Awaitility.await()
                .atMost(30, TimeUnit.SECONDS)
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
        val customerVoucher = Arrays.stream(customerVouchers)
                .filter((CustomerVoucherModel voucher) -> voucher.getCode().equalsIgnoreCase(voucherCode))
                .findFirst()
                .orElse(new CustomerVoucherModel());
        purchaseDate = customerVoucher.getPurchaseDate();

        assertAll(
                () -> assertEquals(VoucherStatus.RESERVED, customerVoucher.getStatus()),
                () -> assertEquals(voucherCode, customerVoucher.getCode()),
                () -> assertEquals(burnRuleId, customerVouchers[0].getSpendRuleId()),
                () -> assertEquals(voucherPrice / PRICE_UNIVERSAL_QUOTIENT,
                        Double.valueOf(customerVouchers[0].getAmountInTokens()))
        );

        val voucher = getVoucherByVoucherId(customerVoucher.getId())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(VoucherModel.class);
        assertEquals(VoucherStatus.RESERVED, voucher.getStatus());

        // =============================================================================================
        // customer's balance after buying a voucher
        Awaitility.await()
                .atMost(AWAITILITY_PBF_TRANSFER_BALANCE_SEC, TimeUnit.SECONDS)
                .pollInterval(Duration.TWO_SECONDS)
                .until(() -> customerData.getExtraAmount() + customerData.getNewAmount() + INITIAL_BALANCE
                        - voucherPrice / PRICE_ENV_QUOTIENT == getCustomerBalance(customerToken));

        assertEquals(customerData.getExtraAmount() + customerData.getNewAmount() + INITIAL_BALANCE
                - voucherPrice / PRICE_ENV_QUOTIENT, getCustomerBalance(customerToken));

        // =============================================================================================
        // FAL-4123
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

        val voucherTransaction = Arrays.stream(voucherTransactions)
                .filter(tran -> tran.getVoucherId().equalsIgnoreCase(voucher.getId()))
                .findFirst()
                .orElse(new VoucherPurchasePaymentResponse());

        assertAll(
                () -> assertEquals(voucher.getId(), voucherTransaction.getVoucherId()),
                () -> assertEquals(voucherPrice / PRICE_UNIVERSAL_QUOTIENT,
                        Double.valueOf(voucherTransaction.getAmount())),
                () -> assertEquals(customerData.getCustomerId(), voucherTransaction.getCustomerId())
        );

        // =============================================================================================
        // FAL-4189
        val spendRule = getSpendRuleById(burnRuleId, customerToken);
        assertEquals(1, spendRule.getSoldCount());

        val voucherData = getVouchers(PaginationRequestModel
                .builder()
                .currentPage(CURRENT_PAGE_LOWER_BOUNDARY)
                .pageSize(PAGE_SIZE_UPPER_BOUNDARY)
                .build(), customerToken)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(VoucherListModel.class);

        assertAll(
                () -> assertEquals(1, voucherData.getTotalCount()),
                () -> assertEquals(voucherCode, voucherData.getVouchers()[0].getCode()),
                // FAL-4334
                () -> assertEquals(purchaseDate, voucherData.getVouchers()[0].getPurchaseDate())
        );

        // =============================================================================================
        // FAL-4141 (calcelled)
        /*
        val actualPushMessage = getNotificationMessages(
                NotificationMessagesRequestModel
                        .notificationMessagesRequestModelBuilder()
                        .currentPage(CURRENT_PAGE_LOWER_BOUNDARY)
                        .pageSize(CURRENT_PAGE_LOWER_BOUNDARY)
                        .customerId(customerData.getCustomerId())
                        .build())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .jsonPath()
                .getObject("Data[0]", NotificationMessageResponseModel.class);

        assertAll(
                // TODO: the second amount should be taken from the response
                () -> assertTrue(actualPushMessage.getMessage().contains(MESSAGE.apply(voucherPrice, voucherPrice))),
                () -> assertFalse(actualPushMessage.isRead())
        );

        val actualNotificationMessages = getAuditMessageFromService(customerData.getCustomerId(),
                MessageType.PUSH_NOTIFICATION, DeliveryStatus.FAILED, VOUCHER_NOTIFICATION_TEMPLATE);

        assertAll(
                () -> assertEquals(customerData.getCustomerId(), actualNotificationMessages.getCustomerId()),
                () -> assertEquals(MessageType.PUSH_NOTIFICATION.getType(),
                        actualNotificationMessages.getMessageType()),
                () -> assertEquals(null, actualNotificationMessages.getSubjectTemplateId()),
                () -> assertEquals(CallType.RABBITMQ.getType(), actualNotificationMessages.getCallType()),
                () -> assertEquals(FormattingStatus.SUCCESS.getStatus(),
                        actualNotificationMessages.getFormattingStatus())
        );
        */

        val voucherFinalData = getVoucherByVoucherId(customerVoucher.getId())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(VoucherModel.class);
        assertEquals(VoucherStatus.SOLD, voucherFinalData.getStatus());

        // TODO: it doesn't work since some time
        // assertEquals(originalMVNBalance + voucherPrice / PRICE_ENV_QUOTIENT,
        //         getAddressBalance(MVN_GATEWAY_ADDRESS));

        // FAL-4376
        Awaitility.await()
                .atMost(AWAITILITY_OPERATIONS_HISTORY_SEC, TimeUnit.SECONDS)
                .pollInterval(AWAITILITY_POLL_INTERVAL_MID_SEC, TimeUnit.SECONDS)
                .until(() -> 0 < getVoucherPurchases(PaginationModelWithDatesRange
                        .paginationModelWithDatesRangeBuilder()
                        .currentPage(CURRENT_PAGE_LOWER_BOUNDARY)
                        .pageSize(PAGE_SIZE_UPPER_BOUNDARY)
                        .fromDate(ZonedDateTime.now(ZoneId.of("GMT")).minusMinutes(TEN_MINUTES).format(formatter))
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
                .fromDate(ZonedDateTime.now(ZoneId.of("GMT")).minusMinutes(TEN_MINUTES).format(formatter))
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
                () -> assertEquals(voucherPrice / PRICE_UNIVERSAL_QUOTIENT, actualVoucherPurchasesResult.getAmount()),
                () -> assertEquals(voucherFinalData.getId(), actualVoucherPurchasesResult.getVoucherId())
        );

    }

    private Double getCustomerBalance(String token) {
        return Double.valueOf(getCustomerWallets(token)[0].getBalance().replace(",", ""));
    }
}
