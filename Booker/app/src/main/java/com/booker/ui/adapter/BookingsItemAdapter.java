package com.booker.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.booker.R;
import com.booker.model.api.pojo.Booking;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class BookingsItemAdapter extends BaseAdapter implements ListAdapter {

    public final ArrayList<Booking> list;
    private final Context context;

    public BookingsItemAdapter(Context context, ArrayList<Booking> list) {
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
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.item_user_calendar_event, null);
        }

        TextView facility = view.findViewById(R.id.tvFacility);
        TextView bookingId = view.findViewById(R.id.tvBookingId);
        TextView beginAt = view.findViewById(R.id.event_begin_timestamp);
        TextView endAt = view.findViewById(R.id.event_end_timestamp);

        Booking b = list.get(i);

        facility.setText(b.getFacility().getName());
        bookingId.setText(String.valueOf(b.getId()));

        Date beginDate = new Date(b.getBeginAt() * 1000);
        Date endDate = new Date(b.getEndAt() * 1000);
        SimpleDateFormat sfd = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.ENGLISH);

        beginAt.setText(sfd.format(beginDate));
        endAt.setText(sfd.format(endDate));

        return view;
    }
}
