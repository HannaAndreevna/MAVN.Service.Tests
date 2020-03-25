package com.lykke.tests.api.service.admin;

import static com.lykke.tests.api.base.PathConsts.AdminApiService.BY_ID_PATH;
import static com.lykke.tests.api.base.Paths.AdminApi.ADMIN_GENERATE_CLIENT_ID_API_PATH;
import static com.lykke.tests.api.base.Paths.AdminApi.ADMIN_GENERATE_CLIENT_SECRET_API_PATH;
import static com.lykke.tests.api.base.Paths.AdminApi.ADMIN_PARTNERS_API_PATH;
import static com.lykke.api.testing.api.base.RequestHeader.getHeader;
import static com.lykke.api.testing.api.common.QueryParamsUtils.getQueryParams;
import static com.lykke.tests.api.common.PaginationConts.CURRENT_PAGE_LOWER_BOUNDARY;
import static com.lykke.tests.api.common.PaginationConts.PAGE_SIZE_LOWER_BOUNDARY;
import static org.apache.http.HttpStatus.SC_OK;

import com.lykke.tests.api.service.admin.model.partners.PartnerCreateRequest;
import com.lykke.tests.api.service.admin.model.partners.PartnerDetailsResponse;
import com.lykke.tests.api.service.admin.model.partners.PartnerUpdateRequest;
import com.lykke.tests.api.service.admin.model.partners.PartnersListRequest;
import com.lykke.tests.api.service.admin.model.partners.PartnersResponse;
import com.lykke.tests.api.service.admin.model.partners.ValidationErrorResponse;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import lombok.experimental.UtilityClass;
import org.apache.http.HttpStatus;

@UtilityClass
public class PartnersUtils {

    public Response generateClientSecret(String token) {
        return getHeader(token)
                .post(ADMIN_GENERATE_CLIENT_SECRET_API_PATH);
    }

    public Response generateClientId(String token) {
        return getHeader(token)
                .post(ADMIN_GENERATE_CLIENT_ID_API_PATH);
    }

    @Step("Get Partners")
    public PartnersResponse getPartnersPaginated(PartnersListRequest requestObject, String token) {
        return getHeader(token)
                .queryParams(getQueryParams(requestObject))
                .get(ADMIN_PARTNERS_API_PATH)
                .then()
                .assertThat()
                .statusCode(requestObject.getHttpStatus())
                .extract()
                .as(PartnersResponse.class);
    }

    @Step("Get partner by name")
    public String getPartnerIdByName(String name, String token) {
        return getHeader(token)
                .queryParams(getQueryParams(PartnersListRequest
                        .builder()
                        .name(name)
                        .currentPage(CURRENT_PAGE_LOWER_BOUNDARY)
                        .pageSize(PAGE_SIZE_LOWER_BOUNDARY)
                        .build()))
                .get(ADMIN_PARTNERS_API_PATH)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(PartnersResponse.class).getPartners()[0].getId();
    }

    public ValidationErrorResponse getPartnersPaginated_ValidationResponse(
            PartnersListRequest requestObject, String token) {
        return getHeader(token)
                .queryParams(getQueryParams(requestObject))
                .get(ADMIN_PARTNERS_API_PATH)
                .then()
                .assertThat()
                .statusCode(requestObject.getHttpStatus())
                .extract()
                .as(ValidationErrorResponse.class);
    }

    @Step("Add new partner")
    public Response addNewPartner(PartnerCreateRequest partnerCreateRequest, String token) {
        return getHeader(token)
                .body(partnerCreateRequest)
                .post(ADMIN_PARTNERS_API_PATH);
    }

    @Step("Update partner")
    public Response updatePartner(PartnerUpdateRequest partnerUpdateRequest, String token) {
        return getHeader(token)
                .body(partnerUpdateRequest)
                .put(ADMIN_PARTNERS_API_PATH);
    }

    @Deprecated
    @Step("Get partner by id")
    public PartnerDetailsResponse getPartnerById(String id, String token) {
        return getHeader(token)
                .get(ADMIN_PARTNERS_API_PATH + BY_ID_PATH.getFilledInPath(id))
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .as(PartnerDetailsResponse.class);
    }

    @Deprecated
    public Response getPartnerById_Response(String id, String token) {
        return getHeader(token)
                .get(ADMIN_PARTNERS_API_PATH + BY_ID_PATH.getFilledInPath(id));
    }
}
