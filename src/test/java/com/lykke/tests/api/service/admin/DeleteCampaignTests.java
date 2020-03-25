package com.lykke.tests.api.service.admin;

import static com.lykke.api.testing.api.common.GenerateUtils.generateRandomString;
import static com.lykke.tests.api.service.admin.CampaignUtils.createCampaignAndReturnId;
import static com.lykke.tests.api.service.admin.CampaignUtils.deleteCampaign;
import static com.lykke.tests.api.service.campaigns.CampaignUtils.getCampaignById;
import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.apache.http.HttpStatus.SC_NOT_FOUND;
import static org.apache.http.HttpStatus.SC_NO_CONTENT;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import com.lykke.api.testing.annotations.UserStoryId;
import com.lykke.tests.api.base.BaseApiTest;
import com.lykke.tests.api.common.model.LykkeApiErrorCode;
import java.util.UUID;

import lombok.val;
import org.junit.jupiter.api.Test;

public class DeleteCampaignTests extends BaseApiTest {

    private static final String ERROR_CODE = "GuidCanNotBeParsed";
    private static final String ERROR_MESSAGE = "Invalid Identifier";

    @Test
    @UserStoryId(storyId = { 940, 942 })
    void shouldDeleteCampaignById() {
        val campaignId = createCampaignAndReturnId();

        deleteCampaign(campaignId)
                .then()
                .assertThat()
                .statusCode(SC_NO_CONTENT);

        getCampaignById(campaignId)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .body("ErrorCode", equalTo("EntityNotFound"));
    }

    @Test
    @UserStoryId(storyId = { 940, 942 })
    void shouldNotDeleteCampaignByNonExistingId() {
        val campaignId = UUID.randomUUID().toString();

        deleteCampaign(campaignId)
                .then()
                .assertThat()
                .statusCode(SC_NO_CONTENT);
    }

    @Test
    @UserStoryId(storyId = { 940, 942 })
    void shouldNotDeleteCampaignByInvalidId() {
        val campaignId = generateRandomString(10);
        val expectedResponse = LykkeApiErrorCode
                .builder()
                .error(ERROR_CODE)
                .message(ERROR_MESSAGE)
                .build();

        val actualResponse = deleteCampaign(campaignId)
                .then()
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .extract()
                .as(LykkeApiErrorCode.class);

        assertThat(expectedResponse, equalTo(actualResponse));
    }
}
