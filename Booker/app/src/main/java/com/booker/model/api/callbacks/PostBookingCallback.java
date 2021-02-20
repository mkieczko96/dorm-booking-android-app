package com.booker.model.api.callbacks;

import android.util.Log;

import com.booker.model.data.Booking;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostBookingCallback implements Callback<Booking> {

    @Override
    public void onResponse(@NotNull Call<Booking> call, Response<Booking> response) {
        if (response.isSuccessful()) {
            Booking savedBooking = response.body();
            EventBus.getDefault().post(savedBooking);
        }
    }

    @Override
    public void onFailure(@NotNull Call<Booking> call, Throwable t) {
        Log.e("POST", t.getLocalizedMessage());
    }
}
