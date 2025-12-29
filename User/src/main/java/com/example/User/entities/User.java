package com.example.User.entities;

import com.example.User.customAnnotations.ValidEmail;
import com.example.User.entities.response.UserResponseDTO;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.FetchType;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

/**
 * Represents a user entity in the system.
 * This class maps to the "users" table in the database.
 */
@SuppressWarnings("checkstyle:RegexpSingleline")
@Entity
@Table(name = "user_entity")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    @ValidEmail
    private String email;

    @Column(nullable = false, unique = true)
    @Pattern(
            regexp = "^\\+?[1-9]\\d{1,14}$",
            message = "Invalid phone number format"
    )
    private String phoneNumber;

    @Column(nullable = false)
    private String password;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private boolean isKycDone = false;

    @ElementCollection(fetch = FetchType.EAGER)
    private Set<String> roles = Set.of("ROLE_USER");

    /**
     * Converts this User entity to a UserResponseDTO.
     *
     * @return a UserResponseDTO containing user details.
     */
    public UserResponseDTO toResponseDTO() {
        return UserResponseDTO.builder()
                .id(this.id)
                .name(this.name)
                .email(this.email)
                .phoneNumber(this.phoneNumber)
                .createdAt(this.createdAt)
                .isKycDone(this.isKycDone)
                .build();
    }
}
