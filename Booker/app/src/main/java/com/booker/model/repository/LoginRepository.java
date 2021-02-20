package com.booker.model.repository;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.booker.model.api.ApiClient;
import com.booker.model.api.Resource;
import com.booker.model.api.services.LoginService;
import com.booker.model.data.JwtToken;

import org.jetbrains.annotations.NotNull;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginRepository {
    private final LoginService mLoginService;

    public LoginRepository() {
        mLoginService = ApiClient.getLoginService();
    }

    public LiveData<Resource<JwtToken>> authenticate(String base64Credentials) {
        final MutableLiveData<Resource<JwtToken>> data = new MediatorLiveData<>();
        data.postValue(Resource.loading(null));

        mLoginService.authenticate("Basic " + base64Credentials).enqueue(new Callback<JwtToken>() {
            @Override
            public void onResponse(@NotNull Call<JwtToken> call, @NotNull Response<JwtToken> response) {
                if (response.isSuccessful()) {
                    JwtToken jwtToken = response.body();
                    data.postValue(Resource.success(jwtToken));
                } else {
                    data.postValue(Resource.error(response.message(), null));
                }
            }

            @Override
            public void onFailure(@NotNull Call<JwtToken> call, @NotNull Throwable t) {
                data.postValue(Resource.error(t.getLocalizedMessage(), null));
            }
        });

        return data;
    }
}
