package com.booker.ui.fragment;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
import com.booker.ui.adapter.BookingsItemAdapter;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.kizitonwose.calendarview.CalendarView;
import com.kizitonwose.calendarview.model.CalendarDay;
import com.kizitonwose.calendarview.model.CalendarMonth;
import com.kizitonwose.calendarview.ui.DayBinder;
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

import kotlin.Unit;
import lombok.NoArgsConstructor;
import lombok.Setter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.widget.AdapterView.AdapterContextMenuInfo;

@NoArgsConstructor
public class HomeFragment extends Fragment {
    private FragmentHomeBinding mHomeBinding;
    private User mUser;
    private LocalDate mTodayDate;
    private LocalDate mSelectedDate;
    private BookingsItemAdapter adapter;
    private HashMap<LocalDate, List<Booking>> bookings;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mHomeBinding = FragmentHomeBinding.bind(view);
        bookings.clear();
        getUserBookings();
        adapter = new BookingsItemAdapter(requireContext(), new ArrayList<>());
        mHomeBinding.eventList.setAdapter(adapter);
        registerForContextMenu(mHomeBinding.eventList);
        setCalendarView(mHomeBinding.calendarView);
        mHomeBinding.fabCreateBooking.setOnClickListener(this::onFabClick);
    }

    @Override
    public void onCreateContextMenu(@NonNull ContextMenu menu, @NonNull View v, @Nullable ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = requireActivity().getMenuInflater();
        inflater.inflate(R.menu.menu_context, menu);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
        if (item.getItemId() == R.id.edit) {
            editBooking(info.position);
            return true;
        } else if (item.getItemId() == R.id.delete) {
            deleteBooking(info.position);
            return true;
        } else {
            return super.onContextItemSelected(item);
        }
    }

    @NotNull
    public static HomeFragment newInstance(User user) {
        HomeFragment newFragment = new HomeFragment();
        newFragment.mUser = user;
        newFragment.mTodayDate = LocalDate.now();
        newFragment.bookings = new HashMap<>();
        return newFragment;
    }

    private void deleteBooking(int position) {
        Booking booking = bookings.get(mSelectedDate).get(position);

        Call<String> call = ApiClient.getBookingsService()
                .deleteBookingById(getBearerToken(), booking.getId());

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    bookings.get(mSelectedDate).remove(position);
                    Snackbar.make(
                            requireView(),
                            "Booking deleted successfully.",
                            BaseTransientBottomBar.LENGTH_LONG
                    ).show();
                    updateAdapter(mSelectedDate);
                } else {
                    Snackbar.make(
                            requireView(),
                            "Booking was not deleted.",
                            BaseTransientBottomBar.LENGTH_LONG
                    ).show();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.e("DEL", t.getLocalizedMessage());
            }
        });
    }

    private void editBooking(int position) {
        Booking booking = bookings.get(mSelectedDate).get(position);
        CreateBookingFragment fragment = CreateBookingFragment.newInstance(mUser, mSelectedDate, booking);
        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_placeholder, fragment, fragment.getClass().getSimpleName())
                .addToBackStack(fragment.getClass().getSimpleName())
                .commit();
    }

    private void onFabClick(View view) {
        CreateBookingFragment fragment = CreateBookingFragment.newInstance(mUser, mSelectedDate);
        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_placeholder, fragment, fragment.getClass().getSimpleName())
                .addToBackStack(fragment.getClass().getSimpleName())
                .commit();
    }

    private void setCalendarView(CalendarView calendarView) {
        calendarView.setup(YearMonth.of(2021, 1), YearMonth.of(2021, 12), DayOfWeek.MONDAY);
        calendarView.setDayBinder(new DayViewBinder());
        calendarView.setMonthScrollListener(this::onMonthScroll);
    }

    private Unit onMonthScroll(CalendarMonth calendarMonth) {
        Toolbar toolbar = requireActivity().findViewById(R.id.app_bar);
        toolbar.setTitle(DateTimeFormatter.ofPattern("MMMM yyyy")
                .format(calendarMonth.getYearMonth()));
        return Unit.INSTANCE;
    }

    private String getBearerToken() {
        return "Bearer " + requireActivity()
                .getSharedPreferences(getString(R.string.pref_file), Context.MODE_PRIVATE)
                .getString("dorm.booker.jwt", null);
    }

    private void getUserBookings() {
        Call<List<Booking>> call = ApiClient.getBookingsService()
                .findBookingsByUserId(getBearerToken(), mUser.getId());

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
                    mHomeBinding.calendarView.notifyCalendarChanged();
                    setSelectedDate(mSelectedDate != null ? mSelectedDate : mTodayDate);
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
        if (mSelectedDate != null) mHomeBinding.calendarView.notifyDateChanged(mSelectedDate);
        mHomeBinding.calendarView.notifyDateChanged((date));
        mSelectedDate = date;
        mHomeBinding.calendarView.smoothScrollToDate(date.minusDays(3));
        updateAdapter(date);
    }

    private void updateAdapter(LocalDate date) {
        adapter.list.clear();
        adapter.list.addAll(bookings.get(date) != null ? bookings.get(date) : new ArrayList<>());

        boolean containsDate = bookings.containsKey(date) && !adapter.list.isEmpty();
        mHomeBinding.eventList.setVisibility(containsDate ? View.VISIBLE : View.INVISIBLE);
        mHomeBinding.noEventsInfo.setVisibility(containsDate ? View.INVISIBLE : View.VISIBLE);

        adapter.notifyDataSetChanged();
    }

    @Setter
    class DayViewContainer extends ViewContainer {
        final ViewCalendarDayBinding mBinding;
        CalendarDay day;

        public DayViewContainer(@NotNull View view) {
            super(view);
            mBinding = ViewCalendarDayBinding.bind(view);
            view.setOnClickListener(v -> setSelectedDate(day.getDate()));
        }
    }

    class DayViewBinder implements DayBinder<DayViewContainer> {
        @Override
        public void bind(@NotNull DayViewContainer container, @NotNull CalendarDay day) {
            container.setDay(day);
            TextView calendarDay = container.mBinding.calendarDay;
            TextView calendarDayHeader = container.mBinding.calendarDayHeader;
            View eventMarker = container.mBinding.calendarDayEventMarker;
            View selectedMarker = container.mBinding.calendarDaySelectedMarker;

            calendarDay.setText(String.valueOf(day.getDay()));
            calendarDayHeader.setText(day.getDate().getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.ENGLISH));

            if (day.getDate().equals(LocalDate.now())) {
                calendarDay.setTypeface(null, Typeface.BOLD);
                calendarDayHeader.setTypeface(null, Typeface.BOLD);
                selectedMarker.setVisibility(View.INVISIBLE);
                eventMarker.setVisibility(View.INVISIBLE);
            }

            if (day.getDate().equals(mSelectedDate)) {
                selectedMarker.setVisibility(View.VISIBLE);
                eventMarker.setVisibility(View.INVISIBLE);
            } else {
                boolean hasEvents = bookings.containsKey(day.getDate()) && !bookings.get(day.getDate()).isEmpty();
                selectedMarker.setVisibility(View.INVISIBLE);
                eventMarker.setVisibility(hasEvents ? View.VISIBLE : View.INVISIBLE);
            }
        }

        @NotNull
        @Override
        public DayViewContainer create(@NotNull View view) {
            return new DayViewContainer(view);
        }
    }
}