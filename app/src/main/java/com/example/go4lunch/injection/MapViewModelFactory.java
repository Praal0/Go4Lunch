package com.example.go4lunch.injection;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.go4lunch.ViewModels.MapViewModel;
import com.example.go4lunch.location.LocationRepository;
import com.example.go4lunch.permission_checker.PermissionChecker;

public class MapViewModelFactory implements ViewModelProvider.Factory {
    private final LocationRepository locationRepository;
    private final PermissionChecker permissionChecker;

    public MapViewModelFactory(PermissionChecker permissionChecker, LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
        this.permissionChecker = permissionChecker;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(MapViewModel.class)){
            return (T) new MapViewModel(permissionChecker,locationRepository);
        }
        throw new IllegalArgumentException("Unknown ViewModel Class");
    }
}
