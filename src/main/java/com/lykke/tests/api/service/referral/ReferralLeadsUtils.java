package com.lykke.tests.api.service.referral;

import static com.lykke.tests.api.base.Paths.REFERRAL_API_REFERRAL_LEADS_APPROVED_PATH;
import static com.lykke.tests.api.base.Paths.REFERRAL_API_REFERRAL_LEADS_APPROVE_PATH;
import static com.lykke.tests.api.base.Paths.REFERRAL_API_REFERRAL_LEADS_PATH;
import static com.lykke.tests.api.base.Paths.REFERRAL_API_REFERRAL_LEADS_PROPERTY_PURCHASES_PATH;
import static com.lykke.tests.api.base.Paths.REFERRAL_API_REFERRAL_LEADS_STATISTIC_PATH;
import static com.lykke.api.testing.api.base.RequestHeader.getHeader;
import static com.lykke.api.testing.api.common.QueryParamsUtils.getQueryParams;
import static java.util.stream.Collectors.toMap;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.http.HttpStatus.SC_OK;

import com.lykke.tests.api.service.referral.model.referralleadmodel.ReferralLeadsApprovedResponseModel;
import com.lykke.tests.api.service.referral.model.referralleadmodel.ReferralLeadsCreateModel;
import com.lykke.tests.api.service.referral.model.referralleadmodel.ReferralLeadsCreateRequest;
import com.lykke.tests.api.service.referral.model.referralleadmodel.ReferralLeadsPropertyPurchaseResponseModel;
import com.lykke.tests.api.service.referral.model.referralleadmodel.ReferralLeadsResponseModel;
import com.lykke.tests.api.service.referral.model.referralleadmodel.StatisticResponseModel;
import com.lykke.tests.api.service.referral.model.referralleadmodel.ValidationErrorLeadResponse;
import com.lykke.tests.api.service.referral.model.referralleadmodel.ValidationErrorResponseModel;
import io.restassured.response.Response;
import java.util.Map;
import java.util.stream.Stream;
import lombok.experimental.UtilityClass;
import org.json.simple.JSONObject;

@UtilityClass
public class ReferralLeadsUtils {

    private static final String AGENT_ID_FIELD = "agentId";
    private static String FIRST_NAME_FIELD = "FirstName";
    private static String LAST_NAME_FIELD = "LastName";
    private static String PHONE_COUNTRY_CODE_ID = "PhoneCountryCodeId";
    private static String PHONE_NUMBER_FIELD = "PhoneNumber";
    private static String EMAIL_FIELD = "Email";
    private static String NOTE_FIELD = "Note";
    private static String CUSTOMER_ID_FIELD = "CustomerId";

    public StatisticResponseModel getReferralLeadsStatistic() {
        return getHeader()
                .get(REFERRAL_API_REFERRAL_LEADS_STATISTIC_PATH)
                .then()

                // TODO: error 500
                .log().all()

                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(StatisticResponseModel.class);
    }

    public ReferralLeadsResponseModel getReferralLeadsResponse(String agentId) {
        return getHeader()
                .queryParams(AGENT_ID_FIELD, agentId)
                .get(REFERRAL_API_REFERRAL_LEADS_PATH)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(ReferralLeadsResponseModel.class);
    }

    public ValidationErrorResponseModel createReferralLead(ReferralLeadsCreateModel requestParameters) {
        return getHeader()
                .body(getQueryParams(requestParameters))
                .post(REFERRAL_API_REFERRAL_LEADS_PATH)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(ValidationErrorResponseModel.class);
    }

    public static ValidationErrorLeadResponse createReferralLead(
            ReferralLeadsCreateRequest referralLeadsCreateRequest) {
        return getHeader()
                .body(getPropertiesObject(referralLeadsCreateRequest))
                .post(REFERRAL_API_REFERRAL_LEADS_PATH)
                .then()
                .assertThat()
                .statusCode(referralLeadsCreateRequest.getHttpStatus())
                .extract()
                .as(ValidationErrorLeadResponse.class);
    }

    public ReferralLeadsApprovedResponseModel getReferralLeadsApprovedResponse() {
        return getHeader()
                .get(REFERRAL_API_REFERRAL_LEADS_APPROVED_PATH)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(ReferralLeadsApprovedResponseModel.class);
    }

    public ReferralLeadsPropertyPurchaseResponseModel getReferralLeadsPropertyPurchaseResponse() {
        return getHeader()
                .get(REFERRAL_API_REFERRAL_LEADS_PROPERTY_PURCHASES_PATH)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(ReferralLeadsPropertyPurchaseResponseModel.class);
    }

    public static Response approveLead(String referralId) {
        return getHeader()
                .body(approveLeadObject(referralId))
                .put(REFERRAL_API_REFERRAL_LEADS_APPROVE_PATH);
    }

    private static JSONObject approveLeadObject(String referralId) {
        JSONObject approveObj = new JSONObject();
        approveObj.put("ReferralId", referralId);
        return approveObj;
    }

    private static Map<String, String> getPropertiesObject(ReferralLeadsCreateRequest requestParameters) {

        return Stream.of(new String[][]{
                {FIRST_NAME_FIELD, requestParameters.getFirstName()},
                {LAST_NAME_FIELD, requestParameters.getLastName()},
                {PHONE_COUNTRY_CODE_ID, String.valueOf(requestParameters.getPhoneCountryCodeId())},
                {PHONE_NUMBER_FIELD, requestParameters.getPhoneNumber()},
                {EMAIL_FIELD, requestParameters.getEmail()},
                {NOTE_FIELD, requestParameters.getNote()},
                {CUSTOMER_ID_FIELD, requestParameters.getCustomerId()},
        })
                .collect(toMap(item -> item[0], item -> null == item[1] ? EMPTY : item[1]));
    }
}
