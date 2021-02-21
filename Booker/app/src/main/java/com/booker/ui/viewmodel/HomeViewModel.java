package com.booker.ui.viewmodel;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.booker.model.PreferenceProvider;
import com.booker.model.api.Resource;
import com.booker.model.data.Booking;
import com.booker.model.data.Reminder;
import com.booker.model.repository.BookingRepository;
import com.booker.model.repository.ReminderRepository;
import com.booker.ui.AlarmReceiver;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeViewModel extends AndroidViewModel {
    private final BookingRepository mBookingRepository;
    private final ReminderRepository mReminderRepository;
    private final PreferenceProvider mProvider;
    private LiveData<Resource<List<LocalDate>>> mDates;
    private LiveData<Resource<List<Booking>>> mEvents;
    private LiveData<Resource<List<Booking>>> mAllEvents;
    private Booking mSelectedBooking;

    public HomeViewModel(@NonNull Application application) {
        super(application);
        mProvider = new PreferenceProvider(application);
        mBookingRepository = new BookingRepository();
        mReminderRepository = new ReminderRepository();
    }

    public Booking getSelectedBooking() {
        return mSelectedBooking;
    }

    public void setSelectedBooking(int pos) {
        mSelectedBooking = mEvents.getValue().getData().get(pos);
    }

    public LiveData<Resource<List<LocalDate>>> getAllBeginDates(long userId) {
        mDates = mBookingRepository.getAllBeginDates(mProvider.getToken(), userId);
        return mDates;
    }

    public LiveData<Resource<List<Booking>>> getAllBookings(long userId) {
        Map<String, String> params = new HashMap<>();
        params.put("user-id", String.valueOf(userId));

        mAllEvents = mBookingRepository.getAllBookings(mProvider.getToken(), params);
        return mAllEvents;
    }

    public LiveData<Resource<List<Booking>>> getBookingsForSelectedDate(LocalDate selectedDate, long userId) {
        Map<String, String> params = new HashMap<>();
        params.put("user-id", String.valueOf(userId));
        params.put("begin-after", String.valueOf(selectedDate
                .atStartOfDay()
                .atZone(ZoneId.systemDefault())
                .toEpochSecond()));
        params.put("begin-before", String.valueOf(LocalDateTime
                .of(selectedDate, LocalTime.of(23, 59))
                .atZone(ZoneId.systemDefault())
                .toEpochSecond()));

        mEvents = mBookingRepository.getAllBookings(mProvider.getToken(), params);
        return mEvents;
    }

    public LiveData<Resource<Booking>> removeSelectedBooking() {
        for (Reminder r : mSelectedBooking.getReminders()) {
            removeReminderAlarm(r);
            removeReminder(r.getId());
        }
        mEvents.getValue().getData().remove(mSelectedBooking);

        if (mEvents.getValue().getData().isEmpty())
            mDates.getValue().getData().remove(
                    Instant.ofEpochSecond(mSelectedBooking.getBeginAt())
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate());

        return mBookingRepository.removeBooking(mProvider.getToken(), mSelectedBooking.getId());
    }

    public LiveData<Resource<Reminder>> removeReminder(long id) {
        return mReminderRepository.remove(mProvider.getToken(), id);
    }

    public void removeReminderAlarm(Reminder r) {
        AlarmManager manager = (AlarmManager) getApplication().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getApplication(), AlarmReceiver.class);
        intent.putExtra("REMINDER_TITLE", r.getTitle());
        intent.putExtra("REMINDER_MESSAGE", r.getMessage());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                getApplication(),
                r.getId().intValue(),
                intent,
                PendingIntent.FLAG_CANCEL_CURRENT);

        manager.cancel(pendingIntent);
    }

    public void createReminderAlarm(Reminder r) {
        AlarmManager manager = (AlarmManager) getApplication().getSystemService(Context.ALARM_SERVICE);
        if (r.getTriggerTime() > Instant.now().getEpochSecond()) {
            Intent intent = new Intent(getApplication(), AlarmReceiver.class);
            intent.putExtra("REMINDER_TITLE", r.getTitle());
            intent.putExtra("REMINDER_MESSAGE", r.getMessage());
            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    getApplication(),
                    r.getId().intValue(),
                    intent,
                    PendingIntent.FLAG_CANCEL_CURRENT);

            manager.setExact(AlarmManager.RTC_WAKEUP,
                    r.getTriggerTime() * 1000,
                    pendingIntent);
        }
    }
}
