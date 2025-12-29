package com.example.User.repository;

import com.example.User.entities.User;
import jakarta.persistence.EntityManager;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.constraints.Null;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
@AutoConfigureTestDatabase
public class UserRepositoryTest {
    @Autowired
    private EntityManager entityManager;

    @Autowired
    UserRepository userRepository;

    @Nested
    class FindByEmailTest {

        @Test
        void findByEmail_ShouldReturnUser_WhenEmailExists() {
            User user = User.builder()
                    .name("Ravi")
                    .email("ravi@gmail.com")
                    .password("password123")
                    .createdAt(LocalDateTime.parse("2023-10-01T12:00:00"))
                    .isKycDone(false)
                    .build();

            userRepository.save(user);
            var foundUser = userRepository.findByEmail("ravi@gmail.com");
            assertThat(foundUser).isPresent();
            assertThat(foundUser.get().getName()).isEqualTo("Ravi");
        }

        @Test
        void shouldNotReturnUser_WhenEmailDoesNotExist() {
            var foundUser = userRepository.findByEmail("ravi@gmail.com");
            assertThat(foundUser).isNotPresent();
        }

        @Test
        void shouldThrowException_WhenNameIsNull() {
            User user = User.builder()
                    .email("abhishek132@gmail.com")
                    .password("password123")
                    .createdAt(LocalDateTime.parse("2023-10-01T12:00:00"))
                    .isKycDone(false)
                    .build();
            org.hibernate.exception.ConstraintViolationException exception = assertThrows(org.hibernate.exception.ConstraintViolationException.class, () -> {
                entityManager.persist(user);
                entityManager.flush();
            });
            assertThat(exception.getSQLException().getMessage()).contains("NULL not allowed for column \"NAME\"");
        }

        @Test
        void shouldThrowException_WhenInvalidEmail() {
            User user = User.builder()
                    .name("Abhishek")
                    .email("abhishek113gmail.com")
                    .password("password123")
                    .createdAt(LocalDateTime.parse("2023-10-01T12:00:00"))
                    .isKycDone(false)
                    .build();

            ConstraintViolationException exception= assertThrows(ConstraintViolationException.class, () -> {
                entityManager.persist(user);
                entityManager.flush();
            });
            assertThat(exception.getConstraintViolations()).hasSize(1);
            assertThat(exception.getConstraintViolations())
                    .anySatisfy(violation -> {
                        assertThat(violation.getPropertyPath().toString()).isEqualTo("email");
                        assertThat(violation.getMessage()).isEqualTo("Invalid email format");
                    });
        }

        @Test
        void shouldThrowException_WhenPasswordIsNull() {
            User user = User.builder()
                    .name("abhishek ray")
                    .email("abhishekray1341@gmail.com")
                    .createdAt(LocalDateTime.parse("2023-10-01T12:00:00"))
                    .isKycDone(false)
                    .build();
            org.hibernate.exception.ConstraintViolationException exception = assertThrows(org.hibernate.exception.ConstraintViolationException.class, () -> {
                entityManager.persist(user);
                entityManager.flush();
            });
            assertThat(exception.getSQLException().getMessage()).contains("NULL not allowed for column \"PASSWORD\"");
        }


    }

}