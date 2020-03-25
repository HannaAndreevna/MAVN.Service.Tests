package com.lykke.tests.api.common.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Builder
@Data
public class TransferPair {

    private CustomerBalanceInfo sender;
    private CustomerBalanceInfo recipient;
    private Double transferAmount;
}
