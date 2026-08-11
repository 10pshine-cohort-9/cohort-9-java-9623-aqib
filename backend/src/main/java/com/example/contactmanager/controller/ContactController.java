package com.example.contactmanager.controller;

import com.example.contactmanager.dto.ApiResponse;
import com.example.contactmanager.dto.ContactDetailResponse;
import com.example.contactmanager.dto.ContactRequest;
import com.example.contactmanager.dto.ContactResponse;
import com.example.contactmanager.dto.PageResponse;
import com.example.contactmanager.security.SecurityUtils;
import com.example.contactmanager.service.ContactService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/contacts")
public class ContactController {

    private static final Logger log = LoggerFactory.getLogger(ContactController.class);
    private static final int MAX_PAGE_SIZE = 100;

    private final ContactService contactService;

    public ContactController(ContactService contactService) {
        this.contactService = contactService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ContactResponse>> create(@Valid @RequestBody ContactRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        log.info("POST /api/contacts - user id={}", userId);
        ContactResponse response = contactService.create(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Contact created", response));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ContactDetailResponse>> update(
            @PathVariable Long id, @Valid @RequestBody ContactRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        log.info("PUT /api/contacts/{} - user id={}", id, userId);
        ContactDetailResponse response = contactService.update(userId, id, request);
        return ResponseEntity.ok(ApiResponse.success("Contact updated", response));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        Long userId = SecurityUtils.getCurrentUserId();
        log.info("DELETE /api/contacts/{} - user id={}", id, userId);
        contactService.delete(userId, id);
        return ResponseEntity.ok(ApiResponse.message("Contact deleted"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ContactDetailResponse>> getById(@PathVariable Long id) {
        Long userId = SecurityUtils.getCurrentUserId();
        log.info("GET /api/contacts/{} - user id={}", id, userId);
        ContactDetailResponse response = contactService.getById(userId, id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<ContactResponse>>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search) {
        Long userId = SecurityUtils.getCurrentUserId();
        int safeSize = Math.min(Math.max(size, 1), MAX_PAGE_SIZE);
        int safePage = Math.max(page, 0);
        log.info("GET /api/contacts - user id={} page={} size={} search='{}'", userId, safePage, safeSize, search);
        Pageable pageable = PageRequest.of(safePage, safeSize,
                Sort.by("lastName", "firstName").ascending());
        PageResponse<ContactResponse> response = contactService.list(userId, search, pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
