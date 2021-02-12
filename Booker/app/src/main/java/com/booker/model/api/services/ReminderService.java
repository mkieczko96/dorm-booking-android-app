package com.booker.model.api.services;

import com.booker.model.api.pojo.Reminder;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ReminderService {
    @GET("reminders/")
    Call<List<Reminder>> findAll(@Header("Authorization") String bearer);

    @GET("reminders/")
    Call<List<Reminder>> findAllByBooking(@Header("Authorization") String bearer, @Query("booking-id") long bookingId);

    @PUT("reminders/{id}")
    Call<Reminder> save(@Header("Authorization") String bearer, @Path("id") long id, @Body Reminder updated);

    @POST("reminders/")
    Call<Reminder> save(@Header("Authorization") String bearer, @Body Reminder reminder);

    @DELETE("reminders/{id}")
    Call<Reminder> remove(@Header("Authorization") String bearer, @Path("id") long id);
}
