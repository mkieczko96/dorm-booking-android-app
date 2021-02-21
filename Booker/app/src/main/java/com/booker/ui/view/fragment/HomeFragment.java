package com.booker.ui.view.fragment;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.booker.R;
import com.booker.databinding.FragmentHomeBinding;
import com.booker.databinding.ViewCalendarDayBinding;
import com.booker.model.api.Resource;
import com.booker.model.data.Booking;
import com.booker.model.data.Reminder;
import com.booker.model.data.User;
import com.booker.ui.adapter.BookingsItemAdapter;
import com.booker.ui.viewmodel.HomeViewModel;
import com.booker.ui.viewmodel.UserViewModel;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.kizitonwose.calendarview.CalendarView;
import com.kizitonwose.calendarview.model.CalendarDay;
import com.kizitonwose.calendarview.model.CalendarMonth;
import com.kizitonwose.calendarview.ui.DayBinder;
import com.kizitonwose.calendarview.ui.ViewContainer;

import org.jetbrains.annotations.NotNull;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import kotlin.Unit;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static android.widget.AdapterView.AdapterContextMenuInfo;

@NoArgsConstructor
public class HomeFragment extends Fragment {
    private User mUser;
    private LocalDate mTodayDate;
    private LocalDate mSelectedDate;
    private List<LocalDate> mDates = new ArrayList<>();
    private ArrayList<Booking> mEvents = new ArrayList<>();
    private UserViewModel mUserViewModel;
    private HomeViewModel mHomeViewModel;
    private BookingsItemAdapter mAdapter;
    private FragmentHomeBinding mHomeBinding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mHomeBinding = FragmentHomeBinding.bind(view);
        mUserViewModel = new ViewModelProvider
                .AndroidViewModelFactory(requireActivity().getApplication())
                .create(UserViewModel.class);
        mHomeViewModel = new ViewModelProvider
                .AndroidViewModelFactory(requireActivity().getApplication())
                .create(HomeViewModel.class);

        setEventList(mHomeBinding.eventList);
        setCalendarView(mHomeBinding.calendarView);
        loadCurrentUserBookingDetails();

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
    public static HomeFragment newInstance() {
        HomeFragment newFragment = new HomeFragment();
        newFragment.mTodayDate = LocalDate.now();
        return newFragment;
    }

    private void setEventList(ListView eventList) {
        mAdapter = new BookingsItemAdapter(requireContext(), mEvents);
        eventList.setAdapter(mAdapter);
        registerForContextMenu(eventList);
    }

    private void loadCurrentUserBookingDetails() {
        mUserViewModel.getCurrentUser().observe(this, userResource -> {
            if (userResource.getStatus() == Resource.Status.SUCCESS) {
                mUser = userResource.getData();
                loadBookingBeginDates();
                setSelectedDate(mTodayDate);
                createReminders();
            }
        });
    }

    private void loadBookingBeginDates() {
        mHomeViewModel.getAllBeginDates(mUser.getId()).observe(this, listResource -> {
            if (listResource.getStatus() == Resource.Status.SUCCESS) {
                mDates = listResource.getData();
                for (LocalDate ld : mDates) {
                    mHomeBinding.calendarView.notifyDateChanged(ld);
                }
            }
        });
    }

    private void createReminders() {
        mHomeViewModel.getAllBookings(mUser.getId()).observe(this, listResource -> {
            if (listResource.getStatus() == Resource.Status.SUCCESS) {
                for (Booking b : listResource.getData())
                    for (Reminder r : b.getReminders())
                        mHomeViewModel.createReminderAlarm(r);
            } else if (listResource.getStatus() == Resource.Status.ERROR) {
                Snackbar.make(
                        requireView(),
                        "Couldn't create notifications.",
                        BaseTransientBottomBar.LENGTH_LONG
                ).show();
            }
        });
    }

    private void deleteBooking(int position) {
        mHomeViewModel.setSelectedBooking(position);
        mHomeViewModel.removeSelectedBooking().observe(this, resource -> {
            if (resource.getStatus() == Resource.Status.SUCCESS) {
                Snackbar.make(
                        requireView(),
                        "Booking deleted successfully.",
                        BaseTransientBottomBar.LENGTH_LONG
                ).show();
                mHomeBinding.calendarView.notifyDateChanged(mSelectedDate);
                updateAdapter();
                updateView();
            } else if (resource.getStatus() == Resource.Status.ERROR) {
                Snackbar.make(
                        requireView(),
                        "Booking was not deleted.",
                        BaseTransientBottomBar.LENGTH_LONG
                ).show();
            }
        });
    }

    private void editBooking(int position) {
        mHomeViewModel.setSelectedBooking(position);
        CreateBookingFragment fragment = CreateBookingFragment.newInstance(mUser, mSelectedDate);
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
        MaterialToolbar toolbar = requireActivity().findViewById(R.id.app_bar);
        toolbar.setTitle(DateTimeFormatter.ofPattern("MMMM yyyy")
                .format(calendarMonth.getYearMonth()));
        return Unit.INSTANCE;
    }

    private void setSelectedDate(LocalDate date) {
        if (mSelectedDate != null)
            mHomeBinding.calendarView.notifyDateChanged(mSelectedDate);
        mHomeBinding.calendarView.notifyDateChanged((date));
        mSelectedDate = date;
        mHomeBinding.calendarView.scrollToDate(date.minusDays(3));
        mHomeViewModel.getBookingsForSelectedDate(date, mUser.getId()).observe(this, listResource -> {
            if (listResource.getStatus() == Resource.Status.SUCCESS) {
                mEvents = (ArrayList<Booking>) listResource.getData();
                updateAdapter();
                updateView();
            }
        });
    }

    private void updateView() {
        if (mEvents == null || mEvents.isEmpty()) {
            mHomeBinding.eventList.setVisibility(View.GONE);
            mHomeBinding.noEventsInfo.setVisibility(View.VISIBLE);
        } else {
            mHomeBinding.eventList.setVisibility(View.VISIBLE);
            mHomeBinding.noEventsInfo.setVisibility(View.GONE);
        }
    }

    private void updateAdapter() {
        mAdapter.list.clear();
        mAdapter.list.addAll(mEvents);
        mAdapter.notifyDataSetChanged();
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
                boolean hasEvents = mDates.contains(day.getDate());
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