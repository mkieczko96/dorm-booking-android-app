package com.booker.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.booker.R;
import com.booker.Utility;
import com.booker.model.api.ApiClient;
import com.booker.model.api.pojo.Booking;
import com.booker.model.api.pojo.Facility;
import com.booker.model.api.pojo.User;
import com.booker.databinding.DialogFacilityCalendarBinding;
import com.booker.ui.BookingDates;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;
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

    private Facility mSelectedFacility;
    private User mUser;
    private DialogFacilityCalendarBinding mBinding;
    private LocalDate mSelectedDate;
    private int mViewItemsCount;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_facility_calendar, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mBinding = DialogFacilityCalendarBinding.bind(view);
        mBinding.previousDay.setOnClickListener(v -> setSelectedDate(mSelectedDate.minusDays(1)));
        mBinding.nextDay.setOnClickListener(v -> setSelectedDate(mSelectedDate.plusDays(1)));

        createTimeSpans();
        setSelectedDate(mSelectedDate);
    }

    public static DialogFacilityCalendar newInstance(User user, Facility facility, LocalDate date) {
        DialogFacilityCalendar dialog = new DialogFacilityCalendar();
        dialog.mSelectedFacility = facility;
        dialog.mSelectedDate = date;
        dialog.mUser = user;
        return dialog;
    }

    private void setSelectedDate(LocalDate date) {
        mSelectedDate = date;
        SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        SimpleDateFormat formatter = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());
        try {
            mBinding.eventDay.setText(formatter.format(parser.parse(mSelectedDate.toString())));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        redrawCalendar();
    }

    private void redrawCalendar() {
        clearEvents();
        Call<List<Booking>> call = ApiClient.getBookingsService()
                .findAllBookings(
                        getBearerToken(),
                        getRequestQueries()
                );
        call.enqueue(new BookingListCallback());
    }

    private void clearEvents() {
        mBinding.facilityDayView.removeViews(
                mViewItemsCount,
                mBinding.facilityDayView.getChildCount() - mViewItemsCount
        );
    }

    private Map<String, String> getRequestQueries() {
        Map<String, String> queries = new HashMap<>();
        queries.put("facility-id", String.valueOf(mSelectedFacility.getId()));
        queries.put("between", String.valueOf(mSelectedDate
                .atStartOfDay(ZoneId.systemDefault())
                .toEpochSecond()));
        queries.put("and", String.valueOf(mSelectedDate.atTime(23, 59)
                .atZone(ZoneId.systemDefault())
                .toEpochSecond()));
        return queries;
    }

    private void createTimeSpans() {
        int defaultDuration = mSelectedFacility.getDefaultBookingDuration().intValue() / 60;
        for (int i = 0; i < 24; i += defaultDuration) {
            GridLayout.LayoutParams params = getLayoutParams(requireContext(), i, defaultDuration);

            View view = new View(getContext());
            view.setLayoutParams(params);
            view.setBackgroundResource(R.drawable.item_event_slot_bg);
            view.setClickable(true);
            view.setFocusable(true);
            view.setOnClickListener(this::onEmptyEventSpotClick);

            mBinding.facilityDayView.addView(view, 0);
        }
        mViewItemsCount = mBinding.facilityDayView.getChildCount();
    }

    private void onEmptyEventSpotClick(View view) {
        int defaultDuration = mSelectedFacility.getDefaultBookingDuration().intValue() / 60;
        int row = (24 / defaultDuration - 1) - mBinding.facilityDayView.indexOfChild(view);
        int hour = row * defaultDuration;
        LocalDateTime beginAt = LocalDateTime.of(mSelectedDate, LocalTime.of(hour, 0));
        LocalDateTime endAt = LocalDateTime.of(mSelectedDate, LocalTime.of(hour + defaultDuration, 0));
        EventBus.getDefault().post(new BookingDates(beginAt, endAt));
        dismiss();
    }

    private String getBearerToken() {
        return "Bearer " + requireActivity()
                .getSharedPreferences(getString(R.string.pref_file), Context.MODE_PRIVATE)
                .getString("dorm.booker.jwt", null);
    }

    private void drawEvents(List<Booking> bookings) {
        SimpleDateFormat hour = new SimpleDateFormat("H", Locale.getDefault());
        Context context = requireContext();

        for (Booking b : bookings) {
            Date startDateTime = new Date(b.getBeginAt() * 1000);
            Date endDateTime = new Date(b.getEndAt() * 1000);

            int startHour = Integer.parseInt(hour.format(startDateTime));
            int endHour = Integer.parseInt(hour.format(endDateTime));

            int bookingDuration = endHour - startHour;

            GridLayout.LayoutParams eventParams = getLayoutParams(context, startHour, bookingDuration);
            TextView room = getRoomTextView(context, b.getUser().getRoom());
            FrameLayout event = getEventLayout(context, eventParams, room);

            mBinding.facilityDayView.addView(event);
        }
    }

    private GridLayout.LayoutParams getLayoutParams(Context context, int startHour, int bookingDuration) {
        GridLayout.LayoutParams eventParams = new GridLayout.LayoutParams();
        eventParams.width = 0;
        eventParams.height = (int) Utility.dpToPx(context, 60F * bookingDuration);
        eventParams.rowSpec = GridLayout.spec(startHour, bookingDuration);
        eventParams.columnSpec = GridLayout.spec(1, 1, 1);
        return eventParams;
    }

    private FrameLayout getEventLayout(Context context, GridLayout.LayoutParams eventParams, TextView room) {
        FrameLayout event = new FrameLayout(context);
        event.setPadding((int) Utility.dpToPx(context, 8), (int) Utility.dpToPx(context, 8), 0, 0);
        event.setLayoutParams(eventParams);
        event.setBackgroundResource(R.color.purple_200);
        event.setClickable(false);
        event.setFocusable(false);
        event.setElevation(Utility.dpToPx(context, 8));
        event.bringToFront();
        event.addView(room);
        event.bringChildToFront(room);
        return event;
    }

    private TextView getRoomTextView(Context context, Long room) {
        TextView view = new TextView(context);
        view.setWidth(GridLayout.LayoutParams.WRAP_CONTENT);
        view.setHeight(GridLayout.LayoutParams.WRAP_CONTENT);
        view.setTextColor(Color.BLACK);
        view.setText(getString(R.string.msg_room, room));
        return view;
    }

    class BookingListCallback implements Callback<List<Booking>> {

        @Override
        public void onResponse(@NotNull Call<List<Booking>> call, Response<List<Booking>> response) {
            if (response.isSuccessful()) {
                List<Booking> bookings = response.body() != null ? response.body() : new ArrayList<>();
                drawEvents(bookings);
            } else {
                try {
                    Log.e("CALL", call.request().toString());

                    assert response.errorBody() != null;
                    Log.e("CALL", response.errorBody().string());
                } catch (NullPointerException | IOException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onFailure(Call<List<Booking>> call, Throwable t) {
            Log.e("CALL", call.request().toString());
            Log.e("CALL", t.getLocalizedMessage());
        }
    }
}