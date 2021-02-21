package com.booker.model.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.booker.model.api.ApiClient;
import com.booker.model.api.Resource;
import com.booker.model.api.services.BookingService;
import com.booker.model.data.Booking;

import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookingRepository {
    private final BookingService mBookingService;

    public BookingRepository() {
        mBookingService = ApiClient.getBookingsService();
    }

    public LiveData<Resource<Booking>> removeBooking(String jwtToken, long id) {
        final MutableLiveData<Resource<Booking>> data = new MutableLiveData<>();
        data.postValue(Resource.loading(null));
        mBookingService.deleteBookingById("Bearer " + jwtToken, id)
                .enqueue(new Callback<Booking>() {
                    @Override
                    public void onResponse(Call<Booking> call, Response<Booking> response) {
                        if(response.isSuccessful())
                            data.postValue(Resource.success(new Booking()));
                        else
                            data.postValue(Resource.error(response.message(), null));
                    }

                    @Override
                    public void onFailure(Call<Booking> call, Throwable t) {
                        data.postValue(Resource.error(t.getLocalizedMessage(), null));
                    }
                });
        return data;
    }

    public LiveData<Resource<List<Booking>>> getAllBookings(String jwtToken, Map<String, String> params) {
        final MutableLiveData<Resource<List<Booking>>> data = new MutableLiveData<>();
        data.postValue(Resource.loading(null));

        mBookingService.findAllBookings("Bearer " + jwtToken, params)
                .enqueue(new Callback<List<Booking>>() {
            @Override
            public void onResponse(@NotNull Call<List<Booking>> call, @NotNull Response<List<Booking>> response) {
                if(response.isSuccessful()) {
                    data.postValue(Resource.success(response.body()));
                } else {
                    data.postValue(Resource.error(response.message(), null));
                }
            }

            @Override
            public void onFailure(@NotNull Call<List<Booking>> call, @NotNull Throwable t) {
                data.postValue(Resource.error(t.getLocalizedMessage(), null));
            }
        });

        return data;
    }

    public LiveData<Resource<List<LocalDate>>> getAllBeginDates(String jwtToken, long userId) {
        final MutableLiveData<Resource<List<LocalDate>>> data = new MutableLiveData<>();
        data.postValue(Resource.loading(null));

        mBookingService.findAllBeginAtDates("Bearer " + jwtToken, userId)
                .enqueue(new Callback<List<Long>>() {
                    @Override
                    public void onResponse(@NotNull Call<List<Long>> call, @NotNull Response<List<Long>> response) {
                        if (response.isSuccessful()) {
                            List<LocalDate> dates = new ArrayList<>();
                            for (Long d : response.body()) {
                                LocalDate date = Instant.ofEpochSecond(d)
                                        .atZone(ZoneId.systemDefault())
                                        .toLocalDate();
                                if (!dates.contains(date)) dates.add(date);
                            }
                            dates.sort((o1, o2) -> (int) (o1.toEpochDay() - o2.toEpochDay()));
                            data.postValue(Resource.success(dates));
                        } else {
                            data.postValue(Resource.error(response.message(), null));
                        }
                    }

                    @Override
                    public void onFailure(@NotNull Call<List<Long>> call, @NotNull Throwable t) {
                        data.postValue(Resource.error(t.getLocalizedMessage(), null));
                    }
                });

        return data;
    }
}
