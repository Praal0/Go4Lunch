 package com.example.go4lunch.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import com.example.go4lunch.R;
import com.example.go4lunch.base.BaseActivity;
import com.example.go4lunch.fragment.ListFragment;
import com.example.go4lunch.fragment.MapFragment;
import com.example.go4lunch.fragment.WorkmatesFragment;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import java.util.Map;

import jp.wasabeef.glide.transformations.BlurTransformation;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener{

    private static final int TITLE_HUNGRY = R.string.hungry;
    // For design
    private Toolbar toolbar;
    private DrawerLayout drawer;
    private ImageView mImageView_bk,mImageView;
    private TextView mNameText,mEmailText;
    private NavigationView mNavigationView;
    private BottomNavigationView bottomNav;
    private static final int SIGN_OUT_TASK = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setSupportActionBar(toolbar);
        init();
        configureToolBar();
        initNavigationdrawer();
        configureBottomNav();
        configureNavigationView();
        updateUIWhenCreating();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_view,new MapFragment()).commit();
        toolbar.setTitle("I'm Hungry");
    }

    // --------------------
    // ACTIONS
    // --------------------

    private void init() {
        toolbar = findViewById(R.id.simple_toolbar);
        drawer = findViewById(R.id.drawer_layout);
        mNavigationView = findViewById(R.id.activity_main_nav_view);
        View headerContainer = mNavigationView.getHeaderView(0); // This returns the container layout in nav_drawer_header.xml (e.g., your RelativeLayout or LinearLayout)
        mImageView = headerContainer.findViewById(R.id.drawer_image);
        mImageView_bk = headerContainer.findViewById(R.id.drawer_image_bk);
        mNameText = headerContainer.findViewById(R.id.drawer_name);
        mEmailText = headerContainer.findViewById(R.id.drawer_email);
        bottomNav = findViewById(R.id.bottom_navigation);

    }

    // --------------------
    // UI
    // --------------------

    private OnSuccessListener<Void> updateUIAfterRESTRequestsCompleted(final int origin){
        return new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                switch (origin){
                    case SIGN_OUT_TASK:
                        launchActivity(LoginActivity.class,null);
                        finish();
                        break;
                    default:
                        break;
                }
            }
        };
    }

    private void launchActivity(Class mClass, Map<String,Object> info){
        Intent intent = new Intent(this, mClass);
        if (info != null){
            for (Object key : info.keySet()) {
                String mKey = (String)key;
                String value = (String) info.get(key);
                intent.putExtra(mKey, value);
            }
        }
        startActivity(intent);
    }

    // ---------------------
    // CONFIGURATION
    // ---------------------

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);

        return true;
    }


    private void initNavigationdrawer() {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
    }

    // Configure NavigationView
    private void configureNavigationView(){
        mNavigationView.setNavigationItemSelectedListener(this);
    }

    private void configureBottomNav() {
        bottomNav.setOnNavigationItemSelectedListener(navListener);
    }

    private void configureToolBar(){
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(TITLE_HUNGRY);
    }



    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = null;
                    switch (item.getItemId()) {
                        case R.id.nav_map:
                            toolbar.setTitle("I'm Hungry");
                            selectedFragment = new MapFragment();
                            break;

                        case R.id.nav_list:
                            toolbar.setTitle("I'm Hungry");
                            selectedFragment = new ListFragment();
                            break;

                        case R.id.nav_workmates:
                            toolbar.setTitle("Available Workmates");
                            selectedFragment = new WorkmatesFragment();
                            break;

                        case R.id.nav_tchat:
                            launchActivity(ChatActivity.class,null);
                            toolbar.setTitle("Tchat");
                            break;
                    }
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_view,
                            selectedFragment).commit();
                    return true;
                }
    };

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void updateUIWhenCreating(){

        if (this.getCurrentUser() != null){
            //Get picture URL from Firebase
            Glide.with(this)
                    .load(R.drawable.lunch)
                    .apply(RequestOptions.bitmapTransform(new BlurTransformation(30)))
                    .into(mImageView_bk);

            //Get email & username from Firebase
            String email = TextUtils.isEmpty(this.getCurrentUser().getEmail()) ? getString(R.string.info_no_email_found) : this.getCurrentUser().getEmail();
            String username = TextUtils.isEmpty(this.getCurrentUser().getDisplayName()) ? getString(R.string.info_no_username_found) : this.getCurrentUser().getDisplayName();

            //Get picture URL from Firebase
            if (this.getCurrentUser().getPhotoUrl() != null) {
                Glide.with(this)
                        .load(this.getCurrentUser().getPhotoUrl())
                        .apply(RequestOptions.circleCropTransform())
                        .into(mImageView);
            }
            //Update views with data
            mEmailText.setText(email);
            mNameText.setText(username);
        }
    }

    // ---------------------
    // ACTIONS
    // ---------------------
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle Navigation Item Click
        int id = item.getItemId();
        switch (id){
            case R.id.nav_lunch :
                break;

            case R.id.nav_setting:
                break;

            case R.id.nav_logout:
                this.signOutUserFromFirebase();
                break;

            default:
                break;
        }
        this.drawer.closeDrawer(GravityCompat.START);
        return true;

    }

    // --------------------
    // REST REQUEST
    // --------------------
    private void signOutUserFromFirebase() {
        AuthUI.getInstance()
                .signOut(this)
                .addOnSuccessListener(this, this.updateUIAfterRESTRequestsCompleted(SIGN_OUT_TASK));
    }
}