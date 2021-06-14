package com.example.go4lunch.ViewModels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.maps.model.LatLng;

public class CommunicationViewModel extends ViewModel {
    public final MutableLiveData<LatLng> currentUserPosition = new MutableLiveData<>();
    public final MutableLiveData<String> currentUserUID = new MutableLiveData<>();

    public void updateCurrentUserPosition(LatLng latLng){ currentUserPosition.setValue(latLng); }

    public LatLng getCurrentUserPosition(){
        return currentUserPosition.getValue();
    }


    public String getCurrentUserPositionFormatted(){
        String location = currentUserPosition.getValue().toString().replace("lat/lng: (", "");
        return location.replace(")", "");
    }

    public void updateCurrentUserUID(String uid){
        currentUserUID.setValue(uid);
    }

}
