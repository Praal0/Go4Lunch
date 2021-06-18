package com.example.go4lunch.ViewModels;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.go4lunch.BuildConfig;
import com.example.go4lunch.Utils.PlacesStreams;
import com.example.go4lunch.controller.fragment.MapFragment;
import com.google.android.gms.maps.model.LatLng;

import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;

import static android.content.ContentValues.TAG;
import static com.example.go4lunch.controller.fragment.MapFragment.SEARCH_TYPE;

public class CommunicationViewModel extends ViewModel {
    private static final String API_KEY = BuildConfig.API_KEY;
    public final MutableLiveData<LatLng> currentUserPosition = new MutableLiveData<>();
    public final MutableLiveData<String> currentUserUID = new MutableLiveData<>();

    private String SEARCH_TYPE = "restaurant";
    private Disposable disposable;


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

    public void executeHttpRequestWithRetrofitPlaceStream( DisposableObserver createObserver){
        String location = "45.6345291,4.7827152";
        Log.e(TAG, "Location : "+location );
        disposable = PlacesStreams.streamFetchNearbyPlaces(location, 1000, SEARCH_TYPE, API_KEY).subscribeWith(createObserver);
    }
    public void executeHttpRequestWithRetrofit( DisposableObserver createObserver){
        String location = "45.6345291,4.7827152";
        disposable = PlacesStreams.streamFetchPlaceInfo(location,1000,
                MapFragment.SEARCH_TYPE,MapFragment.API_KEY).subscribeWith(createObserver);
    }


}
