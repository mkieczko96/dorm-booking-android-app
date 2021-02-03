package com.booker.ui.fragment;

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
import com.booker.databinding.CalendarDayLayoutBinding;
import com.booker.databinding.CalendarMonthLayoutBinding;
import com.booker.databinding.FragmentHomeBinding;
import com.booker.ui.adapter.BookingsItemAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.kizitonwose.calendarview.CalendarView;
import com.kizitonwose.calendarview.model.CalendarDay;
import com.kizitonwose.calendarview.model.CalendarMonth;
import com.kizitonwose.calendarview.ui.DayBinder;
import com.kizitonwose.calendarview.ui.MonthHeaderFooterBinder;
import com.kizitonwose.calendarview.ui.ViewContainer;

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
import java.util.Map;

import kotlin.Unit;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


// TODO: Refactor
public class HomeFragment extends Fragment {
    FragmentHomeBinding binding;

    CalendarView calendarView;
    ListView calendarEventsView;
    FloatingActionButton fab;

    long currentUserId;
    LocalDate today = LocalDate.now();
    LocalDate selectedDate = null;
    BookingsItemAdapter adapter;
    Map<LocalDate, List<Booking>> bookings = new HashMap<>();

    public HomeFragment() {

    }

    public HomeFragment(long userId) {
        currentUserId = userId;
    }

    public static HomeFragment newInstance(long userId) {
        return new HomeFragment(userId);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding = FragmentHomeBinding.bind(view);
        calendarView = binding.calendarView;
        calendarEventsView = binding.eventList;
        fab = binding.fabCreateBooking;

        getUserBookings();
        adapter = new BookingsItemAdapter(getContext());
        calendarEventsView.setAdapter(adapter);

        setCalendarView(calendarView);

        fab.setOnClickListener(this::onFabClick);
    }

    private void onFabClick(View view) {
        NewBookingFragment fragment = NewBookingFragment.newInstance();
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_placeholder, fragment, fragment.getClass().getSimpleName())
                .addToBackStack(fragment.getClass().getSimpleName())
                .commit();
    }

    private void setCalendarView(CalendarView calendarView) {
        calendarView.setup(YearMonth.of(2020, 1), YearMonth.of(2022, 12), DayOfWeek.MONDAY);
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

    private void getUserBookings() {
        String bearer = "Bearer "  + getActivity()
                .getSharedPreferences(getString(R.string.pref_file), Context.MODE_PRIVATE)
                .getString("dorm.booker.jwt", null);

        Call<List<Booking>> call = ApiClient
                .getBookingsService()
                .findBookingsByUserId(bearer, currentUserId);

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
                    setSelectedDate(today);
                    Log.i("DEB", "Loaded all bookings");
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

    private void setSelectedDate(LocalDate date) {
        if (selectedDate != date) {
            if (selectedDate != null) binding.calendarView.notifyDateChanged(selectedDate);
            binding.calendarView.notifyDateChanged((date));
            selectedDate = date;
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    class DayViewContainer extends ViewContainer {
        CalendarDay day;
        final CalendarDayLayoutBinding binding;

        public DayViewContainer(@NotNull View view) {
            super(view);
            binding = CalendarDayLayoutBinding.bind(view);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setSelectedDate(day.getDate());
                }
            });
        }
    }

    class DayViewBinder implements DayBinder<DayViewContainer> {
        @NotNull
        @Override
        public DayViewContainer create(@NotNull View view) {
            return new DayViewContainer(view);
        }

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

            if (day.getDate().equals(selectedDate)) {
                selectedMarker.setVisibility(View.VISIBLE);
                eventMarker.setVisibility(View.INVISIBLE);
            } else {
                selectedMarker.setVisibility(View.INVISIBLE);
                eventMarker.setVisibility(bookings.containsKey(day.getDate()) ? View.VISIBLE : View.INVISIBLE);
            }
        }
    }

    class MonthViewContainer extends ViewContainer {

        CalendarMonthLayoutBinding binding;

        public MonthViewContainer(@NotNull View view) {
            super(view);
            binding = CalendarMonthLayoutBinding.bind(view);

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