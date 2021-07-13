package com.example.go4lunch.ui.workmates;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class WorkmatesViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public WorkmatesViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is workmates fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}