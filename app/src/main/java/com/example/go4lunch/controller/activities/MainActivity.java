 package com.example.go4lunch.controller.activities;

 import android.content.Intent;
 import android.os.Bundle;
 import android.text.TextUtils;
 import android.util.Log;
 import android.view.Menu;
 import android.view.MenuItem;
 import android.view.View;
 import android.widget.ImageView;
 import android.widget.TextView;
 import android.widget.Toast;

 import androidx.appcompat.app.ActionBarDrawerToggle;
 import androidx.appcompat.widget.Toolbar;
 import androidx.core.view.GravityCompat;
 import androidx.drawerlayout.widget.DrawerLayout;
 import androidx.fragment.app.Fragment;
 import androidx.lifecycle.ViewModelProvider;
 import androidx.lifecycle.ViewModelProviders;

 import com.bumptech.glide.Glide;
 import com.bumptech.glide.request.RequestOptions;
 import com.example.go4lunch.R;
 import com.example.go4lunch.ViewModels.CommunicationViewModel;
 import com.example.go4lunch.api.RestaurantsHelper;
 import com.example.go4lunch.api.UserHelper;
 import com.example.go4lunch.base.BaseActivity;
 import com.example.go4lunch.controller.fragment.ListFragment;
 import com.example.go4lunch.controller.fragment.MapFragment;
 import com.example.go4lunch.controller.fragment.MatesFragment;
 import com.firebase.ui.auth.AuthUI;
 import com.google.android.gms.tasks.OnSuccessListener;
 import com.google.android.material.bottomnavigation.BottomNavigationView;
 import com.google.android.material.navigation.NavigationView;
 import com.google.firebase.firestore.DocumentSnapshot;
 import com.google.firebase.firestore.EventListener;
 import com.google.firebase.firestore.FirebaseFirestoreException;
 import com.google.firebase.firestore.QueryDocumentSnapshot;

 import java.util.HashMap;
 import java.util.Map;

 import jp.wasabeef.glide.transformations.BlurTransformation;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener{

    // For design
    private Toolbar toolbar;
    private DrawerLayout drawer;
    private NavigationView mNavigationView;
    private BottomNavigationView bottomNav;
    private static final int SIGN_OUT_TASK = 10;
    protected CommunicationViewModel mViewModel;

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
        retrieveCurrentUser();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_view,new MapFragment()).commit();

    }

    // --------------------
    // ACTIONS
    // --------------------

    private void init() {
        toolbar = findViewById(R.id.simple_toolbar);
        drawer = findViewById(R.id.drawer_layout);
        mNavigationView = findViewById(R.id.activity_main_nav_view);
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
        bottomNav.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            switch (item.getItemId()) {
                case R.id.nav_map:
                    toolbar.setTitle(R.string.hungry);
                    selectedFragment = new MapFragment();
                    break;

                case R.id.nav_list:
                    toolbar.setTitle(R.string.hungry);
                    selectedFragment = new ListFragment();
                    break;

                case R.id.nav_workmates:
                    toolbar.setTitle(R.string.workmates);
                    selectedFragment = new MatesFragment();
                    break;

                case R.id.nav_tchat:
                    launchActivity(ChatActivity.class,null);
                    toolbar.setTitle(R.string.chatTitle);
                    break;
            }
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_view,
                    selectedFragment).commit();
            return true;
        });
    }

    private void configureToolBar(){
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.hungry);
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void retrieveCurrentUser(){
        mViewModel = new ViewModelProvider(this).get(CommunicationViewModel.class);
        mViewModel.updateCurrentUserUID(getCurrentUser().getUid());
        UserHelper.getUsersCollection().document(getCurrentUser().getUid()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable DocumentSnapshot documentSnapshot, @javax.annotation.Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.e("MAIN_ACTIVITY", "Listen failed.", e);
                    return;
                }

                if (documentSnapshot != null && documentSnapshot.exists()) {
                    Log.e("MAIN_ACTIVITY", "Current data: " + documentSnapshot.getData());
                } else {
                    Log.e("MAIN_ACTIVITY", "Current data: null");
                }
            }
        });
    }

    private void updateUIWhenCreating(){

        if (this.getCurrentUser() != null){
            View headerContainer = mNavigationView.getHeaderView(0); // This returns the container layout in nav_drawer_header.xml (e.g., your RelativeLayout or LinearLayout)
            ImageView mImageView = headerContainer.findViewById(R.id.drawer_image);
            ImageView mImageView_bk = headerContainer.findViewById(R.id.drawer_image_bk);
            TextView mNameText = headerContainer.findViewById(R.id.drawer_name);
            TextView mEmailText = headerContainer.findViewById(R.id.drawer_email);

            Glide.with(this)
                    .load(R.drawable.lunch)
                    .apply(RequestOptions.bitmapTransform(new BlurTransformation(30)))
                    .into(mImageView_bk);

            //Get picture URL from Firebase
            if (this.getCurrentUser().getPhotoUrl() != null) {
                Glide.with(this)
                        .load(this.getCurrentUser().getPhotoUrl())
                        .apply(RequestOptions.circleCropTransform())
                        .into(mImageView);
            }

            //Get email from Firebase
            String email = TextUtils.isEmpty(this.getCurrentUser().getEmail()) ? getString(R.string.info_no_email_found) : this.getCurrentUser().getEmail();
            String name = TextUtils.isEmpty(this.getCurrentUser().getDisplayName()) ? getString(R.string.info_no_username_found) : this.getCurrentUser().getDisplayName();

            //Update views with data
            mEmailText.setText(email);
            mNameText.setText(name);
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
                RestaurantsHelper.getBooking(getCurrentUser().getUid(),getTodayDate()).addOnCompleteListener(bookingTask -> {
                    if (bookingTask.isSuccessful()){
                        if (bookingTask.getResult().isEmpty()){
                            Toast.makeText(this, getResources().getString(R.string.drawer_no_restaurant_booked), Toast.LENGTH_SHORT).show();
                        }else{
                            Map<String,Object> extra = new HashMap<>();
                            for (QueryDocumentSnapshot booking : bookingTask.getResult()){
                                extra.put("PlaceDetailResult",booking.getData().get("restaurantId"));
                            }
                            launchActivity(PlaceDetailActivity.class,extra);
                        }

                    }
                });
                break;

            case R.id.nav_setting:
                launchActivity(SettingActivity.class,null);
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