package com.example.contactmanager.entity;

import com.example.contactmanager.enums.PhoneLabel;
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
@Table(name = "phone_numbers", uniqueConstraints = {
        @UniqueConstraint(name = "uk_phone_contact_value",
                columnNames = {"contact_id", "phone_number"})
})
public class PhoneNumber extends BaseEntity {

    @Setter
    @Column(name = "phone_number", nullable = false, length = 30)
    private String value;

    @Setter
    @Enumerated(EnumType.STRING)
    @Column(name = "label", nullable = false, length = 20)
    private PhoneLabel label;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "contact_id", nullable = false)
    @Setter(AccessLevel.PROTECTED)
    private Contact contact;
}
