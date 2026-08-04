package com.example.contactmanager.dto;

import com.example.contactmanager.entity.Contact;
import com.example.contactmanager.entity.EmailAddress;
import com.example.contactmanager.entity.PhoneNumber;
import com.example.contactmanager.entity.User;

import java.util.Comparator;
import java.util.List;

/**
 * Centralised mapper that converts entities to response DTOs.
 * Keeping mapping logic in one place enforces the single responsibility principle
 * and avoids leaking JPA entities into the API layer.
 */
public final class DtoMapper {

    private DtoMapper() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * Converts a User entity to a UserResponse DTO.
     *
     * @param user the entity to convert
     * @return the response DTO
     */
    public static UserResponse toUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .phone(user.getPhone())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .build();
    }

    /**
     * Converts a Contact entity to a ContactResponse summary DTO.
     *
     * @param contact the entity to convert
     * @return the summary DTO
     */
    public static ContactResponse toContactResponse(Contact contact) {
        return ContactResponse.builder()
                .id(contact.getId())
                .firstName(contact.getFirstName())
                .lastName(contact.getLastName())
                .title(contact.getTitle())
                .createdAt(contact.getCreatedAt())
                .build();
    }

    /**
     * Converts an EmailAddress entity to an EmailDto.
     *
     * @param email the entity to convert
     * @return the DTO
     */
    public static EmailDto toEmailDto(EmailAddress email) {
        return EmailDto.builder()
                .id(email.getId())
                .value(email.getValue())
                .label(email.getLabel().name())
                .build();
    }

    /**
     * Converts a PhoneNumber entity to a PhoneDto.
     *
     * @param phone the entity to convert
     * @return the DTO
     */
    public static PhoneDto toPhoneDto(PhoneNumber phone) {
        return PhoneDto.builder()
                .id(phone.getId())
                .value(phone.getValue())
                .label(phone.getLabel().name())
                .build();
    }

    /**
     * Converts a Contact entity to a ContactDetailResponse with labeled emails and phones.
     *
     * @param contact the entity to convert
     * @return the detail DTO
     */
    public static ContactDetailResponse toContactDetailResponse(Contact contact) {
        List<EmailDto> emails = contact.getEmailAddresses().stream()
                .sorted(Comparator.comparing(EmailAddress::getLabel))
                .map(DtoMapper::toEmailDto)
                .toList();
        List<PhoneDto> phones = contact.getPhoneNumbers().stream()
                .sorted(Comparator.comparing(PhoneNumber::getLabel))
                .map(DtoMapper::toPhoneDto)
                .toList();
        return ContactDetailResponse.builder()
                .id(contact.getId())
                .firstName(contact.getFirstName())
                .lastName(contact.getLastName())
                .title(contact.getTitle())
                .emails(emails)
                .phones(phones)
                .createdAt(contact.getCreatedAt())
                .updatedAt(contact.getUpdatedAt())
                .build();
    }
}
