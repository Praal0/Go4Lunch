package com.example.go4lunch.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.example.go4lunch.BuildConfig;
import com.example.go4lunch.R;
import com.example.go4lunch.activities.PlaceDetailActivity;
import com.example.go4lunch.activities.ViewModels.CommunicationViewModel;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.util.Objects;
import java.util.concurrent.Executor;

import pub.devrel.easypermissions.EasyPermissions;

import static android.content.ContentValues.TAG;
import static com.firebase.ui.auth.AuthUI.getApplicationContext;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MapFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MapFragment extends BaseFragment implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks, LocationListener {

    private GoogleMap googleMap;
    private static final int PERMS_FINE_COARSE_LOCATION = 100;
    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    private LocationCallback mLocationCallback;
    private FusedLocationProviderClient mFusedLocationClient;
    private MapView mMapView;
    private CameraPosition cameraPosition;

    private static final String[] perms = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};


    // The entry point to the Places API.
    private PlacesClient placesClient;

    // The entry point to the Fused Location Provider.
    private FusedLocationProviderClient fusedLocationProviderClient;

    // A default location (Sydney, Australia) and default zoom to use when location permission is
    // not granted.
    private final LatLng defaultLocation = new LatLng(-33.8523341, 151.2106085);
    private static final int DEFAULT_ZOOM = 15;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private String API_KEY = BuildConfig.API_KEY;
    private boolean locationPermissionGranted;

    // The geographical location where the device is currently located. That is, the last-known
    // location retrieved by the Fused Location Provider.
    private Location lastKnownLocation;

    // Keys for storing activity state.
    // [START maps_current_place_state_keys]
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";
    // [END maps_current_place_state_keys]

    private CommunicationViewModel mViewModel;



    public MapFragment() {
        // Required empty public constructor
    }


    public static MapFragment newInstance(String param1, String param2) {

        return null;
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(CommunicationViewModel.class);

    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_map, container, false);
        mMapView = rootView.findViewById(R.id.mapView);

        mMapView.onCreate(savedInstanceState);
        mMapView.onResume();

        setHasOptionsMenu(true);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume();

        configureMapView();
        configureGoogleApiClient();
        configureLocationRequest();
        configureLocationCallBack();


        return rootView;
    }

    private void configureLocationCallBack() {
        try {
            MapsInitializer.initialize(getActivity().getBaseContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
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
        });
    }

    private boolean checkLocationPermission() {
        if (EasyPermissions.hasPermissions(getContext(), perms)) {
            return true;
        } else {
            return false;
        }
    }


    private void configureGoogleApiClient(){
        mGoogleApiClient = new GoogleApiClient
                .Builder(getContext())
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .enableAutoManage(getActivity(), this)
                .build();
    }

    private void startLocationUpdates() {
        if (checkLocationPermission()){
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null);
        }
    }

    private void configureLocationRequest(){
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        // Create the LocationRequest object
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(100 * 1000)        // 100 seconds, in milliseconds
                .setFastestInterval(1000); // 1 second, in milliseconds
    }

    private void configureMapView() {

    }



    private void updateLocationUI() {

    }


    private void getDeviceLocation() {

    }

    private void getLocationPermission() {

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


    @Override
    public void onLocationChanged(@NonNull Location location) {
        handleNewLocation(location);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onConnected(@Nullable Bundle bundle) {
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

    private void handleNewLocation(Location location) {
        Log.e(TAG, "handleNewLocation: " );
        double currentLatitude = location.getLatitude();
        double currentLongitude = location.getLongitude();

        this.mViewModel.updateCurrentUserPosition(new LatLng(currentLatitude, currentLongitude));

        googleMap.moveCamera(CameraUpdateFactory.newLatLng(this.mViewModel.getCurrentUserPosition()));
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(this.mViewModel.getCurrentUserPosition(), mViewModel.getCurrentUserZoom()));
        stopLocationUpdates();
    }

    private void stopLocationUpdates() {
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}