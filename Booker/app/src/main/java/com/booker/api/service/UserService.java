package com.booker.api.service;

import com.booker.data.models.User;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

public interface UserService {
    @GET("users/me")
    Call<User> getCurrentUser(@Header("Authorization") String bearer);
}
