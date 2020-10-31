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

    @OneToMany(mappedBy = "user")
    private List<Booking> bookings;

    // add some account setting
    // all user device list

    // Change it to credentials class or something
    @Column(name = "email_address", nullable = false)
    private String emailAddress;

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
