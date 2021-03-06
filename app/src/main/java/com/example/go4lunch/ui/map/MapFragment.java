package com.example.go4lunch.ui.map;

import android.Manifest;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.example.go4lunch.BuildConfig;
import com.example.go4lunch.R;
import com.example.go4lunch.api.PlacesStreams;
import com.example.go4lunch.models.PlacesInfo.PlacesDetails.PlaceDetailsResults;
import com.example.go4lunch.viewModels.MapViewModel;
import com.example.go4lunch.api.RestaurantsHelper;
import com.example.go4lunch.base.BaseFragment;
import com.example.go4lunch.ui.MainActivity;
import com.example.go4lunch.ui.detail.PlaceDetailActivity;
import com.example.go4lunch.injection.Injection;
import com.example.go4lunch.injection.MapViewModelFactory;
import com.example.go4lunch.models.PlacesInfo.MapPlacesInfo;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.observers.DisposableObserver;
import pub.devrel.easypermissions.EasyPermissions;


public class MapFragment extends BaseFragment implements OnMapReadyCallback, LocationListener {

    public static final String API_KEY = BuildConfig.API_KEY;
    private static final String TAG = MapFragment.class.getSimpleName();
    private static final String[] perms = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

    public static final String SEARCH_TYPE = "restaurant";
    private static final int RC_LOCATION_CONTACTS_PERM = 124;

    private GoogleMap googleMap;
    private MapView mMapView;
    private MapViewModel mViewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MapViewModelFactory mapViewModelFactory = Injection.provideMapViewModelFactory(this.getActivity().getApplication());
        mViewModel = new ViewModelProvider(this, mapViewModelFactory).get(MapViewModel.class);
    }

    public static MapFragment newInstance() {
        return new MapFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        mMapView = view.findViewById(R.id.mapView);
        mMapView.getMapAsync(this);
        mMapView.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        refresh();
        return view;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap mMap) {
        if (checkLocationPermission()) {
            googleMap = mMap;
            //Request location updates:
            googleMap.setMyLocationEnabled(true);
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
        }else{
            EasyPermissions.requestPermissions(this,"Need permission for use MapView and ListView",
                    RC_LOCATION_CONTACTS_PERM, String.valueOf(perms));
        }

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.toolbar_menu, menu);

        SearchManager searchManager = (SearchManager) getContext().getSystemService(Context.SEARCH_SERVICE);

        MenuItem item = menu.findItem(R.id.menu_search);
        SearchView searchView = new SearchView(((MainActivity) getContext()).getSupportActionBar().getThemedContext());
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW | MenuItem.SHOW_AS_ACTION_IF_ROOM);
        item.setActionView(searchView);
        searchView.setQueryHint(getResources().getString(R.string.toolbar_search_hint));
        searchView.setSearchableInfo(searchManager.getSearchableInfo(((MainActivity) getContext()).getComponentName()));

        searchView.setIconifiedByDefault(false);// Do not iconify the widget; expand it by default
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (query.length() > 3 ){
                    PlacesStreams.streamFetchAutoCompleteInfo(query,mViewModel.getCurrentUserPositionFormatted(),1000,API_KEY).subscribeWith(createObserver());
                }else{
                    Toast.makeText(getContext(), getResources().getString(R.string.search_too_short), Toast.LENGTH_LONG).show();
                    mViewModel.executeHttpRequestWithRetrofitPlaceStream(createObserver());
                }
                return true;

            }
            @Override
            public boolean onQueryTextChange(String query) {
                if (query.length() > 3){
                    PlacesStreams.streamFetchAutoCompleteInfo(query,mViewModel.getCurrentUserPositionFormatted(),1000,API_KEY).subscribeWith(createObserver());
                }else{
                    mViewModel.executeHttpRequestWithRetrofitPlaceStream(createObserver());
                }
                return false;
            }
        });
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
            makerMapPlacesInfo(result);
        }else if(results instanceof ArrayList){
            Log.e(TAG, "updateUI: SEARCH_NUMBER : " + ((ArrayList)results).size() );
            makerArrayList(results);
        }
    }

    private <T> void makerArrayList(T results) {
        if (((ArrayList)results).size() > 0){
            for(Object result : ((ArrayList)results)){
                PlaceDetailsResults detail = ((PlaceDetailsResults) result);
                RestaurantsHelper.getTodayBooking(detail.getPlaceId(), getTodayDate()).addOnCompleteListener(restaurantTask -> {
                    if (restaurantTask.isSuccessful()) {
                        Double lat = detail.getGeometry().getLocation().getLat();
                        Double lng = detail.getGeometry().getLocation().getLng();
                        String title = detail.getName();

                        MarkerOptions markerOptions = new MarkerOptions();
                        markerOptions.position(new LatLng(lat, lng));
                        markerOptions.title(title);
                        if (restaurantTask.getResult().isEmpty()) {
                            Bitmap bitmap = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(),
                                    R.drawable.baseline_place_unbook),
                                    128,128,true);// If there is no booking for today
                            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(bitmap));
                        } else { // If there is booking for today
                            Bitmap bitmap = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(),
                                    R.drawable.baseline_place_booked),
                                    128,128,true);// If there is no booking for today
                            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(bitmap));
                        }
                        Marker marker = googleMap.addMarker(markerOptions);
                        marker.setTag(detail.getPlaceId());
                    }
                });
            }
        }else{
            Toast.makeText(getContext(), getResources().getString(R.string.no_restaurant_error_message), Toast.LENGTH_LONG).show();
        }
    }

    private void makerMapPlacesInfo(MapPlacesInfo result) {
        Log.e(TAG, "updateUI: " + result .getResults().size());
        if (result.getResults().size() > 0){
            for (int i = 0; i < result.getResults().size(); i++) {
                int CurrentObject = i;
                RestaurantsHelper.getTodayBooking(result.getResults().get(CurrentObject).getPlaceId(), getTodayDate()).addOnCompleteListener(restaurantTask -> {
                    if (restaurantTask.isSuccessful()) {
                        Double lat = result.getResults().get(CurrentObject).getGeometry().getLocation().getLat();
                        Double lng = result.getResults().get(CurrentObject).getGeometry().getLocation().getLng();
                        String title = result.getResults().get(CurrentObject).getName();

                        MarkerOptions markerOptions = new MarkerOptions();
                        markerOptions.position(new LatLng(lat, lng));
                        markerOptions.title(title);
                        if (restaurantTask.getResult().isEmpty()) { // If there is no booking for today
                            Bitmap bitmap = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(),
                                    R.drawable.baseline_place_unbook),
                                    128,128,true);// If there is no booking for today
                            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(bitmap));
                        } else { // If there is booking for today
                            Bitmap bitmap = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(),
                                    R.drawable.baseline_place_booked),
                                    128,128,true);// If there is no booking for today
                            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(bitmap));
                        }
                        Marker marker = googleMap.addMarker(markerOptions);
                        marker.setTag(result.getResults().get(CurrentObject).getPlaceId());
                    }
                });
            }
        }else{ Toast.makeText(getContext(), getResources().getString(R.string.no_restaurant_error_message), Toast.LENGTH_SHORT).show();
        }
    }

    // -----------------
    // ACTION
    // -----------------
    public void refresh() {
        // No GPS permission
        if (checkLocationPermission()) {
            mViewModel.startLocationRequest(getContext());
            mViewModel.getLocation().observe(getViewLifecycleOwner(),(location -> {
                if (location !=null){
                    LatLng latLng = new LatLng(location.getLat(),location.getLng());
                    mViewModel.updateCurrentUserPosition(latLng);
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,15));
                    googleMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
                    mViewModel.executeHttpRequestWithRetrofitPlaceStream(createObserver());
                }
            }));
        }else{
            EasyPermissions.requestPermissions(this,"Need permission for use MapView and ListView",
                    RC_LOCATION_CONTACTS_PERM, perms);
            refresh();
        }
    }

    private boolean onClickMarker(Marker marker){
        if (marker.getTag() != null){
            Log.e(TAG, "onClickMarker: " + marker.getTag() );
            Intent intent = new Intent(getActivity(),PlaceDetailActivity.class);
            String tag = marker.getTag().toString();
            intent.putExtra("PlaceDetailResult", tag);
            startActivity(intent);
            getActivity().finish();
            return true;
        }else{
            Log.e(TAG, "onClickMarker: ERROR NO TAG" );
            return false;
        }
    }

    private boolean checkLocationPermission() {
        if (EasyPermissions.hasPermissions(getContext(), perms)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        refresh();
    }
}