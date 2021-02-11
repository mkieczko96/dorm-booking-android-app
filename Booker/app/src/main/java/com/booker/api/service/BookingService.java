package com.booker.api.service;

import com.booker.data.models.Booking;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

public interface BookingService {
    @GET("bookings")
    Call<List<Booking>> findBookingsByUserId(@Header("Authorization") String bearer, @Query("user-id") long userId);

    @GET("bookings")
    Call<List<Booking>> findAllBookings(@Header("Authorization") String bearer, @QueryMap Map<String, String> queries);

    @POST("bookings")
    Call<Booking> saveBooking(@Header("Authorization") String bearer, @Body Booking newBooking);

    @DELETE("bookings/{id}")
    Call<String> deleteBookingById(@Header("Authorization") String bearer, @Path("id") long id);

    @PUT("bookings/{id}")
    Call<Booking> updateBooking(@Header("Authorization") String bearerToken, @Path("id") long id, @Body Booking booking);
}
