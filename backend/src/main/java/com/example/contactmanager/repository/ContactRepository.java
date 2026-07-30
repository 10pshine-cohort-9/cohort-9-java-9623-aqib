package com.example.contactmanager.repository;

import com.example.contactmanager.entity.Contact;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data JPA repository for {@link Contact} entities.
 * Provides paginated listing and case-insensitive name search scoped to a user.
 */
@Repository
public interface ContactRepository extends JpaRepository<Contact, Long> {

    /**
     * Lists all contacts for a user, ordered by last name then first name.
     *
     * @param userId   the owner's id
     * @param pageable pagination parameters
     * @return a page of contacts belonging to the user
     */
    Page<Contact> findByUserIdOrderByLastNameAscFirstNameAsc(Long userId, Pageable pageable);

    /**
     * Case-insensitive search across first name, last name, and full name for a given owner.
     *
     * @param userId   the owner's id
     * @param query    the search term
     * @param pageable pagination parameters
     * @return a page of matching contacts
     */
    @Query("select c from Contact c where c.user.id = :userId "
            + "and (lower(c.firstName) like lower(concat('%', :query, '%')) "
            + "or lower(c.lastName) like lower(concat('%', :query, '%')) "
            + "or lower(concat(c.firstName, ' ', c.lastName)) like lower(concat('%', :query, '%')))")
    Page<Contact> searchByUserId(@Param("userId") Long userId,
                                @Param("query") String query,
                                Pageable pageable);

    /**
     * Finds a contact by id only if it belongs to the given user (ownership check).
     *
     * @param id     the contact id
     * @param userId the owner's id
     * @return an Optional containing the contact if found and owned by the user
     */
    Optional<Contact> findByIdAndUserId(Long id, Long userId);
}
