package com.booker.ui.view.fragment;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
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
import com.booker.databinding.FragmentHomeBinding;
import com.booker.databinding.ViewCalendarDayBinding;
import com.booker.model.api.ApiClient;
import com.booker.model.data.Booking;
import com.booker.model.data.Reminder;
import com.booker.model.data.User;
import com.booker.ui.AlarmReceiver;
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
import java.time.LocalDateTime;
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
    private HashMap<LocalDate, List<Booking>> mBookings;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mHomeBinding = FragmentHomeBinding.bind(view);
        mBookings.clear();
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
        newFragment.mBookings = new HashMap<>();
        return newFragment;
    }

    private void createReminders(Context context) {
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        for (LocalDate date : mBookings.keySet()) {
            if (date.isAfter(mTodayDate) || date.isEqual(mTodayDate)) {
                for (Booking b : mBookings.get(date)) {
                    for (Reminder r : b.getReminders()) {
                        if (r.getTriggerTime() > Instant.now().getEpochSecond()) {
                            Intent passableIntent = new Intent(context, AlarmReceiver.class);
                            passableIntent.putExtra("REMINDER_TITLE", r.getTitle());
                            passableIntent.putExtra("REMINDER_MESSAGE", r.getMessage());
                            PendingIntent intent = PendingIntent.getBroadcast(
                                    context,
                                    r.getId().intValue(),
                                    passableIntent,
                                    PendingIntent.FLAG_CANCEL_CURRENT);

                            manager.setExact(AlarmManager.RTC_WAKEUP,
                                    r.getTriggerTime() * 1000,
                                    intent);
                        }
                    }
                }
            }
        }
    }

    private void deleteBooking(int position) {
        Booking booking = mBookings.get(mSelectedDate).get(position);

        deleteReminders(booking.getReminders());

        Call<String> call = ApiClient.getBookingsService()
                .deleteBookingById(getBearerToken(), booking.getId());

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    mBookings.get(mSelectedDate).remove(position);
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

    private void deleteReminders(List<Reminder> reminders) {
        Context context = requireContext();
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        for (Reminder r : reminders) {
            if (r.getTriggerTime() > Instant.now().getEpochSecond()) {
                Intent passableIntent = new Intent(context, AlarmReceiver.class);
                passableIntent.putExtra("REMINDER_TITLE", r.getTitle());
                passableIntent.putExtra("REMINDER_MESSAGE", r.getMessage());
                PendingIntent intent = PendingIntent.getBroadcast(
                        context,
                        r.getId().intValue(),
                        passableIntent,
                        PendingIntent.FLAG_CANCEL_CURRENT);

                manager.cancel(intent);
            }
        }
    }

    private void editBooking(int position) {
        Booking booking = mBookings.get(mSelectedDate).get(position);
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
        HashMap<String, String> params = new HashMap<>();
        params.put("user-id", String.valueOf(mUser.getId()));
        long after = LocalDateTime.of(2021, 1, 1, 0, 0)
                .atZone(ZoneId.systemDefault())
                .toEpochSecond();
        params.put("begin-after", String.valueOf(after));

        Call<List<Booking>> call = ApiClient.getBookingsService()
                .findAllBookings(getBearerToken(), params);

        call.enqueue(new Callback<List<Booking>>() {
            @Override
            public void onResponse(Call<List<Booking>> call, Response<List<Booking>> response) {
                if (response.isSuccessful()) {
                    List<Booking> bookingList = response.body();
                    assert bookingList != null;
                    bookingList.forEach(b -> {
                        LocalDate localDate = Instant.ofEpochSecond(b.getBeginAt())
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate();
                        mBookings.computeIfAbsent(localDate, k -> new ArrayList<>()).add(b);
                        mHomeBinding.calendarView.notifyDateChanged(localDate);
                    });
                    setSelectedDate(mSelectedDate != null ? mSelectedDate : mTodayDate);
                    createReminders(requireContext());
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
        adapter.list.addAll(mBookings.get(date) != null ? mBookings.get(date) : new ArrayList<>());

        boolean containsDate = mBookings.containsKey(date) && !adapter.list.isEmpty();
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
                boolean hasEvents = mBookings.containsKey(day.getDate()) && !mBookings.get(day.getDate()).isEmpty();
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