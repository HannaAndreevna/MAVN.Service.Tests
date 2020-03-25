package com.lykke.tests.api.service.privateblockchainfacade.model;

import static com.lykke.tests.api.common.CommonConsts.IS_GUID;
import static org.apache.commons.lang3.StringUtils.EMPTY;

import com.lykke.api.testing.annotations.PublicApi;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Builder
@Data
@PublicApi
public class BonusRewardRequestModel {

    public static final Double AMOUNT_LOWER_BOUNDARY = 1.0;
    public static final Double AMOUNT_UPPER_BOUNDARY = 9.22337203685478E+18;
    private static final String AMOUNT_BOUNDARIES = "The field Amount must be between 1 and 2147483647.";
    private static final String AMOUNT_INVALID_INPUT = "The input was not valid.";
    private static final String EMPTY_CUSTOMER_ID = "The CustomerId field is required.";
    private static final String CUSTOMER_ID_INVALID_INPUT = AMOUNT_INVALID_INPUT;
    private static final String EMPTY_REWARD_ID = "The RewardRequestId field is required.";
    private static final String EMPTY_BONUS_REASON = "The BonusReason field is required.";

    private String customerId;
    private String amount;
    private String rewardRequestId;
    private String bonusReason;
    private UUID campaignId;
    private UUID conditionId;

    public String[] getAmountMessage() {
        return AMOUNT_LOWER_BOUNDARY > Double.valueOf(amount)
                ? -AMOUNT_UPPER_BOUNDARY >= Double.valueOf(amount)
                ? new String[]{AMOUNT_INVALID_INPUT}
                : new String[]{AMOUNT_BOUNDARIES}
                : AMOUNT_UPPER_BOUNDARY <= Double.valueOf(amount)
                        ? new String[]{AMOUNT_INVALID_INPUT}
                        : new String[]{EMPTY};
    }

    public String[] getCustomerIdMessage() {
        return EMPTY.equals(customerId)
                ? new String[]{CUSTOMER_ID_INVALID_INPUT}
                : IS_GUID.apply(customerId)
                        ? new String[]{EMPTY}
                        : new String[]{CUSTOMER_ID_INVALID_INPUT};
    }

    public String[] getRewardRequestIdMessage() {
        return EMPTY.equals(rewardRequestId) || null == rewardRequestId
                ? new String[]{EMPTY_REWARD_ID}
                : new String[]{EMPTY};
    }

    public String[] getBonusReasonMessage() {
        return EMPTY.equals(bonusReason)
                ? new String[]{EMPTY_BONUS_REASON}
                : new String[]{EMPTY};
    }
}