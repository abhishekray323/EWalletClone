package com.example.User.service;

import com.example.User.customExceptions.UserAlreadyExistsException;
import com.example.User.entities.User;
import com.example.User.entities.models.WalletCreationEvent;
import com.example.User.entities.request.UserRequestDTO;
import com.example.User.repository.UserRepository;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.hamcrest.Matchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @InjectMocks
    UserService userService;

    @Mock
    PasswordEncoder passwordEncoder;

    @Mock
    UserRepository userRepository;

    @Mock
    KafkaTemplate<String, WalletCreationEvent> kafkaTemplate;


    @Nested
    class RegisterUserTests {

        @Test
        void shouldRegisterUserSuccessfully(){
            UserRequestDTO userRequestDTO = base();
            User user = User.builder()
                    .id(UUID.randomUUID())
                    .name(userRequestDTO.getName())
                    .email(userRequestDTO.getEmail())
                    .password(userRequestDTO.getPassword())
                    .createdAt(LocalDateTime.now())
                    .build();

            when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());
            when(userRepository.save(ArgumentMatchers.any(User.class))).thenReturn(user);

            User registeredUser = userService.register(userRequestDTO);
            assertThat(registeredUser.getId()).isNotNull();
            assertThat(registeredUser.getName()).isEqualTo(user.getName());
            assertThat(registeredUser.getEmail()).isEqualTo(user.getEmail());
            assertThat(registeredUser.getPassword()).isEqualTo(user.getPassword());
            assertThat(registeredUser.getCreatedAt()).isNotNull();

            verify(userRepository).findByEmail("abhishekray1234@gmail.com");
            verify(userRepository).save(ArgumentMatchers.any(User.class));
            verify(kafkaTemplate).send(eq("wallet-creation-topic"), ArgumentMatchers.any(WalletCreationEvent.class));
            verifyNoMoreInteractions(userRepository);
        }

        @Test
        void shouldThrowErrorForAlreadyExistingUser(){
            UserRequestDTO userRequestDTO = base();

            when(userRepository.findByEmail(userRequestDTO.getEmail())).thenThrow(new UserAlreadyExistsException(userRequestDTO.getName(),userRequestDTO.getEmail()));
            when(passwordEncoder.encode(userRequestDTO.getPassword())).thenReturn("encodedPassword123");

            assertThatThrownBy(()-> userService.register(userRequestDTO))
                    .isInstanceOf(UserAlreadyExistsException.class)
                    .hasMessageContaining("User already exists username: " + userRequestDTO.getName() + " email: " + userRequestDTO.getEmail());
        }

        static UserRequestDTO base() {
            return UserRequestDTO.builder()
                    .name("Abhishek Ray")
                    .email("abhishekray1234@gmail.com")
                    .password("password123")
                    .confirmPassword("password123")
                    .build();
        }
    }
}
