package com.booker.model.api.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Booking {

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
    private String timezone;

    @Expose
    @SerializedName("facility")
    private Facility facility;

    @Expose
    private List<Reminder> reminders;

    private User user;
}
