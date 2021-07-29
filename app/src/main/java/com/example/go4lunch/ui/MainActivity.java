 package com.example.go4lunch.ui;

 import android.Manifest;
 import android.content.Intent;
 import android.content.pm.ActivityInfo;
 import android.os.Bundle;
 import android.text.TextUtils;
 import android.util.Log;
 import android.view.Menu;
 import android.view.MenuItem;
 import android.view.View;
 import android.widget.ImageView;
 import android.widget.TextView;
 import android.widget.Toast;

 import androidx.annotation.NonNull;
 import androidx.appcompat.app.ActionBarDrawerToggle;
 import androidx.appcompat.widget.Toolbar;
 import androidx.core.view.GravityCompat;
 import androidx.drawerlayout.widget.DrawerLayout;
 import androidx.fragment.app.Fragment;
 import androidx.fragment.app.FragmentTransaction;
 import androidx.lifecycle.ViewModelProvider;

 import com.bumptech.glide.Glide;
 import com.bumptech.glide.request.RequestOptions;
 import com.example.go4lunch.R;
 import com.example.go4lunch.api.RestaurantsHelper;
 import com.example.go4lunch.api.UserHelper;
 import com.example.go4lunch.base.BaseActivity;
 import com.example.go4lunch.ui.chat.MessageActivity;
 import com.example.go4lunch.ui.detail.PlaceDetailActivity;
 import com.example.go4lunch.ui.list.ListFragment;
 import com.example.go4lunch.ui.login.LoginActivity;
 import com.example.go4lunch.ui.map.MapFragment;
 import com.example.go4lunch.ui.mates.MatesFragment;
 import com.example.go4lunch.ui.setting.SettingActivity;
 import com.example.go4lunch.viewModels.MatesViewModel;
 import com.firebase.ui.auth.AuthUI;
 import com.google.android.gms.tasks.OnSuccessListener;
 import com.google.android.material.bottomnavigation.BottomNavigationView;
 import com.google.android.material.navigation.NavigationView;
 import com.google.firebase.firestore.DocumentSnapshot;
 import com.google.firebase.firestore.EventListener;
 import com.google.firebase.firestore.FirebaseFirestoreException;
 import com.google.firebase.firestore.QueryDocumentSnapshot;

 import java.util.HashMap;
 import java.util.List;
 import java.util.Map;

 import jp.wasabeef.glide.transformations.BlurTransformation;
 import pub.devrel.easypermissions.AfterPermissionGranted;
 import pub.devrel.easypermissions.AppSettingsDialog;
 import pub.devrel.easypermissions.EasyPermissions;

 public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener, EasyPermissions.PermissionCallbacks{

    // For design
    private Toolbar toolbar;
    private DrawerLayout drawer;
    private NavigationView mNavigationView;
    private BottomNavigationView bottomNav;
    private static final int SIGN_OUT_TASK = 10;
    protected MatesViewModel mViewModel;
     private static final String[] perms = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
        initialize();

    }

    @AfterPermissionGranted(124)
     private void initialize() {
        if (!EasyPermissions.hasPermissions(this,perms)){
            EasyPermissions.requestPermissions(this,"Need permission for use MapView and ListView",
                    124, perms);
        }else{
            loadLocale();
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

    // Void Launch activity
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
        finish();
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
            Fragment newFragment = new Fragment();
            switch (item.getItemId()) {
                case R.id.nav_map:
                    toolbar.setTitle(R.string.hungry);
                    newFragment = MapFragment.newInstance();
                    break;

                case R.id.nav_list:
                    toolbar.setTitle(R.string.hungry);
                    newFragment = ListFragment.newInstance();
                    break;

                case R.id.nav_workmates:
                    toolbar.setTitle(R.string.workmates);
                    newFragment = new MatesFragment();
                    break;

                case R.id.nav_tchat:
                    launchActivity(MessageActivity.class,null);
                    break;
            }
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            // Replace whatever is in the fragment_container view with this fragment,
            // and add the transaction to the back stack if needed
            transaction.replace(R.id.fragment_view, newFragment);
            transaction.addToBackStack(null);
            // Commit the transaction
            transaction.commit();
            return true;
        });
    }

    private void configureToolBar(){
        setSupportActionBar(toolbar);
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
            finish();
        }
    }

    private void retrieveCurrentUser(){
        mViewModel = new ViewModelProvider(this).get(MatesViewModel.class);
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
            }else{
                Glide.with(this)
                        .load(R.drawable.ic_anon_user_48dp)
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

     @Override
     public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
         super.onRequestPermissionsResult(requestCode, permissions, grantResults);
         EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
     }

     @Override
     public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        initialize();
     }

     @Override
     public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
         if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
             new AppSettingsDialog.Builder(this).build().show();
         }
     }
 }