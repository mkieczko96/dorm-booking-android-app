package com.dormbooker.api.data.models;

import lombok.*;

import javax.persistence.*;

@Getter @Setter
@Entity @Table(name = "bookings")
public class Booking {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "facility_id", nullable = false, updatable = false, insertable = false)
    private Long facilityId;

    @Column(name = "user_id", nullable = false, updatable = false, insertable = false)
    private Long userId;

    @Column(name = "begin_at", nullable = false)
    private Long beginAt; // unix timestamp - milliseconds elapsed since 1970-01-01 00:00

    @Column(name = "duration", nullable = false)
    private Long durationInMinutes;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "facility_id", referencedColumnName = "id")
    private Facility facility;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;
}
