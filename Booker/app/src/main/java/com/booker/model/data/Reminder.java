package com.booker.model.data;

import com.google.gson.annotations.Expose;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Reminder {
    @Expose
    private Long id;

    @Expose
    private Long bookingId;

    @Expose
    private String label;

    private int duration;

    @Expose
    private String title;

    @Expose
    private String message;

    @Expose
    private Long triggerTime;

    @Expose
    private String timezone;

    public Reminder(int seconds, String option) {
        duration = seconds;
        label = option;
    }
}