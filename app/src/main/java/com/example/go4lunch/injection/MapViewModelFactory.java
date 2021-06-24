package com.example.go4lunch.injection;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.go4lunch.ViewModels.MapViewModel;
import com.example.go4lunch.location.LocationRepository;

public class MapViewModelFactory implements ViewModelProvider.Factory {
    private final LocationRepository locationRepository;

    public MapViewModelFactory(LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(MapViewModel.class)){
            return (T) new MapViewModel(locationRepository);
        }
        throw new IllegalArgumentException("Unknown ViewModel Class");
    }
}
