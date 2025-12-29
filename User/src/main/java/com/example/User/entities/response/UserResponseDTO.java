package com.example.User.entities.response;

import com.example.User.customAnnotations.ValidEmail;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Represents a response DTO for user details.
 * This class is used to send user information back to the client.
 */
@Data
@Builder
public class UserResponseDTO {
    @NonNull
    private UUID id;

    @NonNull
    @NotEmpty
    private String name;

    @NonNull
    @NotEmpty
    @ValidEmail
    private String email;

    @NonNull
    @NotEmpty
    private String phoneNumber;

    @NonNull
    private LocalDateTime createdAt;

    private boolean isKycDone = false;



}
