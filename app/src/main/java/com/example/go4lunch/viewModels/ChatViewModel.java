package com.example.go4lunch.viewModels;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;

import com.example.go4lunch.models.Message;
import com.example.go4lunch.models.User;
import com.example.go4lunch.repository.ChatRepository;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.Query;

import java.util.Calendar;

public class ChatViewModel extends ViewModel {

    private ChatRepository chatRepository;

    public ChatViewModel(
            @NonNull ChatRepository chatRepository
    ) {
        this.chatRepository = chatRepository;
    }


    public Query getAllMessageForChat(String userSender, String userReceiver){
        return chatRepository.getAllMessageForChat(userSender,userReceiver);
    }


    public static Task<DocumentReference> createMessageForChat(String textMessage, User userSender,User userReceiver){
        // Create the Message object
        Message message = new Message(textMessage,userSender.getUid(),userSender.getUid()+"/"+userReceiver.getUid(),userSender.getUrlPicture(), Calendar.getInstance().getTime());

        // Store Message to Firestore
        return ChatRepository.getChatCollection()
                .add(message);
    }

    public static Task<DocumentReference> createMessageWithImageForChat(String urlImage, String textMessage, User userSender,User userReceiver){

        // Creating Message with the URL image
        Message message = new Message(textMessage, urlImage, userSender.getUid()+"/"+userReceiver.getUid(),userSender.getUrlPicture(), Calendar.getInstance().getTime());

        // Storing Message on Firestore
        return ChatRepository.getChatCollection()
                .add(message);
    }

}
