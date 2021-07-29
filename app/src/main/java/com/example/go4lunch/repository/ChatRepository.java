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
import java.util.Arrays;
import java.util.List;

import static android.content.ContentValues.TAG;

public class ChatRepository {
    private static final String CHAT_COLLECTION = "chats";

    public ChatRepository() { }

    public static CollectionReference getChatCollection(){
        return FirebaseFirestore.getInstance().collection(CHAT_COLLECTION);
    }

    public Query getAllMessageForChat(String userSender, String userReceiver){
        String firstCanon = userSender + "/" + userReceiver;
        String secondCanon = userReceiver + "/" + userSender;

        return getChatCollection()
                .whereIn("idUserSenderIdReceiver", Arrays.asList(firstCanon,secondCanon))
                .orderBy("dateCreated")
                .limit(50);
    }


}
