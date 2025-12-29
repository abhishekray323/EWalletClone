package com.example.User.entities;

import com.example.User.entities.response.UserResponseDTO;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class UserEntityTest {


    @Test
    void toUserResponseDTOTest(){
        User user = User.builder()
                .id(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"))
                .name("Abhishek Ray")
                .email("abhishek1234@gmail.com")
                .password("password123")
                .createdAt(LocalDateTime.parse("2023-10-01T12:00:00"))
                .isKycDone(false)
                .build();

        UserResponseDTO userResponseDTO = UserResponseDTO.builder()
                .id(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"))
                .name("Abhishek Ray")
                .email("abhishek1234@gmail.com")
                .createdAt(LocalDateTime.parse("2023-10-01T12:00:00"))
                .isKycDone(false)
                .build();

        assertThat(user.toResponseDTO()).isEqualTo(userResponseDTO);
    }

}
