package com.example.go4lunch.controller.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import com.example.go4lunch.R;
import com.example.go4lunch.ViewModels.MatesViewModel;
import com.example.go4lunch.Views.ViewPagerAdapter;
import com.example.go4lunch.api.UserHelper;
import com.example.go4lunch.base.BaseActivity;
import com.example.go4lunch.controller.fragment.ChatFragment;
import com.example.go4lunch.controller.fragment.UsersFragment;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;
import java.util.Objects;

public class SelecteChatActivity extends BaseActivity {
    protected MatesViewModel mViewModel;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selecte_chat);
        retrieveCurrentUser();
        this.initView();
        this.configureToolbar();
    }

    // ---------------------
    // CONFIGURATION
    // ---------------------

    private void configureToolbar(){
        setSupportActionBar(mToolbar);
        ActionBar ab = getSupportActionBar();
        ab.setHomeButtonEnabled(true);
        ab.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                Intent homeIntent = new Intent(this, MainActivity.class);
                startActivity(homeIntent);
                finish();
        }
        return (super.onOptionsItemSelected(menuItem));
    }
    private void retrieveCurrentUser() {
        mViewModel = new ViewModelProvider(this).get(MatesViewModel.class);
        mViewModel.updateCurrentUserUID(Objects.requireNonNull(getCurrentUser()).getUid());
        UserHelper.getUsersCollection().document(getCurrentUser().getUid()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable DocumentSnapshot documentSnapshot, @javax.annotation.Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.e("SELECTED_ACTIVITY", "Listen failed.", e);
                    return;
                }
                if (documentSnapshot != null && documentSnapshot.exists()) {
                    Log.e("SELECTED_ACTIVITY", "Current data: " + documentSnapshot.getData());
                } else {
                    Log.e("SELECTED_ACTIVITY", "Current data: null");
                }
            }
        });
    }

    private void initView() {
        TabLayout tabLayout = findViewById(R.id.tabs);
        ViewPager viewPager = findViewById(R.id.view_pager);
        mToolbar = findViewById(R.id.simple_toolbar);

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFragment(new ChatFragment(),"Chats");
        viewPagerAdapter.addFragment(new UsersFragment(),"Users");

        viewPager.setAdapter(viewPagerAdapter);

        tabLayout.setupWithViewPager(viewPager);
    }


}