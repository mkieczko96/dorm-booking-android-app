package com.booker.api.services;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface LoginService {
    @POST("auth")
    Call<Map<Object, Object>> authenticate(@Header("Authorization") String httpBasic);
}
