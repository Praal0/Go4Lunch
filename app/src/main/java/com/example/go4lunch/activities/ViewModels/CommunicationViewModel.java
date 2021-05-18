package com.example.go4lunch.activities.ViewModels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.maps.model.LatLng;

public class CommunicationViewModel extends ViewModel {
    public final MutableLiveData<LatLng> currentUserPosition = new MutableLiveData<>();
    public final MutableLiveData<String> currentUserUID = new MutableLiveData<>();
    public final MutableLiveData<Integer> currentUserZoom = new MutableLiveData<>();
    public final MutableLiveData<Integer> currentUserRadius = new MutableLiveData<>();

    public void updateCurrentUserPosition(LatLng latLng){
        currentUserPosition.setValue(latLng);
    }

    public LatLng getCurrentUserPosition(){
        return currentUserPosition.getValue();
    }

    public void updateCurrentUserZoom(int zoom){
        currentUserZoom.setValue(zoom);
    }

    public Integer getCurrentUserZoom(){return currentUserZoom.getValue();}

    public void updateCurrentUserRadius(int radius){
        currentUserRadius.setValue(radius);
    }

    public Integer getCurrentUserRadius(){return currentUserRadius.getValue();}

    public String getCurrentUserPositionFormatted(){
        String location = currentUserPosition.getValue().toString().replace("lat/lng: (", "");
        return location.replace(")", "");
    }

    public void updateCurrentUserUID(String uid){
        currentUserUID.setValue(uid);
    }

    public String getCurrentUserUID() {
        return currentUserUID.getValue();
    }
}
