package com.booker.api.services;

import com.booker.api.data.Facility;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

public interface FacilityService {
    @GET("facilities")
    Call<List<Facility>> findAllFacilities(@Header("Authorization") String bearer);
}
