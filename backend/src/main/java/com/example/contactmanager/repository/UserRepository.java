package com.example.contactmanager.repository;

import com.example.contactmanager.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data JPA repository for {@link User} entities.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Finds a user by their email address.
     *
     * @param email the email address to search for
     * @return an Optional containing the matching user, or empty if not found
     */
    Optional<User> findByEmail(String email);

    /**
     * Finds a user by their phone number.
     *
     * @param phone the phone number to search for
     * @return an Optional containing the matching user, or empty if not found
     */
    Optional<User> findByPhone(String phone);

    /**
     * Checks whether a user with the given email already exists.
     *
     * @param email the email address to check
     * @return true if a user with this email exists
     */
    boolean existsByEmail(String email);

    /**
     * Checks whether a user with the given phone already exists.
     *
     * @param phone the phone number to check
     * @return true if a user with this phone exists
     */
    boolean existsByPhone(String phone);
}
