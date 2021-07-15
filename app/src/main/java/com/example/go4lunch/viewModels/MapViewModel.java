package com.example.go4lunch.viewModels;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.go4lunch.BuildConfig;
import com.example.go4lunch.utils.PlacesStreams;
import com.example.go4lunch.ui.map.MapFragment;
import com.example.go4lunch.location.LocationRepository;
import com.example.go4lunch.models.PlacesInfo.Location;
import com.google.android.gms.maps.model.LatLng;

import io.reactivex.observers.DisposableObserver;
import pub.devrel.easypermissions.EasyPermissions;

import static android.content.ContentValues.TAG;

public class MapViewModel extends ViewModel{
    @NonNull
    private static final String API_KEY = BuildConfig.API_KEY;
    private static final String[] perms = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
    public  final MutableLiveData<LatLng> currentUserPosition = new MutableLiveData<>();
    private String SEARCH_TYPE = "restaurant";

    @NonNull
    private final LocationRepository locationRepository;

    public void executeHttpRequestWithRetrofitFetchPlaceInfo( DisposableObserver createObserver){
        if (getCurrentUserPosition() != null){
            String location = getCurrentUserPositionFormatted();
            Log.e(TAG, "Location : " + location);
            PlacesStreams.streamFetchPlaceInfo(location, 1000, MapFragment.SEARCH_TYPE, API_KEY).subscribeWith(createObserver);
        }
    }

    public MapViewModel(
            @NonNull LocationRepository locationRepository
    ) {
        this.locationRepository = locationRepository;
    }
    public LatLng getCurrentUserPosition() {
        return currentUserPosition.getValue();
    }

    public void updateCurrentUserPosition(LatLng latLng) {
        currentUserPosition.setValue(latLng);
    }

    public String getCurrentUserPositionFormatted(){
        String location = currentUserPosition.getValue().toString().replace("lat/lng: (", "");
        return location.replace(")", "");
    }

    public String getCurrentUserPositionF(){
        if (getCurrentUserPosition() != null){
            String location = getCurrentUserPositionFormatted();
            return location.replace(")", "");
        }else
            return null;
    }

    public void executeHttpRequestWithRetrofitPlaceStream(DisposableObserver createObserver) {
        if (getCurrentUserPosition() != null){
            String location = getCurrentUserPositionFormatted();
            Log.e(TAG, "Location : " + location);
            PlacesStreams.streamFetchNearbyPlaces(location, 1000, SEARCH_TYPE, API_KEY).subscribeWith(createObserver);
        }else{

        }
    }

    @SuppressLint("MissingPermission")
    public void startLocationRequest(Context context) {
        if (EasyPermissions.hasPermissions(context, perms)) {
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
