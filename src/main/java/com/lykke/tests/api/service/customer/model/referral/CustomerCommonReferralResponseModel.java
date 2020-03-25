package com.lykke.tests.api.service.customer.model.referral;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy.UpperCamelCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.lykke.tests.api.common.enums.BusinessVertical;
import com.lykke.tests.api.service.referral.model.common.ReferralType;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode.Exclude;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
@JsonNaming(UpperCamelCaseStrategy.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CustomerCommonReferralResponseModel {

    private ReferralType referralType;
    private CommonReferralStatus status;
    private BusinessVertical vertical;
    private String firstName;
    private String lastName;
    private String partnerName;
    @Exclude
    private Date timeStamp;
    private boolean hasStaking;
    @Exclude // TODO: temporarily
    private String totalReward;
    @Exclude // TODO: temporarily
    private String currentRewardedAmount;
    private RewardRatioAttributeModel rewardRatio;
    private boolean rewardHasRatio;
    private ReferralStakingModel staking;
    private boolean isApproximate;
}
