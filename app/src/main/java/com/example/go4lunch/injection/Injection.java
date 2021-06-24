package com.example.go4lunch.injection;

import android.app.Application;

import com.example.go4lunch.location.LocationRepository;
import com.example.go4lunch.permission_checker.PermissionChecker;
import com.google.android.gms.location.FusedLocationProviderClient;

public class Injection {
    public static LocationRepository locationDataSource(FusedLocationProviderClient fusedLocationProviderClient){
        return new LocationRepository(fusedLocationProviderClient);
    }

    public static PermissionChecker permissionChecker(Application application){
        return new PermissionChecker(application);
    }

    public static MapViewModelFactory provideMapViewModelFactory(Application application){
        LocationRepository locationRepository = locationDataSource(new FusedLocationProviderClient(application));
        PermissionChecker permissionChecker = permissionChecker(application);
        return new MapViewModelFactory(permissionChecker,locationRepository);
    }
}
