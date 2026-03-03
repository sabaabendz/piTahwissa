package tn.esprit.tahwissa.utils;

import java.util.regex.Pattern;

public class Validator {
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );

    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }

    public static boolean isNotEmpty(String value) {
        return value != null && !value.trim().isEmpty();
    }

    public static boolean isValidLength(String value, int min, int max) {
        return value != null && value.length() >= min && value.length() <= max;
    }

    public static boolean isPositiveNumber(double value) {
        return value > 0;
    }

    public static boolean isPositiveInteger(int value) {
        return value > 0;
    }
}
