package com.example.go4lunch.ui.user;

import android.content.Intent;
import android.os.Bundle;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.go4lunch.R;
import com.example.go4lunch.utils.ItemClickSupport;
import com.example.go4lunch.viewModels.MatesViewModel;
import com.example.go4lunch.ui.user.UserAdapter;
import com.example.go4lunch.api.UserHelper;
import com.example.go4lunch.base.BaseFragment;
import com.example.go4lunch.ui.chat.MessageActivity;
import com.example.go4lunch.models.User;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;


public class UsersFragment extends BaseFragment {
    public static final String USER_DATA = "user";
    public static final String CURRENT_USER_DATA = "userCurrent";

    private RecyclerView mRecyclerView;
    private List<User> mUsers;
    private UserAdapter mUserAdapter;
    private MatesViewModel mViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_users, container, false);
        mRecyclerView = view.findViewById(R.id.user_recycler_view);

        setHasOptionsMenu(true);

        mViewModel = new ViewModelProvider(getActivity()).get(MatesViewModel.class);
        mViewModel.currentUserUID.observe(getViewLifecycleOwner(), uid -> {
            configureRecyclerView();
            updateUIWhenCreating();
            configureOnClickRecyclerView();
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
        this.mUserAdapter = new UserAdapter(this.mUsers);
        this.mRecyclerView.setAdapter(this.mUserAdapter);
        this.mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(getResources().getDrawable(R.drawable.divider));
        mRecyclerView.addItemDecoration(dividerItemDecoration);
    }

    // -----------------
    // ACTION
    // -----------------

    // Configure item click on RecyclerView
    private void configureOnClickRecyclerView(){
        ItemClickSupport.addTo(mRecyclerView, R.layout.fragment_mates_item)
                .setOnItemClickListener((recyclerView, position, v) -> {
                    Intent intent = new Intent(getContext(), MessageActivity.class);
                    intent.putExtra(USER_DATA,mUserAdapter.getMates(position));
                    User user = new User(getCurrentUser().getUid(),getCurrentUser().getDisplayName(),"",false);
                    intent.putExtra(CURRENT_USER_DATA,user);
                    startActivity(intent);
                });
    }

    // Update UI when activity is creating
    private void updateUIWhenCreating(){
        CollectionReference collectionReference = UserHelper.getUsersCollection();
        collectionReference.get().addOnCompleteListener(task -> {
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
                Log.e("TAG", "Error getting documents: ", task.getException());
            }
            mUserAdapter.notifyDataSetChanged();
        });
    }
}