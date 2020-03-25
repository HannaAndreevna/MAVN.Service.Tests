package com.lykke.tests.selftest.config;

import static com.lykke.tests.selftest.config.ExampleSettingsReaderTest.LocalSettingsConsts.BACKEND_PASSWORD;
import static com.lykke.tests.selftest.config.ExampleSettingsReaderTest.LocalSettingsConsts.FRONTEND_PASSWORD;
import static com.lykke.tests.selftest.config.ExampleSettingsReaderTest.LocalSettingsConsts.SPECIFIC_SETTING_01;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.lykke.api.testing.config.environment.EnvironmentCode;
import com.lykke.tests.api.common.ConfigUtils;
import lombok.var;
import org.junit.jupiter.api.Test;

public class ExampleSettingsReaderTest {

    public static final String ENVIRONMENT_FILE_LOCAL = EnvironmentCode.DEV.getCode();
    public static final boolean IS_DEBUG_MODE = false;

    @Test
    void readConfig() {
        // Act
        var config = ConfigUtils.getSettings();

        // Assert
        assertAll(
                () -> {
                    assertEquals(ENVIRONMENT_FILE_LOCAL, config.getEnvironment().getCode());
                },
                () -> {
                    assertEquals(IS_DEBUG_MODE, config.getTestSettings().isJooqDebugMode());
                },
                () -> {
                    assertEquals(LocalSettingsConsts.SCREENSHOT_FOLDER, config.getTestSettings().getScreenshotFolder());
                },
                () -> {
                    assertEquals(LocalSettingsConsts.WEB_DRIVER_TIMEOUT,
                            config.getTestSettings().getWebDriverTimeout());
                },
                () -> {
                    assertEquals(LocalSettingsConsts.CHROME_DRIVER_PATH,
                            config.getTestSettings().getChromeDriverPath());
                }
        );
    }

    @Test
    void readEnvironmentConfig() {
        // Act
        var config = ConfigUtils.getSettings();

        // Assert
        assertAll(
                () -> {
                    assertEquals(LocalSettingsConsts.BACKEND_URL,
                            config.getEnvironmentSettings().getBackend().getUrl());
                },
                () -> {
                    assertEquals(LocalSettingsConsts.BACKEND_USERNAME,
                            config.getEnvironmentSettings().getBackend().getUsername());
                },
                () -> {
                    assertEquals(BACKEND_PASSWORD, config.getEnvironmentSettings().getBackend().getPassword());
                },
                () -> {
                    assertEquals(LocalSettingsConsts.FRONTEND_URL,
                            config.getEnvironmentSettings().getFrontend().getUrl());
                },
                () -> {
                    assertEquals(LocalSettingsConsts.FRONTEND_USERNAME,
                            config.getEnvironmentSettings().getFrontend().getUsername());
                },
                () -> {
                    assertEquals(FRONTEND_PASSWORD, config.getEnvironmentSettings().getFrontend().getPassword());
                },
                () -> {
                    assertEquals(SPECIFIC_SETTING_01,
                            config.getEnvironmentSettings().getSettings().isBooleanSetting1());
                }
        );
    }

    class LocalSettingsConsts {

        public static final String BACKEND_URL = ".svc.cluster.local";
        public static final String BACKEND_USERNAME = "string@mymail.bg";
        public static final String BACKEND_PASSWORD = "Pass123$";
        public static final String FRONTEND_URL = "https://backoffice.falcon-dev.open-source.exchange";
        public static final String FRONTEND_USERNAME = "string@mymail.bg";
        public static final String FRONTEND_PASSWORD = "Pass123$";
        public static final boolean SPECIFIC_SETTING_01 = true;
        public static final String SCREENSHOT_FOLDER = "./screenshot/output/";
        public static final int WEB_DRIVER_TIMEOUT = 5000;
        public static final String CHROME_DRIVER_PATH = "/Users/chromedriver";
    }
}
