package com.example.contactmanager.controller;

import com.example.contactmanager.config.TestSecurityConfig;
import com.example.contactmanager.dto.ContactDetailResponse;
import com.example.contactmanager.dto.ContactRequest;
import com.example.contactmanager.dto.ContactResponse;
import com.example.contactmanager.dto.EmailDto;
import com.example.contactmanager.dto.PageResponse;
import com.example.contactmanager.dto.PhoneDto;
import com.example.contactmanager.exception.GlobalExceptionHandler;
import com.example.contactmanager.exception.ResourceNotFoundException;
import com.example.contactmanager.security.CustomUserDetailsService;
import com.example.contactmanager.security.JwtUtil;
import com.example.contactmanager.security.CustomUserDetails;
import com.example.contactmanager.service.ContactService;
import com.example.contactmanager.entity.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ContactController.class)
@Import({GlobalExceptionHandler.class, TestSecurityConfig.class})
class ContactControllerTest {

    private static final Long USER_ID = 1L;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ContactService contactService;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @BeforeEach
    void setUpSecurityContext() {
        User user = User.builder().id(USER_ID).email("jane@example.com")
                .password("hash").firstName("Jane").lastName("Doe").build();
        CustomUserDetails details = new CustomUserDetails(user);
        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(details, null, details.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("create returns 201 on valid payload")
    void createValid() throws Exception {
        ContactRequest request = ContactRequest.builder()
                .firstName("John").lastName("Smith").title("Engineer")
                .emails(List.of(EmailDto.builder().value("john@x.com").label("WORK").build()))
                .phones(List.of(PhoneDto.builder().value("+923001234567").label("PERSONAL").build()))
                .build();
        ContactResponse response = ContactResponse.builder()
                .id(10L).firstName("John").lastName("Smith").title("Engineer").build();
        when(contactService.create(eq(USER_ID), any(ContactRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/contacts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.id").value(10))
                .andExpect(jsonPath("$.data.firstName").value("John"));
    }

    @Test
    @DisplayName("create returns 400 when first name is blank")
    void createInvalid() throws Exception {
        ContactRequest request = ContactRequest.builder()
                .firstName("").lastName("Smith").build();

        mockMvc.perform(post("/api/contacts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("update returns 200 on valid payload")
    void updateValid() throws Exception {
        ContactRequest request = ContactRequest.builder()
                .firstName("John").lastName("Smith").build();
        ContactDetailResponse response = ContactDetailResponse.builder()
                .id(10L).firstName("John").lastName("Smith")
                .emails(List.of()).phones(List.of()).build();
        when(contactService.update(eq(USER_ID), eq(10L), any(ContactRequest.class))).thenReturn(response);

        mockMvc.perform(put("/api/contacts/10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(10));
    }

    @Test
    @DisplayName("update returns 404 when contact not found")
    void updateNotFound() throws Exception {
        ContactRequest request = ContactRequest.builder()
                .firstName("John").lastName("Smith").build();
        when(contactService.update(eq(USER_ID), eq(99L), any(ContactRequest.class)))
                .thenThrow(new ResourceNotFoundException("Contact", 99L));

        mockMvc.perform(put("/api/contacts/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("delete returns 200 on success")
    void deleteValid() throws Exception {
        doNothing().when(contactService).delete(USER_ID, 10L);

        mockMvc.perform(delete("/api/contacts/10"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("getById returns 200 with contact detail")
    void getByIdValid() throws Exception {
        ContactDetailResponse response = ContactDetailResponse.builder()
                .id(10L).firstName("John").lastName("Smith")
                .emails(List.of(EmailDto.builder().value("john@x.com").label("WORK").build()))
                .phones(List.of()).build();
        when(contactService.getById(USER_ID, 10L)).thenReturn(response);

        mockMvc.perform(get("/api/contacts/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.emails[0].label").value("WORK"));
    }

    @Test
    @DisplayName("list returns paginated contacts")
    void listValid() throws Exception {
        PageResponse<ContactResponse> page = PageResponse.of(
                new PageImpl<>(List.of(
                        ContactResponse.builder().id(10L).firstName("John").lastName("Smith").build()),
                        PageRequest.of(0, 10), 1));
        when(contactService.list(eq(USER_ID), any(), any())).thenReturn(page);

        mockMvc.perform(get("/api/contacts").param("page", "0").param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.totalElements").value(1));
    }

    @Test
    @DisplayName("list forwards search term")
    void listWithSearch() throws Exception {
        PageResponse<ContactResponse> page = PageResponse.of(
                new PageImpl<>(List.of(), PageRequest.of(0, 10), 0));
        when(contactService.list(eq(USER_ID), eq("john"), any())).thenReturn(page);

        mockMvc.perform(get("/api/contacts").param("search", "john"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.totalElements").value(0));
    }
}
