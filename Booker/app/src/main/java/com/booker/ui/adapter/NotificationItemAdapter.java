package com.booker.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.booker.R;
import com.booker.databinding.ItemNotificationBinding;
import com.booker.model.api.ApiClient;
import com.booker.model.api.callbacks.DeleteReminderCallback;
import com.booker.model.api.callbacks.PutReminderCallback;
import com.booker.model.api.pojo.Reminder;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.List;

public class NotificationItemAdapter extends BaseAdapter implements ListAdapter {
    private final List<Reminder> mReminders;
    private final String[] mReminderLabels;
    private final int[] mDurationInSeconds;
    private final Context mContext;

    public NotificationItemAdapter(Context context, List<Reminder> reminders) {
        this.mContext = context;
        this.mReminders = reminders;
        this.mReminderLabels = context.getResources().getStringArray(R.array.notification_time_options);
        this.mDurationInSeconds = new int[]{0, 600, 900, 1800, 3600, 7200, 86400, 172800, 604800};
    }

    @Override
    public int getCount() {
        return mReminders.size();
    }

    @Override
    public Reminder getItem(int i) {
        return mReminders.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup parent) {
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.item_notification, null);
        }

        ItemNotificationBinding binding = ItemNotificationBinding.bind(view);

        TextView notificationTime = binding.notificationTime;
        Reminder notificationItem = mReminders.get(i);

        notificationTime.setText(notificationItem.getLabel());

        notificationTime.setOnClickListener(notification -> {
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(mContext);
            builder.setTitle(R.string.notification_time_dialog_title)
                    .setItems(R.array.notification_time_options, (dialog, item) -> {
                        notificationItem.setDuration(mDurationInSeconds[item]);
                        notificationItem.setLabel(mReminderLabels[item]);
                        mReminders.set(i, notificationItem);
                        notifyDataSetChanged();

                        if(notificationItem.getId() != null)
                            updateNotification(notificationItem);

                        dialog.dismiss();
                    })
                    .show();
        });

        binding.notificationDelete.setOnClickListener(notification -> {
            if(mReminders.get(i).getId() != null) {
                deleteNotification(mReminders.get(i));
            }

            mReminders.remove(i);
            if (mReminders.size() < 5) {
                ListView lv = (ListView) parent;
                lv.findViewById(R.id.notification_add).setEnabled(true);
            }
            notifyDataSetChanged();
        });

        return view;
    }

    private void deleteNotification(Reminder reminder) {
        String token = mContext.getSharedPreferences(mContext.getString(R.string.pref_file), Context.MODE_PRIVATE)
                .getString("dorm.booker.jwt", null);

        ApiClient.getReminderService().remove("Bearer " + token, reminder.getId())
            .enqueue(new DeleteReminderCallback());
    }

    private void updateNotification(Reminder reminder) {
        String token = mContext.getSharedPreferences(mContext.getString(R.string.pref_file), Context.MODE_PRIVATE)
                .getString("dorm.booker.jwt", null);

        ApiClient.getReminderService().save("Bearer " + token, reminder)
                .enqueue(new PutReminderCallback());
    }
}
