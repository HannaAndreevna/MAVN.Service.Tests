package com.lykke.tests.api.service.referral.model.referralleadmodel;

import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.apache.http.HttpStatus.SC_OK;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.val;

@AllArgsConstructor
@Builder
@Data
public class ReferralLeadsCreateRequest {

    private static final String FIRST_NAME_IS_REQUIRED_MESSAGE = "First name is required.";
    private static final String FIRST_NAME_LENGTH_VALIDATION_MESSAGE = "First name length should be in between 3 and"
            + " 100 characters long.";
    private static final String FIRST_NAME_VALIDATION_MESSAGE = "First name field can contains only letters, periods,"
            + " hyphens and single quotes.";
    private static final String LAST_NAME_IS_REQUIRED_MESSAGE = "Last name is required.";
    private static final String LAST_NAME_LENGTH_VALIDATION_MESSAGE = "Last name length should be in between 3"
            + " and 100 characters long.";
    private static final String LAST_NAME_VALIDATION_MESSAGE = "Last name field can contains only letters, periods,"
            + " hyphens and single quotes.";
    private static final String NOTE_LENGTH_VALIDATION_MESSAGE = "Note length should be in between 2 and 1000"
            + " characters long.";
    private static final String EMAIL_IS_REQUIRED_MESSAGE = "Email is required.";
    private static final String EMAIL_SHOULD_BE_VALID_MESSAGE = "Email should be a valid email address.";
    private static final String CUSTOMER_ID_IS_REQUIRED_MESSAGE = "Customer id is required.";
    private static final String PHONE_NUMBER_IS_REQUIRED_MESSAGE = "Phone Number is required.";
    private static final String COUNTRY_CODE_REQUIRED_MESSAGE = "Country code is required.";
    private static final String COUNTRY_NAME_REQUIRED_MESSAGE = "Country name is required.";
    private static final String PHONE_COUNTRY_CODE_ID_VALID_MESSAGE = "Phone country code id should be greater than 0.";
    private static final String PHONE_COUNTRY_CODE_ID_NOT_EXISTS_MESSAGE = "Country code does not exist.";
    private static final String PHONE_COUNTRY_CODE_ID_NOT_EXISTS_ERR = "CountryCodeDoesNotExist";
    private static final String PHONE_NUMBER_LENGTH_VALIDATION_MESSAGE = "Phone number length should be in between 2"
            + " and 50 characters long.";
    private static String nameRegEx = "^((?![!@#$%^&*()_+{}|:\\\"?></,;\\[\\]=~]).)+$";
    private String firstName;
    private String lastName;
    private String countryCode;
    private String countryName;
    private int phoneCountryCodeId;
    private String phoneNumber;
    private String email;
    private String note;
    private String customerId;

    public int getHttpStatus() {
        return isFirstNameValid() && isLastNameValid() && isCountryCodeValid() && isCountryNameValid()
                && isPhoneNumberValid() && isNoteValid() && isEmailValid()
                && isCustomerIdValid()
                ? SC_OK
                : SC_BAD_REQUEST;
    }

    public ValidationErrorLeadResponse getValidationResponse() {
        val response = new ValidationErrorLeadResponse();
        response.getModelErrors()
                .setFirstName(isFirstNameValid() ? null : getFirstNameValidationErrorMessage());
        response.getModelErrors()
                .setLastName(isLastNameValid() ? null : getLastNameValidationErrorMessage());
        response.getModelErrors()
                .setCountryCode(isCountryCodeValid() ? null : new String[]{COUNTRY_CODE_REQUIRED_MESSAGE});
        response.getModelErrors()
                .setCountryName(isCountryNameValid() ? null : new String[]{COUNTRY_NAME_REQUIRED_MESSAGE});
        response.getModelErrors()
                .setPhoneNumber(isPhoneNumberValid() ? null : getPhoneNumberValidationErrorMessage());
        response.getModelErrors().setEmail(isEmailValid() ? null : getEmailValidationErrorMessage());
        response.getModelErrors().setNote(isNoteValid() ? null : new String[]{NOTE_LENGTH_VALIDATION_MESSAGE});
        response.getModelErrors()
                .setCustomerId(isCustomerIdValid() ? null : new String[]{CUSTOMER_ID_IS_REQUIRED_MESSAGE});
        response.setErrorMessage(getErrorMessage());
        return response;
    }

    private String getErrorMessage() {
        String errorMessage;
        if (!isFirstNameValid() && firstName.length() == 0) {
            errorMessage = FIRST_NAME_IS_REQUIRED_MESSAGE;
        } else if (!isFirstNameValid() && firstName.length() > 1 && firstName.matches(nameRegEx)) {
            errorMessage = FIRST_NAME_LENGTH_VALIDATION_MESSAGE;
        } else if (!isFirstNameValid() && !firstName.matches(nameRegEx)) {
            errorMessage = FIRST_NAME_VALIDATION_MESSAGE;
        } else if (!isLastNameValid() && lastName.length() == 0) {
            errorMessage = LAST_NAME_IS_REQUIRED_MESSAGE;
        } else if (!isLastNameValid() && lastName.length() > 0 && lastName.matches(nameRegEx)) {
            errorMessage = LAST_NAME_LENGTH_VALIDATION_MESSAGE;
        } else if (!isLastNameValid() && !lastName.matches(nameRegEx)) {
            errorMessage = LAST_NAME_VALIDATION_MESSAGE;
        } else if (!isPhoneNumberValid() && phoneNumber.length() == 0) {
            errorMessage = PHONE_NUMBER_IS_REQUIRED_MESSAGE;
        } else if (!isCountryCodeValid()) {
            errorMessage = COUNTRY_CODE_REQUIRED_MESSAGE;
        } else if (!isCountryNameValid()) {
            errorMessage = COUNTRY_NAME_REQUIRED_MESSAGE;
        } else if (!isPhoneNumberValid() && phoneNumber.length() > 0) {
            errorMessage = PHONE_NUMBER_LENGTH_VALIDATION_MESSAGE;
        } else if (!isEmailValid() && email.length() == 0) {
            errorMessage = EMAIL_IS_REQUIRED_MESSAGE;
        } else if (!isEmailValid()) {
            errorMessage = EMAIL_SHOULD_BE_VALID_MESSAGE;
        } else if (!isNoteValid()) {
            errorMessage = NOTE_LENGTH_VALIDATION_MESSAGE;
        } else {
            errorMessage = CUSTOMER_ID_IS_REQUIRED_MESSAGE;
        }
        return errorMessage;
    }

    private boolean isFirstNameValid() {
        return isFieldValueValid(firstName, 3, 100) && firstName.matches(nameRegEx);
    }

    private boolean isLastNameValid() {
        return isFieldValueValid(lastName, 3, 100) && lastName.matches(nameRegEx);
    }

    private boolean isCountryCodeValid() {
        return countryCode != EMPTY;
    }

    private boolean isCountryNameValid() {
        return countryName != EMPTY;
    }

    private boolean isPhoneNumberValid() {
        return isFieldValueValid(phoneNumber, 2, 50);
    }

    private boolean isEmailValid() {
        return isEmailValueValid();
    }

    private boolean isNoteValid() {
        return isFieldValueValid(note, 0, 2000);
    }

    private boolean isCustomerIdValid() {
        return customerId != EMPTY;
    }


    private boolean isFieldValueValid(String value, int min, int max) {
        return value.length() >= min && value.length() <= max;
    }

    private boolean isEmailValueValid() {
        String regex = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
        return email.matches(regex);
    }

    private String[] getFirstNameValidationErrorMessage() {
        return (!isFirstNameValid() && firstName.length() == 0)
                ? new String[]{FIRST_NAME_IS_REQUIRED_MESSAGE, FIRST_NAME_LENGTH_VALIDATION_MESSAGE,
                FIRST_NAME_VALIDATION_MESSAGE}
                : (!firstName.matches(nameRegEx))
                        ? new String[]{FIRST_NAME_VALIDATION_MESSAGE}
                        : new String[]{FIRST_NAME_LENGTH_VALIDATION_MESSAGE};
    }

    private String[] getLastNameValidationErrorMessage() {
        return (!isLastNameValid() && lastName.length() == 0)
                ? new String[]{LAST_NAME_IS_REQUIRED_MESSAGE, LAST_NAME_LENGTH_VALIDATION_MESSAGE,
                LAST_NAME_VALIDATION_MESSAGE}
                : (!lastName.matches(nameRegEx))
                        ? new String[]{LAST_NAME_VALIDATION_MESSAGE}
                        : new String[]{LAST_NAME_LENGTH_VALIDATION_MESSAGE};
    }

    private String[] getPhoneNumberValidationErrorMessage() {
        return (!isPhoneNumberValid() && phoneNumber.length() == 0)
                ? new String[]{PHONE_NUMBER_IS_REQUIRED_MESSAGE, PHONE_NUMBER_LENGTH_VALIDATION_MESSAGE}
                : new String[]{PHONE_NUMBER_LENGTH_VALIDATION_MESSAGE};
    }

    private String[] getEmailValidationErrorMessage() {
        return (!isEmailValid() && email.length() == 0)
                ? new String[]{EMAIL_IS_REQUIRED_MESSAGE, EMAIL_SHOULD_BE_VALID_MESSAGE}
                : new String[]{EMAIL_SHOULD_BE_VALID_MESSAGE};
    }
}
