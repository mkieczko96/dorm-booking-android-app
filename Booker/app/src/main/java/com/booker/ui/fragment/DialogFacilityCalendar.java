package com.booker.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.booker.R;
import com.booker.databinding.DialogFacilityCalendarBinding;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class DialogFacilityCalendar extends DialogFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_facility_calendar, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        DialogFacilityCalendarBinding binding = DialogFacilityCalendarBinding.bind(view);
    }

    public static DialogFacilityCalendar newInstance() {
        return new DialogFacilityCalendar();
    }
}