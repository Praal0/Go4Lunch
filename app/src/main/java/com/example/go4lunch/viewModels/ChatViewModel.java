package com.example.go4lunch.viewModels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.go4lunch.models.Message;
import com.example.go4lunch.models.User;
import com.example.go4lunch.repository.ChatRepository;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.Query;

import java.util.Calendar;
import java.util.List;

public class ChatViewModel extends ViewModel {

    private static volatile ChatViewModel instance;
    private ChatRepository chatRepository;

    private ChatViewModel() {
        chatRepository = ChatRepository.getInstance();
    }

    public static ChatViewModel getInstance() {
        ChatViewModel result = instance;
        if (result != null) {
            return result;
        }
        synchronized(ChatViewModel.class) {
            if (instance == null) {
                instance = new ChatViewModel();
            }
            return instance;
        }
    }

    public MutableLiveData<List<Message>> getAllMessageForChat(String userSender, String userReceiver){
        return chatRepository.messageLiveData;
    }


    public static Task<DocumentReference> createMessageForChat(String textMessage, User userSender,User userReceiver){
        // Create the Message object
        Message message = new Message(textMessage,userSender.getUid(),userSender.getUrlPicture(),userReceiver.getUid(), Calendar.getInstance().getTime());

        // Store Message to Firestore
        return ChatRepository.getChatCollection()
                .add(message);
    }

    public static Task<DocumentReference> createMessageWithImageForChat(String urlImage, String textMessage, User userSender,User userReceiver){

        // Creating Message with the URL image
        Message message = new Message(textMessage, urlImage, userSender.getUid(),userSender.getUrlPicture(),userReceiver.getUid(), Calendar.getInstance().getTime());

        // Storing Message on Firestore
        return ChatRepository.getChatCollection()
                .add(message);
    }

}
