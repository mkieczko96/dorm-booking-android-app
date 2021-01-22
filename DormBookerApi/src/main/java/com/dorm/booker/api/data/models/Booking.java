package com.dormbooker.api.data.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import javax.persistence.*;

@Getter @Setter
@Entity @Table(name = "bookings")
public class Booking {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false, updatable = true, insertable = true)
    private Long userId;

    @Column(name = "begin_at", nullable = false)
    private Long beginAt; // unix timestamp - milliseconds elapsed since 1970-01-01 00:00

    @Column(name = "duration", nullable = false)
    private Long durationInMinutes;

    @ManyToOne
    @JoinColumn(name = "facility_id", nullable = false)
    @JsonIgnoreProperties("bookings")
    Facility facility;
}
