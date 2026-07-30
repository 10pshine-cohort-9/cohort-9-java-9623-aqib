package com.example.contactmanager.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "phone_numbers", uniqueConstraints = {
        @UniqueConstraint(name = "uk_phone_contact_value",
                columnNames = {"contact_id", "phone_number"})
})
public class PhoneNumber extends BaseEntity {

    @Column(name = "phone_number", nullable = false, length = 30)
    private String value;

    @Column(name = "label", nullable = false, length = 20)
    private String label;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "contact_id", nullable = false)
    private Contact contact;
}
