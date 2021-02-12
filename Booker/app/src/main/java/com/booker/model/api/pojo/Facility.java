package com.booker.model.api.pojo;

import com.google.gson.annotations.Expose;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Facility {
    @Expose
    private Long id;

    @Expose
    private Long adminId;

    @Expose
    private String name;

    @Expose
    private Long floor;

    @Expose
    private Long defaultBookingDuration;

    @Expose
    private String imageUrl;
}
