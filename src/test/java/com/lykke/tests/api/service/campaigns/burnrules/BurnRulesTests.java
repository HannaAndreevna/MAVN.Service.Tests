package com.lykke.tests.api.service.campaigns.burnrules;

import static com.lykke.api.testing.api.common.GenerateUtils.generateRandomString;
import static com.lykke.api.testing.api.common.GenerateUtils.getRandomUuid;
import static com.lykke.tests.api.common.CommonConsts.SMOKE_TEST;
import static com.lykke.tests.api.common.PaginationConts.CURRENT_PAGE_LOWER_BOUNDARY;
import static com.lykke.tests.api.common.PaginationConts.PAGE_SIZE_UPPER_BOUNDARY;
import static com.lykke.tests.api.service.campaigns.BurnRulesUtils.createBurnRule;
import static com.lykke.tests.api.service.campaigns.BurnRulesUtils.deleteBurnRuleById;
import static com.lykke.tests.api.service.campaigns.BurnRulesUtils.editBurnRule;
import static com.lykke.tests.api.service.campaigns.BurnRulesUtils.getBurnRuleById;
import static com.lykke.tests.api.service.campaigns.BurnRulesUtils.getBurnRuleContentId;
import static com.lykke.tests.api.service.campaigns.BurnRulesUtils.getBurnRuleId;
import static com.lykke.tests.api.service.campaigns.BurnRulesUtils.getPaginatedBurnRulesList;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.params.provider.Arguments.of;

import com.lykke.api.testing.annotations.UserStoryId;
import com.lykke.api.testing.api.common.FakerUtils;
import com.lykke.tests.api.base.BaseApiTest;
import com.lykke.tests.api.common.PaginationConts;
import com.lykke.tests.api.service.campaigns.model.burnrules.BurnRuleContentCreateRequestModel;
import com.lykke.tests.api.service.campaigns.model.burnrules.BurnRuleContentEditRequestModel;
import com.lykke.tests.api.service.campaigns.model.burnrules.BurnRuleCreateRequestModel;
import com.lykke.tests.api.service.campaigns.model.burnrules.BurnRuleEditRequestModel;
import com.lykke.tests.api.service.campaigns.model.burnrules.BurnRuleInfoResponseModel;
import com.lykke.tests.api.service.campaigns.model.burnrules.BurnRulesCreateRequest;
import com.lykke.tests.api.service.campaigns.model.burnrules.Localization;
import com.lykke.tests.api.service.campaigns.model.burnrules.RuleContentType;
import com.lykke.tests.api.service.campaigns.model.burnrules.Vertical;
import java.util.Arrays;
import java.util.UUID;
import java.util.stream.Stream;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

public class BurnRulesTests extends BaseApiTest {

    private static final String DESCRIPTION = FakerUtils.randomQuote;
    private static final String TITLE = FakerUtils.title;
    private static final RuleContentType RULE_CONTENT_TYPE_TITLE = RuleContentType.TITLE;
    private static final RuleContentType RULE_CONTENT_TYPE_DESCRIPTION = RuleContentType.DESCRIPTION;
    private static final RuleContentType RULE_CONTENT_TYPE_URL_FOR_PICTURE = RuleContentType.URL_FOR_PICTURE;
    private static final Localization LOCALIZATION_EN = Localization.EN;
    private static final Localization LOCALIZATION_AR = Localization.AR;
    private static final String CREATED_BY = FakerUtils.fullName;
    private static final Double AMOUNT_IN_TOKENS = 10.0;
    private static final int AMOUNT_IN_CURRENCY = 20;
    private static final Double INITIAL_PRICE = 23.30;
    private static final Double NEW_PRICE = 37.28;
    private static final String ERROR_MESSAGE = "ErrorMessage";
    private static final String MODEL_ERRORS = "ModelErrors";
    private static final String ERROR_CODE = "ErrorCode";
    private static final String BURN_RULE_CONTENT_0_FIELD = "BurnRuleContents[0]";
    private static final String BURN_RULE_CONTENT_1_FIELD = "BurnRuleContents[1]";
    private static final String SHOULD_HAVE_1_EN_CONTENT_MSG =
            "You should have at least one English content";
    private static final String TITLE_IN_EN_IS_MANDATORY_MSG =
            "Rule Content 'Title' in English is mandatory";
    private static final String SH_1_CONTENT_BY_LOCALIZATION_MSG =
            "Should have only one content for 'type' by localization";
    private static final String LENGTH_OF_VALUE_MUST_BE_AT_LEAST_3_MESSAGE
            = "Title length should be between 3 and 50 characters";
    private static final String DESCRIPTION_LENGTH_SHOULD_BE_BETWEEN_3_AND_1000_CHARACTERS_MESSAGE
            = "Description length should be between 3 and 1000 characters";
    private static final String LENGTH_OF_VALUE_MUST_BE_50_MESSAGE
            = "Title length should be between 3 and 50 characters";
    private static final String BR_VALUE_NOT_BE_EMPTY = "'Value' must not be empty.";
    private static final String LENGTH_OF_VALUE_MUST_BE_1000_MESSAGE
            = "Description length should be between 3 and 1000 characters";
    private static final String TITLE_MUST_NOT_BE_EMPTY = "'Title' must not be empty.";
    private static final String LENGTH_OF_TITLE_MUST_BE_AT_LEAST_3_MESSAGE =
            "The length of 'Title' must be at least 3 characters. You entered 2 characters.";
    private static final String LENGTH_OF_TITLE_MUST_BE_50_CH_OR_FEWER_MESSAGE =
            "The length of 'Title' must be 50 characters or fewer. You entered 51 characters.";
    private static final String LENGTH_OF_DESCRIPTION_MUST_BE_AT_LEAST_3_MESSAGE =
            "The length of 'Description' must be at least 3 characters. You entered 2 characters.";
    private static final String LENGTH_OF_DESCRIPTION_MUST_BE_1000_CH_OR_FEWER_MESSAGE =
            "The length of 'Description' must be 1000 characters or fewer. You entered 1001 characters.";
    private static final String INVALID_ID_MSG = "Rule does not have any contents with id: %s";
    private static final String VALUE_MUST_NOT_BE_EMPTY_MSG = "Title is required";
    private static final String BURN_RULE_DOESNT_EXIST_MSG = "Burn rule with id %s does not exist.";
    private static final int ORDER = 37;
    private static String burnRuleId;
    private static String value = FakerUtils.title;
    BurnRuleContentCreateRequestModel burnRuleContentTitle;
    BurnRuleContentCreateRequestModel burnRuleContentDescription;
    BurnRuleCreateRequestModel burnRuleCreateRequest;
    BurnRuleContentCreateRequestModel.BurnRuleContentCreateRequestModelBuilder baseBurnRuleContentTitle;
    BurnRuleCreateRequestModel.BurnRuleCreateRequestModelBuilder baseBurnRuleCreateRequest;
    BurnRuleContentCreateRequestModel.BurnRuleContentCreateRequestModelBuilder baseBurnRuleContentDescription;

    private static Stream burnRule_invalidParams() {
        return Stream.of(
                of(EMPTY, TITLE, DESCRIPTION, AMOUNT_IN_TOKENS, AMOUNT_IN_CURRENCY),
                of(FakerUtils.fullName, EMPTY, DESCRIPTION, AMOUNT_IN_TOKENS, AMOUNT_IN_CURRENCY),
                of(FakerUtils.fullName, generateRandomString(2), DESCRIPTION, AMOUNT_IN_TOKENS, AMOUNT_IN_CURRENCY),
                of(FakerUtils.fullName, generateRandomString(51), DESCRIPTION, AMOUNT_IN_TOKENS, AMOUNT_IN_CURRENCY),
                of(FakerUtils.fullName, TITLE, generateRandomString(2), AMOUNT_IN_TOKENS, AMOUNT_IN_CURRENCY),
                of(FakerUtils.fullName, TITLE, generateRandomString(1001), AMOUNT_IN_TOKENS, AMOUNT_IN_CURRENCY),
                of(FakerUtils.fullName, TITLE, DESCRIPTION, -1, AMOUNT_IN_CURRENCY),
                of(FakerUtils.fullName, TITLE, DESCRIPTION, 0, AMOUNT_IN_CURRENCY),
                of(FakerUtils.fullName, TITLE, DESCRIPTION, AMOUNT_IN_TOKENS, -1),
                of(FakerUtils.fullName, TITLE, DESCRIPTION, AMOUNT_IN_TOKENS, 0)
        );
    }

    private static Stream burnRuleEditInvalidParams() {
        return Stream.of(
                of(TITLE, DESCRIPTION, TITLE_IN_EN_IS_MANDATORY_MSG),
                of(EMPTY, DESCRIPTION, TITLE_MUST_NOT_BE_EMPTY),
                of(generateRandomString(2), DESCRIPTION, LENGTH_OF_TITLE_MUST_BE_AT_LEAST_3_MESSAGE),
                of(generateRandomString(51), DESCRIPTION, LENGTH_OF_TITLE_MUST_BE_50_CH_OR_FEWER_MESSAGE),
                of(TITLE, generateRandomString(2), LENGTH_OF_DESCRIPTION_MUST_BE_AT_LEAST_3_MESSAGE),
                of(TITLE, generateRandomString(1001), LENGTH_OF_DESCRIPTION_MUST_BE_1000_CH_OR_FEWER_MESSAGE)
        );
    }

    private static Stream valueInvalidParams() {
        return Stream.of(
                of(EMPTY, generateRandomString(30), VALUE_MUST_NOT_BE_EMPTY_MSG),
                of(generateRandomString(2), generateRandomString(30), LENGTH_OF_VALUE_MUST_BE_AT_LEAST_3_MESSAGE),
                of(generateRandomString(51), generateRandomString(30), LENGTH_OF_VALUE_MUST_BE_50_MESSAGE),
                of(generateRandomString(30), generateRandomString(2),
                        DESCRIPTION_LENGTH_SHOULD_BE_BETWEEN_3_AND_1000_CHARACTERS_MESSAGE),
                of(generateRandomString(30), generateRandomString(1001), LENGTH_OF_VALUE_MUST_BE_1000_MESSAGE)
        );
    }

    @BeforeEach
    void setup() {
        baseBurnRuleContentTitle = BurnRuleContentCreateRequestModel
                .builder()
                .ruleContentType(RULE_CONTENT_TYPE_TITLE)
                .localization(LOCALIZATION_EN)
                .value(value);

        burnRuleContentTitle = baseBurnRuleContentTitle
                .build();

        baseBurnRuleContentDescription = BurnRuleContentCreateRequestModel
                .builder()
                .ruleContentType(RULE_CONTENT_TYPE_DESCRIPTION)
                .localization(LOCALIZATION_EN)
                .value(value);

        burnRuleContentDescription = baseBurnRuleContentDescription
                .build();

        baseBurnRuleCreateRequest = BurnRuleCreateRequestModel
                .burnRuleCreateRequestBuilder()
                .createdBy(CREATED_BY)
                .burnRuleContents(
                        new BurnRuleContentCreateRequestModel[]{burnRuleContentTitle, burnRuleContentDescription})
                .title(TITLE)
                .description(DESCRIPTION)
                .amountInTokens(AMOUNT_IN_TOKENS.toString())
                .amountInCurrency(AMOUNT_IN_CURRENCY)
                .vertical(Vertical.HOSPITALITY)
                .price(INITIAL_PRICE)
                .order(ORDER);

        burnRuleCreateRequest = baseBurnRuleCreateRequest
                .build();

        burnRuleId = getBurnRuleId(burnRuleCreateRequest);
    }

    @Test
    @UserStoryId(storyId = 1559)
    @Tag(SMOKE_TEST)
    void shouldGetBurnRuleById() {
        val actualResult = getBurnRuleById(burnRuleId);

        val actualBurnRuleContents = actualResult.getBurnRuleContents()[1];
        assertAll(
                () -> assertNotNull(actualResult.getBurnRuleContents()[1].getId()),
                () -> assertEquals(Localization.EN.getCode(), actualBurnRuleContents.getLocalization()),
                () -> assertTrue(Arrays.stream(actualResult.getBurnRuleContents())
                        .filter(content -> content.getRuleContentType().equalsIgnoreCase(
                                RULE_CONTENT_TYPE_TITLE.getCode())).findAny().isPresent()),
                () -> assertTrue(Arrays.stream(actualResult.getBurnRuleContents())
                        .filter(content -> content.getRuleContentType().equalsIgnoreCase(
                                RULE_CONTENT_TYPE_DESCRIPTION.getCode())).findAny().isPresent()),
                () -> assertEquals(value, actualBurnRuleContents.getValue()),
                () -> assertNull(actualBurnRuleContents.getImage()),
                () -> assertEquals(DESCRIPTION, actualResult.getDescription()),
                () -> assertNotNull(actualResult.getId()),
                () -> assertEquals(TITLE, actualResult.getTitle()),
                () -> assertEquals(AMOUNT_IN_CURRENCY, actualResult.getAmountInCurrency()),
                () -> assertEquals(AMOUNT_IN_TOKENS.toString(), actualResult.getAmountInTokens()),
                () -> assertEquals("None", actualResult.getErrorCode()),
                () -> assertNull(actualResult.getErrorMessage())
        );
    }

    @Test
    @UserStoryId(storyId = 1559)
    @Tag(SMOKE_TEST)
    void shouldCreateAndGetBurnRuleWithAllContentTypes() {
        val burnRuleContentTitleArValue = FakerUtils.title;
        val burnRuleContentDescriptionEnValue = FakerUtils.randomQuote;
        val burnRuleContentDescriptionArValue = FakerUtils.randomQuote;

        val burnRuleContentTitleAr = BurnRuleContentCreateRequestModel
                .builder()
                .ruleContentType(RULE_CONTENT_TYPE_TITLE)
                .localization(LOCALIZATION_AR)
                .value(burnRuleContentTitleArValue)
                .build();

        val burnRuleContentDescriptionEn = BurnRuleContentCreateRequestModel
                .builder()
                .ruleContentType(RULE_CONTENT_TYPE_DESCRIPTION)
                .localization(LOCALIZATION_EN)
                .value(burnRuleContentDescriptionEnValue)
                .build();

        val burnRuleContentDescriptionAr = BurnRuleContentCreateRequestModel
                .builder()
                .ruleContentType(RULE_CONTENT_TYPE_DESCRIPTION)
                .localization(LOCALIZATION_AR)
                .value(burnRuleContentDescriptionArValue)
                .build();

        val burnRuleContentUrlForPictureEn = BurnRuleContentCreateRequestModel
                .builder()
                .ruleContentType(RULE_CONTENT_TYPE_URL_FOR_PICTURE)
                .localization(LOCALIZATION_EN)
                .build();

        val burnRuleContentUrlForPictureAr = BurnRuleContentCreateRequestModel
                .builder()
                .ruleContentType(RULE_CONTENT_TYPE_URL_FOR_PICTURE)
                .localization(LOCALIZATION_AR)
                .build();

        val burnRuleContentObject = new BurnRuleContentCreateRequestModel[]{
                burnRuleContentTitle, burnRuleContentTitleAr, burnRuleContentDescriptionEn,
                burnRuleContentDescriptionAr, burnRuleContentUrlForPictureEn, burnRuleContentUrlForPictureAr
        };

        val burnRuleRequest = baseBurnRuleCreateRequest
                .burnRuleContents(burnRuleContentObject)
                .build();

        val burnRuleId = getBurnRuleId(burnRuleRequest);

        val actualResult = getBurnRuleById(burnRuleId);

        assertAll(
                //TODO: Check if possible to compare the objects
                () -> assertEquals(actualResult.getBurnRuleContents().length, 6),
                () -> assertEquals(actualResult.getDescription(), DESCRIPTION),
                () -> assertNotNull(actualResult.getId()),
                () -> assertEquals(actualResult.getTitle(), TITLE),
                () -> assertEquals(actualResult.getAmountInTokens(), AMOUNT_IN_TOKENS.toString()),
                () -> assertEquals(actualResult.getAmountInCurrency(), AMOUNT_IN_CURRENCY),
                () -> assertEquals(actualResult.getErrorCode(), "None"),
                () -> assertNull(actualResult.getErrorMessage())
        );

    }

    @Test
    @UserStoryId(storyId = 1559)
    void shouldDeleteBurnRuleById() {
        deleteBurnRuleById(burnRuleId);

        val actualResult = getBurnRuleById(burnRuleId);
        assertAll(
                () -> assertNull(actualResult.getDescription()),
                () -> assertEquals(actualResult.getId(), "00000000-0000-0000-0000-000000000000"),
                () -> assertNull(actualResult.getTitle()),
                () -> assertEquals(actualResult.getErrorCode(), "EntityNotFound"),
                () -> assertEquals(actualResult.getErrorMessage(),
                        String.format(BURN_RULE_DOESNT_EXIST_MSG, burnRuleId))
        );
    }

    @Test
    @UserStoryId(storyId = 1559)
    void shouldHaveOnlyOneContentForTypeByLocalization() {
        val burnRuleRequest = baseBurnRuleCreateRequest
                .burnRuleContents(new BurnRuleContentCreateRequestModel[]{burnRuleContentTitle, burnRuleContentTitle,
                        burnRuleContentDescription})
                .build();

        createBurnRule(burnRuleRequest)
                .then()
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .body(ERROR_MESSAGE, equalTo(SH_1_CONTENT_BY_LOCALIZATION_MSG))
                .body(MODEL_ERRORS + "." + BURN_RULE_CONTENT_0_FIELD,
                        equalTo(SH_1_CONTENT_BY_LOCALIZATION_MSG));
    }

    @Test
    @UserStoryId(storyId = 1559)
    void shouldHaveAtLeastOneEnglishContent() {
        val burnRuleRequest = baseBurnRuleCreateRequest
                .burnRuleContents(new BurnRuleContentCreateRequestModel[]{})
                .build();

        createBurnRule(burnRuleRequest)
                .then()
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .body(ERROR_MESSAGE, equalTo(SHOULD_HAVE_1_EN_CONTENT_MSG))
                .body(MODEL_ERRORS + "." + BURN_RULE_CONTENT_0_FIELD,
                        equalTo(SHOULD_HAVE_1_EN_CONTENT_MSG))
                .body(MODEL_ERRORS + "." + BURN_RULE_CONTENT_1_FIELD,
                        equalTo(TITLE_IN_EN_IS_MANDATORY_MSG));
    }

    @ParameterizedTest(name = "Run {index}: createdBy={0}, title={1}, description={2}, amountInTokens={3}, "
            + "amountInCurrency={4}")
    @MethodSource("burnRule_invalidParams")
    @UserStoryId(storyId = 1559)
    void shouldValidateBurnRulesFields(String createdBy, String title, String description, Double amountInTokens,
            int amountInCurrency) {

        val requestObject = BurnRulesCreateRequest
                .builder()
                .burnRuleContents(
                        new BurnRuleContentCreateRequestModel[]{burnRuleContentTitle, burnRuleContentDescription})
                .title(title)
                .description(description)
                .createdBy(createdBy)
                .amountInTokens(amountInTokens.toString())
                .amountInCurrency(amountInCurrency)
                .vertical(Vertical.HOSPITALITY)
                .build();

        val response = createBurnRule(requestObject);

        assertEquals(requestObject.getValidationResponse(), response);
    }

    @Test
    @UserStoryId(storyId = {1559, 3915, 3871})
    @Tag(SMOKE_TEST)
    void shouldGetPaginatedResponse() {
        val response = getPaginatedBurnRulesList(burnRuleCreateRequest.getTitle(), 1, 100);
        val actualResult = Arrays.stream(response.getBurnRules())
                .filter(rule -> rule.getId().equalsIgnoreCase(burnRuleId))
                .findFirst()
                .orElse(new BurnRuleInfoResponseModel());

        assertAll(
                () -> assertEquals(CURRENT_PAGE_LOWER_BOUNDARY, response.getCurrentPage()),
                () -> assertEquals(PAGE_SIZE_UPPER_BOUNDARY, response.getPageSize()), // TODO: why it's not 100
                () -> assertNotNull(response.getTotalCount()),
                () -> assertNotNull(actualResult.getId()),
                () -> assertEquals(burnRuleCreateRequest.getTitle(), actualResult.getTitle()),
                // FAL-3915
                () -> assertEquals(INITIAL_PRICE, actualResult.getPrice()),
                // FAL-3871
                () -> assertTrue(0 < actualResult.getOrder())
        );
    }

    @Test
    @UserStoryId(storyId = {1559, 3871})
    @Tag(SMOKE_TEST)
    void shouldNotGetPaginatedResponse() {
        val response = getPaginatedBurnRulesList("asd1ea", 1, 100);

        assertAll(
                () -> assertEquals(1, response.getCurrentPage()),
                () -> assertEquals(500, response.getPageSize()), // TODO: why it's not 100
                () -> assertTrue(0 <= response.getTotalCount())
        );
    }

    @Test
    @UserStoryId(storyId = {1559, 3915, 3871})
    void shouldUpdateBurnRuleWithOneConditionType() {
        val value = FakerUtils.title;
        val description = FakerUtils.randomQuote;
        val title = FakerUtils.title;
        final Double newAmountInTokens = 5.0;
        val newAmountInCurrency = 12;

        val burnRuleContentEditObj = BurnRuleContentEditRequestModel
                .builder()
                .id(getBurnRuleContentId(burnRuleId))
                .ruleContentType(RULE_CONTENT_TYPE_TITLE)
                .localization(LOCALIZATION_EN)
                .value(value)
                .build();

        val burnRuleContentDescriptionEditObj = BurnRuleContentEditRequestModel
                .builder()
                .id(getBurnRuleContentId(burnRuleId, 1))
                .ruleContentType(RULE_CONTENT_TYPE_DESCRIPTION)
                .localization(LOCALIZATION_EN)
                .value(value)
                .build();

        val burnRuleEditObj = BurnRuleEditRequestModel
                .burnRuleEditRequestBuilder()
                .burnRuleContents(new BurnRuleContentEditRequestModel[]{burnRuleContentEditObj,
                        burnRuleContentDescriptionEditObj})
                .description(description)
                .title(title)
                .id(burnRuleId)
                .amountInTokens(newAmountInTokens.toString())
                .amountInCurrency(newAmountInCurrency)
                .vertical(Vertical.HOSPITALITY)
                .price(NEW_PRICE)
                .order(ORDER + 1)
                .build();

        editBurnRule(burnRuleEditObj, burnRuleContentEditObj, burnRuleContentDescriptionEditObj)
                .then()
                .assertThat()
                .statusCode(SC_OK);

        val actualResult = getBurnRuleById(burnRuleId);

        assertAll(
                () -> assertEquals(burnRuleContentEditObj.getId(), actualResult.getBurnRuleContents()[0].getId()),
                () -> assertEquals(burnRuleContentEditObj.getLocalization().getCode(),
                        actualResult.getBurnRuleContents()[0].getLocalization()),
                () -> assertEquals(burnRuleContentEditObj.getRuleContentType().getCode(),
                        actualResult.getBurnRuleContents()[0].getRuleContentType()),
                () -> assertEquals(burnRuleContentEditObj.getValue(), actualResult.getBurnRuleContents()[0].getValue()),
                () -> assertNull(actualResult.getBurnRuleContents()[0].getImage()),
                () -> assertEquals(description, actualResult.getDescription()),
                () -> assertNotNull(actualResult.getId()),
                () -> assertEquals(title, actualResult.getTitle()),
                () -> assertEquals(newAmountInTokens, Double.valueOf(actualResult.getAmountInTokens())),
                () -> assertEquals(newAmountInCurrency, actualResult.getAmountInCurrency()),
                () -> assertEquals("None", actualResult.getErrorCode()),
                () -> assertNull(actualResult.getErrorMessage()),
                // FAL-3915
                () -> assertEquals(NEW_PRICE, actualResult.getPrice()),
                // FAL-3871
                () -> assertEquals(ORDER + 1, actualResult.getOrder())
        );
    }

    @ParameterizedTest(name = "Run {index}: title={0}, description={1}, message={2}")
    @MethodSource("burnRuleEditInvalidParams")
    @UserStoryId(storyId = {1559, 3871})
    void shouldNotUpdateBurnRuleWhenConditionTypeIsNotTitle(String title, String description, String message) {
        val value = FakerUtils.title;

        val burnRuleContentEditObj = BurnRuleContentEditRequestModel
                .builder()
                .id(getBurnRuleContentId(burnRuleId))
                .ruleContentType(RULE_CONTENT_TYPE_DESCRIPTION)
                .localization(LOCALIZATION_EN)
                .value(value)
                .build();

        val burnRuleEditObj = BurnRuleEditRequestModel
                .burnRuleEditRequestBuilder()
                .burnRuleContents(new BurnRuleContentEditRequestModel[]{burnRuleContentEditObj})
                .description(description)
                .title(title)
                .id(burnRuleId)
                .amountInTokens(AMOUNT_IN_TOKENS.toString())
                .amountInCurrency(AMOUNT_IN_CURRENCY)
                .vertical(Vertical.HOSPITALITY)
                .build();

        editBurnRule(burnRuleEditObj, burnRuleContentEditObj, burnRuleContentEditObj)
                .then()
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .body(ERROR_MESSAGE, equalTo(message));
    }

    @Test
    @UserStoryId(storyId = {1559, 3871})
    void shouldNotUpdateRuleContentType() {
        val burnRuleContentEditObj = BurnRuleContentEditRequestModel
                .builder()
                .id(getBurnRuleContentId(burnRuleId))
                .ruleContentType(RULE_CONTENT_TYPE_DESCRIPTION)
                .localization(LOCALIZATION_EN)
                .value(value)
                .build();

        val burnRuleEditObj = BurnRuleEditRequestModel
                .burnRuleEditRequestBuilder()
                .burnRuleContents(new BurnRuleContentEditRequestModel[]{burnRuleContentEditObj})
                .description(DESCRIPTION)
                .title(TITLE)
                .amountInTokens(AMOUNT_IN_TOKENS.toString())
                .amountInCurrency(AMOUNT_IN_CURRENCY)
                .id(burnRuleId)
                .vertical(Vertical.HOSPITALITY)
                .build();

        editBurnRule(burnRuleEditObj, burnRuleContentEditObj, burnRuleContentEditObj)
                .then()
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .body(ERROR_MESSAGE, equalTo(TITLE_IN_EN_IS_MANDATORY_MSG))
                .body(MODEL_ERRORS + "." + BURN_RULE_CONTENT_0_FIELD,
                        equalTo(TITLE_IN_EN_IS_MANDATORY_MSG));
    }

    @Test
    @UserStoryId(storyId = {1559, 3871})
    void shouldNotUpdateLocalization() {
        val burnRuleContentEditObj = BurnRuleContentEditRequestModel
                .builder()
                .id(getBurnRuleContentId(burnRuleId))
                .ruleContentType(RULE_CONTENT_TYPE_TITLE)
                .localization(LOCALIZATION_AR)
                .value(value)
                .build();

        val burnRuleContentDescriptionEditObj = BurnRuleContentEditRequestModel
                .builder()
                .id(getBurnRuleContentId(burnRuleId))
                .ruleContentType(RULE_CONTENT_TYPE_DESCRIPTION)
                .localization(LOCALIZATION_AR)
                .value(value)
                .build();

        val burnRuleEditObj = BurnRuleEditRequestModel
                .burnRuleEditRequestBuilder()
                .burnRuleContents(new BurnRuleContentEditRequestModel[]{burnRuleContentEditObj})
                .description(DESCRIPTION)
                .title(TITLE)
                .amountInTokens(AMOUNT_IN_TOKENS.toString())
                .amountInCurrency(AMOUNT_IN_CURRENCY)
                .id(burnRuleId)
                .vertical(Vertical.HOSPITALITY)
                .build();

        editBurnRule(burnRuleEditObj, burnRuleContentEditObj, burnRuleContentDescriptionEditObj)
                .then()
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .body(ERROR_MESSAGE, equalTo(TITLE_IN_EN_IS_MANDATORY_MSG))
                .body(MODEL_ERRORS + "." + BURN_RULE_CONTENT_0_FIELD,
                        equalTo(TITLE_IN_EN_IS_MANDATORY_MSG));
    }

    @Test
    @UserStoryId(storyId = {1559, 3871})
    void contentIdShouldBeValid() {
        val id = UUID.randomUUID().toString();
        val burnRuleContentEditObj = BurnRuleContentEditRequestModel
                .builder()
                .id(id)
                .ruleContentType(RULE_CONTENT_TYPE_TITLE)
                .localization(LOCALIZATION_EN)
                .value(value)
                .build();

        val burnRuleContentDescriptionEditObj = BurnRuleContentEditRequestModel
                .builder()
                .id(id)
                .ruleContentType(RULE_CONTENT_TYPE_DESCRIPTION)
                .localization(LOCALIZATION_EN)
                .value(value)
                .build();

        val burnRuleEditObj = BurnRuleEditRequestModel
                .burnRuleEditRequestBuilder()
                .burnRuleContents(new BurnRuleContentEditRequestModel[]{burnRuleContentEditObj,
                        burnRuleContentDescriptionEditObj})
                .description(DESCRIPTION)
                .title(TITLE)
                .id(burnRuleId)
                .amountInTokens(AMOUNT_IN_TOKENS.toString())
                .amountInCurrency(AMOUNT_IN_CURRENCY)
                .vertical(Vertical.HOSPITALITY)
                .build();

        editBurnRule(burnRuleEditObj, burnRuleContentEditObj, burnRuleContentDescriptionEditObj)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .body(ERROR_CODE, equalTo("EntityNotValid"))
                .body(ERROR_MESSAGE, containsString(String.format(INVALID_ID_MSG, id)));
    }

    @ParameterizedTest(name = "Run {index}: titleValue={0}, descriptionValue={1}, message={2}")
    @MethodSource("valueInvalidParams")
    @UserStoryId(storyId = 1559)
    void lengthOfValueShouldBeValid(String titleValue, String descriptionValue, String message) {

        val burnRuleContent = baseBurnRuleContentTitle
                .value(titleValue)
                .build();

        val burnRuleContentDescriptionEn = BurnRuleContentCreateRequestModel
                .builder()
                .ruleContentType(RULE_CONTENT_TYPE_DESCRIPTION)
                .localization(LOCALIZATION_EN)
                .value(descriptionValue)
                .build();

        val burnRuleContentObject = new BurnRuleContentCreateRequestModel[]{
                burnRuleContent, burnRuleContentDescriptionEn};

        val burnRuleRequest = baseBurnRuleCreateRequest
                .burnRuleContents(burnRuleContentObject)
                .build();

        createBurnRule(burnRuleRequest)
                .then()
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .body(ERROR_MESSAGE, equalTo(message));
    }

    @Test
    @UserStoryId(storyId = 1559)
    void getBurnRuleWhenIdIsNotValid() {
        val id = UUID.randomUUID().toString();
        val actualResult = getBurnRuleById(id);

        System.out.println(actualResult);

        assertAll(
                () -> assertNull(actualResult.getDescription()),
                () -> assertEquals(actualResult.getId(), "00000000-0000-0000-0000-000000000000"),
                () -> assertNull(actualResult.getTitle()),
                () -> assertEquals(actualResult.getErrorCode(), "EntityNotFound"),
                () -> assertEquals(actualResult.getErrorMessage(),
                        String.format(BURN_RULE_DOESNT_EXIST_MSG, id))
        );
    }
}
