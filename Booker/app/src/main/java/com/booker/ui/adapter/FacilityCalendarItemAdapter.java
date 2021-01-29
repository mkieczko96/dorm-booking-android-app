package com.booker.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.booker.R;
import com.booker.databinding.FacilityCalendarItemBinding;

import java.util.ArrayList;

public class FacilityCalendarItemAdapter extends BaseAdapter implements ListAdapter {
    Context context;
    ArrayList<String> hours;

    public FacilityCalendarItemAdapter(Context context, ArrayList<String> hours) {
        this.context = context;
        this.hours = hours;
    }

    @Override
    public int getCount() {
        return hours.size();
    }

    @Override
    public String getItem(int i) {
        return hours.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.facility_calendar_item, null);
        }

        FacilityCalendarItemBinding binding = FacilityCalendarItemBinding.bind(view);
        TextView hour = binding.facilityCalendarHour;

        hour.setText(hours.get(i));

        return view;
    }
}
