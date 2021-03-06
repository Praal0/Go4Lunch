package com.example.go4lunch.api;


import com.example.go4lunch.models.AutoComplete.AutoCompleteResult;
import com.example.go4lunch.models.PlacesInfo.MapPlacesInfo;
import com.example.go4lunch.models.PlacesInfo.PlacesDetails.PlaceDetailsInfo;

import io.reactivex.Observable;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface PlacesService {

    String API_BASE_URL = "https://maps.googleapis.com/maps/api/place/";

    @GET("nearbysearch/json?")
    Observable<MapPlacesInfo> getNearbyPlaces(@Query("location") String location, @Query("radius") int radius, @Query("type") String type, @Query("key") String key);

    @GET("details/json")
    Observable<PlaceDetailsInfo> getPlacesInfo(@Query("placeid") String placeId, @Query("key") String key);

    @GET("autocomplete/json?strictbounds&types=establishment")
    Observable<AutoCompleteResult> getPlaceAutoComplete(@Query("input") String query, @Query("location") String location, @Query("radius") int radius, @Query("key") String apiKey );

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(API_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(new OkHttpClient.Builder().addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC)).build())
            .build();
}