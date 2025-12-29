package com.example.User.customAnnotations.implementation;

import com.example.User.customAnnotations.ValidEmail;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidEmailImplementation implements ConstraintValidator<ValidEmail, String> {

    private static final String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
    @Override
    public void initialize(ValidEmail constraintAnnotation) {
        // Initialization logic if needed
    }

    /**
     * Validates the email format using a regular expression.
     *
     * @param s the email string to validate
     * @param constraintValidatorContext context in which the constraint is evaluated
     * @return true if the email is valid, false otherwise
     */
    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        return validateEmail(s);
    }

    /**
     * Validates the email format using a regular expression.
     *
     * @param email the email string to validate
     * @return true if the email is valid, false otherwise
     */
    boolean validateEmail(String email) {
        Pattern pattern = Pattern.compile(EMAIL_REGEX);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

}
