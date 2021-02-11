package com.dorm.booker.api.data.models;

import com.google.gson.annotations.Expose;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "facilities")
public class Facility {

    @Id
    @Expose
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Expose
    @Column(name = "admin_id", updatable = false, insertable = false)
    private Long adminId;

    @Expose
    @Column(name = "name", nullable = false)
    private String name;

    @Expose
    @Column(name = "floor", nullable = false)
    private Long floor;

    @Expose
    @Column(name = "default_duration", nullable = false)
    private Long defaultBookingDuration;
}
