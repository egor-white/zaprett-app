package io.egorwhite.zaprett.ui.strategy;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class StrategyViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public StrategyViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is notifications fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}