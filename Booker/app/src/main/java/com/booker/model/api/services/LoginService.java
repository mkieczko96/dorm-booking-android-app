package com.booker.model.api.services;

import com.booker.model.data.JwtToken;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface LoginService {
    @POST("auth")
    Call<JwtToken> authenticate(@Header("Authorization") String httpBasic);
}
