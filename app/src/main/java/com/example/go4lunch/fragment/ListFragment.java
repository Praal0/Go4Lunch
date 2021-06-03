package com.example.go4lunch.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.go4lunch.R;
import com.example.go4lunch.Utils.ItemClickSupport;
import com.example.go4lunch.Utils.PlacesStreams;
import com.example.go4lunch.Views.RestaurantAdapter;
import com.example.go4lunch.activities.PlaceDetailActivity;
import com.example.go4lunch.activities.ViewModels.CommunicationViewModel;
import com.example.go4lunch.models.PlacesInfo.PlacesDetails.PlaceDetailsResults;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;

import static android.content.ContentValues.TAG;
import static com.example.go4lunch.fragment.MapFragment.SEARCH_TYPE;

/**
 * A simple {@link Fragment} subclass.

 * create an instance of this fragment.
 */
public class ListFragment extends BaseFragment {


    RecyclerView mRecyclerView;
    SwipeRefreshLayout mSwipeRefreshLayout;

    private Disposable disposable;
    private List<PlaceDetailsResults> mResults;
    private RestaurantAdapter adapter;

    private CommunicationViewModel mViewModel;

    public static ListFragment newInstance() {
        return new ListFragment();
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initItem();
        mViewModel = new ViewModelProvider(this).get(CommunicationViewModel.class);
    }

    private void initItem() {
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);

        mRecyclerView = getView().findViewById(R.id.list_recycler_view);
        mSwipeRefreshLayout = getView().findViewById(R.id.list_swipe_refresh);

        setHasOptionsMenu(true);

        mViewModel.currentUserPosition.observe(getViewLifecycleOwner(), latLng -> {
            executeHttpRequestWithRetrofit();
            configureRecyclerView();
        });

        this.configureOnClickRecyclerView();
        this.configureOnSwipeRefresh();

        return view;
    }

    private void configureOnClickRecyclerView() {
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
        this.adapter = new RestaurantAdapter(this.mResults, mViewModel.getCurrentUserPositionFormatted());
        this.mRecyclerView.setAdapter(this.adapter);
        this.mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        RecyclerView.ItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(), 100);
        mRecyclerView.addItemDecoration(dividerItemDecoration);
    }

    private void executeHttpRequestWithRetrofit(){
        String location = mViewModel.getCurrentUserPositionFormatted();
        Log.e(TAG, "Location : "+location );
        disposable = PlacesStreams.streamFetchPlaceInfo(mViewModel.getCurrentUserPositionFormatted(), mViewModel.getCurrentUserRadius(),
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
                getActivity().runOnUiThread(() -> mSwipeRefreshLayout.setRefreshing(false));
                handleError(e);}
            @Override
            public void onComplete() { }
        };
    }

    private void disposeWhenDestroy() {
        if (this.disposable != null && !this.disposable.isDisposed()) this.disposable.dispose();
    }

    // -------------------
    // UPDATE UI
    // -------------------

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
}