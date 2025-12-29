package com.example.User.entities.request;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class UserRequestDTOTest {
    static Validator validator;

    @BeforeAll
    static void init(){
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @Test
    void testValidUserRequestDTO() {
        UserRequestDTO userRequestDTO = base();
        var violations = validator.validate(userRequestDTO);
        assert violations.isEmpty();
    }

    @Test
    void testEmptyName() {
        UserRequestDTO userRequestDTO = base();
        userRequestDTO.setName("");
        Set<ConstraintViolation<UserRequestDTO>> violations = validator.validate(userRequestDTO);
        assertThat(violations).
                anySatisfy(violation -> {;
                    assertThat(violation.getPropertyPath().toString()).isEqualTo("name");
                    assertThat(violation.getMessage()).isEqualTo("must not be empty");
                });
    }

    @Test
    void testEmptyEmail() {
        UserRequestDTO userRequestDTO = base();
        userRequestDTO.setEmail("");
        Set<ConstraintViolation<UserRequestDTO>> violations = validator.validate(userRequestDTO);
        assertThat(violations).
                anySatisfy(violation -> {;
                    assertThat(violation.getPropertyPath().toString()).isEqualTo("email");
                    assertThat(violation.getMessage()).isEqualTo("must not be empty");
                });
    }

    @Test
    void testInvalidFormatEmail() {
        UserRequestDTO userRequestDTO = base();
        userRequestDTO.setEmail("abhishekray1894gmailcom");
        Set<ConstraintViolation<UserRequestDTO>> violations = validator.validate(userRequestDTO);
        assertThat(violations).
                anySatisfy(violation -> {;
                    assertThat(violation.getPropertyPath().toString()).isEqualTo("email");
                    assertThat(violation.getMessage()).isEqualTo("Invalid email format");
                });
    }
    @Test
    void testEmptyPassword() {
        UserRequestDTO userRequestDTO = base();
        userRequestDTO.setPassword("");
        Set<ConstraintViolation<UserRequestDTO>> violations = validator.validate(userRequestDTO);
        assertThat(violations).
                anySatisfy(violation -> {;
                    assertThat(violation.getPropertyPath().toString()).isEqualTo("password");
                    assertThat(violation.getMessage()).isEqualTo("must not be empty");
                });
    }

    @Test
    void testMatchingPassword() {
        UserRequestDTO userRequestDTO = base();
        userRequestDTO.setConfirmPassword("differentPassword");
        Set<ConstraintViolation<UserRequestDTO>> violations = validator.validate(userRequestDTO);
        assertThat(violations).
                anySatisfy(violation -> {;
                    assertThat(violation.getPropertyPath().toString()).isEmpty();
                    assertThat(violation.getMessage()).contains("Passwords do not match");
                    assertThat(violation.getRootBeanClass()).isEqualTo(UserRequestDTO.class);
                });
    }

    private static UserRequestDTO base(){
        return UserRequestDTO.builder()
                .name("Abhishek Ray")
                .email("abhishekray1894@gmail.com")
                .password("password123")
                .confirmPassword("password123")
                .build();
    }

}
