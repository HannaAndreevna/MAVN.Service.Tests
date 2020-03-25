package com.lykke.tests.api.common;

public class ResourcesConsts {

    public static class CustomerApi {

        public static final String COUNTRIES_OF_RESIDENCE_JSON_FILE = "countriesOfResidence.json";
        public static final String COUNTRY_PHONE_CODES_JSON_FILE = "countryPhoneCodes.json";
    }

    public static class Dictionaries {

        public static final String COUNTRIES_OF_RESIDENCE_JSON_FILE = CustomerApi.COUNTRIES_OF_RESIDENCE_JSON_FILE;
        public static final String COUNTRY_PHONE_CODES_JSON_FILE = "countryPhoneCodesDictionary.json";
        public static final String COMMON_INFORMATIOMN_JSON_FILE = "commonInformation.json";
    }

    public static class Campaigns {

        public static final String BONUS_TYPES = "bonusTypes.json";
        public static final String ACTIVE_CONDIITONS = "activeConditions.json";
    }

    public static class Admins {

        public static final String AUTOFILL_DATA = "autofillData.json";
        public static final String AUTOFILL_VALUES = "autofillValues.json";
    }
}
