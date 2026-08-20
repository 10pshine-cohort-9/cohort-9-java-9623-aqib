package com.example.contactmanager.service;

import com.example.contactmanager.dto.ContactRequest;
import com.example.contactmanager.dto.EmailDto;
import com.example.contactmanager.dto.PhoneDto;
import com.example.contactmanager.exception.BadRequestException;
import com.example.contactmanager.exception.ResourceNotFoundException;
import com.example.contactmanager.repository.ContactRepository;
import com.example.contactmanager.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ContactServiceTest {

    private static final Long USER_ID = 1L;

    @Mock
    private ContactRepository contactRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ContactServiceImpl contactService;

    private com.example.contactmanager.entity.User owner;

    @BeforeEach
    void setUp() {
        owner = com.example.contactmanager.entity.User.builder()
                .id(USER_ID).email("jane@example.com").password("hash")
                .firstName("Jane").lastName("Doe").build();
    }

    @Test
    @DisplayName("create saves a contact with emails and phones")
    void createSuccess() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(owner));
        when(contactRepository.save(any())).thenAnswer(inv -> {
            com.example.contactmanager.entity.Contact original = inv.getArgument(0);
            return com.example.contactmanager.entity.Contact.builder()
                    .id(10L)
                    .firstName(original.getFirstName())
                    .lastName(original.getLastName())
                    .title(original.getTitle())
                    .user(original.getUser())
                    .emailAddresses(original.getEmailAddresses())
                    .phoneNumbers(original.getPhoneNumbers())
                    .build();
        });

        ContactRequest request = ContactRequest.builder()
                .firstName("John").lastName("Smith").title("Engineer")
                .emails(List.of(EmailDto.builder().value("john@x.com").label("WORK").build()))
                .phones(List.of(PhoneDto.builder().value("+923001234567").label("PERSONAL").build()))
                .build();

        var response = contactService.create(USER_ID, request);

        assertEquals(10L, response.getId());
        assertEquals("John", response.getFirstName());
        verify(contactRepository).save(any());
    }

    @Test
    @DisplayName("create throws when owner user not found")
    void createOwnerNotFound() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());
        ContactRequest request = ContactRequest.builder()
                .firstName("John").lastName("Smith").build();
        assertThrows(ResourceNotFoundException.class, () -> contactService.create(USER_ID, request));
    }

    @Test
    @DisplayName("create rejects duplicate email values")
    void createDuplicateEmails() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(owner));
        ContactRequest request = ContactRequest.builder()
                .firstName("John").lastName("Smith")
                .emails(List.of(
                        EmailDto.builder().value("dup@x.com").label("WORK").build(),
                        EmailDto.builder().value("DUP@x.com").label("PERSONAL").build()))
                .build();
        assertThrows(BadRequestException.class, () -> contactService.create(USER_ID, request));
    }

    @Test
    @DisplayName("update overwrites existing contact data")
    void updateSuccess() {
        var contact = com.example.contactmanager.entity.Contact.builder()
                .id(10L).firstName("Old").lastName("Name").user(owner).build();
        when(contactRepository.findByIdAndUserId(10L, USER_ID)).thenReturn(Optional.of(contact));
        when(contactRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        ContactRequest request = ContactRequest.builder()
                .firstName("New").lastName("Name").title("Lead")
                .emails(List.of(EmailDto.builder().value("n@x.com").label("WORK").build()))
                .build();

        var response = contactService.update(USER_ID, 10L, request);

        assertEquals("New", response.getFirstName());
        assertEquals("Lead", response.getTitle());
        assertEquals(1, response.getEmails().size());
    }

    @Test
    @DisplayName("update throws when contact not found")
    void updateNotFound() {
        when(contactRepository.findByIdAndUserId(99L, USER_ID)).thenReturn(Optional.empty());
        ContactRequest request = ContactRequest.builder()
                .firstName("New").lastName("Name").build();
        assertThrows(ResourceNotFoundException.class, () -> contactService.update(USER_ID, 99L, request));
    }

    @Test
    @DisplayName("delete removes the contact")
    void deleteSuccess() {
        var contact = com.example.contactmanager.entity.Contact.builder()
                .id(10L).firstName("John").lastName("Smith").user(owner).build();
        when(contactRepository.findByIdAndUserId(10L, USER_ID)).thenReturn(Optional.of(contact));

        contactService.delete(USER_ID, 10L);

        verify(contactRepository).delete(contact);
    }

    @Test
    @DisplayName("getById returns contact detail")
    void getByIdSuccess() {
        var contact = com.example.contactmanager.entity.Contact.builder()
                .id(10L).firstName("John").lastName("Smith").user(owner).build();
        when(contactRepository.findByIdAndUserId(10L, USER_ID)).thenReturn(Optional.of(contact));

        var response = contactService.getById(USER_ID, 10L);

        assertEquals(10L, response.getId());
        assertEquals("John", response.getFirstName());
    }

    @Test
    @DisplayName("list without search returns page of contacts")
    void listNoSearch() {
        var contact = com.example.contactmanager.entity.Contact.builder()
                .id(10L).firstName("John").lastName("Smith").user(owner).build();
        when(contactRepository.findByUserIdOrderByLastNameAscFirstNameAsc(eq(USER_ID), any()))
                .thenReturn(new PageImpl<>(List.of(contact), PageRequest.of(0, 10), 1));

        var response = contactService.list(USER_ID, null, PageRequest.of(0, 10));

        assertEquals(1, response.getTotalElements());
        assertEquals(1, response.getContent().size());
    }

    @Test
    @DisplayName("list with search delegates to search query")
    void listWithSearch() {
        when(contactRepository.searchByUserId(eq(USER_ID), eq("john"), any()))
                .thenReturn(new PageImpl<>(List.of(), PageRequest.of(0, 10), 0));

        var response = contactService.list(USER_ID, "john", PageRequest.of(0, 10));

        assertEquals(0, response.getTotalElements());
    }
}
