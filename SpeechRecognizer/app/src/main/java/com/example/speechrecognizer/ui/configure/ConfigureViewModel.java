package com.example.speechrecognizer.ui.configure;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ConfigureViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public ConfigureViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is configure fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}