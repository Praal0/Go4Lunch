package com.example.go4lunch.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.ViewModelProvider;

import com.example.go4lunch.R;
import com.example.go4lunch.activities.ViewModels.CommunicationViewModel;
import com.example.go4lunch.api.UserHelper;
import com.firebase.ui.auth.AuthUI;

import java.util.Arrays;

import com.example.go4lunch.base.BaseActivity;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;

public class LoginActivity extends BaseActivity {

    // FOR DATA
    private static final int RC_SIGN_IN = 1000;
    private CommunicationViewModel mViewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(CommunicationViewModel.class);
        setContentView(R.layout.activity_login);
        if (!this.isCurrentUserLogged()){
            this.startSignInActivity();
        }else {
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
                    startSignInActivity();
                } else if (response.getError().getErrorCode() == ErrorCodes.NO_NETWORK) {
                    Toast.makeText(this, getString(R.string.error_no_internet), Toast.LENGTH_SHORT).show();
                } else if (response.getError().getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                    Toast.makeText(this, getString(R.string.error_unknown_error), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void startSignInActivity(){
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setTheme(R.style.LoginTheme)
                        .setLogo(R.drawable.meal_logo)
                        .setAvailableProviders(
                                Arrays.asList(
                                        new AuthUI.IdpConfig.EmailBuilder().build(), // EMAIL
                                        new AuthUI.IdpConfig.FacebookBuilder().build(), // FACEBOOK
                                        new AuthUI.IdpConfig.GoogleBuilder().build()   // GOOGLE
                                ))
                        .setIsSmartLockEnabled(false, true)
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
        if (this.getCurrentUser() != null){
            Log.e("LOGIN_ACTIVITY", "createUserInFirestore: LOGGED" );
            String urlPicture = (this.getCurrentUser().getPhotoUrl() != null) ? this.getCurrentUser().getPhotoUrl().toString() : null;
            String username = this.getCurrentUser().getDisplayName();
            String uid = this.getCurrentUser().getUid();
            UserHelper.createUser(uid, username, urlPicture).addOnFailureListener(this.onFailureListener());
        }else{
            Log.e("LOGIN_ACTIVITY", "createUserInFirestore: NOT LOGGED" );
        }
    }
}