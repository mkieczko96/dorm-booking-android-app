package com.booker.api.services.callbacks;

import android.util.Log;

import com.booker.api.data.Reminder;

import org.jetbrains.annotations.NotNull;

import lombok.SneakyThrows;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostReminderCallback implements Callback<Reminder> {
    @SneakyThrows
    @Override
    public void onResponse(@NotNull Call<Reminder> call, Response<Reminder> response) {
        if (response.isSuccessful()) {
            Log.i("POST", "Reminder saved successfully.");
        } else {
            Log.e("POST", response.errorBody().string());
        }
    }

    @Override
    public void onFailure(@NotNull Call<Reminder> call, Throwable t) {
        Log.e("POST", t.getLocalizedMessage());
        t.printStackTrace();
    }
}
