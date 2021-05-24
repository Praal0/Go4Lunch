package com.example.go4lunch.fragment;
import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.example.go4lunch.BuildConfig;
import com.example.go4lunch.R;
import com.example.go4lunch.Utils.PlacesStreams;
import com.example.go4lunch.activities.PlaceDetailActivity;
import com.example.go4lunch.activities.ViewModels.CommunicationViewModel;
import com.example.go4lunch.models.PlacesInfo.MapPlacesInfo;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import pub.devrel.easypermissions.EasyPermissions;


public class MapFragment extends BaseFragment implements OnMapReadyCallback, LocationListener {

    private static final int PERMS_FINE_COARSE_LOCATION = 100;
    private static final String TAG = MapFragment.class.getSimpleName();
    private static final String[] perms = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
    public static final String API_KEY = BuildConfig.API_KEY;
    public static final String SEARCH_TYPE = "restaurant";


    private GoogleMap googleMap;
    private MapView mMapView;
    private LocationRequest mLocationRequest;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationCallback mLocationCallback;



    private CommunicationViewModel mViewModel;

    public static MapFragment newInstance() {
        return new MapFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(CommunicationViewModel.class);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        mMapView = view.findViewById(R.id.mapView);
        mMapView.getMapAsync(this);
        mMapView.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mViewModel.currentUserPosition.observe(getViewLifecycleOwner(), latLng -> {
            executeHttpRequestWithRetrofit();
        });



        this.configureLocationRequest();
        this.configureLocationCallBack();
        startLocationUpdates();
        createDriveFile();
        return view;
    }

    @SuppressLint("PotentialBehaviorOverride")
    @Override
    public void onMapReady(@NonNull GoogleMap mMap) {
        googleMap = mMap;
        if (checkLocationPermission()) {
            //Request location updates:
            googleMap.setMyLocationEnabled(true);
        }
        googleMap.getUiSettings().setCompassEnabled(true);
        googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        View locationButton = ((View) mMapView.findViewById(Integer.parseInt("1")).
                getParent()).findViewById(Integer.parseInt("2"));

        // and next place it, for example, on bottom right (as Google Maps app)
        RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
        // position on right bottom
        rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
        rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        rlp.setMargins(0, 0, 30, 30);
        googleMap.getUiSettings().setRotateGesturesEnabled(true);
        googleMap.setOnMarkerClickListener(MapFragment.this::onClickMarker);

    }

    @SuppressLint("MissingPermission")
    private void createDriveFile() {
        if (EasyPermissions.hasPermissions(getContext(), perms)) {
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                handleNewLocation(location);
                            } else {
                                if (EasyPermissions.hasPermissions(getContext(), perms)) {
                                    mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null);
                                }

                            }
                        }
                    });
        } else {
            // Do not have permissions, request them now
            EasyPermissions.requestPermissions(this, getString(R.string.popup_title_perm_access),
                    PERMS_FINE_COARSE_LOCATION, perms);
        }
    }

    @Override
    public void onStart(){
        super.onStart();
        mMapView.onStart();
    }



    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        mViewModel.currentUserPosition.removeObservers(this);
    }


    @Override
    public void onDestroyView(){
        super.onDestroyView();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();

    }



    private void handleNewLocation(Location location) {
        Log.e(TAG, "handleNewLocation: " );
        double currentLatitude = location.getLatitude();
        double currentLongitude = location.getLongitude();
        this.mViewModel.updateCurrentUserPosition(new LatLng(currentLatitude, currentLongitude));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(this.mViewModel.getCurrentUserPosition()));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(14), 2000, null);
        stopLocationUpdates();
    }



    @Override
    public void onLocationChanged(@NonNull Location location) {
        handleNewLocation(location);
    }

    // -------------------
    // HTTP (RxJAVA)
    // -------------------

    private void executeHttpRequestWithRetrofit(){
        String location = mViewModel.getCurrentUserPositionFormatted();
        Log.e(TAG, "Location : "+location );
        Disposable disposable = PlacesStreams.streamFetchNearbyPlaces(location, 1000, SEARCH_TYPE, API_KEY).subscribeWith(createObserver());
    }

    private <T> DisposableObserver<T> createObserver(){
        return new DisposableObserver<T>() {
            @Override
            public void onNext(T t) {
                updateUI(t);
            }
            @Override
            public void onError(Throwable e) {handleError(e);}
            @Override
            public void onComplete() { }
        };
    }


    // -------------------
    // UPDATE UI
    // -------------------

    private <T> void updateUI( T results){
        googleMap.clear();
        if(results instanceof MapPlacesInfo){
            MapPlacesInfo result = ((MapPlacesInfo) results);
            Log.e(TAG, "updateUI: " + result .getResults().size());
            if (result.getResults().size() > 0){
                for (int i = 0; i < result.getResults().size(); i++) {
                    int CurrentObject = i;
                    Double lat = result.getResults().get(CurrentObject).getGeometry().getLocation().getLat();
                    Double lng = result.getResults().get(CurrentObject).getGeometry().getLocation().getLng();
                    String title = result.getResults().get(CurrentObject).getName();
                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(new LatLng(lat, lng));
                    markerOptions.title(title);
                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.baseline_place_unbook));
                    Marker marker = googleMap.addMarker(markerOptions);
                    marker.setTag(result.getResults().get(CurrentObject).getPlaceId());
                }
            }
            }else{
                Toast.makeText(getContext(), getResources().getString(R.string.no_restaurant_error_message), Toast.LENGTH_SHORT).show();
            }
    }

    // -----------------
    // CONFIGURATION
    // -----------------
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

    private boolean onClickMarker(Marker marker){
        if (marker.getTag() != null){
            Log.e(TAG, "onClickMarker: " + marker.getTag() );
            Intent intent = new Intent(getActivity(), PlaceDetailActivity.class);
            intent.putExtra("PlaceDetailResult", marker.getTag().toString());
            startActivity(intent);
            return true;
        }else{
            Log.e(TAG, "onClickMarker: ERROR NO TAG" );
            return false;
        }
    }


}