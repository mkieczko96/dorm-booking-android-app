package com.booker.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.booker.R;
import com.booker.api.ApiClient;
import com.booker.data.models.Booking;
import com.booker.data.models.User;
import com.booker.databinding.FragmentHomeBinding;
import com.booker.databinding.ViewCalendarDayBinding;
import com.booker.databinding.ViewCalendarMonthBinding;
import com.booker.ui.adapter.BookingsItemAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.kizitonwose.calendarview.CalendarView;
import com.kizitonwose.calendarview.model.CalendarDay;
import com.kizitonwose.calendarview.model.CalendarMonth;
import com.kizitonwose.calendarview.ui.DayBinder;
import com.kizitonwose.calendarview.ui.MonthHeaderFooterBinder;
import com.kizitonwose.calendarview.ui.ViewContainer;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.jetbrains.annotations.NotNull;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import kotlin.Unit;
import lombok.NoArgsConstructor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@NoArgsConstructor
public class HomeFragment extends Fragment {
    private final LocalDate today = LocalDate.now();
    private FragmentHomeBinding binding;
    private CalendarView calendarView;
    private User mUser;
    private LocalDate mSelectedDate = null;
    private BookingsItemAdapter adapter;
    private HashMap<LocalDate, List<Booking>> bookings = new HashMap<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding = FragmentHomeBinding.bind(view);
        calendarView = binding.calendarView;
        ListView calendarEventsView = binding.eventList;
        FloatingActionButton fab = binding.fabCreateBooking;
        bookings.clear();
        getUserBookings();
        adapter = new BookingsItemAdapter(getContext());
        calendarEventsView.setAdapter(adapter);
        setCalendarView(calendarView);
        fab.setOnClickListener(this::onFabClick);
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    public static HomeFragment newInstance(User user) {
        HomeFragment newFragment = new HomeFragment();
        newFragment.mUser = user;
        return newFragment;
    }

    @Subscribe
    public void onBookingCreated(Booking newBooking) {
    }

    private void onFabClick(View view) {
        CreateBookingFragment fragment = CreateBookingFragment.newInstance(mUser, mSelectedDate);
        Activity activity = getActivity();

        assert activity != null;
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_placeholder, fragment, fragment.getClass().getSimpleName())
                .addToBackStack(fragment.getClass().getSimpleName())
                .commit();
    }

    private void setCalendarView(CalendarView calendarView) {
        calendarView.setup(YearMonth.of(2021, 1), YearMonth.of(2021, 12), DayOfWeek.MONDAY);
        calendarView.setDayBinder(new DayViewBinder());
        calendarView.setMonthHeaderBinder(new MonthViewBinder());
        calendarView.setMonthScrollListener(this::onMonthScroll);
        calendarView.scrollToDate(today.minusDays(3)); // set to beginning of the Month to make smooth scroll shorter
    }

    private Unit onMonthScroll(CalendarMonth calendarMonth) {
        Toolbar toolbar = requireActivity().findViewById(R.id.app_bar);
        toolbar.setTitle(DateTimeFormatter.ofPattern("MMMM yyyy")
                .format(calendarMonth.getYearMonth()));
        return Unit.INSTANCE;
    }

    private String getToken() {
        Activity activity = getActivity();

        assert activity != null;
        return activity.getSharedPreferences(getString(R.string.pref_file), Context.MODE_PRIVATE)
                .getString("dorm.booker.jwt", null);
    }

    private void getUserBookings() {
        String bearer = "Bearer " + getToken();

        Call<List<Booking>> call = ApiClient
                .getBookingsService()
                .findBookingsByUserId(bearer, mUser.getId());

        call.enqueue(new Callback<List<Booking>>() {
            @Override
            public void onResponse(Call<List<Booking>> call, Response<List<Booking>> response) {
                if (response.isSuccessful()) {
                    List<Booking> bookingList = response.body();
                    assert bookingList != null;
                    bookingList.forEach(b -> {
                        LocalDate localDate = Instant.ofEpochSecond(b.getBeginAt())
                                .atZone(ZoneId.of("UCT"))
                                .toLocalDate();
                        bookings.computeIfAbsent(localDate, k -> new ArrayList<>()).add(b);
                    });
                    calendarView.notifyCalendarChanged();
                    setMSelectedDate(today);
                } else {
                    Log.e("BOOK", response.errorBody().toString());
                }
            }

            @Override
            public void onFailure(Call<List<Booking>> call, Throwable t) {
                Log.e("BOOK", t.getLocalizedMessage());

            }
        });
    }

    private void setMSelectedDate(LocalDate date) {
        if (mSelectedDate != date) {
            if (mSelectedDate != null) binding.calendarView.notifyDateChanged(mSelectedDate);
            binding.calendarView.notifyDateChanged((date));
            mSelectedDate = date;
            calendarView.smoothScrollToDate(date.minusDays(3));
            updateAdapter(date);
        }
    }

    private void updateAdapter(LocalDate date) {
        adapter.list.clear();
        adapter.list.addAll(bookings.get(date) != null ? bookings.get(date) : new ArrayList<>());

        if (!bookings.containsKey(date)) {
            binding.eventList.setVisibility(View.INVISIBLE);
            binding.noEventsInfo.setVisibility(View.VISIBLE);
        } else {
            binding.eventList.setVisibility(View.VISIBLE);
            binding.noEventsInfo.setVisibility(View.INVISIBLE);
        }

        adapter.notifyDataSetChanged();
    }

    class DayViewContainer extends ViewContainer {
        final ViewCalendarDayBinding binding;
        CalendarDay day;

        public DayViewContainer(@NotNull View view) {
            super(view);
            binding = ViewCalendarDayBinding.bind(view);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setMSelectedDate(day.getDate());
                }
            });
        }
    }

    class DayViewBinder implements DayBinder<DayViewContainer> {
        @Override
        public void bind(@NotNull DayViewContainer container, @NotNull CalendarDay day) {
            container.day = day;
            TextView calendarDay = container.binding.calendarDay;
            TextView calendarDayHeader = container.binding.calendarDayHeader;
            View eventMarker = container.binding.calendarDayEventMarker;
            View selectedMarker = container.binding.calendarDaySelectedMarker;

            calendarDay.setText(String.valueOf(day.getDay()));
            calendarDayHeader.setText(day.getDate().getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.ENGLISH));

            if (day.getDate().equals(today)) {
                calendarDay.setTextColor(getResources().getColor(R.color.purple_200));
                selectedMarker.setVisibility(View.INVISIBLE);
                eventMarker.setVisibility(View.INVISIBLE);
            }

            if (day.getDate().equals(mSelectedDate)) {
                selectedMarker.setVisibility(View.VISIBLE);
                eventMarker.setVisibility(View.INVISIBLE);
            } else {
                selectedMarker.setVisibility(View.INVISIBLE);
                eventMarker.setVisibility(bookings.containsKey(day.getDate()) ? View.VISIBLE : View.INVISIBLE);
            }
        }

        @NotNull
        @Override
        public DayViewContainer create(@NotNull View view) {
            return new DayViewContainer(view);
        }
    }

    class MonthViewContainer extends ViewContainer {

        ViewCalendarMonthBinding binding;

        public MonthViewContainer(@NotNull View view) {
            super(view);
            binding = ViewCalendarMonthBinding.bind(view);

        }
    }

    class MonthViewBinder implements MonthHeaderFooterBinder<MonthViewContainer> {

        @Override
        public void bind(@NotNull MonthViewContainer container, @NotNull CalendarMonth month) {

        }

        @NotNull
        @Override
        public MonthViewContainer create(@NotNull View view) {
            return new MonthViewContainer(view);
        }
    }

}