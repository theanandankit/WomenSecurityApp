package com.project.womensecurityapp.ui.trusted;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class TrustedViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public TrustedViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is trusted fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}