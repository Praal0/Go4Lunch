package com.example.go4lunch.controller.fragment;

import android.Manifest;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.go4lunch.R;
import com.example.go4lunch.Utils.DividerItemDecoration;
import com.example.go4lunch.Utils.ItemClickSupport;
import com.example.go4lunch.Utils.PlacesStreams;
import com.example.go4lunch.ViewModels.CommunicationViewModel;
import com.example.go4lunch.Views.RestaurantAdapter;
import com.example.go4lunch.controller.activities.PlaceDetailActivity;
import com.example.go4lunch.models.PlacesInfo.PlacesDetails.PlaceDetailsResults;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import pub.devrel.easypermissions.EasyPermissions;

import static com.firebase.ui.auth.AuthUI.TAG;

public class ListFragment extends BaseFragment  {
    private RecyclerView mRecyclerView;

    private Disposable disposable;
    public String locationSav;
    private static final String[] perms = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
    private List<PlaceDetailsResults> mResults;
    private RestaurantAdapter adapter;
    private CommunicationViewModel mViewModel;
    private LocationRequest mLocationRequest;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationCallback mLocationCallback;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(CommunicationViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);
        mRecyclerView = view.findViewById(R.id.list_recycler_view);

        setHasOptionsMenu(true);

        this.configureLocationRequest();
        this.configureLocationCallBack();
        startLocationUpdates();


        mViewModel.currentUserPosition.observe(getViewLifecycleOwner(), latLng -> {
            executeHttpRequestWithRetrofit();
        });

        configureRecyclerView();


        this.configureOnClickRecyclerView();

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.disposeWhenDestroy();
    }

    @Override
    public void onStop() {
        super.onStop();
        mViewModel.currentUserPosition.removeObservers(this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();

        inflater.inflate(R.menu.toolbar_menu, menu);
    }

    // -----------------
    // CONFIGURATION
    // -----------------

    // Configure RecyclerView, Adapter, LayoutManager & glue it together
    private void configureRecyclerView(){
        this.mResults = new ArrayList<>();
        this.adapter = new RestaurantAdapter(this.mResults,locationSav);
        this.mRecyclerView.setAdapter(this.adapter);
        this.mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    private void configureLocationCallBack() {
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    handleNewLocation(location);
                }
            }
        };
    }

    private void handleNewLocation(Location location) {
        Log.e(TAG, "handleNewLocation: " );
        double currentLatitude = location.getLatitude();
        double currentLongitude = location.getLongitude();
        this.mViewModel.updateCurrentUserPosition(new LatLng(currentLatitude, currentLongitude));
        stopLocationUpdates();
    }

    private void stopLocationUpdates() {
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
    }

    private void startLocationUpdates() {
        if (checkLocationPermission()){
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null);
        }
    }

    private void configureLocationRequest(){
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());
        // Create the LocationRequest object
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(100 * 1000)        // 100 seconds, in milliseconds
                .setFastestInterval(1000); // 1 second, in milliseconds
    }

    public boolean checkLocationPermission() {
        if (EasyPermissions.hasPermissions(requireContext(), perms)) {
            return true;
        } else {
            return false;
        }
    }

    // -----------------
    // ACTION
    // -----------------

    // Configure item click on RecyclerView
    private void configureOnClickRecyclerView(){
        ItemClickSupport.addTo(mRecyclerView, R.layout.fragment_list_item)
                .setOnItemClickListener((recyclerView, position, v) -> {
                    PlaceDetailsResults result = adapter.getRestaurant(position);
                    Intent intent = new Intent(getActivity(), PlaceDetailActivity.class);
                    intent.putExtra("PlaceDetailResult", result.getPlaceId());
                    startActivity(intent);
                });
    }

    // -------------------
    // HTTP (RxJAVA)
    // -------------------

    private void executeHttpRequestWithRetrofit(){
        Log.e(TAG, "Location : "+ locationSav );
        this.disposable = PlacesStreams.streamFetchPlaceInfo(mViewModel.getCurrentUserPositionFormatted(),1000,
                MapFragment.SEARCH_TYPE,MapFragment.API_KEY).subscribeWith(createObserver());
    }

    private <T> DisposableObserver<T> createObserver(){
        return new DisposableObserver<T>() {
            @Override
            public void onNext(T t) {
                if (t instanceof ArrayList){
                    updateUI((ArrayList) t);
                }
            }
            @Override
            public void onError(Throwable e) {
                handleError(e);}
            @Override
            public void onComplete() { }
        };
    }

    private void disposeWhenDestroy(){
        if (this.disposable != null && !this.disposable.isDisposed()) this.disposable.dispose();
    }

    // -------------------
    // UPDATE UI
    // -------------------

    private void updateUI(List<PlaceDetailsResults> results){
        mResults.clear();
        if (results.size() > 0){
            mResults.addAll(results);
        }else{
            Toast.makeText(getContext(), getResources().getString(R.string.no_restaurant_error_message), Toast.LENGTH_SHORT).show();
        }
        adapter.notifyDataSetChanged();
    }
}