package com.booker.model.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.booker.model.api.ApiClient;
import com.booker.model.api.Resource;
import com.booker.model.api.services.UserService;
import com.booker.model.data.User;

import org.jetbrains.annotations.NotNull;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserRepository {
    private final UserService mUserService;

    public UserRepository() {
        mUserService = ApiClient.getUserService();
    }

    public LiveData<Resource<User>> getCurrentUser(String jwtToken) {
        MutableLiveData<Resource<User>> data = new MutableLiveData<>();
        data.postValue(Resource.loading(null));

        mUserService.getCurrentUser("Bearer " + jwtToken).enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NotNull Call<User> call, @NotNull Response<User> response) {
                if (response.isSuccessful())
                    data.postValue(Resource.success(response.body()));
                else
                    data.postValue(Resource.error(response.message(), null));
            }

            @Override
            public void onFailure(@NotNull Call<User> call, @NotNull Throwable t) {
                data.postValue(Resource.error(t.getLocalizedMessage(), null));
            }
        });

        return data;
    }
}
