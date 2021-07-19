package com.example.go4lunch.repository;

import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.go4lunch.models.Message;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

public class ChatRepository {
    private static final String CHAT_COLLECTION = "chats";
    private static volatile ChatRepository instance;
    public MutableLiveData<List<Message>> messageLiveData = new MutableLiveData<>();

    private ChatRepository() { }

    public static ChatRepository getInstance() {
        ChatRepository result = instance;
        if (result != null) {
            return result;
        }
        synchronized(ChatRepository.class) {
            if (instance == null) {
                instance = new ChatRepository();
            }
            return instance;
        }
    }

    public static CollectionReference getChatCollection(){
        return FirebaseFirestore.getInstance().collection(CHAT_COLLECTION);
    }

    public void getAllMessageForChat(String userSender, String userReceiver){
        getChatCollection().whereEqualTo("idUserSender",userSender).whereEqualTo("idUserReceiver",userReceiver)
                .orderBy("dateCreated")
                .limit(50)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable  FirebaseFirestoreException error) {
                        if (error != null) {
                            Log.w(TAG, "Listen failed.", error);
                            return;
                        }
                        List<Message> messageLists = new ArrayList<Message>();
                        for (QueryDocumentSnapshot doc : value) {
                            if (doc.get("message") != null) {
                                messageLists.add(doc.toObject(Message.class));
                            }
                        }
                        messageLiveData.setValue(messageLists);
                    }
                });
    }


}
