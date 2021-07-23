package com.example.go4lunch.ui.list;

import android.Manifest;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.go4lunch.BuildConfig;
import com.example.go4lunch.R;
import com.example.go4lunch.utils.ItemClickSupport;
import com.example.go4lunch.api.PlacesStreams;
import com.example.go4lunch.viewModels.MapViewModel;
import com.example.go4lunch.base.BaseFragment;
import com.example.go4lunch.ui.map.MapFragment;
import com.example.go4lunch.ui.MainActivity;
import com.example.go4lunch.ui.detail.PlaceDetailActivity;
import com.example.go4lunch.injection.Injection;
import com.example.go4lunch.injection.MapViewModelFactory;
import com.example.go4lunch.models.PlacesInfo.PlacesDetails.PlaceDetailsResults;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.observers.DisposableObserver;
import pub.devrel.easypermissions.EasyPermissions;

public class ListFragment extends BaseFragment implements EasyPermissions.PermissionCallbacks{

    private String API_KEY = BuildConfig.API_KEY;
    private RecyclerView mRecyclerView;
    private List<PlaceDetailsResults> mResults;
    private RestaurantAdapter adapter;
    private static final String[] perms = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
    private static final int RC_LOCATION_CONTACTS_PERM = 124;

    private MapViewModel mViewModel;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    public ListFragment() {
        // Required empty public constructor
    }

    public static ListFragment newInstance() {
        return new ListFragment();
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
        mSwipeRefreshLayout = view.findViewById(R.id.list_swipe_refresh);
        // Inflate the layout for this fragment
        refresh();
        setHasOptionsMenu(true);
        this.configureOnClickRecyclerView();
        this.configureOnSwipeRefresh();
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
                getActivity().runOnUiThread(() -> mSwipeRefreshLayout.setRefreshing(false));
                handleError(e);}
            @Override
            public void onComplete() { }
        };
    }

    private void executeHttpRequestWithRetrofit(){
        mSwipeRefreshLayout.setRefreshing(true);
        PlacesStreams.streamFetchPlaceInfo(mViewModel.getCurrentUserPositionFormatted(), 1000, MapFragment.SEARCH_TYPE,API_KEY).subscribeWith(createObserver());
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
                }
                return true;

            }
            @Override
            public boolean onQueryTextChange(String query) {
                if (query.length() > 3){
                    PlacesStreams.streamFetchAutoCompleteInfo(query,mViewModel.getCurrentUserPositionFormatted(),1000,API_KEY).subscribeWith(createObserver());
                }
                return false;
            }
        });
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

    private void configureOnSwipeRefresh() {
        mSwipeRefreshLayout.setOnRefreshListener(this::executeHttpRequestWithRetrofit);
    }

    private void configureRecyclerView() {
        this.mResults = new ArrayList<>();
        this.adapter = new RestaurantAdapter(this.mResults, mViewModel.getCurrentUserPositionF());
        this.mRecyclerView.setAdapter(this.adapter);
        this.mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(getResources().getDrawable(R.drawable.divider));
        mRecyclerView.addItemDecoration(dividerItemDecoration);
    }

    private void updateUI(List<PlaceDetailsResults> results){
        mSwipeRefreshLayout.setRefreshing(false);
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
        if (EasyPermissions.hasPermissions(getContext(), perms)) {
            mViewModel.startLocationRequest(getContext());
            mViewModel.getLocation().observe(getViewLifecycleOwner(),(location -> {
                if (location !=null){
                    mViewModel.updateCurrentUserPosition(new LatLng(location.getLat(), location.getLng()));
                    mViewModel.executeHttpRequestWithRetrofitFetchPlaceInfo(createObserver());
                    configureRecyclerView();
                }
            }));
        }else{
            EasyPermissions.requestPermissions(this,"Need permission for use MapView",
                    RC_LOCATION_CONTACTS_PERM, perms);
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