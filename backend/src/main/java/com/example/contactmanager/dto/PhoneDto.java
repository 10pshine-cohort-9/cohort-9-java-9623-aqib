package com.example.contactmanager.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Labeled phone number DTO used in contact requests and detail responses.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PhoneDto {

    private Long id;

    @NotBlank(message = "Phone value is required")
    @Size(max = 30, message = "Phone must be at most 30 characters")
    private String value;

    @NotBlank(message = "Phone label is required")
    @Size(max = 20, message = "Phone label must be at most 20 characters")
    private String label;
}
