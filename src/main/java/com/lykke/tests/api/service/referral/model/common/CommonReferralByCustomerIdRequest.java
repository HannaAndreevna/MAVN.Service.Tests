package com.lykke.tests.api.service.referral.model.common;

import static java.util.stream.Collectors.toList;

import java.util.Arrays;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Builder
@Data
public class CommonReferralByCustomerIdRequest {

    private String customerId;
    private String campaignId;
    private CommonReferralStatus[] statuses;

    public String[] getStatuses() {
        return null == statuses
                ? new String[]{}
                : Arrays.stream(statuses)
                        .map(status -> status.getCode())
                        .collect(toList())
                        .toArray(new String[]{});
    }
}
