package com.example.go4lunch.injection;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.go4lunch.repository.ChatRepository;
import com.example.go4lunch.repository.LocationRepository;
import com.example.go4lunch.viewModels.ChatViewModel;
import com.example.go4lunch.viewModels.MapViewModel;

public class ChatViewModelFactory implements ViewModelProvider.Factory {
    private final ChatRepository chatRepository;

    public ChatViewModelFactory(ChatRepository chatRepository) {
        this.chatRepository = chatRepository;
    }


    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(ChatViewModel.class)){
            return (T) new ChatViewModel(chatRepository);
        }
        throw new IllegalArgumentException("Unknown ViewModel Class");
    }
}
