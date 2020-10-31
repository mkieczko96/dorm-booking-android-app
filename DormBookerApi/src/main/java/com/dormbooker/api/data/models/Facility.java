package com.dormbooker.api.data.models;

import lombok.*;

import javax.persistence.*;
import java.util.List;

@Getter @Setter
@Entity @Table(name = "facilities")
public class Facility {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "admin_id", nullable = true, updatable = false, insertable = false)
    private Long adminId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "floor", nullable = false)
    private Long floor;

    @Column(name = "default_duration", nullable = false)
    private Long defaultBookingDuration;

    @OneToOne
    @JoinColumn(name = "admin_id", referencedColumnName = "id")
    private User admin;

    @OneToMany(mappedBy = "facility")
    private List<Booking> bookings;
}
