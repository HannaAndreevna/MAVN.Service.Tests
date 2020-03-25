package com.lykke.tests.api.service.mavnpropertyintegration;

import static com.lykke.api.testing.api.common.GenerateUtils.generateRandomString;
import static com.lykke.api.testing.config.SettingsReader.readSettings;
import static com.lykke.tests.api.common.ConfigUtils.getPathToResourceFolder;
import static com.lykke.tests.api.service.customermanagement.RegisterCustomerUtils.registerDefaultVerifiedCustomer;
import static com.lykke.tests.api.service.mavnpropertyintegration.MAVNPropertyIntegrationUtils.getPaidInvoicePayments;
import static com.lykke.tests.api.service.mavnpropertyintegration.MAVNPropertyIntegrationUtils.postPaidInvoicePayments;
import static com.lykke.tests.api.service.partnermanagement.PartnerManagementUtils.createPartner;
import static com.lykke.tests.api.service.partnermanagement.PartnerManagementUtils.getLocationExternalId;
import static java.util.stream.Collectors.toList;
import static org.apache.http.HttpStatus.SC_NO_CONTENT;
import static org.apache.http.HttpStatus.SC_OK;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.lykke.api.testing.annotations.UserStoryId;
import com.lykke.api.testing.config.SettingsReader;
import com.lykke.tests.api.base.BaseApiTest;
import com.lykke.tests.api.service.mavnpropertyintegration.model.GetPaidInvoicePaymentHistoryRequestModel;
import com.lykke.tests.api.service.mavnpropertyintegration.model.PaginatedResponseModel;
import com.lykke.tests.api.service.mavnpropertyintegration.model.PaidInvoicePaymentHistoryResponseModel;
import com.lykke.tests.api.service.mavnpropertyintegration.model.PaidInvoicePaymentManualEntryModel;
import com.lykke.tests.api.service.mavnpropertyintegration.model.PaymentDueLocationInvoiceDetailManualEntryModel;
import com.lykke.tests.api.service.mavnpropertyintegration.model.PaymentDueManualEntryModel;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.stream.Stream;
import lombok.val;
import org.junit.jupiter.api.Test;

public class PaidInvoicePaymentsTests extends BaseApiTest {

    private static final String CUSTOMER_TRANSACTION = "string"; // hard-coded in Dev
    private static final int INSTALLMENT_NUMBER = 1;
    private static final int TOTAL_INSTALLMENTS = 10;
    private static final Double PAID_AMOUNT = 0.01;
    private static final Double VAT_AMOUNT = 0.01;
    private static final Double NET_PROPERTY_PRICE = 10.0;
    private static final Double DISCOUNT_AMOUNT = 10.0;
    private static final String ORDER_NUMBER = readSettings(getPathToResourceFolder()).getEnvironmentSettings()
            .getSettings().getStringSetting1();
    private static final String OPPORTUNITY_ID = generateRandomString(10);
    private static final String MVN_REFERRAL_ID = generateRandomString(10);
    private static final String ACCOUNT_NUMBER = generateRandomString(10);
    private static final String TRANSACTION_NUMBER = "string"; // hard-coded in Dev
    private static final String ORACLE_CUSTOMER_ACCOUNT_ID = generateRandomString(10);
    private static final String UNIT_LOCATION_CODE_FIELD = "UnitLocationCode";
    private static final String CUSTOMER_TRANSACTION_ID_FIELD = "CustomerTransactionId";
    private static final String INSTALLMENT_NUMBER_FIELD = "InstallmentNumber";
    private static final String TOTAL_INSTALLMENTS_FIELD = "TotalInstallments";
    private static final String PAID_AMOUNT_FIELD = "PaidAmount";
    private static final String VAT_AMOUNT_FIELD = "VatAmount";
    private static final String NET_PROPERTY_PRICE_FIELD = "NetPropertyPrice";
    private static final String DISCOUNT_AMOUNT_FIELD = "DiscountAmount";
    private static final String ORDER_NUMBER_FIELD = "OrderNumber";
    private static final String OPPORTUNITY_ID_FIELD = "OpportunityId";
    private static final String MVN_REFERRAL_ID_FIELD = "MVNReferralId";
    private static final String ACCOUNT_NUMBER_FIELD = "AccountNumber";
    private static final String SALESFORCE_ACCOUNT_ID_FIELD = "SalesforceAccountId";
    private static final String AGENT_CUSTOMER_ID_FIELD = "AgentCustomerId";
    private static final String BUYER_CUSTOMER_ID_FIELD = "BuyerCustomerId";
    private static final String TRANSACTION_NUMBER_FIELD = "TransactionNumber";
    private static final String ORACLE_CUSTOMER_ACCOUNT_ID_FIELD = "OracleCustomerAccountId";

    @Test
    @UserStoryId(3637)
    void shouldPostPaidInvoicePayment() {
        val customerData = registerDefaultVerifiedCustomer();
        val partnerData = createPartner(generateRandomString(10));
        val locationExternalId = getLocationExternalId(partnerData);

        postPaidInvoicePayments(PaidInvoicePaymentManualEntryModel
                .builder()
                .payments(

                        new PaymentDueManualEntryModel[]{PaymentDueManualEntryModel
                                .builder()
                                .invoiceDetail(

                                        new PaymentDueLocationInvoiceDetailManualEntryModel[]{

                                                PaymentDueLocationInvoiceDetailManualEntryModel
                                                        .builder()
                                                        ////55    .trxDate("2019-11-12")
                                                        .trxDate(Instant.now().toString())
                                                        .trxNumber(TRANSACTION_NUMBER)
                                                        .accountNumber(ACCOUNT_NUMBER)
                                                        .customerTrxId(CUSTOMER_TRANSACTION)
                                                        .installmentNumber(INSTALLMENT_NUMBER)
                                                        .lineAmount(0.01)
                                                        .customerAccountId(ORACLE_CUSTOMER_ACCOUNT_ID)
                                                        .locationCode(locationExternalId)

                                                        .build()
                                        }
                                )
                                .vatAmount(VAT_AMOUNT)
                                .discountAmount(DISCOUNT_AMOUNT)
                                .netPropertyPrice(NET_PROPERTY_PRICE)
                                .totalAmountPaidAsOf(0.01)
                                .totalInstallments(TOTAL_INSTALLMENTS)
                                .orderNumber(ORDER_NUMBER)
                                .leadEmail(customerData.getEmail())
                                .mvnReferralId(MVN_REFERRAL_ID)
                                .accountId(customerData.getCustomerId())
                                .referrerEmail(customerData.getEmail())
                                .opportunityId(OPPORTUNITY_ID)
                                .build()
                        }
                )

                .build())
                .then()
                .assertThat()
                .statusCode(SC_NO_CONTENT);

        val expectedResult = PaidInvoicePaymentHistoryResponseModel
                .builder()
                .unitLocationCode(locationExternalId)
                .customerTransactionId(CUSTOMER_TRANSACTION)
                .installmentNumber(INSTALLMENT_NUMBER)
                .totalInstallments(TOTAL_INSTALLMENTS)
                .paidAmount(PAID_AMOUNT)
                .vatAmount(VAT_AMOUNT)
                .netPropertyPrice(NET_PROPERTY_PRICE)
                .discountAmount(DISCOUNT_AMOUNT)
                .orderNumber(ORDER_NUMBER)
                .opportunityId(OPPORTUNITY_ID)
                .mvnReferralId(MVN_REFERRAL_ID)
                .accountNumber(ACCOUNT_NUMBER)
                .salesforceAccountId(customerData.getCustomerId())
                .agentCustomerId(customerData.getCustomerId())
                .buyerCustomerId(customerData.getCustomerId())
                .transactionNumber(TRANSACTION_NUMBER)
                .oracleCustomerAccountId(ORACLE_CUSTOMER_ACCOUNT_ID)
                .build();

        val preliminaryResult = getPaidInvoicePayments(
                GetPaidInvoicePaymentHistoryRequestModel
                        .getPaidInvoicePaymentHistoryRequestModelBuilder()
                        .fromTimestamp(Date.from(Instant.now().minus(10_000, ChronoUnit.DAYS)))
                        .currentPage(1)
                        .pageSize(500)
                        .build())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(PaginatedResponseModel.class);

        val actualResultCollection = Stream.of(preliminaryResult.getData())
                .map(historyItem -> {
                    val itemMap = (LinkedHashMap) historyItem;
                    return PaidInvoicePaymentHistoryResponseModel
                            .builder()
                            .unitLocationCode(itemMap.get(UNIT_LOCATION_CODE_FIELD).toString())
                            .customerTransactionId(itemMap.get(CUSTOMER_TRANSACTION_ID_FIELD).toString())
                            .installmentNumber(Integer.valueOf(itemMap.get(INSTALLMENT_NUMBER_FIELD).toString()))
                            .totalInstallments(Integer.valueOf(itemMap.get(TOTAL_INSTALLMENTS_FIELD).toString()))
                            .paidAmount(Double.valueOf(itemMap.get(PAID_AMOUNT_FIELD).toString()))
                            .vatAmount(Double.valueOf(itemMap.get(VAT_AMOUNT_FIELD).toString()))
                            .netPropertyPrice(Double.valueOf(itemMap.get(NET_PROPERTY_PRICE_FIELD).toString()))
                            .discountAmount(Double.valueOf(itemMap.get(DISCOUNT_AMOUNT_FIELD).toString()))
                            .orderNumber(itemMap.get(ORDER_NUMBER_FIELD).toString())
                            .opportunityId(itemMap.get(OPPORTUNITY_ID_FIELD).toString())
                            .mvnReferralId(itemMap.get(MVN_REFERRAL_ID_FIELD).toString())
                            .accountNumber(itemMap.get(ACCOUNT_NUMBER_FIELD).toString())
                            .salesforceAccountId(itemMap.get(SALESFORCE_ACCOUNT_ID_FIELD).toString())
                            .agentCustomerId(itemMap.get(AGENT_CUSTOMER_ID_FIELD).toString())
                            .buyerCustomerId(itemMap.get(BUYER_CUSTOMER_ID_FIELD).toString())
                            .transactionNumber(itemMap.get(TRANSACTION_NUMBER_FIELD).toString())
                            .oracleCustomerAccountId(itemMap.get(ORACLE_CUSTOMER_ACCOUNT_ID_FIELD).toString())
                            .build();
                })
                .collect(toList())
                .toArray(new PaidInvoicePaymentHistoryResponseModel[]{});

        val actualResult = Arrays.stream(actualResultCollection)
                .filter(historyItem -> historyItem.getAgentCustomerId().equalsIgnoreCase(customerData.getCustomerId()))
                .findFirst()
                .orElse(new PaidInvoicePaymentHistoryResponseModel());

        assertEquals(expectedResult, actualResult);
    }
}
