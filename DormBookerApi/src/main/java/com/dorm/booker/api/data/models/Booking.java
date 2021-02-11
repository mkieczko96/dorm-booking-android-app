package com.dorm.booker.api.data.models;

import com.google.gson.annotations.Expose;
import lombok.*;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "bookings")
public class Booking {

    @Expose
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", insertable = false, updatable = false)
    private Long userId;

    @Expose
    @Column(name = "begin_at", nullable = false)
    private Long beginAt;

    @Expose
    @Column(name = "end_at", nullable = false)
    private Long endAt;

    @Column(name = "facility_id", insertable = false, updatable = false)
    private Long facilityId;

    @Expose
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "booking")
    private List<Reminder> reminders;

    @Expose
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Expose
    @ManyToOne
    @JoinColumn(name = "facility_id", nullable = false)
    private Facility facility;
}
