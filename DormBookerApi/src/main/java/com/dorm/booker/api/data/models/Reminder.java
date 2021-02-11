package com.dorm.booker.api.data.models;

import com.google.gson.annotations.Expose;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "reminders")
public class Reminder {

    @Id
    @Expose
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Expose
    @Column(name = "booking_id", nullable = false)
    private Long bookingId;

    @ManyToOne
    @JoinColumn(name = "booking_id", referencedColumnName = "id", updatable = false, insertable = false)
    private Booking booking;

    @Expose
    @Column(name = "trigger_time", nullable = false)
    private Long triggerTime;

    @Expose
    @Column(nullable = false)
    private String title;

    @Expose
    @Column(nullable = false)
    private String message;

    @Expose
    @Column(nullable = false)
    private String label;
}
