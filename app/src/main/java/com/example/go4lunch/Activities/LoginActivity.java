package com.example.go4lunch.Activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.example.go4lunch.R;
import com.firebase.ui.auth.AuthUI;

import java.util.Arrays;

import com.example.go4lunch.base.BaseActivity;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.material.snackbar.Snackbar;

public class LoginActivity extends BaseActivity {

    // FOR DATA
    private static final int RC_SIGN_IN = 1000;
    private CoordinatorLayout coordinatorLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        coordinatorLayout = findViewById(R.id.coordinatorLayout);
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
                launchMainActivity();
            } else { // ERRORS
                if (response == null) {
                    Snackbar.make(coordinatorLayout,R.string.error_authentication_canceled,Snackbar.LENGTH_LONG).show();
                    startSignInActivity();
                } else if (response.getError().getErrorCode() == ErrorCodes.NO_NETWORK) {
                    Snackbar.make(coordinatorLayout,R.string.error_no_internet,Snackbar.LENGTH_LONG).show();
                } else if (response.getError().getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                    Snackbar.make(coordinatorLayout,R.string.error_unknown_error,Snackbar.LENGTH_LONG).show();
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
}