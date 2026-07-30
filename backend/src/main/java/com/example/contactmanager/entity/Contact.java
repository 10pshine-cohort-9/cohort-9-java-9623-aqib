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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "contacts", uniqueConstraints = {
        @UniqueConstraint(name = "uk_contact_owner_name",
                columnNames = {"user_id", "first_name", "last_name"})
})
public class Contact extends BaseEntity {

    @Column(name = "first_name", nullable = false, length = 60)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 60)
    private String lastName;

    @Column(name = "title", length = 100)
    private String title;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "contact", cascade = CascadeType.ALL,
            orphanRemoval = true, fetch = FetchType.EAGER)
    @Builder.Default
    private List<EmailAddress> emailAddresses = new ArrayList<>();

    @OneToMany(mappedBy = "contact", cascade = CascadeType.ALL,
            orphanRemoval = true, fetch = FetchType.EAGER)
    @Builder.Default
    private List<PhoneNumber> phoneNumbers = new ArrayList<>();

    /**
     * Helper to maintain both sides of the email association.
     */
    public void addEmail(EmailAddress email) {
        emailAddresses.add(email);
        email.setContact(this);
    }


    public void removeEmail(EmailAddress email) {
        emailAddresses.remove(email);
        email.setContact(null);
    }

    public void addPhone(PhoneNumber phone) {
        phoneNumbers.add(phone);
        phone.setContact(this);
    }


    public void removePhone(PhoneNumber phone) {
        phoneNumbers.remove(phone);
        phone.setContact(null);
    }

    public void setEmailAddresses(List<EmailAddress> emails) {
        emailAddresses.clear();
        if (emails != null) {
            emails.forEach(this::addEmail);
        }
    }

    public void setPhoneNumbers(List<PhoneNumber> phones) {
        phoneNumbers.clear();
        if (phones != null) {
            phones.forEach(this::addPhone);
        }
    }
}
