package com.example.go4lunch.api;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.example.go4lunch.notification.AlarmReceiver;

import java.util.Calendar;

import static android.content.Context.ALARM_SERVICE;

public class NotificationHelper {
    private Context mContext;
    private AlarmManager alarmManagerRTC;
    private PendingIntent alarmIntentRTC;

    public static int ALARM_TYPE_RTC = 100;

    public NotificationHelper(Context context) {
        mContext = context;
    }


    public void scheduleRepeatingNotification(){
        // Set the alarm to start at approximately 2:00 p.m.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 12);


        //Setting intent to class where Alarm broadcast message will be handled
        Intent intent = new Intent(mContext, AlarmReceiver.class);
        //Setting alarm pending intent
        alarmIntentRTC = PendingIntent.getBroadcast(mContext, ALARM_TYPE_RTC, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        //getting instance of AlarmManager service
        alarmManagerRTC = (AlarmManager) mContext.getSystemService(ALARM_SERVICE);

        alarmManagerRTC.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, alarmIntentRTC);
    }

    public void cancelAlarmRTC() {
        if (alarmManagerRTC!= null) {
            alarmManagerRTC.cancel(alarmIntentRTC);
        }
    }

}
