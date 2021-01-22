package com.dorm.booker.api.data.models;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "facilities")
public class Facility {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "admin_id", updatable = false, insertable = false)
    private Long adminId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "floor", nullable = false)
    private Long floor;

    @Column(name = "default_duration", nullable = false)
    private Long defaultBookingDuration;
}
