package com.example.contactmanager.repository;

import com.example.contactmanager.entity.Contact;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ContactRepository extends JpaRepository<Contact, Long> {

    Page<Contact> findByUserIdOrderByLastNameAscFirstNameAsc(Long userId, Pageable pageable);

    /**
     * Case-insensitive search across first name and last name for a given owner.
     */
    @Query("select c from Contact c where c.user.id = :userId "
            + "and (lower(c.firstName) like lower(concat('%', :query, '%')) "
            + "or lower(c.lastName) like lower(concat('%', :query, '%')) "
            + "or lower(concat(c.firstName, ' ', c.lastName)) like lower(concat('%', :query, '%')))")
    Page<Contact> searchByUserId(@Param("userId") Long userId,
                                @Param("query") String query,
                                Pageable pageable);

    Optional<Contact> findByIdAndUserId(Long id, Long userId);
}
