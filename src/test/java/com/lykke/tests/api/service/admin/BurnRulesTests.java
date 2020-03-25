package com.lykke.tests.api.service.admin;

import static com.lykke.api.testing.api.common.GenerateUtils.generateRandomInt;
import static com.lykke.api.testing.api.common.GenerateUtils.generateRandomString;
import static com.lykke.api.testing.api.common.GenerateUtils.getRandomUuid;
import static com.lykke.tests.api.common.CommonConsts.SMOKE_TEST;
import static com.lykke.tests.api.common.prerequisites.BurnRules.createBurnRuleWithAllContentTypes;
import static com.lykke.tests.api.service.admin.BurnRulesUtils.addAnImage;
import static com.lykke.tests.api.service.admin.BurnRulesUtils.createBurnRule;
import static com.lykke.tests.api.service.admin.BurnRulesUtils.createBurnRule_response;
import static com.lykke.tests.api.service.admin.BurnRulesUtils.deleteBurnRule;
import static com.lykke.tests.api.service.admin.BurnRulesUtils.getBurnRuleById;
import static com.lykke.tests.api.service.admin.BurnRulesUtils.getBurnRuleById_Response;
import static com.lykke.tests.api.service.admin.BurnRulesUtils.getBurnRuleId;
import static com.lykke.tests.api.service.admin.BurnRulesUtils.getBurnRules;
import static com.lykke.tests.api.service.admin.BurnRulesUtils.updateBurnRule;
import static com.lykke.tests.api.service.admin.BurnRulesUtils.updateImage;
import static com.lykke.tests.api.service.admin.LoginAdminUtils.getAdminToken;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.apache.http.HttpStatus.SC_NO_CONTENT;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.params.provider.Arguments.of;

import com.lykke.api.testing.annotations.UserStoryId;
import com.lykke.api.testing.api.common.FakerUtils;
import com.lykke.tests.api.base.BaseApiTest;
import com.lykke.tests.api.common.HelperUtils;
import com.lykke.tests.api.common.enums.BusinessVertical;
import com.lykke.tests.api.common.enums.Localization;
import com.lykke.tests.api.service.admin.model.bonustypes.Vertical;
import com.lykke.tests.api.service.admin.model.burnrules.BurnRuleCreateRequest;
import com.lykke.tests.api.service.admin.model.burnrules.BurnRuleCreateRequestModel;
import com.lykke.tests.api.service.admin.model.burnrules.BurnRuleUpdateRequest;
import com.lykke.tests.api.service.admin.model.burnrules.MobileContentCreateRequest;
import com.lykke.tests.api.service.admin.model.burnrules.MobileContentEditRequest;
import com.lykke.tests.api.service.admin.model.burnrules.MobileContentResponse;
import java.util.stream.Stream;
import lombok.val;
import org.apache.http.HttpStatus;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

public class BurnRulesTests extends BaseApiTest {

    private static final String BURN_RULE_TITLE = FakerUtils.title;
    private static final String DESCRIPTION = FakerUtils.randomQuote;
    private static final Double AMOUNT_IN_TOKENS = 10.0;
    private static final int AMOUNT_IN_CURRENCY = 32;
    private static final String ERROR_FIELD = "error";
    private static final String MESSAGE_FIELD = "message";
    private static final String MOBILE_CONTENTS_0_FIELD = "MobileContents[0]";
    private static final String MOBILE_CONTENTS_0_TITLE_0_FIELD_PATH = "\"MobileContents[0].Title\"[0]";
    private static final String MOBILE_CONTENTS_0_DESCRIPTION_FIELD_PATH = "\"MobileContents[0].Description\"[0]";
    private static final String ENTITY_NOT_FOUND = "EntityNotFound";
    private static final String ENTITY_NOT_VALID = "EntityNotValid";
    private static final String BURN_RULE_DOES_NOT_EXIST_MSG = "Burn rule with id %s does not exist.";
    private static final String SHOULD_HAVE_AT_LEAST_ONE_ITEM_MSG =
            "There should be at least one item in the MobileContents value";
    private static final String ERROR_MESSAGE_FIELD = "ErrorMessage";
    private static final String BURN_RULE_CONTENTS_0_FIELD = "ModelErrors.BurnRuleContents[0]";
    private static final String SH_ONE_CONTENT_FOR_TYPE_BY_LOC_MSG =
            "Should have only one content for 'type' by localization";
    private static final String EN_CONTENT_IS_REQUIRED_MSG = "English content is required.";
    private static final String RULE_DOES_NOT_HAVE_ANY_CONTENT_MSG = "Rule does not have any contents with id: %s";
    private static final String TITLE_IS_REQUIRED_MSG = "Title is required";
    private static final String LENGTH_OF_TITLE_MB_AT_LEAST_3_MSG =
            "Title length should be between 3 and 50 characters";
    private static final String LENGTH_OF_TITLE_MB_3_50_CH_MSG =
            "Title length should be between 3 and 50 characters";
    private static final String LENGTH_OF_DESCRIPTION_MB_3_1K_CR_MSG =
            "Description length should be between 3 and 1000 characters";
    private static final String NOT_VALID_FILE_FORMAT = "NotValidFileFormat";
    private static final String NOT_SUPPORTED_FILE_FORMAT_MSG = "%s is not supported file type";
    private static final String BR_CONTENT_TYPE_DN_EXIST_MSG =
            "Burn Content type with id %s does not exist.";
    private static final String INVALID_BURN_RULE = "InvalidBurnRuleId";
    private static final String INVALID_BURN_RULE_MSG = "Invalid Burn Rule id.";
    private static final BusinessVertical BUSINESS_VERTICAL = BusinessVertical.HOSPITALITY;
    private static final int ORDER = 10;
    private static String token;
    private static MobileContentCreateRequest mobileContents;
    private static BurnRuleCreateRequestModel burnRule;

    private static Stream burnRule_invalidParams() {
        return Stream.of(
                of(EMPTY, DESCRIPTION, AMOUNT_IN_TOKENS, AMOUNT_IN_CURRENCY, BUSINESS_VERTICAL),
                of(generateRandomString(2), DESCRIPTION,
                        AMOUNT_IN_TOKENS, AMOUNT_IN_CURRENCY, BUSINESS_VERTICAL),
                of(generateRandomString(51), DESCRIPTION,
                        AMOUNT_IN_TOKENS, AMOUNT_IN_CURRENCY, BUSINESS_VERTICAL),
                of(BURN_RULE_TITLE, generateRandomString(2),
                        AMOUNT_IN_TOKENS, AMOUNT_IN_CURRENCY, BUSINESS_VERTICAL),
                of(BURN_RULE_TITLE, generateRandomString(1001), AMOUNT_IN_TOKENS,
                        AMOUNT_IN_CURRENCY, BUSINESS_VERTICAL),
                of(BURN_RULE_TITLE, DESCRIPTION, -1, AMOUNT_IN_CURRENCY, BUSINESS_VERTICAL),
                of(BURN_RULE_TITLE, DESCRIPTION, 0, AMOUNT_IN_CURRENCY, BUSINESS_VERTICAL),
                of(BURN_RULE_TITLE, DESCRIPTION, AMOUNT_IN_TOKENS, -1, BUSINESS_VERTICAL),
                of(BURN_RULE_TITLE, DESCRIPTION, AMOUNT_IN_TOKENS, 0, BUSINESS_VERTICAL),
                of(BURN_RULE_TITLE, DESCRIPTION, AMOUNT_IN_TOKENS, AMOUNT_IN_CURRENCY, null)
        );
    }

    private static Stream burnRuleEditInvalidParams() {
        return Stream.of(
                of(EMPTY, DESCRIPTION, MOBILE_CONTENTS_0_TITLE_0_FIELD_PATH, TITLE_IS_REQUIRED_MSG),
                of(generateRandomString(2), DESCRIPTION, MOBILE_CONTENTS_0_TITLE_0_FIELD_PATH,
                        LENGTH_OF_TITLE_MB_AT_LEAST_3_MSG),
                of(generateRandomString(51), DESCRIPTION, MOBILE_CONTENTS_0_TITLE_0_FIELD_PATH,
                        LENGTH_OF_TITLE_MB_3_50_CH_MSG),
                of(BURN_RULE_TITLE, generateRandomString(2), MOBILE_CONTENTS_0_DESCRIPTION_FIELD_PATH,
                        LENGTH_OF_DESCRIPTION_MB_3_1K_CR_MSG),
                of(BURN_RULE_TITLE, generateRandomString(1001), MOBILE_CONTENTS_0_DESCRIPTION_FIELD_PATH,
                        LENGTH_OF_DESCRIPTION_MB_3_1K_CR_MSG)
        );
    }

    @BeforeEach
    void setup() {
        token = getAdminToken();
        mobileContents = MobileContentCreateRequest
                .builder()
                .mobileLanguage(Localization.EN)
                .title(generateRandomString(12))
                .description(generateRandomString(30))
                .build();

        burnRule = BurnRuleCreateRequestModel
                .burnRuleCreateRequestModelBuilder()
                .title(BURN_RULE_TITLE)
                .description(DESCRIPTION)
                .businessVertical(BusinessVertical.HOSPITALITY)
                .mobileContents(new MobileContentCreateRequest[]{mobileContents})
                .amountInTokens(AMOUNT_IN_TOKENS.toString())
                .amountInCurrency(AMOUNT_IN_CURRENCY)
                .order(ORDER)
                .build();
    }

    @Test
    @UserStoryId(storyId = 1277)
    void shouldCreateBurnRule() {
        val actualResult = createBurnRule(burnRule, token);

        assertAll(
                () -> assertNotNull(actualResult.getId()),
                () -> assertNotNull(actualResult.getCreatedImageContents()),
                () -> assertEquals(Localization.EN,
                        actualResult.getCreatedImageContents()[0].getMobileLanguage()),
                () -> assertNotNull(actualResult.getCreatedImageContents()[0].getRuleContentId())
        );
    }

    @Test
    @UserStoryId(storyId = {1277, 3869, 4333})
    @Tag(SMOKE_TEST)
    void shouldGetPaginatedBurnRules() {
        createBurnRule(burnRule, token);
        val actualResult = getBurnRules(BURN_RULE_TITLE, 1, 100, token);

        assertAll(
                () -> assertEquals(1, actualResult.getPagedResponse().getCurrentPage()),
                () -> assertNotNull(actualResult.getPagedResponse().getTotalCount()),
                () -> assertEquals(BURN_RULE_TITLE, actualResult.getBurnRules()[0].getTitle()),
                () -> assertEquals(AMOUNT_IN_TOKENS, actualResult.getBurnRules()[0].getAmountInTokens()),
                () -> assertEquals(AMOUNT_IN_CURRENCY, actualResult.getBurnRules()[0].getAmountInCurrency()),
                () -> assertNotNull(actualResult.getBurnRules()[0].getId()),
                // FAL-3869
                () -> assertEquals(ORDER, actualResult.getBurnRules()[0].getOrder()),
                // FAL-4333
                () -> assertEquals(Vertical.HOSPITALITY, actualResult.getBurnRules()[0].getVertical())
        );
    }

    @Test
    @UserStoryId(1277)
    void shouldNotGetPaginatedResponse() {
        val response = getBurnRules("asd1ea", 1, 100, token);

        assertAll(
                () -> assertEquals(response.getPagedResponse().getCurrentPage(), 1),
                () -> assertEquals(response.getPagedResponse().getTotalCount(), 0),
                () -> assertEquals(response.getBurnRules().length, 0)
        );
    }

    @ParameterizedTest(name = "Run {index}: title={0}, description={1}, amountInTokens={2}, amountInCurrency={3},"
            + "businessVertical={4}")
    @MethodSource("burnRule_invalidParams")
    @UserStoryId(storyId = 1277)
    void shouldValidateBurnRulesFields(String title, String description, Double amountInTokens,
            int amountInCurrency, BusinessVertical businessVertical) {

        val requestObject = BurnRuleCreateRequest
                .builder()
                .mobileContents(new MobileContentCreateRequest[]{mobileContents})
                .title(title)
                .description(description)
                .amountInTokens(amountInTokens.toString())
                .amountInCurrency(amountInCurrency)
                .businessVertical(businessVertical)
                .build();

        val response = createBurnRule(requestObject, token);

        assertEquals(requestObject.getValidationResponse(), response);
    }

    @Test
    @UserStoryId(storyId = {1277, 1910, 3869})
    @Tag(SMOKE_TEST)
    void shouldGetBurnRulesById() {
        val mobileContents = MobileContentCreateRequest
                .builder()
                .mobileLanguage(Localization.EN)
                .title(generateRandomString(12))
                .description(generateRandomString(30))
                .build();

        val burnRuleObj = BurnRuleCreateRequestModel
                .burnRuleCreateRequestModelBuilder()
                .title(generateRandomString())
                .description(generateRandomString())
                .mobileContents(new MobileContentCreateRequest[]{mobileContents})
                .amountInTokens(Double.valueOf(generateRandomInt()).toString())
                .amountInCurrency(generateRandomInt())
                .businessVertical(BusinessVertical.REAL_ESTATE)
                .order(ORDER)
                .build();

        final String burnRuleId = getBurnRuleId(burnRuleObj, token);
        val actualResult = getBurnRuleById(burnRuleId, token);

        val expectedContent = burnRuleObj.getMobileContents()[0];
        MobileContentResponse actualContent = actualResult.getMobileContents()[0];

        assertAll(
                () -> assertEquals(burnRuleObj.getAmountInCurrency(), actualResult.getAmountInCurrency()),
                () -> assertEquals(burnRuleObj.getAmountInTokens(), actualResult.getAmountInTokens()),
                () -> assertEquals(burnRuleObj.getDescription(), actualResult.getDescription()),
                () -> assertEquals(burnRuleObj.getTitle(), actualResult.getTitle()),
                () -> assertNotNull(actualResult.getId()),
                () -> assertEquals(expectedContent.getDescription(), actualContent.getDescription()),
                () -> assertEquals(expectedContent.getTitle(), actualContent.getTitle()),
                () -> assertEquals(expectedContent.getMobileLanguage(), actualContent.getMobileLanguage()),
                () -> assertNotNull(actualContent.getImageId()),
                () -> assertNotNull(actualContent.getDescriptionId()),
                () -> assertNotNull(actualContent.getTitleId()),
                () -> assertNull(actualContent.getImage().getId()),
                () -> assertNull(actualContent.getImage().getImageBlobUrl()),
                () -> assertNotNull(actualContent.getImage().getRuleContentId()),
                // FAL-3869
                () -> assertEquals(ORDER, actualResult.getOrder())
        );
    }

    @Test
    @UserStoryId(storyId = 1277)
    void shouldNotGetBurnRuleByIdWhenBurnRuleWithSuchIdDoesNotExist() {
        val id = getRandomUuid();
        getBurnRuleById_Response(id, token)
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .body(ERROR_FIELD, CoreMatchers.equalTo(ENTITY_NOT_FOUND))
                .body(MESSAGE_FIELD, CoreMatchers.equalTo(String.format(BURN_RULE_DOES_NOT_EXIST_MSG, id)));
    }

    @Test
    @UserStoryId(storyId = 1277)
    void shouldCreateAndGetBurnRuleWithAllContentTypes() {

        val mobileContentsEn = MobileContentCreateRequest
                .builder()
                .mobileLanguage(Localization.EN)
                .title(generateRandomString(12))
                .description(generateRandomString(30))
                .build();

        val mobileContentsAr = MobileContentCreateRequest
                .builder()
                .mobileLanguage(Localization.AR)
                .title(generateRandomString(20))
                .description(generateRandomString(49))
                .build();

        val burnRuleObj = BurnRuleCreateRequestModel
                .burnRuleCreateRequestModelBuilder()
                .title(generateRandomString())
                .description(generateRandomString())
                .mobileContents(new MobileContentCreateRequest[]{mobileContentsEn, mobileContentsAr})
                .amountInTokens(Double.valueOf(generateRandomInt()).toString())
                .amountInCurrency(generateRandomInt())
                .businessVertical(BusinessVertical.RETAIL)
                .build();

        val burnRuleId = getBurnRuleId(burnRuleObj, token);
        val actualResult = getBurnRuleById(burnRuleId, token);

        val expectedContent_0 = burnRuleObj.getMobileContents()[0];
        val actualContent_0 = actualResult.getMobileContents()[0];
        val expectedContent_1 = burnRuleObj.getMobileContents()[1];
        val actualContent_1 = actualResult.getMobileContents()[1];

        assertAll(
                () -> assertEquals(burnRuleObj.getAmountInCurrency(), actualResult.getAmountInCurrency()),
                () -> assertEquals(burnRuleObj.getAmountInTokens(), actualResult.getAmountInTokens()),
                () -> assertEquals(burnRuleObj.getDescription(), actualResult.getDescription()),
                () -> assertEquals(burnRuleObj.getTitle(), actualResult.getTitle()),
                () -> assertNotNull(actualResult.getId()),
                () -> assertEquals(expectedContent_0.getDescription(), actualContent_0.getDescription()),
                () -> assertEquals(expectedContent_0.getTitle(), actualContent_0.getTitle()),
                () -> assertEquals(expectedContent_0.getMobileLanguage(), actualContent_0.getMobileLanguage()),
                () -> assertNotNull(actualContent_0.getImageId()),
                () -> assertNotNull(actualContent_0.getDescriptionId()),
                () -> assertNotNull(actualContent_0.getTitleId()),
                () -> assertNull(actualContent_0.getImage().getId()),
                () -> assertNull(actualContent_0.getImage().getImageBlobUrl()),
                () -> assertNotNull(actualContent_0.getImage().getRuleContentId()),
                () -> assertEquals(expectedContent_1.getDescription(), actualContent_1.getDescription()),
                () -> assertEquals(expectedContent_1.getTitle(), actualContent_1.getTitle()),
                () -> assertEquals(expectedContent_1.getMobileLanguage(), actualContent_1.getMobileLanguage()),
                () -> assertNotNull(actualContent_1.getImageId()),
                () -> assertNotNull(actualContent_1.getDescriptionId()),
                () -> assertNotNull(actualContent_1.getTitleId()),
                () -> assertNull(actualContent_1.getImage().getId()),
                () -> assertNull(actualContent_1.getImage().getImageBlobUrl()),
                () -> assertNotNull(actualContent_1.getImage().getRuleContentId())
        );
    }

    @Test
    @UserStoryId(storyId = 1277)
    void shouldHaveOnlyOneContentForTypeByLocalization() {
        val burnRuleObj = BurnRuleCreateRequest
                .builder()
                .title(generateRandomString())
                .description(generateRandomString())
                .businessVertical(BUSINESS_VERTICAL)
                .mobileContents(new MobileContentCreateRequest[]{mobileContents, mobileContents})
                // TODO: generateRandomMoney18
                .amountInTokens(Double.valueOf(generateRandomInt()).toString())
                .amountInCurrency(generateRandomInt())
                .build();

        createBurnRule_response(burnRuleObj, token)
                .then()
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .body(ERROR_MESSAGE_FIELD, equalTo(SH_ONE_CONTENT_FOR_TYPE_BY_LOC_MSG))
                .body(BURN_RULE_CONTENTS_0_FIELD, equalTo(SH_ONE_CONTENT_FOR_TYPE_BY_LOC_MSG));
    }

    @Test
    @UserStoryId(storyId = 1277)
    void shouldHaveAtLeastOneEnglishContent() {
        val burnRuleObj = BurnRuleCreateRequest
                .builder()
                .title(generateRandomString())
                .description(generateRandomString())
                .mobileContents(new MobileContentCreateRequest[]{})
                .amountInTokens(Double.valueOf(generateRandomInt()).toString())
                .amountInCurrency(generateRandomInt())
                .build();

        createBurnRule_response(burnRuleObj, token)
                .then()
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .body(MOBILE_CONTENTS_0_FIELD, equalTo(SHOULD_HAVE_AT_LEAST_ONE_ITEM_MSG));
    }

    @Test
    @UserStoryId(storyId = {1277, 1910, 3869})
    void shouldUpdateBurnRule() {
        val id = getBurnRuleId(burnRule, token);
        val burnRuleContent = getBurnRuleById(id, token);
        val titleId = burnRuleContent.getMobileContents()[0].getTitleId();
        val descriptionId = burnRuleContent.getMobileContents()[0].getDescriptionId();
        val imageId = burnRuleContent.getMobileContents()[0].getImageId();

        val mobileContents = MobileContentEditRequest
                .mobileContentEditRequestBuilder()
                .titleId(titleId)
                .descriptionId(descriptionId)
                .imageId(imageId)
                .mobileLanguage(Localization.EN)
                .title(generateRandomString(20))
                .description(generateRandomString(49))
                .build();

        BurnRuleUpdateRequest burnRuleUpdateObj = BurnRuleUpdateRequest
                .burnRuleUpdateRequestBuilder()
                .id(id)
                .title(generateRandomString())
                .description(generateRandomString())
                .businessVertical(BusinessVertical.RETAIL)
                .mobileContents(new MobileContentEditRequest[]{mobileContents})
                .amountInTokens(Double.valueOf(generateRandomInt()).toString())
                .amountInCurrency(generateRandomInt())
                .order(ORDER)
                .build();

        updateBurnRule(burnRuleUpdateObj, token)
                .then()
                .assertThat()
                .statusCode(SC_NO_CONTENT);

        val updatedBurnRuleContent = getBurnRuleById(id, token);

        val expectedContent = burnRuleUpdateObj.getMobileContents()[0];
        val actualContent = updatedBurnRuleContent.getMobileContents()[0];

        assertAll(
                () -> assertEquals(burnRuleUpdateObj.getAmountInCurrency(),
                        updatedBurnRuleContent.getAmountInCurrency()),
                () -> assertEquals(burnRuleUpdateObj.getAmountInTokens(), updatedBurnRuleContent.getAmountInTokens()),
                () -> assertEquals(burnRuleUpdateObj.getDescription(), updatedBurnRuleContent.getDescription()),
                () -> assertEquals(burnRuleUpdateObj.getTitle(), updatedBurnRuleContent.getTitle()),
                () -> assertEquals(burnRuleUpdateObj.getId(), updatedBurnRuleContent.getId()),

                () -> assertEquals(expectedContent.getDescription(), actualContent.getDescription()),
                () -> assertEquals(expectedContent.getTitle(), actualContent.getTitle()),
                () -> assertEquals(expectedContent.getMobileLanguage(), actualContent.getMobileLanguage()),
                () -> assertNull(actualContent.getImage().getId()),
                () -> assertNull(actualContent.getImage().getImageBlobUrl()),
                () -> assertNotNull(actualContent.getImage().getRuleContentId()),
                // FAL-3869
                () -> assertEquals(ORDER, updatedBurnRuleContent.getOrder())
        );
    }

    @Test
    @UserStoryId(storyId = 1910)
    void shouldNotUpdateLocalization() {
        val id = getBurnRuleId(burnRule, token);
        val burnRuleContent = getBurnRuleById(id, token);
        val titleId = burnRuleContent.getMobileContents()[0].getTitleId();
        val descriptionId = burnRuleContent.getMobileContents()[0].getDescriptionId();
        val imageId = burnRuleContent.getMobileContents()[0].getImageId();

        val mobileContents = MobileContentEditRequest
                .mobileContentEditRequestBuilder()
                .titleId(titleId)
                .descriptionId(descriptionId)
                .imageId(imageId)
                .mobileLanguage(Localization.AR)
                .title(generateRandomString(20))
                .description(generateRandomString(49))
                .build();

        val burnRuleUpdateObj = BurnRuleUpdateRequest
                .burnRuleUpdateRequestBuilder()
                .id(id)
                .title(generateRandomString())
                .description(generateRandomString())
                .businessVertical(BusinessVertical.RETAIL)
                .mobileContents(new MobileContentEditRequest[]{mobileContents})
                .amountInTokens(Double.valueOf(generateRandomInt()).toString())
                .amountInCurrency(generateRandomInt())
                .build();

        updateBurnRule(burnRuleUpdateObj, token)
                .then()
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .body(MOBILE_CONTENTS_0_FIELD, equalTo(EN_CONTENT_IS_REQUIRED_MSG));
    }

    @Test
    @UserStoryId(storyId = 1910)
    void titleIdShouldBeValid() {
        val id = getBurnRuleId(burnRule, token);
        val burnRuleContent = getBurnRuleById(id, token);
        val titleId = getRandomUuid();
        val descriptionId = burnRuleContent.getMobileContents()[0].getDescriptionId();
        val imageId = burnRuleContent.getMobileContents()[0].getImageId();

        val mobileContents = MobileContentEditRequest
                .mobileContentEditRequestBuilder()
                .titleId(titleId)
                .descriptionId(descriptionId)
                .imageId(imageId)
                .mobileLanguage(Localization.EN)
                .title(generateRandomString(20))
                .description(generateRandomString(49))
                .build();

        val burnRuleUpdateObj = BurnRuleUpdateRequest
                .burnRuleUpdateRequestBuilder()
                .id(id)
                .title(generateRandomString())
                .description(generateRandomString())
                .businessVertical(BUSINESS_VERTICAL)
                .mobileContents(new MobileContentEditRequest[]{mobileContents})
                .amountInTokens(Double.valueOf(generateRandomInt()).toString())
                .amountInCurrency(generateRandomInt())
                .build();

        updateBurnRule(burnRuleUpdateObj, token)
                .then()
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .body(ERROR_FIELD, equalTo(ENTITY_NOT_VALID))
                .body(MESSAGE_FIELD, equalTo(String.format(RULE_DOES_NOT_HAVE_ANY_CONTENT_MSG, titleId)));
    }

    @Test
    @UserStoryId(storyId = 1910)
    void descriptionIdShouldBeValid() {
        val id = getBurnRuleId(burnRule, token);
        val burnRuleContent = getBurnRuleById(id, token);
        val titleId = burnRuleContent.getMobileContents()[0].getTitleId();
        val descriptionId = getRandomUuid();
        val imageId = burnRuleContent.getMobileContents()[0].getImageId();

        val mobileContents = MobileContentEditRequest
                .mobileContentEditRequestBuilder()
                .titleId(titleId)
                .descriptionId(descriptionId)
                .imageId(imageId)
                .mobileLanguage(Localization.EN)
                .title(generateRandomString(20))
                .description(generateRandomString(49))
                .build();

        val burnRuleUpdateObj = BurnRuleUpdateRequest
                .burnRuleUpdateRequestBuilder()
                .id(id)
                .title(generateRandomString())
                .description(generateRandomString())
                .businessVertical(BUSINESS_VERTICAL)
                .mobileContents(new MobileContentEditRequest[]{mobileContents})
                .amountInTokens(Double.valueOf(generateRandomInt()).toString())
                .amountInCurrency(generateRandomInt())
                .build();

        updateBurnRule(burnRuleUpdateObj, token)
                .then()
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .body(ERROR_FIELD, equalTo(ENTITY_NOT_VALID))
                .body(MESSAGE_FIELD, equalTo(String.format(RULE_DOES_NOT_HAVE_ANY_CONTENT_MSG, descriptionId)));
    }

    @Test
    @UserStoryId(storyId = 1910)
    void imageIdShouldBeValid() {
        val id = getBurnRuleId(burnRule, token);
        val burnRuleContent = getBurnRuleById(id, token);
        val titleId = burnRuleContent.getMobileContents()[0].getTitleId();
        val descriptionId = burnRuleContent.getMobileContents()[0].getDescriptionId();
        val imageId = getRandomUuid();

        val mobileContents = MobileContentEditRequest
                .mobileContentEditRequestBuilder()
                .titleId(titleId)
                .descriptionId(descriptionId)
                .imageId(imageId)
                .mobileLanguage(Localization.EN)
                .title(generateRandomString(20))
                .description(generateRandomString(49))
                .build();

        val burnRuleUpdateObj = BurnRuleUpdateRequest
                .burnRuleUpdateRequestBuilder()
                .id(id)
                .title(generateRandomString())
                .description(generateRandomString())
                .businessVertical(BUSINESS_VERTICAL)
                .mobileContents(new MobileContentEditRequest[]{mobileContents})
                .amountInTokens(Double.valueOf(generateRandomInt()).toString())
                .amountInCurrency(generateRandomInt())
                .build();

        updateBurnRule(burnRuleUpdateObj, token)
                .then()
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .body(ERROR_FIELD, equalTo(ENTITY_NOT_VALID))
                .body(MESSAGE_FIELD, equalTo(String.format(RULE_DOES_NOT_HAVE_ANY_CONTENT_MSG, imageId)));
    }

    @ParameterizedTest(name = "Run {index}: title={0}, description={1}, field={2}, message={3}")
    @MethodSource("burnRuleEditInvalidParams")
    @UserStoryId(storyId = 1910)
    void shouldNotUpdateBurnRuleWhenConditionTypeIsNotTitle(String title, String description, String field,
            String message) {
        val id = getBurnRuleId(burnRule, token);
        val burnRuleContent = getBurnRuleById(id, token);
        val titleId = burnRuleContent.getMobileContents()[0].getTitleId();
        val descriptionId = burnRuleContent.getMobileContents()[0].getDescriptionId();
        val imageId = getRandomUuid();

        val mobileContents = MobileContentEditRequest
                .mobileContentEditRequestBuilder()
                .titleId(titleId)
                .descriptionId(descriptionId)
                .imageId(imageId)
                .mobileLanguage(Localization.EN)
                .title(title)
                .description(description)
                .build();

        val burnRuleUpdateObj = BurnRuleUpdateRequest
                .burnRuleUpdateRequestBuilder()
                .id(id)
                .title(generateRandomString())
                .description(generateRandomString())
                .businessVertical(BUSINESS_VERTICAL)
                .mobileContents(new MobileContentEditRequest[]{mobileContents})
                .amountInTokens(Double.valueOf(generateRandomInt()).toString())
                .amountInCurrency(generateRandomInt())
                .build();

        updateBurnRule(burnRuleUpdateObj, token)
                .then()
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .body(field, equalTo(message));
    }

    @ParameterizedTest
    @CsvSource({"test_image.png",
            "test_image.jpg"})
    @UserStoryId(storyId = 1277)
    void shouldUploadImage(String imageName) {
        val rule = createBurnRule(burnRule, token);
        val burnRuleId = rule.getId();
        val ruleContentId = rule.getCreatedImageContents()[0].getRuleContentId();
        val imagePath = HelperUtils.getImagePath(imageName);

        addAnImage(ruleContentId, imagePath, token)
                .then()
                .assertThat()
                .statusCode(SC_NO_CONTENT);

        val actualResult = getBurnRuleById(burnRuleId, token);
        val mobileContents = actualResult.getMobileContents()[0];

        assertAll(
                () -> assertNotNull(mobileContents.getImage().getId()),
                () -> assertEquals(ruleContentId, mobileContents.getImage().getRuleContentId()),
                () -> assertNotNull(mobileContents.getImage().getImageBlobUrl())
        );
    }

    @Test
    @UserStoryId(storyId = 1277)
    void uploadImage_notSupportedImageFormat() {
        val rule = createBurnRule(burnRule, token);
        val ruleContentId = rule.getCreatedImageContents()[0].getRuleContentId();
        val imagePath = HelperUtils.getImagePath("invalid_format_img.tiff");

        addAnImage(ruleContentId, imagePath, token)
                .then()
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .body(ERROR_FIELD, equalTo(NOT_VALID_FILE_FORMAT))
                .body(MESSAGE_FIELD, equalTo(String.format(NOT_SUPPORTED_FILE_FORMAT_MSG, "image/tiff")));
    }

    @Test
    @UserStoryId(storyId = 1277)
    void uploadImage_notImageFile() {
        val rule = createBurnRule(burnRule, token);
        val ruleContentId = rule.getCreatedImageContents()[0].getRuleContentId();
        val imagePath = HelperUtils.getImagePath("test.txt");

        addAnImage(ruleContentId, imagePath, token)
                .then()
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .body(ERROR_FIELD, equalTo(NOT_VALID_FILE_FORMAT))
                .body(MESSAGE_FIELD, equalTo(String.format(NOT_SUPPORTED_FILE_FORMAT_MSG, "text/plain")));
    }

    @Test
    @UserStoryId(storyId = 1277)
    void uploadImage_notValidRuleContentId() {
        val ruleContentId = getRandomUuid();
        val imagePath = HelperUtils.getImagePath("test_image.png");

        addAnImage(ruleContentId, imagePath, token)
                .then()
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .body(ERROR_FIELD, equalTo(ENTITY_NOT_FOUND))
                .body(MESSAGE_FIELD, equalTo(String.format(BR_CONTENT_TYPE_DN_EXIST_MSG, ruleContentId)));
    }

    @Test
    @UserStoryId(storyId = 1910)
    void shouldUpdateImage() {
        val rule = createBurnRule(burnRule, token);
        val burnRuleId = rule.getId();
        val ruleContentId = rule.getCreatedImageContents()[0].getRuleContentId();
        val imagePath = HelperUtils.getImagePath("test_image.png");
        val newImagePath = HelperUtils.getImagePath("test_image.jpg");

        addAnImage(ruleContentId, imagePath, token)
                .then()
                .assertThat()
                .statusCode(SC_NO_CONTENT);

        val actualResult = getBurnRuleById(burnRuleId, token);

        val id = actualResult.getMobileContents()[0].getImage().getId();
        val imageBlobUrl = actualResult.getMobileContents()[0].getImage().getImageBlobUrl();

        updateImage(burnRuleId, ruleContentId, newImagePath, token)
                .then()
                .assertThat()
                .statusCode(SC_NO_CONTENT);

        val updatedResult = getBurnRuleById(burnRuleId, token);
        val mobileContents = updatedResult.getMobileContents()[0];

        assertAll(
                () -> assertNotNull(mobileContents.getImage().getId()),
                () -> assertNotEquals(id, mobileContents.getImage().getId()),
                () -> assertEquals(ruleContentId, mobileContents.getImage().getRuleContentId()),
                () -> assertNotNull(mobileContents.getImage().getImageBlobUrl()),
                () -> assertNotEquals(imageBlobUrl, mobileContents.getImage().getImageBlobUrl())
        );
    }

    @Test
    @UserStoryId(storyId = 1910)
    void updateImage_invalidRuleContentId() {
        val rule = createBurnRule(burnRule, token);
        val burnRuleId = rule.getId();
        val ruleContentId = rule.getCreatedImageContents()[0].getRuleContentId();
        val invalidRuleContentId = getRandomUuid();
        val imagePath = HelperUtils.getImagePath("test_image.png");
        val newImagePath = HelperUtils.getImagePath("test_image.jpg");

        addAnImage(ruleContentId, imagePath, token)
                .then()
                .assertThat()
                .statusCode(SC_NO_CONTENT);

        updateImage(burnRuleId, invalidRuleContentId, newImagePath, token)
                .then()
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .body(ERROR_FIELD, equalTo(ENTITY_NOT_FOUND))
                .body(MESSAGE_FIELD, equalTo(String.format(BR_CONTENT_TYPE_DN_EXIST_MSG, invalidRuleContentId)));
    }

    @Test
    @UserStoryId(storyId = 1910)
    void updateImage_notSupportedImageFormat() {
        val rule = createBurnRule(burnRule, token);
        val burnRuleId = rule.getId();
        val ruleContentId = rule.getCreatedImageContents()[0].getRuleContentId();
        val imagePath = HelperUtils.getImagePath("test_image.png");
        val newImagePath = HelperUtils.getImagePath("invalid_format_img.tiff");

        addAnImage(ruleContentId, imagePath, token)
                .then()
                .assertThat()
                .statusCode(SC_NO_CONTENT);

        updateImage(burnRuleId, ruleContentId, newImagePath, token)
                .then()
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .body(ERROR_FIELD, equalTo(NOT_VALID_FILE_FORMAT))
                .body(MESSAGE_FIELD, equalTo(String.format(NOT_SUPPORTED_FILE_FORMAT_MSG, "image/tiff")));
    }

    @Test
    @UserStoryId(storyId = 1910)
    void updateImage_notImageFile() {
        val rule = createBurnRule(burnRule, token);
        val burnRuleId = rule.getId();
        val ruleContentId = rule.getCreatedImageContents()[0].getRuleContentId();
        val imagePath = HelperUtils.getImagePath("test_image.png");
        val newImagePath = HelperUtils.getImagePath("test.txt");

        addAnImage(ruleContentId, imagePath, token)
                .then()
                .assertThat()
                .statusCode(SC_NO_CONTENT);

        updateImage(burnRuleId, ruleContentId, newImagePath, token)
                .then()
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .body(ERROR_FIELD, equalTo(NOT_VALID_FILE_FORMAT))
                .body(MESSAGE_FIELD, equalTo(String.format(NOT_SUPPORTED_FILE_FORMAT_MSG, "text/plain")));
    }

    @Test
    @UserStoryId(storyId = 2601)
    void shouldDeleteBurnRule() {
        val burnRuleId = createBurnRuleWithAllContentTypes(true);

        deleteBurnRule(burnRuleId, token);

        getBurnRuleById_Response(burnRuleId, token)
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .body(ERROR_FIELD, CoreMatchers.equalTo(ENTITY_NOT_FOUND))
                .body(MESSAGE_FIELD, CoreMatchers.equalTo(String.format(BURN_RULE_DOES_NOT_EXIST_MSG, burnRuleId)));
    }

    @Test
    @UserStoryId(storyId = 2601)
    void shouldNotDeleteBurnRuleWhenTheIdIsNotValied() {
        val burnRuleId = "invalid-burn-rule-id";

        deleteBurnRule(burnRuleId, token)
                .then()
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .body(ERROR_FIELD, equalTo(INVALID_BURN_RULE))
                .body(MESSAGE_FIELD, equalTo(INVALID_BURN_RULE_MSG));
    }
}
