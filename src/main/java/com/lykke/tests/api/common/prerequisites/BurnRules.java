package com.lykke.tests.api.common.prerequisites;

import static com.lykke.tests.api.common.enums.PictureContentType.JPG;
import static com.lykke.tests.api.service.campaigns.BurnRulesUtils.addBurnRuleImage;
import static com.lykke.tests.api.service.campaigns.BurnRulesUtils.getBurnRuleContentIdByContentType;
import static com.lykke.tests.api.service.campaigns.BurnRulesUtils.getBurnRuleId;
import static org.apache.http.HttpStatus.SC_OK;

import com.lykke.api.testing.api.common.Base64Utils;
import com.lykke.api.testing.api.common.FakerUtils;
import com.lykke.tests.api.common.HelperUtils;
import com.lykke.tests.api.service.campaigns.model.burnrules.BurnRuleContentCreateRequestModel;
import com.lykke.tests.api.service.campaigns.model.burnrules.BurnRuleCreateRequestModel;
import com.lykke.tests.api.service.campaigns.model.burnrules.FileCreateRequestModel;
import com.lykke.tests.api.service.campaigns.model.burnrules.Localization;
import com.lykke.tests.api.service.campaigns.model.burnrules.RuleContentType;
import com.lykke.tests.api.service.campaigns.model.burnrules.Vertical;
import lombok.val;

public class BurnRules {

    private static final RuleContentType RULE_CONTENT_TYPE_TITLE = RuleContentType.TITLE;
    private static final RuleContentType RULE_CONTENT_TYPE_DESCRIPTION = RuleContentType.DESCRIPTION;
    private static final RuleContentType RULE_CONTENT_TYPE_URL_FOR_PICTURE = RuleContentType.URL_FOR_PICTURE;
    private static final Localization LOCALIZATION_EN = Localization.EN;
    private static final Localization LOCALIZATION_AR = Localization.AR;
    private static final String BURN_RULE_TITLE = FakerUtils.title;
    private static final Double AMOUNT_IN_TOKENS = 12.0;
    private static final int AMOUNT_IN_CURRENCY = 21;
    private static final String BURN_RULE_CONTENT_TITLE_EN_VALUE = "ENGLISH TITLE";
    private static final String BURN_RULE_CONTENT_TITLE_AR_VALUE = "ARABIC TITLE";
    private static final String BURN_RULE_CONTENT_DESCRIPTION_EN_VALUE = "ENGLISH DESCRIPTION";
    private static final String BURN_RULE_CONTENT_DESCRIPTION_AR_VALUE = "ARABIC DESCRIPTION";
    static BurnRuleContentCreateRequestModel burnRuleContentTitleEn = BurnRuleContentCreateRequestModel
            .builder()
            .ruleContentType(RULE_CONTENT_TYPE_TITLE)
            .localization(LOCALIZATION_EN)
            .value(BURN_RULE_CONTENT_TITLE_EN_VALUE)
            .build();
    static BurnRuleContentCreateRequestModel burnRuleContentTitleAr = BurnRuleContentCreateRequestModel
            .builder()
            .ruleContentType(RULE_CONTENT_TYPE_TITLE)
            .localization(LOCALIZATION_AR)
            .value(BURN_RULE_CONTENT_TITLE_AR_VALUE)
            .build();
    static BurnRuleContentCreateRequestModel burnRuleContentDescriptionEn = BurnRuleContentCreateRequestModel
            .builder()
            .ruleContentType(RULE_CONTENT_TYPE_DESCRIPTION)
            .localization(LOCALIZATION_EN)
            .value(BURN_RULE_CONTENT_DESCRIPTION_EN_VALUE)
            .build();
    static BurnRuleContentCreateRequestModel burnRuleContentDescriptionAr = BurnRuleContentCreateRequestModel
            .builder()
            .ruleContentType(RULE_CONTENT_TYPE_DESCRIPTION)
            .localization(LOCALIZATION_AR)
            .value(BURN_RULE_CONTENT_DESCRIPTION_AR_VALUE)
            .build();
    static BurnRuleContentCreateRequestModel burnRuleContentUrlForPictureEn = BurnRuleContentCreateRequestModel
            .builder()
            .ruleContentType(RULE_CONTENT_TYPE_URL_FOR_PICTURE)
            .localization(LOCALIZATION_EN)
            .value("")
            .build();
    static BurnRuleContentCreateRequestModel burnRuleContentUrlForPictureAr = BurnRuleContentCreateRequestModel
            .builder()
            .ruleContentType(RULE_CONTENT_TYPE_URL_FOR_PICTURE)
            .localization(LOCALIZATION_AR)
            .value("")
            .build();
    private static String content = Base64Utils.encodeToString(HelperUtils.getImagePath("test_image.jpg"));

    public static String createBurnRuleWithAllContentTypes(Boolean image) {
        String burnRuleId;

        val burnRuleContentObject = new BurnRuleContentCreateRequestModel[]{
                burnRuleContentTitleEn, burnRuleContentTitleAr, burnRuleContentDescriptionEn,
                burnRuleContentDescriptionAr, burnRuleContentUrlForPictureEn, burnRuleContentUrlForPictureAr
        };

        val burnRuleCreateRequest = BurnRuleCreateRequestModel
                .burnRuleCreateRequestBuilder()
                .createdBy(FakerUtils.fullName)
                .burnRuleContents(burnRuleContentObject)
                .title(BURN_RULE_TITLE)
                .description(FakerUtils.title)
                .amountInTokens(AMOUNT_IN_TOKENS.toString())
                .amountInCurrency(AMOUNT_IN_CURRENCY)
                .vertical(Vertical.HOSPITALITY)
                .price(20.0)
                .order(123)
                .build();

        burnRuleId = getBurnRuleId(burnRuleCreateRequest);

        if (image) {
            addImage(burnRuleId, LOCALIZATION_EN);
            addImage(burnRuleId, LOCALIZATION_AR);
        }

        return burnRuleId;
    }

    public static String createBurnRuleWithENContents(Boolean image) {
        return createBurnRuleWithENContents(image, Vertical.HOSPITALITY);
    }

    public static String createBurnRuleWithENContents(Boolean image, Vertical vertical) {
        String burnRuleId;

        val burnRuleContentObject = new BurnRuleContentCreateRequestModel[]{
                burnRuleContentTitleEn, burnRuleContentDescriptionEn, burnRuleContentUrlForPictureEn
        };

        val burnRuleCreateRequest = BurnRuleCreateRequestModel
                .burnRuleCreateRequestBuilder()
                .createdBy(FakerUtils.fullName)
                .burnRuleContents(burnRuleContentObject)
                .title(BURN_RULE_TITLE)
                .description(FakerUtils.title)
                .amountInTokens(AMOUNT_IN_TOKENS.toString())
                .amountInCurrency(AMOUNT_IN_CURRENCY)
                .vertical(vertical)
                .build();

        burnRuleId = getBurnRuleId(burnRuleCreateRequest);

        if (image) {
            addImage(burnRuleId, LOCALIZATION_EN);
        }

        return burnRuleId;
    }

    private static void addImage(String burnRuleId,
            Localization localization) {
        val type = JPG.getValue();

        val fileCreateRequestObj = FileCreateRequestModel
                .builder()
                .ruleContentId(getBurnRuleContentIdByContentType(
                        burnRuleId, RULE_CONTENT_TYPE_URL_FOR_PICTURE, localization))
                .name(FakerUtils.title)
                .type(type)
                .content(content)
                .build();

        addBurnRuleImage(fileCreateRequestObj)
                .then()
                .statusCode(SC_OK);
    }
}
