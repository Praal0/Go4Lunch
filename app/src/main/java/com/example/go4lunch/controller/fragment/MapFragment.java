package com.example.go4lunch.controller.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.go4lunch.BuildConfig;
import com.example.go4lunch.R;
import com.example.go4lunch.Utils.PlacesStreams;
import com.example.go4lunch.ViewModels.MapViewModel;
import com.example.go4lunch.controller.activities.PlaceDetailActivity;
import com.example.go4lunch.ViewModels.CommunicationViewModel;
import com.example.go4lunch.injection.Injection;
import com.example.go4lunch.injection.MapViewModelFactory;
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
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.List;

import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import pub.devrel.easypermissions.EasyPermissions;


public class MapFragment extends BaseFragment implements OnMapReadyCallback, EasyPermissions.PermissionCallbacks {

    public static final String API_KEY = BuildConfig.API_KEY;
    private static final String TAG = MapFragment.class.getSimpleName();

    public static final String SEARCH_TYPE = "restaurant";
    private static final int RC_LOCATION_CONTACTS_PERM = 124;

    private GoogleMap googleMap;
    private MapView mMapView;
    private LocationRequest mLocationRequest;
    private FusedLocationProviderClient mFusedLocationClient;
    private static final String[] perms = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
    private LocationCallback mLocationCallback;

    private MapViewModel mViewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MapViewModelFactory mapViewModelFactory = Injection.provideMapViewModelFactory(this.getActivity().getApplication());
        mViewModel = new ViewModelProvider(this, mapViewModelFactory).get(MapViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        mMapView = view.findViewById(R.id.mapView);
        mMapView.getMapAsync(this);
        mMapView.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        refresh();


        configureLocationCallBack();
        mViewModel.executeHttpRequestWithRetrofitPlaceStream(createObserver());


        return view;
    }


    @Override
    public void onMapReady(@NonNull GoogleMap mMap) {
        googleMap = mMap;

        //Request location updates:


        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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

        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            getContext(), R.raw.mapstyle));

            if (!success) {
                Log.e(TAG, "Style parsing failed.");
            }

        }catch (Resources.NotFoundException e){
            Log.e(TAG, "Can't find style. Error: ", e);
        }

        googleMap.getUiSettings().setRotateGesturesEnabled(true);
        googleMap.setOnMarkerClickListener(MapFragment.this::onClickMarker);

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.toolbar_menu, menu);
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
    public void onDestroyView(){
        super.onDestroyView();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();

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
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(this.mViewModel.getCurrentUserPosition()));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(13), 2000, null);
    }

    // -------------------
    // HTTP (RxJAVA)
    // -------------------

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




    private void configureLocationRequest(){
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());
        // Create the LocationRequest object
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(100 * 1000)        // 100 seconds, in milliseconds
                .setFastestInterval(1000); // 1 second, in milliseconds
    }

    // -----------------
    // ACTION
    // -----------------


    public void refresh() {
        // No GPS permission
        if (EasyPermissions.hasPermissions(getContext(),perms)) {
            mViewModel.startLocationRequest(getContext());
            mViewModel.getLocation().observe(getViewLifecycleOwner(),(location -> {
                if (location !=null){
                    mViewModel.updateCurrentUserPosition(new LatLng(location.getLat(), location.getLng()));
                }
            }));
        } else {
            EasyPermissions.requestPermissions(this,"Need permission for use MapView",
                    RC_LOCATION_CONTACTS_PERM, perms);
        }
    }

    private boolean onClickMarker(Marker marker){
        if (marker.getTag() != null){
            Log.e(TAG, "onClickMarker: " + marker.getTag() );
            Intent intent = new Intent(getActivity(),PlaceDetailActivity.class);
            String tag = marker.getTag().toString();
            intent.putExtra("PlaceDetailResult", tag);
            startActivity(intent);
            return true;
        }else{
            Log.e(TAG, "onClickMarker: ERROR NO TAG" );
            return false;
        }
    }


    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        refresh();
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        refresh();
    }
}