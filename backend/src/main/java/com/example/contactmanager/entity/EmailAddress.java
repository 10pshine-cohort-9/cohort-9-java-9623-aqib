package com.example.contactmanager.entity;

import com.example.contactmanager.enums.EmailLabel;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "email_addresses", uniqueConstraints = {
        @UniqueConstraint(name = "uk_email_contact_value",
                columnNames = {"contact_id", "email_address"})
})
public class EmailAddress extends BaseEntity {

    @Setter
    @Column(name = "email_address", nullable = false, length = 150)
    private String value;

    @Setter
    @Enumerated(EnumType.STRING)
    @Column(name = "label", nullable = false, length = 20)
    private EmailLabel label;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "contact_id", nullable = false)
    @Setter(AccessLevel.PROTECTED)
    private Contact contact;
}
