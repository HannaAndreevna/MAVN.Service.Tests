package com.lykke.tests.api.service.customerprofile.model.partnercontacts;

import static com.lykke.tests.api.base.Paths.CustomerProfile.CUSTOMER_PROFILE_API_PARTNER_CONTACTS_PATH;
import static com.lykke.tests.api.base.Paths.CustomerProfile.PARTNER_CONTACTS_BY_LOCATION;
import static com.lykke.tests.api.base.Paths.CustomerProfile.PARTNER_CONTACTS_PAGINATED;
import static com.lykke.api.testing.api.base.RequestHeader.getHeaderWithKey;
import static com.lykke.tests.api.common.CommonConsts.ADMIN_API_KEY;
import static com.lykke.api.testing.api.common.QueryParamsUtils.getQueryParams;
import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.apache.http.HttpStatus.SC_OK;

import com.lykke.tests.api.service.customerprofile.model.partnercontacts.model.PartnerContactByLocationResponseModel;
import com.lykke.tests.api.service.customerprofile.model.partnercontacts.model.PartnerContactModel;
import com.lykke.tests.api.service.customerprofile.model.partnercontacts.model.PartnerContactPaginatedValidationErrorResponse;
import com.lykke.tests.api.service.customerprofile.model.partnercontacts.model.PartnerContactsByLocationRequestModel;
import com.lykke.tests.api.service.customerprofile.model.partnercontacts.model.PartnerContactsPaginatedModel;
import com.lykke.tests.api.service.customerprofile.model.partnercontacts.model.PartnerContactsPaginatedRequest;
import com.lykke.tests.api.service.customerprofile.model.partnercontacts.model.ValidationErrorResponse;
import io.restassured.response.Response;
import lombok.experimental.UtilityClass;

@UtilityClass
public class PartnerContactsUtils {

    private static final String LOCATION_ID_FIELD = "locationId";

    public Response createPartnerContacts(PartnerContactModel requestObject) {
        return getHeaderWithKey(ADMIN_API_KEY)
                .body(requestObject)
                .post(CUSTOMER_PROFILE_API_PARTNER_CONTACTS_PATH)
                .thenReturn();
    }

    public Response updatePartnerContacts(PartnerContactModel requestObject) {
        return getHeaderWithKey(ADMIN_API_KEY)
                .body(requestObject)
                .put(CUSTOMER_PROFILE_API_PARTNER_CONTACTS_PATH)
                .thenReturn();
    }

    public ValidationErrorResponse createPartnerContactsValidationErrors(PartnerContactModel requestObject) {
        return getHeaderWithKey(ADMIN_API_KEY)
                .body(requestObject)
                .post(CUSTOMER_PROFILE_API_PARTNER_CONTACTS_PATH)
                .then()
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .extract()
                .as(ValidationErrorResponse.class);
    }

    public PartnerContactsPaginatedModel getPartnerContactsPaginated(PartnerContactsPaginatedRequest requestObject) {
        return getHeaderWithKey(ADMIN_API_KEY)
                .queryParams(getQueryParams(requestObject))
                .get(PARTNER_CONTACTS_PAGINATED)
                .then()
                .statusCode(requestObject.getHttpStatus())
                .extract()
                .as(PartnerContactsPaginatedModel.class);
    }

    public PartnerContactPaginatedValidationErrorResponse getPartnerContactsPaginatedValidationErrors(
            PartnerContactsPaginatedRequest requestObject) {
        return getHeaderWithKey(ADMIN_API_KEY)
                .queryParams(getQueryParams(requestObject))
                .get(PARTNER_CONTACTS_PAGINATED)
                .then()
                .statusCode(requestObject.getHttpStatus())
                .extract()
                .as(PartnerContactPaginatedValidationErrorResponse.class);
    }

    public PartnerContactByLocationResponseModel getPartnerContactsByLocation(String locationId) {
        return getHeaderWithKey(ADMIN_API_KEY)
                .queryParams(getQueryParams(
                        PartnerContactsByLocationRequestModel.builder().locationId(locationId).build()))
                .get(PARTNER_CONTACTS_BY_LOCATION.apply(locationId))
                .then()
                .statusCode(SC_OK)
                .extract()
                .as(PartnerContactByLocationResponseModel.class);
    }

    public Response deletePartnerContactsByLocation(String locationId) {
        return getHeaderWithKey(ADMIN_API_KEY)
                .queryParams(getQueryParams(
                        PartnerContactsByLocationRequestModel.builder().locationId(locationId).build()))
                .delete(PARTNER_CONTACTS_BY_LOCATION.apply(locationId))
                .thenReturn();
    }

}
