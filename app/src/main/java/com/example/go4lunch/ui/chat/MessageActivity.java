package com.example.go4lunch.ui.chat;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.go4lunch.R;
import com.example.go4lunch.api.UserHelper;
import com.example.go4lunch.base.BaseActivity;
import com.example.go4lunch.injection.ChatViewModelFactory;
import com.example.go4lunch.injection.Injection;
import com.example.go4lunch.injection.MapViewModelFactory;
import com.example.go4lunch.models.Message;
import com.example.go4lunch.models.User;
import com.example.go4lunch.viewModels.ChatViewModel;
import com.example.go4lunch.viewModels.MapViewModel;
import com.example.go4lunch.viewModels.MatesViewModel;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.List;
import java.util.UUID;

import pub.devrel.easypermissions.EasyPermissions;

import static com.example.go4lunch.ui.user.UsersFragment.USER_DATA;

public class MessageActivity extends BaseActivity implements  MessageAdapter.Listener {
    private Toolbar mToolbar;
    private ImageView profileImageView;
    private TextView profileTextView;
    private RecyclerView recyclerView;
    private ImageView imageViewPreview;
    private Uri uriImageSelected;
    private TextInputEditText editTextMessage;
    private ImageButton activity_chat_send_button, activity_chat_file_button;
    private ChatViewModel chatViewModel;


    // FOR DATA
    private static final String PERMS = Manifest.permission.READ_EXTERNAL_STORAGE;
    private static final int RC_IMAGE_PERMS = 100;
    private static final int RC_CHOOSE_PHOTO = 200;
    private User modelCurrentUser;
    private User modelUserReceiver;
    private MessageAdapter mMessageAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        initView();
        ChatViewModelFactory chatViewModelFactory = Injection.provideChatViewModelFactory(this.getApplication());
        chatViewModel = new ViewModelProvider(this, chatViewModelFactory).get(ChatViewModel.class);
        configureToolbar();
        updateUIWhenCreating();
        getCurrentUserFromFirestore();
        clickSendMessage();
        clickAddFile();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        this.handleResponse(requestCode, resultCode, data);
    }

    private void initView() {
        imageViewPreview = findViewById(R.id.activity_chat_image_chosen_preview);
        recyclerView = findViewById(R.id.activity_chat_recycler_view);
        editTextMessage = findViewById(R.id.activity_chat_message_edit_text);
        activity_chat_send_button = findViewById(R.id.activity_chat_send_button);
        activity_chat_file_button = findViewById(R.id.activity_chat_add_file_button);
        profileImageView = findViewById(R.id.profil_image);
        profileTextView = findViewById(R.id.profil_textView);
        mToolbar = findViewById(R.id.toolbar);
        Intent intent = getIntent();
        modelUserReceiver = intent.getParcelableExtra(USER_DATA);
    }

    private void configureToolbar(){
        setSupportActionBar(mToolbar);
        ActionBar ab = getSupportActionBar();
        ab.setTitle("");
        ab.setHomeButtonEnabled(true);
        ab.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                finish();
        }
        return (super.onOptionsItemSelected(menuItem));
    }

    private void getCurrentUserFromFirestore(){
        UserHelper.getUser(getCurrentUser().getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                modelCurrentUser = documentSnapshot.toObject(User.class);
                configureRecyclerView();
            }
        });
    }

    // --------------------
    // ACTIONS
    // --------------------

    private void clickSendMessage() {
        activity_chat_send_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(editTextMessage.getText()) && modelCurrentUser != null) {
                    // Check if the ImageView is set
                    if (imageViewPreview.getDrawable() == null) {
                        // SEND A TEXT MESSAGE
                        ChatViewModel.createMessageForChat(editTextMessage.getText().toString(), modelCurrentUser, modelUserReceiver).addOnFailureListener(onFailureListener());
                        editTextMessage.setText("");
                    } else {
                        // SEND A IMAGE + TEXT IMAGE
                        uploadPhotoInFirebaseAndSendMessage(editTextMessage.getText().toString());
                        editTextMessage.setText("");
                        imageViewPreview.setImageDrawable(null);
                    }
                }
            }
        });
    }

    private void clickAddFile() {
        activity_chat_file_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImageFromPhone();
            }
        });
    }

    private void uploadPhotoInFirebaseAndSendMessage(String message) {
        String uuid = UUID.randomUUID().toString(); // GENERATE UNIQUE STRING
        //  UPLOAD TO GCS
        final StorageReference mImageRef = FirebaseStorage.getInstance().getReference(uuid);
        UploadTask uploadTask = mImageRef.putFile(this.uriImageSelected);

        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    Log.e("CHAT_ACTIVITY_TAG", "Error TASK_URI : " + task.getException() );
                    throw task.getException();
                }
                // Continue with the task to get the download URL
                return mImageRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    ChatViewModel.createMessageWithImageForChat(downloadUri.toString(), message, modelCurrentUser,modelUserReceiver).addOnFailureListener(onFailureListener());
                } else {
                    Log.e("CHAT_ACTIVITY_TAG", "Error ON_COMPLETE : " + task.getException() );
                }
            }
        });
    }

    // --------------------
    // UI
    // --------------------

    // Update UI when activity is creating
    private void updateUIWhenCreating(){
        profileTextView.setText(modelUserReceiver.getUsername().toString());
        Glide.with(MessageActivity.this).load(modelUserReceiver.getUrlPicture())
                .apply(RequestOptions.circleCropTransform()).into(profileImageView);
    }

    private void configureRecyclerView(){
        mMessageAdapter = new MessageAdapter(generateOptionsForAdapter(chatViewModel.getAllMessageForChat(modelCurrentUser.getUid(),modelUserReceiver.getUid())), Glide.with(this), this, modelCurrentUser.getUid());
        mMessageAdapter.startListening();
        mMessageAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                recyclerView.smoothScrollToPosition(mMessageAdapter.getItemCount()); // Scroll to bottom on new messages
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(this.mMessageAdapter);
    }

    private FirestoreRecyclerOptions<Message> generateOptionsForAdapter(Query query){
        return new FirestoreRecyclerOptions.Builder<Message>()
                .setQuery(query, Message.class)
                .setLifecycleOwner(this)
                .build();
    }


    @Override
    protected void onStop() {
        super.onStop();
        mMessageAdapter.stopListening();
    }

    // --------------------
    // FILE MANAGEMENT
    // --------------------

    private void chooseImageFromPhone(){
        if (!EasyPermissions.hasPermissions(this, PERMS)) {
            EasyPermissions.requestPermissions(this, getString(R.string.popup_title_permission_files_access), RC_IMAGE_PERMS, PERMS);
            return;
        }
        // Launch an "Selection Image" Activity
        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, RC_CHOOSE_PHOTO);
    }

    // Handle activity response (after user has chosen or not a picture)
    private void handleResponse(int requestCode, int resultCode, Intent data){
        if (requestCode == RC_CHOOSE_PHOTO) {
            if (resultCode == RESULT_OK) { //SUCCESS
                this.uriImageSelected = data.getData();
                Glide.with(this) //SHOWING PREVIEW OF IMAGE
                        .load(this.uriImageSelected)
                        .apply(RequestOptions.circleCropTransform())
                        .into(this.imageViewPreview);
            } else {
                Toast.makeText(this, getString(R.string.chat_no_image_chosen), Toast.LENGTH_SHORT).show();
            }
        }
    }


    // --------------------
    // CALLBACK
    // --------------------

    @Override
    public void onDataChanged() {
    }
}