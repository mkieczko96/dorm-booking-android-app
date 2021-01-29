package com.booker.data.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
public class Facility {
    @Expose
    @SerializedName("id")
    private Long id;

    @Expose
    @SerializedName("adminId")
    private Long adminId;

    @Expose
    @SerializedName("name")
    private String name;

    @Expose
    @SerializedName("floor")
    private Long floor;

    @Expose
    @SerializedName("defaultBookingDuration")
    private Long defaultBookingDuration;
}
