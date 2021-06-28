package com.example.go4lunch.controller.fragment;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.go4lunch.R;
import com.example.go4lunch.Utils.ItemClickSupport;
import com.example.go4lunch.ViewModels.CommunicationViewModel;
import com.example.go4lunch.ViewModels.MapViewModel;
import com.example.go4lunch.Views.RestaurantAdapter;
import com.example.go4lunch.controller.activities.PlaceDetailActivity;
import com.example.go4lunch.injection.Injection;
import com.example.go4lunch.injection.MapViewModelFactory;
import com.example.go4lunch.models.PlacesInfo.PlacesDetails.PlaceDetailsResults;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.observers.DisposableObserver;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ListFragment extends BaseFragment implements EasyPermissions.PermissionCallbacks{

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private RecyclerView mRecyclerView;
    private List<PlaceDetailsResults> mResults;
    private RestaurantAdapter adapter;
    private static final String[] perms = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
    private static final int RC_LOCATION_CONTACTS_PERM = 124;

    private MapViewModel mViewModel;

    public ListFragment() {
        // Required empty public constructor
    }

    public static ListFragment newInstance() {
        ListFragment fragment = new ListFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MapViewModelFactory mapViewModelFactory = Injection.provideMapViewModelFactory(this.getActivity().getApplication());
        mViewModel = new ViewModelProvider(this, mapViewModelFactory).get(MapViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_list, container, false);
        mRecyclerView = view.findViewById(R.id.list_recycler_view);
        // Inflate the layout for this fragment
        refresh();
        configureRecyclerView();
        setHasOptionsMenu(true);
        this.configureOnClickRecyclerView();

        return view;

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

    private void configureRecyclerView() {
        this.mResults = new ArrayList<>();
        this.adapter = new RestaurantAdapter(this.mResults, mViewModel.getCurrentUserPositionF());
        this.mRecyclerView.setAdapter(this.adapter);
        this.mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    private void updateUI(List<PlaceDetailsResults> results){
        mResults.clear();
        if (results.size() > 0){
            mResults.addAll(results);
        }else{
            Toast.makeText(getContext(), getResources().getString(R.string.no_restaurant_error_message), Toast.LENGTH_SHORT).show();
        }
        adapter.notifyDataSetChanged();
    }

    public void refresh() {
        // No GPS permission
        if (checkLocationPermission()) {
            mViewModel.startLocationRequest(getContext());
            mViewModel.getLocation().observe(getViewLifecycleOwner(),(location -> {
                if (location !=null){
                    mViewModel.updateCurrentUserPosition(new LatLng(location.getLat(), location.getLng()));
                    mViewModel.executeHttpRequestWithRetrofitFetchPlaceInfo(createObserver());
                }
            }));
        }else{
            EasyPermissions.requestPermissions(this,"Need permission for use MapView",
                    RC_LOCATION_CONTACTS_PERM, perms);
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
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        refresh();
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        refresh();
    }
}