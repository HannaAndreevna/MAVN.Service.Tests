package com.lykke.tests.api.service.operationshistory.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy.UpperCamelCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonNaming(UpperCamelCaseStrategy.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PaginatedCustomerOperationsResponse extends BasePagedResponse {

    private TransferResponse[] transfers;
    private BonusCashInResponse[] bonusCashIns;
    private PaymentTransferResponse[] paymentTransfers;
    private PaymentTransferResponse[] refundedPaymentTransfers;
    private PartnersPaymentResponse[] partnersPayments;
    private PartnersPaymentResponse[] refundedPartnersPayments;
    private ReferralStakeResponse[] referralStakes;
    private ReferralStakeResponse[] releasedReferralStakes;
    private LinkedWalletTransferResponse[] linkedWalletTransfers;
    private FeeCollectedOperationResponse[] feeCollectedOperations;
    private VoucherPurchasePaymentResponse[] voucherPurchasePayments;
}
