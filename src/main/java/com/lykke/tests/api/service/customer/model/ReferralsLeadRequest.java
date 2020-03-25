package com.lykke.tests.api.service.customer.model;

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
public class ReferralsLeadRequest {

    private static final String EMAIL_ERROR = "InvalidEmailFormat";
    private static final String MODEL_ERROR = "ModelValidationFailed";
    private static final String MIN_LENGTH_FIRST_NAME_MESSAGE = "The field FirstName must be a string or array type with a minimum length of '3'.";
    private static final String MIN_LENGTH_LAST_NAME_MESSAGE = "The field LastName must be a string or array type with a minimum length of '3'.";
    private static final String FIRST_NAME_FIELD_VALIDATION_MESSAGE = "The field FirstName must match the regular expression '^((?![!@#$%^&*()_+{}|:\\\"?></,;[\\]\\\\=~]).)+$'.";
    private static final String LAST_NAME_FIELD_VALIDATION_MESSAGE = "The field LastName must match the regular expression '^((?![!@#$%^&*()_+{}|:\\\"?></,;[\\]\\\\=~]).)+$'.";
    private static final String NUMBER_FIELD_VALIDATION_MESSAGE = "The field Number must match the regular expression '^[0-9 A-Z a-z #;,()+*-]{1,30}$'.";
    private static final String MAX_LENGTH_FIRST_NAME_ERROR_MESSAGE = "The field FirstName must be a string or array type with a maximum length of '100'.";
    private static final String MAX_LENGTH_LAST_NAME_ERROR_MESSAGE = "The field LastName must be a string or array type with a maximum length of '100'.";
    private static final String MIN_LENGTH_NUMBER_ERROR_MESSAGE = "The field Number must be a string or array type with a minimum length of '2'.";
    private static final String MAX_LENGTH_NUMBER_ERROR_MESSAGE = "The field Number must be a string or array type with a maximum length of '50'.";
    private static final String MAX_LENGTH_NOTE_ERROR_MESSAGE = "The field Note must be a string or array type with a maximum length of '2000'.";
    private static final String MIN_LENGTH_NOTE_ERROR_MESSAGE = "The field Note must be a string or array type with a minimum length of '2'.";
    private static final String FIRST_NAME_FIELD_IS_REQUIRED = "The FirstName field is required. ";
    private static final String LAST_NAME_FIELD_IS_REQUIRED = "The LastName field is required. ";
    private static final String COUNTRY_CODE_FIELD_IS_REQUIRED = "The CountryCode field is required.";
    private static final String COUNTRY_NAME_FIELD_IS_REQUIRED = "The CountryName field is required.";
    private static final String NUMBER_FIELD_IS_REQUIRED = "The Number field is required. ";
    private static final String EMAIL_FIELD_IS_REQUIRED_MESSAGE = "The Email field is required.";
    private static final String EMAIL_IS_NOT_VALID = "The Email field is not a valid e-mail address.";
    private static String nameRegEx = "^((?![!@#$%^&*()_+{}|:\\\"?></,;\\[\\]=~]).)+$";
    private static String numberRegEx = "^[0-9 A-Z a-z #;,()+*-]{1,30}$";
    private String firstName;
    private String lastName;
    private String countryCode;
    private String countryName;
    private String number;
    private String email;
    private String note;

    public int getHttpStatus() {
        return isFirstNameValid() && firstName.matches(nameRegEx) && isLastNameValid() && lastName
                .matches(nameRegEx) && isCountryCodeValid() && isCountryNameValid() && isNumberValid() && isNoteValid()
                && isEmailValid()
                ? SC_OK
                : SC_BAD_REQUEST;
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

    private boolean isNumberValid() {
        return isFieldValueValid(number, 2, 50) && number.matches(numberRegEx);
    }

    private boolean isEmailValid() {
        return isEmailValueValid();
    }

    private boolean isNoteValid() {
        return isFieldValueValid(note, 2, 2000);
    }

    private boolean isFieldValueValid(String value, int min, int max) {
        return value.length() >= min && value.length() <= max;
    }

    private boolean isEmailValueValid() {
        String regex = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
        return (email.matches(regex));
    }

    public ValidationErrorReferralsLeadResponse getValidationResponse() {
        val response = new ValidationErrorReferralsLeadResponse();
        response.setMessage(getValidationMessage());
        response.setError(getValidationError());
        return response;
    }

    private String getValidationMessage() {
        String message;
        if (!isFirstNameValid()) {
            message = getFirstNameValidationErrorMessage();
        } else if (!isLastNameValid()) {
            message = getLastNameValidationErrorMessage();
        } else if (!isCountryCodeValid()) {
            message = COUNTRY_CODE_FIELD_IS_REQUIRED;
        } else if (!isCountryNameValid()) {
            message = COUNTRY_NAME_FIELD_IS_REQUIRED;
        } else if (!isNumberValid()) {
            message = getNumberValidationErrorMessage();
        } else if (!isEmailValid()) {
            message = getEmailValidationErrorMessage();
        } else {
            message = getNoteValidationErrorMessage();
        }

        return message;
    }

    private String getValidationError() {
        String error;
        if (!isFirstNameValid()) {
            error = MODEL_ERROR;
        } else if (!isLastNameValid()) {
            error = MODEL_ERROR;
        } else if (!isCountryCodeValid()) {
            error = MODEL_ERROR;
        } else if (!isCountryNameValid()) {
            error = MODEL_ERROR;
        } else if (!isNumberValid()) {
            error = MODEL_ERROR;
        } else if (!isEmailValid()) {
            error = EMAIL_ERROR;
        } else {
            error = MODEL_ERROR;
        }

        return error;
    }

    private String getFirstNameValidationErrorMessage() {
        String message;

        if (!firstName.matches(nameRegEx) && firstName.length() > 3) {
            message = FIRST_NAME_FIELD_VALIDATION_MESSAGE;
        } else if (firstName.length() == 0) {
            message = FIRST_NAME_FIELD_IS_REQUIRED + MIN_LENGTH_FIRST_NAME_MESSAGE;
        } else if (!isFirstNameValid() && firstName.length() >= 100 && firstName.matches(nameRegEx)) {
            message = MAX_LENGTH_FIRST_NAME_ERROR_MESSAGE;
        } else {
            message = MIN_LENGTH_FIRST_NAME_MESSAGE;
        }

        return message;
    }

    private String getLastNameValidationErrorMessage() {
        String message;

        if (!lastName.matches(nameRegEx) && lastName.length() > 3) {
            message = LAST_NAME_FIELD_VALIDATION_MESSAGE;
        } else if (lastName.length() == 0) {
            message = LAST_NAME_FIELD_IS_REQUIRED + MIN_LENGTH_LAST_NAME_MESSAGE;
        } else if (!isLastNameValid() && lastName.length() >= 100 && lastName.matches(nameRegEx)) {
            message = MAX_LENGTH_LAST_NAME_ERROR_MESSAGE;
        } else {
            message = MIN_LENGTH_LAST_NAME_MESSAGE;
        }

        return message;
    }

    private String getNumberValidationErrorMessage() {
        String message;

        if (!number.matches(numberRegEx) && number.length() > 2) {
            message = NUMBER_FIELD_VALIDATION_MESSAGE;
        } else if (number.length() == 0) {
            message = NUMBER_FIELD_IS_REQUIRED + MIN_LENGTH_NUMBER_ERROR_MESSAGE;
        } else if (!isNumberValid() && number.length() >= 100 && number.matches(numberRegEx)) {
            message = MAX_LENGTH_NUMBER_ERROR_MESSAGE;
        } else {
            message = MIN_LENGTH_NUMBER_ERROR_MESSAGE;
        }

        return message;
    }

    private String getEmailValidationErrorMessage() {
        return (email.length() == 0)
                ? EMAIL_FIELD_IS_REQUIRED_MESSAGE + " " + EMAIL_IS_NOT_VALID
                : EMAIL_IS_NOT_VALID;
    }

    private String getNoteValidationErrorMessage() {
        return (!isNoteValid() && note.length() >= 2000)
                ? MAX_LENGTH_NOTE_ERROR_MESSAGE
                : (note.length() < 2)
                        ? MIN_LENGTH_NOTE_ERROR_MESSAGE
                        : null;
    }
}
