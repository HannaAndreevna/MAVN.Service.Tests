package com.lykke.tests.api.service.campaigns.burnrules;

import static com.lykke.tests.api.service.campaigns.BurnRulesUtils.addBurnRuleImage;
import static com.lykke.tests.api.service.campaigns.BurnRulesUtils.editBurnRuleImage;
import static com.lykke.tests.api.service.campaigns.BurnRulesUtils.getBurnRuleById;
import static com.lykke.tests.api.service.campaigns.BurnRulesUtils.getBurnRuleContentIdByContentType;
import static com.lykke.tests.api.service.campaigns.BurnRulesUtils.getBurnRuleId;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.lykke.api.testing.annotations.UserStoryId;
import com.lykke.api.testing.api.common.Base64Utils;
import com.lykke.api.testing.api.common.FakerUtils;
import com.lykke.tests.api.base.BaseApiTest;
import com.lykke.tests.api.common.HelperUtils;
import com.lykke.tests.api.common.enums.PictureContentType;
import com.lykke.tests.api.service.campaigns.model.burnrules.BurnRuleContentCreateRequestModel;
import com.lykke.tests.api.service.campaigns.model.burnrules.BurnRuleCreateRequestModel;
import com.lykke.tests.api.service.campaigns.model.burnrules.BurnRuleResponseModel;
import com.lykke.tests.api.service.campaigns.model.burnrules.FileCreateRequestModel;
import com.lykke.tests.api.service.campaigns.model.burnrules.FileEditRequestModel;
import com.lykke.tests.api.service.campaigns.model.burnrules.Localization;
import com.lykke.tests.api.service.campaigns.model.burnrules.RuleContentType;
import com.lykke.tests.api.service.campaigns.model.burnrules.Vertical;
import java.util.UUID;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class BurnRulesImagesTests extends BaseApiTest {

    private static final String DESCRIPTION = FakerUtils.randomQuote;
    private static final String TITLE = FakerUtils.title;
    private static final RuleContentType RULE_CONTENT_TYPE_TITLE = RuleContentType.TITLE;
    private static final RuleContentType RULE_CONTENT_TYPE_URL_FOR_PICTURE = RuleContentType.URL_FOR_PICTURE;
    private static final RuleContentType RULE_CONTENT_TYPE_DESCRIPTION = RuleContentType.DESCRIPTION;
    private static final Localization LOCALIZATION_EN = Localization.EN;
    private static final String CREATED_BY = FakerUtils.fullName;
    private static final Double AMOUNT_IN_TOKENS = 10.0;
    private static final int AMOUNT_IN_CURRENCY = 20;
    private static final String ERROR_MESSAGE = "ErrorMessage";
    private static final String MODEL_ERRORS = "ModelErrors";
    private static final String ERROR_CODE = "ErrorCode";
    private static final String VALUE = FakerUtils.title;
    private static final int ORDER = 31;
    private static String burnRuleId;

    private static String content = Base64Utils.encodeToString(HelperUtils.getImagePath("test_image.jpg"));
    private static String newContent = Base64Utils.encodeToString(HelperUtils.getImagePath("test_image.png"));
    BurnRuleContentCreateRequestModel burnRuleTitleContent;
    BurnRuleContentCreateRequestModel burnRuleUrlForPictureContent;
    BurnRuleContentCreateRequestModel burnRuleDescriptionContent;
    BurnRuleCreateRequestModel burnRuleCreateRequest;
    BurnRuleContentCreateRequestModel.BurnRuleContentCreateRequestModelBuilder baseBurnRuleTitleContent;
    BurnRuleContentCreateRequestModel.BurnRuleContentCreateRequestModelBuilder baseBurnRuleUrlForPictureContent;
    BurnRuleContentCreateRequestModel.BurnRuleContentCreateRequestModelBuilder baseBurnRuleDescriptionContent;
    BurnRuleCreateRequestModel.BurnRuleCreateRequestModelBuilder baseBurnRuleCreateRequest;

    @BeforeEach
    void setup() {
        baseBurnRuleTitleContent = BurnRuleContentCreateRequestModel
                .builder()
                .ruleContentType(RULE_CONTENT_TYPE_TITLE)
                .localization(LOCALIZATION_EN)
                .value(VALUE);
        baseBurnRuleUrlForPictureContent = BurnRuleContentCreateRequestModel
                .builder()
                .ruleContentType(RULE_CONTENT_TYPE_URL_FOR_PICTURE)
                .localization(LOCALIZATION_EN)
                .value(EMPTY);
        baseBurnRuleDescriptionContent = BurnRuleContentCreateRequestModel
                .builder()
                .ruleContentType(RULE_CONTENT_TYPE_DESCRIPTION)
                .localization(LOCALIZATION_EN)
                .value(VALUE);

        burnRuleTitleContent = baseBurnRuleTitleContent.build();
        burnRuleUrlForPictureContent = baseBurnRuleUrlForPictureContent.build();
        burnRuleDescriptionContent = baseBurnRuleDescriptionContent.build();

        baseBurnRuleCreateRequest = BurnRuleCreateRequestModel
                .burnRuleCreateRequestBuilder()
                .createdBy(CREATED_BY)
                .burnRuleContents(new BurnRuleContentCreateRequestModel[]{
                        burnRuleTitleContent, burnRuleUrlForPictureContent, burnRuleDescriptionContent
                })
                .amountInTokens(AMOUNT_IN_TOKENS.toString())
                .amountInCurrency(AMOUNT_IN_CURRENCY)
                .vertical(Vertical.HOSPITALITY)
                .title(TITLE)
                .description(DESCRIPTION)
                .order(ORDER);

        burnRuleCreateRequest = baseBurnRuleCreateRequest
                .build();

        burnRuleId = getBurnRuleId(burnRuleCreateRequest);
    }

    @Test
    @UserStoryId(storyId = {1559, 3871})
    void shouldUploadImage() {
        val type = PictureContentType.PNG.getValue();

        val fileCreateRequestObj = FileCreateRequestModel
                .builder()
                .ruleContentId(getBurnRuleContentIdByContentType(
                        burnRuleId, RULE_CONTENT_TYPE_URL_FOR_PICTURE, LOCALIZATION_EN))
                .name(FakerUtils.title)
                .type(type)
                .content(content)
                .build();

        addBurnRuleImage(fileCreateRequestObj)
                .then()
                .statusCode(SC_OK)
                .body(ERROR_CODE, equalTo("None"))
                .body(ERROR_MESSAGE, nullValue());

        val actualResult = getBurnRuleById(burnRuleId);

        assertAll(
                () -> assertEquals(type, actualResult
                        .getBurnRuleContents()[getUrlForPictureContentId(actualResult)].getImage().getType()),
                () -> assertNotNull(actualResult
                        .getBurnRuleContents()[getUrlForPictureContentId(actualResult)].getImage().getBlobUrl()),
                () -> assertNotNull(actualResult
                        .getBurnRuleContents()[getUrlForPictureContentId(actualResult)].getImage().getName()),
                // FAL-3871
                () -> assertEquals(ORDER, actualResult.getOrder())
        );
    }

    @Test
    @UserStoryId(storyId = {1559, 3871})
    void ruleContentTypeMustBeValid() {
        val invalid_content_type = "image/gif";
        val fileCreateRequestObj = FileCreateRequestModel
                .builder()
                .ruleContentId(getBurnRuleContentIdByContentType(
                        burnRuleId, RULE_CONTENT_TYPE_URL_FOR_PICTURE, LOCALIZATION_EN))
                .name(FakerUtils.title)
                .type(invalid_content_type)
                .content(content)
                .build();

        addBurnRuleImage(fileCreateRequestObj)
                .then()
                .statusCode(SC_OK)
                .body(ERROR_CODE, equalTo("NotValidFileFormat"))
                .body(ERROR_MESSAGE, equalTo(String.format("%s is not supported file type", invalid_content_type)));
    }

    @Test
    @UserStoryId(storyId = {1559, 3871})
    void ruleContentTypeCannotBeEmpty() {
        val fileCreateRequestObj = FileCreateRequestModel
                .builder()
                .ruleContentId(getBurnRuleContentIdByContentType(
                        burnRuleId, RULE_CONTENT_TYPE_URL_FOR_PICTURE, LOCALIZATION_EN))
                .name(FakerUtils.title)
                .type(EMPTY)
                .content(content)
                .build();

        addBurnRuleImage(fileCreateRequestObj)
                .then()
                .statusCode(SC_BAD_REQUEST)
                .body("ModelErrors.Type[0]", equalTo("'Type' must not be empty."))
                .body(ERROR_MESSAGE, equalTo("'Type' must not be empty."));
    }

    @Test
    @UserStoryId(storyId = {1559, 3871})
    void ruleContentTypeCannotBeNull() {
        val fileCreateRequestObj = FileCreateRequestModel
                .builder()
                .ruleContentId(getBurnRuleContentIdByContentType(
                        burnRuleId, RULE_CONTENT_TYPE_URL_FOR_PICTURE, LOCALIZATION_EN))
                .name(FakerUtils.title)
                .content(content)
                .build();

        addBurnRuleImage(fileCreateRequestObj)
                .then()
                .statusCode(SC_BAD_REQUEST)
                .body("ModelErrors.Type[0]", equalTo("'Type' must not be empty."))
                .body(ERROR_MESSAGE, equalTo("'Type' must not be empty."));
    }

    @Test
    @UserStoryId(storyId = {1559, 3871})
    void ruleContentCannotBeEmpty() {
        val fileCreateRequestObj = FileCreateRequestModel
                .builder()
                .ruleContentId(getBurnRuleContentIdByContentType(
                        burnRuleId, RULE_CONTENT_TYPE_URL_FOR_PICTURE, LOCALIZATION_EN))
                .name(FakerUtils.title)
                .type(PictureContentType.JPEG.getValue())
                .content(EMPTY)
                .build();

        addBurnRuleImage(fileCreateRequestObj)
                .then()
                .statusCode(SC_BAD_REQUEST)
                .body("ModelErrors.Content[0]", equalTo("'Content' must not be empty."))
                .body(ERROR_MESSAGE, equalTo("'Content' must not be empty."));
    }

    @Test
    @UserStoryId(storyId = 1559)
    void ruleContentCannotBeNull() {
        val fileCreateRequestObj = FileCreateRequestModel
                .builder()
                .ruleContentId(getBurnRuleContentIdByContentType(
                        burnRuleId, RULE_CONTENT_TYPE_URL_FOR_PICTURE, LOCALIZATION_EN))
                .name(FakerUtils.title)
                .type(PictureContentType.JPEG.getValue())
                .build();

        addBurnRuleImage(fileCreateRequestObj)
                .then()
                .statusCode(SC_BAD_REQUEST)
                .body("ModelErrors.Content[0]", equalTo("'Content' must not be empty."))
                .body(ERROR_MESSAGE, equalTo("'Content' must not be empty."));
    }

    @Test
    @UserStoryId(storyId = {1559, 3871})
    void shouldNotUploadImageWhenContentIdIsNotUrlForPictureId() {
        val contentTitleId = getBurnRuleContentIdByContentType(
                burnRuleId, RULE_CONTENT_TYPE_TITLE, LOCALIZATION_EN);
        val fileCreateRequestObj = FileCreateRequestModel
                .builder()
                .ruleContentId(contentTitleId)
                .name(FakerUtils.title)
                .type(PictureContentType.JPEG.getValue())
                .content(content)
                .build();

        addBurnRuleImage(fileCreateRequestObj)
                .then()
                .statusCode(SC_OK)
                .body(ERROR_CODE, equalTo("NotValidRuleContentType"))
                .body(ERROR_MESSAGE,
                        equalTo(String.format("Burn Content type with id %s is not image type", contentTitleId)));
    }

    @Test
    @UserStoryId(storyId = {1559, 3871})
    void shouldNotUploadImageWhenContentIdIsNotValid() {
        val contentId = UUID.randomUUID().toString();
        val fileCreateRequestObj = FileCreateRequestModel
                .builder()
                .ruleContentId(contentId)
                .name(FakerUtils.title)
                .type(PictureContentType.JPEG.getValue())
                .content(content)
                .build();

        addBurnRuleImage(fileCreateRequestObj)
                .then()
                .statusCode(SC_OK)
                .body(ERROR_CODE, equalTo("EntityNotFound"))
                .body(ERROR_MESSAGE, equalTo(String.format("Burn Content type with id %s does not exist.", contentId)));
    }

    @Test
    @UserStoryId(storyId = {1559, 3871})
    void shouldEditBurnRuleImage() {
        val type = PictureContentType.PNG.getValue();
        val newType = PictureContentType.JPG.getValue();

        val fileCreateRequestObj = FileCreateRequestModel
                .builder()
                .ruleContentId(getBurnRuleContentIdByContentType(
                        burnRuleId, RULE_CONTENT_TYPE_URL_FOR_PICTURE, LOCALIZATION_EN))
                .name(FakerUtils.title)
                .type(type)
                .content(content)
                .build();

        addBurnRuleImage(fileCreateRequestObj)
                .then()
                .statusCode(SC_OK)
                .body(ERROR_CODE, equalTo("None"))
                .body(ERROR_MESSAGE, nullValue());

        val actualResult = getBurnRuleById(burnRuleId);
        val name = actualResult.getBurnRuleContents()[getUrlForPictureContentId(actualResult)].getImage().getName();
        val blobUrl = actualResult.getBurnRuleContents()[getUrlForPictureContentId(actualResult)].getImage()
                .getBlobUrl();

        assertAll(
                () -> assertEquals(type, actualResult
                        .getBurnRuleContents()[getUrlForPictureContentId(actualResult)].getImage().getType())
        );

        val fileEditRequestObj = FileEditRequestModel
                .builder()
                .id(burnRuleId)
                .ruleContentId(getBurnRuleContentIdByContentType(
                        burnRuleId, RULE_CONTENT_TYPE_URL_FOR_PICTURE, LOCALIZATION_EN))
                .name(FakerUtils.title)
                .type(newType)
                .content(newContent)
                .build();

        editBurnRuleImage(fileEditRequestObj)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .body(ERROR_CODE, equalTo("None"))
                .body(ERROR_MESSAGE, nullValue());

        val result = getBurnRuleById(burnRuleId);

        assertAll(
                () -> assertEquals(newType, result
                        .getBurnRuleContents()[getUrlForPictureContentId(actualResult)].getImage().getType()),
                //TODO: Check if there is a better way for asserting that the picture is edited.
                () -> assertNotEquals(name, result
                        .getBurnRuleContents()[getUrlForPictureContentId(actualResult)].getImage().getName()),
                () -> assertNotEquals(blobUrl, result
                        .getBurnRuleContents()[getUrlForPictureContentId(actualResult)].getImage().getBlobUrl()),
                // FAL-3871
                () -> assertEquals(ORDER, actualResult.getOrder())
        );
    }

    private int getUrlForPictureContentId(BurnRuleResponseModel burnRuleResponseModel) {
        int index = 0;
        for (int i = 0; i < burnRuleResponseModel.getBurnRuleContents().length; i++) {
            if (burnRuleResponseModel.getBurnRuleContents()[i]
                    .getRuleContentType().equalsIgnoreCase(RULE_CONTENT_TYPE_URL_FOR_PICTURE.getCode())) {
                index = i;
            }
        }

        return index;
    }
}
