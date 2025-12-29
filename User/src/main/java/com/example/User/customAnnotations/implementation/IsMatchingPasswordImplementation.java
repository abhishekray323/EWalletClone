package com.example.User.customAnnotations.implementation;

import com.example.User.customAnnotations.IsMatchingPassword;
import com.example.User.entities.request.UserRequestDTO;
import jakarta.validation.ConstraintValidator;

public class IsMatchingPasswordImplementation implements ConstraintValidator<IsMatchingPassword, UserRequestDTO> {

    @Override
    public void initialize(IsMatchingPassword constraintAnnotation) {
    }

    /**
     * Validates that the password and confirmPassword fields in UserRequestDTO match.
     *
     * @param userRequestDTO the UserRequestDTO object containing the password and confirmPassword fields
     * @param context context in which the constraint is evaluated
     * @return true if the passwords match, false otherwise
     */
    @Override
    public boolean isValid(UserRequestDTO userRequestDTO, jakarta.validation.ConstraintValidatorContext context) {
        return userRequestDTO.getPassword().equals(userRequestDTO.getConfirmPassword());
    }

}
