package com.example.go4lunch;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Collections;

public class LoginActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 123;
    private Button btnGoogle;
    private Button btnFacecebook;
    private Button btnEmail;
    private CoordinatorLayout coordinatorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        innit();
        googleBtnClick();
        facebookBtnClick();
        emailBtnClick();
    }

    @Nullable
    protected FirebaseUser getCurrentUser(){ return FirebaseAuth.getInstance().getCurrentUser(); }

    protected Boolean isCurrentUserLogged(){ return (this.getCurrentUser() != null); }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        this.handleResponseAfterSignIn(requestCode, resultCode, data);
    }

    private void innit() {
        btnGoogle = findViewById(R.id.google_btn);
        btnFacecebook = findViewById(R.id.facebook_btn);
        coordinatorLayout = findViewById(R.id.login_activity_constraint_layout);
        btnEmail = findViewById(R.id.email_btn);
    }

    private void emailBtnClick() {
        btnEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {startSignInEmail();}
        });
    }

    private void googleBtnClick() {
        btnGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { startSignInGoogle(); }
        });
    }

    private void facebookBtnClick() {
        btnFacecebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { startSignInActivitywithFacebook(); }
        });
    }

    private void startSignInEmail() {
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(Collections.singletonList(new
                                AuthUI.IdpConfig.EmailBuilder().build()))
                        .setIsSmartLockEnabled(false, true)
                        .build(),
                RC_SIGN_IN);
    }

    private void startSignInGoogle() {
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(Collections.singletonList(new
                                AuthUI.IdpConfig.GoogleBuilder().build()))
                        .setIsSmartLockEnabled(false, true)
                        .build(),
                RC_SIGN_IN);
    }

    /**
     * Create user in Firebase for Facebook
     */
    private void startSignInActivitywithFacebook() {
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(Collections.singletonList(new
                                AuthUI.IdpConfig.FacebookBuilder().build()))
                        .setIsSmartLockEnabled(false, true)
                        .build(),
                RC_SIGN_IN);
    }

    private void showSnackBar(CoordinatorLayout coordinatorLayout, String message){
        Snackbar.make(coordinatorLayout, message, Snackbar.LENGTH_SHORT).show();
    }

    private void handleResponseAfterSignIn(int requestCode, int resultCode, Intent data){

        IdpResponse response = IdpResponse.fromResultIntent(data);

        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) { // SUCCESS
                showSnackBar(this.coordinatorLayout, getString(R.string.connection_succeed));
            } else { // ERRORS
                if (response == null) {
                    showSnackBar(this.coordinatorLayout, getString(R.string.error_authentication_canceled));
                } else if (response.getError().getErrorCode() == ErrorCodes.NO_NETWORK) {
                    showSnackBar(this.coordinatorLayout, getString(R.string.error_no_internet));
                } else if (response.getError().getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                    showSnackBar(this.coordinatorLayout, getString(R.string.error_unknown_error));
                }
            }
        }
    }




}