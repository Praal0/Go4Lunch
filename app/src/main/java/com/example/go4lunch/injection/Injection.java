package com.example.go4lunch.injection;

import android.app.Application;

import com.example.go4lunch.repository.ChatRepository;
import com.example.go4lunch.repository.LocationRepository;
import com.google.android.gms.location.FusedLocationProviderClient;

public class Injection {
    public static LocationRepository locationDataSource(FusedLocationProviderClient fusedLocationProviderClient){
        return new LocationRepository(fusedLocationProviderClient);
    }

    public static ChatRepository chatDataSource(FusedLocationProviderClient fusedLocationProviderClient){
        return new ChatRepository();
    }

    public static MapViewModelFactory provideMapViewModelFactory(Application application){
        LocationRepository locationRepository = locationDataSource(new FusedLocationProviderClient(application));
        return new MapViewModelFactory(locationRepository);
    }

    public static ChatViewModelFactory provideChatViewModelFactory(Application application){
        ChatRepository chatRepository = chatDataSource(new FusedLocationProviderClient(application));
        return new ChatViewModelFactory(chatRepository);
    }


}
