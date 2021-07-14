package com.example.go4lunch.api;

import com.example.go4lunch.models.Message;
import com.example.go4lunch.models.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.Query;

import org.joda.time.DateTime;

import java.util.Calendar;
import java.util.Date;

public class MessageHelper {



    public static Task<DocumentReference> createMessageForChat(String textMessage, User userSender,User userReceiver){
        // Create the Message object
        Message message = new Message(textMessage,userSender.getUid(),userSender.getUrlPicture(),userReceiver.getUid(), Calendar.getInstance().getTime());

        // Store Message to Firestore
        return ChatHelper.getChatCollection()
                .add(message);
    }

    public static Task<DocumentReference> createMessageWithImageForChat(String urlImage, String textMessage, User userSender,User userReceiver){

        // Creating Message with the URL image
        Message message = new Message(textMessage, urlImage, userSender.getUid(),userSender.getUrlPicture(),userReceiver.getUid(), Calendar.getInstance().getTime());

        // Storing Message on Firestore
        return ChatHelper.getChatCollection()
                .add(message);
    }

    // --- GET ---

    public static Query getAllMessageForChat(String userSender, String userReceiver){
        return ChatHelper.getChatCollection()
                .orderBy("dateCreated")
                .limit(50);
    }

}
