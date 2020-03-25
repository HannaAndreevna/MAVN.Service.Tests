package com.lykke.tests.api.service.customer;

import static com.lykke.tests.api.service.customer.CustomerCommonInformation.getCustomerCommonInfo;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.Matchers.containsString;

import com.lykke.api.testing.annotations.UserStoryId;
import com.lykke.tests.api.base.BaseApiTest;
import org.junit.jupiter.api.Test;

public class CustomerCommonInformationTests extends BaseApiTest {

    private static final String SUPPORT_EMAIL_FIELD = "SupportEmailAddress";
    private static final String DOWNLOAD_APP_FIELD = "DownloadAndroidAppLink";
    private static final String DOWNLOAD_ISO_APP_FIELD = "DownloadIsoAppLink";
    private static final String GOOGLE_PLAY_URL = "https://play.google.com";
    private static final String ITUNES_URL = "itunes.apple.com";
    private static final String TOKENSUPPORT_ADDRESS = "tokensupport@mavn.com";

    @Test
    @UserStoryId(storyId = {3589})
    void shouldGetCustomerCommonInfo() {
        getCustomerCommonInfo()
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .body(DOWNLOAD_APP_FIELD, containsString(GOOGLE_PLAY_URL))
                .body(DOWNLOAD_ISO_APP_FIELD, containsString(ITUNES_URL))
                .body(SUPPORT_EMAIL_FIELD, containsString(TOKENSUPPORT_ADDRESS));
    }
}
