package com.booker.ui.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.booker.R;
import com.booker.data.models.Facility;
import com.booker.databinding.BookingNotificationFooterBinding;
import com.booker.databinding.FragmentNewBookingBinding;
import com.booker.ui.adapter.FacilityDialogItem;
import com.booker.ui.adapter.FacilityDialogItemAdapter;
import com.booker.ui.adapter.NotificationItemAdapter;
import com.booker.ui.adapter.NotificationItem;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NewBookingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NewBookingFragment extends Fragment {


    private static final String USER_ID = "userId";
    private static final String BOOKING_DATE = "bookingDate";

    FragmentNewBookingBinding binding;

    TextView facility;
    ListView notifications;

    private long userId;
    private LocalDate bookingDate;
    private Facility selectedFacility;

    public NewBookingFragment() {
    }

    public static NewBookingFragment newInstance(long userId, LocalDate bookingDate) {
        NewBookingFragment fragment = new NewBookingFragment();
        Bundle args = new Bundle();
        args.putLong(USER_ID, userId);
        args.putLong(BOOKING_DATE, bookingDate.toEpochDay());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userId = getArguments().getLong(USER_ID);
            bookingDate = LocalDate.ofEpochDay (getArguments().getLong(BOOKING_DATE));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_new_booking, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding = FragmentNewBookingBinding.bind(view);
        facility = binding.newBookingFacility;
        notifications = binding.bookingNotifications;

        ArrayList<FacilityDialogItem> facilityDialogItems = new ArrayList<FacilityDialogItem>();
        facilityDialogItems.add(new FacilityDialogItem(R.drawable.ic_laundry_light_button, "Laundry"));
        facilityDialogItems.add(new FacilityDialogItem(R.drawable.ic_gym_light_button, "Gym"));
        facilityDialogItems.add(new FacilityDialogItem(R.drawable.ic_tv_light_button, "TV room"));

        FacilityDialogItemAdapter facilityDialogItemAdapter = new FacilityDialogItemAdapter(getContext(), facilityDialogItems);
        facility.setOnClickListener(facilityText -> {
            MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(getContext());
            dialogBuilder.setTitle("Select facility")
                    .setAdapter(facilityDialogItemAdapter, (dialog, i) -> {
                        facility.setText(facilityDialogItems.get(i).getName());

                        dialog.dismiss();
                    })
                    .show();
        });



        List<NotificationItem> itemList = new ArrayList<>();
        itemList.add(new NotificationItem("10 minutes before"));
        NotificationItemAdapter adapter = new NotificationItemAdapter(getContext(), itemList);
        notifications.setAdapter(adapter);

        ViewGroup footer = (ViewGroup)getLayoutInflater().inflate(R.layout.booking_notification_footer, notifications, false);
        BookingNotificationFooterBinding footerBinding = BookingNotificationFooterBinding.bind(footer);
        Button add = footerBinding.notificationAdd;
        add.setOnClickListener(footerBtn -> {
            itemList.add(new NotificationItem(
                    getResources().getStringArray(R.array.notification_time_options)[0]
            ));

            if (itemList.size() == 5) footerBinding.notificationAdd.setEnabled(false);

            adapter.notifyDataSetChanged();
        });

        notifications.addFooterView(footerBinding.getRoot());

    }
}