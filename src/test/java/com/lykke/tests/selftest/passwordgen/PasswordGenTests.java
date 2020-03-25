package com.lykke.tests.selftest.passwordgen;

import static com.lykke.api.testing.api.common.PasswordGen.generateInvalidPassword;
import static com.lykke.api.testing.api.common.PasswordGen.generateValidPassword;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.regex.Pattern;
import lombok.val;
import org.junit.jupiter.api.Test;

public class PasswordGenTests {

    private static final Pattern PATTERN_LOWER_CASE_PRESENT = Pattern.compile("\\p{javaLowerCase}+");
    private static final Pattern PATTERN_UPPER_CASE_PRESENT = Pattern.compile("\\p{javaUpperCase}+");
    private static final Pattern PATTERN_NUMERIC_PRESENT = Pattern.compile("\\p{Digit}+");
    private static final Pattern PATTERN_SPECIAL_PRESENT = Pattern.compile("[\\!\\@\\#\\$\\%\\&]+");
    private static int MAX_ALLOWABLE_LENGTH = 50;
    private static int MIN_ALLOWABLE_LENGTH = 8;

    @Test
    void shouldGenerateValidPassword() {
        val actualPassword = generateValidPassword();

        assertAll(
                () -> assertTrue(isLengthValid(actualPassword)),
                () -> assertTrue(PATTERN_LOWER_CASE_PRESENT.matcher(actualPassword).find()),
                () -> assertTrue(PATTERN_UPPER_CASE_PRESENT.matcher(actualPassword).find()),
                () -> assertTrue(PATTERN_NUMERIC_PRESENT.matcher(actualPassword).find()),
                () -> assertTrue(PATTERN_SPECIAL_PRESENT.matcher(actualPassword).find())
        );
    }

    @Test
    void shouldGenerateInvalidPasswordTooShort() {
        val minLength = 4;
        val maxLength = 4;
        val actualPassword = generateInvalidPassword(minLength, maxLength, true, true, true, true);

        assertAll(
                () -> assertTrue(isLengthExpected(actualPassword, minLength, maxLength)),
                () -> assertTrue(PATTERN_LOWER_CASE_PRESENT.matcher(actualPassword).find()),
                () -> assertTrue(PATTERN_UPPER_CASE_PRESENT.matcher(actualPassword).find()),
                () -> assertTrue(PATTERN_NUMERIC_PRESENT.matcher(actualPassword).find()),
                () -> assertTrue(PATTERN_SPECIAL_PRESENT.matcher(actualPassword).find())
        );
    }

    @Test
    void shouldGenerateInvalidPasswordTooShortAndHasNoNumeric() {
        val minLength = 4;
        val maxLength = 4;
        val actualPassword = generateInvalidPassword(minLength, maxLength, true, true, false, true);

        assertAll(
                () -> assertTrue(isLengthExpected(actualPassword, minLength, maxLength)),
                () -> assertTrue(PATTERN_LOWER_CASE_PRESENT.matcher(actualPassword).find()),
                () -> assertTrue(PATTERN_UPPER_CASE_PRESENT.matcher(actualPassword).find()),
                () -> assertFalse(PATTERN_NUMERIC_PRESENT.matcher(actualPassword).find()),
                () -> assertTrue(PATTERN_SPECIAL_PRESENT.matcher(actualPassword).find())
        );
    }

    @Test
    void shouldGenerateInvalidPasswordTooShortAndHasNoNumericAndSpecial() {
        val minLength = 4;
        val maxLength = 4;
        val actualPassword = generateInvalidPassword(minLength, maxLength, true, true, false, false);

        assertAll(
                () -> assertTrue(isLengthExpected(actualPassword, minLength, maxLength)),
                () -> assertTrue(PATTERN_LOWER_CASE_PRESENT.matcher(actualPassword).find()),
                () -> assertTrue(PATTERN_UPPER_CASE_PRESENT.matcher(actualPassword).find()),
                () -> assertFalse(PATTERN_NUMERIC_PRESENT.matcher(actualPassword).find()),
                () -> assertFalse(PATTERN_SPECIAL_PRESENT.matcher(actualPassword).find())
        );
    }

    @Test
    void shouldGenerateInvalidPasswordTooLong() {
        val minLength = MAX_ALLOWABLE_LENGTH + 1;
        val maxLength = MAX_ALLOWABLE_LENGTH + 2;
        val actualPassword = generateInvalidPassword(minLength, maxLength, true, true, true, true);

        assertAll(
                () -> assertTrue(isLengthExpected(actualPassword, minLength, maxLength)),
                () -> assertTrue(PATTERN_LOWER_CASE_PRESENT.matcher(actualPassword).find()),
                () -> assertTrue(PATTERN_UPPER_CASE_PRESENT.matcher(actualPassword).find()),
                () -> assertTrue(PATTERN_NUMERIC_PRESENT.matcher(actualPassword).find()),
                () -> assertTrue(PATTERN_SPECIAL_PRESENT.matcher(actualPassword).find())
        );
    }

    @Test
    void shouldGenerateInvalidPasswordWithoutLowerCase() {
        val actualPassword = generateInvalidPassword(MIN_ALLOWABLE_LENGTH, MAX_ALLOWABLE_LENGTH, true, false, true,
                true);

        assertAll(
                () -> assertTrue(isLengthValid(actualPassword)),
                () -> assertFalse(PATTERN_LOWER_CASE_PRESENT.matcher(actualPassword).find()),
                () -> assertTrue(PATTERN_UPPER_CASE_PRESENT.matcher(actualPassword).find()),
                () -> assertTrue(PATTERN_NUMERIC_PRESENT.matcher(actualPassword).find()),
                () -> assertTrue(PATTERN_SPECIAL_PRESENT.matcher(actualPassword).find())
        );
    }

    @Test
    void shouldGenerateInvalidPasswordWithoutUpperCase() {
        val actualPassword = generateInvalidPassword(MIN_ALLOWABLE_LENGTH, MAX_ALLOWABLE_LENGTH, false, true, true,
                true);

        assertAll(
                () -> assertTrue(isLengthValid(actualPassword)),
                () -> assertTrue(PATTERN_LOWER_CASE_PRESENT.matcher(actualPassword).find()),
                () -> assertFalse(PATTERN_UPPER_CASE_PRESENT.matcher(actualPassword).find()),
                () -> assertTrue(PATTERN_NUMERIC_PRESENT.matcher(actualPassword).find()),
                () -> assertTrue(PATTERN_SPECIAL_PRESENT.matcher(actualPassword).find())
        );
    }

    @Test
    void shouldGenerateInvalidPasswordWithoutNumeric() {
        val actualPassword = generateInvalidPassword(MIN_ALLOWABLE_LENGTH, MAX_ALLOWABLE_LENGTH, true, true, false,
                true);

        assertAll(
                () -> assertTrue(isLengthValid(actualPassword)),
                () -> assertTrue(PATTERN_LOWER_CASE_PRESENT.matcher(actualPassword).find()),
                () -> assertTrue(PATTERN_UPPER_CASE_PRESENT.matcher(actualPassword).find()),
                () -> assertFalse(PATTERN_NUMERIC_PRESENT.matcher(actualPassword).find()),
                () -> assertTrue(PATTERN_SPECIAL_PRESENT.matcher(actualPassword).find())
        );
    }

    @Test
    void shouldGenerateInvalidPasswordWithoutSpecial() {
        val actualPassword = generateInvalidPassword(MIN_ALLOWABLE_LENGTH, MAX_ALLOWABLE_LENGTH, true, true, true,
                false);

        assertAll(
                () -> assertTrue(isLengthValid(actualPassword)),
                () -> assertTrue(PATTERN_LOWER_CASE_PRESENT.matcher(actualPassword).find()),
                () -> assertTrue(PATTERN_UPPER_CASE_PRESENT.matcher(actualPassword).find()),
                () -> assertTrue(PATTERN_NUMERIC_PRESENT.matcher(actualPassword).find()),
                () -> assertFalse(PATTERN_SPECIAL_PRESENT.matcher(actualPassword).find())
        );
    }

    @Test
    void shouldGenerateInvalidPasswordWithoutLowerAndUpperCase() {
        val actualPassword = generateInvalidPassword(MIN_ALLOWABLE_LENGTH, MAX_ALLOWABLE_LENGTH, false, false, true,
                true);

        assertAll(
                () -> assertTrue(isLengthValid(actualPassword)),
                () -> assertFalse(PATTERN_LOWER_CASE_PRESENT.matcher(actualPassword).find()),
                () -> assertFalse(PATTERN_UPPER_CASE_PRESENT.matcher(actualPassword).find()),
                () -> assertTrue(PATTERN_NUMERIC_PRESENT.matcher(actualPassword).find()),
                () -> assertTrue(PATTERN_SPECIAL_PRESENT.matcher(actualPassword).find())
        );
    }

    @Test
    void shouldGenerateInvalidPasswordWithoutNumericAndSpecial() {
        val actualPassword = generateInvalidPassword(MIN_ALLOWABLE_LENGTH, MAX_ALLOWABLE_LENGTH, true, true, false,
                false);

        assertAll(
                () -> assertTrue(isLengthValid(actualPassword)),
                () -> assertTrue(PATTERN_LOWER_CASE_PRESENT.matcher(actualPassword).find()),
                () -> assertTrue(PATTERN_UPPER_CASE_PRESENT.matcher(actualPassword).find()),
                () -> assertFalse(PATTERN_NUMERIC_PRESENT.matcher(actualPassword).find()),
                () -> assertFalse(PATTERN_SPECIAL_PRESENT.matcher(actualPassword).find())
        );
    }

    private boolean isLengthValid(String password) {
        return MIN_ALLOWABLE_LENGTH <= password.length() && MAX_ALLOWABLE_LENGTH >= password.length();
    }

    private boolean isLengthExpected(String password, int minLength, int maxLength) {
        return minLength <= password.length() && maxLength >= password.length();
    }
}
