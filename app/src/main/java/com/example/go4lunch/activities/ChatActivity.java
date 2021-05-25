package com.example.go4lunch.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.go4lunch.R;
import com.example.go4lunch.Views.ChatAdapter;
import com.example.go4lunch.api.MessageHelper;
import com.example.go4lunch.api.UserHelper;
import com.example.go4lunch.base.BaseActivity;
import com.example.go4lunch.models.Message;
import com.example.go4lunch.models.User;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class ChatActivity extends BaseActivity implements  ChatAdapter.Listener{

    RecyclerView recyclerView;
    TextInputEditText editTextMessage;
    ImageView imageViewPreview;
    Toolbar mToolbar;
    ImageButton activity_chat_send_button;

    // FOR DATA

    private ChatAdapter mChatAdapter;
    @Nullable
    private User modelCurrentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_chat);
        ButterKnife.bind(this);
        initView();
        this.configureRecyclerView();
        this.configureToolbar();
        this.getCurrentUserFromFirestore();
    }

    private void initView() {
        recyclerView = findViewById(R.id.activity_chat_recycler_view);
        editTextMessage = findViewById(R.id.activity_chat_message_edit_text);
        imageViewPreview = findViewById(R.id.activity_chat_image_chosen_preview);
        mToolbar = findViewById(R.id.simple_toolbar);
        activity_chat_send_button = findViewById(R.id.activity_chat_send_button);
    }

    public boolean onOptionsItemSelected(MenuItem item){
        Intent myIntent = new Intent(getApplicationContext(), ChatActivity.class);
        startActivityForResult(myIntent, 0);
        int id = item.getItemId();

        if (id==android.R.id.home) {
            finish();
        }
        return true;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Calling the appropriate method after activity result
    }

    // --------------------
    // ACTIONS
    // --------------------


    @OnClick(R.id.activity_chat_send_button)
    public void onClickSendMessage() {
        if (!TextUtils.isEmpty(editTextMessage.getText()) && modelCurrentUser != null) {
            // Check if the ImageView is set
            if (this.imageViewPreview.getDrawable() == null) {
                // SEND A TEXT MESSAGE
                MessageHelper.createMessageForChat(editTextMessage.getText().toString(), modelCurrentUser).addOnFailureListener(this.onFailureListener());
                this.editTextMessage.setText("");
            } else {
                // SEND A IMAGE + TEXT IMAGE
                this.editTextMessage.setText("");
                this.imageViewPreview.setImageDrawable(null);
            }
        }
    }

    // ---------------------
    // CONFIGURATION
    // ---------------------

    private void configureToolbar(){
        setSupportActionBar(mToolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    // --------------------
    // REST REQUESTS
    // --------------------

    private void getCurrentUserFromFirestore(){
        UserHelper.getUser(getCurrentUser().getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                modelCurrentUser = documentSnapshot.toObject(User.class);
            }
        });
    }

    // --------------------
    // UI
    // --------------------

    private void configureRecyclerView(){
        this.mChatAdapter = new ChatAdapter(generateOptionsForAdapter(MessageHelper.getAllMessageForChat()), Glide.with(this), this, this.getCurrentUser().getUid());
        mChatAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                recyclerView.smoothScrollToPosition(mChatAdapter.getItemCount()); // Scroll to bottom on new messages
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(this.mChatAdapter);
    }


    private FirestoreRecyclerOptions<Message> generateOptionsForAdapter(Query query){
        return new FirestoreRecyclerOptions.Builder<Message>()
                .setQuery(query, Message.class)
                .setLifecycleOwner(this)
                .build();
    }

    // --------------------
    // FILE MANAGEMENT
    // --------------------


    // --------------------
    // CALLBACK
    // --------------------

    @Override
    public void onDataChanged() {
    }
}
