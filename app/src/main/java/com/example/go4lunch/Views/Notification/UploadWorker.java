package com.example.go4lunch.Views.Notification;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class UploadWorker extends Worker {

    public UploadWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {

        uploadNotification();

        // Indicate whether the work finished successfully with the Result
        return Result.success();

    }

    private void uploadNotification() {
    }
}
