package com.example.contactmanager.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Registration payload. Either {@code email} or {@code phone} must be provided.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterRequest {

    @Email(message = "Email must be a valid email address")
    @Size(max = 150)
    private String email;

    @Size(max = 30)
    private String phone;

    @NotBlank(message = "First name is required")
    @Size(max = 60, message = "First name must be at most 60 characters")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(max = 60, message = "Last name must be at most 60 characters")
    private String lastName;

    @NotBlank(message = "Password is required")
    @Size(min = 6, max = 100, message = "Password must be between 6 and 100 characters")
    private String password;
}
