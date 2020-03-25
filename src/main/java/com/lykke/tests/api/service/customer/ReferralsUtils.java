package com.lykke.tests.api.service.customer;

import static com.lykke.tests.api.base.PathConsts.CustomerApiEndpoint.LEADS_PATH;
import static com.lykke.tests.api.base.PathConsts.CustomerApiEndpoint.LEAD_PATH;
import static com.lykke.tests.api.base.Paths.REFERRALS_API_PATH;
import static com.lykke.api.testing.api.base.RequestHeader.getHeader;
import static org.apache.http.HttpStatus.SC_OK;

import com.lykke.api.testing.api.common.QueryParamsUtils;
import com.lykke.tests.api.service.customer.model.LeadReferralsListResponseModel;
import com.lykke.tests.api.service.customer.model.ReferralsLeadRequest;
import com.lykke.tests.api.service.customer.model.ValidationErrorReferralsLeadResponse;
import io.restassured.response.Response;
import lombok.experimental.UtilityClass;
import org.json.simple.JSONObject;

@UtilityClass
public class ReferralsUtils {

    public static final String FIRST_NAME_FIELD = "FirstName";
    public static final String LAST_NAME_FIELD = "LastName";
    public static final String COUNTRY_CODE_FIELD = "CountryCode";
    public static final String COUNTRY_NAME_FIELD = "CountryName";
    public static final String NUMBER_FIELD = "Number";
    public static final String EMAIL_FIELD = "Email";
    public static final String NOTE_FIELD = "Note";

    public static ValidationErrorReferralsLeadResponse addPropertyReferralForACustomer(String token,
            ReferralsLeadRequest requestObject) {
        return getHeader(token)
                .body(QueryParamsUtils.getQueryParams(requestObject))
                .post(REFERRALS_API_PATH + LEAD_PATH.getPath())
                .then()
                .assertThat()
                .statusCode(requestObject.getHttpStatus())
                .extract()
                .as(ValidationErrorReferralsLeadResponse.class);
    }

    static Response addLeadReferralForCustomer(String token, String firstName, String lastName, String countryCode,
            String countryName, String number, String email, String note) {
        return getHeader(token)
                .body(getLeadObject(firstName, lastName, countryCode, countryName, number, email, note))
                .post(REFERRALS_API_PATH + LEAD_PATH.getPath());
    }

    static LeadReferralsListResponseModel getReferralLeadsForACustomer(String token) {
        return getHeader(token)
                .get(REFERRALS_API_PATH + LEADS_PATH.getPath())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(LeadReferralsListResponseModel.class);
    }

    private static JSONObject getLeadObject(String firstName, String lastName, String countryCode, String countryName,
            String number, String email, String note) {
        JSONObject leadRegisterObject = new JSONObject();
        leadRegisterObject.put(FIRST_NAME_FIELD, firstName);
        leadRegisterObject.put(LAST_NAME_FIELD, lastName);
        leadRegisterObject.put(COUNTRY_CODE_FIELD, countryCode);
        leadRegisterObject.put(COUNTRY_NAME_FIELD, countryName);
        leadRegisterObject.put(NUMBER_FIELD, number);
        leadRegisterObject.put(EMAIL_FIELD, email);
        leadRegisterObject.put(NOTE_FIELD, note);
        return leadRegisterObject;
    }
}
