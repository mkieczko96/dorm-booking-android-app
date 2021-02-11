package com.dorm.booker.api.data.models;


import com.google.gson.annotations.Expose;
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
    @Expose
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Expose
    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Expose
    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Expose
    @Column(name = "room", nullable = false)
    private Long room;

    @Expose
    @Column(name = "email_address", nullable = false)
    private String username;

    @Expose
    @Column(name = "password", nullable = false)
    private String password; //BCrypt hash of password

    @Expose
    @Column(name = "created_on", nullable = false)
    private Long createdOn; // unix timestamp

    @Expose
    @Column(name = "last_modified_on", nullable = false)
    private Long lastModifiedOn; // unix timestamp

    @Expose
    @Column(name = "expires_on", nullable = false)
    private Long expiresOn; //unix timestamp

    @Expose
    @Column(name = "account_expired")
    private Boolean accountNonExpired;

    @Expose
    @Column(name = "login_attempts")
    private Long incorrectLoginAttempts;

    @Expose
    @Column(name = "account_unlocked")
    private Boolean accountNonLocked;

    @Expose
    @Column(name = "credentials_changed_on")
    private Long credentialsUpdatedOn;

    @Expose
    @Column(name = "credentials_non_expired")
    private Boolean credentialsNonExpired;

    @Expose
    @Column(name = "enabled", nullable = false)
    private Boolean enabled;

    @Expose
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
