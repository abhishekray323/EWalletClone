package com.example.User.customAnnotations;

import com.example.User.customAnnotations.implementation.IsMatchingPasswordImplementation;
import jakarta.validation.Constraint;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({TYPE, ANNOTATION_TYPE})
@Retention(RUNTIME)
@Constraint(validatedBy = IsMatchingPasswordImplementation.class)
@Documented
public @interface IsMatchingPassword {
    String message() default "Fields do not match";
    Class<?>[] groups() default {};
    Class<? extends jakarta.validation.Payload>[] payload() default {};
}
