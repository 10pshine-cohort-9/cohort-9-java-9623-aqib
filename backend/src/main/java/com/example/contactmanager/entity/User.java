package com.example.contactmanager.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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

@Getter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(name = "uk_user_email", columnNames = "email"),
        @UniqueConstraint(name = "uk_user_phone", columnNames = "phone")
})
public class User extends BaseEntity {

    @Setter
    @Column(name = "email", length = 150)
    private String email;

    @Setter
    @Column(name = "phone", length = 30)
    private String phone;

    @Setter
    @Column(name = "password", nullable = false, length = 100)
    private String password;

    @Setter
    @Column(name = "first_name", nullable = false, length = 60)
    private String firstName;

    @Setter
    @Column(name = "last_name", nullable = false, length = 60)
    private String lastName;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true,
            fetch = jakarta.persistence.FetchType.LAZY)
    @Builder.Default
    @Setter(AccessLevel.NONE)
    private List<Contact> contacts = new ArrayList<>();

    public void addContact(Contact contact) {
        contacts.add(contact);
        contact.setUser(this);
    }

    public void removeContact(Contact contact) {
        contacts.remove(contact);
        contact.setUser(null);
    }
}
