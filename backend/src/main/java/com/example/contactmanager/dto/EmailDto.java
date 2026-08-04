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
 * Labeled email address DTO used in contact requests and detail responses.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmailDto {

    private Long id;

    @NotBlank(message = "Email value is required")
    @Email(message = "Email must be a valid email address")
    @Size(max = 150, message = "Email must be at most 150 characters")
    private String value;

    @NotBlank(message = "Email label is required")
    @Size(max = 20, message = "Email label must be at most 20 characters")
    private String label;
}
