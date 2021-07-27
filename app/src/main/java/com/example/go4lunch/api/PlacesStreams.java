package com.example.go4lunch.api;


import com.example.go4lunch.models.AutoComplete.AutoCompleteResult;
import com.example.go4lunch.models.PlacesInfo.MapPlacesInfo;
import com.example.go4lunch.models.PlacesInfo.PlacesDetails.PlaceDetailsInfo;
import com.example.go4lunch.models.PlacesInfo.PlacesDetails.PlaceDetailsResults;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;


public class PlacesStreams {
    private static PlacesService mapPlacesInfo = PlacesService.retrofit.create(PlacesService.class);

    public static Observable<MapPlacesInfo> streamFetchNearbyPlaces(String location, int radius, String type, String key){
        return mapPlacesInfo.getNearbyPlaces(location,radius,type,key)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .timeout(10, TimeUnit.SECONDS);
    }

    public static  Observable<List<PlaceDetailsResults>> streamFetchPlaceInfo(String location, int radius, String type, String key){
        return mapPlacesInfo.getNearbyPlaces(location,radius,type,key)
                .flatMapIterable(MapPlacesInfo::getResults)
                .flatMap(info -> mapPlacesInfo.getPlacesInfo(info.getPlaceId(), key))
                .map(PlaceDetailsInfo::getResult)
                .toList()
                .toObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .timeout(10, TimeUnit.SECONDS);
    }

    public static Observable<PlaceDetailsInfo> streamSimpleFetchPlaceInfo(String placeId, String key){
      return  mapPlacesInfo.getPlacesInfo(placeId, key)
              .subscribeOn(Schedulers.io())
              .observeOn(AndroidSchedulers.mainThread())
              .timeout(10, TimeUnit.SECONDS);
    }

    public static  Observable<List<PlaceDetailsResults>> streamFetchAutoCompleteInfo(String query, String location, int radius, String apiKey){
        return mapPlacesInfo.getPlaceAutoComplete(query, location, radius, apiKey)
                .flatMapIterable(AutoCompleteResult::getPredictions)
                .flatMap(info -> mapPlacesInfo.getPlacesInfo(info.getPlaceId(), apiKey))
                .map(PlaceDetailsInfo::getResult)
                .toList()
                .toObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .timeout(10, TimeUnit.SECONDS);
    }




}