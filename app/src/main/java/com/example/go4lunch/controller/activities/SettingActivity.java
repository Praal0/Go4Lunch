package com.example.go4lunch.controller.activities;

import android.app.AlarmManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;

import com.example.go4lunch.R;
import com.example.go4lunch.api.NotificationHelper;
import com.example.go4lunch.api.UserHelper;
import com.example.go4lunch.base.BaseActivity;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class SettingActivity extends BaseActivity {

   public static final long INTERVAL = AlarmManager.INTERVAL_FIFTEEN_MINUTES;
    private Toolbar mToolbar;
    private Switch mSwitch;
    private Button btnSave;
    private NotificationHelper mNotificationHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        initialize();
        configureToolbar();
        retrieveUserSettings();
        createNotificationHelper();
        clickBtnSave();
    }

    private void initialize() {
        btnSave = findViewById(R.id.settings_save);
        mSwitch = findViewById(R.id.settings_switch);
        mToolbar = findViewById(R.id.simple_toolbar);
        mToolbar.setTitle(R.string.setting);
    }

    private void configureToolbar(){
        setSupportActionBar(mToolbar);
        ActionBar ab = getSupportActionBar();
        assert ab != null;
        ab.setDisplayHomeAsUpEnabled(true);
    }

    private void createNotificationHelper(){
        mNotificationHelper = new NotificationHelper(getBaseContext());
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                finish();
        }
        return (super.onOptionsItemSelected(menuItem));
    }

    private void retrieveUserSettings(){
        UserHelper.getUsersCollection().document(getCurrentUser().getUid()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.e("TAG", "Listen failed.", e);
                    return;
                }

                if (documentSnapshot != null && documentSnapshot.exists()) {
                    Log.e("TAG", "Current data: " + documentSnapshot.getData());
                    if (documentSnapshot.getData().get("notification").equals(true)){
                        mSwitch.setChecked(true);
                        mNotificationHelper.scheduleRepeatingNotification();

                    }else{
                        mSwitch.setChecked(false);
                        mNotificationHelper.cancelAlarmRTC();
                    }
                } else {
                    Log.e("TAG", "Current data: null");
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