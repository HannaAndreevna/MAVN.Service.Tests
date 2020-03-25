package com.lykke.tests.api.service.campaigns.model;

import com.lykke.api.testing.annotations.NetClassName;
import com.lykke.api.testing.annotations.PublicApi;
import com.lykke.tests.api.service.campaigns.model.burnrules.Vertical;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@PublicApi
@NetClassName("BonusTypeEditModel")
public class BonusType {

    private String type;
    private String displayName;
    private boolean isAvailable;
    private Vertical vertical;
    private boolean allowInfinite;
    private boolean allowPercentage;
    private boolean allowConversionRate;
    private boolean isStakeable;

    // from ConditionModel?
    private String immediateReward;
    private Integer completionCount;

    public void setImmediateReward(String value) {
        immediateReward = value;
    }

    public void setImmediateReward(int value) {
        immediateReward = String.valueOf(value);
    }
}
