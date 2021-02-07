package com.booker.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.DialogFragment;

import com.booker.R;

public class FacilityCalendarDialogFragment extends DialogFragment {

    public FacilityCalendarDialogFragment() {
        // Required empty public constructor
    }

    public static FacilityCalendarDialogFragment newInstance() {
        return new FacilityCalendarDialogFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_facility_calendar, container, false);
    }
}