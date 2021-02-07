package com.booker.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.booker.R;
import com.booker.api.ApiClient;
import com.booker.data.models.Facility;
import com.booker.databinding.FragmentCreateBookingBinding;
import com.booker.databinding.ViewNotificationListFooterBinding;
import com.booker.ui.adapter.FacilityDialogItemAdapter;
import com.booker.ui.adapter.NotificationItem;
import com.booker.ui.adapter.NotificationItemAdapter;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import lombok.NoArgsConstructor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@NoArgsConstructor
public class CreateBookingFragment extends DialogFragment {

    private AlertDialog facilityChooserDialog;
    private TextView selectedFacilityName;
    private ListView notificationList;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_create_booking, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FragmentCreateBookingBinding binding = FragmentCreateBookingBinding.bind(view);
        selectedFacilityName = binding.newBookingFacility;
        notificationList = binding.bookingNotifications;

        //setFacilityChooserDialog();
        getFacilityDialogArrayList();
        setNotificationListWithDefaultValue();

        selectedFacilityName.setOnClickListener(this::onFacilityNameClick);

        binding.beginDate.setOnClickListener(this::onDateTimeClick);

    }

    public static CreateBookingFragment newInstance() {
        return new CreateBookingFragment();
    }

    private void onDateTimeClick(View v) {
        showDialog();
    }

    private void setFacilityChooserDialog(ArrayList<Facility> items) {
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

    private String getToken() {
        Activity activity = getActivity();

        assert activity != null;
        return activity.getSharedPreferences(getString(R.string.pref_file), Context.MODE_PRIVATE)
                .getString("dorm.booker.jwt", null);
    }

    private void getFacilityDialogArrayList() {
        String token = "Bearer " + getToken();
        Call<List<Facility>> call = ApiClient.getFacilityService()
                .findAllFacilities(token);

        call.enqueue(new Callback<List<Facility>>() {
            @Override
            public void onResponse(Call<List<Facility>> call, Response<List<Facility>> response) {

                if (response.isSuccessful()) {
                    ArrayList<Facility> items = (ArrayList<Facility>) response.body();
                    items.removeIf(f -> f.getName().startsWith("Laundry") && f.getFloor() != 1);
                    setFacilityChooserDialog(items);
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

    private void onFacilityItemClick(DialogInterface dialog, int index) {
        ListView facilityList = ((AlertDialog) dialog).getListView();
        String facilityName = ((Facility) facilityList.getAdapter()
                .getItem(index)).getName();
        selectedFacilityName.setText(facilityName);
        dialog.dismiss();
    }

    private void onFacilityNameClick(View view) {
        facilityChooserDialog.show();
    }

    public void showDialog() {
        FragmentManager manager = getFragmentManager();
        FacilityCalendarDialogFragment fragment = FacilityCalendarDialogFragment.newInstance();
        fragment.show(manager, "dialog");
    }

}