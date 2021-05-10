package com.example.go4lunch;

import androidx.appcompat.app.AppCompatActivity;

import android.media.Image;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import base.BaseActivity;

public class MapActivity extends BaseActivity {

    // For design
    Button btnUpdate;
    TextView textInputEditTextUsername;
    TextView textViewEmail;
    ImageView imageViewProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        clickBtnSignOut();
        clickBtnUpdate();
        init();
    }

    // --------------------
    // ACTIONS
    // --------------------

    private void init() {
        imageViewProfile = findViewById(R.id.profile_activity_imageview_profile);
        textInputEditTextUsername = findViewById(R.id.profile_activity_edit_text_username);
        textViewEmail = findViewById(R.id.profile_activity_text_view_email);
    }

    private void clickBtnSignOut() {

    }

    private void clickBtnUpdate() {

    }

    // --------------------
    // UI
    // --------------------

    // 1 - Update UI when activity is creating
    private void updateUIWhenCreating(){

        if (this.getCurrentUser() != null){

            //Get picture URL from Firebase
            if (this.getCurrentUser().getPhotoUrl() != null) {
                Glide.with(this)
                        .load(this.getCurrentUser().getPhotoUrl())
                        .apply(RequestOptions.circleCropTransform())
                        .into(imageViewProfile);
            }

            //Get email & username from Firebase
            String email = TextUtils.isEmpty(this.getCurrentUser().getEmail()) ? getString(R.string.info_no_email_found) : this.getCurrentUser().getEmail();
            String username = TextUtils.isEmpty(this.getCurrentUser().getDisplayName()) ? getString(R.string.info_no_username_found) : this.getCurrentUser().getDisplayName();

            //Update views with data
            this.textInputEditTextUsername.setText(username);
            this.textViewEmail.setText(email);
        }
    }

}