package com.lykke.tests.api.common;

import static com.lykke.api.testing.config.SettingsReader.readSettings;
import static com.lykke.tests.api.common.ConfigUtils.getPathToResourceFolder;
import static com.lykke.tests.api.service.customer.LoginLogoutCustomerUtils.getUserToken;

import com.lykke.tests.api.service.customer.LoginLogoutCustomerUtils;
import java.util.function.Function;

public class CommonConsts {

    public static final String API_KEY = "tests-key";
    public static final String ADMIN_API_KEY = "admin-api-key";
    public static final String NOTIFICATION_ADAPTER_API_KEY = "key";//"notification-system-adapter-key"; // Test env
    // Dev env: "key";

    public static final String SMOKE_TEST = "smoke";

    public static final String ERROR_CODE_NONE = "None";
    public static final String ERROR_CODE_FIELD = "ErrorCode";
    public static final String ERROR_FIELD_UCFL = "Error";
    public static final String ERROR_FIELD = "error";
    public static final String MESSAGE_FIELD = "message";
    public static final String ERROR_MESSAGE_FIELD = "ErrorMessage";

    public static final String DEFAULT_CURRENCY = "MVN";
    public static final String BALANCE_FIELD = "Balance";
    public static final String ASSET_SYMBOL_FIELD = "AssetSymbol";
    public static final String REFERRAL_CODE_FIELD = "ReferralCode";
    public static final String EMAIL_FIELD = "Email";

    public static final String INVALID_EMAIL_FORMAT_ERROR_MESSAGE = "The field Email must match the regular "
            + "expression '\\A(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9]"
            + "(?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?)\\Z'.";
    public static final int AWAITILITY_GET_CUSTOMER_ID_SEC = 5;
    public static final int AWAITILITY_DEFAULT_SEC = 20;
    public static final int AWAITILITY_DEFAULT_MIN = 5;
    public static final int AWAITILITY_140_SEC = 140;
    public static final int AWAITILITY_DEFAULT_MID_SEC = 60;
    public static final int AWAITILITY_DEFAULT_MAX_SEC = 60;
    public static final int AWAITILITY_PBF_TRANSFER_BALANCE_SEC = 300;
    public static final int AWAITILITY_INITIAL_BALANCE_WAIT_SECONDS = 300;
    public static final int AWAITILITY_OPERATIONS_HISTORY_SEC = 300;
    public static final int AWAITILITY_WAITING_FOR_EMAIL_MESSAGE_SEC = 60;
    public static final int AWAITILITY_POLL_INTERVAL_SMALL_SEC = 2;
    public static final int AWAITILITY_POLL_INTERVAL_MID_SEC = 5;
    public static final int AWAITILITY_OPERATIONS_HISTORY_STATISTICS_SEC = 20;
    public static final int AWAITILITY_FOR_PARTNER_API_SEC = 60;

    public static final String PERSONAL_DATA_NAMESPACE_VALUE = "personalData";
    public static final String COMMON_INFORMATION_NAMESPACE_VALUE = "mavncommoninfo";
    public static final String PASSWORD_REG_EX = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#\\$%\\^&\\*])(?=.{8,})";
    public static final String EMPTY_PASSWORD_ERR_MSG = "Password is a required field";
    public static final String INVALID_PASSWORD_ERR_MSG = "Password length should be between 8 and 50 characters. "
            + "Password should contain 1 lowercase, 1 uppercase, 1 digits and 1 special symbols. "
            + "Allowed symbols are: abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789 . "
            + "Allowed special symbols are: !@#$%&. Whitespaces are  allowed";
    public static final String INVALID_PASSWORD_ERR = "InvalidPasswordFormat";

    public static final String CUSTOMER_EMAIL_FIELD = "CustomerEmail";
    public static final String RESET_IDENTIFIER_FIELD = "ResetIdentifier";
    public static final String PASSWORD_FIELD = "Password";

    public static final String VALID_PASSWORD = "P@ssword1!";
    public static final String UUID_REGEX = "[0-9a-fA-F]{8}\\-[0-9a-fA-F]{4}\\-[0-9a-fA-F]{4}"
            + "\\-[0-9a-fA-F]{4}\\-[0-9a-fA-F]{12}";
    public static final Function<String, Boolean> IS_GUID =
            (id) -> id.matches("[\\da-f]{8}\\-([\\da-f]{4}\\-){3}[\\da-f]{12}");
    public static final String MODEL_VALIDATION_FAILURE = "ModelValidationFailed";
    public static final String NOTE_VALIDATION_MSG = "Note shouldn't be longer than 2000 characters.";
    public static final String EMAIL_NOT_VERIFIED_ERR_MSG = "Email is not verified";
    public static final String NO_TOKENS_ERR_MSG = "Not enough tokens";
    public static final String ALREADY_REGISTERED_ERR_MSG = "Customer already registered and waiting for KYA completion";
    public static final String IMAGE_IS_REQUIRED_MSG = "Images required";
    public static final String NAME_REQUIRED_MSG = "Name required";
    public static final String NAME_VALIDATION_MSG = "Name shouldn't be longer than 100 characters.";
    public static final String CONTENT_IS_REQUIRED_MSG = "Content required";
    public static final String CONTENT_IS_NOT_BASE64_MSG = "Content is not base64 encoded";
    public static final String IMAGE_SIZE_VALIDATION_MSG = "Image shouldn't be bigger than 10Mb.";
    public static final String IBAN_INVALID_MSG = "IBAN invalid.";
    public static final String SWIFT_INVALID_MSG = "SWIFT invalid.";
    public static final String NON_LATIN_PWD = "QwertyД123 !";
    public static final String WHITE_SPACE_PWD = "Qwerty123 !";

    public static String getFieldItem(String fieldName) {
        return fieldName + "[0]";
    }

    public static final class Currency {

        public static final String AED_CURRENCY = "AED";
        public static final String MVN_CURRENCY = "MVN";
        public static final String USD_CURRENCY = "USD";
        public static final String FAKE_CURRENCY = "FAC";
        public static final Double SOME_CURRENCY_RATE = 3.0;
        public static final int MVN_TO_USD_RATE = 15;
        public static final float SOME_AMOUNT_IN_CURRENCY = 1200;
        public static final Long SOME_AMOUNT_IN_TOKENS = 15000L;
        public static final Long TOKEN_TO_ATTO_RATE = 1_000_000_000_000_000_000L;
    }

    public static final class Location {

        public static final String LOCATION_US = "US";
        public static final String LOCATION_FAKE = "FAKE_LOCATION";
    }

    public static final class SignIn {

        public static final int NUMBER_OF_ATTEMPTS_BEFORE_LOCK = 9;
        public static final int STATUS_CODE_TOO_MANY_ATTEMPTS = 429;
    }

    public static final class Image {

        public static final String IMAGE_URL = readSettings(getPathToResourceFolder()).getEnvironmentSettings()
                .getImage().getUrl();
    }

    public static final class Expiration {

        public static final int DEFAULT_EXPIRATION_INTERVAL_MSEC = 10000;
    }

    public static final class CustomerData {

        public static final int PHONE_NUMBER_LENGTH = 12;
    }

    public static final class Staking {

        public static final int DEFAULT_STAKING_PERIOD = 1;
        public static final int DEFAULT_STAKE_WARNING_PERIOD = 1;
    }

    public static final class Ethereum {

        public static final String WALLET_PRIVATE_KEY = readSettings(getPathToResourceFolder()).getEnvironmentSettings()
                .getEthereum().getWalletPrivateKey();
        public static final String WALLET_PUBLIC_ADDRESS = readSettings(getPathToResourceFolder())
                .getEnvironmentSettings().getEthereum().getWalletPublicAddress();
        public static final String MVN_TRANSIT_ACCOUNT = readSettings(getPathToResourceFolder())
                .getEnvironmentSettings().getEthereum().getTransitAccount();
        public static final String ETH_NODE_ADDRESS = "http://10.92.17.133:8545";
        public static final String ISSUE_ADDRESS = readSettings(getPathToResourceFolder()).getEnvironmentSettings()
                .getEthereum().getTokenIssueAddress();
    }

    public static final class AwaitilityConsts {

        public static final int TIME_TO_MINE_30_BLOCKS_MINS = 12;
    }

    public static final class RegistrationData {

        public static final int COUNTRY_OF_NATIONALITY_ID_01 = 1;
        public static final String COUNTRY_OF_NATIONALITY_NAME_01 = "United Arab Emirates";
    }

    public static final class MVN {
        
        public static final String MVN_GATEWAY_ADDRESS = readSettings(getPathToResourceFolder())
                .getEnvironmentSettings().getEthereum().getMVNVoucherGatewayAddress();
    }

    public static final class CustomerCredentials {

        public static final String USERNAME = "im7lwdetz9n440@example123.com";
        public static final String PASSWORD = "oY3#oY3#oY3#oY3#o";

        public static String getUsetToken() {
            return getUserToken(USERNAME, PASSWORD);
        }
    }
}
