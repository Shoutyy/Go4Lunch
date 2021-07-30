package com.example.go4lunch.controllers.fragments.list;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ListViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public ListViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is list fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}