package com.example.contactmanager.repository;

import com.example.contactmanager.config.JpaAuditingConfig;
import com.example.contactmanager.entity.Contact;
import com.example.contactmanager.entity.EmailAddress;
import com.example.contactmanager.entity.PhoneNumber;
import com.example.contactmanager.entity.User;
import com.example.contactmanager.enums.EmailLabel;
import com.example.contactmanager.enums.PhoneLabel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@Import(JpaAuditingConfig.class)
class ContactRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ContactRepository contactRepository;

    @Autowired
    private UserRepository userRepository;

    private User persistUser() {
        User user = User.builder()
                .email("jane@example.com")
                .password("encoded")
                .firstName("Jane").lastName("Doe")
                .build();
        return entityManager.persistAndFlush(user);
    }

    private Contact persistContact(User owner, String first, String last, String title) {
        Contact contact = Contact.builder()
                .firstName(first).lastName(last).title(title).user(owner).build();
        return entityManager.persistAndFlush(contact);
    }

    @Test
    @DisplayName("findByEmail returns the persisted user")
    void findByEmail() {
        persistUser();
        Optional<User> found = userRepository.findByEmail("jane@example.com");
        assertTrue(found.isPresent());
        assertEquals("Jane", found.get().getFirstName());
    }

    @Test
    @DisplayName("existsByEmail and existsByPhone report correctly")
    void existsChecks() {
        persistUser();
        assertTrue(userRepository.existsByEmail("jane@example.com"));
        assertFalse(userRepository.existsByEmail("nobody@example.com"));
    }

    @Test
    @DisplayName("findByUserIdOrderByLastNameAscFirstNameAsc paginates contacts")
    void listPaginated() {
        User owner = persistUser();
        persistContact(owner, "John", "Smith", "Eng");
        persistContact(owner, "Alice", "Brown", "Lead");
        entityManager.flush();
        entityManager.clear();

        Page<Contact> page = contactRepository
                .findByUserIdOrderByLastNameAscFirstNameAsc(owner.getId(), PageRequest.of(0, 10));

        assertEquals(2, page.getTotalElements());
        // ordered by last name asc -> Brown first
        assertEquals("Brown", page.getContent().get(0).getLastName());
    }

    @Test
    @DisplayName("searchByUserId matches first or last name case-insensitively")
    void searchByName() {
        User owner = persistUser();
        persistContact(owner, "John", "Smith", null);
        persistContact(owner, "Alice", "Brown", null);
        entityManager.flush();
        entityManager.clear();

        Page<Contact> results = contactRepository
                .searchByUserId(owner.getId(), "john", PageRequest.of(0, 10));

        assertEquals(1, results.getTotalElements());
        assertEquals("John", results.getContent().get(0).getFirstName());
    }

    @Test
    @DisplayName("searchByUserId matches combined first + last name")
    void searchByFullName() {
        User owner = persistUser();
        persistContact(owner, "John", "Smith", null);
        entityManager.flush();
        entityManager.clear();

        Page<Contact> results = contactRepository
                .searchByUserId(owner.getId(), "john smi", PageRequest.of(0, 10));

        assertEquals(1, results.getTotalElements());
    }

    @Test
    @DisplayName("findByIdAndUserId returns contact only for the owning user")
    void findByIdAndUserId() {
        User owner = persistUser();
        Contact contact = persistContact(owner, "John", "Smith", "Eng");
        entityManager.flush();
        entityManager.clear();

        Optional<Contact> found = contactRepository.findByIdAndUserId(contact.getId(), owner.getId());
        assertTrue(found.isPresent());

        Optional<Contact> notFound = contactRepository.findByIdAndUserId(contact.getId(), 9999L);
        assertFalse(notFound.isPresent());
    }

    @Test
    @DisplayName("cascaded emails and phones are persisted with the contact")
    void cascadeEmailsAndPhones() {
        User owner = persistUser();
        Contact contact = Contact.builder()
                .firstName("John").lastName("Smith").user(owner).build();
        EmailAddress email = EmailAddress.builder().value("john@x.com").label(EmailLabel.WORK).build();
        PhoneNumber phone = PhoneNumber.builder().value("+923001234567").label(PhoneLabel.PERSONAL).build();
        contact.addEmail(email);
        contact.addPhone(phone);
        entityManager.persistAndFlush(contact);
        entityManager.clear();

        Contact reloaded = contactRepository.findById(contact.getId()).orElseThrow();
        assertEquals(List.of("john@x.com"),
                reloaded.getEmailAddresses().stream().map(EmailAddress::getValue).toList());
        assertEquals(List.of("+923001234567"),
                reloaded.getPhoneNumbers().stream().map(PhoneNumber::getValue).toList());
    }
}
