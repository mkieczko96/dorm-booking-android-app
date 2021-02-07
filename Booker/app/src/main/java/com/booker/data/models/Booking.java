package com.booker.data.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Booking {
    private String imageURL;

    @Expose
    @SerializedName("id")
    private long id;

    @Expose
    @SerializedName("userId")
    private long userId;

    @Expose
    @SerializedName("facilityId")
    private long facilityId;

    @Expose
    @SerializedName("beginAt")
    private long beginAt;

    @Expose
    @SerializedName("endAt")
    private long endAt;

    @Expose
    @SerializedName("facility")
    private Facility facility;
}
