package com.example.contactmanager.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Full detail representation including labeled emails and phones.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContactDetailResponse {

    private Long id;
    private String firstName;
    private String lastName;
    private String title;
    private List<EmailDto> emails;
    private List<PhoneDto> phones;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
