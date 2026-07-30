package com.example.contactmanager.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * JPA entity for a contact owned by a user, with labeled emails and phone numbers.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "contacts", uniqueConstraints = {
        @UniqueConstraint(name = "uk_contact_owner_name",
                columnNames = {"user_id", "first_name", "last_name"})
})
public class Contact extends BaseEntity {

    @Setter
    @Column(name = "first_name", nullable = false, length = 60)
    private String firstName;

    @Setter
    @Column(name = "last_name", nullable = false, length = 60)
    private String lastName;

    @Setter
    @Column(name = "title", length = 100)
    private String title;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    @Setter(AccessLevel.PROTECTED)
    private User user;

    @OneToMany(mappedBy = "contact", cascade = CascadeType.ALL,
            orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    @Setter(AccessLevel.NONE)
    private List<EmailAddress> emailAddresses = new ArrayList<>();

    @OneToMany(mappedBy = "contact", cascade = CascadeType.ALL,
            orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    @Setter(AccessLevel.NONE)
    private List<PhoneNumber> phoneNumbers = new ArrayList<>();

    /**
     * Helper to maintain both sides of the email association.
     * Detaches the email from any previous contact before adding.
     * Idempotent: skips if the email is already associated with this contact.
     */
    public void addEmail(EmailAddress email) {
        if (emailAddresses.contains(email)) {
            return;
        }
        if (email.getContact() != null && email.getContact() != this) {
            email.getContact().removeEmail(email);
        }
        emailAddresses.add(email);
        email.setContact(this);
    }

    /**
     * Helper to remove an email and break the association cleanly.
     * Only clears the back-reference if the email was actually in this contact's list.
     */
    public void removeEmail(EmailAddress email) {
        if (emailAddresses.remove(email)) {
            email.setContact(null);
        }
    }

    /**
     * Helper to maintain both sides of the phone association.
     * Detaches the phone from any previous contact before adding.
     * Idempotent: skips if the phone is already associated with this contact.
     */
    public void addPhone(PhoneNumber phone) {
        if (phoneNumbers.contains(phone)) {
            return;
        }
        if (phone.getContact() != null && phone.getContact() != this) {
            phone.getContact().removePhone(phone);
        }
        phoneNumbers.add(phone);
        phone.setContact(this);
    }

    /**
     * Helper to remove a phone and break the association cleanly.
     * Only clears the back-reference if the phone was actually in this contact's list.
     */
    public void removePhone(PhoneNumber phone) {
        if (phoneNumbers.remove(phone)) {
            phone.setContact(null);
        }
    }

    /**
     * Replaces all email addresses, detaching removed children and attaching new ones
     * through the helper methods so back-references stay consistent.
     * Validates all incoming elements before mutating the collection.
     */
    public void setEmailAddresses(List<EmailAddress> emails) {
        List<EmailAddress> incoming = emails == null ? List.of() : new ArrayList<>(emails);
        incoming.forEach(e -> {
            if (e == null) {
                throw new IllegalArgumentException("Email list must not contain null elements");
            }
        });
        new ArrayList<>(emailAddresses).forEach(this::removeEmail);
        incoming.forEach(this::addEmail);
    }

    /**
     * Replaces all phone numbers, detaching removed children and attaching new ones
     * through the helper methods so back-references stay consistent.
     * Validates all incoming elements before mutating the collection.
     */
    public void setPhoneNumbers(List<PhoneNumber> phones) {
        List<PhoneNumber> incoming = phones == null ? List.of() : new ArrayList<>(phones);
        incoming.forEach(p -> {
            if (p == null) {
                throw new IllegalArgumentException("Phone list must not contain null elements");
            }
        });
        new ArrayList<>(phoneNumbers).forEach(this::removePhone);
        incoming.forEach(this::addPhone);
    }
}
