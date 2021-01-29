package com.booker.api;

import com.booker.api.service.BookingService;
import com.booker.api.service.LoginService;
import com.booker.api.service.UserService;
import com.booker.data.models.Booking;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class ApiClient {
    private static final String BASE_URL = "http://10.0.2.2:8080/api/";

    private static Retrofit getRetrofit() {
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .addInterceptor(httpLoggingInterceptor)
                .build();

        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();
    }

    public static LoginService getLoginService() {
        return getRetrofit().create(LoginService.class);
    }

    public static BookingService getBookingsService() {
        return getRetrofit().create(BookingService.class);
    }

    public static UserService getUserService() {
        return getRetrofit().create(UserService.class);
    }
}
