package com.lykke.tests.api.service.customermanagement;

import com.lykke.api.testing.annotations.UserStoryId;
import com.lykke.tests.api.base.BaseApiTest;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public class ConfirmEmailTests extends BaseApiTest {

    @Disabled("to be done when email server or db is there")
    @Test
    @UserStoryId(storyId = 654)
    void shouldConfirmEmailWithValidAndActiveVerificationCode() {
        // TODO
    }

    @Disabled("to be done when email server or db is there")
    @Test
    @UserStoryId(storyId = 654)
    void shouldNotConfirmEmailWithValidButExpiredVerificationCode() {
        // TODO
    }

    @Disabled("to be done when email server or db is there")
    @Test
    @UserStoryId(storyId = 654)
    void shouldNotConfirmEmailIfAlreadyVerified() {
        // TODO
    }
}
