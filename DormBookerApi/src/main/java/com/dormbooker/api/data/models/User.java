package com.dormbooker.api.data.models;


import lombok.*;

import javax.persistence.*;
import java.util.List;

@Getter @Setter
@NoArgsConstructor
@Entity @Table(name = "users")
public class User {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "room", nullable = false)
    private Long room;

    @OneToMany
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private List<Booking> bookings;

    // TODO: Add some account setting
    // TODO: Add all user device list

    // TODO: Change it to credentials class or something
    @Column(name = "email_address", nullable = false)
    private String emailAddress;

    // TODO: Change it to credentials class or something
    // TODO: Hash the password
    @Column(name = "password", nullable = false)
    private String password;

    // administrative data
    @Column(name = "created_on", nullable = false)
    private Long createdOn; // unix timestamp

    @Column(name = "last_modified_on", nullable = false)
    private Long lastModifiedOn; // unix timestamp

    @Column(name = "expires_on", nullable = false)
    private Long expiresOn; //unix timestamp

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;
}
