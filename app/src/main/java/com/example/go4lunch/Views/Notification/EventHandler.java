package com.example.go4lunch.Views.Notification;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.Constraints;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.go4lunch.R;
import com.example.go4lunch.controller.activities.MainActivity;

import java.util.concurrent.TimeUnit;

public class EventHandler extends Worker {

    public final int notificatioId = 1;
    public final String CHANNEL_ID = "12";

    public EventHandler(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        createNofication();
        return Result.success();

    }

    private void createNofication() {
        //intent to open our activity
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),0,intent,0);
//notifications
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(),CHANNEL_ID)
                .setSmallIcon(R.drawable.baseline_place_booked)
                .setContentTitle("Event Reminder")
                .setContentText("Hello, 4 days remaining to event xyz")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);
//show notification
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(getApplicationContext());
        notificationManagerCompat.notify(notificatioId,builder.build());
    }

    public static void periodicWorkRequest(Context context){
        PeriodicWorkRequest periodicWorkRequest = new PeriodicWorkRequest.Builder(EventHandler.class,10,TimeUnit.MINUTES)
                .setInitialDelay(2,TimeUnit.MINUTES)
                .setConstraints(setCons())
                .build();
        WorkManager.getInstance(context).enqueue(periodicWorkRequest);
    }


    public static void oneOffRequest(Context context){
        OneTimeWorkRequest oneTimeWorkRequest = new OneTimeWorkRequest.Builder(EventHandler.class)
                .setInitialDelay(10, TimeUnit.SECONDS)
                .setConstraints(setCons())
                .build();

        WorkManager.getInstance(context).enqueue(oneTimeWorkRequest);
    }

    public static Constraints setCons(){
        Constraints constraints =  new Constraints.Builder().build();
        return constraints;
    }



}
