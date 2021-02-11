package com.booker.api.data;

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

    @Expose
    private String title;

    @Expose
    private String message;

    @Expose
    private Long triggerTime;
}