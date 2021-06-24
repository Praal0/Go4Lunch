package com.example.go4lunch.injection;

import android.app.Application;

import com.example.go4lunch.location.LocationRepository;
import com.google.android.gms.location.FusedLocationProviderClient;

public class Injection {
    public static LocationRepository locationDataSource(FusedLocationProviderClient fusedLocationProviderClient){
        return new LocationRepository(fusedLocationProviderClient);
    }



    public static MapViewModelFactory provideMapViewModelFactory(Application application){
        LocationRepository locationRepository = locationDataSource(new FusedLocationProviderClient(application));
        return new MapViewModelFactory(locationRepository);
    }
}
