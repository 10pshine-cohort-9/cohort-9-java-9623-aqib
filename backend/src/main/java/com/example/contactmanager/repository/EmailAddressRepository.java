package com.example.contactmanager.repository;

import com.example.contactmanager.entity.EmailAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for {@link EmailAddress} entities.
 */
@Repository
public interface EmailAddressRepository extends JpaRepository<EmailAddress, Long> {
}
