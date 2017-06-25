package com.gnnsnowszerro.psngiftcardsgenerator;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import java.util.Calendar;

/**
 * Created by Alexandr on 19/06/2017.
 */

public class NotificationService extends IntentService {

    public static int NotificationID = 133;

    private static final String TAG = NotificationService.class.getSimpleName();

    public NotificationService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        long lastUsedTimeMillis = preferences.getLong("last_used_time", 0);

        Calendar lastUsedTime = Calendar.getInstance();
        lastUsedTime.setTimeInMillis(lastUsedTimeMillis);

        Calendar calendarNow = Calendar.getInstance();

//      Add 1 day (24 hours) to lastUsedTime and compare date.
//      If calendarNow is after lastUsedTime then show notification.
        lastUsedTime.add(Calendar.DAY_OF_MONTH, 1);
        if (calendarNow.after(lastUsedTime)) {
            showNotification();
        }

    }

    private void showNotification() {

        int requestID = (int) System.currentTimeMillis();
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(this, requestID, intent, 0);


        Notification notification = new NotificationCompat.Builder(this)
                .setTicker(getString(R.string.app_name) + ": " + "Free Codes!!!")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(getString(R.string.app_name))
                .setContentText("Get Your PSN Code for FREE Now!")
                .setContentIntent(pIntent)
                .setAutoCancel(true)
                .build();

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(NotificationID, notification);
    }

    public static void setServiceAlarm(Context context, boolean isOn, Calendar time) {
        Intent intent = new Intent(context, NotificationService.class);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, 0);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);

        if (isOn) {
            alarmManager.cancel(pendingIntent);
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, time.getTimeInMillis(), 6 * 60 * 60 * 1000, pendingIntent);
        } else {
            alarmManager.cancel(pendingIntent);
            pendingIntent.cancel();
        }
    }
}
