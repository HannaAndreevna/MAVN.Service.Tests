package com.lykke.tests.api.service.customer.model.history;

import static java.util.stream.Collectors.toMap;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.PropertyNamingStrategy.UpperCamelCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.lykke.api.testing.annotations.PublicApi;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@JsonNaming(UpperCamelCaseStrategy.class)
@PublicApi
public enum HistoryOperationType {
    SEND_TRANSFER("SendTransfer", "Incoming transfer operation"),
    RECEIVE_TRANSFER("ReceiveTransfer", "Outgoing transfer operation"),
    BONUS_REWARD("BonusReward", "The bonus reward operation"),
    PAYMENT_TRANSFER("PaymentTransfer", "Payment transfer operation"),
    PAYMENT_TRANSFER_REFUND("PaymentTransferRefund", "Payment transfer refund operation"),
    PARTNER_PAYMENT("PartnerPayment", "Partners payment operation"),
    PARTNER_PAYMENT_REFUND("PartnerPaymentRefund", "Partner payment refund operation"),
    REFERRAL_STAKE("ReferralStake", "Referral stake operation"),
    RELEASED_REFERRAL_STAKE("ReleasedReferralStake", "Released referral stake operation"),
    WALLET_LINKING("WalletLinking", "Wallet Linking operation");

    private static Map<String, HistoryOperationType> FORMAT_MAP =
            Stream.of(HistoryOperationType.values())
                    .collect(toMap(c -> c.getCode(), Function.identity()));
    @Getter
    private String code;

    @Getter
    private String comment;

    @JsonCreator
    public static HistoryOperationType fromString(String value) {
        return Optional
                .ofNullable(FORMAT_MAP.get(value))
                .orElseThrow(() -> new IllegalArgumentException(value));
    }
}
