package com.lykke.tests.api.service.privateblockchainfacade.model;

import static com.lykke.tests.api.common.CommonConsts.IS_GUID;
import static org.apache.commons.lang3.StringUtils.EMPTY;

import java.util.function.Function;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Builder
@Data
public class CustomerBalanceRequestModel {
    private static final String CUSTOMER_ID_INVALID_INPUT = "The input was not valid.";
    private static final Function<String, String> VALUE_IS_NOT_VALID =
            (id) -> String.format("The value '%s' is not valid.", id);

    private String customerId;

    public String[] getCustomerIdMessage() {
        return EMPTY.equals(customerId)
                ? new String[] { CUSTOMER_ID_INVALID_INPUT}
                : IS_GUID.apply(customerId)
                ? new String[] { EMPTY }
                : new String[] { VALUE_IS_NOT_VALID.apply(customerId) };
    }
}
