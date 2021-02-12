package com.booker.ui;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

import com.booker.R;

public class AlarmReceiver extends BroadcastReceiver {
    private static final String ID = "BOOKER_REMINDER_CHANNEL_ID";
    private static final String CHANNEL = "BOOKER_REMINDER_CHANNEL_NAME";

    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder notification = new NotificationCompat.Builder(context, ID)
                .setContentTitle("Title")
                .setContentText("Content")
                .setSmallIcon(R.drawable.ic_notification_24dp);
        NotificationChannel channels = new NotificationChannel(ID, CHANNEL, NotificationManager.IMPORTANCE_HIGH);
        manager.createNotificationChannel(channels);
        manager.notify(1, notification.build());
    }
}
