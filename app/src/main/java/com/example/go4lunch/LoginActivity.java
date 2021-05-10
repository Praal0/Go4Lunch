package com.example.go4lunch;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import com.firebase.ui.auth.AuthUI;

import java.util.Arrays;

import base.BaseActivity;

public class LoginActivity extends BaseActivity {

    // FOR DATA
    private static final int RC_SIGN_IN = 1000;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
    }

    @SuppressLint("ResourceType")
    private void startSignInActivity(){
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setTheme(R.style.LoginTheme)
                        .setLogo(R.drawable.meal_logo)
                        .setAvailableProviders(
                                Arrays.asList(new AuthUI.IdpConfig.EmailBuilder().build(), // EMAIL
                                        new AuthUI.IdpConfig.GoogleBuilder().build(),      // GOOGLE
                                        new AuthUI.IdpConfig.FacebookBuilder().build()))         // FACEBOOK
                        .setIsSmartLockEnabled(false, true)
                        .build(),
                RC_SIGN_IN);
    }
    // --------------------
    // ACTION
    // --------------------

    private void launchMainActivity() {
        Intent intent = new Intent(this, MapActivity.class);
        startActivity(intent);
    }
}