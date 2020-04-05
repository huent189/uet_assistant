package vnu.uet.mobilecourse.assistant.ui.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ChatViewModel extends ViewModel {
    private MutableLiveData<String> mText;

    public ChatViewModel() {
        mText = new MutableLiveData<>();
//        mText.setValue("This is home fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}
