package com.lykke.tests.api.common.enums.campaign;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum ConditionType {
    SIGNUP("signup"),
    EMAIL_VERIFIED("emailverified"),
    FRIEND_REFERRAL("friend-referral"),
    MVN_PURCHASE("mvn-purchase"),
    PURCHASE_REFERRAL("purchase-referral"),
    HOTEL_STAY("hotel-stay"),
    ESTATE_PURCHASE("estate-purchase"),
    ESTATE_LEAD_REFERRAL("estate-lead-referral"),
    ESTATE_PURCHASE_REFERRAL("estate-purchase-referral"),
    HOTEL_STAY_REFERRAL("hotel-stay-referral");

    @Getter
    private String value;
}
