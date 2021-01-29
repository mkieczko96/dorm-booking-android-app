package com.booker.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.booker.R;
import com.booker.databinding.BookingNotificationItemBinding;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.List;

public class NotificationItemAdapter extends BaseAdapter implements ListAdapter {

    List<NotificationItem> notifications;
    String[] notificationOptions;
    Context context;

    public NotificationItemAdapter(Context context, List<NotificationItem> notifications) {
        this.context = context;
        this.notifications = notifications;
        this.notificationOptions = context.getResources().getStringArray(R.array.notification_time_options);
    }

    public NotificationItemAdapter(Context context, String[] notifications) {
        this.context = context;
        this.notifications = new ArrayList<>();
        for (String i: notifications) {
            this.notifications.add(new NotificationItem(i));
        }
        this.notificationOptions = context.getResources().getStringArray(R.array.notification_time_options);
    }

    @Override
    public int getCount() {
        return notifications.size();
    }

    @Override
    public Object getItem(int i) {
        return notifications.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup parent) {
        if(view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.booking_notification_item, null);
        }

        BookingNotificationItemBinding binding = BookingNotificationItemBinding.bind(view);

        TextView notificationTime = binding.notificationTime;
        ImageView removeNotification = binding.notificationDelete;
        NotificationItem notificationItem = notifications.get(i);

        notificationTime.setText(notificationItem.notificationTime);

        notificationTime.setOnClickListener(notification -> {
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);
            builder.setTitle(R.string.notification_time_dialog_title)
                    .setItems(R.array.notification_time_options, (dialog, item) -> {
                        notificationItem.notificationTime = notificationOptions[item];
                        notifications.set(i, notificationItem);
                        notifyDataSetChanged();
                        dialog.dismiss();
                    })

                    .show();
        });

        removeNotification.setOnClickListener(notification -> {
            notifications.remove(i);
            if(notifications.size() < 5) {
                ListView lv = (ListView) parent;
                lv.findViewById(R.id.notification_add).setEnabled(true);
            }
            notifyDataSetChanged();
            // TODO: Add logic to remove notification info from DB
        });

        return view;
    }
}
