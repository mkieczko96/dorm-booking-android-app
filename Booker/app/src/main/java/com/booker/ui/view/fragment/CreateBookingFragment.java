package com.booker.ui.view.fragment;

import android.app.Activity;
import android.app.Application;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModelProvider;

import com.booker.R;
import com.booker.model.PreferenceProvider;
import com.booker.model.api.ApiClient;
import com.booker.model.api.Resource;
import com.booker.model.api.callbacks.PostReminderCallback;
import com.booker.model.api.services.FacilityService;
import com.booker.model.data.Booking;
import com.booker.model.data.Facility;
import com.booker.model.data.Reminder;
import com.booker.model.data.User;
import com.booker.model.api.services.ReminderService;
import com.booker.model.api.callbacks.PostBookingCallback;
import com.booker.databinding.FragmentCreateBookingBinding;
import com.booker.databinding.ViewNotificationListFooterBinding;
import com.booker.ui.BookingDates;
import com.booker.ui.adapter.FacilityDialogItemAdapter;
import com.booker.ui.adapter.NotificationItemAdapter;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.SneakyThrows;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@NoArgsConstructor
public class CreateBookingFragment extends DialogFragment {

    DateTimeFormatter format = DateTimeFormatter.ofPattern("dd.MM.yyyy @ HH:mm");
    private Facility mSelectedFacility;
    private User mUser;
    private FragmentCreateBookingBinding mBinding;
    private LocalDate mSelectedDate;
    private BookingDates mBookingDates;
    private ActionMode mActionMode;
    private Booking mEditBooking;
    private Booking mNewBooking;
    private ArrayList<Reminder> mNotificationsList;
    private String[] mOptions;
    private String[] mMessages;
    private int[] mSeconds;
    private NewBookingViewModel mNewViewModel;

//    @Override
//    public void onStart() {
//        super.onStart();
//        EventBus.getDefault().register(this);
//    }
//
//    @Override
//    public void onStop() {
//        super.onStop();
//        EventBus.getDefault().unregister(this);
//    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_create_booking, container, false);
    }

    @Getter @Setter
    class NewBookingViewModel extends AndroidViewModel {

        private final PreferenceProvider mProvider;
        private final FacilityService mFacilityService;

        public NewBookingViewModel(@NonNull Application application) {
            super(application);
            mProvider = new PreferenceProvider(application);
            mFacilityService = ApiClient.getFacilityService();
        }

        // Both will be set in DialogFragment and then will be read in NewBookingFragment.
        private MutableLiveData<LocalDateTime> mBeginDate;
        private MutableLiveData<LocalDateTime> mEndDate;

        private MutableLiveData<Resource<List<Facility>>> mFacilities;


    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mBinding = FragmentCreateBookingBinding.bind(view);
        mNewViewModel = new ViewModelProvider.AndroidViewModelFactory(requireActivity().getApplication())
                .create(NewBookingViewModel.class);

        // Constant data loaded from resources (mainly).
        mOptions = getResources()
                .getStringArray(R.array.notification_time_options);
        mMessages = getResources()
                .getStringArray(R.array.notification_message_options);
        mSeconds = new int[]{0, 600, 900, 1800, 3600, 7200, 86400, 172800, 604800};

        setActionToolbar();
        getFacilityDialogArrayList();


        if (mEditBooking == null) {
            mActionMode.setTitle("New booking");
            mNewBooking = new Booking();
        } else {
            mActionMode.setTitle("Edit booking");
            mSelectedFacility = mEditBooking.getFacility();
            mBinding.facilityDropdown.setText(mEditBooking.getFacility().getName());
            mBinding.facilityDropdown.setEnabled(false);

            LocalDateTime start = LocalDateTime.ofEpochSecond(mEditBooking.getBeginAt(), 0, ZoneOffset.UTC);
            LocalDateTime end = LocalDateTime.ofEpochSecond(mEditBooking.getEndAt(), 0, ZoneOffset.UTC);
            mBinding.beginDateTime.setText(format.format(start));
            mBinding.endDateTime.setText(format.format(end));
        }

        loadNotifications();

        mBinding.beginDateTime.setOnClickListener(this::onDateTimeClick);
        mBinding.endDateTime.setOnClickListener(this::onDateTimeClick);
    }

    private void setActionToolbar() {
        ActionMode.Callback actionMode = new CreateBookingActionMode(); // Maybe move inner class to external class?
        mActionMode = requireActivity().startActionMode(actionMode);
    }

    public static CreateBookingFragment newInstance(User user, LocalDate date) {
        CreateBookingFragment fragment = new CreateBookingFragment();
        fragment.mUser = user;
        fragment.mSelectedDate = date;
        fragment.mEditBooking = null;
        return fragment;
    }

    public static CreateBookingFragment newInstance(User user, LocalDate date, Booking booking) {
        CreateBookingFragment fragment = new CreateBookingFragment();
        fragment.mUser = user;
        fragment.mSelectedDate = date;
        fragment.mEditBooking = booking;
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

    private void loadNotifications() {
        if (mEditBooking != null) {
            mNotificationsList = (ArrayList<Reminder>) mEditBooking.getReminders();
        } else {
            mNotificationsList = initNotification();
        }

        NotificationItemAdapter adapter = new NotificationItemAdapter(requireContext(), mNotificationsList);
        ViewNotificationListFooterBinding bind = ViewNotificationListFooterBinding.inflate(getLayoutInflater());

        bind.notificationAdd.setOnClickListener(onAddNotificationButtonClick(mNotificationsList, adapter));
        mBinding.bookingNotifications.addFooterView(bind.getRoot());
        mBinding.bookingNotifications.setAdapter(adapter);
    }

    @NotNull
    private View.OnClickListener onAddNotificationButtonClick(ArrayList<Reminder> items, NotificationItemAdapter adapter) {
        return v -> {
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext());
            builder.setTitle(R.string.notification_time_dialog_title)
                    .setItems(R.array.notification_time_options, (dialog, item) -> {
                        if (items.stream().noneMatch(n -> n.getLabel().equals(mOptions[item]))) {
                            items.add(new Reminder(mSeconds[item], mOptions[item]));
                            items.sort((o1, o2) -> o1.getDuration() - o2.getDuration());
                            if (adapter.getCount() == 5) v.setEnabled(false);
                            adapter.notifyDataSetChanged();
                        }
                        dialog.dismiss();
                    })
                    .show();
        };
    }

    private ArrayList<Reminder> initNotification() {
        Reminder reminder = new Reminder();
        reminder.setLabel(mOptions[1]);
        reminder.setDuration(mSeconds[1]);

        ArrayList<Reminder> items = new ArrayList<>();
        items.add(reminder);
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
        mNewBooking.setUserId(mUser.getId());
        mNewBooking.setUser(mUser);
        mNewBooking.setFacilityId(mSelectedFacility.getId());
        mNewBooking.setFacility(mSelectedFacility);
        mNewBooking.setBeginAt(mBookingDates.getBeginAt().atZone(ZoneId.systemDefault()).toEpochSecond());
        mBookingDates.getBeginAt().atZone(ZoneId.systemDefault()).toEpochSecond();
        mNewBooking.setEndAt(mBookingDates.getEndAt().atZone(ZoneId.systemDefault()).toEpochSecond());
        mNewBooking.setTimezone(ZoneId.systemDefault().toString());
        String token = getBearerToken();

        Thread post = new Thread(() -> {
            try {
                Response<Booking> call = ApiClient.getBookingsService().saveBooking(token, mNewBooking)
                        .execute();
                if (call.isSuccessful()) {
                    Booking b = call.body();
                    ReminderService service = ApiClient.getReminderService();
                    mNotificationsList.forEach(r -> {
                        r.setMessage(getMessage(r.getLabel()));
                        r.setTitle(b.getFacility().getName() + " booking");
                        r.setTriggerTime(b.getBeginAt() - r.getDuration());
                        r.setTimezone(ZoneId.systemDefault().toString());
                        r.setBookingId(b.getId());

                        try {
                            service.save(token, r).execute();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        post.start();

    }

    private String getMessage(String label) {
        for(int i = 0; i < mOptions.length; i++) {
            if(mOptions[i].equals(label)) {
                return mMessages[i];
            }
        }
        return "Well.. this is some strange notification time, isn't it?";
    }

    private void updateBooking() {
        if (mBookingDates != null) {
            mEditBooking.setBeginAt(mBookingDates.getBeginAt().atZone(ZoneId.systemDefault()).toEpochSecond());
            mEditBooking.setEndAt(mBookingDates.getEndAt().atZone(ZoneId.systemDefault()).toEpochSecond());
        }
        mEditBooking.setReminders(new ArrayList<>());
        mNotificationsList.forEach(r -> {
           if(r.getId() == null) {
               r.setBookingId(mEditBooking.getId());
               r.setTriggerTime(mEditBooking.getBeginAt() - r.getDuration());
               r.setTimezone(ZoneId.systemDefault().toString());
               r.setMessage(getMessage(r.getLabel()));
               r.setTitle(mEditBooking.getFacility().getName() + " booking");

               ApiClient.getReminderService()
                       .save(getBearerToken(),r)
                       .enqueue(new PostReminderCallback());
           }

       });
        ApiClient.getBookingsService().updateBooking(getBearerToken(), mEditBooking.getId(), mEditBooking)
                .enqueue(new PostBookingCallback());
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

        @SneakyThrows
        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            if (item.getItemId() == R.id.save) {
                if (mEditBooking == null) {
                    saveBooking();
                } else {
                    updateBooking();
                }

                Thread.sleep(300);

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