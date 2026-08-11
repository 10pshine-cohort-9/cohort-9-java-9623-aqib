package com.example.contactmanager.security;

import com.example.contactmanager.entity.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * Spring Security {@link UserDetails} adapter wrapping a {@link User} entity.
 * The identifier (email or phone) is exposed as the username.
 */
@Getter
public class CustomUserDetails implements UserDetails {

    private final Long id;
    private final String username;
    private final String password;
    private final Collection<? extends GrantedAuthority> authorities;

    public CustomUserDetails(User user) {
        this.id = user.getId();
        this.username = resolveIdentifier(user);
        this.password = user.getPassword();
        this.authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }

    private static String resolveIdentifier(User user) {
        if (user.getEmail() != null && !user.getEmail().isBlank()) {
            return user.getEmail();
        }
        return user.getPhone();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
