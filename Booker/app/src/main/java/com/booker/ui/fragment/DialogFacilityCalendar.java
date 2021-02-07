package com.booker.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.booker.R;
import com.booker.api.ApiClient;
import com.booker.data.models.Booking;
import com.booker.data.models.Facility;
import com.booker.databinding.DialogFacilityCalendarBinding;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import lombok.NoArgsConstructor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@NoArgsConstructor
public class DialogFacilityCalendar extends DialogFragment {

    private Facility selectedFacility;
    private long userId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_facility_calendar, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        DialogFacilityCalendarBinding binding = DialogFacilityCalendarBinding.bind(view);

        Map<String, String> queries = new HashMap<>();
        queries.put("facility-id", String.valueOf(selectedFacility.getId()));
        queries.put("between", String.valueOf(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toEpochSecond()));
        queries.put("and", String.valueOf(LocalDate.now().atTime(23, 59).toEpochSecond(ZoneOffset.UTC)));
        Call<List<Booking>> call = ApiClient.getBookingsService()
                .findAllBookings(
                        "Bearer " + getToken(),
                        queries
                );
        call.enqueue(new Callback<List<Booking>>() {
            @Override
            public void onResponse(Call<List<Booking>> call, Response<List<Booking>> response) {
                if (response.isSuccessful()) {
                    List<Booking> bookings = response.body();
                    SimpleDateFormat hour = new SimpleDateFormat("h", Locale.ENGLISH);
                    for (Booking b : bookings) {
                        Object key;
                        int start = Integer.parseInt(hour.format(b.getBeginAt()));
                        int end =Integer.parseInt( hour.format(b.getEndAt()));
                        int duration = end - start;
                        // TODO: Create selectable views spanning default durations
                        //       and create all pulled events on list.
                    }
                } else {
                    try {
                        Log.e("DEB", response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Booking>> call, Throwable t) {
                Log.e("DEB", t.getLocalizedMessage());
            }
        });
    }

    public static DialogFacilityCalendar newInstance(Facility facility) {
        DialogFacilityCalendar dialog = new DialogFacilityCalendar();
        dialog.selectedFacility = facility;
        return dialog;
    }

    private String getToken() {
        Activity activity = getActivity();

        assert activity != null;
        return activity.getSharedPreferences(getString(R.string.pref_file), Context.MODE_PRIVATE)
                .getString("dorm.booker.jwt", null);
    }
}