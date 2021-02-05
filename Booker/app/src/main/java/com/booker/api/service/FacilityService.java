package com.booker.api.service;

import com.booker.data.models.Booking;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface FacilityService {
    @GET("bookings")
    Call<List<Booking>> findBookingsByUserId(@Header("Authorization") String bearer, @Query("user-id") long userId);

    @GET("bookings")
    Call<List<Booking>> findBookingsByUserId(@Header("Authorization") String bearer, @Query("user-id") long userId, @Query("begin-after") long date);

    @DELETE("bookings/{id}")
    Call<String> deleteBookingById(@Header("Authorization") String bearer, @Path("id") long id);
}
