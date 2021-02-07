package com.booker.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.booker.R;
import com.booker.data.models.Booking;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class BookingsItemAdapter extends BaseAdapter implements ListAdapter {

    public ArrayList<Booking> list;
    private Context context;

    public BookingsItemAdapter(Context context) {
        this.list = new ArrayList<>();
        this.context = context;
    }

    public BookingsItemAdapter(ArrayList<Booking> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if(view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.item_user_calendar_event, null);
        }

        TextView facility = view.findViewById(R.id.tvFacility);
        TextView bookingId = view.findViewById(R.id.tvBookingId);
        TextView beginAt = view.findViewById(R.id.tvBeginAt);
        TextView duration = view.findViewById(R.id.tvDuration);

        Booking b = list.get(i);

        facility.setText(b.getFacility().getName());
        bookingId.setText(String.valueOf(b.getId()));

        Date date = new Date(b.getBeginAt() * 1000);
        SimpleDateFormat sfd = new SimpleDateFormat("dd.MM.yyyy hh:mm", Locale.ENGLISH);

        beginAt.setText(sfd.format(date));
        duration.setText(b.getDuration() / 60.0 + " hours");

        return view;
    }
}
