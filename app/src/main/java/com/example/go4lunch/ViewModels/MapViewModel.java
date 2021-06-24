package com.example.go4lunch.ViewModels;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.example.go4lunch.BuildConfig;
import com.example.go4lunch.Utils.PlacesStreams;
import com.example.go4lunch.location.LocationRepository;
import com.example.go4lunch.models.PlacesInfo.Location;
import com.example.go4lunch.permission_checker.PermissionChecker;
import com.google.android.gms.maps.model.LatLng;

import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import pub.devrel.easypermissions.EasyPermissions;

import static android.content.ContentValues.TAG;

public class MapViewModel extends ViewModel{
    @NonNull
    private final PermissionChecker permissionChecker;
    private static final String API_KEY = BuildConfig.API_KEY;
    private final MutableLiveData<LatLng> currentUserPosition = new MutableLiveData<>();
    private final MutableLiveData<String> currentUserUID = new MutableLiveData<>();
    private String SEARCH_TYPE = "restaurant";


    private Disposable disposable;

    @NonNull
    private final LocationRepository locationRepository;

    public MapViewModel(
            @NonNull PermissionChecker permissionChecker,
            @NonNull LocationRepository locationRepository
    ) {
        this.permissionChecker = permissionChecker;
        this.locationRepository = locationRepository;


    }

    public LatLng getCurrentUserPosition() {
        return currentUserPosition.getValue();
    }

    public void updateCurrentUserPosition(LatLng latLng) {
        currentUserPosition.setValue(latLng);
    }

    public void executeHttpRequestWithRetrofitPlaceStream(DisposableObserver createObserver) {
        if (getCurrentUserPosition() != null){
            String location = getCurrentUserPosition().toString();
            Log.e(TAG, "Location : " + location);
            disposable = PlacesStreams.streamFetchNearbyPlaces(location, 1000, SEARCH_TYPE, API_KEY).subscribeWith(createObserver);
        }else{

        }
    }
    
    public void startLocationRequest(Context context) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            locationRepository.startLocationRequest();
        }
    }


    public LiveData<Location> getLocation() {
        return locationRepository.getLocationLiveData();
    }

    public void stopLocationRequest(){
        locationRepository.stopLocationRequest();
    }




}
