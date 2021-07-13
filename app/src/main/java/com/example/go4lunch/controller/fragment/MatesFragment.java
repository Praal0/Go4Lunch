package com.example.go4lunch.controller.fragment;

import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.go4lunch.R;
import com.example.go4lunch.Utils.ItemClickSupport;
import com.example.go4lunch.ViewModels.MatesViewModel;
import com.example.go4lunch.Views.MatesAdapter;
import com.example.go4lunch.api.RestaurantsHelper;
import com.example.go4lunch.api.UserHelper;
import com.example.go4lunch.base.BaseFragment;
import com.example.go4lunch.controller.activities.PlaceDetailActivity;
import com.example.go4lunch.models.User;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class MatesFragment extends BaseFragment {

    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private List<User> mUsers;
    private MatesAdapter mMatesAdapter;

    private MatesViewModel mViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_mates, container, false);
        mRecyclerView = view.findViewById(R.id.mates_recycler_view);
        mSwipeRefreshLayout = view.findViewById(R.id.list_swipe_refresh);

        setHasOptionsMenu(true);

        mViewModel = new ViewModelProvider(getActivity()).get(MatesViewModel.class);
        mViewModel.currentUserUID.observe(getViewLifecycleOwner(), uid -> {
            configureRecyclerView();
            updateUIWhenCreating();
            configureOnClickRecyclerView();
            configureOnSwipeRefresh();
        });

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
    }

    // -----------------
    // CONFIGURATION
    // -----------------

    // Configure RecyclerView, Adapter, LayoutManager & glue it together
    private void configureRecyclerView(){
        this.mUsers = new ArrayList<>();
        this.mMatesAdapter = new MatesAdapter(this.mUsers);
        this.mRecyclerView.setAdapter(this.mMatesAdapter);
        this.mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(getResources().getDrawable(R.drawable.divider));
        mRecyclerView.addItemDecoration(dividerItemDecoration);
    }

    private void configureOnSwipeRefresh(){
        mSwipeRefreshLayout.setOnRefreshListener(this::updateUIWhenCreating);
    }

    // -----------------
    // ACTION
    // -----------------

    // Configure item click on RecyclerView
    private void configureOnClickRecyclerView(){
        ItemClickSupport.addTo(mRecyclerView, R.layout.fragment_mates_item)
                .setOnItemClickListener((recyclerView, position, v) -> {
                    User result = mMatesAdapter.getMates(position);
                    retrieveBookedRestaurantByUser(result);
                });
    }

    private void retrieveBookedRestaurantByUser(User user){
        RestaurantsHelper.getBooking(user.getUid(),getTodayDate()).addOnCompleteListener(bookingTask -> {
            if (bookingTask.isSuccessful()){
                if (!(bookingTask.getResult().isEmpty())){
                    for (QueryDocumentSnapshot booking : bookingTask.getResult()){
                        showBookedRestaurantByUser(booking.getData().get("restaurantId").toString());
                    }
                }
            }
        });
    }

    private void showBookedRestaurantByUser(String placeId){
        Intent intent = new Intent(getActivity(), PlaceDetailActivity.class);
        intent.putExtra("PlaceDetailResult", placeId);
        startActivity(intent);
    }

    // --------------------
    // UI
    // --------------------

    // Update UI when activity is creating
    private void updateUIWhenCreating(){
        mSwipeRefreshLayout.setRefreshing(true);
        CollectionReference collectionReference = UserHelper.getUsersCollection();
        collectionReference.get().addOnCompleteListener(task -> {
            mSwipeRefreshLayout.setRefreshing(false);
            if (task.isSuccessful()){
                mUsers.clear();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    if (!(getCurrentUser().getUid().toString().equals(document.getData().get("uid").toString()))){
                        String uid = document.getData().get("uid").toString();
                        String username = document.getData().get("username").toString();
                        String urlPicture = document.getData().get("urlPicture").toString();
                        User userToAdd = new User(uid,username,urlPicture,false);
                        mUsers.add(userToAdd);
                    }
                }
            }else {
                getActivity().runOnUiThread(() -> mSwipeRefreshLayout.setRefreshing(false));
                Log.e("TAG", "Error getting documents: ", task.getException());
            }
            mMatesAdapter.notifyDataSetChanged();
        });
    }
}