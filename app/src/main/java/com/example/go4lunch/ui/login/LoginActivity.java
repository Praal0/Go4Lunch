package com.example.go4lunch.ui.login;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.ViewModelProvider;

import com.example.go4lunch.R;
import com.example.go4lunch.viewModels.MatesViewModel;
import com.example.go4lunch.api.UserHelper;
import com.example.go4lunch.ui.MainActivity;
import com.firebase.ui.auth.AuthUI;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.example.go4lunch.base.BaseActivity;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;

public class LoginActivity extends BaseActivity {

    // FOR DATA
    private static final int RC_SIGN_IN = 1000;
    private String URL_ICON = "https://cdn1.iconfinder.com/data/icons/material-core/20/account-circle-512.png";
    private MatesViewModel mViewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(MatesViewModel.class);
        setContentView(R.layout.activity_login);
        if (!this.isCurrentUserLogged()){
            this.startSignInActivity();
        }else {
            this.mViewModel.updateCurrentUserUID(getCurrentUser().getUid());
            launchMainActivity();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Handle SignIn Activity response on activity result
        this.handleResponseAfterSignIn(requestCode, resultCode, data);
    }

    // --------------------
    // UTILS
    // --------------------
    private void handleResponseAfterSignIn(int requestCode, int resultCode, Intent data){
        IdpResponse response = IdpResponse.fromResultIntent(data);
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) { // SUCCESS
                this.createUserInFirestone();
                launchMainActivity();
            } else { // ERRORS
                if (response == null) {
                    Toast.makeText(this, getString(R.string.error_authentication_canceled), Toast.LENGTH_SHORT).show();
                    finish();
                } else if (response.getError().getErrorCode() == ErrorCodes.NO_NETWORK) {
                    Toast.makeText(this, getString(R.string.error_no_internet), Toast.LENGTH_SHORT).show();
                } else if (response.getError().getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                    Toast.makeText(this, getString(R.string.error_unknown_error), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
    
    private void startSignInActivity(){

        // Choose authentication providers
          List<AuthUI.IdpConfig> providers = Arrays.asList(
                  new AuthUI.IdpConfig.EmailBuilder().build(),
                  new AuthUI.IdpConfig.GoogleBuilder().build(),
                  new AuthUI.IdpConfig.FacebookBuilder().build());


        // Launch the activity
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setTheme(R.style.LoginTheme)
                        .setAvailableProviders(providers)
                        .setLockOrientation(true)
                        .setIsSmartLockEnabled(false, true)
                        .setLogo(R.drawable.meal_logo_login)
                        .build(),
                RC_SIGN_IN);
    }


    // --------------------
    // ACTION
    // --------------------

    private void launchMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    // --------------------
    // REST REQUEST
    // --------------------

    // Http request that create user in firestore
    private void createUserInFirestone(){
        String uid;
        String username;
        String urlPicture;
        if (this.getCurrentUser() != null){
            Log.e("LOGIN_ACTIVITY", "createUserInFirestore: LOGGED" );
            if (this.getCurrentUser().getPhotoUrl() != null){
                 urlPicture = this.getCurrentUser().getPhotoUrl().toString();
            }else{
                urlPicture = URL_ICON;
            }
             username = this.getCurrentUser().getDisplayName();
             uid = this.getCurrentUser().getUid();
            UserHelper.createUser(uid, username, urlPicture,false).addOnFailureListener(this.onFailureListener());
        }else{
            Log.e("LOGIN_ACTIVITY", "createUserInFirestore: NOT LOGGED" );
        }
    }
}