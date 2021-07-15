package com.example.go4lunch.viewModels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MatesViewModel extends ViewModel {
    public final MutableLiveData<String> currentUserUID = new MutableLiveData<>();

    public void updateCurrentUserUID(String uid){
        currentUserUID.setValue(uid);
    }

    public MutableLiveData<String> getCurrentUserUID() {
        return currentUserUID;
    }
}
