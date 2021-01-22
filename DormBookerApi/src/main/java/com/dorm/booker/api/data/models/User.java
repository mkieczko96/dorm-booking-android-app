package com.dormbooker.api.data.models;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "room", nullable = false)
    private Long room;

    @Column(name = "email_address", nullable = false)
    private String username;

    @Column(name = "password", nullable = false)
    private String password; //BCrypt hash of password

    @Column(name = "created_on", nullable = false)
    private Long createdOn; // unix timestamp

    @Column(name = "last_modified_on", nullable = false)
    private Long lastModifiedOn; // unix timestamp

    @Column(name = "expires_on", nullable = false)
    private Long expiresOn; //unix timestamp

    @Column(name = "account_expired")
    private Boolean accountNonExpired;

    @Column(name = "login_attempts")
    private Long incorrectLoginAttempts;

    @Column(name = "account_unlocked")
    private Boolean accountNonLocked;

    @Column(name = "credentials_changed_on")
    private Long credentialsUpdatedOn;

    @Column(name = "credentials_non_expired")
    private Boolean credentialsNonExpired;

    @Column(name = "enabled", nullable = false)
    private Boolean enabled;

    @ManyToMany(fetch = FetchType.EAGER)
    private List<Role> roles;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(r -> new SimpleGrantedAuthority(r.getName()))
                .collect(Collectors.toList());
    }

    @Override
    public boolean isAccountNonExpired() {
        return accountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return credentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}
