package com.example.User.controller;

import com.example.User.config.SecurityConfiguration;
import com.example.User.customExceptions.UserAlreadyExistsException;
import com.example.User.entities.User;
import com.example.User.entities.request.UserRequestDTO;
import com.example.User.entities.response.UserResponseDTO;
import com.example.User.filter.JsonUsernamePasswordAuthFilter;
import com.example.User.handler.SuccessfullLoginHandler;
import com.example.User.handler.UnsuccessfulLoginHandler;
import com.example.User.repository.UserRepository;
import com.example.User.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Slf4j
@WebMvcTest(controllers = UserController.class)
@Import(SecurityConfiguration.class)
public class UserControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    UserService userService;

    @MockitoBean
    UserDetailsService uds;

    @MockitoBean
    SuccessfullLoginHandler successfullLoginHandler;

    @MockitoBean
    UnsuccessfulLoginHandler unsuccessfulLoginHandler;

    @MockitoBean
    UserRepository userRepository;

    @Nested
    class UserRegistersTests {
        @Test
        void userRegistrationSuccess200() throws Exception {

            User user = User.builder()
                    .id(UUID.fromString("550e8400-e29b-41d4-a716-446655440000"))
                    .name("Hari")
                    .email("hari@gmail.com")
                    .createdAt(LocalDateTime.parse("2023-10-01T12:00:00"))
                    .isKycDone(false)
                    .build();

            Mockito.when(userService.register(Mockito.any(UserRequestDTO.class))
            ).thenReturn(
                    user);
            String requestBody = "{ \"name\": \"Hari\", \"email\":\"hari@gmail.com\" , \"password\": \"testpass\", \"confirmPassword\": \"testpass\" }";

            mockMvc.perform(
                    post("/api/users/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody)
                    )
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.name").value("Hari"))
            .andExpect( jsonPath("$.password").doesNotExist())
            .andExpect(jsonPath("$.email").value("hari@gmail.com"))
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.createdAt").exists())
            .andExpect(jsonPath("$.kycDone").value(false));
        }

        @Test
        void userRegistrationFailureUserAlreadyExist() throws Exception {

            User user = User.builder()
                    .id(UUID.fromString("550e8400-e29b-41d4-a716-446655440000"))
                    .name("Hari")
                    .email("hari@gmail.com")
                    .createdAt(LocalDateTime.parse("2023-10-01T12:00:00"))
                    .isKycDone(false)
                    .roles(Set.of("USER"))
                    .build();

            Mockito.when(userService.register(Mockito.any(UserRequestDTO.class))
            ).thenThrow(new UserAlreadyExistsException("Hari","hari@gmail.com"));

            String requestBody = "{ \"name\": \"Hari\", \"email\":\"hari@gmail.com\" , \"password\": \"testpass\", \"confirmPassword\": \"testpass\" }";

            mockMvc.perform(
                            post("/api/users/register")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(requestBody)
                    )
                    .andExpect(status().isConflict());
        }

        @Test
        void userRegistrationFailureBadRequestException() throws Exception {


            String requestBody = "{ \"name\": \"\", \"email\":\"hari@gmail.com\" , \"password\": \"testpass\", \"confirmPassword\": \"testpass\" }";

            mockMvc.perform(
                            post("/api/users/register")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(requestBody)
                    )
                    .andExpect(status().isBadRequest());
        }

        @Test
        void userRegistrationFailureRuntimeException() throws Exception {

            User user = User.builder()
                    .id(UUID.fromString("550e8400-e29b-41d4-a716-446655440000"))
                    .name("Hari")
                    .email("hari@gmail.com")
                    .createdAt(LocalDateTime.parse("2023-10-01T12:00:00"))
                    .isKycDone(false)
                    .build();

            Mockito.when(userService.register(Mockito.any(UserRequestDTO.class))
            ).thenThrow(new RuntimeException());

            String requestBody = "{ \"name\": \"Hari\", \"email\":\"hari@gmail.com\" , \"password\": \"testpass\", \"confirmPassword\": \"testpass\" }";

            mockMvc.perform(
                            post("/api/users/register")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(requestBody)
                    )
                    .andExpect(status().isInternalServerError());
        }

    }

    @Nested
    class UserLoginTests {
        @BeforeEach
        void setup() {
            UserDetails userDetails= org.springframework.security.core.userdetails.User.builder()
                                    .username("hari@gmail.com")
                                    .password("testpass")
                                    .disabled(false)
                                    .authorities("ROLE_USER")
                                    .build();
            Mockito.when(uds.loadUserByUsername("hari@gmail.com")).thenReturn(userDetails);

            User user = User.builder()
                    .id(UUID.fromString("550e8400-e29b-41d4-a716-446655440000"))
                    .name("Hari")
                    .email("hari@gmail.com")
                    .createdAt(LocalDateTime.parse("2023-10-01T12:00:00"))
                    .isKycDone(false)
                    .build();
            Mockito.when(userRepository.findByEmail("hari@gmail.com")).thenReturn(Optional.ofNullable(user));
        }

        @Test
        void userLoginSuccess200() throws Exception {

            String requestBody = "{ \"email\":\"hari@gmail.com\" , \"password\": \"testpass\" }";
            mockMvc.perform(
                            post("/api/users/login")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(requestBody)
                    )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name").value("Hari"))
                    .andExpect( jsonPath("$.password").doesNotExist())
                    .andExpect(jsonPath("$.email").value("hari@gmail.com"))
                    .andExpect(jsonPath("$.id").exists())
                    .andExpect(jsonPath("$.createdAt").exists())
                    .andExpect(jsonPath("$.kycDone").value(false));;
        }
    }
}
