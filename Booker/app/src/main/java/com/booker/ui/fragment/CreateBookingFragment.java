package com.booker.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.booker.R;
import com.booker.api.ApiClient;
import com.booker.api.data.Booking;
import com.booker.api.data.Facility;
import com.booker.api.data.User;
import com.booker.databinding.FragmentCreateBookingBinding;
import com.booker.databinding.ViewNotificationListFooterBinding;
import com.booker.ui.BookingDates;
import com.booker.ui.adapter.FacilityDialogItemAdapter;
import com.booker.ui.adapter.NotificationItem;
import com.booker.ui.adapter.NotificationItemAdapter;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import lombok.NoArgsConstructor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@NoArgsConstructor
public class CreateBookingFragment extends DialogFragment {

    DateTimeFormatter format = DateTimeFormatter.ofPattern("dd.MM.yyyy @ HH:mm");
    private ListView notificationList;
    private Facility mSelectedFacility;
    private User mUser;
    private FragmentCreateBookingBinding mBinding;
    private LocalDate mSelectedDate;
    private BookingDates mBookingDates;
    private ActionMode mActionMode;
    private Booking mBooking;

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

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_create_booking, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ActionMode.Callback actionMode = new CreateBookingActionMode();
        mActionMode = requireActivity().startActionMode(actionMode);
        mBinding = FragmentCreateBookingBinding.bind(view);
        notificationList = mBinding.bookingNotifications;
        getFacilityDialogArrayList();

        if (mBooking == null) {
            mActionMode.setTitle("New booking");
            setNotificationListWithDefaultValue();
        } else {
            mActionMode.setTitle("Edit booking");
            mSelectedFacility = mBooking.getFacility();
            mBinding.facilityDropdown.setText(mBooking.getFacility().getName());
            mBinding.facilityDropdown.setEnabled(false);

            LocalDateTime start = LocalDateTime.ofEpochSecond(mBooking.getBeginAt(), 0, ZoneOffset.UTC);
            LocalDateTime end = LocalDateTime.ofEpochSecond(mBooking.getEndAt(), 0, ZoneOffset.UTC);
            mBinding.beginDateTime.setText(format.format(start));
            mBinding.endDateTime.setText(format.format(end));
        }
        mBinding.beginDateTime.setOnClickListener(this::onDateTimeClick);
        mBinding.endDateTime.setOnClickListener(this::onDateTimeClick);
    }

    public static CreateBookingFragment newInstance(User user, LocalDate date) {
        CreateBookingFragment fragment = new CreateBookingFragment();
        fragment.mUser = user;
        fragment.mSelectedDate = date;
        fragment.mBooking = null;
        return fragment;
    }

    public static CreateBookingFragment newInstance(User user, LocalDate date, Booking booking) {
        CreateBookingFragment fragment = new CreateBookingFragment();
        fragment.mUser = user;
        fragment.mSelectedDate = date;
        fragment.mBooking = booking;
        return fragment;
    }

    @Subscribe
    public void onDateSelected(BookingDates dates) {
        mBookingDates = dates;
        mBinding.beginDateTime.setText(dates.getBeginAt().format(format));
        mBinding.endDateTime.setText(dates.getEndAt().format(format));
    }

    private void onDateTimeClick(View v) {
        if (mSelectedFacility != null)
            showDialog();
        else
            Snackbar.make(requireView(), "Facility was not selected.", BaseTransientBottomBar.LENGTH_SHORT).show();
    }

    private void setFacilityChooserDialog(ArrayList<Facility> items) {
        FacilityDialogItemAdapter adapter = new FacilityDialogItemAdapter(requireContext(), items);
        mBinding.facilityDropdown.setAdapter(adapter);
        mBinding.facilityDropdown.setOnItemClickListener((p, v, pos, id) -> {
            mSelectedFacility = items.stream()
                    .filter(f -> f.getId() == id)
                    .findFirst()
                    .orElse(items.get(pos));
            mBinding.facilityDropdown.setText(mSelectedFacility.getName());
        });
    }

    private void setNotificationListWithDefaultValue() {
        ArrayList<NotificationItem> items = getDefaultNotificationsList();
        Context context = getContext();

        assert context != null;
        NotificationItemAdapter adapter = new NotificationItemAdapter(context, items);

        ViewNotificationListFooterBinding binding = ViewNotificationListFooterBinding.inflate(getLayoutInflater());
        Button addNotification = binding.notificationAdd;
        addNotification.setOnClickListener(onAddNotificationButtonClick(items, adapter));
        notificationList.addFooterView(binding.getRoot());

        notificationList.setAdapter(adapter);
    }

    @NotNull
    private View.OnClickListener onAddNotificationButtonClick(ArrayList<NotificationItem> items, NotificationItemAdapter adapter) {
        return v -> {
            items.add(new NotificationItem(getResources()
                    .getStringArray(R.array.notification_time_options)[0]));

            if (adapter.getCount() == 5) v.setEnabled(false);
            adapter.notifyDataSetChanged();
        };
    }

    private ArrayList<NotificationItem> getDefaultNotificationsList() {
        ArrayList<NotificationItem> items = new ArrayList<>();
        items.add(new NotificationItem("10 minutes before"));
        return items;
    }

    private String getBearerToken() {
        Activity activity = getActivity();

        assert activity != null;
        return "Bearer " + activity.getSharedPreferences(getString(R.string.pref_file), Context.MODE_PRIVATE)
                .getString("dorm.booker.jwt", null);
    }

    private void getFacilityDialogArrayList() {
        String token = getBearerToken();
        Call<List<Facility>> call = ApiClient.getFacilityService()
                .findAllFacilities(token);

        call.enqueue(new Callback<List<Facility>>() {
            @Override
            public void onResponse(@NotNull Call<List<Facility>> call, @NotNull Response<List<Facility>> response) {

                if (response.isSuccessful()) {
                    ArrayList<Facility> items = (ArrayList<Facility>) response.body();
                    if (items != null) {
                        items.removeIf(f -> f.getName().startsWith("Laundry") && f.getFloor() != 1);
                        setFacilityChooserDialog(items);
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
            public void onFailure(Call<List<Facility>> call, Throwable t) {
                Log.e("DEB", t.getLocalizedMessage());
            }
        });
    }

    public void showDialog() {
        FragmentManager manager = requireFragmentManager();
        DialogFacilityCalendar fragment = DialogFacilityCalendar.newInstance(mUser, mSelectedFacility, mSelectedDate);
        fragment.show(manager, "dialog");
    }

    private void saveBooking() {
        Booking booking = new Booking();
        booking.setUserId(mUser.getId());
        booking.setUser(mUser);
        booking.setFacilityId(mSelectedFacility.getId());
        booking.setFacility(mSelectedFacility);
        booking.setBeginAt(mBookingDates.getBeginAt().toEpochSecond(ZoneOffset.UTC));
        booking.setEndAt(mBookingDates.getEndAt().toEpochSecond(ZoneOffset.UTC));

        Call<Booking> call = ApiClient.getBookingsService().saveBooking(getBearerToken(), booking);
        call.enqueue(new CreateBookingCallback());
    }

    private void updateBooking() {
        mBooking.setBeginAt(mBookingDates.getBeginAt().toEpochSecond(ZoneOffset.UTC));
        mBooking.setEndAt(mBookingDates.getEndAt().toEpochSecond(ZoneOffset.UTC));

        ApiClient.getBookingsService().updateBooking(getBearerToken(), mBooking.getId(), mBooking)
                .enqueue(new CreateBookingCallback());
    }

    static class CreateBookingCallback implements Callback<Booking> {

        @Override
        public void onResponse(@NotNull Call<Booking> call, Response<Booking> response) {
            if (response.isSuccessful()) {
                Booking savedBooking = response.body();
                EventBus.getDefault().post(savedBooking);
            }
        }

        @Override
        public void onFailure(@NotNull Call<Booking> call, Throwable t) {
            Log.e("POST", t.getLocalizedMessage());
        }
    }

    class CreateBookingActionMode implements ActionMode.Callback {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.menu_create_booking, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            if (item.getItemId() == R.id.save) {
                if (mBooking == null) {
                    saveBooking();
                } else {
                    updateBooking();
                }
                mode.finish();
                return true;
            } else {
                return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            requireFragmentManager().popBackStackImmediate();
            mActionMode = null;
        }
    }

}