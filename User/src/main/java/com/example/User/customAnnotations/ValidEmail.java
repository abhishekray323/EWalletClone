package com.example.User.customAnnotations;

import com.example.User.customAnnotations.implementation.ValidEmailImplementation;
import jakarta.validation.Constraint;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({TYPE, FIELD, ANNOTATION_TYPE})
@Retention(RUNTIME)
@Constraint(validatedBy = {ValidEmailImplementation.class})
public @interface ValidEmail {
    String message() default "Invalid email format";

    Class<?>[] groups() default {};

    Class<? extends jakarta.validation.Payload>[] payload() default {};
}
