package com.example.User.entities.response;


import com.example.User.entities.User;
import com.example.User.entities.request.UserRequestDTO;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class UserResponseDTOTest {

    static Validator validator;

    @BeforeAll
    static void init(){
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }


    @Test
    void testValidUserRequestDTO() {
        UserResponseDTO userResponseDTO = base();
        var violations = validator.validate(userResponseDTO);
        assert violations.isEmpty();
    }

    @Test
    void testEmptyName() {
        UserResponseDTO userResponseDTO = base();
        userResponseDTO.setName("");
        Set<ConstraintViolation<UserResponseDTO>> violations = validator.validate(userResponseDTO);
        assertThat(violations).
                anySatisfy(violation -> {;
                    assertThat(violation.getPropertyPath().toString()).isEqualTo("name");
                    assertThat(violation.getMessage()).isEqualTo("must not be empty");
                });
    }

    @Test
    void testEmptyEmail() {
        UserResponseDTO userResponseDTO = base();
        userResponseDTO.setEmail("");
        Set<ConstraintViolation<UserResponseDTO>> violations = validator.validate(userResponseDTO);
        assertThat(violations).
                anySatisfy(violation -> {;
                    assertThat(violation.getPropertyPath().toString()).isEqualTo("email");
                    assertThat(violation.getMessage()).isEqualTo("must not be empty");
                });
    }

    @Test
    void testInvalidFormatEmail() {
        UserResponseDTO userResponseDTO = base();
        userResponseDTO.setEmail("abhishekray1894gmailcom");
        Set<ConstraintViolation<UserResponseDTO>> violations = validator.validate(userResponseDTO);
        assertThat(violations).
                anySatisfy(violation -> {;
                    assertThat(violation.getPropertyPath().toString()).isEqualTo("email");
                    assertThat(violation.getMessage()).isEqualTo("Invalid email format");
                });
    }

    UserResponseDTO base(){
        return UserResponseDTO.builder()
                .id(UUID.fromString("550e8400-e29b-41d4-a716-446655440000"))
                .name("Abhishek")
                .email("abhishekray123484@gmail.com")
                .createdAt(LocalDateTime.parse("2023-10-01T12:00:00"))
                .isKycDone(false)
                .build();
    }


    }
