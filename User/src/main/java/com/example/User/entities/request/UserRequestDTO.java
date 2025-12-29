package com.example.User.entities.request;

import com.example.User.customAnnotations.IsMatchingPassword;
import com.example.User.customAnnotations.ValidEmail;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Data
@Builder
@IsMatchingPassword(message = "Passwords do not match")
public class UserRequestDTO {
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
    @NotEmpty
    private String password;

   @NonNull
    @NotEmpty
    private String confirmPassword;
}
