package com.example.go4lunch.ui.setting;

import android.app.AlarmManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;

import com.example.go4lunch.R;
import com.example.go4lunch.notification.NotificationHelper;
import com.example.go4lunch.api.UserHelper;
import com.example.go4lunch.base.BaseActivity;
import com.example.go4lunch.ui.MainActivity;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class SettingActivity extends BaseActivity {

   public static final long INTERVAL = AlarmManager.INTERVAL_FIFTEEN_MINUTES;
    private Toolbar mToolbar;
    private Switch mSwitch;
    private Button btnSave, btnLanguage;
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
        clickBtnLanguage();
    }
    private void initialize() {
        btnSave = findViewById(R.id.settings_save);
        btnLanguage = findViewById(R.id.langue_btn);
        mSwitch = findViewById(R.id.settings_switch);
        mToolbar = findViewById(R.id.toolbar_setting);
    }

    private void configureToolbar(){
        setSupportActionBar(mToolbar);
        ActionBar ab = getSupportActionBar();
        ab.setTitle(R.string.setting);
        ab.setHomeButtonEnabled(true);
        ab.setDisplayHomeAsUpEnabled(true);
    }


    private void createNotificationHelper(){
        mNotificationHelper = new NotificationHelper(getBaseContext());
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

    // When we use back button of phone
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent homeIntent = new Intent(this, MainActivity.class);
        startActivity(homeIntent);
        finish();
    }

    private void retrieveUserSettings(){
        UserHelper.getUsersCollection().document(getCurrentUser().getUid()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
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

                    }else{
                        mSwitch.setChecked(false);

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
                            if (mSwitch.isChecked()){
                                mNotificationHelper.sendNotification();
                            }else{
                                mNotificationHelper.cancelAlarmRTC();
                            }
                            Snackbar.make(v, R.string.setting_update, Snackbar.LENGTH_SHORT).show();
                        });
            }
        });
    }

    private void clickBtnLanguage() {
        btnLanguage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(SettingActivity.this,R.style.AlertDialog);

                // Set Title.
                builder.setTitle(R.string.selected_language);


                // Add a list
                final CharSequence[] items = {getString(R.string.french), getString(R.string.english)};
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case 0 :
                                setLocale("fr");
                                recreate();
                                break;

                            case 1 :
                                setLocale("en");
                                recreate();
                                break;

                            default:
                                break;
                        }
                    }

                });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });

    }





}