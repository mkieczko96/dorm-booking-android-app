package com.booker.ui.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.booker.R;
import com.booker.databinding.BookingNotificationFooterBinding;
import com.booker.databinding.FragmentNewBookingBinding;
import com.booker.ui.adapter.FacilityDialogItem;
import com.booker.ui.adapter.FacilityDialogItemAdapter;
import com.booker.ui.adapter.NotificationItem;
import com.booker.ui.adapter.NotificationItemAdapter;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import lombok.NoArgsConstructor;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NewBookingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
@NoArgsConstructor
public class NewBookingFragment extends DialogFragment {

    private AlertDialog facilityChooserDialog;
    private TextView selectedFacilityName;
    private ListView notificationList;

    public static NewBookingFragment newInstance() {
        return new NewBookingFragment();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return super.onCreateDialog(savedInstanceState);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_new_booking, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FragmentNewBookingBinding binding = FragmentNewBookingBinding.bind(view);
        selectedFacilityName = binding.newBookingFacility;
        notificationList = binding.bookingNotifications;

        setFacilityChooserDialog();
        setNotificationListWithDefaultValue();

        selectedFacilityName.setOnClickListener(this::onFacilityNameClick);
    }

    private void setFacilityChooserDialog() {
        ArrayList<FacilityDialogItem> items = getFacilityDialogArrayList();
        Context context = getContext();

        assert context != null;
        FacilityDialogItemAdapter adapter = new FacilityDialogItemAdapter(context, items);
        MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(context);
        facilityChooserDialog = dialogBuilder.setTitle(R.string.facility_chooser_title)
                .setAdapter(adapter, this::onFacilityItemClick)
                .create();
    }

    private void setNotificationListWithDefaultValue() {
        ArrayList<NotificationItem> items = getDefaultNotificationsList();
        Context context = getContext();

        assert context != null;
        NotificationItemAdapter adapter = new NotificationItemAdapter(context, items);

        BookingNotificationFooterBinding binding = BookingNotificationFooterBinding.inflate(getLayoutInflater());
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

    // TODO: Load data from REST API
    private ArrayList<FacilityDialogItem> getFacilityDialogArrayList() {
        ArrayList<FacilityDialogItem> items = new ArrayList<>();
        items.add(new FacilityDialogItem(R.drawable.ic_laundry_light_button, "Laundry"));
        items.add(new FacilityDialogItem(R.drawable.ic_gym_light_button, "Gym"));
        items.add(new FacilityDialogItem(R.drawable.ic_tv_light_button, "TV room"));
        return items;
    }

    private void onFacilityItemClick(DialogInterface dialog, int index) {
        ListView facilityList = ((AlertDialog) dialog).getListView();
        String facilityName = ((FacilityDialogItem) facilityList.getAdapter()
                .getItem(index)).getName();
        selectedFacilityName.setText(facilityName);
        dialog.dismiss();
    }

    private void onFacilityNameClick(View view) {
        facilityChooserDialog.show();
    }

    // TODO: Move to MainActivity
    public void showDialog() {
        FragmentManager manager = getFragmentManager();
        NewBookingFragment fragment = NewBookingFragment.newInstance();

        assert manager != null;
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.add(android.R.id.content, fragment)
                .addToBackStack(fragment.getClass().getSimpleName())
                .commit();
    }

}