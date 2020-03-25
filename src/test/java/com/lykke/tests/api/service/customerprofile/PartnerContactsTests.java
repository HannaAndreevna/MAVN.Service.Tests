package com.lykke.tests.api.service.customerprofile;

import static com.lykke.api.testing.api.common.GenerateUtils.generateRandomEmail;
import static com.lykke.api.testing.api.common.GenerateUtils.generateRandomString;
import static com.lykke.tests.api.common.CommonConsts.SMOKE_TEST;
import static com.lykke.tests.api.common.PaginationConts.CURRENT_PAGE_LOWER_BOUNDARY;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.http.HttpStatus.SC_OK;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.lykke.api.testing.annotations.UserStoryId;
import com.lykke.tests.api.base.BaseApiTest;
import com.lykke.tests.api.service.admin.TestDataForPaginatedTests;
import com.lykke.tests.api.service.customerprofile.model.partnercontacts.PartnerContactsUtils;
import com.lykke.tests.api.service.customerprofile.model.partnercontacts.model.PartnerContactModel;
import com.lykke.tests.api.service.customerprofile.model.partnercontacts.model.PartnerContactsPaginatedRequest;
import java.util.stream.Stream;
import lombok.val;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class PartnerContactsTests extends BaseApiTest {

    private static final String NONE_ERROR_CODE = "None";
    private static final String PARTNER_NOT_FOUND_ERR_MSG = "PartnerContactDoesNotExist";
    private static final String LOCATION_ID_IS_REQUIRED_ERR_MSG = "The LocationId field is required.";
    private static final String FIRST_NAME_IS_REQUIRED_ERR_MSG = "The FirstName field is required.";
    private static final String LAST_NAME_IS_REQUIRED_ERR_MSG = "The LastName field is required.";
    private static final String PHONE_NUMBER_IS_REQUIRED_ERR_MSG = "The PhoneNumber field is required.";
    private static final String EMAIL_IS_REQUIRED_ERR_MSG = "The Email field is required.";

    static Stream<Arguments> getWrongPaginationParameters() {
        return TestDataForPaginatedTests.getWrongPaginationParameters();
    }

    @Test
    @UserStoryId(storyId = 2438)
    void shouldCreatePartnerContact_DeleteAfterwards() {
        val requestObject = PartnerContactModel
                .builder()
                .locationId(generateRandomString(5))
                .email(generateRandomEmail())
                .firstName(generateRandomString(5))
                .lastName(generateRandomString(5))
                .phoneNumber(generateRandomString(5))
                .build();

        PartnerContactsUtils.createPartnerContacts(requestObject)
                .then()
                .assertThat()
                .statusCode(SC_OK);

        val getPartnerContactResponse = PartnerContactsUtils
                .getPartnerContactsByLocation(requestObject.getLocationId());

        assertAll(
                () -> assertEquals(requestObject, getPartnerContactResponse.getPartnerContact()),
                () -> assertEquals(NONE_ERROR_CODE, getPartnerContactResponse.getErrorCode())
        );

        PartnerContactsUtils.deletePartnerContactsByLocation(requestObject.getLocationId());

        val getDeletedPartnerContactResponse = PartnerContactsUtils
                .getPartnerContactsByLocation(requestObject.getLocationId());

        assertAll(
                () -> assertNull(getDeletedPartnerContactResponse.getPartnerContact()),
                () -> assertEquals(PARTNER_NOT_FOUND_ERR_MSG, getDeletedPartnerContactResponse.getErrorCode())
        );
    }

    @Test
    @Tag(SMOKE_TEST)
    @UserStoryId(storyId = 2438)
    void shouldGetPartnerContactsPaginated() {
        val requestObject = PartnerContactsPaginatedRequest.builder()
                .pageSize(100)
                .currentPage(CURRENT_PAGE_LOWER_BOUNDARY)
                .build();

        val getPaginatedContacts = PartnerContactsUtils.getPartnerContactsPaginated(requestObject);

        assertAll(
                () -> assertNotNull(getPaginatedContacts.getPartnerContacts())
        );
    }

    @Test
    @UserStoryId
    void shouldUpdatePartnerContact() {
        val requestObject = PartnerContactModel
                .builder()
                .locationId(generateRandomString(5))
                .email(generateRandomEmail())
                .firstName(generateRandomString(5))
                .lastName(generateRandomString(5))
                .phoneNumber(generateRandomString(5))
                .build();

        PartnerContactsUtils.createPartnerContacts(requestObject)
                .then()
                .assertThat()
                .statusCode(SC_OK);

        val updateRequestObject = requestObject;
        updateRequestObject.setFirstName(generateRandomString(5));

        PartnerContactsUtils.updatePartnerContacts(requestObject)
                .then()
                .assertThat()
                .statusCode(SC_OK);

        val updatedResponse = PartnerContactsUtils
                .getPartnerContactsByLocation(updateRequestObject.getLocationId());

        assertAll(
                () -> assertNotEquals(requestObject, updatedResponse)
        );
    }

    @ParameterizedTest(name = "Run {index}: currentPage={0}, pageSize={1}")
    @MethodSource("getWrongPaginationParameters")
    @UserStoryId(storyId = 2438)
    void shouldNotReturnContactsPaginated(int currentPage, int pageSize) {
        val requestObject = PartnerContactsPaginatedRequest.builder()
                .pageSize(pageSize)
                .currentPage(currentPage)
                .build();

        val validationResponse = PartnerContactsUtils.getPartnerContactsPaginatedValidationErrors(requestObject);

        assertEquals(requestObject.getValidationResponse().getModelErrors(), validationResponse.getModelErrors());
    }

    @Test
    @UserStoryId(storyId = 2438)
    void shouldNotCreatePartnerContact() {
        val requestObject = PartnerContactModel
                .builder()
                .locationId(EMPTY)
                .email(EMPTY)
                .firstName(EMPTY)
                .lastName(EMPTY)
                .phoneNumber(EMPTY)
                .build();

        val contactResponse = PartnerContactsUtils.createPartnerContactsValidationErrors(requestObject);

        assertAll(
                () -> assertEquals(LOCATION_ID_IS_REQUIRED_ERR_MSG,
                        contactResponse.getModelErrors().getLocationId()[0]),
                () -> assertEquals(FIRST_NAME_IS_REQUIRED_ERR_MSG,
                        contactResponse.getModelErrors().getFirstName()[0]),
                () -> assertEquals(LAST_NAME_IS_REQUIRED_ERR_MSG,
                        contactResponse.getModelErrors().getLastName()[0]),
                () -> assertEquals(PHONE_NUMBER_IS_REQUIRED_ERR_MSG,
                        contactResponse.getModelErrors().getPhoneNumber()[0]),
                () -> assertEquals(EMAIL_IS_REQUIRED_ERR_MSG, contactResponse.getModelErrors().getEmail()[0])
        );
    }
}
