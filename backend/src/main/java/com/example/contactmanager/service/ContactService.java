package com.example.contactmanager.service;

import com.example.contactmanager.dto.ContactDetailResponse;
import com.example.contactmanager.dto.ContactRequest;
import com.example.contactmanager.dto.ContactResponse;
import com.example.contactmanager.dto.PageResponse;
import org.springframework.data.domain.Pageable;

public interface ContactService {

    ContactResponse create(Long userId, ContactRequest request);

    ContactDetailResponse update(Long userId, Long contactId, ContactRequest request);

    void delete(Long userId, Long contactId);

    ContactDetailResponse getById(Long userId, Long contactId);

    PageResponse<ContactResponse> list(Long userId, String search, Pageable pageable);
}
