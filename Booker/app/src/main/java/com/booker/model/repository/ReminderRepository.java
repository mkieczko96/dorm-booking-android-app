package com.booker.model.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.booker.model.api.ApiClient;
import com.booker.model.api.Resource;
import com.booker.model.api.services.ReminderService;
import com.booker.model.data.Reminder;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReminderRepository {
    private final ReminderService mReminderService;

    public ReminderRepository() {
        mReminderService = ApiClient.getReminderService();
    }

    public LiveData<Resource<Reminder>> remove(String jwtToken, long id) {
        final MutableLiveData<Resource<Reminder>> data = new MutableLiveData<>();
        data.postValue(Resource.loading(null));

        mReminderService.remove("Bearer " + jwtToken, id)
                .enqueue(new Callback<Reminder>() {
                    @Override
                    public void onResponse(Call<Reminder> call, Response<Reminder> response) {
                        if(response.isSuccessful())
                            data.postValue(Resource.success(new Reminder()));
                        else
                            data.postValue(Resource.error(response.message(), null));
                    }

                    @Override
                    public void onFailure(Call<Reminder> call, Throwable t) {
                        data.postValue(Resource.error(t.getLocalizedMessage(), null));
                    }
                });
        return data;
    }

}
