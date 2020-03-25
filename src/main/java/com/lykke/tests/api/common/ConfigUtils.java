package com.lykke.tests.api.common;

import static com.lykke.api.testing.config.ConfigConsts.CENTRAL_CONFIG_FILE_NAME;
import static com.lykke.api.testing.config.ConfigConsts.YAML_EXTENSION;
import static com.lykke.api.testing.config.SettingsReader.readSettings;

import com.lykke.api.testing.config.Settings;
import java.io.File;
import lombok.experimental.UtilityClass;
import lombok.val;

@UtilityClass
public class ConfigUtils {

    public static String getPathToResourceFolder() {
        val file = new File(
                ConfigUtils.class.getClassLoader().getResource(CENTRAL_CONFIG_FILE_NAME + YAML_EXTENSION).getFile());
        return file.getAbsoluteFile().getParent();
    }

    public static String getBackendUsername() {
        return readSettings(getPathToResourceFolder()).getEnvironmentSettings().getBackend().getUsername();
    }

    public static String getBackendPassword() {
        return readSettings(getPathToResourceFolder()).getEnvironmentSettings().getBackend().getPassword();
    }

    public static String getBackendUrl() {
        return readSettings(getPathToResourceFolder()).getEnvironmentSettings().getBackend().getUrl();
    }

    public static String getFrontendUrl() {
        return readSettings(getPathToResourceFolder()).getEnvironmentSettings().getFrontend().getUrl();
    }

    public static Settings getSettings() {
        return readSettings(getPathToResourceFolder());
    }
}
