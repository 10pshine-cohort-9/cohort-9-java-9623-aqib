package com.example.contactmanager.service;

import com.example.contactmanager.dto.ContactDetailResponse;
import com.example.contactmanager.dto.ContactRequest;
import com.example.contactmanager.dto.ContactResponse;
import com.example.contactmanager.dto.DtoMapper;
import com.example.contactmanager.dto.EmailDto;
import com.example.contactmanager.dto.PageResponse;
import com.example.contactmanager.dto.PhoneDto;
import com.example.contactmanager.entity.Contact;
import com.example.contactmanager.entity.EmailAddress;
import com.example.contactmanager.entity.PhoneNumber;
import com.example.contactmanager.entity.User;
import com.example.contactmanager.enums.EmailLabel;
import com.example.contactmanager.enums.PhoneLabel;
import com.example.contactmanager.exception.BadRequestException;
import com.example.contactmanager.exception.ResourceNotFoundException;
import com.example.contactmanager.repository.ContactRepository;
import com.example.contactmanager.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
@Transactional
public class ContactServiceImpl implements ContactService {

    private static final Logger log = LoggerFactory.getLogger(ContactServiceImpl.class);

    private final ContactRepository contactRepository;
    private final UserRepository userRepository;

    public ContactServiceImpl(ContactRepository contactRepository, UserRepository userRepository) {
        this.contactRepository = contactRepository;
        this.userRepository = userRepository;
    }

    @Override
    public ContactResponse create(Long userId, ContactRequest request) {
        log.info("Creating contact for user id={}", userId);
        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));

        Contact contact = Contact.builder()
                .firstName(request.getFirstName().trim())
                .lastName(request.getLastName().trim())
                .title(StringUtils.hasText(request.getTitle()) ? request.getTitle().trim() : null)
                .build();
        owner.addContact(contact);
        applyEmails(contact, request.getEmails());
        applyPhones(contact, request.getPhones());

        Contact saved = contactRepository.save(contact);
        log.info("Contact created id={} for user id={}", saved.getId(), userId);
        return DtoMapper.toContactResponse(saved);
    }

    @Override
    public ContactDetailResponse update(Long userId, Long contactId, ContactRequest request) {
        log.info("Updating contact id={} for user id={}", contactId, userId);
        Contact contact = findOwnedContact(userId, contactId);

        contact.setFirstName(request.getFirstName().trim());
        contact.setLastName(request.getLastName().trim());
        contact.setTitle(StringUtils.hasText(request.getTitle()) ? request.getTitle().trim() : null);
        applyEmails(contact, request.getEmails());
        applyPhones(contact, request.getPhones());

        Contact saved = contactRepository.save(contact);
        log.info("Contact updated id={}", saved.getId());
        return DtoMapper.toContactDetailResponse(saved);
    }

    @Override
    public void delete(Long userId, Long contactId) {
        log.info("Deleting contact id={} for user id={}", contactId, userId);
        Contact contact = findOwnedContact(userId, contactId);
        contactRepository.delete(contact);
        log.info("Contact deleted id={}", contactId);
    }

    @Override
    @Transactional(readOnly = true)
    public ContactDetailResponse getById(Long userId, Long contactId) {
        log.debug("Fetching contact id={} for user id={}", contactId, userId);
        Contact contact = findOwnedContact(userId, contactId);
        return DtoMapper.toContactDetailResponse(contact);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ContactResponse> list(Long userId, String search, Pageable pageable) {
        Page<Contact> page;
        if (StringUtils.hasText(search)) {
            log.debug("Listing contacts for user id={} with search='{}'", userId, search);
            page = contactRepository.searchByUserId(userId, search.trim(), pageable);
        } else {
            log.debug("Listing contacts for user id={}", userId);
            page = contactRepository.findByUserIdOrderByLastNameAscFirstNameAsc(userId, pageable);
        }
        return PageResponse.of(page.map(DtoMapper::toContactResponse));
    }

    private Contact findOwnedContact(Long userId, Long contactId) {
        return contactRepository.findByIdAndUserId(contactId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Contact", contactId));
    }

    private void applyEmails(Contact contact, List<EmailDto> emails) {
        new ArrayList<>(contact.getEmailAddresses()).forEach(contact::removeEmail);
        if (emails == null || emails.isEmpty()) {
            return;
        }
        validateNoDuplicates(emails.stream().map(EmailDto::getValue)
                        .map(value -> value.trim().toLowerCase(Locale.ROOT)).toList(),
                "email");
        for (EmailDto dto : emails) {
            EmailAddress email = EmailAddress.builder()
                    .value(dto.getValue().trim())
                    .label(parseEmailLabel(dto.getLabel()))
                    .build();
            contact.addEmail(email);
        }
    }

    private void applyPhones(Contact contact, List<PhoneDto> phones) {
        new ArrayList<>(contact.getPhoneNumbers()).forEach(contact::removePhone);
        if (phones == null || phones.isEmpty()) {
            return;
        }
        validateNoDuplicates(phones.stream().map(PhoneDto::getValue)
                        .map(value -> value.trim().toLowerCase(Locale.ROOT)).toList(),
                "phone number");
        for (PhoneDto dto : phones) {
            PhoneNumber phone = PhoneNumber.builder()
                    .value(dto.getValue().trim())
                    .label(parsePhoneLabel(dto.getLabel()))
                    .build();
            contact.addPhone(phone);
        }
    }

    private void validateNoDuplicates(List<String> values, String field) {
        long unique = values.stream().distinct().count();
        if (unique != values.size()) {
            throw new BadRequestException("Duplicate " + field + " values are not allowed");
        }
    }

    private EmailLabel parseEmailLabel(String label) {
        try {
            return EmailLabel.valueOf(label.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            throw new BadRequestException("Invalid email label: " + label);
        }
    }

    private PhoneLabel parsePhoneLabel(String label) {
        try {
            return PhoneLabel.valueOf(label.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            throw new BadRequestException("Invalid phone label: " + label);
        }
    }
}
