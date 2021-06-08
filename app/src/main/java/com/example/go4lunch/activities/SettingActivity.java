package com.example.go4lunch.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import com.example.go4lunch.R;
import com.example.go4lunch.activities.ViewModels.CommunicationViewModel;
import com.example.go4lunch.api.UserHelper;
import com.example.go4lunch.base.BaseActivity;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class SettingActivity extends BaseActivity {

    private Toolbar mToolbar;
    private Switch mSwitch;
    private Button btnSave;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        initialize();


        configureToolbar();
        retrieveUserSettings();
        clickBtnSave();
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



    private void configureToolbar(){
        setSupportActionBar(mToolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
    }



    private void initialize() {
        btnSave = findViewById(R.id.settings_save);
        mSwitch = findViewById(R.id.settings_switch);
        mToolbar = findViewById(R.id.simple_toolbar);
        mToolbar.setTitle(R.string.setting);

    }

    private void retrieveUserSettings(){
        UserHelper.getUsersCollection().document(getCurrentUser().getUid()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.e("TAG", "Listen failed.", e);
                    return;
                }
                if (documentSnapshot.getData().get("notification").equals(true)){
                    mSwitch.setChecked(true);
                }else{
                    mSwitch.setChecked(false);
                }

            }
        });
    }

    private void clickBtnSave() {
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserHelper.updateUserSettings(getCurrentUser().getUid(),mSwitch.isChecked()).addOnSuccessListener(
                        updateTask ->{
                            Log.e("SETTINGS_ACTIVITY", "saveSettings: DONE" );
                            Snackbar.make(v, R.string.setting_update, Snackbar.LENGTH_SHORT).show();
                        });
            }
        });
    }


}