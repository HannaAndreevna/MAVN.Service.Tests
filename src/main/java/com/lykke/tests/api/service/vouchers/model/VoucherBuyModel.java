package com.lykke.tests.api.service.vouchers.model;

import com.lykke.api.testing.annotations.PublicApi;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Builder
@Data
@PublicApi
public class VoucherBuyModel {

    private String customerId;
    private String spendRuleId;
}
