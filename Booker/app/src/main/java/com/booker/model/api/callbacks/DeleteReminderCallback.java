package com.booker.model.api.callbacks;

import com.booker.model.data.Reminder;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class DeleteReminderCallback implements Callback<Reminder> {

    @Override
    public void onResponse(Call<Reminder> call, Response<Reminder> response) {

    }

    @Override
    public void onFailure(Call<Reminder> call, Throwable t) {

    }
}
