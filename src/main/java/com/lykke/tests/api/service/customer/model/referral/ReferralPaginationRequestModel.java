package com.lykke.tests.api.service.customer.model.referral;

import com.lykke.tests.api.service.customer.model.PaginationRequestModel;
import lombok.Builder;
import lombok.Data;

@Data
public class ReferralPaginationRequestModel extends PaginationRequestModel {

    private CommonReferralStatus status;
    private String earnRuleId;

    @Builder(builderMethodName = "referralPaginationRequestModelBuilder")
    public ReferralPaginationRequestModel(int currentPage, int pageSize, CommonReferralStatus status,
            String earnRuleId) {
        super(currentPage, pageSize);
        this.status = status;
        this.earnRuleId = earnRuleId;
    }

    public String getStatus() {
        return status.getCode();
    }
}
