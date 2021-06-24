package com.example.go4lunch.permission_checker;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class PermissionChecker {

    @NonNull
    private final Application application;

    public PermissionChecker(@NonNull Application application) {
        this.application = application;
    }

    public boolean hasLocationPermission() {
        return ContextCompat.checkSelfPermission(application, ACCESS_FINE_LOCATION) == PERMISSION_GRANTED;
    }
}