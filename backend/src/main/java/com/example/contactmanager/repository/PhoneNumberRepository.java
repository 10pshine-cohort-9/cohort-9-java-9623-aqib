package com.example.contactmanager.repository;

import com.example.contactmanager.entity.PhoneNumber;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for {@link PhoneNumber} entities.
 */
@Repository
public interface PhoneNumberRepository extends JpaRepository<PhoneNumber, Long> {
}
